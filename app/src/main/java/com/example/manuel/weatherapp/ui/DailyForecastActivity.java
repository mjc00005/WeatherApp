package com.example.manuel.weatherapp.ui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manuel.weatherapp.R;
import com.example.manuel.weatherapp.adapters.DayAdapter;
import com.example.manuel.weatherapp.modelo.Day;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DailyForecastActivity extends Activity {

    public Day[] mDays;

    @Bind(android.R.id.list) ListView mListView;
    @Bind(android.R.id.empty) TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);
        //String[] daysOfTheWeek = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);

        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);




        DayAdapter adapter = new DayAdapter(DailyForecastActivity.this, mDays);
        mListView.setAdapter(adapter);
        //setListAdapter(adapter);
        mListView.setEmptyView(mEmptyTextView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dayOfTheWWeek = mDays[position].getDayOfTheWeek();
                String conditions = mDays[position].getSummary();
                String highTemp = mDays[position].getTemperatureMax()+"";
                String message = String.format("On %s them high will be %s and it will be %s.",
                        dayOfTheWWeek,
                        highTemp,
                        conditions);
                Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });


    }

    //If extends from ListActivity the main Class
//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        String dayOfTheWWeek = mDays[position].getDayOfTheWeek();
//        String conditions = mDays[position].getSummary();
//        String highTemp = mDays[position].getTemperatureMax()+"";
//        String message = String.format("On %s them high will be %s and it will be %s.",
//                dayOfTheWWeek,
//                highTemp,
//                conditions);
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//
//    }
}
