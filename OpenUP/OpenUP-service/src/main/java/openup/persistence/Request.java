/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.persistence;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;
import javax.ws.rs.ForbiddenException;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 *
 * @author FOXCONN
 */
@CacheDefaults(cacheName = "Entity")
@RequestScoped
public class Request {
    
    @Inject
    private Application application;
    
    EntityManager joinTransaction(EntityManager manager){
        if(manager != null){
            if(!manager.isJoinedToTransaction()){
                manager.joinTransaction();
            }
        }
        return manager;
    }
    
    EntityManager getManager(String unit, Principal principal) throws Exception{
        EntityManager manager = null;
        if(principal != null){
            Context context = application.getContext(unit);
            if(context != null){
                Credential credential = context.getCredential(principal.getName());
                if(credential != null){
                    if(principal instanceof JsonWebToken){
                        JsonWebToken jwt = (JsonWebToken)principal;
                        Session session = credential.getSession(jwt.getIssuedAtTime());
                        if(session != null){
                            if(session.checkExpirationTime(jwt.getExpirationTime())){
                                manager = session
                                    .putConversation(jwt.getTokenID())
                                    .putManager(jwt.getIssuedAtTime());
                            }
                        }
                    }
                    if(manager == null) {
                    	manager = credential.getDefaultManager();
                    }
                }
            }
            if(manager == null){
                throw new ForbiddenException();
            }
        }
        return manager;
    }
    
    public <T> TypedQuery<T> createNamedQuery(String unit, Principal principal, String name, Class<T> cls) throws Exception{
        return getManager(unit, principal).createNamedQuery(name, cls);
    }
    
    @Transactional
    @CachePut
    public void persist(
            @CacheKey
            String unit,
            Principal principal,
            @CacheKey
            String name,
            @CacheKey
            String id, 
            @CacheValue
            Object object) throws Exception{
        EntityManager manager = getManager(unit, principal);
        manager = joinTransaction(manager);
        manager.persist(object);
    }
    
    @CacheResult
    public <T> T find(@CacheKey String unit, Principal principal, @CacheKey String name, Class<T> cls, @CacheKey String id) throws Exception{
        return getManager(unit, principal).find(cls, id);
    }
    
    @Transactional
    @CacheRemove
    public void remove(@CacheKey String unit, Principal principal, @CacheKey String name, @CacheKey String id, Object object) throws Exception{
        EntityManager manager = getManager(unit, principal);
        manager = joinTransaction(manager);
        manager.remove(object);
    }
    
    @CacheResult(cacheName = "EntityType")
    public <T> Entity<T> findEntity(@CacheKey String unit, Principal principal, @CacheKey String name) throws Exception{
        Entity<T> result = new Entity<>();
        getManager(unit, principal).getMetamodel()
                .getEntities()
                .stream()
                .filter(
                        entityType -> {
                            return entityType.getName().equals(name);
                        }
                )
                .findFirst()
                .ifPresent(entityType -> {
                	try {
                		@SuppressWarnings("unchecked")
						EntityType<T> type = entityType.getClass().cast(entityType);
	                	result.setType(type);
	                	}
                	catch(ClassCastException ex) {
                		
                	}});
        return result;
    }
    
    @CacheResult(cacheName = "NamedQuery")
    public <T> List<T> getNamedQueryResult(@CacheKey String unit, Principal principal, @CacheKey String name, Class<T> cls) throws Exception{
        return getManager(unit, principal)
                .createNamedQuery(name, cls)
                .getResultStream()
                .collect(Collectors.toList());
    }
}
