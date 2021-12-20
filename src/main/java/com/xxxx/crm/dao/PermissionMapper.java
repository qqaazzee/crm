package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {


    /**
     * 查询角色Id查询权限记录
     * @param roleId
     * @return
     */
    Integer countPermissionByRoleId(Integer roleId);

    /**
     * 查询角色Id删除权限记录
     * @param roleId
     */
    void deletePermissionByRoleId(Integer roleId);

    /**
     * 查询角色拥有的所有资源id的集合
     * @param roleId
     * @return
     */
    List<Integer> queryRoleHasModuleIdsByRoleId(Integer roleId);

    /**
     * 通过查询用户ID拥有的角色 角色拥有的资源 得到用户拥有的资源列表 (资源权限码)
     * @param userId
     * @return
     */
    List<String> queryUserHasROleHasPermissionByUserId(Integer userId);

    //通过资源Id查询权限记录
    Integer countPermissionByModuleId(Integer id);

    //通过资源id删除权限记录
    Integer deletePermissionByModule(Integer id);
}
