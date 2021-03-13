package hackaton.r2d2.travelblog.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import hackaton.r2d2.travelblog.R
import hackaton.r2d2.travelblog.model.Video

class ProfileViewVideoAdapter(
    private val onVideoClick: (Video) -> Unit
) : ListAdapter<Video, ProfileViewVideoAdapter.ProfileVideoViewHolder>(VIDEO_COMPARATOR) {

    class ProfileVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val youtubePlayer: YouTubePlayerView = itemView.findViewById(R.id.youtube_player_item)
        private val timeTextView: TextView = itemView.findViewById(R.id.tv_time)
        val content: View = itemView.findViewById(R.id.content)

        fun bind(video: Video) {
            timeTextView.text = video.start
            youtubePlayer.getPlayerUiController().showFullscreenButton(true)

            youtubePlayer.addYouTubePlayerListener(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {

                        /**
                        Set videoId
                         */
                        val videoId = video.id
                        //первый параметр ID видео
                        //второй параметр с какой секунды запустить
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })
        }

        companion object {
            fun create(parent: ViewGroup): ProfileVideoViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_blogger_item_video, parent, false)
                return ProfileVideoViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileVideoViewHolder {
        return ProfileVideoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ProfileVideoViewHolder, position: Int) {
        val current = getItem(position)
        holder.content.setOnClickListener { onVideoClick(current) }
        holder.bind(current)
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