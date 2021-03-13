package hackaton.r2d2.travelblog.model

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val channelId: String,
    val channelTitle: String,
    val start: String,
    val end: String,
    val thumbnail: String,
)
