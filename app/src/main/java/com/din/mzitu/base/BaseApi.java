package com.din.mzitu.base;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class BaseApi {

    /**
     * 从第一个页面获取该系列的网页总数
     *
     * @param elements
     * @param last
     * @return
     */
    protected int position(Elements elements, int last) {
        if (elements.size() >= 2) {
            return Integer.valueOf(elements.get(elements.size() - last).text());
        }
        return 0;
    }

    /**
     * 链接到网址获取数据
     *
     * @param url
     * @param css
     * @param query0
     * @return
     */
    protected Elements selectElements(String url, String css, String query0) {
        try {
            Document document = Jsoup.connect(url).get();       // 通过Jsoup链接到一个网址，获取到整个document
            Elements elements = document.select(css).select(query0);     // 分析网页的内容
            return elements;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 链接到网址获取数据
     *
     * @param url
     * @param css
     * @param query0
     * @return
     */
    protected Elements selectElements(String url, String css, String query0, String query1) {
        try {
            Document document = Jsoup.connect(url).get();       // 通过Jsoup链接到一个网址，获取到整个document
            Elements elements = document.select(css).select(query0).select(query1);     // 分析网页的内容
            return elements;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}