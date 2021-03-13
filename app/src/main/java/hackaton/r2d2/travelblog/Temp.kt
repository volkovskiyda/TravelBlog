package hackaton.r2d2.travelblog

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Repository usage
fun repository() {
//    val scope = lifecycleScope
    val scope = CoroutineScope(Dispatchers.Main)
    val repository = Repository()
    scope.launch {
        val users = repository.loadUsers()
        val user = users.first()
        val videos = repository.loadVideos(user.uid)
        println("")
    }
}