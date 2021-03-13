package hackaton.r2d2.travelblog.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hackaton.r2d2.travelblog.Repository
import hackaton.r2d2.travelblog.model.User
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    private val _allUsers = MutableLiveData<List<User>>()
    val allUsers: LiveData<List<User>> get() = _allUsers

    init {
        viewModelScope.launch {
            _allUsers.value = runCatching { Repository.instance.loadUsers() }.getOrNull().orEmpty()
        }
    }

    fun displayUserDetails(user: User) {
        Repository.instance.selectUser(user)
    }
}
