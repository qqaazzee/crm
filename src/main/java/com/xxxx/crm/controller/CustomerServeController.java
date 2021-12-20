package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.CustomerLossQuery;
import com.xxxx.crm.query.CustomerServeQuery;
import com.xxxx.crm.service.CustomerServeService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.CustomerServe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_serve")
public class CustomerServeController extends BaseController {

    @Resource
    private CustomerServeService customerServeService;


    /**
     * 分页条件查询业务表
     * @param customerServeQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerLossByParams(CustomerServeQuery customerServeQuery, Integer flag, HttpServletRequest request){

        //判断是否执行服务处理 如果则分配给当前登录用户的服务记录
        if (flag != null && flag == 1) {
            //设置查询条件:分配人
            customerServeQuery.setAssigner(LoginUserUtil.releaseUserIdFromCookie(request));
        }

        return customerServeService.queryCustomerServeList(customerServeQuery);
    }

    /**
     * 进入主页面
     * @return
     */
    // 服务管理页面转发方法
    @RequestMapping("index/{type}")
    public String index(@PathVariable Integer type){
        if(type==1){
            return "customerServe/customer_serve";
        }else if(type==2){

            return "customerServe/customer_serve_assign";
        }else if(type==3){
            return "customerServe/customer_serve_proce";
        }else if(type==4){
            return "customerServe/customer_serve_feed_back";
        }else if(type==5){
            return "customerServe/customer_serve_archive";
        }else{
            return "";
        }
    }

    /**
     * 打开创建服务页面
     * @return
     */
    @RequestMapping("addCustomerServePage")
    public String addCustomerServePage(){
        return "customerServe/customer_serve_add";

    }

    /**
     * 创建服务
     * @param customerServe
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCustomerServe(CustomerServe customerServe){
        customerServeService.addCustomerServe(customerServe);
        return success("添加服务成功");

    }

    /**
     * 服务更新
     *      1.服务分配
     *      2.服务处理
     *      3.服务反馈
     * @param customerServe
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCustomerServe(CustomerServe customerServe){
        customerServeService.updateCustomerServe(customerServe);
        return success("添加服务成功");
    }

    /**
     * 打开服务处理
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addCustomerServeAssignPage")
    public String addCustomerServeAssignPage(Integer id, Model model){
        //通过id'查询服务记录 并设置到请求域中
        model.addAttribute("customerServe",customerServeService.selectByPrimaryKey(id));
        return "customerServe/customer_serve_assign_add";
    }


    /**
     * 打开服务处理
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addCustomerServeProcePage")
    public String addCustomerServeProcePage(Integer id, Model model){
        //通过id'查询服务记录 并设置到请求域中
        model.addAttribute("customerServe",customerServeService.selectByPrimaryKey(id));
        return "customerServe/customer_serve_proce_add";
    }

    /**
     * 打开服务反馈
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addCustomerServeBackPage")
    public String addCustomerServeBackPage(Integer id, Model model){
        //通过id'查询服务记录 并设置到请求域中
        model.addAttribute("customerServe",customerServeService.selectByPrimaryKey(id));
        return "customerServe/customer_serve_feed_back_add";
    }
}
