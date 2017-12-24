package me.zhaoweihao.hnuplus.Adapter

import android.content.Context
import android.content.Intent
import android.media.Image
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView
import com.bumptech.glide.Glide
import com.yoavst.kotlin.`KotlinPackage$Toasts$53212cf1`.toast

import me.zhaoweihao.hnuplus.CommentActivity
import me.zhaoweihao.hnuplus.JavaBean.Post
import me.zhaoweihao.hnuplus.MainActivity
import me.zhaoweihao.hnuplus.PhotoViewActivity
import me.zhaoweihao.hnuplus.R

/**
 * Created by Administrator on 2017/11/9.
 */

/**
 * Show post's data to recyclerview
 */
class PostAdapter(private val mPostList: List<Post>,private val disableCode: Int) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var mContext: Context? = null

    private val disable = disableCode

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var postAuthor = view.findViewById<TextView>(R.id.tv_author)
        var postContent = view.findViewById<TextView>(R.id.tv_content)
        var postImage = view.findViewById<ImageView>(R.id.iv_post)
        var postView: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.posts_item, parent, false)
        val holder = ViewHolder(view)
        when(disable){
            1 -> {
                holder.postView.setOnClickListener {
                    val position = holder.adapterPosition
                    val post = mPostList[position]
                    val intent = Intent(mContext, CommentActivity::class.java)
                    intent.putExtra("author", post.author!!.username)
                    intent.putExtra("content", post.content)
                    intent.putExtra("objectID", post.objectId)
                    intent.putExtra("authorObjectID", post.author!!.objectId)
                    if (post.image == null){

                    }else{
                        intent.putExtra("imageUrl", post.image!!.fileUrl)
                    }
                    (mContext as MainActivity).startActivity(intent)
                }
                holder.postImage.setOnClickListener {
                    val position = holder.adapterPosition
                    val post = mPostList[position]
                    val intent = Intent(mContext, PhotoViewActivity::class.java)
                    intent.putExtra("imageUrl", post.image!!.fileUrl)
                    (mContext as MainActivity).startActivity(intent)
                }
            }

            0 -> {
                holder.postView.setOnClickListener {
                    toast(mContext,"Please check your network status")
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPostList[position]
        holder.postAuthor.text = post.author!!.username
        holder.postContent.text = post.content
        if (post.image == null) {
            Glide.clear(holder.postImage)
            holder.postImage.visibility = View.GONE
        } else {
            holder.postImage.visibility = View.VISIBLE
            Glide.with(mContext).load(post.image!!.fileUrl).into(holder.postImage)
        }

    }

    override fun getItemCount(): Int {
        return mPostList.size
    }
}
