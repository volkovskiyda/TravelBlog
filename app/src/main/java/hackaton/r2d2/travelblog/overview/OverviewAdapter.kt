package hackaton.r2d2.travelblog.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.request.RequestDisposable
import hackaton.r2d2.travelblog.R
import hackaton.r2d2.travelblog.model.User

class OverviewAdapter(private val onClickListener: OnClickListener) : ListAdapter<User, OverviewAdapter.UserViewHolder>(
    USERS_COMPARATOR
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        return UserViewHolder.create(
            parent
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(current)
        }

        holder.bind(current.photoUrl, current.name)
    }

    class OnClickListener(val clickListener: (user: User) -> Unit) {
        fun onClick(user: User) = clickListener(user)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoUrlImageView: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        private val countTextView: TextView = itemView.findViewById(R.id.tv_count)

        fun bind(photoUrl: String, name: String) {
            val requestDisposable : RequestDisposable = photoUrlImageView.load(photoUrl) {

            }
            nameTextView.text = name
            countTextView.text = "2 видео"
        }

        companion object {
            fun create(parent: ViewGroup) : UserViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_overview_item, parent, false)
                return UserViewHolder(
                    view
                )
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