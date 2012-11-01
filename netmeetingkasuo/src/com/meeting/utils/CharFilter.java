package com.meeting.utils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 
 * ±àÂë¹ýÂËÆ÷
 * 
 */
public class CharFilter implements Filter {

	private static final String IE_CONTENT_TYPE = "application/x-www-form-urlencoded";
	private static final String FF_AJAX_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";
	private static final String XMLHTTP_REQUEST = "XMLHttpRequest";
	private static final String AJAX_CHARACTER_ENCODING_UTF8 = "UTF-8";
	private static final String NORMAL_CHARACTER_ENCODING_GBK = "GBK";

	private static Logger logger = Logger.getLogger(CharFilter.class);

	public void destroy() {

	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String requestedWith = request.getHeader("x-requested-with");
		String type = request.getContentType();
		if (requestedWith != null) {
			logger.debug("requestedWith: " + requestedWith + "\ntype: " + type);
		}
		if (XMLHTTP_REQUEST.equals(requestedWith)
				&& (FF_AJAX_CONTENT_TYPE.equals(type) || IE_CONTENT_TYPE
						.equals(type))) {
			request.setCharacterEncoding(AJAX_CHARACTER_ENCODING_UTF8);
		} else {
			request.setCharacterEncoding(NORMAL_CHARACTER_ENCODING_GBK);
		}
		filterChain.doFilter(request, response);

	}

	public void init(FilterConfig config) throws ServletException {

	}

}
