package com.din.mzitu.api;

import com.din.mzitu.adapter.PostSingleAdapter;
import com.din.mzitu.adapter.PostAllAdapter;
import com.din.mzitu.base.BaseApi;
import com.din.mzitu.bean.PostSingleBean;
import com.din.mzitu.bean.PostAllBean;
import com.din.mzitu.bean.PostDateBean;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/4 下午12:20
 */
public final class Mzitu extends BaseApi {


    private final static int OFFSET = 5;

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
    public List<PostAllBean> parseMzituMainData(int page, String webURL) {
        List<PostAllBean> postAllBeans = new ArrayList<>();
        // 获取页面显示的页数
        Elements pageElements = selectElements(webURL, "nav.navigation", "a.page-numbers");
        if (pageElements != null) {
            int count = pageElements.size();
            // 判断显示的总页数，分一页和多页。一页没有结果，多页有换页标示，多页时根据换页标示获取总页数
            int position = count >= 1 ? Integer.parseInt(pageElements.get(count - 2).text()) : 1;
            if (position >= page) {
                // 当请求的页数小于总的页数则去请求数据
                Elements elements = selectElements(webURL + "page/" + page, "div.postlist", "ul[id=pins]", "li");     // 分析网页的内容
                // 遍历每一页的帖子个数
                for (int i = 0; i < elements.size(); i++) {
                    String url = elements.get(i).select("a").attr("href");
                    String image = elements.get(i).select("img").attr("data-original");
                    String title = elements.get(i).select("img").attr("alt");
                    postAllBeans.add(new PostAllBean(PostAllAdapter.TYPE_MZITU, url, image, title));     // 将获取到的内容放在List里
                }
                // 请求页数小于总页数返回请求的数据
                return postAllBeans;
            }
            // 请求页数超过总页数返回空
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
    public List<PostSingleBean> parseMzituContentData(int page, String webURL) {
        List<PostSingleBean> postSingleBeans = new ArrayList<>();
        Elements elements = selectElements(webURL, "div.pagenavi", "span");     // 分析网页的内容
        // 获取图片总数
        int pages = position(elements, 2);
        // 分页起点
        int start = (page - 1) * OFFSET;
        // 分页数目为10条
        int count = page * OFFSET;
        // 分页终点
        int end = pages > count ? count : pages;
        // 判断是否到底部了
        if (pages >= page) {
            for (int i = start; i < end; i++) {
                Element element = selectElements(webURL + "/" + i, "div.main-image", "img").first();
                Elements inform = selectElements(webURL + "/" + i, "div.main-meta", "span");
                String informs = inform.get(0).text() + "  " + inform.get(1).text() + "  " + inform.get(2).text() + "  ";
                String url = element.attr("src");
                postSingleBeans.add(new PostSingleBean(PostSingleAdapter.TYPE_MZITU, url, informs));     // 将获取到的内容放在List里
            }
            // 请求页数小于总页数返回请求的数据
            return postSingleBeans;
        } else {
            // 请求页数超过总页数返回空
            return null;
        }
    }

    /**
     * 妹子网每日更新页面Jsoup分析
     *
     * @param webURL
     * @return
     */
    public List<PostDateBean> parseMzituUpdateData(int page, String webURL) {
        List<PostDateBean> postDateBeans = new ArrayList<>();
        // 获取总的月份
        Elements elements = selectElements(webURL, "div.all", "ul.archives", "li");
        if (page < elements.size()) {   // 当请求的页数小于总的年份去请求新的数据
            Elements nextElements = elements.get(page).select("a");
            for (int j = 0; j < nextElements.size(); j++) {
                String title = nextElements.get(j).text();
                String url = nextElements.get(j).attr("href");
                postDateBeans.add(new PostDateBean(title, url));     // 将获取到的内容放在List里
            }
            // 请求页数小于总页数返回请求的数据
            return postDateBeans;
        }
        // 请求页数超过总页数返回空
        return null;
    }

    /**
     * 妹子网自拍Jsoup解析
     *
     * @param webURL
     * @return
     */
    public List<PostSingleBean> parseMzituSelfData(int page, String webURL) {
        List<PostSingleBean> postSingleBeans = new ArrayList<>();
        Elements elements = selectElements(webURL, "div.pagenavi-cm", "span.current");     // 分析网页的内容
        int pages = Integer.valueOf(elements.get(elements.size() - 1).text());
        if (pages >= page) {
            webURL = webURL + "comment-page-" + page + "/#comments";
            Elements pageElement = selectElements(webURL, "div[id=comments]", "li");
            for (int j = 0; j < pageElement.size(); j++) {
                String url = pageElement.get(j).select("img").attr("src");
                String title = pageElement.get(j).select("div.comment-meta").select("a").text();
                postSingleBeans.add(new PostSingleBean(PostSingleAdapter.TYPE_MZITU, url, title));
                // 将获取到的内容放在List里
            }
            // 请求页数小于总页数返回请求的数据
            return postSingleBeans;
        }
        // 请求页数超过总页数返回空
        return null;
    }
}