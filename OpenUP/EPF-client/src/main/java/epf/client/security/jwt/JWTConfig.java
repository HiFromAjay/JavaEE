/**
 * 
 */
package epf.client.security.jwt;

/**
 * @author PC
 *
 */
public interface JWTConfig {
	
    /**
     * 
     */
    String PRIVATE_KEY = "mp.jwt.issue.privatekey";
    /**
     * 
     */
    String EXPIRE_DURATION = "epf.security.jwt.exp.duration";
    /**
     * 
     */
    String EXPIRE_TIMEUNIT = "epf.security.jwt.exp.timeunit";
}
