package com.geronso.pearler.friends.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.geronso.pearler.R
import com.geronso.pearler.base.NetworkDataProvider
import com.geronso.pearler.profile.model.data.ProfileData
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendsAdapter : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private var users = listOf<ProfileData>()
    private var friends: Boolean = false
    var onAddFriend: (id: String) -> Unit = {}
    var onSelectedProfile: (id: String) -> Unit = {}

    fun setData(users: List<ProfileData>, friends: Boolean) {
        this.users = users
        this.friends = friends
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_friend, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.name
        holder.addFriend.visibility = if (friends) View.GONE else View.VISIBLE
        holder.addFriend.setOnClickListener {
            onAddFriend(user.id)
        }
        holder.itemView.setOnClickListener {
            onSelectedProfile(user.id)
        }
        if (user.image_url != "") {
            Picasso
                .get()
                .load(user.image_url)
                .into(holder.avatar)
        } else {
            Picasso
                .get()
                .load(R.drawable.ic_pearl)
                .into(holder.avatar)
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: CircleImageView = view.findViewById(R.id.friendAvatar)
        val name: TextView = view.findViewById(R.id.friendName)
        val addFriend: ImageView = view.findViewById(R.id.addFriend)
    }
}