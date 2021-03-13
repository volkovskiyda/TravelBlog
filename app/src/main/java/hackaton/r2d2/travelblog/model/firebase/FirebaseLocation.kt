package hackaton.r2d2.travelblog.model.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class FirebaseLocation(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Timestamp,
) {
    @Suppress("unused")
    constructor() : this(0.0, 0.0, Timestamp.now())
}