package WebmagicTest;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Na;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.FilePersistentBase;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.util.List;
import java.io.FileOutputStream;

public class TsingHuaTest extends FilePersistentBase implements PageProcessor, Pipeline {

    private  Site site;
    private  String filename;
    private  HSSFWorkbook workbook;
    private  HSSFSheet sheet;
    private  int rows=0;
    private  Logger logger;

    public TsingHuaTest(){
        Logger logger = LoggerFactory.getLogger(getClass());
        site = Site.me().setTimeOut(1000).setRetrySleepTime(3);
        setPath("C:\\Users\\zzlsix\\Desktop\\demoTest");//设置保存路径
        filename =new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".xls";
        workbook =new HSSFWorkbook();
        sheet=workbook.createSheet("爬取结果");
        HSSFRow row=sheet.createRow((rows));
        row.createCell(0).setCellValue("名字");
      //  row.createCell(1).setCellValue("性别");
        row.createCell(1).setCellValue("职称");
        row.createCell(2).setCellValue("邮箱");
        rows++;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<String>names=resultItems.get("name");//姓名
       // List<String>sexes=resultItems.get("sex");//性别
        List<String>emlies=resultItems.get("emlie");//邮箱
        List<String>ranks=resultItems.get("rank");//职称
        // logger.debug(names.toString());
        //logger.debug(sexes.toString());
        //logger.debug(ranks.toString());
        //logger.debug(works.toString());
        //创建行
        for(int i=0;i<names.size();i++){
            HSSFRow row=sheet.createRow(rows);
            //row.createCell(0).setCellValue((rows));
            row.createCell(0).setCellValue(names.get(i));
           // row.createCell(1).setCellValue(sexes.get(i));
            row.createCell(1).setCellValue(ranks.get(i));
            row.createCell(2).setCellValue(emlies.get(i));
            rows++;
        }
        save();
    }

    //保存
    private synchronized void save(){
        try{
            FileOutputStream out=new FileOutputStream(getFile(this.path).getPath()+"\\"+filename);
            workbook.write(out);
            out.close();
            logger.info(this.path+"\\"+filename+"存储完毕");
        }catch (IOException e){
            logger.warn("存储失败",e);
        }
    }

    @Override
    public void process(Page page) {

        Selectable html=page.getHtml();
        Selectable name=html.xpath("//div[@class=text]/h2/a/text()");
       // Selectable sex=html.xpath("//div[@class=text]/h2/a/text()");
        Selectable rank=html.xpath("//div[@class=text]/p[1]/text()");
        Selectable emlie=html.xpath("//div[@class=text]/p[3]/text()");
        // page.putField("div1",page.getHtml().css("td.w1 a").all());//td下的 w1类的中的a
        // page.putField("div2",page.getHtml().css("td.w4").all());
        page.putField("name",name.all());
       // page.putField("sex",sex.all());
        page.putField("rank",rank.all());
        page.putField("emlie",emlie.all());

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new TsingHuaTest())
                .addUrl("https://www.cs.tsinghua.edu.cn/szzk/jzgml.htm")//爬取页面
                .addPipeline(new TsingHuaTest())
                .run();//执行爬虫
    }
}
