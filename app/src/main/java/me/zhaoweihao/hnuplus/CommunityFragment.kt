package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import java.util.Collections

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener

import me.zhaoweihao.hnuplus.Adapter.PostAdapter
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Bmob.Post

import android.preference.PreferenceManager

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yoavst.kotlin.`KotlinPackage$SystemServices$69d7d2d0`.connectivityManager

import kotlinx.android.synthetic.main.community_layout.*
import me.zhaoweihao.hnuplus.Interface.Communitynterface

/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class CommunityFragment : Fragment(), Communitynterface {

    private var layoutManager: LinearLayoutManager? = null
    private var adapter: PostAdapter? = null
    private var userInfo: MyUser? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val communityLayout = inflater!!.inflate(R.layout.community_layout,
                container, false)
        return communityLayout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        pull_to_refresh!!.setOnRefreshListener { loadData() }


        fb!!.setOnClickListener {
            //get local user data information
            userInfo = BmobUser.getCurrentUser(MyUser::class.java)
            //if userinfo is null,it means you did not sign in
            if (userInfo != null) {
                //we will get data from PostActivity
                (activity as MainActivity).gotoPostFragment()
            } else {
                //show a snackbar to tell you to sign in
                Snackbar.make(fb!!, "You are not signin", Snackbar.LENGTH_SHORT)
                        .setAction("Sign in") {
                            val intent = Intent(activity, SigninActivity::class.java)
                            startActivity(intent)
                        }.show()

            }
        }

        rv_posts!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                //show or hide floating button when scroll the recyclerview
                if (dy > 0)
                    fb!!.hide()
                else if (dy < 0)
                    fb!!.show()
            }
        })

    }

    /**
     * show data on recyclerview
     * @networkCode 1 -> online
     *              0 -> offline
     */

    private fun refreshRecyclerView(networkCode: Int) {

        when (networkCode) {
            //query data via bmob sdk
            1 -> {
                pull_to_refresh!!.setRefreshing(true)
                val query = BmobQuery<Post>()
                //        query.setLimit(15)
                query.include("author,image,likes")
                query.findObjects(object : FindListener<Post>() {

                    override fun done(`object`: List<Post>, e: BmobException?) {
                        if (e == null) {
                            Collections.reverse(`object`)
                            //save the data to shareprefs in case of network offline status
                            saveListToPrefs(`object`)

                            layoutManager = LinearLayoutManager(activity)
                            rv_posts!!.layoutManager = layoutManager
                            adapter = PostAdapter(`object`,1)
                            rv_posts!!.adapter = adapter
                            Snackbar.make(rv_posts!!, "refresh successfully", Snackbar.LENGTH_SHORT).show()
                            pull_to_refresh!!.setRefreshing(false)

                        } else {
                            Snackbar.make(rv_posts!!, "refresh failed", Snackbar.LENGTH_SHORT).show()
                            pull_to_refresh!!.setRefreshing(false)
                        }
                    }

                })}
            //show data from shareprefs
            0 -> {
                val appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(activity.getApplicationContext())
                val gson = Gson()
                val json = appSharedPrefs.getString("MyObject", "")
                val type = object : TypeToken<List<Post>>() {
                }.type
                val postList: List<Post> = gson.fromJson(json,type)

                layoutManager = LinearLayoutManager(activity)
                rv_posts!!.layoutManager = layoutManager
                adapter = PostAdapter(postList,0)
                rv_posts!!.adapter = adapter
                Snackbar.make(rv_posts!!, "please check your network status", Snackbar.LENGTH_SHORT).show()
                pull_to_refresh!!.setRefreshing(false)
            }

        }

    }

    private fun loadData(){
        //check network status
        val conMgr = connectivityManager(context)
        val activeNetwork = conMgr.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected) {
            // notify user online and load online data
            refreshRecyclerView(1)
        } else {
            // notify user offline and load local data
            refreshRecyclerView(0)
        }
    }

    /**
     * save list<post> to sharepreferences
     */
    private fun saveListToPrefs(postList: List<Post>){
        val appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(activity.applicationContext)
        val prefsEditor = appSharedPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(postList)
        prefsEditor.putString("MyObject", json)
        prefsEditor.commit()
    }

    override fun myMethod() {
        loadData()
    }

}