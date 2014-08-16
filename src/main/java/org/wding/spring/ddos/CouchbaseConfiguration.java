package org.wding.spring.ddos;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.CouchbaseClient;

@Configuration
public class CouchbaseConfiguration {


    @Bean(destroyMethod = "shutdown")
    public CouchbaseClient couchbaseClient() throws Exception {

      return new CouchbaseClient(
        bootstrapUris(bootstrapHosts()),
        getBucketName(),
        getBucketPassword()
      );
    }
    
    protected List<String> bootstrapHosts() {
    	return Collections.singletonList("127.0.0.1");
    }
    
    protected String getBucketName() {
    	return "default";
    }
    
    protected String getBucketPassword() {
    	return "";
    }
    
    private static List<URI> bootstrapUris(List<String> hosts) throws URISyntaxException {
      List<URI> uris = new ArrayList<URI>();
      for (String host : hosts) {
        uris.add(new URI("http://" + host + ":8091/pools"));
      }
      return uris;
    }

}
