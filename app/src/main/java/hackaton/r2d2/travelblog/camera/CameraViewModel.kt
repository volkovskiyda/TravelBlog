package hackaton.r2d2.travelblog.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {


    private val _statusRecording = MutableLiveData(false)
    val statusRecording:LiveData<Boolean>
    get() = _statusRecording

    fun startRecord() {
        _statusRecording.value = true
    }

    fun stopRecord(){
        _statusRecording.value = false
    }
}