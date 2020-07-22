package com.sommerengineering.rxjava;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

// retrofit interface responsible for relative portion of api endpoint
public interface RequestApi {

    @GET("todos/1")
    Observable<ResponseBody> makeObservableQuery();
}
