package cc.it120.www.servletFreemarker.init;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.it120.www.servletFreemarker.FreemarkerServlet;
import cc.it120.www.servletFreemarker.method.BaseApiTemplateMethodModelEx;


public class InitWeb implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/apis.properties"), "UTF-8"));
			StringBuilder build = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				build.append(line.trim());
			}
			JSONObject json = JSONObject.parseObject(build.toString());
			String serverName = json.getString("serverName");
			if (serverName == null || "".equals(serverName)) {
				FreemarkerServlet.DEFAULT_SERVLET_NAME = "default";
			} else if ("Tomcat".equalsIgnoreCase(serverName) || "Jetty".equalsIgnoreCase(serverName) || "JBoss".equalsIgnoreCase(serverName) || "GlassFish".equalsIgnoreCase(serverName)) {
				FreemarkerServlet.DEFAULT_SERVLET_NAME = "default";
			} else if ("gae".equalsIgnoreCase(serverName)) { // Google App Engine
				FreemarkerServlet.DEFAULT_SERVLET_NAME = "_ah_default";
			} else if ("Resin".equalsIgnoreCase(serverName)) {
				FreemarkerServlet.DEFAULT_SERVLET_NAME = "resin-file";
			} else if ("WebLogic".equalsIgnoreCase(serverName)) {
				FreemarkerServlet.DEFAULT_SERVLET_NAME = "FileServlet";
			} else if ("WebSphere".equalsIgnoreCase(serverName)) {
				FreemarkerServlet.DEFAULT_SERVLET_NAME = "SimpleFileServlet";
			} else {
				FreemarkerServlet.DEFAULT_SERVLET_NAME = "default";
			}
			BaseApiTemplateMethodModelEx.IS_DEBUG = json.getBooleanValue("debug");
			JSONArray resources = json.getJSONArray("resources");
			if (resources != null) {
				for (Object object : resources) {
					FreemarkerServlet.RESOURCES.add((String) object);
				}
			}
			
			JSONArray apis = json.getJSONArray("apis");
			for (Object object : apis) {
				JSONObject api = (JSONObject) object;
				BaseApiTemplateMethodModelEx templateMethodModelEx = new BaseApiTemplateMethodModelEx(api, json);
				
				FreemarkerServlet.FREEMARKER_EXT_METHODS.put(api.getString("name"), templateMethodModelEx);
			}
			FreemarkerServlet.REWRITE_ARRAYS = json.getJSONArray("rewrites");
		} catch (Exception e) {
			System.out.println("apis.properties 文件格式不正确，文件内容必须为 json 格式");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				in.close();
			} catch (Exception e2) {}
        }
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
