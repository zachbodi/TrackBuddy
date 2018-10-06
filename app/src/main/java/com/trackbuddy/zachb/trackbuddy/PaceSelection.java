package com.trackbuddy.zachb.trackbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PaceSelection extends AppCompatActivity {

    public static final String PACE_VALUES = "com.example.zachb.trackbuddy.extra.PACE_VALUES";

    Button beginButton;
    EditText distanceInput, lapDistanceInput, minInput, secInput;
    long distance, lapDistance, minutes, seconds, milliseconds;
    long[] paceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pace_selection);

        beginButton = (Button)findViewById(R.id.beginButton);

        Button reviewButton = (Button)findViewById(R.id.reviewButton);

        distanceInput = (EditText)findViewById(R.id.distanceInput);
        lapDistanceInput = (EditText)findViewById(R.id.lapDistanceInput);
        minInput = (EditText)findViewById(R.id.minInput);
        secInput = (EditText)findViewById(R.id.secInput);

        beginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(lapDistanceInput.getText()) || TextUtils.isEmpty(distanceInput.getText()) || (TextUtils.isEmpty(minInput.getText()) && TextUtils.isEmpty(secInput.getText()))) {

                }
                else{
                    if(TextUtils.isEmpty(minInput.getText())) {
                        minInput.setText("00");
                    }
                    if(TextUtils.isEmpty(secInput.getText())) {
                        secInput.setText("00");
                    }

                    distance = Long.parseLong(distanceInput.getText().toString());
                    lapDistance = Long.parseLong(lapDistanceInput.getText().toString());
                    minutes = Long.parseLong(minInput.getText().toString());
                    seconds = Long.parseLong(secInput.getText().toString());

                    milliseconds = calculateMilliseconds(minutes, seconds);

                    paceInfo = new long[] {distance,lapDistance,milliseconds};
                    launchTimer();
                }
            }
        });

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
                String currentListJson = preferences.getString("WORKOUT_HIST", "NULL");

                if(currentListJson != "NULL") {
                    Intent testIntent = new Intent(view.getContext(), histWorkoutReview.class);
                    startActivityForResult(testIntent, 1);
                }
                else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaceSelection.this);
                    alertDialogBuilder.setTitle("No Workouts");
                    alertDialogBuilder.setMessage("You have to work out first!");
                    alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

    }

    long calculateMilliseconds(long minutes, long seconds) {
        return (minutes*60000)+(seconds*1000);
    }

    public void launchTimer() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PACE_VALUES, paceInfo);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaceSelection.this);
                alertDialogBuilder.setTitle("Invalid Values");
                alertDialogBuilder.setMessage("Please enter valid values");
                alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                distanceInput.getText().clear();
                secInput.getText().clear();
                minInput.getText().clear();
                lapDistanceInput.getText().clear();
            }
        }
    }
}
