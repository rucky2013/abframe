package org.abframe.controller;

import net.common.utils.date.DateUtil;
import net.common.utils.uuid.UuidUtil;
import org.abframe.controller.base.BaseController;
import org.abframe.entity.Page;
import org.abframe.service.AdvertService;
import org.abframe.util.Constant;
import org.abframe.util.PageData;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * 广告管理
 */
@Controller
@RequestMapping(value = "/adv")
public class AdvertController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvertController.class);

    @Autowired
    private AdvertService advertService;

    @RequestMapping(value = "/list")
    public ModelAndView listAdvert(Page page) {

        ModelAndView mv = new ModelAndView();
        PageData pd;
        try {
            pd = this.getPageData();

            String title = pd.getString("title");
            String publisher = pd.getString("publisher");
            if (null != title && !"".equals(title)) {
                title = title.trim();
                pd.put("title", title);
            }
            if (null != publisher && !"".equals(publisher)) {
                publisher = publisher.trim();
                pd.put("publisher", publisher);
            }

            page.setPd(pd);
            List<PageData> varList = advertService.list(page);

            mv.setViewName("advert/advertList");
            mv.addObject("varList", varList);
            mv.addObject("pd", pd);
            mv.addObject(Constant.SESSION_QX, this.getHC());    //按钮权限
        } catch (Exception e) {
            LOGGER.error("Controller adv exception ", e);
        }

        return mv;
    }


    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public ModelAndView toAdd() {
        ModelAndView mv = new ModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            mv.setViewName("advert/advertEdit");
            mv.addObject("msg", "save");
        } catch (Exception e) {
            LOGGER.error("Controller adv/toAdd exception ", e);
        }
        return mv;
    }


    @RequestMapping(value = "/toEdit", method = RequestMethod.GET)
    public ModelAndView toEdit() {
        ModelAndView mv = new ModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            pd = advertService.findById(pd);

            mv.setViewName("advert/advertEdit");
            mv.addObject("msg", "updateRoleById");
            mv.addObject("pd", pd);
        } catch (Exception e) {
            LOGGER.error("Controller adv/toEdit exception ", e);
        }
        return mv;
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ModelAndView edit(
            HttpServletRequest request,
            @RequestParam(value = "tp", required = false) MultipartFile tp,
            @RequestParam(value = "tpz", required = false) String tpz,
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "starttime", required = false) String starttime,
            @RequestParam(value = "endtime", required = false) String endtime,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "tourl", required = false) String tourl
    ) throws Exception {
        ModelAndView mv = new ModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();
        pd.put("content", content);
        pd.put("id", id);
        pd.put("title", title);
        pd.put("type", type);
        pd.put("publisher", publisher);
        pd.put("starttime", starttime);
        pd.put("endtime", endtime);
        pd.put("status", status);
        pd.put("tourl", tourl);
        pd.put("uptime", DateUtil.getDateTimeStr());

        if (null == tpz) {
            tpz = "";
        }

        //图片上传
        String pictureSaveFilePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../";
        pictureSaveFilePath = pictureSaveFilePath.substring(6);        //去掉 'file:/'

        if (null != tp && !tp.isEmpty()) {
            try {
                String tpid = UuidUtil.genTerseUuid();

                // 扩展名格式：
                String extName = "";
                if (tp.getOriginalFilename().lastIndexOf(".") >= 0) {
                    extName = tp.getOriginalFilename().substring(tp.getOriginalFilename().lastIndexOf("."));
                }

                this.copyFile(tp.getInputStream(), pictureSaveFilePath + "TP", tpid + extName).replaceAll("-", "");
                pd.put("adurl", tpid + extName);
            } catch (IOException e) {
                LOGGER.error("Controller adv exception ", e);
            }
        } else {
            pd.put("adurl", tpz);
        }
        advertService.edit(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ModelAndView save(
            HttpServletRequest request,
            @RequestParam(value = "tp", required = false) MultipartFile tp,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "starttime", required = false) String starttime,
            @RequestParam(value = "endtime", required = false) String endtime,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "tourl", required = false) String tourl
    ) throws Exception {
        ModelAndView mv = new ModelAndView();
        PageData pd = new PageData();
        pd = this.getPageData();

        pd.put("title", title);
        pd.put("content", content);
        pd.put("type", type);
        pd.put("publisher", publisher);
        pd.put("starttime", starttime);
        pd.put("endtime", endtime);
        pd.put("status", status);
        pd.put("tourl", tourl);

        pd.put("addtime", DateUtil.getDateTimeStr());
        pd.put("uptime", DateUtil.getDateTimeStr());

        //图片上传
        String pictureSaveFilePath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("") + "../../");
        pictureSaveFilePath = pictureSaveFilePath.substring(6);        //去掉 'file:/'

        if (null != tp && !tp.isEmpty()) {
            try {
                String id = UuidUtil.genTerseUuid();

                // 扩展名格式：
                String extName = "";
                if (tp.getOriginalFilename().lastIndexOf(".") >= 0) {
                    extName = tp.getOriginalFilename().substring(tp.getOriginalFilename().lastIndexOf("."));
                }

                this.copyFile(tp.getInputStream(), pictureSaveFilePath + "TP", id + extName).replaceAll("-", "");
                pd.put("adurl", id + extName);
            } catch (IOException e) {
                LOGGER.error("Controller adv exception ", e);
            }
        } else {
            pd.put("adurl", "");
        }
        advertService.save(pd);
        mv.addObject("msg", "success");
        mv.setViewName("save_result");
        return mv;
    }


    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public void delete(PrintWriter out) throws Exception {
        ModelAndView mv = new ModelAndView();
        PageData pd = new PageData();
        try {
            pd = this.getPageData();
            pd = advertService.findById(pd);
            String adurl = pd.getString("adurl");

            //删除硬盘上的文件 start
            String xmpath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../";    //项目路径

            if (adurl != null && !adurl.equals("")) {
                adurl = (xmpath.trim() + "TP/" + adurl.trim()).substring(6).trim();
                File f1 = new File(adurl.trim());
                if (f1.exists()) {
                    f1.delete();
                } else {
                    System.out.println("====" + adurl + "不存在");
                }
            }
            advertService.delete(pd);
            out.write("success");
            out.close();
        } catch (Exception e) {
            LOGGER.error("Controller adv exception ", e);
        }

    }


    @RequestMapping(value = "/delPic")
    public void delPic(PrintWriter out) {

        try {
            ModelAndView mv = new ModelAndView();
            PageData pd = new PageData();
            pd = this.getPageData();

            String tpurl = pd.getString("tpurl");                                                    //图片路径
            if (tpurl != null) {
                //删除硬盘上的文件 start
                String xmpath = String.valueOf(Thread.currentThread().getContextClassLoader().getResource("")) + "../../";    //项目路径
                tpurl = xmpath.trim() + "TP/" + tpurl.trim();
                tpurl = tpurl.substring(6);                                                            //去掉 'file:/'
                File f = new File(tpurl.trim());
                if (f.exists()) {
                    f.delete();
                } else {
                    System.out.println("====" + tpurl + "不存在");
                }
                //删除硬盘上的文件 end
                advertService.delTp(pd);                                                        //删除数据中图片数据
            }

            out.write("success");
            out.close();
        } catch (Exception e) {
            LOGGER.error("Controller adv exception ", e);
        }
    }


    /**
     * 写文件到当前目录的upload目录中
     *
     * @param in
     * @throws IOException
     */
    private String copyFile(InputStream in, String dir, String realName)
            throws IOException {
        File file = new File(dir, realName);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        FileUtils.copyInputStreamToFile(in, file);
        return realName;
    }
}
