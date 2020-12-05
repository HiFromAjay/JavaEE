/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.client.security;

import java.net.URL;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author FOXCONN
 */
@Path("security")
public interface Security {
    
    String AUDIENCE_URL_FORMAT = "%s://%s:%s/";
    String TOKEN_ID_FORMAT = "%s-%s-%s";
    String REQUEST_HEADER_NAME = "Authorization";
    String REQUEST_HEADER_FORMAT = "Bearer %s";
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    Response login(
            @FormParam("username")
            String username,
            @FormParam("password")
            String password, 
            @QueryParam("url")
            URL url) throws Exception;
    
    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    Response runAs(
            @FormParam("runAs") 
            String role,
            @Context
            SecurityContext context, 
            @Context 
            UriInfo uriInfo
            ) throws Exception;
    
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    Response logOut(
            @Context
            SecurityContext context
            ) throws Exception;
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Response authenticate(@Context SecurityContext context);
}
