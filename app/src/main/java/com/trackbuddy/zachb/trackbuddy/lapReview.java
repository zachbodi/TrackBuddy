package com.trackbuddy.zachb.trackbuddy;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class lapReview extends ListActivity {

    Button returnButton;

    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int Seconds, Minutes, CentiSeconds;
    long timeDifference;
    String prefix;

    byte[] historyBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lap_review);

        Intent finishIntent = getIntent();

        final long [] lapsArray = finishIntent.getLongArrayExtra(MainActivity.LAP_LIST);
        long goalLap = finishIntent.getLongExtra(MainActivity.GOAL_LAP, 100000);

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
                editor.apply();

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
