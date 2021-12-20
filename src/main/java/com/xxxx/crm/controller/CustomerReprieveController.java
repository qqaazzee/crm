package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerLossQuery;
import com.xxxx.crm.query.CustomerReprieveQuery;
import com.xxxx.crm.service.CustomerReprieveService;
import com.xxxx.crm.vo.CustomerReprieve;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_rep")
public class CustomerReprieveController extends BaseController {

    @Resource
    private CustomerReprieveService customerReprieveService;

    /**
     * 分页条件查询流失列表
     * @param customerReprieveQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerReprieveByParams(CustomerReprieveQuery customerReprieveQuery){
        System.out.println(customerReprieveQuery);
        return customerReprieveService.queryCustomerReprieveByQuery(customerReprieveQuery);
    }


    /**
     * 添加暂缓数据
     * @param customerReprieve
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCustomerPepr(CustomerReprieve customerReprieve){
        customerReprieveService.addCustomerPepr(customerReprieve);
        return success("添加暂缓数据成功");

    }


    /**
     * 修改暂缓数据
     * @param customerReprieve
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCustomerPepr(CustomerReprieve customerReprieve){
        customerReprieveService.updateCustomerPepr(customerReprieve);
        return success("修改暂缓数据成功");
    }

    /**
     * 打开添加修改暂缓页面
     * @return
     */
    @RequestMapping("addOrUpdateCustomerReprPage")
    public String addOrUpdateCustomerReprPage(Integer lossId, HttpServletRequest request,Integer id){
        request.setAttribute("lossId",lossId);

        //判断ID是否为空
        if (id != null) {
            //通过主键id查询暂缓数据
            CustomerReprieve customerRep = customerReprieveService.selectByPrimaryKey(id);
            //设置到请求域中
            request.setAttribute("customerRep",customerRep);
        }

        return "customerLoss/customer_rep_add_update";
    }


    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo delete(Integer id){
        customerReprieveService.delteCustomerPepr(id);
        return success("记录删除成功");

    }

}
