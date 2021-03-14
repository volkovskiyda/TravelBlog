package hackaton.r2d2.travelblog.model.firebase

import androidx.annotation.Keep

@Keep
data class FirebaseUser(
    val uid: String,
    val email: String,
    val name: String,
    val photoUrl: String?,
    val videos: Long,
    val locations: Long,
) {
    @Suppress("unused")
    constructor() : this("", "", "", "", 0, 0)
}
