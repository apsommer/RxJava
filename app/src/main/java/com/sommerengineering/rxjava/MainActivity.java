package com.sommerengineering.rxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName() + " ~~ ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create observable (listener)
        Observable<Task> observable = Observable
                .fromIterable(DataSource.createTaskList())
                .subscribeOn(Schedulers.io()) // do work on background thread managed by default Scheduler
                .observeOn(AndroidSchedulers.mainThread()); // observe result on main UI thread (callback)

        // subscribe to observable emissions
        observable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.d(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext");
                Log.d(TAG, task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: observable finished emitting");
            }
        });
        
    }
}