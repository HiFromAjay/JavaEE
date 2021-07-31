/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epf.client.security;

import java.net.URL;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import epf.util.client.Client;

/**
 *
 * @author FOXCONN
 */
@Path("security")
public interface Security {
	
	/**
     * 
     */
    String SECURITY_URL = "epf.security.url";
    
    /**
     * 
     */
    String AUDIENCE_FORMAT = "%s://%s:%s/";
    /**
     * 
     */
    String TOKEN_ID_FORMAT = "%s-%s-%s";
    /**
     * 
     */
    String HEADER_FORMAT = "Bearer %s";
    
    /**
     * 
     */
    String URL = "url";
    
    /**
     * @param username
     * @param passwordHash
     * @param url
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    String login(
            @FormParam("username")
            @NotBlank
            final String username,
            @FormParam("password_hash")
            @NotBlank
            final String passwordHash, 
            @QueryParam(URL)
            @NotNull
            final URL url
    );
    
    /**
     * @param client
     * @param username
     * @param passwordHash
     * @param url
     * @return
     */
    static String login(
    		final Client client,
    		final String username, 
    		final String passwordHash, 
    		final URL url) {
    	final Form form = new Form();
    	form.param("username", username);
    	form.param("password_hash", passwordHash);
    	return client.request(
    			target -> target.queryParam(URL, url),
    			req -> req.accept(MediaType.TEXT_PLAIN))
    			.post(Entity.form(form), String.class);
    }
    
    /**
     * @param username
     * @param passwordHash
     * @param url
     * @return
     */
    @POST
    @Path("otp")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    String newOneTimePassword(
            @FormParam("username")
            @NotBlank
            final String username,
            @FormParam("password_hash")
            @NotBlank
            final String passwordHash, 
            @QueryParam(URL)
            @NotNull
            final URL url
    );
    
    /**
     * @param client
     * @param username
     * @param passwordHash
     * @param url
     * @return
     */
    static String newOneTimePassword(
    		final Client client,
    		final String username, 
    		final String passwordHash, 
    		final URL url) {
    	final Form form = new Form();
    	form.param("username", username);
    	form.param("password_hash", passwordHash);
    	return client.request(
    			target -> target.path("otp").queryParam(URL, url),
    			req -> req.accept(MediaType.TEXT_PLAIN))
    			.post(Entity.form(form), String.class);
    }
    
    @PUT
    @Path("otp")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    String loginOneTimePassword(
            @FormParam("otp")
            @NotBlank
            final String oneTimePassword
    );
    
    /**
     * @param client
     * @param oneTimePassword
     * @return
     */
    static String loginOneTimePassword(
    		final Client client,
    		final String oneTimePassword
    		) {
    	final Form form = new Form();
    	form.param("otp", oneTimePassword);
    	return client.request(
    			target -> target.path("otp"),
    			req -> req.accept(MediaType.TEXT_PLAIN))
    			.put(Entity.form(form), String.class);
    }
    
    /**
     * @return
     */
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    String logOut();
    
    /**
     * @param client
     * @return
     */
    static String logOut(final Client client) {
    	return client.request(
    			target -> target, 
    			req -> req.accept(MediaType.TEXT_PLAIN)
    			)
    			.delete(String.class);
    }
    
    /**
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Token authenticate();
    
    /**
     * @param client
     * @return
     */
    static Token authenticate(final Client client) {
    	return client.request(
    			target -> target, 
    			req -> req.accept(MediaType.APPLICATION_JSON)
    			)
    			.get(Token.class);
    }
    
    /**
     * @param info
     */
    @PATCH
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    void update(
    		@BeanParam
    		@Valid
    		final CredentialInfo info
    		);
    
    /**
     * @param client
     * @param fields
     * @return
     */
    static Response update(final Client client, final Map<String, String> fields) {
    	final Form form = new Form();
    	fields.forEach((name, value) -> form.param(name, value));
    	return client.request(
    			target -> target, 
    			req -> req
    			)
    	.build(HttpMethod.PATCH, Entity.form(form))
    	.invoke();
    }
    
    /**
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    String revoke();
    
    /**
     * @param client
     * @return
     */
    static String revoke(
    		final Client client) {
    	return client.request(
    			target -> target,
    			req -> req.accept(MediaType.TEXT_PLAIN))
    			.put(Entity.form(new Form()), String.class);
    }
}
