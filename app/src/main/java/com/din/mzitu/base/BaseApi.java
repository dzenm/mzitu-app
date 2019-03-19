package com.din.mzitu.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import com.din.mzitu.api.IPresenter;
import com.din.mzitu.basehelper.LogHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public abstract class BaseApi implements IPresenter {

    public static final String LIGUI = "http://www.ligui.org";
    public final static String IMG = "img";
    public final static String HREF = "href";
    public final static String SRC = "src";
    public final static String ALT = "alt";
    public final static String LI = "li";
    public final static String SPAN = "span";
    public final static String DATA_ORIGINAL = "data-original";
    public final static String A = "a";
    public final static String SUFFIX = ".html";
    public final static String NEWSFEED = "newsfeed";

    public final static int OFFSET = 5;

    /**
     * 从第一个页面获取该系列的网页总数
     *
     * @param elements
     * @param last
     * @return
     */
    protected int position(Elements elements, int last) {
        if (elements.size() >= 2) {
            int position = Integer.valueOf(elements.get(elements.size() - last).text());
            LogHelper.d("页面数量: " + position);
            return position;
        }
        return 0;
    }

    /**
     * 链接到网址获取数据
     *
     * @param args ...
     * @return
     */
    protected Elements selectElements(String... args) {
        if (args.length < 2) {
            return null;
        }
        try {
            LogHelper.d("当前爬虫的页面URL: " + args[0]);
            Document document = Jsoup.connect(args[0]).get();       // 通过Jsoup链接到一个网址，获取到整个document
            Elements elements = document.select(args[1]);
            for (int i = 2; i < args.length; i++) {
                elements = elements.select(args[i]);     // 分析网页的内容
            }
            LogHelper.d("当前爬虫的页面返回的节点个数: " + elements.size());
            return elements;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onCreate(LifecycleOwner owner) {

    }

    @Override
    public void onStart(LifecycleOwner owner) {

    }

    @Override
    public void onResume(LifecycleOwner owner) {

    }

    @Override
    public void onPause(LifecycleOwner owner) {

    }

    @Override
    public void onStop(LifecycleOwner owner) {

    }

    @Override
    public void onDestory(LifecycleOwner owner) {

    }

    @Override
    public void onLifeCycleChanged(LifecycleOwner owner, Lifecycle.Event event) {

    }
}