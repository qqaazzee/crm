package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.CustomerLossMapper;
import com.xxxx.crm.dao.CustomerReprieveMapper;
import com.xxxx.crm.query.CustomerReprieveQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CustomerLoss;
import com.xxxx.crm.vo.CustomerReprieve;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerReprieveService extends BaseService<CustomerReprieve,Integer> {

    @Resource
    private CustomerReprieveMapper customerReprieveMapper;

    @Resource
    private CustomerLossMapper customerLossMapper;

    public Map<String,Object> queryCustomerReprieveByQuery(CustomerReprieveQuery customerReprieveQuery){

        Map<String ,Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerReprieveQuery.getPage(),customerReprieveQuery.getLimit());
        //得到对应分页对象
        PageInfo<CustomerReprieve> pageInfo = new PageInfo<>(customerReprieveMapper.selectByParams(customerReprieveQuery));

        //设置map对象
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());

        return map;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerPepr(CustomerReprieve customerReprieve) {

        //参数校验
        checkParams(customerReprieve.getLossId(),customerReprieve.getMeasure());
        //设置参数的默认值
        customerReprieve.setIsValid(1);
        customerReprieve.setCreateDate(new Date());
        customerReprieve.setUpdateDate(new Date());

        //执行添加操作
        AssertUtil.isTrue(customerReprieveMapper.insertSelective(customerReprieve)<1,"添加暂缓数据失败");
    }

    private void checkParams(Integer lossId, String measure) {

        AssertUtil.isTrue(null ==lossId || customerLossMapper.selectByPrimaryKey(lossId)== null,"流失客户记录不存在");

        AssertUtil.isTrue(StringUtils.isBlank(measure),"暂缓措施内容不能为空");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerPepr(CustomerReprieve customerReprieve) {

        //参数校验
        AssertUtil.isTrue(null == customerReprieve.getId() || customerReprieveMapper.selectByPrimaryKey(customerReprieve.getId()) == null ,"待更新记录不存在");
        checkParams(customerReprieve.getLossId(),customerReprieve.getMeasure());

        //设置参数默认值
        customerReprieve.setUpdateDate(new Date());

        //执行修改操作 判断受影响的行数
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve)<1,"修改数据失败");

    }

    /**
     * 删除暂缓数据
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void delteCustomerPepr(Integer id) {
        //判断id是否为空
        AssertUtil.isTrue(null == id,"待删除记录不存在");
        //通过id查询暂缓数据
        CustomerReprieve customerReprieve = customerReprieveMapper.selectByPrimaryKey(id);
        //判断数据是否存在
        AssertUtil.isTrue(null == customerReprieve,"待删除记录不存在");


        //设置isValid
        customerReprieve.setIsValid(0);
        customerReprieve.setUpdateDate(new Date());

        //执行更新操作 判断受影响的行数
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve) <1,"删除记录失败");


    }
}
