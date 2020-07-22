package com.sommerengineering.rxjava;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

public class Repository {

    // singleton
    private static Repository instance;
    public static Repository getInstance() {

        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    // execute the endpoint query and return future observable to caller
    public Future<Observable<ResponseBody>> makeFutureQuery() {

        // A future is essentially a pending task. It's a promise for a result from a task when it
        // runs sometime in the future. The task can be a executed via a Runnable or a Callable
        // (not to be confused with an Rx Callable). An example of something that can execute these
        // runnables or callables is an ExecutorService.

        // executor runs on single background thread
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        // create an observable from a java callable
        final Callable<Observable<ResponseBody>> callable = new Callable<Observable<ResponseBody>>() {

            @Override
            public Observable<ResponseBody> call() throws Exception {
                return ServiceGenerator.getRequestApi().makeObservableQuery();
            }
        };

        // create the future observable, this does not actually make the call only sets it up for expected future call
        return new Future<Observable<ResponseBody>> () {

            // following methods clean up the executor as needed

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {

                if (mayInterruptIfRunning) {
                    executor.shutdown();
                }

                return false;
            }

            @Override
            public boolean isCancelled() {
                return executor.isShutdown();
            }

            @Override
            public boolean isDone() {
                return executor.isTerminated();
            }

            // next two methods are the key: when this future observable is observed (in viewmodel
            // when subscribe is called) only then the executor will execute, via one of these
            // methods, which points to the callable observable created above, which makes the
            // endpoint call.

            @Override
            public Observable<ResponseBody> get() throws ExecutionException, InterruptedException {
                return executor.submit(callable).get();
            }

            @Override
            public Observable<ResponseBody> get(long timeout, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
                return executor.submit(callable).get(timeout, timeUnit);
            }
        };
    }
}
