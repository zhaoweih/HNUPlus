package me.zhaoweihao.hnuplus

import android.app.FragmentManager
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_post.*
import me.zhaoweihao.hnuplus.Interface.PostInterface


class PostActivity : AppCompatActivity() {

    private var postFragment: PostFragment? = null
    public var path: String? = null

    private var listener: PostInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        setSupportActionBar(my_toolbar)
        title = getString(R.string.post_title)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        // Open a Fragment transaction
        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()
        if (postFragment == null) {
            // If PostFragment is empty, create one and add to the screen
            postFragment = PostFragment()
            setListener(postFragment!!)
            transaction.add(R.id.fl_post, postFragment)
        } else {
            // If PostFragment is not empty, it will be displayed directly
            transaction.show(postFragment)
        }
        transaction.commit()

    }

    fun setListener(listener: PostInterface) {
        this.listener = listener
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
        //receive data from PostActivity
            2 -> if (resultCode == RESULT_OK) {
                //uri to path
                val uri = Matisse.obtainResult(data)[0]
                listener!!.showImage(uri)
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
                if (cursor!!.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    path = cursor.getString(columnIndex)
                    Log.d("PA",path)
                } else {
                    //boooo, cursor doesn't have rows ...
                }
                cursor.close()

            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_submit -> listener!!.myMethod()
            android.R.id.home -> finish()
        }
        return true
    }

}
