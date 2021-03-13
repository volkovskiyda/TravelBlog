package hackaton.r2d2.travelblog.overview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import hackaton.r2d2.travelblog.R
import hackaton.r2d2.travelblog.model.User

class OverviewAdapter(
    private val onUserClick: (User) -> Unit
) : ListAdapter<User, OverviewAdapter.UserViewHolder>(USERS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder.create(parent)

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { onUserClick(current) }
        holder.bind(current)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoUrlImageView: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val countTextView: TextView = itemView.findViewById(R.id.tv_count)

        fun bind(user: User) {
            photoUrlImageView.load(user.photoUrl)
            nameTextView.text = user.name
            countTextView.text = "${user.videos} videos"
        }

        companion object {
            fun create(parent: ViewGroup): UserViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_overview_item, parent, false)
                return UserViewHolder(view)
            }
        }
    }

    companion object {
        private val USERS_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.uid == newItem.uid
            }

        }
    }


}