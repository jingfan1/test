package com.wangchao;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages="com.wangchao")
@EnableTransactionManagement(proxyTargetClass =true)
//druid注解使用，否则报错
@ServletComponentScan
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
    private static String ADMIN_SESSION_NAME = "adminUserSession";
    private static String FRONT_SESSION_NAME = "frontUserSession";

    //多线程
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(executor.getCorePoolSize() * 10);
        executor.setWaitForTasksToCompleteOnShutdown(false);
        return executor;
    }
   
    //拦截器
    @Bean
    public FilterRegistrationBean registFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response,
                                 FilterChain filterChain) throws IOException, ServletException {
                System.out.println("in filter");
                HttpServletRequest hrequest = (HttpServletRequest) request;
                HttpServletResponse hresponse = (HttpServletResponse) response;

                // 请求的URI
                String uri = hrequest.getRequestURI();
                // Referer从哪个页面链接过来的
                String referer = hrequest.getHeader("Referer");

                try {
                    Integer memberSession = null;
                    if (uri.contains("front")) {
                        memberSession = (Integer) hrequest.getSession()
                            .getAttribute(FRONT_SESSION_NAME);
                        System.out.println("apisesson=" + memberSession);
                    } else if (uri.contains("admin")) {
                        memberSession = (Integer) hrequest.getSession()
                            .getAttribute(ADMIN_SESSION_NAME);
                        System.out.println("adminsesson=" + memberSession);
                    } else if (uri.contains("wx")) {
                        memberSession = (Integer) hrequest.getSession()
                            .getAttribute(FRONT_SESSION_NAME);
                        System.out.println("wxsesson=" + memberSession);
                    }
                    String source = (String) hrequest.getParameter("source");
                    // 用户未登录时，跳转到登录页面
                    String path = hrequest.getContextPath();
                    // 用户已经登录
                    if (memberSession != null) {
                        if (uri.contains("wx")) {
//                            hresponse.sendRedirect(
//                                path + uri.replace("wlsd/", "") + "?source=" + source);
//                            if (Integer.parseInt(source) == 1) {
//                                hresponse.sendRedirect(path + "/#/");
//                            } else if (Integer.parseInt(source) == 2) {
//                                hresponse.sendRedirect(path + "/#/authorized?source=2");
//                            } else if (Integer.parseInt(source) == 3) {
//                                hresponse.sendRedirect(path + "/#/license?source=3");
//                            }
                        } else {
                            filterChain.doFilter(request, response);
                            return;
                        }
                    } else {
                        //                        filterChain.doFilter(request, response);
                        //                        return;
                        System.out.println("source=" + source);
                        //前台
                        if (uri.contains("front")) {
                            if (source == null || "".equals(source)) {
                                hresponse.sendRedirect(path + "/api/dologin");
                            } else {
                                hresponse.sendRedirect(path + "/api/wxlogin?source=" + source);
                            }
                        //后台
                        } else if (uri.contains("admin")) {
                            hresponse.sendRedirect(path + "/api/dologin");
                        //微信
                        } else if (uri.contains("wx")) {
                            hresponse.sendRedirect(path + "/api/wxlogin?source=" + source);
                        }
                        
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void destroy() {
            }

        });
        registration.addUrlPatterns("/front/*");//================*******************************
        registration.setOrder(1);
        registration.addUrlPatterns("/admin/*");
        registration.setOrder(2);
        registration.addUrlPatterns("/wx/*");
        registration.setOrder(3);
        return registration;
    }

    @SuppressWarnings("serial")
    @Bean
    public ServletRegistrationBean registServlet() {
        ServletRegistrationBean servletRegist = new ServletRegistrationBean();
        servletRegist.setServlet(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req,
                                 HttpServletResponse resp) throws ServletException, IOException {
                resp.getWriter().write("inside servlet");
            }
        });
        servletRegist.addUrlMappings("/registedServlet");
        return servletRegist;
    }
}
