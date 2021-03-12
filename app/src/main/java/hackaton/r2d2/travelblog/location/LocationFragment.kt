package hackaton.r2d2.travelblog.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hackaton.r2d2.travelblog.R


class LocationFragment : Fragment() {

    //tag для логов
    private val TAG = LocationFragment::class.java.simpleName

    private val REQUEST_LOCATION_PERMISSION = 1

    private var fusedLocationClient: FusedLocationProviderClient? = null


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        //Начальный параметры
        val homeLatLng = LatLng(59.94019072565021, 30.31458675591602)
        val zoomLevel = 15f

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        //googleMap.addMarker(MarkerOptions().position(homeLatLng))


        //применить стиль из папки Raw
        setMapStyle(googleMap)

        //включить слежение
        enableMyLocation(googleMap)

        val myLocationButton: FloatingActionButton =
            requireActivity().findViewById(R.id.fab_location)

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


    //включить слежение за местоположением
    @SuppressLint("MissingPermission")
    private fun enableMyLocation(map: GoogleMap) {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    //проверка доступа к геолокации
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

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