package hackaton.r2d2.travelblog

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hackaton.r2d2.travelblog.model.User
import hackaton.r2d2.travelblog.model.Video
import hackaton.r2d2.travelblog.model.firebase.FirebaseUser
import hackaton.r2d2.travelblog.model.firebase.FirebaseVideo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Repository private constructor() {

    companion object { val instance by lazy { Holder.instance } }

    private object Holder { val instance = Repository() }

    private val selectedUser = MutableStateFlow<User?>(null)

    suspend fun loadUsers(): List<User> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("users").get().addOnCompleteListener { task ->
            task.result?.let { continuation.resume(it.toObjects(FirebaseUser::class.java).map { model ->
                with(model) { User(uid, email, name, photoUrl, videos) }
            }) }
            task.exception?.let { continuation.resumeWithException(it) }
        }
    }

    suspend fun loadVideos(userId: String): List<Video> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("users").document(userId).collection("videos").get()
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
                                start = start.toDate().toString(),
                                end = end.toDate().toString(),
                                thumbnail = thumbnail
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
}