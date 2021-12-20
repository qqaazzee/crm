package com.xxxx.crm.controller;

import com.xxxx.crm.annoation.RequiredPermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SakeChanceService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SakeChanceService sakeChanceService;

    /**
     * 营销机会数据查询(分页多条件查询)  101001
     *   如果flag的值不为空 且值为1 则表示当前查询的是客户开发计划 否则查询营销机会数据
     *
     * @param saleChanceQuery
     * @return
     */

    @RequestMapping("list")
    @ResponseBody
    public Map<String , Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery,
                                                        Integer flag,HttpServletRequest request){
        //判断flag的值
        if(flag!=null && flag == 1){
            //查询客户开发计划
            //设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置指派人 (当前登录用户的ID)
            //从cookie中获取当前登录用户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);

        }

        return sakeChanceService.querySaleChanceByParams(saleChanceQuery);

    }

    /**
     * 进入营销机会管理页面 1010
     * @return
     */
    @RequestMapping("index")
    public String index () {
        return "saleChance/sale_chance";
    }


    /**
     * 添加营销机会  101002
     * @return
     */
    @RequiredPermission(code = "101002")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo aad(SaleChance saleChance, HttpServletRequest request){
        //从cookie中获取当前登录的名字
         String userName = CookieUtil.getCookieValue(request,"userName");
         //设置用户名到营销机会对象
        saleChance.setCreateMan(userName);
        // 调用Service层的添加方法
        sakeChanceService.addSaleChance(saleChance);
        return success("营销机会添加成功!");
    }


    /**
     * 更新营销机会 101004
     * @param saleChance
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo update(SaleChance saleChance){

        sakeChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功!");
    }

    //添加或者修改到营销机会数据页面
    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer sid, Model model){

        //判断sid是否为空
        if (sid != null) {
            //通过id查询营销机会数据
            SaleChance saleChance = sakeChanceService.selectByPrimaryKey(sid);
            //将数据设置到请求域中
            model.addAttribute("saleChance",saleChance);
        }


        return "saleChance/add_update";
    }


    /**
     * 删除营销机会
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        //调用Service层的删除方法
        sakeChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功");
    }

    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        sakeChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功");
    }
}