package com.github.diego.pacheco.sandbox.java.spring.boot.gatling;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.diego.pacheco.sandbox.java.spring.boot.gatling.ActivateJstack.ExecuteJStackTask;

@Controller
@EnableAutoConfiguration
@SuppressWarnings("deprecation")
public class MainResource {
	ExecuteJStackTask ste = null;
	
	public MainResource() {
		this.ste = new ExecuteJStackTask("C:\\Program Files\\Java\\jdk1.8.0_51\\bin\\jstack.exe", 20);
		ste.startScheduleTask();
	}

	
	@RequestMapping(value="/giveme",method=RequestMethod.POST)
    @ResponseBody
    String home(@RequestHeader HttpHeaders headers){
		Thread.currentThread().setName("Thread attending /giveme");
		 String result = "Hello World California :-) " + new Date().toString();
		 System.out.println(result);
         return result;
    }
	
	private void randomServerCongestion(){
		Random r = new Random();
		try {
			Thread.sleep(r.nextInt(30));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/token/{token}")
    @ResponseBody
    String receiveAndPrint(@PathVariable String token, @RequestHeader HttpHeaders headers){
		Thread.currentThread().setName("Thread attending /token/{token}");
		String result = "I got your token: " + token;
		System.out.println(result);
        return result; 
    }
	
	@Bean
	FilterRegistrationBean responseFilter() {
		return new FilterRegistrationBean(new Filter() {
			public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
				HttpServletResponse response = (HttpServletResponse) res;
				response.setHeader("Access-Control-Allow-Methods","POST,GET,OPTIONS,DELETE");
				response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
				response.setHeader("Token", "token-" + new Date().getMinutes() + "" + new Date().getSeconds() );
				chain.doFilter(req, res);
			}
			public void init(FilterConfig filterConfig) {}
			public void destroy() {}
		});
	}
	
	@Bean
	FilterRegistrationBean performanceFilter() {
		return new FilterRegistrationBean(new Filter() {
			public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
				long receiveRequestTime = System.currentTimeMillis();
				chain.doFilter(req, res);
				randomServerCongestion();
				ste.incThrughput(System.currentTimeMillis() - receiveRequestTime);
			}
			public void init(FilterConfig filterConfig) {}
			public void destroy() {}
		});
	}

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MainResource.class, args);
    }
}
