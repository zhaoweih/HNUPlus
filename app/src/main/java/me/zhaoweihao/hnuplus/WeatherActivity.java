package me.zhaoweihao.hnuplus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.io.IOException;

import me.zhaoweihao.hnuplus.Gson.Channel;
import me.zhaoweihao.hnuplus.Gson.Weather;
import me.zhaoweihao.hnuplus.Utils.HttpUtil;
import me.zhaoweihao.hnuplus.Utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements TencentLocationListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(permissions, 0);
            }else {
                TencentLocationRequest request = TencentLocationRequest.create();
                TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
                int error = locationManager.requestLocationUpdates(request, this);
            }
        }




    }

    private void requestWeather(String latitude,String longitude) {

        String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(SELECT%20woeid%20FROM%20geo.places%20WHERE%20text%3D%22("+latitude+"%2C"+longitude+")%22)%20and%20u%3D'c'&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Weather weather = Utility.handleWeatherResponse(responseData);
                Channel channel = weather.getQuery().getResults().getChannel();
                String location = channel.getLocation().getCity()+","+channel.getLocation().getRegion()+","+channel.getLocation().getCountry();
                Log.d("WA2",location);
            }
        });
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            Log.d("WA1",tencentLocation.getAddress());
            String latitude = String.valueOf(tencentLocation.getLatitude());
            String longitude = String.valueOf(tencentLocation.getLongitude());
            requestWeather(latitude,longitude);
            TencentLocationManager locationManager =
                    TencentLocationManager.getInstance(this);
            locationManager.removeUpdates(this);
        } else {
            // 定位失败
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
    }
}
