package com.insightsurfface.myword.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by acorn on 2020/8/4.
 */
public class TranslateWraper implements Parcelable {
    private String query;
    private String UKPhonetic;
    private String UKSpeakUrl;
    private String USPhonetic;
    private String USSpeakUrl;
    private String translate;
    private String webTranslate;

    public TranslateWraper() {

    }

    protected TranslateWraper(Parcel in) {
        query = in.readString();
        UKPhonetic = in.readString();
        UKSpeakUrl = in.readString();
        USPhonetic = in.readString();
        USSpeakUrl = in.readString();
        translate = in.readString();
        webTranslate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(query);
        dest.writeString(UKPhonetic);
        dest.writeString(UKSpeakUrl);
        dest.writeString(USPhonetic);
        dest.writeString(USSpeakUrl);
        dest.writeString(translate);
        dest.writeString(webTranslate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TranslateWraper> CREATOR = new Creator<TranslateWraper>() {
        @Override
        public TranslateWraper createFromParcel(Parcel in) {
            return new TranslateWraper(in);
        }

        @Override
        public TranslateWraper[] newArray(int size) {
            return new TranslateWraper[size];
        }
    };

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUKPhonetic() {
        return UKPhonetic;
    }

    public void setUKPhonetic(String UKPhonetic) {
        this.UKPhonetic = UKPhonetic;
    }

    public String getUKSpeakUrl() {
        return UKSpeakUrl;
    }

    public void setUKSpeakUrl(String UKSpeakUrl) {
        this.UKSpeakUrl = UKSpeakUrl;
    }

    public String getUSPhonetic() {
        return USPhonetic;
    }

    public void setUSPhonetic(String USPhonetic) {
        this.USPhonetic = USPhonetic;
    }

    public String getUSSpeakUrl() {
        return USSpeakUrl;
    }

    public void setUSSpeakUrl(String USSpeakUrl) {
        this.USSpeakUrl = USSpeakUrl;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getWebTranslate() {
        return webTranslate;
    }

    public void setWebTranslate(String webTranslate) {
        this.webTranslate = webTranslate;
    }
}
