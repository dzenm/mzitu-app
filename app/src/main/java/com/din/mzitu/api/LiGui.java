package com.din.mzitu.api;

import android.arch.lifecycle.LifecycleOwner;

import com.din.mzitu.adapter.PicAdapter;
import com.din.mzitu.adapter.PostAdapter;
import com.din.mzitu.base.BaseApi;
import com.din.mzitu.basehelper.LogHelper;
import com.din.mzitu.bean.PicBean;
import com.din.mzitu.bean.PostBean;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/4 下午12:19
 */
public final class LiGui extends BaseApi {

    private List<List> topNavs;

    private LiGui() {
        topNavs = new ArrayList<>();
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        LogHelper.d("主页面打开，创建线程获取");
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseLiGuiBanner(LIGUI);
            }
        }).start();
    }

    private static class Instance {
        private static final LiGui instance = new LiGui();
    }

    public static LiGui getInstance() {
        LogHelper.d("LiGui---创建爬虫单例");
        return Instance.instance;
    }

    public List<List> getTopNavs() {
        return topNavs;
    }

    /**
     * 丽柜网解析头部导航栏
     *
     * @param webURL
     * @return
     */
    public void parseLiGuiBanner(String webURL) {
        topNavs.clear();
        List<String> titles = new ArrayList<>();
        List<String> urls = new ArrayList<>();
        Elements elements = selectElements(webURL, "div.topnav", A);
        if (elements != null) {
            LogHelper.d("LiGui---爬虫Banner的数量: " + elements.size());
            for (int i = 0; i < elements.size(); i++) {
                if (i != 0 && i != 1 && i != 3) {
                    String title = elements.get(i).text();
                    String url = LIGUI + elements.get(i).attr(HREF);
                    titles.add(title);
                    LogHelper.d("LiGui---爬虫Banner的标题: " + title);
                    urls.add(url);
                    LogHelper.d("LiGui---爬虫Banner的URL: " + url);
                }
            }
            topNavs.add(titles);
            topNavs.add(urls);
        }
    }

    /**
     * 丽柜网套图Jsoup解析
     *
     * @param webURL
     * @return
     */
    public List<PostBean> parseLiGuiMainData(int page, String webURL) {
        LogHelper.d("LiGui---主页面爬虫的URL: " + webURL);
        List<PostBean> postBeans = new ArrayList<>();
        Elements element = selectElements(webURL, "div.pageinfo", A);       // 获取分页的角标
        if (element != null) {
            // 对字符串剪切获取最终的分页数量
            String href = element.get(element.size() - 1).attr(HREF);
            LogHelper.d("LiGui---获取分页的数量: " + href);
            int position;
            String website = null;
            if (webURL.equals("http://www.ligui.org/youmi/")) {
                int start = href.lastIndexOf("_") + 1;
                int end = href.indexOf(".");
                position = Integer.parseInt(href.substring(start, end));
                website = webURL + "list_13_" + page + SUFFIX;
            } else {
                position = Integer.parseInt(href.substring(0, href.indexOf(".")));
                website = webURL + page + SUFFIX;
            }
            if (page <= position) {
                Elements elements = selectElements(website, "div.pic4list", LI);
                if (elements != null) {
                    // 获取每个帖子的url、图片和标题
                    for (int i = 0; i < elements.size(); i++) {
                        String url = LIGUI + elements.get(i).select(A).attr(HREF);
                        String image = LIGUI + elements.get(i).select(IMG).attr(SRC);
                        String title = elements.get(i).select(IMG).attr(ALT);
                        LogHelper.d("LiGui---获取每个帖子的url: " + url);
                        LogHelper.d("LiGui---获取每个帖子的图片: " + image);
                        LogHelper.d("LiGui---获取每个帖子的标题: " + title);
                        postBeans.add(new PostBean(PostAdapter.TYPE_LIGUI, url, image, title));
                    }
                    return postBeans;
                }
            }
        }
        return null;
    }

    public List<PicBean> parseLiGuiContentData(int page, String webURL) {
        LogHelper.d("LiGui---帖子的URL: " + webURL);
        List<PicBean> picBeans = new ArrayList<>();
        int start = (page - 1) * OFFSET + 1;
        int end = page * OFFSET;
        for (int i = start; i <= end; i++) {
            String web = i == 1 ? webURL : webURL.substring(0, webURL.lastIndexOf(".")) + "_" + i + SUFFIX;
            Element element = selectElements(web, "div.nry", IMG).first();
            if (element == null) {
                break;
            }
            String title = element.attr(ALT);
            String url = LIGUI + element.attr(SRC);
            LogHelper.d("LiGui---帖子的标题: " + title);
            LogHelper.d("LiGui---帖子的图片URL: " + url);
            picBeans.add(new PicBean(PicAdapter.TYPE_LIGUI, url, title));
        }
        return picBeans;
    }
}