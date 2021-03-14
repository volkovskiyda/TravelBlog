package hackaton.r2d2.travelblog

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hackaton.r2d2.travelblog.model.Location
import hackaton.r2d2.travelblog.model.User
import hackaton.r2d2.travelblog.model.Video
import hackaton.r2d2.travelblog.model.firebase.FirebaseLocation
import hackaton.r2d2.travelblog.model.firebase.FirebaseUser
import hackaton.r2d2.travelblog.model.firebase.FirebaseVideo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Repository private constructor() {

    companion object {
        val instance by lazy { Holder.instance }
    }

    private object Holder {
        val instance = Repository()
    }

    private val selectedUser = MutableStateFlow<User?>(null)
    private val selectedVideo = MutableStateFlow<Video?>(null)

    suspend fun loadUsers(): List<User> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("users").get().addOnCompleteListener { task ->
            task.result?.let {
                continuation.resume(it.toObjects(FirebaseUser::class.java).map { model ->
                    with(model) { User(uid, email, name, photoUrl, videos) }
                })
            }
            task.exception?.let { continuation.resumeWithException(it) }
        }
    }

    suspend fun loadVideos(userId: String): List<Video> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("users").document(userId)
            .collection("videos")
            .orderBy("start", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                task.result?.let {
                    continuation.resume(it.toObjects(FirebaseVideo::class.java).map { model ->
                        with(model) {
                            Video(
                                id = id,
                                title = title,
                                description = description,
                                channelId = channelId,
                                channelTitle = channelTitle,
                                start = start.toDate(),
                                end = end.toDate(),
                                thumbnail = thumbnail
                            )
                        }
                    })
                }
                task.exception?.let { continuation.resumeWithException(it) }
            }
    }

    suspend fun loadLocations(userId: String, start: Date, end: Date): List<Location> =
        suspendCoroutine { continuation ->
            Firebase.firestore.collection("users").document(userId)
                .collection("locations")
                .whereGreaterThanOrEqualTo("timestamp", Timestamp(start))
                .whereLessThanOrEqualTo("timestamp", Timestamp(end))
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener { task ->
                    task.result?.let {
                        continuation.resume(
                            it.toObjects(FirebaseLocation::class.java).map { model ->
                                with(model) {
                                    Location(
                                        latLng = LatLng(latitude, longitude),
                                        timestamp = timestamp.toDate()
                                    )
                                }
                            })
                    }
                    task.exception?.let { continuation.resumeWithException(it) }
                }
        }

    fun selectUser(user: User) {
        selectedUser.value = user
    }

    suspend fun loadSelectedUser(): User = selectedUser.filterNotNull().first()

    fun selectVideo(video: Video) {
        selectedVideo.value = video
    }

    suspend fun loadSelectedVideo(): Video = selectedVideo.filterNotNull().first()
}