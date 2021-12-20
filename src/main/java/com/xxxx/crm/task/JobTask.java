package com.xxxx.crm.task;


import com.xxxx.crm.service.CustomerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class JobTask {


    @Resource
    private CustomerService customerService;


    //没五秒执行一次
//    @Scheduled(cron = "0/2 * * * * ?")
    public void jon(){
        System.out.println("定时任务开始执行"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        //调用需要被定时执行的方法
        customerService.updateCustomerState();
    }

}
