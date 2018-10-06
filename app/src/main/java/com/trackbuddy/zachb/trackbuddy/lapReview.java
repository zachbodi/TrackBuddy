package com.trackbuddy.zachb.trackbuddy;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class lapReview extends ListActivity {

    Button returnButton;

    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int Seconds, Minutes, CentiSeconds;
    long timeDifference;
    String prefix;
    TextView dateText, distanceText, lapText, goalLapText;

    byte[] historyBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lap_review);

        Intent finishIntent = getIntent();

        long goalLap = finishIntent.getLongExtra(MainActivity.GOAL_LAP, 0);
        long eventDistance = finishIntent.getLongExtra("EVENT_DISTANCE", 0);
        long lapDistance = finishIntent.getLongExtra("LAP_DISTANCE", 0);
        long[] justLapsArray = finishIntent.getLongArrayExtra(MainActivity.LAP_LIST);

        final long[] lapsArray = new long[justLapsArray.length + 1];
        lapsArray[0] = goalLap;
        System.arraycopy(justLapsArray, 0, lapsArray, 1, justLapsArray.length);

        returnButton = (Button)findViewById(R.id.returnButton);
        dateText = (TextView)findViewById(R.id.dateText);
        distanceText = (TextView)findViewById(R.id.distanceText);
        lapText = (TextView)findViewById(R.id.lapText);
        goalLapText = (TextView)findViewById(R.id.goalLapText);

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        final String[] infoArray = new String[4];
        String date = getDate();
        String eventDistanceInfo = "Distance: " + eventDistance + " Meters";
        String lapDistanceInfo = "Lap: " + lapDistance + " Meters";
        String goalLapInfo = "Goal Lap: " + millisToString(goalLap);
        infoArray[0] = date;
        infoArray[1] = eventDistanceInfo;
        infoArray[2] = lapDistanceInfo;
        infoArray[3] = goalLapInfo;

        distanceText.setText(eventDistanceInfo);
        lapText.setText(lapDistanceInfo);
        goalLapText.setText(goalLapInfo);
        dateText.setText(date);

        for (int i = 1; i < lapsArray.length; i++) {

            timeDifference = lapsArray[i] - goalLap;
            prefix = "+";

            if(timeDifference < 0) {
                timeDifference = Math.abs(timeDifference);
                prefix = "-";
            }

            listItems.add("Lap " + (i) + ": " + millisToString(lapsArray[i]) + "    (" + prefix +
                    millisToString(timeDifference) + ")");

        }
        adapter.notifyDataSetChanged();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                Gson gson = new Gson();

                ArrayList<long[]> emptyLapHist = new ArrayList<>();
                String json_default = gson.toJson(emptyLapHist);

                Type type = new TypeToken<ArrayList<long[]>>() {}.getType();
                String currentListJson = preferences.getString("WORKOUT_HIST", json_default);
                ArrayList<long[]> currentListNorm = gson.fromJson(currentListJson, type);

                currentListNorm.add(lapsArray);
                String newListJson = gson.toJson(currentListNorm);
                editor.putString("WORKOUT_HIST", newListJson);

                ////////////

                ArrayList<String[]> emptyInfoHist = new ArrayList<>();
                String json_info_default = gson.toJson(emptyInfoHist);

                Type infoType = new TypeToken<ArrayList<String[]>>() {}.getType();
                String currentInfoListJson = preferences.getString("WORKOUT_DATES", json_info_default);
                ArrayList<String[]> currentInfoListNorm = gson.fromJson(currentInfoListJson, infoType);

                currentInfoListNorm.add(infoArray);
                String newInfoListJson = gson.toJson(currentInfoListNorm);
                editor.putString("WORKOUT_DATES", newInfoListJson);
                editor.apply();

                finish();
            }
        });

    }

    private String getDate() {
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(d);

        SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
        String formattedTime = tf.format(d);

        return formattedDate + " (" + formattedTime +")";
    }

    String millisToString(long x) {
        Seconds = (int) (x / 1000);
        Minutes = Seconds / 60;
        Seconds = Seconds % 60;
        CentiSeconds = (int) ((x % 1000) / 10);

        return (String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds) + "." + String.format("%02d", CentiSeconds));
    }

}
