/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.persistence;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.persistence.Query;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import openup.Roles;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.Explode;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterStyle;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 *
 * @author FOXCONN
 */
@RequestScoped
@Path("persistence/query")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(Roles.ANY_ROLE)
public class QueryManager {
    
    @Inject
    private Cache cache;
    
    @Context
    private SecurityContext context;
    
    @GET
    @Path("criteria/{criteria: .+}")
    @Operation(
            summary = "Native Query", 
            description = "Execute a SELECT query and return the query results."
    )
    @APIResponse(
            description = "Result",
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON
            )
    )
    @PermitAll
    public Response getCriteriaQueryResult(
            @PathParam("criteria")
            @Parameter(
                    name = "criteria",
                    in = ParameterIn.PATH,
                    style = ParameterStyle.MATRIX,
                    explode = Explode.TRUE,
                    required = true,
                    allowReserved = true,
                    schema = @Schema(type = SchemaType.OBJECT)
            )
            List<PathSegment> paths,
            @QueryParam("first")
            Integer firstResult,
            @QueryParam("max")
            Integer maxResults
            ) throws Exception{
        ResponseBuilder response = Response.status(Response.Status.NOT_FOUND);
        
        if(!paths.isEmpty()){
            PathSegment rootSegment = paths.get(0);
            Principal principal = context.getUserPrincipal();
            EntityManager manager = cache.getManager(principal);
            Entity entity = cache.findEntity(principal, rootSegment.getPath());
            if(entity.getType() != null){
                CriteriaBuilder builder = manager.getCriteriaBuilder();
                CriteriaQuery<Object> rootQuery = builder.createQuery();
                EntityType rootType = entity.getType();
                Class rootClass = rootType.getJavaType();
                Root rootFrom = rootQuery.from(rootClass);
                
                List<Predicate> rootParams = new ArrayList<>();
                rootSegment.getMatrixParameters().forEach((name, values) -> {
                    rootParams.add(
                            builder.isMember(
                                    values,
                                    rootFrom.get(name)
                            )
                    );
                });
                if(!rootParams.isEmpty()){
                    rootQuery.where(rootParams.toArray(new Predicate[rootParams.size()]));
                }

                ManagedType parentType = rootType;
                Root parentFrom = rootFrom;
                AbstractQuery parentQuery = rootQuery;
                //Join parentJoin = null;
                
                try{
                    for(PathSegment segment : paths.subList(1, paths.size())){
                        Attribute attribute = parentType.getAttribute(segment.getPath());
                        if (attribute.getPersistentAttributeType() != PersistentAttributeType.BASIC) {
                            Class subClass = null;
                            if(attribute.isCollection()){
                                if(attribute.getJavaType() == List.class){
                                    subClass = parentType.getList(segment.getPath()).getBindableJavaType();
                                }
                                else if(attribute.getJavaType() == Map.class){
                                    subClass = parentType.getMap(segment.getPath()).getBindableJavaType();
                                }
                                else if(attribute.getJavaType() == Set.class){
                                    subClass = parentType.getSet(segment.getPath()).getBindableJavaType();
                                }
                                else if(attribute.getJavaType() == Collection.class){
                                    subClass = parentType.getCollection(segment.getPath()).getBindableJavaType();
                                }
                            }
                            else if(attribute.isAssociation()){
                                subClass = parentType.getSingularAttribute(segment.getPath()).getBindableJavaType();
                            }
                            
                            Subquery subQuery = parentQuery.subquery(subClass);
                            Root subFrom = subQuery.correlate(parentFrom);
                            Join join = subFrom.join(segment.getPath());
                            //Join subJoin = subQuery.correlate(parentJoin);
                            subQuery.select(join);

                            List<Predicate> params = new ArrayList<>();
                            segment.getMatrixParameters().forEach((name, values) -> {
                                params.add(
                                        builder.isMember(
                                                values,
                                                subFrom.get(name)
                                        )
                                );
                            });
                            if(!params.isEmpty()){
                                subQuery.where(params.toArray(new Predicate[params.size()]));
                            }
                            
                            ManagedType subType = manager.getMetamodel().managedType(subClass);
                            parentType = subType;
                            parentFrom = subFrom;
                            parentQuery = subQuery;
                        }
                    }
                    Query query;
                    if(rootQuery != parentQuery){
                        rootQuery.select(parentQuery.getSelection());
                    }
                    else{
                        rootQuery.select(rootFrom);//
                    }
                    query = manager.createQuery(rootQuery);
                
                    if(firstResult != null){
                        query.setFirstResult(firstResult);
                    }
                    if(maxResults != null){
                        query.setMaxResults(maxResults);
                    }
                    response.entity(
                                query.getResultStream()
                                        .collect(Collectors.toList())
                    );
                }
                catch(IllegalArgumentException ex){

                }
            }
        }
        return response.build();
    }
    
    @GET
    @Path("{query}")
    @Operation(
            summary = "Named Query", 
            description = "Execute a SELECT query and return the query results."
    )
    @APIResponse(
            description = "Result",
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON
            )
    )
    @APIResponse(
            description = "a query has not been defined with the given name",
            responseCode = "404"
    )
    public Response getNamedQueryResult(
            @PathParam("query")
            String name,
            @MatrixParam("first")
            Integer firstResult,
            @MatrixParam("max")
            Integer maxResults,
            @Context 
            UriInfo uriInfo
            ) throws Exception{
        ResponseBuilder response = Response.ok();
        Query query = null;
        try{
            query = cache.createNamedQuery(context.getUserPrincipal(), name);
        }
        catch(IllegalArgumentException ex){
            response.status(Response.Status.NOT_FOUND);
        }
        if(query != null){
            buildQuery(query, firstResult, maxResults, uriInfo);
            response.entity(
                    query.getResultStream()
                            .collect(Collectors.toList()));
        }
        return response.build();
    }
    
    void buildQuery(Query query, Integer firstResult, Integer maxResults, UriInfo uriInfo){
        uriInfo.getQueryParameters().forEach((param, paramValue) -> {
            String value = "";
            if(!paramValue.isEmpty()){
                value = paramValue.get(0);
            }
            query.setParameter(param, value);
        });
        if(firstResult != null){
            query.setFirstResult(firstResult);
        }
        if(maxResults != null){
            query.setMaxResults(maxResults);
        }
    }
}
