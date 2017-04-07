package cc.it120.www.servletFreemarker.util;

import java.util.Map;

import com.google.common.cache.Cache;
import com.google.common.collect.Maps;

public class CacheUtils {
	public static Map<String, Cache<String, Object>> CACHES_MAP = Maps.newHashMap();

}
