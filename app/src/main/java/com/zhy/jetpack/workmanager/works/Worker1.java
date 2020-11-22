package com.zhy.jetpack.workmanager.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class Worker1 extends Worker {

    public static final String TAG = Worker1.class.getSimpleName();
    private Context context;
    private WorkerParameters workerParams;

    public Worker1(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.workerParams = workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork1...,后台任何开始");

        //接受参数
        String param = this.workerParams.getInputData().getString("fromParam");
        Log.d(TAG, param == null ? "" : param);

        //回传数据
        Data outputData = new Data.Builder().putString("toParam", "三分归元气").build();
        return Result.success(outputData);
    }


}
