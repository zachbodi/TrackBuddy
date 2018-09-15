package com.example.zachb.trackbuddy;

import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Console;

public class MainActivity extends AppCompatActivity {

    long startTime, currentTime, timeRunning, timeRan, thisLapTime, lastLapEnd, pace, paceAbs, timeToConfirm = 0;
    int Seconds, Minutes, CentiSeconds;
    boolean running, confirmEnd = false;
    String pacePrefix;

    long goalTimeMillis = 40000;
    long totalDistance = 400;
    long lapDistance = 100;
    long numLaps = totalDistance/lapDistance;
    long goalLapMillis = goalTimeMillis/numLaps;

    TextView timeDisplay, goalLapDisplay, paceDisplay, lastLapDisplay, goalTimeDisplay, thisLapDisplay;
    Button startButton, resetButton, endSessionButton;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeDisplay = (TextView)findViewById(R.id.timeDisplay);
        goalLapDisplay = (TextView)findViewById(R.id.goalLapDisplay);
        goalTimeDisplay = (TextView)findViewById(R.id.goalTimeDisplay);
        paceDisplay = (TextView)findViewById(R.id.paceDisplay);
        lastLapDisplay = (TextView)findViewById(R.id.lastLapDisplay);
        thisLapDisplay = (TextView)findViewById(R.id.thisLapDisplay);

        startButton = (Button)findViewById(R.id.startButton);
        resetButton = (Button)findViewById(R.id.resetButton);
        endSessionButton = (Button)findViewById(R.id.endSessionButton);

        goalLapDisplay.setText("Goal Lap: " + millisToString(goalLapMillis));
        goalTimeDisplay.setText("Goal Time: " + millisToString(goalTimeMillis));

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!running) {
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    running = true;
                    resetButton.setText("Lap");
                    startButton.setText("Pause");
                }
                else {
                    timeRan += timeRunning;
                    handler.removeCallbacks(runnable);
                    running = false;
                    resetButton.setText("Reset");
                    startButton.setText("Resume");
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!running) {
                    handler.removeCallbacks(runnable);
                    timeRan = 0;
                    timeRunning = 0;
                    lastLapEnd = 0;
                    pace = 0;
                    startButton.setText("Start");
                    timeDisplay.setText("00:00.00");
                    paceDisplay.setText("00:00.00");
                    lastLapDisplay.setText("00:00.00");
                    thisLapDisplay.setText("00:00.00");
                    paceDisplay.setTextColor(Color.parseColor("#000000"));
                }
                else {
                    thisLapTime = currentTime - lastLapEnd;
                    lastLapEnd = currentTime;
                    lastLapDisplay.setText(millisToString(thisLapTime));

                    paceDisplay.setText(checkPace());
                }
            }
        });

        endSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    public Runnable runnable = new Runnable() {
        public void run() {

            timeRunning = SystemClock.uptimeMillis() - startTime;

            currentTime = timeRunning + timeRan;
            thisLapTime = currentTime - lastLapEnd;

            timeDisplay.setText(millisToString(currentTime));
            thisLapDisplay.setText(millisToString(thisLapTime));

            handler.postDelayed(this,0);
        }
    };

    String millisToString(long x) {
        Seconds = (int) (x / 1000);
        Minutes = Seconds / 60;
        Seconds = Seconds % 60;
        CentiSeconds = (int) ((x % 1000) / 10);

        return (String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds) + "." + String.format("%02d", CentiSeconds));
    }

    String checkPace() {
        pace += goalLapMillis - thisLapTime;

        if(pace >= 0) {
            pacePrefix = "-";
            paceDisplay.setTextColor(Color.parseColor("#006400"));
        }
        else {
            pacePrefix = "+";
            paceDisplay.setTextColor(Color.parseColor("#8b0000"));
        }

        paceAbs = Math.abs(pace);

        Seconds = (int) (paceAbs / 1000);
        Minutes = Seconds / 60;
        Seconds = Seconds % 60;
        CentiSeconds = (int) ((paceAbs % 1000) / 10);

        return (pacePrefix + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds) + "." + String.format("%02d", CentiSeconds));
    }
}//github test
