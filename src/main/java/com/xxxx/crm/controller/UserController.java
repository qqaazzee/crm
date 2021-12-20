package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;
    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping ("/login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){

        ResultInfo resultInfo = new ResultInfo();
        System.out.println(userName+userPwd);
        //调用service层登录方法
        UserModel userModel = userService.userLogin(userName, userPwd);

        //设置resultInfo的result的值 (将数据返回给请求)
        resultInfo.setResult(userModel);
//
//        //通过try catch捕获service层的异常 如果service层输出异常 则表示登录失败 否则登录失败
//        try {
//
//
//        }catch (ParamsException p){
//            resultInfo.setCode(p.getCode());
//            resultInfo.setMsg(p.getMsg());
//            p.printStackTrace();
//        }catch (Exception e){
//            resultInfo.setCode(500);
//            resultInfo.setMsg("登录失败");
//        }

        return resultInfo;
    }


    @RequestMapping("toPasswordPage")
    public String toPasswordPage() {
        return "user/password";
    }

    @GetMapping("/queryUserList")
    @ResponseBody
    public User queryUserList(){
        User admin = userMapper.queryUserByName("admin");
        System.out.println(admin);
        return admin;
    }

    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPassword, String newPassword, String repeatPassword){
        System.out.println(oldPassword+newPassword+repeatPassword);

        ResultInfo resultInfo = new ResultInfo();
        //获取cookie中的userid
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层修改棉麻方法
        userService.updatePassword(userId,oldPassword,newPassword,repeatPassword);

//        try {
//
//        } catch (ParamsException p) {
//            resultInfo.setCode(p.getCode());
//            resultInfo.setMsg(p.getMsg());
//            p.printStackTrace();
//        }catch (Exception e){
//            resultInfo.setCode(500);
//            resultInfo.setMsg("修改密码失败");
//            e.printStackTrace();
//        }

        return resultInfo;
    }


    /**
     * 查询所有的销售人员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }


    @RequestMapping("list")
    @ResponseBody
    /**
     * 分页多条件查询用户列表
     */
    public Map<String,Object> selectByParams(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);

    }

    /**
     * 进入管理用户列表页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 添加操作
     * @param user
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功");
    }

    /**
     * 更新操作
     * @param user
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户修改成功成功");
    }

    /**
     * 打开添加或修改用户页面
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id,HttpServletRequest request){

        //判断id是否为空 不为空表示为更新操作 查询用户对象
        if(id != null){
            //通过id查询用户对象
            User user = userService.selectByPrimaryKey(id);
            request.setAttribute("userInfo",user);
        }
        return "user/add_update";
    }


    /**
     * 用户删除
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){

        userService.deleteByIds(ids);
        return success("用户删除成功");
    }

    /**
     * 查询所有的销售人员
     * @return
     */
    @RequestMapping("queryAllCustomerManager")
    @ResponseBody
    public List<Map<String,Object>> queryAllCustomerManager(){
        return userService.queryAllCustomerManager();
    }
}
