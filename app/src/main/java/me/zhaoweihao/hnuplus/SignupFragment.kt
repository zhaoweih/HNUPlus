package me.zhaoweihao.hnuplus

import android.app.Fragment
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.taishi.flipprogressdialog.FlipProgressDialog
import kotlinx.android.synthetic.main.signup_layout.*
import me.zhaoweihao.hnuplus.Bmob.MyUser
import me.zhaoweihao.hnuplus.Utils.Utility

/**
 * Created by ZhaoWeihao on 2017/11/10.
 */

class SignupFragment : Fragment() {

    private var anim: AnimationDrawable? = null
    private var flipProgressDialog:FlipProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val signupLayout = inflater!!.inflate(R.layout.signup_layout,
                container, false)
        return signupLayout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Gradient background
        anim = container_2!!.background as AnimationDrawable
        anim!!.setEnterFadeDuration(6000)
        anim!!.setExitFadeDuration(2000)

        flipProgressDialog=Utility.myDialog()

        btn_signup_2!!.setOnClickListener {
            val username = et_username_2!!.text.toString()
            val password = et_password_2!!.text.toString()
            val passwordConfirm = et_password_confirm!!.text.toString()
            val email = et_email!!.text.toString()

            if (username == "" || password == "" || email == "" || passwordConfirm == "") {
                Toast.makeText(activity, R.string.empty_text_warn, Toast.LENGTH_SHORT).show()
            } else if (password != passwordConfirm) {
                Toast.makeText(activity, getString(R.string.confirm_pwd_not_pwd), Toast.LENGTH_SHORT).show()
            } else {
                flipProgressDialog!!.show(fragmentManager,"")

                val bu = BmobUser()
                bu.username = username
                bu.setPassword(password)
                bu.email = email
                //signup
                bu.signUp(object : SaveListener<MyUser>() {
                    override fun done(s: MyUser, e: BmobException?) {
                        if (e == null) {
                            Toast.makeText(activity, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                            //Auto login after login successfully
                            bu.login(object : SaveListener<BmobUser>() {

                                override fun done(bmobUser: BmobUser, e: BmobException?) {
                                    if (e == null) {
                                        Toast.makeText(activity, R.string.signin_success, Toast.LENGTH_SHORT).show()
                                        flipProgressDialog!!.dismiss()
                                        activity.finish()
                                    } else {
                                        Toast.makeText(activity, R.string.signin_failed, Toast.LENGTH_SHORT).show()
                                        flipProgressDialog!!.dismiss()
                                    }
                                }
                            })
                        } else {
                            Toast.makeText(activity, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show()
                            flipProgressDialog!!.dismiss()
                        }
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (anim != null && !anim!!.isRunning)
            anim!!.start()
    }

    override fun onPause() {
        super.onPause()
        if (anim != null && anim!!.isRunning) {
            anim!!.stop()
        }
    }

}
