package com.trackbuddy.zachb.trackbuddy;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lap_review);

        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<long[]>>() {}.getType();
        String currentListJson = preferences.getString("WORKOUT_HIST", "NULL");
        ArrayList<long[]> currentListNorm = gson.fromJson(currentListJson, type);

        Intent workoutIntent = getIntent();
        int workoutIndexToCall = workoutIntent.getIntExtra("WORKOUT", 0);
        final long [] lapsArray = currentListNorm.get(workoutIndexToCall);
        long goalLap = 0;

        returnButton = (Button)findViewById(R.id.returnButton);

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        //listItems.add("Goal Lap: " + millisToString(goalLap));

        for (int i = 0; i < lapsArray.length; i++) {

            timeDifference = lapsArray[i] - goalLap;
            prefix = "+";

            if(timeDifference < 0) {
                timeDifference = Math.abs(timeDifference);
                prefix = "-";
            }

            listItems.add("Lap " + (i + 1) + ": " + millisToString(lapsArray[i]) + "    (" + prefix +
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
