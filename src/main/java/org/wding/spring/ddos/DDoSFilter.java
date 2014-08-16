package org.wding.spring.ddos;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

public class DDoSFilter extends OncePerRequestFilter {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private static final int MAX_HIT_COUNT_PER_IP = 5;
	
	private static long lastEvictionTime = System.currentTimeMillis();

	private static long evicationTimeInterval = 5*60*1000;

	@Autowired
	private Cache cache;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		long currentTime = System.currentTimeMillis();
		if(currentTime > lastEvictionTime + evicationTimeInterval){
			cache.evictExpiredElements();
			lastEvictionTime = currentTime;
			logger.info("eviction expired ip");
		}
		
		String remoteAddr = request.getRemoteAddr();
		String uri = request.getRequestURI();
		
		String key = generateKey(remoteAddr, uri);
		
		if(key != null){
			Element element = cache.getQuiet(key);
			if(element != null){
				if(element.isExpired()){//check if the user has been inactive for 10s
					element.resetAccessStatistics();
					logger.info("reset statistics for " + key);
				}else{
					long hitCount = element.getHitCount();
					if(hitCount++ >= MAX_HIT_COUNT_PER_IP){
						notifyAttack(request, response);
						element.updateAccessStatistics();
						logger.warn("suspicious access for " + key);
						return;
					}else{
						element.updateAccessStatistics();
						logger.info("update statistics for " + key + " , hit account:" + hitCount);
					}
				}
			}else{
				Element newElement = new Element(key, "");
				cache.put(newElement);
				logger.info("new statistics for " + key);
			}
		}
		
		filterChain.doFilter(request, response);
	}

	/**
	 * if the uri is not we want to monitor, then return null
	 * @param remoteAddr
	 * @param uri
	 * @return
	 */
	private String generateKey(String remoteAddr, String uri) {
		if(uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".jpg") || uri.endsWith(".png")){
			return null;
		}
		return remoteAddr + "-" + uri;
	}

	private void notifyAttack(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			response.getWriter().write("you are under attack");
			response.getWriter().flush();
		} catch (IOException e) {
		}
	}

}
