package epf.security.util;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;

/**
 * @author PC
 *
 */
public interface IdentityStore {
	
	/**
	 * 
	 */
	String PERSISTENCE_UNIT = "EPF-Security";

	/**
	 * @param credential
	 * @return
	 */
	CompletionStage<CredentialValidationResult> validate(final UsernamePasswordCredential credential);
	
	/**
	 * @param callerPrincipal
	 * @return
	 */
	CompletionStage<Set<String>> getCallerGroups(final CallerPrincipal callerPrincipal);
}
