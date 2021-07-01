/**
 * 
 */
package epf.cache;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import epf.cache.persistence.Persistence;
import epf.schema.roles.Role;

/**
 * @author PC
 *
 */
@ApplicationScoped
@Path("cache")
@RolesAllowed(Role.DEFAULT_ROLE)
public class Cache implements epf.client.cache.Cache {

	/**
	 * 
	 */
	@Inject
	private transient Persistence persistence;
	
	/**
	 *
	 */
	@Override
    public Response getEntity(
            final String name,
            final String entityId
            ) {
		final Object entity = persistence.getEntity(name, entityId);
		if(entity != null) {
			return Response.ok(entity).build();
		}
		throw new NotFoundException();
	}
}
