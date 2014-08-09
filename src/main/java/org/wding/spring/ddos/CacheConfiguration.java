package org.wding.spring.ddos;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
    	net.sf.ehcache.config.CacheConfiguration cacheConfiguration = new net.sf.ehcache.config.CacheConfiguration();
        cacheConfiguration.setName("ddos");
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        cacheConfiguration.setTimeToIdleSeconds(10);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(cacheConfiguration);

        net.sf.ehcache.CacheManager ehCacheManager = net.sf.ehcache.CacheManager.create(config);
        
        return ehCacheManager;
    }
    
    @Bean
    public Cache cache(CacheManager cacheManager){
    	return cacheManager.getCache("ddos");
    }
}
