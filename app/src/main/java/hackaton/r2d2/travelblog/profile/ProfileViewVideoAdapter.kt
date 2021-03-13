package hackaton.r2d2.travelblog.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.api.services.youtube.model.Video
import hackaton.r2d2.travelblog.R

class ProfileViewVideoAdapter : ListAdapter<Video, ProfileViewVideoAdapter.ProfileViewHolder>(
    VIDEO_COMPARATOR
) {

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val imgVideoImageView: ImageView = itemView.findViewById(R.id.tv_img_video)

        fun bind(imgUrl: String) {
           imgVideoImageView.load(imgUrl)
        }

        companion object {
            fun create(parent: ViewGroup): ProfileViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_blogger_item_video, parent, false)
                return ProfileViewHolder(
                    view
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder.create(
            parent
        )
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind("Картинка")
    }

    companion object {
        private val VIDEO_COMPARATOR = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}