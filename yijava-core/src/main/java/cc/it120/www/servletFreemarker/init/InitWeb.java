package cc.it120.www.servletFreemarker.init;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cc.it120.www.servletFreemarker.FreemarkerServlet;
import cc.it120.www.servletFreemarker.method.BaseApiTemplateMethodModelEx;
import cc.it120.www.servletFreemarker.util.CacheUtils;


public class InitWeb implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		JSONObject json = null;
		try {
			json = JSONObject.parseObject(readPropertiesFile());
		} catch (Exception e) {
			System.out.println("apis.properties 文件格式不正确，文件内容必须为 json 格式");
			e.printStackTrace();
			System.exit(0);
		}
		
		String serverName = json.getString("serverName");
		initDefaultServlet(serverName);
		
		BaseApiTemplateMethodModelEx.IS_DEBUG = json.getBooleanValue("debug");
		FreemarkerServlet.REWRITE_ARRAYS = json.getJSONArray("rewrites");
		
		JSONArray resources = json.getJSONArray("resources");
		if (resources != null) {
			for (Object object : resources) {
				FreemarkerServlet.RESOURCES.add((String) object);
			}
		}
		
		JSONArray apis = json.getJSONArray("apis");
		for (Object object : apis) {
			JSONObject api = (JSONObject) object;
			initCache(api);
			
			BaseApiTemplateMethodModelEx templateMethodModelEx = new BaseApiTemplateMethodModelEx(api, json);
			FreemarkerServlet.FREEMARKER_EXT_METHODS.put(api.getString("name"), templateMethodModelEx);
		}
	}
	
	private String readPropertiesFile () {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/apis.properties"), "UTF-8"));
			StringBuilder build = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				build.append(line.trim());
			}
			in.close();
			return build.toString();
		} catch (Exception e) {
			System.out.println("apis.properties 不存在或者无法不读该文件的内容");
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}
	
	private void initDefaultServlet (String serverName) {
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
	}
	
	private void initCache (JSONObject apiJson) {
		Integer cacheSeconds = apiJson.getInteger("cacheSeconds");
		if (cacheSeconds == null || cacheSeconds <= 0) {
			return;
		}
		if (!CacheUtils.CACHES_MAP.containsKey("cache_" + cacheSeconds)) {
			Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(cacheSeconds, TimeUnit.SECONDS).build();
			CacheUtils.CACHES_MAP.put("cache_" + cacheSeconds, cache);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
