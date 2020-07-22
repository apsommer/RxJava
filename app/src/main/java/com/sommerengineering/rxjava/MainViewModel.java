package com.sommerengineering.rxjava;

import androidx.lifecycle.ViewModel;
import java.util.concurrent.Future;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

public class MainViewModel extends ViewModel {

    // dependency
    private Repository repository;
    public MainViewModel() {
        repository = Repository.getInstance();
    }

    // called by activity
    public Future<Observable<ResponseBody>> makeFutureQuery() {
        return repository.makeFutureQuery();
    }
}
