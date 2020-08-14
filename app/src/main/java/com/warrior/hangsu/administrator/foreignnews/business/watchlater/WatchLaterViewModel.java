package com.warrior.hangsu.administrator.foreignnews.business.watchlater;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.List;

public class WatchLaterViewModel extends ViewModel {
    private Context mContext;
    private IWatchLaterModel mWatchLaterModel;
    private MutableLiveData<List<String>> watchLaterData = new MutableLiveData<>();

    public WatchLaterViewModel(Context context) {
        mContext = context.getApplicationContext();
        mWatchLaterModel = new WatchLaterModel();
    }

    void getWatchLater() {
        watchLaterData.setValue(mWatchLaterModel.getWatchLater(mContext));
    }

    public LiveData<List<String>> getWatchLaterData() {
        return watchLaterData;
    }
}
