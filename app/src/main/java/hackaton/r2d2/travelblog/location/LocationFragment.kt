package hackaton.r2d2.travelblog.location

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import hackaton.r2d2.travelblog.R
import hackaton.r2d2.travelblog.currentLocation
import hackaton.r2d2.travelblog.databinding.FragmentLocationBinding
import kotlinx.coroutines.launch

class LocationFragment : Fragment() {
    private val viewModel: LocationViewModel by viewModels()

    //определение координат пользователя
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private lateinit var binding: FragmentLocationBinding

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        viewLifecycleOwner.lifecycleScope.launch {
            //Начальные параметры
            val homeLatLng = fusedLocationClient.currentLocation()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15f))
        }

        //применить стиль из папки Raw
        setMapStyle(googleMap)
        requireActivity().findViewById<FloatingActionButton>(R.id.fab_location_consumer).setOnClickListener { getMyLocation(googleMap) }

        viewModel.locations.observe(viewLifecycleOwner) { locations ->
            locations.windowed(2).forEach { (start, end) ->
                googleMap.addPolyline(PolylineOptions().add(start.latLng, end.latLng).color(Color.BLUE))
            }

            for (latLng in locations.map { it.latLng }) {
                val position = MarkerOptions().position(latLng)
                googleMap.addMarker(position)
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(googleMap: GoogleMap) {
        viewLifecycleOwner.lifecycleScope.launch {
            updateMapLocation(fusedLocationClient.currentLocation(), googleMap)
        }
    }

    private fun updateMapLocation(latLng: LatLng?, googleMap: GoogleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var player: YouTubePlayer? = null
        binding = FragmentLocationBinding.inflate(inflater, container, false)

        binding.youtubePlayer.getPlayerUiController().showFullscreenButton(true)

        binding.youtubePlayer.addYouTubePlayerListener(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    player = youTubePlayer
                    youTubePlayer.cueVideo(viewModel.selectedVideo.value!!.id, 0f)
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

        viewModel.seekPosition.observe(viewLifecycleOwner) { position -> player?.seekTo(position) }

        return binding.root
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
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (!success) {
                //Timber.tag(TAG).i("Style apply failed")
            }
        } catch (e: Resources.NotFoundException) {
            //Timber.tag(TAG).e(e, "Can't find style. Error: ")
        }
    }

}