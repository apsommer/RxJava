package com.sommerengineering.rxjava;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodoServiceGenerator {

    // base url for retrofit call
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    // build with base url, rxjava call adpater, and gson converter
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());

    // associate the retrofit object to the api interface (relative portion of api call)
    private static Retrofit retrofit = builder.build();
    private static TodoRequestApi requestApi = retrofit.create(TodoRequestApi.class);

    // expose public getter
    public static TodoRequestApi getRequestApi() {
        return requestApi;
    }
}
