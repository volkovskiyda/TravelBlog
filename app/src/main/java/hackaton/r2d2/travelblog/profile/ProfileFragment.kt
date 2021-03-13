package hackaton.r2d2.travelblog.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.api.load
import hackaton.r2d2.travelblog.databinding.FragmentBloggerProfileBinding
import hackaton.r2d2.travelblog.location.LocationFragment
import hackaton.r2d2.travelblog.model.User

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentBloggerProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBloggerProfileBinding.inflate(inflater)

        val adapter = ProfileViewVideoAdapter { video ->
            viewModel.displayVideo(video)
            parentFragmentManager.beginTransaction()
                .remove(this)
                .replace(android.R.id.content, LocationFragment())
                .commitAllowingStateLoss()
        }
        binding.videoList.adapter = adapter

        viewModel.selectedUser.observe(viewLifecycleOwner) { user -> bindUser(user) }
        viewModel.allVideo.observe(viewLifecycleOwner) { videos -> adapter.submitList(videos) }

        return binding.root
    }

    private fun bindUser(user: User) {
        binding.tvName.text = user.name
        binding.ivAvatar.load(user.photoUrl)
    }
}