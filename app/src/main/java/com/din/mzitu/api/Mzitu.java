package com.din.mzitu.api;

import com.din.mzitu.adapter.PostAllAdapter;
import com.din.mzitu.adapter.PostSingleAdapter;
import com.din.mzitu.base.BaseApi;
import com.din.mzitu.bean.PostAllBean;
import com.din.mzitu.bean.PostDateBean;
import com.din.mzitu.bean.PostSingleBean;

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
    private final static String IMG = "img";
    private final static String HREF = "href";
    private final static String SRC = "src";
    private final static String ALT = "alt";
    private final static String LI = "li";
    private final static String SPAN = "span";
    private final static String A = "a";
    private List<List> headers;

    /**
     * 私密构造方法
     */
    private Mzitu() {
        headers = new ArrayList<>();
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

    public List<List> getHeaders() {
        return headers;
    }

    /**
     * 妹子网的header
     *
     * @param url
     */
    public void parseMzituBanner(String url) {
        List<String> titles = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        Elements elements = selectElements(url, "div.header", LI);
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                titles.add(elements.get(i).select(A).text());
                urls.add(elements.get(i).select(A).attr(HREF));
            }
            headers.add(titles);
            headers.add(urls);
        }
    }

    /**
     * 妹子网的主页面jsoup解析
     *
     * @param postUrl
     * @return
     */
    public List<PostAllBean> parseMzituMainData(int page, String postUrl) {
        List<PostAllBean> postAllBeans = new ArrayList<>();
        // 获取页面显示的页数
        Elements pageElements = selectElements(postUrl, "nav.navigation", A);
        if (pageElements != null) {
            int count = pageElements.size();
            // 判断显示的总页数，分一页和多页。一页没有结果，多页有换页标示，多页时根据换页标示获取总页数
            int position = count >= 1 ? Integer.parseInt(pageElements.get(count - 2).text()) : 1;
            if (position >= page) {
                // 当请求的页数小于总的页数则去请求数据
                Elements elements = selectElements(postUrl + "page/" + page, "div.postlist", LI);     // 分析网页的内容
                // 遍历每一页的帖子个数
                for (int i = 0; i < elements.size(); i++) {
                    String url = elements.get(i).select(A).attr(HREF);
                    String image = elements.get(i).select(IMG).attr("data-original");
                    String title = elements.get(i).select(IMG).attr(ALT);
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
     * @param postUrl
     * @return
     */
    public List<PostSingleBean> parseMzituContentData(int page, String postUrl) {
        List<PostSingleBean> postSingleBeans = new ArrayList<>();
        Elements elements = selectElements(postUrl, "div.pagenavi", SPAN);     // 分析网页的内容
        // 获取图片总数
        int pages = position(elements, 2);
        // 分页起点
        int start = (page - 1) * OFFSET + 1;
        // 分页数目为5条
        int count = page * OFFSET;
        // 判断是否到底部了
        if (pages >= count - OFFSET) {
            // 分页终点
            int end = pages > count ? count : pages;
            for (int i = start; i <= end; i++) {
                Element element = selectElements(postUrl + "/" + i, "div.main-image", IMG).first();
                Elements inform = selectElements(postUrl + "/" + i, "div.main-meta", SPAN);
                String informs = inform.get(0).text() + "  " + inform.get(1).text() + "  " + inform.get(2).text() + "  ";
                String url = element.attr(SRC);
                postSingleBeans.add(new PostSingleBean(PostSingleAdapter.TYPE_MZITU, url, informs));     // 将获取到的内容放在List里
            }
            // 请求页数小于总页数返回请求的数据
            return postSingleBeans;
        }
        // 请求页数超过总页数返回空
        return null;
    }

    /**
     * 妹子网每日更新页面Jsoup分析
     *
     * @param postUrl
     * @return
     */
    public List<PostDateBean> parseMzituUpdateData(int page, String postUrl) {
        List<PostDateBean> postDateBeans = new ArrayList<>();
        // 获取总的月份
        Elements elements = selectElements(postUrl, "div.all", LI);
        if (page < elements.size()) {   // 当请求的页数小于总的年份去请求新的数据
            Elements nextElements = elements.get(page).select(A);
            for (int i = 0; i < nextElements.size(); i++) {
                String title = nextElements.get(i).text();
                String url = nextElements.get(i).attr(HREF);
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
     * @param postUrl
     * @return
     */
    public List<PostSingleBean> parseMzituSelfData(int page, String postUrl) {
        List<PostSingleBean> postSingleBeans = new ArrayList<>();
        Elements elements = selectElements(postUrl + "1", "div.pagenavi-cm", A);     // 分析网页的内容
        int pages = Integer.valueOf(elements.get(elements.size() - 2).text());
        if (pages >= page) {
            postUrl = postUrl + page;
            Elements pageElement = selectElements(postUrl, "div.postlist", LI);
            for (int i = 0; i < pageElement.size(); i++) {
                String url = pageElement.get(i).select(IMG).attr(SRC);
                String title = pageElement.get(i).select(A).text();
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