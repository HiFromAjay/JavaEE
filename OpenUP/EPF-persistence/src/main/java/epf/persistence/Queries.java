package epf.persistence;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
import epf.naming.Naming;
import epf.persistence.ext.EntityManager;
import epf.persistence.ext.EntityManagerFactory;
import epf.persistence.ext.Query;
import epf.persistence.internal.Entity;
import epf.persistence.internal.QueryBuilder;
import epf.persistence.internal.util.EntityTypeUtil;
import epf.persistence.internal.util.PrincipalUtil;
import io.smallrye.common.annotation.NonBlocking;

/**
 *
 * @author FOXCONN
 */
@Path(Naming.PERSISTENCE)
@RolesAllowed(Naming.Security.DEFAULT_ROLE)
@ApplicationScoped
@NonBlocking
public class Queries implements epf.persistence.client.Queries {
    
    /**
     * 
     */
    @Inject
    transient EntityManagerFactory factory;
    
    /**
     * @param manager
     * @param criteria
     * @param firstResult
     * @param maxResults
     * @return
     */
    CompletionStage<List<Object>> executeQuery(
    		final EntityManager manager, 
    		final CriteriaQuery<Object> criteria,
    		final Integer firstResult,
            final Integer maxResults){
    	final Query<Object> query = manager.createQuery(criteria);
    	if(firstResult != null){
            query.setFirstResult(firstResult);
        }
        if(maxResults != null){
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }
    
    @Override
    public CompletionStage<List<Object>> executeQuery(
    		final String schema,
            final List<PathSegment> paths,
            final Integer firstResult,
            final Integer maxResults,
            final SecurityContext context
            ) {
    	final Entity<Object> entity = new Entity<>();
    	if(!paths.isEmpty()){
        	final PathSegment rootSegment = paths.get(0);
        	final String entityName = rootSegment.getPath();
        	@SuppressWarnings("unchecked")
			final EntityType<Object> entityType = (EntityType<Object>) EntityTypeUtil.findEntityType(factory.getMetamodel(), entityName).orElseThrow(NotFoundException::new);
        	EntityTypeUtil.getSchema(entityType).ifPresent(entitySchema -> {
        		if(!entitySchema.equals(schema)) {
        			throw new NotFoundException();
        		}
        	});
        	entity.setType(entityType);
        	final QueryBuilder queryBuilder = new QueryBuilder();
        	final CriteriaQuery<Object> criteria = queryBuilder
        			.metamodel(factory.getMetamodel())
        			.criteria(factory.getCriteriaBuilder())
        			.entity(entity)
        			.paths(paths)
        			.build();
        	final Map<String, Object> props = PrincipalUtil.getClaims(context.getUserPrincipal());
        	props.put(Naming.Persistence.Internal.SCHEMA, schema);
        	return factory.createEntityManager(props).thenCompose(
        			manager -> executeQuery(manager, criteria, firstResult, maxResults).thenCombine(manager.close(), (res, v) -> res)
        			);
        }
    	throw new NotFoundException();
    }
}
