/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup;

import epf.schema.roles.RoleSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import openup.persistence.Request;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Type;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 *
 * @author FOXCONN
 */
@Type
@Path("roles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
@RolesAllowed(Roles.ANY_ROLE)
public class Roles {
    
    @Inject
    private Request request;
    
    @Context
    private SecurityContext context;
    
    private EntityManager manager;
    
    @PostConstruct
    void postConstruct(){
        manager = request.getManager(context);
    }
    
    public static final String ANY_ROLE = "ANY_ROLE";
    public static final String ADMIN = "ADMIN";
    
    @Name("Contents")
    @GET
    @Operation(
            summary = "Roles", 
            description = "This category lists roles organized by role set."
    )
    @APIResponse(
            description = "Role Set",
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = RoleSet.class)
            )
    )
    public List<RoleSet> getRoleSets(){
        return manager.createNamedQuery(
                RoleSet.ROLES, 
                RoleSet.class)
                .getResultStream()
                .collect(Collectors.toList());
    }
}
