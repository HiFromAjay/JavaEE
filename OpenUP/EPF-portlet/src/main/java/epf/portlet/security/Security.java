/**
 * 
 */
package epf.portlet.security;

import epf.client.security.Credential;
import epf.client.security.Token;
import epf.portlet.Event;
import epf.portlet.EventUtil;
import epf.portlet.Naming;
import epf.portlet.RequestUtil;
import epf.portlet.SessionUtil;
import epf.portlet.client.ClientUtil;
import epf.portlet.registry.RegistryUtil;
import epf.util.client.Client;
import epf.util.logging.Logging;
import epf.util.security.PasswordUtil;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author PC
 *
 */
@Named(Naming.SECURITY)
@RequestScoped
public class Security {
	
	/**
	 * 
	 */
	private static final Logger LOGGER = Logging.getLogger(Security.class.getName());
	
	/**
	 * 
	 */
	private final Credential credential = new Credential();
	
	/**
	 * 
	 */
	@Inject
	private transient RequestUtil request;
	
	/**
	 * 
	 */
	@Inject 
	private transient Session session;
	
	/**
	 * 
	 */
	@Inject
	private transient RegistryUtil registryUtil;
	
	/**
	 * 
	 */
	@Inject
	private transient ClientUtil clientUtil;
	
	/**
	 * 
	 */
	@Inject
	private transient EventUtil eventUtil;
	
	/**
	 * 
	 */
	@Inject
	private transient SessionUtil sessionUtil;
	 

	public Credential getCredential() {
		return credential;
	}
	
	/**
	 * @return
	 */
	public String login() {
		try {
			final URI securityUrl = registryUtil.get("security");
			final String passwordHash = PasswordUtil.hash(credential.getCaller(), credential.getPassword());
			final URL url = new URL(
					request.getRequest().getScheme(), 
					request.getRequest().getServerName(), 
					request.getRequest().getServerPort(), 
					""
					);
			String rawToken;
			try(Client client = clientUtil.newClient(securityUrl)){
				rawToken = epf.client.security.Security.login(
						client, 
						null, 
						credential.getCaller(), 
						passwordHash, 
						url
						);
			}
			Token token;
			try(Client client = clientUtil.newClient(securityUrl)){
				client.authorization(rawToken);
				token = epf.client.security.Security.authenticate(client, null);
			}
			token.setRawToken(rawToken);
			session.setToken(token);
			session.setSecurityUrl(securityUrl);
			sessionUtil.setAttribute(Naming.SECURITY_TOKEN, token);
			eventUtil.setEvent(Event.SECURITY_TOKEN, token);
			return "session";
		} 
		catch (Exception e) {
			LOGGER.throwing(getClass().getName(), "login", e);
		}
		return "";
	}
	
	/**
	 * @return
	 */
	public String logout() {
		try(Client client = clientUtil.newClient(registryUtil.get("security"))){
			client.authorization(session.getToken().getRawToken());
			epf.client.security.Security.logOut(client, null);
			session.setToken(null);
			sessionUtil.setAttribute(Naming.SECURITY_TOKEN, null);
			eventUtil.setEvent(Event.SECURITY_TOKEN, null);
		} 
		catch (Exception e) {
			LOGGER.throwing(getClass().getName(), "logout", e);
		}
		return "security";
	}
	
	/**
	 * @return
	 */
	public String update() {
		final Map<String, String> info = new HashMap<>();
		info.put("password", new String(credential.getPassword()));
		try(Client client = clientUtil.newClient(registryUtil.get("security"))){
			client.authorization(session.getToken().getRawToken());
			epf.client.security.Security.update(client, null, info);
		} 
		catch (Exception e) {
			LOGGER.throwing(getClass().getName(), "update", e);
		}
		return "security";
	}
	
	/**
	 * @return
	 */
	public String revoke() {
		try {
			final URI securityUrl = registryUtil.get("security");
			String rawToken;
			try(Client client = clientUtil.newClient(securityUrl)){
				client.authorization(session.getToken().getRawToken());
				rawToken = epf.client.security.Security.revoke(client, null);
			}
			Token token;
			try(Client client = clientUtil.newClient(securityUrl)){
				client.authorization(rawToken);
				token = epf.client.security.Security.authenticate(client, null);
			}
			token.setRawToken(rawToken);
			session.setToken(token);
			session.setSecurityUrl(securityUrl);
			sessionUtil.setAttribute(Naming.SECURITY_TOKEN, token);
			eventUtil.setEvent(Event.SECURITY_TOKEN, token);
		}
		catch (Exception e) {
			LOGGER.throwing(getClass().getName(), "revoke", e);
		}
		return "security";
	}
}
