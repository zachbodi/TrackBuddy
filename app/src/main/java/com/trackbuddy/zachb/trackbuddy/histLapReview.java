package com.trackbuddy.zachb.trackbuddy;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class histLapReview extends ListActivity {

    Button returnButton;

    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int Seconds, Minutes, CentiSeconds;
    long timeDifference;
    String prefix;
    TextView dateText, distanceText, lapText, goalLapText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_lap_review);

        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<long[]>>() {}.getType();
        String currentListJson = preferences.getString("WORKOUT_HIST", "NULL");
        ArrayList<long[]> currentListNorm = gson.fromJson(currentListJson, type);

        Type infoType = new TypeToken<ArrayList<String[]>>() {}.getType();
        String currentInfoListJson = preferences.getString("WORKOUT_DATES", "NULL");
        ArrayList<String[]> currentInfoListNorm = gson.fromJson(currentInfoListJson, infoType);

        Intent workoutIntent = getIntent();
        int workoutIndexToCall = workoutIntent.getIntExtra("WORKOUT", 0);
        final long [] lapsArray = currentListNorm.get(workoutIndexToCall);
        final String [] infoArray = currentInfoListNorm.get(workoutIndexToCall);

        String date = infoArray[0];
        String eventDistanceInfo = infoArray[1];
        String lapDistanceInfo = infoArray[2];
        String goalLapInfo = infoArray[3];

        returnButton = (Button)findViewById(R.id.returnButton);
        dateText = (TextView)findViewById(R.id.dateText);
        distanceText = (TextView)findViewById(R.id.distanceText);
        lapText = (TextView)findViewById(R.id.lapText);
        goalLapText = (TextView)findViewById(R.id.goalLapText);

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        distanceText.setText(eventDistanceInfo);
        lapText.setText(lapDistanceInfo);
        goalLapText.setText(goalLapInfo);
        dateText.setText(date);

        long goalLap = lapsArray[0];
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
                finish();
            }
        });

    }

    String millisToString(long x) {
        Seconds = (int) (x / 1000);
        Minutes = Seconds / 60;
        Seconds = Seconds % 60;
        CentiSeconds = (int) ((x % 1000) / 10);

        return (String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds) + "." + String.format("%02d", CentiSeconds));
    }

}
