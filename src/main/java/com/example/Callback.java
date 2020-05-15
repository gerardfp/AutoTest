package com.example;

import retrofit2.Call;
import retrofit2.Response;

public interface Callback<T> extends retrofit2.Callback<T> {
    @Override
    default void onResponse(Call<T> call, Response<T> response){
        if(response.body() != null){
            onResponseOk(response.body());
        }
    }

    void onResponseOk(T response);

    @Override
    default void onFailure(Call call, Throwable t) {
       t.printStackTrace();
    }
}
