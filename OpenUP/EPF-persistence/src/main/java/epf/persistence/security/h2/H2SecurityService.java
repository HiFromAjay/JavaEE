/**
 * 
 */
package epf.persistence.security.h2;

import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import epf.schema.h2.Queries;
import epf.persistence.security.SecurityService;

/**
 * @author PC
 *
 */
@RequestScoped
public class H2SecurityService implements SecurityService {

	@Override
	@Transactional
	public void setUserPassword(final EntityManager manager, final String userName, final char... password) {
		if(!manager.isJoinedToTransaction()) {
			manager.joinTransaction();
		}
		final String query = String.format(Queries.SET_PASSWORD, userName);
		manager
		.createNativeQuery(query)
		.setParameter(1, new String(password))
		.executeUpdate();
		manager.flush();
	}
	
	/**
     * @param role
     * @return
     */
    protected static String mapRole(final String role) {
    	return Stream.of(role.split("_")).map(text -> text.substring(0, 1) + text.substring(1).toLowerCase(Locale.getDefault())).collect(Collectors.joining("_"));
    }
}
