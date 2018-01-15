package me.zhaoweihao.hnuplus

import android.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_comment.*
import me.zhaoweihao.hnuplus.Interface.CommentInterface

class CommentActivity : AppCompatActivity() {


    private var commentFragment: CommentFragment? = null
    private var listener: CommentInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        title = getString(R.string.comment_title)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        // Open a Fragment transaction
        val transaction = (fragmentManager as FragmentManager?)!!.beginTransaction()
        if (commentFragment == null) {
            // If CommentFragment is empty, create one and add to the screen
            commentFragment = CommentFragment()
            setListener(commentFragment!!)
            transaction.add(R.id.fl_comment, commentFragment)
        } else {
            // If CommentFragment is not empty, it will be displayed directly
            transaction.show(commentFragment)
        }
        transaction.commit()

        iv_comment!!.setOnClickListener { listener!!.myAction(et_comment!!.text.toString()) }
    }

    fun setListener(listener: CommentInterface) {
        this.listener = listener
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}
