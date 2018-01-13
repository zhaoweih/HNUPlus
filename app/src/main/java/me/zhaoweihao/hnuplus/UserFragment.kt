package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.content.Intent

import android.os.Bundle


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import cn.bmob.v3.BmobUser
import com.taishi.flipprogressdialog.FlipProgressDialog
import kotlinx.android.synthetic.main.user_layout.*

import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Utils.Utility


/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class UserFragment : Fragment() {

    private var userInfo: MyUser? = null

    private var flipProgressDialog:FlipProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val userLayout = inflater!!.inflate(R.layout.user_layout,
                container, false)
        return userLayout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flipProgressDialog = Utility.myDialog()
        btn_signout!!.setOnClickListener {
            BmobUser.logOut()
            val currentUser = BmobUser.getCurrentUser()
            if (currentUser == null) {
                Toast.makeText(activity, "sign out successfully", Toast.LENGTH_SHORT).show()
                tv_signinstatus!!.text = "You have not signin"
                btn_signin_1!!.visibility = View.VISIBLE
                btn_signout!!.visibility = View.GONE
            }
        }

        btn_signin_1!!.setOnClickListener {
            val intent = Intent(activity, SigninActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onStart() {
        super.onStart()
       flipProgressDialog!!.show(fragmentManager,"")

        userInfo = BmobUser.getCurrentUser(MyUser::class.java)
        //If userInfo is not null,it means the user login successfully
        if (userInfo != null) {
            tv_signinstatus!!.text = "Welcome " + userInfo!!.username
            btn_signin_1!!.visibility = View.GONE
            btn_signout!!.visibility = View.VISIBLE
            flipProgressDialog!!.dismiss()
        } else {
            tv_signinstatus!!.text = "You have not signin"
            btn_signin_1!!.visibility = View.VISIBLE
            btn_signout!!.visibility = View.GONE
            flipProgressDialog!!.dismiss()
        }

    }

}
