package com.xxxx.crm.service;

import com.github.pagehelper.util.StringUtil;
import com.sun.el.parser.AstAssign;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {

      @Resource
      private UserMapper userMapper;

      @Resource
      private UserRoleMapper userRoleMapper;

      /**
       * 用户登录
       * @param userName
       * @param userPwd
       */
      public UserModel userLogin(String userName,String userPwd){
            //参数判断 判断用户姓名和密码
            checkLoginParams(userName,userPwd);

            //调用数据访问层 通过用户名查询用户记录 返回用户对象
            User user = userMapper.queryUserByName(userName);

            //判断用户对象 是否为空
            AssertUtil.isTrue(user==null,"用户姓名不存在");

            //判断密码
            checkUserPwd(userPwd,user.getUserPwd());
      
            //返回构建用户对象
            return buildUserInfo(user);


            
      }


      /**
       * 修改密码
       * 1 通过id查询用户记录 返回用户对象
       * 2 参数校验
       * 3 设置用户的新密码
       * 4 执行更新 判断受影响的行数
       * @param userId
       * @param oldPwd
       * @param newPwd
       * @param repeatPwd
       */
      @Transactional(propagation = Propagation.REQUIRED)
      public void updatePassword(Integer userId,String oldPwd,String newPwd,String repeatPwd){
            //通过id查询用户记录 返回用户对象
            User user = userMapper.selectByPrimaryKey(userId);
            //判断用户是否存在
            AssertUtil.isTrue(null == user,"待更新记录不存在");

            //参数校验
            checkPasswordParams(user,oldPwd,newPwd,repeatPwd);
            System.out.println(user);
            //设置用户新密码
            user.setUserPwd(Md5Util.encode(newPwd));

            //执行更新 判断受影响的行数
            Integer integer = userMapper.updateByPrimaryKeySelective(user);

            AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败");

      }

      /**
       * 修改密码验证
       * @param user
       * @param oldPwd
       * @param newPwd
       * @param repeatPwd
       */
      private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
            System.out.println(oldPwd+""+newPwd+""+repeatPwd);
            //判断原始密码是否为空
            AssertUtil.isTrue(StringUtils.isBlank(oldPwd),"原始密码不能为空");
            //判断原始密码是否正确
            AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)),"原始密码不正确");

            //判断新密码是否为空
            AssertUtil.isTrue(StringUtils.isBlank(newPwd),"新密码不能为空");
            //判断新密码是否与旧密码一致
            AssertUtil.isTrue(oldPwd.equals(newPwd),"新密码不能与原始密码相同");

            //判断确认密码是否为空
            AssertUtil.isTrue(StringUtils.isBlank(repeatPwd),"确认密码不能为空");
            //判断确认密码是否与新密码一致
            AssertUtil.isTrue(!newPwd.equals(repeatPwd),"确认密码与新密码不一致");



      }

      /**
       * 构建需要返回给客户端的用户对象
       * @param user
       */
      private UserModel buildUserInfo(User user) {
            UserModel userModel = new UserModel();
//            userModel.setUserId(user.getId());
            userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
            userModel.setUserName(user.getUserName());
            userModel.setTrueName(user.getTrueName());
            return userModel;
      }

      /**
       * 密码判断
       * @param
       * @param pwd
       */
      private void checkUserPwd(String userPwd, String pwd) {
            //将客户端传递过来的密码加密
             userPwd = Md5Util.encode(userPwd);
            System.out.println(userPwd);
            System.out.println(pwd);
            //判断密码是否相等
            AssertUtil.isTrue(!userPwd.equals(pwd),"用户密码不正确");

      }

      /**
       * 参数判断
       * @param userName
       * @param
       */
      private void checkLoginParams(String userName, String userPwd) {
            //验证用户名
            AssertUtil.isTrue(StringUtils.isBlank(userName),"用户姓名不能为空");
            //验证用户密码
            AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");

      }

      /**
       * 查询所有的销售人员
       * @return
       */

      /**
       * 查询所有管理用户
       * @return
       */
      public List<Map<String ,Object>> queryAllSales(){
            return userMapper.queryAllSales();

      }

      /**
       * 添加管理用户
       * 1.参数校验
       * 2.设置参数的默认值
       * 3.执行添加奥做 判断受影响的行数
       */
      @Transactional(propagation = Propagation.REQUIRED)
      public void addUser(User user){
            //1.参数校验
            checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),null);

            //2.设置参数的默认值
            user.setIsValid(1);
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            //设置默认密码
            user.setUserPwd(Md5Util.encode("123456"));
            //3.判断受影响的行数
            AssertUtil.isTrue(userMapper.insertSelective(user) < 1,"添加用户失败");

            //用户角色关联

            /**
             * 用户Id
             * userid
             * 角色Id
             * roleIds
             */
            relationUserRole(user.getId(),user.getRoleIds());
      }

      /**
       * 用户角色关联
       *   添加操作
       *         原始角色不存在
       *             1.不添加新的角色记录 不操作用户角色表
       *             2.添加新的角色记录   给指定用户绑定相关的角色记录
       *   更新操作
       *        原始角色不存在
       *             1.不添加新的角色记录  不操作用户角色表
       *             2.添加新的角色记录    给指定用户绑定相关的角色记录
       *        原始角色存在
       *             1.添加新的角色记录    判断已有的角色记录不添加  添加没有的角色记录
       *             2.清空所有的角色记录   删除用户绑定角色记录
       *             3.移除不疯魔角色记录   删除不存在的角色记录 存在的角色记录保留
       *             4.移除部门角色 添加新的角色  删除不存在的角色记录 存在的角色记录保留 添加新的角色
       *
       *
       *   如何进行角色分配???
       *          判断用户对应的角色记录存在 先将原有的角色记录删除 再添加新的角色记录
       *   删除操作
       *          删除指定用户绑定的角色记录
       */

      /**
       * 添加新用户 设置权限
       * @param userId
       * @param roleIds
       */
      private void relationUserRole(Integer userId, String roleIds) {

            //通过用户ID查询角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            //判断角色记录是否存在
            if (count > 0) {
                  //如果角色记录存在 则删除该用户对应的角色记录
                  AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,"用户角色分配失败");

            }
            //判断角色ID是否存在 如果存在 则添加该用户对应的角色记录
            if (StringUtils.isNoneBlank(roleIds)) {
                  //将用户角色数据设置到集合中 执行批量添加
                  List<UserRole> userRolesList = new ArrayList<>();
                  //将角色ID字符串转成数组
                  String[] roleIdArray = roleIds.split(",");
                  //遍历数据 得到对应的用户角色记录 并设置到集合中
                  for(String roleId : roleIdArray){
                        UserRole userRole = new UserRole();
                        userRole.setRoleId(Integer.parseInt(roleId));
                        userRole.setUserId(userId);
                        userRole.setCreateDate(new Date());
                        userRole.setUpdateDate(new Date());
                        //设置到集合中
                        userRolesList.add(userRole);
                  }
                  //批量添加用户角色记录
                  AssertUtil.isTrue(userRoleMapper.insertBatch(userRolesList)!= userRolesList.size(),"用户角色分配失败!");

            }




      }


      /**
       * 更新管理用户
       * 1.参数校验
       * 2.设置参数的默认值
       * 3.执行更新操作
       *
       */
      @Transactional(propagation = Propagation.REQUIRED)
      public void updateUser(User user){
            //判断用ID是否为空 且数据存在
            AssertUtil.isTrue( null == user.getId(),"待更新记录不存在");
            //通过id查询数据
            User temp = userMapper.selectByPrimaryKey(user.getId());
            //判断是否存在
            AssertUtil.isTrue(null == temp,"待更新记录不存在");
            //1.参数校验
            checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),user.getId());
            //2.设置默认值
            user.setUpdateDate(new Date());

            //3.执行更新操作 判断受影响的行数
            AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1,"用户更新失败");

            /**
             * 用户Id
             * userid
             * 角色Id
             * roleIds
             */
            relationUserRole(user.getId(),user.getRoleIds());
      }

      /**
       * 参数校验
       * @param userName
       * @param email
       * @param phone
       * @param userid
       */
      private void checkUserParams(String userName, String email, String phone,Integer userid) {
            //判断用户名是否为空  唯一性
            AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
            User temp = userMapper.queryUserByName(userName);
            /**
             * 如果用户对象为空 则表示用户名可用 如果用户对象不为空则表示不可用
             * 如果是添加操作 数据库中无数据 只要通过名称查到数据 则表示用户被占用
             * 如果是修改操作 数据库中有对应的记录 通过用户名查到数据 可能是当前记录本身 也可能是别的记录
             * 如果用户名存在 且与当前修改记录不是同一个 则表示其他记录占用了该用户名 不可用
             */
            //temp不等于空 为true  temp等于空 false
            AssertUtil.isTrue(null != temp && !(temp.getId().equals(userid)),"用户名已存在 请重新输入");
            //判断邮箱是否为空
            AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空");
            //判断手机号是否为空
            AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");
            //手机号 格式判断
            AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号格式不正确");


      }


      /**
       * 删除操作
       * @param ids
       */
      @Transactional(propagation = Propagation.REQUIRED)
      public void deleteByIds(Integer[] ids) {
            //判断ids是否为空 长度是否大于0
            AssertUtil.isTrue(ids == null || ids.length == 0,"待删除记录不存在");
            //执行删除操作 判断受影响
            AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length,"用户删除失败");

            //遍历用户ID的数组
            for(Integer userId : ids){
                  //通过用户ID查询对用的用户角色记录
                  Integer count  = userRoleMapper.countUserRoleByUserId(userId);
                  //判断用户角色记录是否存在
                  if(count>0){
                        //通过用户ID删除对应的用户角色记录
                        AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,"删除角色失败");
                  }
            }
      }


      /**
       * 查询所有的客户经理
       * @return
       */
    public List<Map<String, Object>> queryAllCustomerManager() {
          return userMapper.queryAllCustomerManager();
    }
}
