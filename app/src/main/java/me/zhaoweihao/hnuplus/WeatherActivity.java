package me.zhaoweihao.hnuplus;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.pwittchen.weathericonview.WeatherIconView;
import com.taishi.flipprogressdialog.FlipProgressDialog;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhaoweihao.hnuplus.Gson.Channel;
import me.zhaoweihao.hnuplus.Gson.Forecast;
import me.zhaoweihao.hnuplus.Gson.Weather;
import me.zhaoweihao.hnuplus.Utils.HttpUtil;
import me.zhaoweihao.hnuplus.Utils.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements TencentLocationListener{


    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.degree)
    TextView degree;
    @BindView(R.id.humidity)
    TextView humidity;
    @BindView(R.id.speed)
    TextView speed;
    @BindView(R.id.sunrise)
    TextView sunrise;
    @BindView(R.id.sunset)
    TextView sunset;
    @BindView(R.id.forecast_layout)
    LinearLayout forecastLayout;
    @BindView(R.id.chill)
    TextView chill;
    @BindView(R.id.direction)
    TextView direction;
    @BindView(R.id.speed_detail)
    TextView speedDetail;
    @BindView(R.id.humidity_detail)
    TextView humidityDetail;
    @BindView(R.id.pressure)
    TextView pressure;
    @BindView(R.id.rising)
    TextView rising;
    @BindView(R.id.visibility)
    TextView visibility;
    @BindView(R.id.latitude)
    TextView latitude;
    @BindView(R.id.longitude)
    TextView longitude;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.my_weather_icon)
    WeatherIconView weatherIconView;

    private String latitudeStr,longitudeStr,addressStr;
    private int icon;

    private FlipProgressDialog flipProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);

        flipProgressDialog=Utility.myDialog();

        flipProgressDialog.show(getFragmentManager(),"");

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
                final Weather weather = Utility.handleWeatherResponse(responseData);
                Channel channel = weather.getQuery().getResults().getChannel();
                String location = channel.getLocation().getCity()+","+channel.getLocation().getRegion()+","+channel.getLocation().getCountry();
                Log.d("WA2",location);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null) {
                            showWeatherInfo(weather);
                        } else {
                            Log.d("WA3","failed to get weather information");
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        Channel channel = weather.getQuery().getResults().getChannel();
        String locationStr = channel.getLocation().getCity()+","+channel.getLocation().getRegion()+","+channel.getLocation().getCountry();
        String statusStr = channel.getItem().getCondition().getText();
        String degreeStr = channel.getItem().getCondition().getTemp();
        String humidityStr = channel.getAtmosphere().getHumidity();
        String speedStr = channel.getWind().getSpeed();
        String sunriseStr = channel.getAstronomy().getSunrise();
        String sunsetStr = channel.getAstronomy().getSunset();
        String chillStr = channel.getWind().getChill();
        String directionStr = channel.getWind().getDirection();
        String pressureStr = channel.getAtmosphere().getPressure();
        String risingStr = channel.getAtmosphere().getRising();
        String visibilityStr = channel.getAtmosphere().getVisibility();
        String code = channel.getItem().getCondition().getCode();

//        location.setText(locationStr);
        setTitle(locationStr);
        status.setText(statusStr);
        degree.setText(degreeStr+" C");
        humidity.setText(humidityStr+" %");
        speed.setText(speedStr+" km/h");
        sunrise.setText(sunriseStr);
        sunset.setText(sunsetStr);
        forecastLayout.removeAllViews();
        chill.setText(chillStr);
        direction.setText(directionStr);
        speedDetail.setText(speedStr+" km/h");
        humidityDetail.setText(humidityStr+" %");
        pressure.setText(pressureStr+" mb");
        rising.setText(risingStr);
        visibility.setText(visibilityStr+" km");
        latitude.setText(latitudeStr);
        longitude.setText(longitudeStr);
        address.setText(addressStr);
        setWeatherIcon(code);

        weatherIconView.setIconResource(getString(icon));
        weatherIconView.setIconSize(80);
        weatherIconView.setIconColor(Color.GRAY);

        for (Forecast forecast : channel.getItem().getForecastList()) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView date = view.findViewById(R.id.date);
            TextView day = view.findViewById(R.id.day);
            TextView statusForecast = view.findViewById(R.id.status_forecast);
            TextView high = view.findViewById(R.id.high);
            TextView low = view.findViewById(R.id.low);
            String dateStr = forecast.getDate();
            String dayStr = forecast.getDay();
            String statusForecastStr = forecast.getText();
            String highStr = forecast.getHigh();
            String lowStr = forecast.getLow();
            date.setText(dateStr);
            day.setText(dayStr);
            statusForecast.setText(statusForecastStr);
            high.setText(highStr);
            low.setText(lowStr);
            forecastLayout.addView(view);
        }

        flipProgressDialog.dismiss();
    }

    private void setWeatherIcon(String code) {
        switch(code) {
            case "0":
                icon = R.string.wi_tornado;
                break;
            case "1":
                icon = R.string.wi_storm_showers;
                break;
            case "2":
                icon = R.string.wi_tornado;
                break;
            case "3":
                icon = R.string.wi_thunderstorm;
                break;
            case "4":
                icon = R.string.wi_thunderstorm;
                break;
            case "5":
                icon = R.string.wi_snow;
                break;
            case "6":
                icon = R.string.wi_rain_mix;
                break;
            case "7":
                icon = R.string.wi_rain_mix;
                break;
            case "8":
                icon = R.string.wi_sprinkle;
                break;
            case "9":
                icon = R.string.wi_sprinkle;
                break;
            case "10":
                icon = R.string.wi_hail;
                break;
            case "11":
                icon = R.string.wi_showers;
                break;
            case "12":
                icon = R.string.wi_showers;
                break;
            case "13":
                icon = R.string.wi_snow;
                break;
            case "14":
                icon = R.string.wi_storm_showers;
                break;
            case "15":
                icon = R.string.wi_snow;
                break;
            case "16":
                icon = R.string.wi_snow;
                break;
            case "17":
                icon = R.string.wi_hail;
                break;
            case "18":
                icon = R.string.wi_hail;
                break;
            case "19":
                icon = R.string.wi_cloudy_gusts;
                break;
            case "20":
                icon = R.string.wi_fog;
                break;
            case "21":
                icon = R.string.wi_fog;
                break;
            case "22":
                icon = R.string.wi_fog;
                break;
            case "23":
                icon = R.string.wi_cloudy_gusts;
                break;
            case "24":
                icon = R.string.wi_cloudy_windy;
                break;
            case "25":
                icon = R.string.wi_thermometer;
                break;
            case "26":
                icon = R.string.wi_cloudy;
                break;
            case "27":
                icon = R.string.wi_night_cloudy;
                break;
            case "28":
                icon = R.string.wi_day_cloudy;
                break;
            case "29":
                icon = R.string.wi_night_cloudy;
                break;
            case "30":
                icon = R.string.wi_day_cloudy;
                break;
            case "31":
                icon = R.string.wi_night_clear;
                break;
            case "32":
                icon = R.string.wi_day_sunny;
                break;
            case "33":
                icon = R.string.wi_night_clear;
                break;
            case "34":
                icon = R.string.wi_day_sunny_overcast;
                break;
            case "35":
                icon = R.string.wi_hail;
                break;
            case "36":
                icon = R.string.wi_day_sunny;
                break;
            case "37":
                icon = R.string.wi_thunderstorm;
                break;
            case "38":
                icon = R.string.wi_thunderstorm;
                break;
            case "39":
                icon = R.string.wi_thunderstorm;
                break;
            case "40":
                icon = R.string.wi_storm_showers;
                break;
            case "41":
                icon = R.string.wi_snow;
                break;
            case "42":
                icon = R.string.wi_snow;
                break;
            case "43":
                icon = R.string.wi_snow;
                break;
            case "44":
                icon = R.string.wi_cloudy;
                break;
            case "45":
                icon = R.string.wi_lightning;
                break;
            case "46":
                icon = R.string.wi_snow;
                break;
            case "47":
                icon = R.string.wi_thunderstorm;
                break;
            case "3200":
                icon = R.string.wi_cloud;
                break;
            default:
                icon = R.string.wi_cloud;
                break;


        }

    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
        if (TencentLocation.ERROR_OK == error) {
            // 定位成功
            Log.d("WA1",tencentLocation.getAddress());
            latitudeStr = String.valueOf(tencentLocation.getLatitude());
            longitudeStr = String.valueOf(tencentLocation.getLongitude());
            addressStr = tencentLocation.getAddress();
            requestWeather(latitudeStr,longitudeStr);
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
