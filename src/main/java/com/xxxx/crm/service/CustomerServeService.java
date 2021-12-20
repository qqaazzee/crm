package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.dao.CustomerServeMapper;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.enums.CustomerServeStatus;
import com.xxxx.crm.query.CustomerServeQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CustomerReprieve;
import com.xxxx.crm.vo.CustomerServe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerServeService extends BaseService<CustomerServe,Integer> {

    @Resource
    private CustomerServeMapper customerServeMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private UserMapper userMapper;

    public Map<String ,Object> queryCustomerServeList(CustomerServeQuery customerServeQuery){

        Map<String ,Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerServeQuery.getPage(),customerServeQuery.getLimit());
        //得到对应分页对象
        PageInfo<CustomerServe> pageInfo = new PageInfo<>(customerServeMapper.selectByParams(customerServeQuery));

        //设置map对象
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());

        return map;
    }

    /**
     * 添加服务操作
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerServe(CustomerServe customerServe) {
        //1,参数校验
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getCustomer()),"客户名不能为空");
        AssertUtil.isTrue(customerMapper.queryCustomerByName(customerServe.getCustomer()) == null , "客户不存在");

        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServeType()),"请选择服务类型");

        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceRequest()),"服务请求内容不能为空");

        //2.设置参数默认值
        customerServe.setState(CustomerServeStatus.CREATED.getState());
        customerServe.setIsValid(1);
        customerServe.setCreateDate(new Date());
        customerServe.setUpdateDate(new Date());

        //3.执行添加操作 判断受影响的行数

        AssertUtil.isTrue(customerServeMapper.insertSelective(customerServe) <1,"添加失败");
    }

    /**
     * 服务分配/服务处理/服务反馈
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerServe(CustomerServe customerServe){
        //客户服务ID 非空且记录存在
        AssertUtil.isTrue(customerServe.getId() == null || customerServeMapper.selectByPrimaryKey(customerServe.getId())==null,"待更新记录不存在");

        //判断客户服务的服务状态
        if (CustomerServeStatus.ASSIGNED.getState().equals(customerServe.getState())) {
            //服务分配操作
            // 分配人 非空 分配用户记录存在
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getAssigner()),"待分配用户不能为空");
            AssertUtil.isTrue(userMapper.selectByPrimaryKey(Integer.parseInt(customerServe.getAssigner())) == null,"待分配记录不存在");
            customerServe.setAssignTime(new Date());

        }else if (CustomerServeStatus.PROCED.getState().equals(customerServe.getState())) {
            //服务处理操作
            // 服务处理内容 非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProce()),"服务处理内容不能为空");
            // 服务处理时间 系统当前时间
            customerServe.setServiceProceTime(new Date());
            //更新时间 系统当前时间

        }else if (CustomerServeStatus.FEED_BACK.getState().equals(customerServe.getState())){
            //服务反馈操作
            //服务反馈内容 非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProceResult()),"服务反馈内容不能为空");
            //服务满意度 非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getSatisfaction()),"请选择服务反馈满意度");
            //服务状态
            customerServe.setState(CustomerServeStatus.ARCHIVED.getState());
            //设置为 服务归档状态


        }

        //更新时间
        customerServe.setUpdateDate(new Date());

        //执行更新操作
        AssertUtil.isTrue(customerServeMapper.updateByPrimaryKeySelective(customerServe)<1,"服务更新失败");

    }

}
