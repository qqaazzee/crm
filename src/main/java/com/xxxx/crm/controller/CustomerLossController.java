package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerLossQuery;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.service.CustomerLossService;
import com.xxxx.crm.vo.CustomerLoss;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_loss")
public class CustomerLossController extends BaseController {

    @Resource
    private CustomerLossService customerLossService;

    /**
     * 分页条件查询流失列表
     * @param customerLossQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerLossByParams(CustomerLossQuery customerLossQuery){
        return customerLossService.queryCustomerLossByParams(customerLossQuery);
    }

    /**
     * 流失列表主页
     * @return
     */
    @RequestMapping("index")
    public String index(){

        return "customerLoss/customer_loss";
    }

    /**
     * 打开暂缓表
     * @return
     */
    @RequestMapping("toCustomerReprPage")
    public String toCustomerReprPage(Integer lossId, Model model){

        CustomerLoss customerLoss = customerLossService.selectByPrimaryKey(lossId);
        System.out.println(customerLoss.getId());

        model.addAttribute("customerLoss",customerLoss);
        return "customerLoss/customer_rep";
    }


    /**
     * 更新流失客户的流失状态
     * @return
     */
    @RequestMapping("updateCustomerLossStateById")
    @ResponseBody
    public ResultInfo updateCustomerLossStateById(Integer id,String lossReason){
        customerLossService.updateCustomerLossStateById(id,lossReason);
        return success("确认流失成功");

    }


}
