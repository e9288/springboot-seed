package com.iandna.gateway.config.interceptor;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.iandna.gateway.config.exception.CommonJsonException;
import com.iandna.gateway.config.model.CustomErrorProperties;
import com.iandna.gateway.util.AriaUtil;
import com.iandna.gateway.util.DateUtil;

@Component
public class CommonInterceptor implements HandlerInterceptor {
	
	private static final Log logger = LogFactory.getLog (CommonInterceptor.class);
	
	@Value("${gateway.appKey}")
	private String gatewayAppkey;
	
	@Autowired
	CustomErrorProperties eProps;
	
	public CommonInterceptor() {
		logger.debug("commonInterceptor created.");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.info("preHandle...");
		boolean flag = reqeustValid(request);
		
		if(!flag) {
			logger.debug("DEBUG eProps null");
			logger.info("INFO eProps null");
			throw new CommonJsonException("E999", eProps.getE999());
		}
		
		logger.info("preHandle...end");
		return flag;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, @Nullable Exception arg3) throws Exception {
	}
	
	
	public boolean reqeustValid(HttpServletRequest request) throws InvalidKeyException, UnsupportedEncodingException {
		boolean passFlag = false;
		Cookie authToken = WebUtils.getCookie(request, "authToken");
		
		if(authToken == null) {
			return passFlag;
		}
		
		String currentDateTime = DateUtil.getCurrentDateTime12();
		String beforeDate = DateUtil.getDate(-1).substring(2);
		String currentTime = currentDateTime.substring(6,10);
		String currentDate = currentDateTime.substring(0,6);
		switch(currentTime) {
		case "2358":
			if(AriaUtil.Encrypt(gatewayAppkey + currentDate).equals(authToken.getValue())
				|| AriaUtil.Encrypt(gatewayAppkey + beforeDate).equals(authToken.getValue())) {
				passFlag = true;
			}
			break;
		case "2359":
			if(AriaUtil.Encrypt(gatewayAppkey + currentDate).equals(authToken.getValue()) 
				|| AriaUtil.Encrypt(gatewayAppkey + beforeDate).equals(authToken.getValue())) {
				passFlag = true;
			}
			break;
		default:
			if(AriaUtil.Encrypt(gatewayAppkey + currentDate).equals(authToken.getValue())) {
				passFlag = true;
			}
		}
		return passFlag;
	}
	
}
