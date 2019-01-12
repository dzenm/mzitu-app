package com.din.mzitu.api;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

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
public final class LiGui extends BaseApi implements IPresenter {

    public static final String LIGUI = "http://www.ligui.org";

    private final static int OFFSET_PICTURE = 5;

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

    @Override
    public void onCreate(LifecycleOwner owner) {
    }

    @Override
    public void onStart(LifecycleOwner owner) {

    }

    @Override
    public void onResume(LifecycleOwner owner) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseLiGuiBanner(LIGUI);
            }
        }).start();
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
        Elements element = selectElements(webURL, "div.pageinfo", A);       // 获取分页的角标
        if (element != null) {
            //对字符串剪切获取最终的分页数量
            String href = element.get(element.size() - 1).attr(HREF);
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
                        postAllBeans.add(new PostAllBean(PostAllAdapter.TYPE_LIGUI, url, image, title));
                    }
                    return postAllBeans;
                }
            }
        }
        return null;
    }

    public List<PostSingleBean> parseLiGuiContentData(int page, String webURL) {
        List<PostSingleBean> postSingleBeans = new ArrayList<>();
        int start = (page - 1) * OFFSET_PICTURE + 1;
        int end = page * OFFSET_PICTURE;
        for (int i = start; i <= end; i++) {
            String web = i == 1 ? webURL : webURL.substring(0, webURL.lastIndexOf(".")) + "_" + i + SUFFIX;
            Element element = selectElements(web, "div.nry", IMG).first();
            if (element == null) {
                break;
            }
            String title = element.attr(ALT);
            String url = LIGUI + element.attr(SRC);
            postSingleBeans.add(new PostSingleBean(PostSingleAdapter.TYPE_LIGUI, url, title));
        }
        return postSingleBeans;
    }
}