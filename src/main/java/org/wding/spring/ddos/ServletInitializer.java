package org.wding.spring.ddos;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableAutoConfiguration
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
	@Bean
	protected CharacterEncodingFilter characterEncodingFilter(){
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		return characterEncodingFilter;
	}
	
//	@Bean
//	protected DDoSFilter ddosFilter(){
//		DDoSFilter ddosFilter = new DDoSFilter();
//		return ddosFilter;
//	}
	
	@Bean
	protected CouchbaseDDoSFilter couchbaseDDoSFilter(){
		CouchbaseDDoSFilter couchbaseDDoSFilter = new CouchbaseDDoSFilter();
		return couchbaseDDoSFilter;
	}

}
