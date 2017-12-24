package me.zhaoweihao.hnuplus

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.ViewGroup

class PhotoViewActivity : AppCompatActivity() {

    private var fragmentManager: FragmentManager? = null
    private var photoViewFragment: PhotoViewFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove the ActionBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById<ViewGroup>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

        setContentView(R.layout.activity_photo_view)

        fragmentManager = supportFragmentManager

        // Open a Fragment transaction
        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()

        if (photoViewFragment == null) {
            // If PhotoViewFragment is empty, create one and add to the screen
            photoViewFragment = PhotoViewFragment()
            transaction.add(R.id.fl_photoview, photoViewFragment)
        } else {
            // If PhotoViewFragment is not empty, it will be displayed directly
            transaction.show(photoViewFragment)
        }
        transaction.commit()

    }
}
