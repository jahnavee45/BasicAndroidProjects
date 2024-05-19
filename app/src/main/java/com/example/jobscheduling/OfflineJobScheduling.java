package com.example.jobscheduling;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class OfflineJobScheduling extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("MyJobService", "Job Started");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("MyJobService", "Job Stopped");
        return false;
    }
}
