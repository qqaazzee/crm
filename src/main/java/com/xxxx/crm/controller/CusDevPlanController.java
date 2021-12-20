package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.CusDevPlanService;
import com.xxxx.crm.service.SakeChanceService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("cus_dev_plan")
@Controller
public class CusDevPlanController  extends BaseController {

    @Resource
    private SakeChanceService sakeChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;


    /**
     * 进入开发界面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "cusDevPlan/cus_dev_plan";
    }



    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer id ,Model model){
        //通过id
        //将对象设置到请求域中
        model.addAttribute("saleChance",sakeChanceService.selectByPrimaryKey(id));
        return "cusDevPlan/cus_dev_plan_data";
    }

    /**
     * 客户开发计划数据查询(分页多条件查询)
     *  如果flag的值不为空 且值为1 则表示当前查询的是客户开发计划 否则查询营销机会数据
     *
     * @param cusDevPlanQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String , Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery){


        return cusDevPlanService.queryCusDevPLanByParams(cusDevPlanQuery);

    }


    /**
     * 添加计划项
     * @param cusDevPlan
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("计划向添加成功");
    }


    /**
     * 更新计划项
     * @param cusDevPlan
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划向更新成功");
    }

    /**
     * 进入添加或修改计划项的页面
     * @return
     */
    @RequestMapping("ToAddOrUpdateCusDevPlanPage")
    public  String ToAddOrUpdateCusDevPlanPage(Integer sId,HttpServletRequest request,Integer id){
        //将营销机会ID设置到请求域中 给计划项页面获取
        request.setAttribute("sId",sId);
        //同福哦计划项ID查询记录
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        //将计划项数据设置到请求域中
        request.setAttribute("cusDevPlan",cusDevPlan);
        return "cusDevPlan/add_update";
    }


    /**
     * 更新计划项
     * @param id
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划向更新成功");
    }
}
