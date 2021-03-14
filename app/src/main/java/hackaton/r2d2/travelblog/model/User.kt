package hackaton.r2d2.travelblog.model

data class User(
    val uid: String,
    val email: String,
    val name: String,
    val photoUrl: String?,
    val videos: Long,
)
