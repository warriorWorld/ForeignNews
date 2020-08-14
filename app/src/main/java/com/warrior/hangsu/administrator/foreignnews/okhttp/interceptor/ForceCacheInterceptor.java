package com.warrior.hangsu.administrator.foreignnews.okhttp.interceptor;

import android.text.TextUtils;
import android.util.Log;


import com.warrior.hangsu.administrator.foreignnews.okhttp.CacheCaretaker;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ForceCacheInterceptor implements Interceptor {
    private final String TAG = "ForceCacheInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        Log.d(TAG, "headers:" + request.headers().toString());
        try {
//            Log.d(TAG, "request body:" + request.body().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSupportCache(request)) {
            String cache = CacheCaretaker.getContent(getKey(request));
            if (!TextUtils.isEmpty(cache)) {
                return new Response.Builder()
                        .request(request)
                        .protocol(Protocol.get(Protocol.HTTP_1_0.toString()))
                        .message("success")
                        .code(200)
                        .body(ResponseBody.create(MediaType.get("application/json"), cache))
                        .build();
            } else {
                Response response = chain.proceed(request);
                if (response.isSuccessful()) {
                    //string方法只能调用一次 在调用了response.body().string()方法之后，response中的流会被关闭，我们需要创建出一个新的response给应用层处理。
                    String content = response.body().string();
                    CacheCaretaker.saveContent(getKey(request), content);
                    return response.newBuilder()
                            .body(ResponseBody.create(response.body().contentType(), content))
                            .build();
                }
                return response;
            }
        } else {
            return chain.proceed(request);
        }
    }

    private boolean isSupportCache(Request request) {
        return true;
    }

    private String getKey(Request request) {
        String url = request.url().toString();
        Headers headers = request.headers();
        String stringHeader = null;
        stringHeader = headers.toString();
        if (!TextUtils.isEmpty(stringHeader)) {
            return url + stringHeader;
        } else {
            return url;
        }
    }
}
