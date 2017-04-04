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
			BaseApiTemplateMethodModelEx.IS_DEBUG = json.getBooleanValue("debug");
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
