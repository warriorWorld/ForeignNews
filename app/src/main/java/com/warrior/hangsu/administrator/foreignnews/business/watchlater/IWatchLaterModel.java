package com.warrior.hangsu.administrator.foreignnews.business.watchlater;

import android.content.Context;

import java.util.List;

import io.reactivex.observers.DisposableObserver;

public interface IWatchLaterModel {
    List<String> getWatchLater(Context context);
}
