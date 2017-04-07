package cc.it120.www.servletFreemarker.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	/**
	 * 构造函数
	 * @param request
	 * @param response
	 */
	public CookieUtil(HttpServletRequest request,HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
	
	/**
	 * 获取 cookie 的值
	 * @param name 名称
	 * @return
	 */
	public String getCookie(String name){
		Cookie[] cookies=request.getCookies();
		if(cookies != null && cookies.length >0){
			for(int i=0;i<cookies.length;i++){
				Cookie cookie=cookies[i];
				if(cookie.getName().equals(name) && cookie.getValue()!=null){
					try {
						return URLDecoder.decode(cookie.getValue(),"utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
	/**
	 * 移除 cookie 
	 * @param name 名称
	 * @param domain 锁定域名 .souqian.com 可以输入 NULL值，只针对当前域名有效
	 */
	public void removeCookie(String name,String domain){
		setCookie(name, null, 0,domain);
	}
	
	/**
	 * 设置或者修改 cookies 的值
	 * @param name 名称
	 * @param value 值
	 * @param domain 锁定域名 .souqian.com 可以输入 NULL值，只针对当前域名有效
	 * @param maxAge 最大生存空间 -1 浏览器生命周期 0 删除 cookie 单位为 秒
	 */
	public void setCookie(String name,String value,Integer maxAge,String domain){
		Cookie[] cookies=request.getCookies();
		boolean flag=false;
		if(cookies!=null){
			for(Cookie c:cookies){
				if(c.getName()!=null && c.getName().equals(name)){
					try {
						if (value != null) {
							c.setValue(URLEncoder.encode(value, "utf-8"));
						} else {
							c.setValue(value);
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if(domain != null && !"".equals(domain)){
						c.setDomain(domain);
					}
					c.setPath("/");
					c.setMaxAge(maxAge);
					response.addCookie(c);
					flag=true;
				}
			}
		}
		if(!flag){
			Cookie c;
			try {
				if (value != null) {
					c = new Cookie(name, URLEncoder.encode(value, "utf-8"));
				} else {
					c = new Cookie(name, value);
				}
				if(domain != null && !"".equals(domain)){
					c.setDomain(domain);
				}
				c.setPath("/");
				c.setMaxAge(maxAge);
				response.addCookie(c);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
}
