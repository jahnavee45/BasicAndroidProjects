package com.example.jobscheduling;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ListView listView;
    ArrayList<String> arr;
    ArrayAdapter<String> adapter;
    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scheduleJob(View v){

        ComponentName cname = new ComponentName(this, OfflineJobScheduling.class);
        JobInfo info = new JobInfo.Builder(123, cname).build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(info);

        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.list);
        arr = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        listView.setAdapter(adapter);

        if(result == JobScheduler.RESULT_SUCCESS){
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                int lineNUmber = 0;
                String[] lines;

                @Override
                public void run() {
                    if(scheduler.getAllPendingJobs().contains(info)){
                        if(lines == null){
                            lines = readDataFromDocument();
                        }
                        if(lineNUmber < lines.length){
                            textView.setText(lines[lineNUmber]);
                            arr.add(lines[lineNUmber]);
                            adapter.notifyDataSetChanged();
                            lineNUmber++;
                            handler.postDelayed(this, 1000);
                        }
                    } else{
                        handler.removeCallbacks(this);
                    }
                }
            };
            handler.post(runnable);
            Log.d("MyJobService", "Job Scheduled, result: " + result);
            Toast.makeText(this, "Job is scheduled", Toast.LENGTH_SHORT).show();
        }else{
            Log.d("MyJobService", "Job scheduling failed");
        }
    }

    public void cancelJob(View v){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Toast.makeText(this, "Job is cancelled", Toast.LENGTH_SHORT).show();
        Log.d("MyJobService", "Job cancelled");
    }

    private String[] readDataFromDocument() {
        assetManager = getAssets();

        List<String> lines = new ArrayList<>();
        try{
            InputStream inputStream = assetManager.open("document.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null){
                lines.add(line);
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return lines.toArray(new String[0]);
    }
}