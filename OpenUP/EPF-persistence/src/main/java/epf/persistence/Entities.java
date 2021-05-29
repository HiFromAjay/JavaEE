/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epf.persistence;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.validation.Validator;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import epf.client.EPFException;
import epf.persistence.impl.Entity;
import epf.persistence.model.EntityBuilder;
import epf.schema.roles.Role;

/**
 *
 * @author FOXCONN
 */
@Path("persistence")
@RolesAllowed(Role.DEFAULT_ROLE)
@RequestScoped
public class Entities implements epf.client.persistence.Entities {
	
	/**
	 * 
	 */
	@Inject
	private transient Logger logger;
    
    /**
     * 
     */
    @Inject
    private transient Request cache;
    
    /**
     * 
     */
    @Inject
    private transient Validator validator;
    
    /**
     * 
     */
    @Context
    private transient SecurityContext context;
    
    @Override
    public Object persist(
            final String unit,
            final String name,
            final InputStream body
            ){
    	final Entity<Object> entity = findEntity(unit, name);
    	Object object = null;
        if(entity.getType() != null){
            try(Jsonb json = JsonbBuilder.create()){
            	final Object obj = json.fromJson(body, entity.getType().getJavaType());
                validator.validate(obj);
                object = cache.persist(unit, context.getUserPrincipal(), name, obj);
            }
            catch(JsonbException ex){
            	throw new BadRequestException(ex);
            }
            catch(Exception ex) {
            	throw new EPFException(ex);
            }
        }
        return object;
    }
    
    @Override
	public void merge(
			final String unit, 
			final String name, 
			final String entityId,
			final InputStream body
			) {
    	final Entity<Object> entity = findEntityObject(unit, name, entityId);
        if(entity.getObject() != null){
            try(Jsonb json = JsonbBuilder.create()){
            	final Object obj = json.fromJson(body, entity.getType().getJavaType());
                validator.validate(obj);
                cache.merge(unit, context.getUserPrincipal(), name, entityId, obj);
            }
            catch(JsonbException ex){
            	logger.throwing(getClass().getName(), "merge", ex);
                throw new BadRequestException(ex);
            }
            catch(Exception ex) {
            	throw new EPFException(ex);
            }
        }
	}
    
    /**
     * @param <T>
     * @param unit
     * @param name
     * @param entityId
     * @return
     */
    protected <T> T find(
    		final String unit,
    		final String name,
    		final String entityId){
        final Entity<T> entity = findEntityObject(unit, name, entityId);
        return entity.getObject();
    }
    
    @Override
    public void remove(
    		final String unit,
    		final String name,
    		final String entityId
            ) {
    	final Entity<Object> entity = findEntityObject(unit, name, entityId);
        if(entity.getObject() != null){
            cache.remove(unit, context.getUserPrincipal(), name, entityId, entity.getObject());
        }
    }
    
    /**
     * @param <T>
     * @param unit
     * @param name
     * @return
     */
    protected <T> Entity<T> findEntity(final String unit, final String name) {
    	final Entity<T> entity = cache.findEntity(unit, context.getUserPrincipal(), name);
        if(entity.getType() == null){
            throw new NotFoundException();
        }
        return entity;
    }
    
    /**
     * @param <T>
     * @param unit
     * @return
     */
    protected <T> List<Entity<T>> findEntities(final String unit){
    	final List<Entity<T>> entities = cache.findEntities(unit, context.getUserPrincipal());
    	if(entities.isEmpty()){
            throw new NotFoundException();
        }
        return entities;
    }
    
    /**
     * @param <T>
     * @param unit
     * @param name
     * @param entityId
     * @return
     */
    protected <T> Entity<T> findEntityObject(final String unit, final String name, final String entityId) {
    	final Entity<T> entity = findEntity(unit, name);
        if(entity.getType() != null){
        	final T object = cache.find(unit, context.getUserPrincipal(), name, entity.getType().getJavaType(), entityId);
            if(object == null){
            	throw new NotFoundException();
            }
            entity.setObject(object);
        }
        return entity;
    }

	@Override
	public Response getEntities(final String unit) {
		final EntityBuilder builder = new EntityBuilder();
		final List<epf.client.model.Entity> entityTypes = findEntities(unit)
				.stream()
				.map(builder::build)
				.collect(Collectors.toList());
		return Response.ok(entityTypes).build();
	}
}
