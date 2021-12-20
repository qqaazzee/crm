package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
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
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private ModuleMapper moduleMapper;

    /**
     * 查询所有的角色列表
     * @return
     */
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRole(userId);
    }

    /**
     * 添加角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        //参数校验
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名不能为空");
        //通过角色名称查询角色记录
        Role temp = roleMapper.selectByRoleName(role.getRoleName());
        //判断角色记录是否存在(添加操作时,如果角色记录存在则表示名称不可用)
        AssertUtil.isTrue(temp != null,"角色名称已存在 并重新输入");

        //设置参数的默认值
        //是否有效
        role.setIsValid(1);
        //创建时间
        role.setCreateDate(new Date());
        //更新时间
        role.setUpdateDate(new Date());

        //更新数据
    AssertUtil.isTrue(roleMapper.insertSelective(role) < 1,"添加角色失败!");

    }

    /**
     * 修改角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        //参数校验
        //角色ID         非空 且数据存在
        AssertUtil.isTrue(null == role.getId(),"待更新记录不存在");
        //设置参数的默认值
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        //执行更新操作 判断受影响的行数
        AssertUtil.isTrue(null == temp,"待更新记录不存在");

        //角色名称 非空 名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不能为空");
        //通过角色名称查询角色记录
        temp = roleMapper.selectByRoleName(role.getRoleName());
        // 判断角色记录是否存在 (如果不存在 表示可使用 如果存在 且角色ID与当前更新的角色ID不一致 表示角色名称不可用)
        AssertUtil.isTrue(null != temp && (!temp.getId().equals(role.getId())),"角色名称已存在!");

        //设置参数默认值
        role.setUpdateDate(new Date());

        //执行更新操作 判断受影响的行数
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"修改角色失败!");

    }

    /**
     * 删除角色
     * @param roleId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId){
        //判断角色Id查询角色记录
        AssertUtil.isTrue(null == roleId,"待删除记录不存在");
        //通过角色ID查询角色记录
        Role role = roleMapper.selectByPrimaryKey(roleId);
        //判断角色记录是否存在
        AssertUtil.isTrue(null == role,"待删除记录不存在");
        //设置删除状态
        role.setIsValid(0);
        role.setUpdateDate(new Date());

        //执行更新操作
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色删除失败!");

    }

    /**
     * 角色授权
     *  将对应的角色id与资源id添加到对应的权限表中
     *      直接添加 不合适 会出现重复的资源权限数据
     *       推荐使用
     *          先将已有的权限记录删除 再将需要设置的权限记录添加
     *          1.通过角色id查询对应的权限记录
     *          2.如果权限记录存在 则删除对应的角色拥有的权限记录
     *          3.如果有权限即记录 则添加
     *
     * @param roleId
     * @param mids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mids) {
        //1.通过角色ID查询对应的权限记录
        Integer count = permissionMapper.countPermissionByRoleId(roleId);
        // 2.如果权限记录存在 则删除对应的角色拥有的权限记录
        if (count>0) {
            //删除权限记录
            permissionMapper.deletePermissionByRoleId(roleId);
        }
        //3.如果有权限记录 则添加权限记录
        if (mids != null && mids.length > 0) {
            //定义Permission的集合
            List<Permission> permissionList = new ArrayList<>();

            //遍历资源ID数组
            for(Integer mId:mids){
                Permission permission = new Permission();
                permission.setModuleId(mId);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mId).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());

                //将对象设置到集合
                permissionList.add(permission);

            }

            //执行批量添加操作 判断受影响的行数
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"角色授权失败");
        }
    }
}


