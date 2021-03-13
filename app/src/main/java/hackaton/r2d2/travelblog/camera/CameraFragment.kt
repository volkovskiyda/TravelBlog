package hackaton.r2d2.travelblog.camera

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color.GRAY
import android.graphics.Color.RED
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hackaton.r2d2.travelblog.R
import hackaton.r2d2.travelblog.databinding.FragmentCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.fixedRateTimer

class CameraFragment : Fragment() {

    //tag для логов
    private val TAG = CameraFragment::class.java.simpleName

    private val viewModel: CameraViewModel by viewModels()


    //определение координат пользователя
    private var fusedLocationClient: FusedLocationProviderClient? = null

    //видеозапись
    private lateinit var videoCapture: VideoCapture


    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var binding: FragmentCameraBinding

    private lateinit var googleAccountCredential: GoogleAccountCredential
    private lateinit var youTube: YouTube

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val email = Firebase.auth.currentUser?.email
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf("https://www.googleapis.com/auth/youtube")
        ).setBackOff(ExponentialBackOff()).setSelectedAccountName(email)

        val httpTransport = NetHttpTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        youTube = YouTube.Builder(
            httpTransport,
            jsonFactory,
            googleAccountCredential
        ).setApplicationName("TravelBlog").build()

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)

        startCamera()

        // Create a configuration object for the video use case
        videoCapture = VideoCapture.Builder().apply {
            // init config here
        }.build()


        //обработчик нажатия
        binding.cameraCaptureButton.setOnClickListener {
            if (viewModel.statusRecording.value == false) {
                binding.cameraCaptureButton.setBackgroundColor(RED)
                startRecording()
                viewModel.startRecord()
            } else if (viewModel.statusRecording.value == true) {
                binding.cameraCaptureButton.setBackgroundColor(GRAY)
                stopRecording()
                viewModel.stopRecord()
            }
        }

        return binding.root
    }

    @SuppressLint("RestrictedApi")
    private fun startRecording() {

        // Create time-stamped output file to hold the image
        val fileName =
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".mp4"
        val videoFile = File(outputDirectory, fileName)

        val startTS = Timestamp.now()
        videoCapture.startRecording(
            VideoCapture.OutputFileOptions.Builder(videoFile).build(),
            cameraExecutor,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    Log.d(TAG, "Video saved succeeded: ${Uri.fromFile(videoFile)}")
                    val endTS = Timestamp.now()
                    uploadVideo(videoFile, startTS, endTS)
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.e(TAG, message)
                }
            })

    }

    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        videoCapture.stopRecording()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalCacheDirs.firstOrNull()?.also { it.mkdirs() }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireContext().filesDir
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            videoCapture = VideoCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_blogger) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)
    }

    private fun uploadVideo(file: File, startTS: Timestamp, endTS: Timestamp) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            uploadYoutubeVideo(
                InputStreamContent(
                    "application/octet-stream",
                    BufferedInputStream(FileInputStream(file))
                ).setLength(file.length()), startTS, endTS
            )
        }
    }

    private fun uploadYoutubeVideo(
        mediaContent: InputStreamContent,
        startTS: Timestamp,
        endTS: Timestamp
    ) = try {
            val response: Video = youTube.videos().insert(listOf("id,snippet,statistics"), Video(), mediaContent).execute()
            updateVideoToFirestore(response, startTS, endTS)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload youtube video", e)
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Failed to upload video", Toast.LENGTH_SHORT).show()
            }
        }

    private fun updateVideoToFirestore(video: Video, startTS: Timestamp, endTS: Timestamp) {
        val user = Firebase.auth.currentUser
        val videoId = video.id

        if (user == null) {
            Log.e(TAG, "User is null, we cannot upload info about video: $videoId")
            return
        }

        val videoData = mapOf(
            "id" to videoId,
            "start" to startTS,
            "end" to endTS,
            "title" to video.snippet.title,
            "description" to video.snippet.description,
            "channelId" to video.snippet.channelId,
            "channelTitle" to video.snippet.channelTitle,
            "publishedAt" to video.snippet.publishedAt.value,
            "thumbnail" to video.snippet.thumbnails.high.url,
        )
        Firebase.firestore
            .collection("users").document(user.uid)
            .collection("videos").document(videoId)
            .set(videoData)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //Начальные параметры
        val homeLatLng = LatLng(59.94019072565021, 30.31458675591602)
        val zoomLevel = 15f

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        //установить пин
        //googleMap.addMarker(MarkerOptions().position(homeLatLng))

        //применить стиль из папки Raw
        setMapStyle(googleMap)

        fixedRateTimer(period = 60_000) {
            getMyLocation(googleMap)
        }

        binding.fabLocationBlogger.setOnClickListener {
            getMyLocation(googleMap)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(googleMap: GoogleMap) {
        /**
         * Здесь если успешно location возвращает latitude и longitude
         */
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                updateMapLocation(location, googleMap)
            }
    }

    private fun updateMapLocation(location: Location?, googleMap: GoogleMap) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                )
            )
        )
        googleMap.addMarker(MarkerOptions().position(
            LatLng(
                location?.latitude ?: 0.0,
                location?.longitude ?: 0.0
            )
        ))

/*        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
        location?.let {
        }*/
    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                //Timber.tag(TAG).i("Style apply failed")
            }
        } catch (e: Resources.NotFoundException) {
            //Timber.tag(TAG).e(e, "Can't find style. Error: ")
        }
    }
}