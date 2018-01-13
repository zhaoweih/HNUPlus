package me.zhaoweihao.hnuplus


import android.content.Intent
import android.os.Bundle

import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.more_layout.*


/**
 * Created by Administrator on 2017/11/9.
 */


class MoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val moreLayout = inflater!!.inflate(R.layout.more_layout, container, false)

        return moreLayout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ll_to_weather.setOnClickListener {
            val intent = Intent(activity,WeatherActivity::class.java)
            startActivity(intent)
        }

        ll_to_note.setOnClickListener {
            val intent = Intent(activity,NoteActivity::class.java)
            startActivity(intent)
        }

//        ll_to_translate.setOnClickListener {
//            val intent = Intent(activity,TranslateActivity::class.java)
//            startActivity(intent)
//        }
        }

    }






