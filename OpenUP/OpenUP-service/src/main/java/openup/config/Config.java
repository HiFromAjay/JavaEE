/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import openup.api.config.ConfigNames;

/**
 *
 * @author FOXCONN
 */
@ApplicationScoped
@Path("config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Config implements openup.api.config.Config {
    
    private Map<String, Object> configs;
    
    @PostConstruct
    void postConstruct(){
        configs = new ConcurrentHashMap<>();
        String url = System.getenv(ConfigNames.OPENUP_URL);
        configs.put(ConfigNames.OPENUP_URL, url);
        configs.put(ConfigNames.OPENUP_CONFIG_URL, url + "config");
        configs.put(ConfigNames.OPENUP_SECURITY_URL, url + "security");
        configs.put(ConfigNames.OPENUP_SECURITY_JWT_HEADER, "Authorization");
        configs.put(ConfigNames.OPENUP_SECURITY_JWT_FORMAT, "Bearer %s");
        configs.put(ConfigNames.OPENUP_PERSISTENCE_URL, url + "persistence");
    }
    
    @Override
    public Map<String, Object> getConfig() throws Exception {
        return configs;
    }
}
