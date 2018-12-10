package com.din.mzitu.api;

import android.util.Log;

import com.din.mzitu.adapter.ContentAdapter;
import com.din.mzitu.adapter.SeriesAdapter;
import com.din.mzitu.base.BaseApi;
import com.din.mzitu.bean.ContentBean;
import com.din.mzitu.bean.SeriesBean;

import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/4 下午12:19
 */
public final class LiGui extends BaseApi {

    private LiGui() {
    }

    private static class Instance {
        private static final LiGui instance = new LiGui();
    }

    public static LiGui getInstance() {
        return LiGui.Instance.instance;
    }

    /**
     * 丽柜网套图Jsoup解析
     *
     * @param webURL
     * @return
     */
    public List<SeriesBean> parseLiGuiMainData(int page, String webURL) {
        List<SeriesBean> seriesBeans = new ArrayList<>();
        Elements elements = selectElements(webURL, "div.boxs", "li");
        Log.d("DZY", "size: " + elements.size());
        for (int i = 0; i < elements.size(); i++) {
            String url = "http://www.ligui.org" + elements.get(i).select("a").attr("href");
            String image = elements.get(i).select("img").attr("src");
            String title = elements.get(i).select("img").attr("alt");
            Log.d("DZY", "url: " + url);
            seriesBeans.add(new SeriesBean(SeriesAdapter.TYPE_LIGUI, url, image, title));
        }
        return seriesBeans;
    }

    public List<ContentBean> parseLiGuiContentData(int page, String webURL) {
        List<ContentBean> contentBeans = new ArrayList<>();
        Elements element = selectElements(webURL, "div[id=pages]", "a");
        int position = position(element, 2);
        webURL = webURL.substring(0, webURL.length() - 5);
        Elements elements;
        for (int i = 1; i <= position; i++) {
            if (i == 1) {
                elements = selectElements(webURL + ".html", "div.content", "center");
            } else {
                elements = selectElements(webURL + "_" + i + ".html", "div.content", "center");
            }
            for (int j = 0; j < elements.size(); j++) {
                String title = elements.get(j).select("img").attr("alt");
                String url = elements.get(j).select("img").attr("src");
                contentBeans.add(new ContentBean(ContentAdapter.TYPE_LIGUI, url, title));
            }
        }
        return contentBeans;
    }
}