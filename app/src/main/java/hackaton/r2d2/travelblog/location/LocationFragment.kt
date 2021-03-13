package hackaton.r2d2.travelblog.location

import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import hackaton.r2d2.travelblog.R
import hackaton.r2d2.travelblog.databinding.FragmentLocationBinding


class LocationFragment : Fragment() {

    //tag для логов
    private val TAG = LocationFragment::class.java.simpleName

    //определение координат пользователя
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //Начальные параметры
        val homeLatLng = LatLng(59.94019072565021, 30.31458675591602)
        val zoomLevel = 15f

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        //googleMap.addMarker(MarkerOptions().position(homeLatLng))

        //применить стиль из папки Raw
        setMapStyle(googleMap)


        val myLocationButton: FloatingActionButton =
            requireActivity().findViewById(R.id.fab_location_consumer)

        myLocationButton.setOnClickListener {
            getMyLocation(googleMap)
        }

    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(googleMap: GoogleMap) {

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

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
        location?.let {
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)

        binding.youtubePlayer.getPlayerUiController().showFullscreenButton(true)

        binding.youtubePlayer.addYouTubePlayerListener(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {

                    /**
                    Set videoId
                     */
                    val videoId = "sZ4crcx7FLU"
                    //первый параметр ID видео
                    //второй параметр с какой секунды запустить
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            })
        binding.youtubePlayer.getPlayerUiController().setFullScreenButtonClickListener {
            if (binding.youtubePlayer.isFullScreen()) {
                binding.youtubePlayer.exitFullScreen()
            } else
                binding.youtubePlayer.enterFullScreen()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_consumer) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)
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