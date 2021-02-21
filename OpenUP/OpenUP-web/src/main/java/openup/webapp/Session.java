/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.webapp;

import java.io.Serializable;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import epf.client.config.ConfigNames;
import epf.client.config.ConfigSource;
import epf.client.security.Security;
import epf.util.client.Client;
import epf.util.client.ClientQueue;
import openup.schema.OpenUP;
import openup.webapp.security.TokenPrincipal;

/**
 *
 * @author FOXCONN
 */
@SessionScoped
@Named("webapp_session")
public class Session implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TokenPrincipal principal;
    
    @Inject
    private SecurityContext context;
    
    @Inject
    private ConfigSource config;
    
    @Inject
    private ClientQueue clients;
    
    @Inject
    private Logger logger;
    
    @PostConstruct
    void postConstruct(){
        if(context.getCallerPrincipal() instanceof TokenPrincipal){
            principal = ((TokenPrincipal)context.getCallerPrincipal());
        }
    }
    
    @PreDestroy
    void preDestroy(){
        if(principal != null){
        	String gateway = config.getValue(ConfigNames.GATEWAY_URL);
        	try(Client client = newClient(new URI(gateway))) {
            	Security.logOut(client, null);
            }
            catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
    
    public Client newClient(URI uri) {
    	Client client = new Client(clients, uri, b -> b);
    	client.authorization(principal.getToken().getRawToken());
    	return client;
    }
}
