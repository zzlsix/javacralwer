package WebmagicTest;

import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class Name implements PageProcessor {

    @Override
    //解析页面
    public void process(Page page) {
        page.putField("div",page.getHtml().css("td.w1 a").all());//td下的 w1类的中的a
        page.putField("div2",page.getHtml().css("td.w4").all());
        page.putField("div3",page.getHtml().css("td.w5").all());



    }

    private Site site = Site.me()
            .setCharset("utf8");
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new Name())
                .addUrl("http://cs.whu.edu.cn/teacher.aspx?showtype=jobtitle&typename=%e6%95%99%e6%8e%88")//爬取页面
                .run();//执行爬虫
    }



}
