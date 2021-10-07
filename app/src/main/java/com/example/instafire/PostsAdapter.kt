package com.example.instafire

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instafire.model.Post
import kotlinx.android.synthetic.main.item_post.view.*
private const val TAG="adapterActivity"

class PostsAdapter(val context: Context, val posts:List<Post>):
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false)
        for(post in posts)
            Log.i(TAG,"Post ${post.description}")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.tvDescription.text=posts[position].description
        holder.itemView.tvUsername.text= posts[position].user?.username
        holder.itemView.tvDescription.text=posts[position].description
        Glide.with(context).load(posts[position].image_url).into(holder.itemView.tvPost)
        holder.itemView.tvRelativeTime.text=DateUtils.getRelativeTimeSpanString(posts[position].creation_time_ms)

        // holder.bind(posts[position])
    }

    override fun getItemCount()=posts.size

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    }

}