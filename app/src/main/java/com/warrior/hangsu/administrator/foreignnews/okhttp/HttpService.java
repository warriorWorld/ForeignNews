package com.warrior.hangsu.administrator.foreignnews.okhttp;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface HttpService {
    @Headers({"Content-Type: application/json;charset=utf-8", "Accept: text/html"})
    @GET
    Observable<ResponseBody> getHtml(@Url String url);
}
