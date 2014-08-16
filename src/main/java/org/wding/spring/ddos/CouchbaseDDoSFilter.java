package org.wding.spring.ddos;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.couchbase.client.CouchbaseClient;

public class CouchbaseDDoSFilter extends OncePerRequestFilter {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	private static final int MAX_HIT_COUNT_PER_IP = 5;
	
	private static final int DEFAULT_EXPIRATION_TIME = 10;
	

	@Autowired
	private CouchbaseClient couchbaseClient;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String remoteAddr = request.getRemoteAddr();
		String uri = request.getRequestURI();
		
		String key = generateKey(remoteAddr, uri);
		
		if(key != null){
			Object element = couchbaseClient.get(key);
			if(element != null){
				int hitCount = (int) element;
				if(hitCount >= MAX_HIT_COUNT_PER_IP){
					notifyAttack(request, response);
					logger.warn("suspicious access for " + key);
					return;
				}else{
					couchbaseClient.set(key, DEFAULT_EXPIRATION_TIME, ++hitCount);
					logger.info("update statistics for " + key + " , hit account:" + hitCount);
				}
			}else{
				couchbaseClient.add(key, DEFAULT_EXPIRATION_TIME, 1);
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
