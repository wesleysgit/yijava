package cc.it120.www.servletFreemarker.method;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.it120.www.servletFreemarker.util.CacheUtils;
import cc.it120.www.servletFreemarker.util.HttpRequest;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class BaseApiTemplateMethodModelEx implements TemplateMethodModelEx {
	private static final Pattern PATTERN = Pattern.compile("\\{(.*)\\}");
	public static boolean IS_DEBUG = true;
	
	private JSONObject parentJson;
	private JSONObject apiJson;
	
	private String name;
	private String url;
	private String method;
	private int connectTimeout;
	private int readTimeout;
	private String paramInit;
	private String returnType;
	private int cacheSeconds;
	
	public BaseApiTemplateMethodModelEx (JSONObject apiJson, JSONObject parentJson) {
		this.parentJson = parentJson;
		this.apiJson = apiJson;
		name = getString("name", "");
		url = getString("url", "");
		method = getString("method", "get");
		connectTimeout = getInt("connectTimeout", 30000);
		readTimeout = getInt("readTimeout", 30000);
		paramInit = getString("params", "");
		returnType = getString("returnType", "string");
		cacheSeconds = getInt("cacheSeconds", 0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if ("".equals(url)) {
			return "url不能为空";
		}
		StringBuilder build = new StringBuilder();
		for (Object object : arguments) {
			String param = String.valueOf(object);
			if (param == null || "".equals(param.trim()) || "null".equals(param.trim())) {
				continue;
			}
			build.append("&").append(param);
		}
		if (!"".equals(paramInit)) {
			build.append("&").append(paramInit);
		}
		String params = build.length() == 0 ?"" : build.substring(1);
		if (cacheSeconds > 0) {
			if (IS_DEBUG) {
				System.out.println("从缓存中读取数据");
			}
			try {
				return CacheUtils.CACHES_MAP.get("cache_" + cacheSeconds).get(name + params, new Callable<Object>() {
		            public Object call() {  
		                return getDataFromAPI(params);
		            }  
		        });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (IS_DEBUG) {
			System.out.println("url:" + url);
		}
		return getDataFromAPI(params);
	}
	
	private Object getDataFromAPI (String params) {
		
		if (IS_DEBUG) {
			System.out.println("params:" + params);
		}
		String resultHtml = null;
		if ("get".equalsIgnoreCase(method)) {
			resultHtml = HttpRequest.sendGet(url, connectTimeout, readTimeout, params);
		} else if ("post".equalsIgnoreCase(method)) {
			resultHtml = HttpRequest.sendPost(url, connectTimeout, readTimeout, params);
		} else {
			return "暂不支持"+ method +"方法";
		}
		if (IS_DEBUG) {
			System.out.println("result:" + resultHtml);
		}
		
		if ("string".equalsIgnoreCase(returnType)) {
			return resultHtml;
		}
		if ("json".equalsIgnoreCase(returnType)) {
			return JSONObject.parse(resultHtml);
		}
		if ("jsonarray".equalsIgnoreCase(returnType)) {
			return JSONArray.parse(resultHtml);
		}
		return "暂不支持返回" + returnType + "类型";
	}
	
	private String getString (String key, String defaultStr) {
		String result = apiJson.getString(key);
		if (result == null || "".equals(result.trim())) {
			return defaultStr;
		}
		
		Matcher m = PATTERN.matcher(result);
		while (m.find()) {
			result = result.replace(m.group(0), parentJson.getString(m.group(1)));
		}
		return result.trim();
	}
	
	private int getInt (String key, int defaulInt) {
		Integer result = apiJson.getInteger(key);
		if (result == null) {
			return defaulInt;
		}
		return result.intValue();
	}

}
