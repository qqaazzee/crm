package com.xxxx.crm.dao;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.model.TreeModule;
import com.xxxx.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    //查询所有的资源列表
    List<TreeModule> queryAllModules();

    //查询所有的资源数据
    List<Module> queryModuleList();

    //通过层级与模块名查询资源对象
    Object queryModuleByGadeAndModuleName(@Param("grade") Integer grade, @Param("moduleName") String moduleName);

    //通过层级与UR了查询资源对象
    Object queryModuleByGrandeAndUrl(@Param("grade")Integer grade, @Param("url")String url);

    //通过权限码查询资源对象
    Object queryModuleByOptvalue(String optValue);

    //通过指定资源是否存在子记录
    Integer queryModuleBuParentId(Integer id);
}
