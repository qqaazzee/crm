package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CustomerLossMapper;
import com.xxxx.crm.dao.CustomerMapper;
import com.xxxx.crm.dao.CustomerOrderMapper;
import com.xxxx.crm.query.CustomerQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.Customer;
import com.xxxx.crm.vo.CustomerLoss;
import com.xxxx.crm.vo.CustomerOrder;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CustomerService extends BaseService<Customer,Integer> {

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private CustomerOrderMapper customerOrderMapper;

    @Resource
    private CustomerLossMapper customerLossMapper;


    /**
     * 多条件分页查询客户
     * @param customerQuery
     * @return
     */
    public Map<String,Object> queryCustomerByParams(CustomerQuery customerQuery){

        Map<String ,Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerQuery.getPage(),customerQuery.getLimit());
        //得到对应分页对象
        PageInfo<Customer> pageInfo = new PageInfo<>(customerMapper.selectByParams(customerQuery));

        //设置map对象
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());


        return map;

    }

    /**
     * 添加客户
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomer(Customer customer){
        //1.参数校验
        checkCustomerParams(customer.getName(),customer.getLegalPerson(),customer.getPhone());
        //判断客户名的唯一性
        Customer temp = customerMapper.queryCustomerByName(customer.getName());
        //判断名称是否存在
        AssertUtil.isTrue(null != temp,"客户名称已存在 请重复输入!");

        //2.设置参数的默认值
        customer.setIsValid(1);
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
        customer.setState(0);
        //客户编号
        String customerId = "KH" + System.currentTimeMillis();
        customer.setCustomerId(customerId);
        //3.执行添加操作 判断受影响的行数
        AssertUtil.isTrue(customerMapper.insertSelective(customer) <1,"添加客户信息失败");
    }

    /**
     * 修改客户
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomer(Customer customer){
        //1.参数校验
        AssertUtil.isTrue(null == customer.getId(),"待更新记录不存在");
        //通过客户Id查询客户记录
        Customer temp = customerMapper.selectByPrimaryKey(customer.getId());
        //判断用户记录
        AssertUtil.isTrue(null == temp,"待更新记录不存在");
        //参数校验
        checkCustomerParams(customer.getName(),customer.getLegalPerson(),customer.getPhone());
        //通过客户名查询客户记录
        temp = customerMapper.queryCustomerByName(customer.getName());
        //判断客户记录 是否存在 且客户ID是否与更新记录的ID一致
        AssertUtil.isTrue(null != temp && !(temp.getId().equals(customer.getId())),"客户名称已存在,请重新输入");
        //2.设置参数的默认值
        customer.setUpdateDate(new Date());

        //3.执行更新操作 判断受影响的行数
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer) < 1,"修改客户名称失败");
    }

    /**
     * 参数校验
     * @param name
     * @param legalPerson
     * @param phone
     */
    private void checkCustomerParams(String name, String legalPerson, String phone) {
        //客户名称 name 非空
        AssertUtil.isTrue(StringUtils.isBlank(name),"客户名称不能为空");
        //法人代表 非空
        AssertUtil.isTrue(StringUtils.isBlank(legalPerson),"法人代表不能为空");
        //手机号码 phone 非空 格式正确
//        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号码不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号码格式不正确");

    }

    /**
     * 删除客户信息
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCustomer(Integer id) {
        //判断id是否为空 数据是否存在
        AssertUtil.isTrue(null == id ,"待删除记录不存在");
        Customer customer = customerMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == customer,"待删除记录不存在" );
        //设置状态为失效
        customer.setIsValid(0);
        customer.setUpdateDate(new Date());

        //执行删除 判断受影响函数
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer) < 1,"删除客户失败");

    }

    /**
     * 更新客户的流失状态
     * 1.查询流失的客户数据
     * 2.将流失客户数据批量添加到客户流失表中
     * 3.批量更新客户的流失状态 0=正常客户 1=流失客户
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerState(){
        //更新客户的流失状态
        List<Customer> lossCustomersList = customerMapper.queryLossCustomers();

        // 2.将流失客户数据批量添加到客户流失表中
        //判断流失客户数据是否存在
        if(null !=lossCustomersList && lossCustomersList.size()>0){
            //定义流失客户的列表
            List<CustomerLoss> customerLosses= new ArrayList<CustomerLoss>();
            List<Integer> lossCusIds=new ArrayList<Integer>();
            //遍历查询到流失客户的数据
            lossCustomersList.forEach(customer->{
                //定义流失对象
                CustomerLoss customerLoss=new CustomerLoss();
                customerLoss.setCreateDate(new Date());
                customerLoss.setCusManager(customer.getCusManager());
                customerLoss.setCusName(customer.getName());
                customerLoss.setCusNo(customer.getCustomerId());
                customerLoss.setIsValid(1);
                //  设置客户流失状态为暂缓流失状态
                customerLoss.setState(0);
                //客户最后下单时间
                //通过客户Id查询最后一条订单记录
                CustomerOrder lastCustomerOrder = customerOrderMapper.queryLastCustomerOrderByCusId(customer.getId());
                if(null !=lastCustomerOrder){
                    customerLoss.setLastOrderTime(lastCustomerOrder.getOrderDate());
                }
                customerLoss.setUpdateDate(new Date());
                //将流失客户对象设置到对应的集合中
                customerLosses.add(customerLoss);
                //将流失客户的Id设置到对应的集合中
                lossCusIds.add(customer.getId());
            });
            //批量天机流失客户记录
            AssertUtil.isTrue(customerLossMapper.insertBatch(customerLosses)<customerLosses.size(),"客户流失数据流转失败!");

            AssertUtil.isTrue(customerMapper.updateCustomerStateByIds(lossCusIds)<lossCusIds.size(),"客户流失数据流转失败!");

        }
    }


    /**
     * 查询客户贡献分析
     * @param customerQuery
     * @return
     */
    public Map<String,Object>queryCustomerContributionByParams(CustomerQuery customerQuery){

        Map<String ,Object> map = new HashMap<>();

        //开启分页
        PageHelper.startPage(customerQuery.getPage(),customerQuery.getLimit());
        //得到对应分页对象
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<Map<String,Object>>(customerMapper.queryCustomerContributionByParams(customerQuery));

        //设置map对象
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());


        return map;

    }

    /**
     * 查询客户构成(折线图数据处理)
     * @return
     */
    public Map<String,Object>countCustomerMake(){
        Map<String,Object> map = new HashMap<>();
        //查询客户构成数据的列表
        List<Map<String,Object>> dataList = customerMapper.countCustomerMake();
        //折线图x轴 数组
        List<String> date1 = new ArrayList<>();
        //折线图y轴 数组
        List<Integer> date2 = new ArrayList<>();

        //判断数据列表 循环设置数据
        if (dataList != null && dataList.size()>0) {
            //遍历集合
            dataList.forEach(m ->{
                //获取level对应的数据 设置到x轴的集合中
                date1.add(m.get("level").toString());
                date2.add(Integer.parseInt(m.get("total").toString()));
            });
        }

        //将x轴的数据集合与y轴的数据集合 设hi到Map中
        map.put("date1",date1);
        map.put("date2",date2);


        return map;

    }

    /**
     * 饼状图
     * @return
     */
    public Map<String, Object> countCustomerMake02() {

        Map<String,Object> map = new HashMap<>();
        //查询客户构成数据的列表
        List<Map<String,Object>> dataList = customerMapper.countCustomerMake();
        //饼状图 数组(数组中是自负床)
        List<String> date1 = new ArrayList<>();
        //饼状图数据 数组(数组中是对象)
        List<Map<String,Object>> date2 = new ArrayList<>();

        //判断数据列表 循环设置数据
        if (dataList != null && dataList.size()>0) {
            //遍历集合
            dataList.forEach(m ->{
                date1.add(m.get("level").toString());
                //饼状图的数据 数组 (该数组是对象)
                Map<String,Object> dateMap = new HashMap<>();
                dateMap.put("name",m.get("level"));
                dateMap.put("value",m.get("total").toString());
                date2.add(dateMap);
            });
        }

        //将x轴的数据集合与y轴的数据集合 设hi到Map中
        map.put("date1",date1);
        map.put("date2",date2);


        return map;
    }
}
