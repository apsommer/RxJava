package com.sommerengineering.rxjava;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

// retrofit interface responsible for relative portion of api endpoint
public interface RequestApi {

    // return endpoint response as an observable
    @GET("todos/1")
    Observable<ResponseBody> makeObservableQuery();

    // note that observable is not a "publisher", but flowable is a "publisher"
    // a publisher is needed for livedata conversion using LiveDataReactiveStreams in repo

    // return endpoint response as a flowable
    @GET("todos/1")
    Flowable<ResponseBody> makeFlowableQuery();
}
