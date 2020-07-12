package com.sommerengineering.rxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName() + " ~~ ";

    // all observers are disposal after they are no longer useful
    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // subscribeOn: put this observable on this (typically background) thread, doing all operations here
        // observeOn: observe this observable's emissions on this (typically main) thread

        // create observable (listener)
        Observable<Task> observable = Observable
                .fromIterable(DataSource.createTaskList())
                .subscribeOn(Schedulers.io()) // get background thread from pool managed by Scheduler
                .filter(new Predicate<Task>() { // operators execute on the subscribeOn background thread

                    // filter
                    @Override
                    public boolean test(Task task) throws Throwable {

                        // demonstrate this method is on the specified background thread
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "filter test: " + Thread.currentThread().getName());
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()); // observe result on main UI thread (callback)

        // subscribe here is different than subscribeOn, this associates the observer (can be annonymous)
        // to the observable

        // subscribe to observable emissions
        observable.subscribe(new Observer<Task>() {

            // all methods execute on the main UI thread

            @Override
            public void onSubscribe(@NonNull Disposable d) {

                Log.d(TAG, "onSubscribe");

                disposables.add(d);

                Log.d(TAG, "disposable added");

                // all observers are manually deleted in onDestroy()

            }

            @Override
            public void onNext(@NonNull Task task) {

                // demonstrate this method is on the UI thread
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                Log.d(TAG, "onNext: " + Thread.currentThread().getName());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // remove all observers (make them eligible for GC)
        // this would be called in onCleared() of viewmodel
        disposables.clear(); // .dispose() disables all future observations of these observables (hard clear)
    }
}