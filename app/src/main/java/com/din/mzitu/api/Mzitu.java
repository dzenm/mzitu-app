package com.din.mzitu.api;

import com.din.mzitu.adapters.ContentAdapter;
import com.din.mzitu.adapters.SeriesAdapter;
import com.din.mzitu.base.BaseApi;
import com.din.mzitu.bean.ContentBean;
import com.din.mzitu.bean.SeriesBean;
import com.din.mzitu.bean.UpdateBean;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/4 下午12:20
 */
public final class Mzitu extends BaseApi {


    private final static int OFFSET = 10;

    /**
     * 私密构造方法
     */
    private Mzitu() {
    }

    /**
     * 静态内部类
     */
    private static class Instance {
        private static final Mzitu instance = new Mzitu();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static Mzitu getInstance() {
        return Instance.instance;
    }

    /**
     * 妹子网的主页面jsoup解析
     *
     * @param webURL
     * @return
     */
    public List<SeriesBean> parseMzituMainData(int page, String webURL) {
        List<SeriesBean> seriesBeans = new ArrayList<>();
        // 获取页面显示的页数
        Elements pageElements = selectElements(webURL, "div.nav-links", "a.page-numbers");
        if (pageElements != null) {
            int count = pageElements.size();
            // 判断显示的页数，分一页和多页。一页没有结果，多页有换页标示，多页时根据换页标示获取总页数
            int position = count >= 1 ? Integer.parseInt(pageElements.get(count - 2).text()) : 1;
            if (position >= page) {
                Elements elements = selectElements(webURL + "page/" + page, "div.postlist", "ul[id=pins]", "li");     // 分析网页的内容
                // 遍历每一页的帖子个数
                for (int i = 0; i < elements.size(); i++) {
                    String url = elements.get(i).select("a").attr("href");
                    String image = elements.get(i).select("img").attr("data-original");
                    String title = elements.get(i).select("img").attr("alt");
                    seriesBeans.add(new SeriesBean(SeriesAdapter.TYPE_MZITU, url, image, title));     // 将获取到的内容放在List里
                }
                return seriesBeans;
            }
            return null;
        }
        return null;
    }

    /**
     * 妹子网单张图片Jsoup解析
     *
     * @param webURL
     * @return
     */
    public List<ContentBean> parseMzituContentData(int page, String webURL) {
        List<ContentBean> contentBeans = new ArrayList<>();
        Elements elements = selectElements(webURL, "div.pagenavi", "span");     // 分析网页的内容
        // 获取图片总数
        int pages = position(elements, 2);
        // 分页起点
        int start = (page - 1) * OFFSET + 1;
        // 分页终点
        int count = pages * OFFSET;
        int end = pages > count ? count : pages;
        for (int i = start; i <= end; i++) {
            Element element = selectElements(webURL + "/" + i, "div.main-image", "img").first();
            Elements inform = selectElements(webURL + "/" + i, "div.main-meta", "span");
            String informs = inform.get(0).text() + "  " + inform.get(1).text() + "  " + inform.get(2).text() + "  ";
            String url = element.attr("src");
            contentBeans.add(new ContentBean(ContentAdapter.TYPE_MZITU, url, informs));     // 将获取到的内容放在List里
        }

        // 判断是否到底部了
        if (count % OFFSET < page) {
            return contentBeans;
        } else {
            return null;
        }
    }

    /**
     * 妹子网每日更新页面Jsoup分析
     *
     * @param webURL
     * @return
     */
    public List<UpdateBean> parseMzituUpdateData(int page, String webURL) {
        List<UpdateBean> updateBeans = new ArrayList<>();
        Elements elements = selectElements(webURL, "div.all", "ul.archives", "li");
        if (page < elements.size()) {
            Elements nextElements = elements.get(page).select("a");
            for (int j = 0; j < nextElements.size(); j++) {
                String title = nextElements.get(j).text();
                String url = nextElements.get(j).attr("href");
                updateBeans.add(new UpdateBean(title, url));     // 将获取到的内容放在List里
            }
            return updateBeans;
        }
        return null;
    }

    /**
     * 妹子网自拍Jsoup解析
     *
     * @param webURL
     * @return
     */
    public List<ContentBean> parseMzituSelfData(int page, String webURL) {
        List<ContentBean> contentBeans = new ArrayList<>();
        Elements elements = selectElements(webURL, "div.pagenavi-cm", "span.current");     // 分析网页的内容
        int pages = Integer.valueOf(elements.get(elements.size() - 1).text());
        if (pages >= page) {
            webURL = webURL + "comment-page-" + page + "/#comments";
            Elements pageElement = selectElements(webURL, "div[id=comments]", "li");
            for (int j = 0; j < pageElement.size(); j++) {
                String url = pageElement.get(j).select("img").attr("src");
                String title = pageElement.get(j).select("div.comment-meta").select("a").text();
                contentBeans.add(new ContentBean(ContentAdapter.TYPE_MZITU, url, title));
                // 将获取到的内容放在List里
            }
            return contentBeans;
        }
        return null;
    }
}