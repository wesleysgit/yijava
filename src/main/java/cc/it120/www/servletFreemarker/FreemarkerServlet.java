package cc.it120.www.servletFreemarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.it120.www.servletFreemarker.method.BaseApiTemplateMethodModelEx;
import cc.it120.www.servletFreemarker.method.UrlEncodeMethod;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class FreemarkerServlet extends freemarker.ext.servlet.FreemarkerServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String, BaseApiTemplateMethodModelEx> FREEMARKER_EXT_METHODS = new HashMap<>();
	public static JSONArray REWRITE_ARRAYS;
	private static UrlEncodeMethod URLENCODEMETHOD = new UrlEncodeMethod();
	public static List<String> RESOURCES = new ArrayList<>();
	public static String DEFAULT_SERVLET_NAME = "default";

	protected boolean preprocessRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		
		String path = request.getServletPath();
		if (path == null || "".equals(path)) {
			return false;
		}
		for (String resource : RESOURCES) {
			if (path.startsWith(resource) || path.endsWith(resource)) {
				this.getServletContext().getNamedDispatcher(DEFAULT_SERVLET_NAME).forward(request, response);
				return true;
			}
		}
		return false;
	}

	protected TemplateModel createModel(ObjectWrapper objectWrapper, ServletContext servletContext,
			final HttpServletRequest request, final HttpServletResponse response) throws TemplateModelException {
		AllHttpScopesHashModel params = (AllHttpScopesHashModel) super.createModel(objectWrapper, servletContext,
				request, response);
		for (String key : FREEMARKER_EXT_METHODS.keySet()) {
			params.putUnlistedModel(key, FREEMARKER_EXT_METHODS.get(key));
		}
		params.putUnlistedModel("urlEncode", URLENCODEMETHOD);
		return params;
	}

	protected String requestUrlToTemplatePath(HttpServletRequest request) throws ServletException {
		String templatePath = super.requestUrlToTemplatePath(request);
		if (templatePath != null && !"".equals(templatePath) && REWRITE_ARRAYS != null && REWRITE_ARRAYS.size() > 0) {
			// 计算rewrite
			for (Object object : REWRITE_ARRAYS) {
				JSONObject rewrite = (JSONObject) object;
				Pattern p = Pattern.compile(rewrite.getString("url"));
				Matcher m = p.matcher(templatePath);
				if (m.find()) {
					for (int i = 0; i <= m.groupCount(); i++) {
						request.setAttribute("rewriteParams" + i, m.group(i));
					}
					request.setAttribute("rewriteName", rewrite.getString("name"));
					return rewrite.getString("page");
				}
			}
		}
		if (!templatePath.endsWith(".html")) {
			templatePath += ".html";
		}
		return templatePath;
	}
}
