package com.sommerengineering.rxjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName() + " ~~ ";

    // all observers are disposal after they are no longer useful
    CompositeDisposable disposables = new CompositeDisposable();

    // emit a single object
    private void createObservable() {

        final Task task = new Task("Walk the dog", false, 3);

        Observable<Task> taskObservable = Observable.create(new ObservableOnSubscribe<Task>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Task> emitter) throws Throwable {

                // emissions are defined here manually

                if (!emitter.isDisposed()) {

                    // emit once
                    emitter.onNext(task);

                    // emissions complete
                    emitter.onComplete();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // do work on background
        .observeOn(AndroidSchedulers.mainThread()); // observe results on main

        // subscribe() here calls subscribe() defined above
        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

                Log.d(TAG, "onSubscribe");
                disposables.add(d);
                Log.d(TAG, "disposable added");
            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "single object emission: " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        // alternatively, "consumer" is another type of observer
        disposables.add(taskObservable.subscribe(new Consumer<Task>() {

            @Override
            public void accept(Task task) throws Throwable {

                Log.d(TAG, "consuming taskObservable ...");
            }
        }));

        // An observer type that has an onSubscribe callback (Observer) exposes the disposable in
        // that method. Alternatively, an observer type without the onSubscribe callback (Consumer)
        // returns the disposable directly in the subscribe() association. All observers are disposable.
    }

    // emit objects in list manually
    private void createObservableFromList() {

        final List<Task> tasks = DataSource.createTaskList();

        Observable<Task> taskObservable = Observable.create(new ObservableOnSubscribe<Task>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Task> emitter) throws Throwable {

                // emissions are defined here manually

                if (!emitter.isDisposed()) {

                    // loop through and emit each item in list
                    for (Task task : tasks) {
                        emitter.onNext(task);
                    }

                    // emissions complete
                    emitter.onComplete();
                }
            }
        })
        .subscribeOn(Schedulers.io()) // do work on background
        .observeOn(AndroidSchedulers.mainThread()); // observe results on main

        // subscribe() here calls subscribe() defined above
        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

                Log.d(TAG, "onSubscribe");
                disposables.add(d);
                Log.d(TAG, "disposable added");
            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "single object in list emission: " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // fromIterable() operator calls onNext() on each object automatically
    private void fromIterableWithFilter() {

        // subscribeOn: put this observable on this (typically background) thread, doing all operations here
        // observeOn: observe this observable's emissions on this (typically main) thread

        // common operators
        // https://codingwithmitch.com/courses/rxjava-rxandroid-for-beginners/rxjava-operators/

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

    private void justOperator() {

        final Task task = new Task("Walk the dog", false, 3);

        Observable<Task> taskObservable = Observable
                .just(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext");
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void rangeOperatorWithMapAndTakeWhileAndRepeat() {

        Observable<Task> observable = Observable
                .range(0, 9) // (a,b]
                .subscribeOn(Schedulers.io())
                .map(new Function<Integer, Task>() {

                    @Override
                    public Task apply(Integer integer) throws Throwable {
                        Log.d(TAG, "thread: " + Thread.currentThread().getName());
                        return new Task("new task with priority: " + integer, false, integer);
                    }
                })
                .takeWhile(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) throws Throwable {
                        return task.getPriority() < 9;
                    }
                })
                .repeat(3)
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: " + task.getPriority());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void intervalOperator() {

        Observable<Long> observable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Throwable {
                        Log.d(TAG, "test: " + aLong + ", thread: " + Thread.currentThread().getName());
                        return aLong <= 5;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d(TAG, "onNext: " + aLong);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void timerOperator() {

        Observable<Long> observable = Observable
                .timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d(TAG, "onNext: " + aLong);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void fromArray() {

        Task[] list = new Task[5];

        list[0] = (new Task("Take out the trash", true, 3));
        list[1] = (new Task("Walk the dog", false, 2));
        list[2] = (new Task("Make my bed", true, 1));
        list[3] = (new Task("Unload the dishwasher", false, 0));
        list[4] = (new Task("Make dinner", true, 5));

        Observable<Task> taskObservable = Observable
                .fromArray(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: : " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void fromCallable() {

        // create Observable (method will not execute yet)
        Observable<Task> callable = Observable
                .fromCallable(new Callable<Task>() {
                    @Override
                    public Task call() throws Exception {

                        // correct line commented below since this example file does not have Room/SQLite setup
//                        return MyDatabase.getTask();
                        return new Task("apples", true, 1);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        // method will be executed since now something has subscribed
        callable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: : " + task.getDescription());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // create view model
    MainViewModel viewModel;

    private void fromRetrofitUsingFutureObservable() {

        // get data using executor/future
        try {

            viewModel.makeFutureQuery().get()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

                    // this method triggers the future to be called
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            Log.d(TAG, "onSubscribe called.");
                        }

                        @Override
                        public void onNext(@NonNull ResponseBody responseBody) {
                            Log.d(TAG, "onNext called.");
                            try {
                                Log.d(TAG, responseBody.string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e(TAG, "onError: ", e);
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete called.");
                        }
                    });

        // executor throwables
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // the following method performs the same action as above using far less code
    private void fromRetrofitUsingFlowableLiveData() {

        // get data using flowable/livedata
        viewModel.makeFlowableQuery().observe(this, new androidx.lifecycle.Observer<ResponseBody>() {

            // must less exposure on livedata (androidx) observer vs. rxjava3 observer
            // onChanged vs. onSubscribed, onNext, onError, onComplete
            // livedata has .observe() in contrast to full observable .subscribe()

            @Override
            public void onChanged(ResponseBody responseBody) {
                Log.d(TAG, "LiveData onChanged called.");
                try {
                    Log.d(TAG, responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void filter() {

        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTaskList())
                .filter(new Predicate<Task>() {

                    @Override
                    public boolean test(Task task) throws Throwable {

                        // only tasks that meet this test will be emitted

                        if(task.getDescription().equals("Wash dishes")) {
                            return true;
                        }
                        return false;

                        // or for example use the task boolean attribute directly
//                        return task.isComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: filter ... " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void distinct() {

        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTaskList())
                .distinct(new Function<Task, String>() {

                    // this is similar to filter, however it considers uniqueness as the predicate
                    // an object field (attribute) must be specified

                    @Override
                    public String apply(Task task) throws Throwable {

                        // this means: emit task if it's description is unique, only one task
                        // with for a given description will be omitted
                        return task.getDescription();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: distinct ... " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void take() {

        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTaskList())
                .take(3) // emit only 3 items regardless of list size
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: take(3) ... " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void takeWhile() {

        Observable<Task> taskObservable = Observable
                .fromIterable(DataSource.createTaskList())
                .takeWhile(new Predicate<Task>() { // continue emitting until predicate fails, then stop emitting

                    @Override
                    public boolean test(Task task) throws Throwable {
                        return task.isComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        taskObservable.subscribe(new Observer<Task>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task) {
                Log.d(TAG, "onNext: takeWhile ... " + task.getDescription());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void mapTransformation() {

        Observable
                .fromIterable(DataSource.createTaskList())
                .map(new Function<Task, String>() {

                    // map applies a function to every emitted item prior to its emission
                    // for each task in list, get its description, and only emit that string

                    @Override
                    public String apply(Task task) throws Throwable {
                        return task.getDescription();
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String string) {
                Log.d(TAG, "onNext: map transformation: " + string);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        // alternatively, update the task object to complete, then emit it
        Observable
                .fromIterable(DataSource.createTaskList())
                .map(new Function<Task, Task>() {

                    @Override
                    public Task apply(Task task) throws Throwable {
                        task.setComplete(true);
                        return task;
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Task>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Task task) {

                        // really strange log output here, if comment out the description print line
                        // below, then only two tasks isComplete booleans are printed!
                        Log.d(TAG, "task: " + task.getDescription());
                        Log.d(TAG, "is task complete? " + task.isComplete());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // basic operator examples
//        createObservable(); // most flexible operator, complete control over emissions
//        createObservableFromList(); // extending the single case above
//        fromIterableWithFilter(); // operator exists to handle manual case above
//        justOperator(); // emit just one observable (or list < 10 items)
//        rangeOperatorWithMapAndTakeWhileAndRepeat(); // example of loop, repeat, and map
//        intervalOperator(); // emit at specified intervals
//        timerOperator(); // emit a single observable after specified delay
//        fromArray(); // same form and idea as fromIterable

        // Room - SQLite
//        fromCallable(); // very useful for db calls, returns result when complete

        // MVVM - Retrofit - RxJava
//        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
//        fromRetrofitUsingFutureObservable();
//        fromRetrofitUsingFlowableLiveData();

        // more operators
//        filter(); // filter via predicate test
//        distinct(); // use this on multiple UI clicks on same button
//        take(); // emit only a certain number of items
//        takeWhile(); // continue emitting until predicate fails, then stop emitting

        // transformation operators
        mapTransformation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // remove all observers (make them eligible for GC)
        // this would be called in onCleared() of viewmodel
        disposables.clear(); // .dispose() disables all future observations of these observables (hard clear)
    }
}