package hackaton.r2d2.travelblog.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackaton.r2d2.travelblog.Repository
import hackaton.r2d2.travelblog.model.User
import hackaton.r2d2.travelblog.model.Video
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _selectedUser = MutableLiveData<User>()
    val selectedUser: LiveData<User>
        get() = _selectedUser

    private val _allVideo = MutableLiveData<List<Video>>()
    val allVideo: LiveData<List<Video>>
        get() = _allVideo

    init {
        val repository = Repository.instance
        viewModelScope.launch {
            val user = repository.loadSelectedUser()
            val videos = runCatching { repository.loadVideos(user.uid) }.getOrNull().orEmpty()
            _selectedUser.value = user
            _allVideo.value = videos
        }
    }

    fun displayVideo(video: Video) {

    }
}