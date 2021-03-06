package org.abframe.service;

import org.abframe.dao.BaseDaoSupport;
import org.abframe.entity.Page;
import org.abframe.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WeixinCommandService {

    @Autowired
    private BaseDaoSupport dao;


    public void save(PageData pd) throws Exception {
        dao.save("WeixinCommandMapper.save", pd);
    }

    public void delete(PageData pd) throws Exception {
        dao.delete("WeixinCommandMapper.delete", pd);
    }

    public void edit(PageData pd) throws Exception {
        dao.update("WeixinCommandMapper.updateRoleById", pd);
    }


    public List<PageData> list(Page page) throws Exception {
        return (List<PageData>) dao.findForList("WeixinCommandMapper.datalistPage", page);
    }


    public List<PageData> listAll(PageData pd) throws Exception {
        return (List<PageData>) dao.findForList("WeixinCommandMapper.listAll", pd);
    }

    /*
    * 通过id获取数据
    */
    public PageData findById(PageData pd) throws Exception {
        return (PageData) dao.findForObject("WeixinCommandMapper.findById", pd);
    }

    /*
    * 批量删除
    */
    public void deleteAll(String[] ArrayDATA_IDS) throws Exception {
        dao.delete("WeixinCommandMapper.deleteAll", ArrayDATA_IDS);
    }

    /*
    * 匹配关键词
    */
    public PageData findByKw(PageData pd) throws Exception {
        return (PageData) dao.findForObject("WeixinCommandMapper.findByKw", pd);
    }
}

