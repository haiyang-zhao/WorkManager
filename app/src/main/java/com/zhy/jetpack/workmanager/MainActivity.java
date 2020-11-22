package com.zhy.jetpack.workmanager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import com.zhy.jetpack.workmanager.works.Worker1;
import com.zhy.jetpack.workmanager.works.Worker2;
import com.zhy.jetpack.workmanager.works.Worker3;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //单个任务
    public void testBackgroundWork1(View view) {
        Data sendData = new Data.Builder().putString("fromParam", "九阳神功").build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(Worker1.class)
                .setInputData(sendData)
                .build();

        WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(request.getId())
                .observe(this, workInfo -> {
                    String param = workInfo.getOutputData().getString("toParam");
                    Log.d(Worker1.TAG, "收到回传数据 : " + param);
                    Log.d(Worker1.TAG, "状态 : " + workInfo.getState().name());
                });

        WorkManager.getInstance(this).enqueue(request);
    }

    //多个任务 顺序执行
    public void testBackgroundWork2(View view) {
        OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(Worker1.class).build();
        OneTimeWorkRequest request2 = new OneTimeWorkRequest.Builder(Worker2.class).build();
        OneTimeWorkRequest request3 = new OneTimeWorkRequest.Builder(Worker3.class).build();


        //顺序执行 request1->request2->request3
        WorkManager.getInstance(this)
                .beginWith(request1).then(request2).then(request3)
                .enqueue();

        //WorkContinuation has cycles
//        WorkContinuation continuation =
//                WorkManager.getInstance(this).beginWith(request1).then(request2).then(request1);
//
//
//        continuation.enqueue().getState().observe(this, state -> {
//            Log.d(Worker1.TAG, state.toString());
//        });
    }

    //多个任务 并发执行
    public void testBackgroundWork3(View view) {
        OneTimeWorkRequest request1 = new OneTimeWorkRequest.Builder(Worker1.class).build();
        OneTimeWorkRequest request2 = new OneTimeWorkRequest.Builder(Worker2.class).build();
        OneTimeWorkRequest request3 = new OneTimeWorkRequest.Builder(Worker3.class).build();


        //并发执行 request2,request3 ->request1
        WorkManager.getInstance(this)
                .beginWith(Arrays.asList(request2, request3)).then(request1)
                .enqueue();
    }

    //重复任务
    public void testBackgroundWork4(View view) {
        //最小间隔是15分钟
        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(Worker1.class, 15, TimeUnit.MINUTES).build();

        WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(request.getId())
                .observe(this, workInfo ->
                        Log.d(Worker1.TAG, workInfo.getState().name()));
        WorkManager.getInstance(this).enqueue(request);
    }

    //约束条件
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("IdleBatteryChargingConstraints")
    public void testBackgroundWork5(View view) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // 网络链接中...
                .setRequiresCharging(true) // 充电中..
                .setRequiresDeviceIdle(true) // 空闲时.. (没有玩游戏)
                .build();
        /**
         * 除了上面设置的约束外，WorkManger还提供了以下的约束作为Work执行的条件：
         *  setRequiredNetworkType：网络连接设置
         *  setRequiresBatteryNotLow：是否为低电量时运行 默认false
         *  setRequiresCharging：是否要插入设备（接入电源），默认false
         *  setRequiresDeviceIdle：设备是否为空闲，默认false
         *  setRequiresStorageNotLow：设备可用存储是否不低于临界阈值
         */

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(Worker1.class)
                .setConstraints(constraints).build();
        WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(request.getId())
                .observe(this, workInfo ->
                        Log.d(Worker1.TAG, workInfo.getState().name()));
        WorkManager.getInstance(this).enqueue(request);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void testBackgroundWork6(View view) {

        // 约束条件，必须满足我的条件，才能执行后台任务 （在连接网络，插入电源 并且 处于空闲时）  内部做了电量优化（Android App 不耗电）
        @SuppressLint("IdleBatteryChargingConstraints") Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // 网络链接中...
                .setRequiresCharging(true) // 充电中..
                .setRequiresDeviceIdle(true) // 空闲时.. (没有玩游戏)
                .build();

        // 请求对象
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(Worker1.class)
                .setConstraints(myConstraints) // TODO 3 约束条件的执行
                .build();


        WorkManager.getInstance(this) // TODO 1 初始化工作源码
                .enqueue(request); // TODO 2 加入队列执行
    }


}