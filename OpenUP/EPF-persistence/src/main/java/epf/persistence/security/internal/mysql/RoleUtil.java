package epf.persistence.security.internal.mysql;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author PC
 *
 */
public interface RoleUtil {
	
	/**
	 * 
	 */
	String ROLE_FORMAT = "'%s'@'%s'";
	/**
	 * 
	 */
	String ROLES_SEPARATOR = "`,`";
	/**
	 * 
	 */
	String ROLE_DELIMITER = "`@`";
	
	/**
	 * @param string
	 * @return
	 */
	static List<Role> parse(final String string) {
		Objects.requireNonNull(string, "String");
		final String[] roles = string.split(ROLES_SEPARATOR);
		return Arrays.asList(roles).stream().map(RoleUtil::fromString).collect(Collectors.toList());
	}
	
	/**
	 * @param string
	 * @return
	 */
	static Role fromString(final String string) {
		Objects.requireNonNull(string, "String");
		final String[] fragments = string.split(ROLE_DELIMITER);
		return new Role(fragments[0].substring(1), fragments[1]);
	}
}
