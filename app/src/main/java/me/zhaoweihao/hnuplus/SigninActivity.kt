package me.zhaoweihao.hnuplus

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager

import android.os.Build
import android.view.View
import android.view.ViewGroup


class SigninActivity : AppCompatActivity() {

    private var fragmentManager: FragmentManager? = null
    private var signinFragment: SigninFragment? = null
    private var signupFragment: SignupFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById<ViewGroup>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        setContentView(R.layout.activity_signin)

        fragmentManager = supportFragmentManager

        // Open a Fragment transaction
        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()

        if (signinFragment == null) {
            // If SigninFragment is empty, create one and add to the screen
            signinFragment = SigninFragment()
            transaction.add(R.id.fl_signin, signinFragment)
        } else {
            // If SigninFragment is not empty, it will be displayed directly
            transaction.show(signinFragment)
        }
        transaction.commit()

    }

    open fun toSignupFragment(){

        // Open a Fragment transaction
        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()

        if (signinFragment != null) {
            transaction.hide(signinFragment)
        }

        if (signupFragment == null) {
            // If SignupFragment is empty, create one and add to the screen
            signupFragment = SignupFragment()
            transaction.add(R.id.fl_signin, signupFragment)
        } else {
            // If SignupFragment is not empty, it will be displayed directly
            transaction.show(signupFragment)
        }
        transaction.commit()

    }





}
