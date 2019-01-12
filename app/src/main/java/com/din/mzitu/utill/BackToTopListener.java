package com.din.mzitu.utill;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author: dinzhenyan
 * @time: 2018/12/15 上午11:41
 */
public class BackToTopListener implements View.OnClickListener {

    private final long delayTime = 300;
    private long lastTime = 0;
    private BackToTopView backToTopView;

    public BackToTopListener(@NonNull BackToTopView backToTopView) {
        this.backToTopView = backToTopView;
    }

    // 双击事件
    @Override
    public void onClick(View v) {
        long nowClickTime = System.currentTimeMillis();
        if (nowClickTime - lastTime > delayTime) {
            lastTime = nowClickTime;
        } else {
            backToTopView.backToTop();
        }
    }

    public interface BackToTopView {
        void backToTop();
    }
}