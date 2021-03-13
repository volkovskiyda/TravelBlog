package hackaton.r2d2.travelblog.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hackaton.r2d2.travelblog.Repository
import hackaton.r2d2.travelblog.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    private val _navToSelectedUser = MutableLiveData<User?>()
    val navToSelectedUser: MutableLiveData<User?>
        get() = _navToSelectedUser

    private val _allUsers = MutableLiveData<List<User>>()
    val allUsers: LiveData<List<User>>
        get() = _allUsers

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun displayUserDetails(user: User){
        _navToSelectedUser.value = user
    }

    init {
        getAllUsers()
    }

    fun displayUserDetailsCompleted(){
        _navToSelectedUser.value = null
    }

    fun getAllUsers() {
        val repository = Repository()
        coroutineScope.launch {
            try {
                val loadingUsers = repository.loadUsers()
                _allUsers.value = loadingUsers
            } catch (e: Exception) {
                _allUsers.value = ArrayList()
            }
        }
    }
}