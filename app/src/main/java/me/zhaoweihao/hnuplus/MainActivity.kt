package me.zhaoweihao.hnuplus

import android.content.Intent
import android.graphics.Color

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
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
import me.zhaoweihao.hnuplus.Interface.HotInterface
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Post
import cn.bmob.v3.listener.UploadFileListener
import cn.bmob.v3.datatype.BmobFile
import org.jetbrains.anko.toast
import java.io.File

import android.app.ProgressDialog


/**
 * Created by ZhaoWeihao on 2017/11/9.
 * Github:https://github.com/zhaoweihaoChina
 */
class MainActivity : AppCompatActivity(){

    /**
     * Four Fragment for display
     */
    private var hotFragment: HotFragment? = null
    private var moreFragment: MoreFragment? = null
//    private var newsFragment: NewsFragment? = null
    private var userFragment: UserFragment? = null

    /**
     * Used to manage Fragment
     */
    private var fragmentManager: FragmentManager? = null

    /**
     * Used to call fragment method
     */
    private var listener: HotInterface? = null

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        Bmob.initialize(this, Constant.APP_ID)

        bindListener()
        fragmentManager = supportFragmentManager

        // select 0 tab when first run
        setTabSelection(0)
    }

    private fun bindListener() {
        hot_layout!!.setOnClickListener{setTabSelection(0)}
        more_layout!!.setOnClickListener{setTabSelection(1)}
//        news_layout!!.setOnClickListener{setTabSelection(2)}
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
                // When you click the message tab, change the tab's picture and text color
                hot_image!!.setImageResource(R.drawable.ic_home_black_24dp)
                hot_text!!.setTextColor(Color.WHITE)
                if (hotFragment == null) {
                    // If Hot Fragment is empty, create one and add to the screen
                    hotFragment = HotFragment()
                    setListener(hotFragment!!)
                    transaction.add(R.id.content, hotFragment)
                } else {
                    // If Hot Fragment is not empty, it will be displayed directly
                    transaction.show(hotFragment)
                }
            }
            1 -> {
                more_image!!.setImageResource(R.drawable.ic_home_black_24dp)
                more_text!!.setTextColor(Color.WHITE)
                if (moreFragment == null) {
                    moreFragment = MoreFragment()
                    transaction.add(R.id.content, moreFragment)
                } else {
                    transaction.show(moreFragment)
                }
            }
//            2 -> {
//                news_image!!.setImageResource(R.drawable.ic_home_black_24dp)
//                news_text!!.setTextColor(Color.WHITE)
//                if (newsFragment == null) {
//                    newsFragment = NewsFragment()
//                    transaction.add(R.id.content, newsFragment)
//                } else {
//                    transaction.show(newsFragment)
//                }
//            }
            2 -> {
                user_image!!.setImageResource(R.drawable.ic_home_black_24dp)
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
        hot_image!!.setImageResource(R.drawable.ic_home_grey_24dp)
        hot_text!!.setTextColor(Color.parseColor("#82858b"))
        more_image!!.setImageResource(R.drawable.ic_home_grey_24dp)
        more_text!!.setTextColor(Color.parseColor("#82858b"))
//        news_image!!.setImageResource(R.drawable.ic_home_grey_24dp)
//        news_text!!.setTextColor(Color.parseColor("#82858b"))
        user_image!!.setImageResource(R.drawable.ic_home_grey_24dp)
        user_text!!.setTextColor(Color.parseColor("#82858b"))
    }

    /**
     * Hide all the Fragments
     */
    private fun hideFragments(transaction: FragmentTransaction) {
        if (hotFragment != null) {
            transaction.hide(hotFragment)
        }
        if (moreFragment != null) {
            transaction.hide(moreFragment)
        }
//        if (newsFragment != null) {
//            transaction.hide(newsFragment)
//        }
        if (userFragment != null) {
            transaction.hide(userFragment)
        }
    }

    public fun gotoPostFragment(){
        val intent = Intent(this, PostActivity::class.java)
                startActivityForResult(intent, 1)
    }

    fun setListener(listener: HotInterface) {
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
                        progressDialog = ProgressDialog(this)
                        progressDialog!!.setMessage("Uploading...")
                        progressDialog!!.setCancelable(true)
                        progressDialog!!.show()

                        post.content = returnedData
                        post.author = user

                        post.save(object : SaveListener<String>() {

                            override fun done(objectId: String, e: BmobException?) {
                                if (e == null) {
                                    progressDialog!!.hide()
                                    Toast.makeText(this@MainActivity, "post successfully", Toast.LENGTH_SHORT).show()
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
                        progressDialog = ProgressDialog(this)
                        progressDialog!!.setMessage("Uploading...")
                        progressDialog!!.setCancelable(true)
                        progressDialog!!.show()
                        //upload photo
                        bmobFile.uploadblock(object : UploadFileListener() {

                            override fun done(e: BmobException?) {
                                if (e == null) {
                                    progressDialog!!.hide()

                                    post.content = returnedData
                                    post.author = user
                                    post.image = bmobFile
                                    //save post
                                    post.save(object : SaveListener<String>() {

                                        override fun done(objectId: String, e: BmobException?) {
                                            if (e == null) {
                                                Toast.makeText(this@MainActivity, "post successfully", Toast.LENGTH_SHORT).show()
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
