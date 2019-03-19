package com.din.mzitu.api;

import com.din.mzitu.adapter.PicAdapter;
import com.din.mzitu.adapter.PostAdapter;
import com.din.mzitu.base.BaseApi;
import com.din.mzitu.basehelper.LogHelper;
import com.din.mzitu.bean.PicBean;
import com.din.mzitu.bean.PostBean;
import com.din.mzitu.bean.PostDateBean;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/4 下午12:20
 */
public final class Mzitu extends BaseApi implements IPresenter {

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
        LogHelper.d("Mzitu---创建爬虫单例");
        return Instance.instance;
    }

    public List<List> getHeaders() {
        return headers;
    }

    /**
     * 妹子网的header
     *
     * @param webUrl
     */
    public void parseMzituBanner(String webUrl) {
        LogHelper.d("Mzitu---爬取的Banner页面url: " + webUrl);
        List<String> titles = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        Elements elements = selectElements(webUrl, "div.header", LI);
        LogHelper.d("Mzitu---爬取的Banner的个数: " + elements.size());
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                String title = elements.get(i).select(A).text();
                String url = elements.get(i).select(A).attr(HREF);
                titles.add(title);
                urls.add(url);
                LogHelper.d("Mzitu---爬取的Banner页面title: " + title);
                LogHelper.d("Mzitu---爬取的Banner页面url: " + url);
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
    public List<PostBean> parseMzituMainData(int page, String postUrl) {
        LogHelper.d("Mzitu---爬取的页面url: " + postUrl);
        LogHelper.d("Mzitu---爬取的页面页数: " + page);
        List<PostBean> postBeans = new ArrayList<>();
        // 获取页面显示的页数
        Elements pageElements = selectElements(postUrl, "nav.pagination", A);
        if (pageElements != null) {
            int count = pageElements.size();
            // 判断显示的总页数，分一页和多页。一页没有结果，多页有换页标示，多页时根据换页标示获取总页数
            int position = count >= 1 ? Integer.parseInt(pageElements.get(count - 2).text()) : 1;
            if (position >= page) {
                // 当请求的页数小于总的页数则去请求数据
                Elements elements = selectElements(postUrl + "page/" + page, "div.postlist", LI);     // 分析网页的内容
                // 遍历每一页的帖子个数
                for (int i = 0; i < elements.size(); i++) {
                    Elements newsfeeds = elements.get(i).select(SPAN);                          // 跳过广告
                    if (newsfeeds == null || newsfeeds.size() == 0) {
                        continue;
                    }
                    String url = elements.get(i).selectFirst(A).attr(HREF);
                    String image = elements.get(i).selectFirst(A).select(IMG).attr(DATA_ORIGINAL);
                    String title = elements.get(i).selectFirst(A).select(IMG).attr(ALT);
                    LogHelper.d("Mzitu---爬取的图片帖子的页面url: " + url);
                    LogHelper.d("Mzitu---爬取的图片帖子的页面缩略图: " + image);
                    LogHelper.d("Mzitu---爬取的图片帖子的页面标题: " + title);
                    postBeans.add(new PostBean(PostAdapter.TYPE_MZITU, url, image, title));    // 将获取到的内容放在List里
                }
                // 请求页数小于总页数返回请求的数据
                return postBeans;
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
    public List<PicBean> parseMzituContentData(int page, String postUrl) {
        LogHelper.d("Mzitu---爬取的图片帖子页面url: " + postUrl);
        List<PicBean> picBeans = new ArrayList<>();
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
            LogHelper.d("Mzitu---爬取的图片段: " + start + "-" + end);
            for (int i = start; i <= end; i++) {
                Element element = selectElements(postUrl + "/" + i, "div.main-image", IMG).first();
                Elements inform = selectElements(postUrl + "/" + i, "div.main-meta", SPAN);
                StringBuffer informs = new StringBuffer();
                for (int j = 0; j < inform.size(); j++) {
                    informs.append(inform.get(j).text() + "  ");
                }
                String url = element.attr(SRC);
                LogHelper.d("Mzitu---爬取的图片帖子的单张图片的信息: " + informs.toString());
                LogHelper.d("Mzitu---爬取的图片帖子的单张图片的url: " + url);
                picBeans.add(new PicBean(PicAdapter.TYPE_MZITU, url, informs.toString()));     // 将获取到的内容放在List里
            }
            // 请求页数小于总页数返回请求的数据
            return picBeans;
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
        page = page - 1;
        LogHelper.d("Mzitu---爬取的每日更新页面的url: " + postUrl);
        List<PostDateBean> postDateBeans = new ArrayList<>();
        // 获取总的月份
        Elements elements = selectElements(postUrl, "div.all", LI);
        if (page < elements.size()) {   // 当请求的页数小于总的年份去请求新的数据
            Elements nextElements = elements.get(page).select(A);
            for (int i = 0; i < nextElements.size(); i++) {
                String title = nextElements.get(i).text();
                String url = nextElements.get(i).attr(HREF);
                LogHelper.d("Mzitu---爬取的每日更新的单张图片的标题: " + title);
                LogHelper.d("Mzitu---爬取的每日更新的单张图片的url: " + url);
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
    public List<PicBean> parseMzituSelfData(int page, String postUrl) {
        LogHelper.d("Mzitu---爬取的自拍页面的url: " + postUrl);
        List<PicBean> picBeans = new ArrayList<>();
        // 通过页面跳转按钮找到页面的个数
        Elements elements = selectElements(postUrl + "1", "div.pagenavi-cm", A);
        int pages = Integer.valueOf(elements.get(elements.size() - 2).text());
        if (pages >= page) {
            postUrl = postUrl + (pages - page + 1);
            Elements pageElement = selectElements(postUrl, "div.postlist", LI);
            for (int i = 0; i < pageElement.size(); i++) {
                String title = pageElement.get(i).select(A).text();
                String url = pageElement.get(i).select(IMG).attr("data-original");
                LogHelper.d("Mzitu---爬取的自拍的单张图片的标题: " + title);
                LogHelper.d("Mzitu---爬取的自拍的单张图片的url: " + url);
                // 将获取到的内容放在List里
                picBeans.add(new PicBean(PicAdapter.TYPE_MZITU, url, title));
            }
            // 请求页数小于总页数返回请求的数据
            return picBeans;
        }
        // 请求页数超过总页数返回空
        return null;
    }
}