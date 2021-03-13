package hackaton.r2d2.travelblog.model

import java.util.*

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val channelId: String,
    val channelTitle: String,
    val start: Date,
    val end: Date,
    val thumbnail: String,
)
