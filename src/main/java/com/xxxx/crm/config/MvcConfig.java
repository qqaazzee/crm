package com.xxxx.crm.config;


import com.xxxx.crm.interceptor.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration  //配置类
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public NoLoginInterceptor noLoginInterceptor(){
        return new NoLoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //需要一个实现类拦截器功能的实例对象
        registry.addInterceptor(noLoginInterceptor())
                //设置要被拦截的资源
                .addPathPatterns("/**")
                //设置不要被拦截的资源
                .excludePathPatterns("/sale_chance/list","/index","/user/login","/css/**","/images/**","/js/**","/lib/**");
    }
}
