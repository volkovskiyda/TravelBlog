package hackaton.r2d2.travelblog.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.api.load
import hackaton.r2d2.travelblog.databinding.FragmentBloggerProfileBinding
import hackaton.r2d2.travelblog.model.User
import hackaton.r2d2.travelblog.model.Video

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentBloggerProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBloggerProfileBinding.inflate(inflater)

        //get par
        val user: User? = User("1", "1", "1", "1")

        if (user!=null) {
            viewModel.updateUser(user)
            bindView(viewModel.selectedUser.value!!, viewModel.allVideo.value!!)
        }    else {
            Toast.makeText(this.context, "failed to load data", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }

    private fun bindView(user: User, video: List<Video>){
        binding.tvName.text = user.name
        binding.ivAvatar.load(user.photoUrl)
        binding.videoList.adapter = ProfileViewVideoAdapter()
    }
}