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

public class excelPipeline extends FilePersistentBase implements PageProcessor, Pipeline {

    private  Site site;
    private  String filename;
    private  HSSFWorkbook workbook;
    private  HSSFSheet sheet;
    private  int rows=0;
    private  Logger logger;

    public excelPipeline(){
        Logger logger = LoggerFactory.getLogger(getClass());
        site = Site.me().setTimeOut(1000).setRetrySleepTime(3);
        setPath("C:\\Users\\zzlsix\\Desktop\\demoTest");//设置保存路径
        filename =new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".xls";
        workbook =new HSSFWorkbook();
        sheet=workbook.createSheet("爬取结果");
        HSSFRow row=sheet.createRow((rows));
        row.createCell(0).setCellValue("名字");
        row.createCell(1).setCellValue("性别");
        row.createCell(2).setCellValue("职称");
        row.createCell(3).setCellValue("研究方向");
        row.createCell(4).setCellValue("人工智能");
        row.createCell(5).setCellValue(("软件工程"));
        row.createCell(6).setCellValue("计算机网络");
        rows++;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<String>names=resultItems.get("name");//姓名
        List<String>sexes=resultItems.get("sex");//性别
        List<String>ranks=resultItems.get("rank");//职称
        List<String>works=resultItems.get("work");//研究方向
       // List<String>worksP=resultItems.get("worksP");
       // logger.debug(names.toString());
        //logger.debug(sexes.toString());
        //logger.debug(ranks.toString());
        //logger.debug(works.toString());
        //创建行
        for(int i=0;i<names.size();i++){
            HSSFRow row=sheet.createRow(rows);
            //row.createCell(0).setCellValue((rows));
            row.createCell(0).setCellValue(names.get(i));
            row.createCell(1).setCellValue(sexes.get(i));
            row.createCell(2).setCellValue(ranks.get(i));
            row.createCell(3).setCellValue(works.get(i));
            if(works.get(i).contains("智能")){
                row.createCell(4).setCellValue(names.get(i));
            }
            else if(works.get(i).contains("软件")){
                row.createCell(5).setCellValue(names.get(i));
            }
            else if (works.get(i).contains("网络")){
                row.createCell(6).setCellValue(names.get(i));
            }
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
        Selectable name=html.xpath("//td[@class=w1]/a/text()");
        Selectable sex=html.xpath("//td[@class=w2]/text()");
        Selectable rank=html.xpath("//td[@class=w4]/text()");
        Selectable work=html.xpath("//td[@class=w5]/text()");
       // Selectable worksP=html.xpath("//td[@class=w5]/text()");//.regex(".*智能.*");
     //   Selectable worksP=html.css("td.w5","text").regex("*.智能*.");
       // page.putField("div1",page.getHtml().css("td.w1 a").all());//td下的 w1类的中的a
       // page.putField("div2",page.getHtml().css("td.w4").all());
        page.putField("name",name.all());
        page.putField("sex",sex.all());
        page.putField("rank",rank.all());
        page.putField("work",work.all());
     //   page.putField("worksP",worksP.all());

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new excelPipeline())
                .addUrl("http://cs.whu.edu.cn/teacher.aspx?showtype=jobtitle&typename=%e6%95%99%e6%8e%88")//爬取页面
                .addPipeline(new excelPipeline())
                .run();//执行爬虫
    }
}
