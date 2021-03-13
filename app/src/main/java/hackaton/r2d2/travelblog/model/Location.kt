package hackaton.r2d2.travelblog.model

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class Location(
    val latLng: LatLng,
    val timestamp: Date
)