package hackaton.r2d2.travelblog.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import hackaton.r2d2.travelblog.R
import hackaton.r2d2.travelblog.databinding.FragmentOverviewBinding
import hackaton.r2d2.travelblog.profile.ProfileFragment

class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentOverviewBinding.inflate(inflater)

        val adapter = OverviewAdapter { user ->
            viewModel.displayUserDetails(user)
            parentFragmentManager.beginTransaction()
                .remove(this)
                .replace(android.R.id.content, ProfileFragment())
                .commitAllowingStateLoss()

        }
        binding.videoList.adapter = adapter

        viewModel.allUsers.observe(viewLifecycleOwner) { users -> adapter.submitList(users) }

        return binding.root
    }
}