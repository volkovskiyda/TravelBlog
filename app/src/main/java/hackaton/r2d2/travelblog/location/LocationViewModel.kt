package hackaton.r2d2.travelblog.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackaton.r2d2.travelblog.Repository
import hackaton.r2d2.travelblog.model.Location
import hackaton.r2d2.travelblog.model.Video
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    private val _selectedVideo = MutableLiveData<Video>()
    val selectedVideo: LiveData<Video> get() = _selectedVideo

    private val _locations = MutableLiveData<List<Location>>()
    val locations: LiveData<List<Location>> get() = _locations

    private val _seekPosition = MutableLiveData<Float>()
    val seekPosition: LiveData<Float> get() = _seekPosition

    init {
        viewModelScope.launch {
            val user = Repository.instance.loadSelectedUser()
            val video = Repository.instance.loadSelectedVideo()
            _selectedVideo.value = video
            val locations = runCatching { Repository.instance.loadLocations(user.uid, video.start, video.end) }.getOrNull().orEmpty()
            _locations.value = locations
        }
    }

    fun selectLocation(location: Location) {
        val locationTime = location.timestamp.time
        _seekPosition.value = (locationTime - (selectedVideo.value?.start?.time ?: locationTime)) / 1000f
    }
}