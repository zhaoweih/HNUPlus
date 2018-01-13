package me.zhaoweihao.hnuplus

import android.app.FragmentTransaction
import android.content.Intent
import android.graphics.Color


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener

import kotlinx.android.synthetic.main.activity_main.*
import me.zhaoweihao.hnuplus.Constant.Constant
import me.zhaoweihao.hnuplus.Interface.Communitynterface
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Post
import cn.bmob.v3.listener.UploadFileListener
import cn.bmob.v3.datatype.BmobFile
import org.jetbrains.anko.toast
import java.io.File

import com.taishi.flipprogressdialog.FlipProgressDialog
import me.zhaoweihao.hnuplus.Utils.Utility
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size


/**
 * Created by ZhaoWeihao on 2017/11/9.
 * Github:https://github.com/zhaoweihaoChina
 */
class MainActivity : AppCompatActivity(){

    /**
     * 3 Fragment for display
     */
    private var communityFragment: CommunityFragment? = null
    private var moreFragment: MoreFragment? = null
    private var userFragment: UserFragment? = null

    /**
     * Used to call fragment method
     */
    private var listener: Communitynterface? = null

    private var flipProgressDialog:FlipProgressDialog? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        title="HNUPlus(Preview Version)"

        flipProgressDialog = Utility.myDialog()

        Bmob.initialize(this, Constant.APP_ID)

        bindListener()

        // select 0 tab when first run
        setTabSelection(0)
    }

    private fun bindListener() {
        community_layout!!.setOnClickListener{setTabSelection(0)}
        more_layout!!.setOnClickListener{setTabSelection(1)}
        user_layout!!.setOnClickListener{setTabSelection(2)}
    }

    /**
     * Set the selected tab page based on the index parameter passed in
     */
    private fun setTabSelection(index: Int) {
        // Clear the last selected state before each selection
        clearSelection()
        // Open a Fragment transaction
        val transaction = fragmentManager!!.beginTransaction()
        // Hide all the Fragment first to prevent multiple Fragment displayed on the screen
        hideFragments(transaction)
        when (index) {
            0 -> {
                community_image!!.setImageResource(R.drawable.community)
                community_text!!.setTextColor(Color.WHITE)
                if (communityFragment == null) {
                    communityFragment = CommunityFragment()
                    setListener(communityFragment!!)
                    transaction.add(R.id.content, communityFragment)
                } else {
                    transaction.show(communityFragment)
                }
            }
            1 -> {
                more_image!!.setImageResource(R.drawable.more)
                more_text!!.setTextColor(Color.WHITE)
                if (moreFragment == null) {
                    moreFragment = MoreFragment()
                    transaction.add(R.id.content, moreFragment)
                } else {
                    transaction.show(moreFragment)
                }
            }
            2 -> {
                user_image!!.setImageResource(R.drawable.user)
                user_text!!.setTextColor(Color.WHITE)
                if (userFragment == null) {
                    userFragment = UserFragment()
                    transaction.add(R.id.content, userFragment)
                } else {
                    transaction.show(userFragment)
                }
            }
        }
        transaction.commit()
    }

    /**
     * Clear all the selected state
     */
    private fun clearSelection() {
        community_image!!.setImageResource(R.drawable.community_grey)
        community_text!!.setTextColor(Color.parseColor("#82858b"))
        more_image!!.setImageResource(R.drawable.more_grey)
        more_text!!.setTextColor(Color.parseColor("#82858b"))
        user_image!!.setImageResource(R.drawable.user_grey)
        user_text!!.setTextColor(Color.parseColor("#82858b"))
    }

    /**
     * Hide all the Fragments
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        if (communityFragment != null) {
            transaction.hide(communityFragment)
        }
        if (moreFragment != null) {
            transaction.hide(moreFragment)
        }
        if (userFragment != null) {
            transaction.hide(userFragment)
        }
    }

    public fun gotoPostFragment(){
        val intent = Intent(this, PostActivity::class.java)
                startActivityForResult(intent, 1)
    }

    fun setListener(listener: Communitynterface) {
        this.listener = listener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            //receive data from PostActivity
            1 -> if (resultCode == RESULT_OK) {
                val returnedData = data!!.getStringExtra("data_return")
                val returnedPath = data!!.getStringExtra("data_return_2")
                //check whether the user selects the picture
                //if returnedPath is null,it means the user did not selects the picture
                if(returnedPath == null){
                    //check the receive data is empty or not
                    if (returnedData == "") {
                        Toast.makeText(this, "empty text", Toast.LENGTH_SHORT).show()
                    } else {
                        //send data to server via bmob sdk
                        val user = BmobUser.getCurrentUser(MyUser::class.java)
                        val post = Post()
                        //show uploading progressdialog
                        flipProgressDialog!!.show(getFragmentManager(),"")

                        post.content = returnedData
                        post.author = user

                        post.save(object : SaveListener<String>() {

                            override fun done(objectId: String, e: BmobException?) {
                                if (e == null) {
                                    flipProgressDialog!!.dismiss()
                                    Toast.makeText(this@MainActivity, "post successfully", Toast.LENGTH_SHORT).show()
                                    viewKonfetti.build()
                                            .addColors(Color.parseColor("#fce18a"), Color.parseColor("#ff726d"), Color.parseColor("#b48def"),Color.parseColor("#f4306d"))
                                            .setDirection(0.0, 359.0)
                                            .setSpeed(1f, 5f)
                                            .setFadeOutEnabled(true)
                                            .setTimeToLive(600L)
                                            .addShapes(Shape.RECT, Shape.CIRCLE)
                                            .addSizes(Size(12))
                                            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
                                            .stream(300, 5000L)
                                    listener!!.myMethod()
                                } else {
                                    Toast.makeText(this@MainActivity, "post failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

                    }
                }
                else{
                    Log.d("MA",returnedPath)
                    //check the receive data is empty or not
                    if (returnedData == "") {
                        Toast.makeText(this, "empty text", Toast.LENGTH_SHORT).show()
                    } else {
                        //send data to server via bmob sdk
                        val user = BmobUser.getCurrentUser(MyUser::class.java)
                        val post = Post()

                        val bmobFile = BmobFile(File(returnedPath))
                        //show uploading progressdialog
                        flipProgressDialog!!.show(getFragmentManager(),"")
                        //upload photo
                        bmobFile.uploadblock(object : UploadFileListener() {

                            override fun done(e: BmobException?) {
                                if (e == null) {


                                    post.content = returnedData
                                    post.author = user
                                    post.image = bmobFile
                                    //save post
                                    post.save(object : SaveListener<String>() {

                                        override fun done(objectId: String, e: BmobException?) {
                                            if (e == null) {
                                                Toast.makeText(this@MainActivity, "post successfully", Toast.LENGTH_SHORT).show()
                                                flipProgressDialog!!.dismiss()
                                                listener!!.myMethod()
                                            } else {
                                                Toast.makeText(this@MainActivity, "post failed", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    })

                                } else {
                                    toast("upload file failed : " + e.message)
                                }

                            }

                            override fun onProgress(value: Int?) {
                                // return uploading percentage
                            }
                        })

                    }
                }

            }
                }

    }

}
