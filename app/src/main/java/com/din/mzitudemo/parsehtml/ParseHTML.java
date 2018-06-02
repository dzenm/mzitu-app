package com.din.mzitudemo.parsehtml;

import com.din.mzitudemo.javabeans.ContentBean;
import com.din.mzitudemo.javabeans.UpdateBean;
import com.din.mzitudemo.javabeans.SeriesBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseHTML {

    /**
     * 每日更新页面Jsoup分析
     *
     * @param URL
     * @return
     */
    public static List<UpdateBean> parseDayUpdateHTMLData(String URL) {
        List<UpdateBean> list = new ArrayList<>();
        Elements elements = getElements(URL, "div.all", "ul.archives");
        for (int i = 0; i < elements.size(); i++) {
            Elements nextElements = elements.get(i).select("a");
            addList(list, nextElements);
        }
        return list;
    }
    
    /**
     * 自拍页面Jsoup分析
     *
     * @param URL
     * @return
     */
    public static List<ContentBean> parseSelfHTMLData(String URL) {
        List<ContentBean> list = new ArrayList<>();
        Elements elements = getElements(URL, "div.pagenavi-cm", "span.current");     // 分析网页的内容
        int pages = Integer.valueOf(elements.get(elements.size() - 1).text());
        for (int i = 1; i <= pages; i++) {
            URL = URL + "comment-page-" + i + "/#comments";
            Elements pageElement = getElements(URL, "div[id=comments]", "li");
            for (int j = 0; j < pageElement.size(); j++) {
                String url = pageElement.get(j).select("img").attr("src");
                String title = pageElement.get(j).select("div.comment-meta").select("a").text();
                list.add(new ContentBean(url, title));     // 将获取到的内容放在List里
            }
        }
        return list;
    }

    /**
     * 每日更新详细内容页面Jsoup分析
     *
     * @param URL
     * @return
     */
    public static List<ContentBean> parseContentHTMLData(String URL) {
        List<ContentBean> list = new ArrayList<>();
        Elements elements = getElements(URL, "div.pagenavi", "span");     // 分析网页的内容
        if (elements.size() >= 2) {
            int pages = getPosition(elements, 2);
            for (int i = 1; i <= pages; i++) {
                try {
                    Document pageDocument = Jsoup.connect(URL + "/" + i).get();
                    Element element = pageDocument.select("div.main-image").select("img").first();
                    getElements(URL, "div.main-image", "span");
                    Elements information = pageDocument.select("div.main-meta").select("span");
                    String tyle = information.get(0).text() + "   ";
                    String time = information.get(1).text() + "   ";
                    String count = information.get(2).text() + "   ";
                    String url = element.attr("src").toString();
                    list.add(new ContentBean(url, tyle + time + count));     // 将获取到的内容放在List里
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 主页面及其他页面Jsoup分析
     *
     * @param URL
     * @return
     */
    public static List<SeriesBean> parseMaintHTMLData(String URL) {
        List<SeriesBean> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(URL).get();       // 通过Jsoup链接到一个网址，获取到整个document
            Elements pageElements = document.select("nav.navigation").select("div.nav-links").select("a.page-numbers");
            if (pageElements.size() >= 1) {
                int position = Integer.parseInt(pageElements.get(pageElements.size() - 2).text());
                for (int i = 1; i <= position; i++) {
                    Document pageDocument = Jsoup.connect(URL + "page/" + i).get();
                    Elements elements = pageDocument.select("div.postlist").select("ul[id=pins]").select("li");     // 分析网页的内容
                    addLists(list, elements);
                }
            } else {
                Document pageDocument = Jsoup.connect(URL).get();
                Elements elements = pageDocument.select("div.postlist").select("ul[id=pins]").select("li");     // 分析网页的内容
                addLists(list, elements);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 从第一个页面获取该系列的网页总数
     *
     * @param elements
     * @param last
     * @return
     */
    private static int getPosition(Elements elements, int last) {
        int pages = Integer.valueOf(elements.get(elements.size() - last).text());
        return pages;
    }

    /**
     * 链接到网址获取数据
     *
     * @param URL
     * @param cssQuery
     * @param query
     * @return
     */
    private static Elements getElements(String URL, String cssQuery, String query) {
        try {
            Document document = Jsoup.connect(URL).get();       // 通过Jsoup链接到一个网址，获取到整个document
            Elements elements = document.select(cssQuery).select(query);     // 分析网页的内容
            return elements;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加到List
     *
     * @param list
     * @param nextElements
     */
    private static void addList(List<UpdateBean> list, Elements nextElements) {
        for (int j = 0; j < nextElements.size(); j++) {
            String title = nextElements.get(j).text().toString();
            String url = nextElements.get(j).attr("href").toString();
            list.add(new UpdateBean(title, url));     // 将获取到的内容放在List里
        }
    }

    /**
     * 添加到List
     *
     * @param list
     * @param elements
     */
    private static void addLists(List<SeriesBean> list, Elements elements) {
        for (int j = 0; j < elements.size(); j++) {
            String url = elements.get(j).select("a").attr("href");
            String image = elements.get(j).select("img").attr("data-original");
            String title = elements.get(j).select("img").attr("alt");
            list.add(new SeriesBean(url, image, title));     // 将获取到的内容放在List里
        }
    }
}