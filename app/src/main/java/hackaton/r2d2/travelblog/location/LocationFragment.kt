package hackaton.r2d2.travelblog.location

import android.annotation.SuppressLint
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import hackaton.r2d2.travelblog.currentLocation
import hackaton.r2d2.travelblog.databinding.FragmentLocationBinding
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class LocationFragment : Fragment() {

    //tag для логов
    private val TAG = LocationFragment::class.java.simpleName

    //определение координат пользователя
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        viewLifecycleOwner.lifecycleScope.launch {
            //Начальные параметры
            val homeLatLng = fusedLocationClient.currentLocation()
            val zoomLevel = 15f

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        }

        //применить стиль из папки Raw
        setMapStyle(googleMap)


        val myLocationButton: FloatingActionButton =
            requireActivity().findViewById(R.id.fab_location_consumer)

        myLocationButton.setOnClickListener { getMyLocation(googleMap) }

    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(googleMap: GoogleMap) {
        viewLifecycleOwner.lifecycleScope.launch {
            updateMapLocation(fusedLocationClient.currentLocation(), googleMap)
        }
    }

    private fun updateMapLocation(latLng: LatLng?, googleMap: GoogleMap) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLng(latLng)
        )

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
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
                binding.fabLocationConsumer.visibility = View.VISIBLE
            } else {
                binding.youtubePlayer.enterFullScreen()
                binding.fabLocationConsumer.visibility = View.GONE
            }
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