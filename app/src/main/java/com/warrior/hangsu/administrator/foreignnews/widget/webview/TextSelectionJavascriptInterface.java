package com.warrior.hangsu.administrator.foreignnews.widget.webview;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;

/**
 * This javascript interface allows the page to communicate that text has been
 * selected by the user.
 * <p/>
 * 这个类是java和JavaScript的桥梁,JavaScript中可以调用这个类中的方法 达到两种语言通讯的效果
 *
 * @author btate
 */
public class TextSelectionJavascriptInterface {

    /**
     * The TAG for logging.
     */
    private static final String TAG = "TextSelectionJavascriptInterface";

    /**
     * The javascript interface name for adding to web view.
     */
    private final String interfaceName = "TextSelection";

    private TextSelectionListener textSelectionListener;

    /**
     * The context.
     */
    Context mContext;

    // Need handler for callbacks to the UI thread
    final Handler mHandler = new Handler();

    /**
     * Constructor accepting context.
     *
     * @param c
     */
    public TextSelectionJavascriptInterface(Context c) {
        this.mContext = c;
    }

    /**
     * Constructor accepting context and mListener.
     *
     * @param c
     * @param mListener
     */
    public TextSelectionJavascriptInterface(Context c,
                                            TextSelectionListener mListener) {
        this.mContext = c;
        this.textSelectionListener = mListener;
    }


    /**
     * 在JavaScript中调用该方法告诉java已经获取到选择的单词
     *
     * @param msg
     */
    @JavascriptInterface
    public void seletedWord(final String msg) {
        if (this.textSelectionListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    textSelectionListener.seletedWord(msg);
                }
            });
        }
    }

    @JavascriptInterface
    public void clickWord(final String msg) {
        if (this.textSelectionListener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    textSelectionListener.clickWord(msg);
                }
            });
        }
    }

    /**
     * Gets the interface name
     *
     * @return
     */
    @JavascriptInterface
    public String getInterfaceName() {
        return this.interfaceName;
    }

    public void setTextSelectionListener(TextSelectionListener textSelectionListener) {
        this.textSelectionListener = textSelectionListener;
    }
}
