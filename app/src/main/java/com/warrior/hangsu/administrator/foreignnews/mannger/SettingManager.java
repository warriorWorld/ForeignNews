/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.warrior.hangsu.administrator.foreignnews.mannger;


import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.utils.ScreenUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtil;

/**
 * @author yuyh.
 * @date 2016/9/23.
 */
public class SettingManager {

    private volatile static SettingManager manager;
    public static final String[] FONT_SIZE_LIST = {"超小", "小", "默认", "大", "很大", "巨大", "非常大"};
    public static final String[] FONT_SIZE_CODE_LIST = {"12", "14", "16", "18", "20", "22", "24"};

    public static SettingManager getInstance() {
        return manager != null ? manager : (manager = new SettingManager());
    }

    /**
     * 保存书籍阅读字体大小
     *
     * @param fontSizePx
     * @return
     */
    public void saveFontSize(int fontSizePx) {
        // 书籍对应
        SharedPreferencesUtil.getInstance().putInt(getFontSizeKey(), fontSizePx);
    }

    public int getReadFontSize() {
        return SharedPreferencesUtil.getInstance().getInt(getFontSizeKey(), ScreenUtils.dpToPxInt(16));
    }

    public String getFontSizeExplain() {
        for (int i = 0; i < FONT_SIZE_LIST.length; i++) {
            int size = Integer.valueOf(FONT_SIZE_CODE_LIST[i]);
            if (size >= (int) ScreenUtils.pxToDp(getReadFontSize())) {
                return FONT_SIZE_LIST[i];
            }
        }
        return "";
    }

    private String getFontSizeKey() {
        return "-readFontSize";
    }

    public void saveReadTheme(int theme) {
        SharedPreferencesUtil.getInstance().putInt("readTheme", theme);
    }

    public int getReadTheme() {
        if (SharedPreferencesUtil.getInstance().getBoolean(ShareKeys.ISNIGHT, false)) {
            return ThemeManager.NIGHT;
        }
        return SharedPreferencesUtil.getInstance().getInt("readTheme", 3);
    }
}
