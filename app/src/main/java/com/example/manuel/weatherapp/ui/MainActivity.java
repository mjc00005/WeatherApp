package com.example.manuel.weatherapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manuel.weatherapp.R;
import com.example.manuel.weatherapp.modelo.Current;
import com.example.manuel.weatherapp.modelo.Day;
import com.example.manuel.weatherapp.modelo.Forecast;
import com.example.manuel.weatherapp.modelo.Hour;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";

    private Forecast mForecast;

    //Instantiate the views
    @Bind(R.id.timeLabel) TextView mTimeLabel;
    @Bind(R.id.temperatureLabel) TextView mTemperatureLabel;
    @Bind(R.id.humidityValue) TextView mHuminityLabel;
    @Bind(R.id.precipValue) TextView mPrecipLabel;
    @Bind(R.id.summaryLabel) TextView mSummaryLabel;
    @Bind(R.id.iconImaView) ImageView mIconImaView;
    @Bind(R.id.locationLabel) TextView mLocationLabel;
    @Bind(R.id.imageViewRefresh) ImageView mIconRefresh;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Set the London Location
        final double latitud = 51.468822;
        final double longitud = 0.005464;

        mProgressBar.setVisibility(View.INVISIBLE);

        //Press Update Botom
        mIconRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getForecast(latitud, longitud);
            }
        });

        getForecast(latitud, longitud);

        Log.e(TAG, "Main UI is running!!!!! ");

    }

    private void getForecast(double latitud, double longitud) {
        String apiKey = "5045fe5a16b75c63354a4e938fb6f2d2";

        String foreCastURL = "https://api.forecast.io/forecast/" + apiKey + "/" + latitud + "," + longitud;

        //Check if the Network is available
        if (isNetworkAvailable()) {
            //Change the visibility from progress bar
            toggleRefresh();

            //Request the JSON
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(foreCastURL)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        //Response response = call.execute();
                        String jsonData = response.body().string();
                        Log.e(TAG, response.body().string());
                        if (response.isSuccessful()) {

                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });


                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Doesn't work the network", Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {

        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mIconRefresh.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mIconRefresh.setVisibility(View.VISIBLE);
        }


    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();

        mTemperatureLabel.setText(current.getTemparature() + "");
        mTimeLabel.setText("At " + current.getFormattedDate() + " it will be");
        mHuminityLabel.setText(current.getHumidity() + "");
        mPrecipLabel.setText(current.getPrecipiChance() + "%");
        mSummaryLabel.setText(current.getSummary());
        mLocationLabel.setText(current.getTimeZone());

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImaView.setImageDrawable(drawable);

    }

    private Forecast parseForecastDetails(String jsonData)throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData)throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for(int i=0; i<data.length(); i++){
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimeZone(timeZone);

            days[i]= day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData)throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");

        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for(int i=0; i<data.length(); i++){
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimeZone(timeZone);

            hours[i]=(hour);
        }

        return hours;

    }

    private Current getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG, " From JSON: " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current cWeather = new Current();
        cWeather.setHumidity(currently.getDouble("humidity"));
        cWeather.setTime(currently.getLong("time"));
        cWeather.setIcon(currently.getString("icon"));
        cWeather.setPrecipiChance(currently.getDouble("precipProbability"));
        cWeather.setSummary(currently.getString("summary"));
        cWeather.setTemparature(currently.getDouble("temperature"));
        cWeather.setTimeZone(timeZone);

        Log.i(TAG, " From JSON: " + timeZone);
        Log.i(TAG, " From JSON: ICON  " + cWeather.getIcon());
        Log.i(TAG, " From JSON: SUMMARY  " + cWeather.getSummary());
        Log.i(TAG, " From JSON: PRECIPI  " + cWeather.getPrecipiChance());
        Log.i(TAG, " From JSON: HUMIDY  " + cWeather.getHumidity());

        Log.d(TAG, cWeather.getFormattedDate());

        return cWeather;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;

    }

    private void alertUserAboutError() {

        android.app.DialogFragment dialoga = new AlertDialogFragment();
        dialoga.show(getFragmentManager(), "error_dialog");

    }

    @OnClick (R.id.hourlyBtn)
    public void startHourlyActivity(View view){
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
        startActivity(intent);
    }

    @OnClick (R.id.dailyBtn)
    public void startDailyActivity(View view){

        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
        startActivity(intent);
    }


}
