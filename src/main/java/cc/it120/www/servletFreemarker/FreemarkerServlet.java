package cc.it120.www.servletFreemarker;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.it120.www.servletFreemarker.method.BaseApiTemplateMethodModelEx;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class FreemarkerServlet extends freemarker.ext.servlet.FreemarkerServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String, BaseApiTemplateMethodModelEx> FREEMARKER_EXT_METHODS = new HashMap<>();

	protected TemplateModel createModel(ObjectWrapper objectWrapper,
            ServletContext servletContext,
            final HttpServletRequest request,
            final HttpServletResponse response) throws TemplateModelException {
		AllHttpScopesHashModel params = (AllHttpScopesHashModel) super.createModel(objectWrapper, servletContext, request, response);
		for (String key : FREEMARKER_EXT_METHODS.keySet()) {
			params.putUnlistedModel(key, FREEMARKER_EXT_METHODS.get(key));
		}
		return params;
	}
}
