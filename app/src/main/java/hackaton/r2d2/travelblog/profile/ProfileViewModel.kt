package hackaton.r2d2.travelblog.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hackaton.r2d2.travelblog.Repository
import hackaton.r2d2.travelblog.model.User
import hackaton.r2d2.travelblog.model.Video
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _selectedUser = MutableLiveData<User>()
    val selectedUser: LiveData<User>
        get() = _selectedUser

    private val _allVideo = MutableLiveData<List<Video>>()
    val allVideo: LiveData<List<Video>>
        get() = _allVideo

    fun updateUser(user: User) {
        _selectedUser.value = user
        loadVideo()
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private fun loadVideo() {
        val repository = Repository()
        coroutineScope.launch {
            try {
                val userId = selectedUser.value!!.uid
                val loadVideo = repository.loadVideos(userId)
                _allVideo.value = loadVideo
            } catch (e: Exception) {
                _allVideo.value = ArrayList()
            }
        }
    }
}