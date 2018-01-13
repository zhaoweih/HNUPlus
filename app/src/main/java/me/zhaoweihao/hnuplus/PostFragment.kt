package me.zhaoweihao.hnuplus

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.Fragment
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import com.bumptech.glide.Glide
import com.yoavst.kotlin.`KotlinPackage$Toasts$53212cf1`.toast
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.post_layout.*
import me.zhaoweihao.hnuplus.Interface.PostInterface

/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class PostFragment :  Fragment(), PostInterface {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val postLayout = inflater!!.inflate(R.layout.post_layout, container,
                false)
        return postLayout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_pic!!.setOnClickListener {
            // request runtime permission
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                showImageSelector()
            }

        }
    }

    private fun showImageSelector(){
        //Initialization Matisse image selector
        Matisse.from(activity)
                .choose(setOf(MimeType.JPEG,MimeType.PNG))
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(2)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //request runtime permission successfully
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSelector()
            } else {
                toast(activity, "denied")
            }
        }
    }

    override fun myMethod() {
        val intent = Intent()
        //Determine whether the user has a choice of pictures
        if((activity as PostActivity).path == null){
            intent.putExtra("data_return", et_post?.text.toString())
            activity.setResult(RESULT_OK,intent)
            activity.finish()
        }else{
            Log.d("PF","have photo")
            intent.putExtra("data_return", et_post?.text.toString())
            intent.putExtra("data_return_2",(activity as PostActivity).path)
            activity.setResult(RESULT_OK,intent)
            activity.finish()
        }
    }

    override fun showImage(imageUri: Uri) {
        //Show image after the user has a choice of pictures
        btn_pic.visibility = View.INVISIBLE
        tv_add_pic.visibility = View.INVISIBLE
        Glide.with(activity).load(imageUri).into(iv_show_pic)
        iv_show_pic.setOnClickListener { showImageSelector() }
    }



}