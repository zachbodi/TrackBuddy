package com.trackbuddy.zachb.trackbuddy;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class histWorkoutReview extends ListActivity {

    Button returnButton;

    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int Seconds, Minutes, CentiSeconds, workoutIndexToCall;
    long timeDifference;
    String prefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_workout_review);

        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        Gson gson = new Gson();

        Type type = new TypeToken<ArrayList<long[]>>() {}.getType();
        String currentListJson = preferences.getString("WORKOUT_HIST", "NULL");
        final ArrayList<long[]> currentListNorm = gson.fromJson(currentListJson, type);

        Type infoType = new TypeToken<ArrayList<String[]>>() {}.getType();
        String currentInfoListJson = preferences.getString("WORKOUT_DATES", "NULL");
        final ArrayList<String[]> currentInfoListNorm = gson.fromJson(currentInfoListJson, infoType);

        returnButton = (Button)findViewById(R.id.returnButton);

        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        //listItems.add("Goal Lap: " + millisToString(goalLap));

        for (int i = currentListNorm.size() - 1; i >= 0; i--) {
            listItems.add("Workout " + String.valueOf(i+1) + ": " + currentInfoListNorm.get(i)[0]);
        }
        adapter.notifyDataSetChanged();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ListView lv = getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                workoutIndexToCall = currentInfoListNorm.size() - 1 - position;
                Intent workoutIntent = new Intent(adapter.getContext(), histLapReview.class);
                workoutIntent.putExtra("WORKOUT", workoutIndexToCall);
                startActivityForResult(workoutIntent, 1);
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
