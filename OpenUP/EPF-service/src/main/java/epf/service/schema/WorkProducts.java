/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epf.service.schema;

import epf.schema.work_products.Domain;
import epf.service.persistence.Request;
import java.security.Principal;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Type;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import epf.schema.EPF;
import epf.schema.roles.Role;

/**
 *
 * @author FOXCONN
 */
@Type
@Path("schema/work-products")
@RolesAllowed(Role.DEFAULT_ROLE)
@RequestScoped
public class WorkProducts {
    
    /**
     * 
     */
    @Inject
    private transient Request cache;
    
    /**
     * 
     */
    @Inject
    private transient Principal principal;
    
    /**
     * @return
     */
    @Name("Contents")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Work Products", 
            description = "List of work products organized by domain."
    )
    @APIResponse(
            description = "Domain",
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Domain.class)
            )
    )
    public List<Domain> getDomains() {
        return cache.getNamedQueryResult(
                EPF.SCHEMA,
                principal,
                Domain.DOMAINS, 
                Domain.class);
    }
}
