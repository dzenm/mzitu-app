package com.din.mzitu.api;

import android.util.Log;

import com.din.mzitu.adapter.PostAllAdapter;
import com.din.mzitu.adapter.PostSingleAdapter;
import com.din.mzitu.base.BaseApi;
import com.din.mzitu.bean.PostAllBean;
import com.din.mzitu.bean.PostSingleBean;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: dinzhenyan
 * @time: 2018/12/4 下午12:19
 */
public final class LiGui extends BaseApi {

    public static final String LIGUI = "http://www.ligui.org";

    private final static int OFFSET_PICTURE = 5;
    private final static int OFFSET_POST = 10;

    private final static String HREF = "href";
    private final static String IMG = "img";
    private final static String SRC = "src";
    private final static String ALT = "alt";
    private final static String LI = "li";
    private final static String A = "a";
    private final static String SUFFIX = ".html";

    private List<List> topNavs;

    private LiGui() {
        topNavs = new ArrayList<>();
    }

    private static class Instance {
        private static final LiGui instance = new LiGui();
    }

    public static LiGui getInstance() {
        return LiGui.Instance.instance;
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
            for (int i = 0; i < elements.size(); i++) {
                if (i != 0 && i != 1 && i != 3) {
                    titles.add(elements.get(i).text());
                    urls.add(LIGUI + elements.get(i).attr(HREF));
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
    public List<PostAllBean> parseLiGuiMainData(int page, String webURL) {
        List<PostAllBean> postAllBeans = new ArrayList<>();
        String website = webURL + "1" + SUFFIX;
        Elements elements = selectElements(website, "div.pic4list", LI);
        if (elements != null) {
            int length = elements.size();
            if (length < (page + 1) * OFFSET_POST) {
                int start = (page - 1) * OFFSET_POST;
                int end = page * OFFSET_POST;
                int count = length >= end ? end : length;

                // 获取每个帖子的url、图片和标题
                for (int i = start; i < count; i++) {
                    String url = LIGUI + elements.get(i).select(A).attr(HREF);
                    String image = LIGUI + elements.get(i).select(IMG).attr(SRC);
                    String title = elements.get(i).select(IMG).attr(ALT);
                    postAllBeans.add(new PostAllBean(PostAllAdapter.TYPE_LIGUI, url, image, title));
                }
                return postAllBeans;
            } else {

            }

        }
        return null;
    }

    public List<PostSingleBean> parseLiGuiContentData(int page, String webURL) {
        List<PostSingleBean> postSingleBeans = new ArrayList<>();
        int i = (page - 1) * OFFSET_PICTURE + 1;
        while (true) {
            if (i <= (page * OFFSET_PICTURE)) {
                break;
            }
            String web = i == 1 ? webURL : webURL.substring(0, webURL.length() - 6) + "_" + i + SUFFIX;
            Log.d("DZY", "web: " + web);
            Element element = selectElements(web, "div.nry", IMG).first();
            Log.d("DZY", "element: " + element);
            if (element == null) {
                break;
            }
            String title = element.attr(ALT);
            String url = LIGUI + element.attr(SRC);
            Log.d("DZY", "title: " + title);
            postSingleBeans.add(new PostSingleBean(PostSingleAdapter.TYPE_LIGUI, url, title));
            i++;
        }
        return postSingleBeans;
    }
}