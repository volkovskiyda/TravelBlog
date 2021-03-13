package hackaton.r2d2.travelblog.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import hackaton.r2d2.travelblog.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentOverviewBinding.inflate(inflater)

        binding.videoList.adapter = OverviewAdapter(OverviewAdapter.OnClickListener {
            viewModel.displayUserDetails(it)
        })

        viewModel.navToSelectedUser.observe(this.viewLifecycleOwner, Observer {
            //navigate to Profile

            viewModel.displayUserDetailsCompleted()
        })

        return binding.root
    }


}