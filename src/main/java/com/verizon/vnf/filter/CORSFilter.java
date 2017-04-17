package com.verizon.vnf.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;

		final String requestOrgin = request.getHeader("Origin");

		final HttpServletResponse response = (HttpServletResponse) res;

		if (requestOrgin != null && requestOrgin.contains("verizon.com")) {
			response.setHeader("Access-Control-Allow-Origin", requestOrgin);
			response.setHeader("x-frame-options", "allow-from " + requestOrgin);
		} else {
			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("x-frame-options", "allow-from *");
		}

		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PATCH");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		response.setHeader("Access-Control-Allow-Headers", "content-type");
		chain.doFilter(req, res);
	}

	public void init(FilterConfig arg0) throws ServletException {

	}

}