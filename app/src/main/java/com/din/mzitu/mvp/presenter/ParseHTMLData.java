package com.din.mzitu.mvp.presenter;

import com.din.mzitu.beans.ContentBean;
import com.din.mzitu.beans.SeriesBean;
import com.din.mzitu.beans.UpdateBean;
import com.din.mzitu.utills.JsoupUtils;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ParseHTMLData {

    private List<SeriesBean> seriesBeans;
    private List<ContentBean> contentBeans;
    private List<UpdateBean> updateBeans;

    public List<SeriesBean> getSeriesBeans() {
        return seriesBeans;
    }

    public List<ContentBean> getContentBeans() {
        return contentBeans;
    }

    public List<UpdateBean> getUpdateBeans() {
        return updateBeans;
    }

    /**
     * 妹子网的主页面jsoup解析
     *
     * @param webURL
     * @return
     */
    public ParseHTMLData parseMzituMainData(String webURL) {
        seriesBeans = new ArrayList<>();
        Elements pageElements = JsoupUtils.selectElements(webURL, "nav.navigation", "a.page-numbers");
        if (pageElements.size() >= 1) {
            int position = Integer.parseInt(pageElements.get(pageElements.size() - 2).text());
            for (int i = 1; i <= position; i++) {
                Elements elements = JsoupUtils.selectElements(webURL + "page/" + i, "div.postlist", "ul[id=pins]", "li");     // 分析网页的内容
                addLists(seriesBeans, elements);
            }
        } else {
            Elements elements = JsoupUtils.selectElements(webURL, "div.postlist", "ul[id=pins]", "li");     // 分析网页的内容
            addLists(seriesBeans, elements);
        }
        return this;
    }

    /**
     * 妹子网的主页面添加到List
     *
     * @param list
     * @param elements
     */
    private void addLists(List<SeriesBean> list, Elements elements) {
        for (int j = 0; j < elements.size(); j++) {
            String url = elements.get(j).select("a").attr("href");
            String image = elements.get(j).select("img").attr("data-original");
            String title = elements.get(j).select("img").attr("alt");
            list.add(new SeriesBean(url, image, title));     // 将获取到的内容放在List里
        }
    }


    /**
     * 妹子网单张图片Jsoup解析
     *
     * @param webURL
     * @return
     */
    public ParseHTMLData parseMzituContentData(String webURL) {
        contentBeans = new ArrayList<>();
        Elements elements = JsoupUtils.selectElements(webURL, "div.pagenavi", "span");     // 分析网页的内容
        int pages = JsoupUtils.position(elements, 2);
        for (int i = 1; i <= pages; i++) {
            Element element = JsoupUtils.selectElements(webURL + "/" + i, "div.main-image", "img").first();
            Elements inform = JsoupUtils.selectElements(webURL + "/" + i, "div.main-meta", "span");
            String informs = inform.get(0).text() + "  " + inform.get(1).text() + "  " + inform.get(2).text() + "  ";
            String url = element.attr("src");
            contentBeans.add(new ContentBean(url, informs));     // 将获取到的内容放在List里
        }
        return this;
    }

    /**
     * 妹子网每日更新页面Jsoup分析
     *
     * @param webURL
     * @return
     */
    public ParseHTMLData parseMzituUpdateData(String webURL) {
        updateBeans = new ArrayList<>();
        Elements elements = JsoupUtils.selectElements(webURL, "div.all", "ul.archives", "li");
        for (int i = 0; i < elements.size(); i++) {
            Elements nextElements = elements.get(i).select("a");
            addList(updateBeans, nextElements);
        }
        return this;
    }

    /**
     * 妹子网每日更新添加到List
     *
     * @param list
     * @param nextElements
     */
    private void addList(List<UpdateBean> list, Elements nextElements) {
        for (int j = 0; j < nextElements.size(); j++) {
            String title = nextElements.get(j).text().toString();
            String url = nextElements.get(j).attr("href").toString();
            list.add(new UpdateBean(title, url));     // 将获取到的内容放在List里
        }
    }


    /**
     * 妹子网自拍Jsoup解析
     *
     * @param webURL
     * @return
     */
    public ParseHTMLData parseMzituSelfData(String webURL) {
        contentBeans = new ArrayList<>();
        Elements elements = JsoupUtils.selectElements(webURL, "div.pagenavi-cm", "span.current");     // 分析网页的内容
        int pages = Integer.valueOf(elements.get(elements.size() - 1).text());
        for (int i = 1; i <= pages; i++) {
            webURL = webURL + "comment-page-" + i + "/#comments";
            Elements pageElement = JsoupUtils.selectElements(webURL, "div[id=comments]", "li");
            for (int j = 0; j < pageElement.size(); j++) {
                String url = pageElement.get(j).select("img").attr("src");
                String title = pageElement.get(j).select("div.comment-meta").select("a").text();
                contentBeans.add(new ContentBean(url, title));     // 将获取到的内容放在List里
            }
        }
        return this;
    }

    /**
     * 丽柜网套图Jsoup解析
     *
     * @param webURL
     * @return
     */
    public ParseHTMLData parseLiGuiMainData(String webURL) {
        seriesBeans = new ArrayList<>();
        Elements elements = JsoupUtils.selectElements(webURL, "div.hezi", "li");
        for (int i = 0; i < elements.size(); i++) {
            String url = "http://www.ligui.org" + elements.get(i).select("a").attr("href");
            String image = elements.get(i).select("img").attr("src");
            String title = elements.get(i).select("img").attr("alt");
            seriesBeans.add(new SeriesBean(url, image, title));
        }
        return this;
    }

    public ParseHTMLData parseLiGuiContentData(String webURL) {
        contentBeans = new ArrayList<>();
        Elements element = JsoupUtils.selectElements(webURL, "div[id=pages]", "a");
        int position = JsoupUtils.position(element, 2);
        webURL = webURL.substring(0, webURL.length() - 5);
        Elements elements;
        for (int i = 1; i <= position; i++) {
            if (i == 1) {
                elements = JsoupUtils.selectElements(webURL + ".html", "div.content", "center");
            } else {
                elements = JsoupUtils.selectElements(webURL + "_" + i + ".html", "div.content", "center");
            }
            for (int j = 0; j < elements.size(); j++) {
                String title = elements.get(j).select("img").attr("alt");
                String url = elements.get(j).select("img").attr("src");
                contentBeans.add(new ContentBean(url, title));
            }
        }
        return this;
    }
}