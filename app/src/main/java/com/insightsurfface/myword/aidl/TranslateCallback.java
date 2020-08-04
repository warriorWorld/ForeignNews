package com.insightsurfface.myword.aidl;

import android.os.RemoteException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by acorn on 2020/8/4.
 */
public abstract class TranslateCallback {
    private ITranslateCallback callback = new ITranslateCallback.Stub() {
        @Override
        public void onResponse(TranslateWraper translate) throws RemoteException {
            TranslateCallback.this.onResponse(translate);
        }

        @Override
        public void onFailure(String message) throws RemoteException {
            TranslateCallback.this.onFailure(message);
        }
    };

    public ITranslateCallback getCallback() {
        return callback;
    }

    public abstract void onResponse(TranslateWraper translate);

    public abstract void onFailure(String message);
}
