package com.zhy.jetpack.workmanager.works;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class Worker3 extends Worker {

    public static final String TAG = Worker3.class.getSimpleName();

    public Worker3(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, " doWork3...,后台任何开始");
        SystemClock.sleep(1000);
        Log.d(TAG, " doWork3...,执行完成");
        return Result.success();
    }
}
