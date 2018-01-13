package me.zhaoweihao.hnuplus.Utils;

import android.graphics.Color;

import com.google.gson.Gson;
import com.taishi.flipprogressdialog.FlipProgressDialog;

import java.util.ArrayList;
import java.util.List;

import me.zhaoweihao.hnuplus.Gson.Translate;
import me.zhaoweihao.hnuplus.Gson.Weather;
import me.zhaoweihao.hnuplus.R;


/**
 * Created by Zhaoweihao on 2018/1/6.
 */

public class Utility {
    public static Weather handleWeatherResponse(String response){
        try{
            Gson gson=new Gson();
            Weather weather=gson.fromJson(response,Weather.class);
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
        }
            return null;
    }

    public static Translate handleTranslateResponse(String response){
        try{
            Gson gson=new Gson();
            Translate translate=gson.fromJson(response,Translate.class);
            return translate;
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    public static FlipProgressDialog myDialog(){
        List<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.ic_favorite_border_white_24dp);
        imageList.add(R.drawable.ic_favorite_white_24dp);

        FlipProgressDialog flipY = new FlipProgressDialog();
        flipY.setImageList(imageList);
        flipY.setCanceledOnTouchOutside(false);
        flipY.setOrientation("rotationY");
        flipY.setBackgroundColor(Color.parseColor("#FF4081"));
        flipY.setDimAmount(0.3f);
        flipY.setCornerRadius(32);

        return flipY;

    }



}
