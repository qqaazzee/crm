package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.AuthException;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /**
     * 异常处理方法
     *  方法的返回值
     *      1 返回视图
     *      2 返回数据
     *  如何判断方法的返回值?
     *      通过方法上是否声明@ResponsesBody注解
     *          如果为声明 则表示返回视图
     *          如果声明了 则表示返回数据
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handler
     * @param ex
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception ex) {

        /**
         * 非法请求拦截
         *  判断是否抛出未登录异常
         *      如果抛出异常 则要求用户登录 重定向跳转到登录页面
         */

        if (ex instanceof NoLoginException) {
            //重定向到登录页面
            ModelAndView mv = new ModelAndView("redirect:/index");
            return mv;
        }


        /**
         * 设置默认异常处理(返回视图)
         */

        ModelAndView modelAndView = new ModelAndView("error");
        //设置异常信息
        modelAndView.addObject("code",500);
        modelAndView.addObject("msg","系统异常 请重试...");

        //判断HandlerMethod
        if(handler instanceof HandlerMethod){
            //类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取方法上声明的@ResponseBody
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            //判断ResponseBody对象是否为空(如果对象为空 则表示返回的视图 如果不为空 则表示返回的是数据)
            if (responseBody == null) {
                /**
                 * 方法返回视图
                 */
                if (ex instanceof ParamsException) {
                    ParamsException p = (ParamsException) ex;
                    //设置异常信息
                    modelAndView.addObject("code",p.getCode());
                    modelAndView.addObject("msg",p.getMsg());

                } else  if (ex instanceof AuthException) {
                    AuthException a = (AuthException) ex;
                    //设置异常信息
                    modelAndView.addObject("code",a.getCode());
                    modelAndView.addObject("msg",a.getMsg());

                }

                return modelAndView;
            }else {
                /**
                 * 方法返回数据
                 */
                //设置默认的异常处理
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("异常 请重试...");

                //判断异常类型是否自定义异常
                if(ex instanceof ParamsException){
                    ParamsException p = (ParamsException) ex;
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());

                } else if(ex instanceof AuthException){   //认证异常
                    AuthException a = (AuthException) ex;
                    resultInfo.setCode(a.getCode());
                    resultInfo.setMsg(a.getMsg());

                }
                //设置响应类型及编码格式(响应JSON格式的数据)
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                //得到字符输出流
                PrintWriter out = null;

                try {
                    out = httpServletResponse.getWriter();
                    //将需要返回的对象JOSN格式的字符
                    String json = JSON.toJSONString(resultInfo);
                    //输出数据
                    out.write(json);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(out !=null){
                        out.close();
                    }
                }

                return null;
            }
        }



        return modelAndView;
    }
}
