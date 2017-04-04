package cc.it120.www.servletFreemarker.method;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class UrlEncodeMethod implements TemplateMethodModelEx {
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		
		String str = String.valueOf(arguments.get(0));
		String type = String.valueOf(arguments.get(1));
		
		try {
			if (type == null || "0".equals(type)) {
				return URLEncoder.encode(str, "utf-8");
			} else {
				return URLDecoder.decode(str, "utf-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

}
