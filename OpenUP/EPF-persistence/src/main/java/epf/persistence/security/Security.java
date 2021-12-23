/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epf.persistence.security;

import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.CredentialValidationResult.Status;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.jwt.config.Names;
import epf.naming.Naming;
import epf.persistence.internal.Application;
import epf.persistence.internal.Session;
import epf.persistence.security.auth.EPFPrincipal;
import epf.persistence.security.auth.IdentityStore;
import epf.persistence.security.otp.OTPIdentityStore;
import epf.security.client.SecurityInterface;
import epf.security.client.jwt.JWT;
import epf.security.client.jwt.TokenUtil;
import epf.security.schema.Token;
import epf.util.logging.LogManager;
import epf.util.security.KeyUtil;

/**
 *
 * @author FOXCONN
 */
@Path(Naming.SECURITY)
@RolesAllowed(Naming.Security.DEFAULT_ROLE)
@RequestScoped
public class Security implements epf.security.client.Security, epf.security.client.otp.OTPSecurity, Serializable, SecurityInterface {
    
    /**
    * 
    */
    private static final long serialVersionUID = 1L;
    
    /**
     * 
     */
    private static final Logger LOGGER = LogManager.getLogger(Security.class.getName());
    
    /**
     * 
     */
    private transient PrivateKey privateKey;
    
    /**
     * 
     */
    @Inject
    private transient Application persistence;
    
    /**
     * 
     */
    @Inject
    private transient OTPIdentityStore otpIdentityStore;
    
    /**
     * 
     */
    @Inject
    @ConfigProperty(name = JWT.PRIVATE_KEY)
    private transient String privateKeyText;
    
    /**
     * 
     */
    @Inject
    @ConfigProperty(name = Names.ISSUER)
    private transient String issuer;
    
    /**
     * 
     */
    @Inject
    @ConfigProperty(name = JWT.EXPIRE_DURATION)
    private transient Long expireAmount;
    
    /**
     * 
     */
    @Inject
    @ConfigProperty(name = JWT.EXPIRE_TIMEUNIT)
    private transient ChronoUnit expireTimeUnit;
    
    /**
     * 
     */
    @Inject
    private transient IdentityStore identityStore;
    
    /**
     * 
     */
    @Context 
    private transient SecurityContext context;
    
    /**
     * 
     */
    @PostConstruct
    protected void postConstruct(){
        try {
            privateKey = KeyUtil.generatePrivate("RSA", privateKeyText);
        } 
        catch (Exception ex) {
            LOGGER.throwing(getClass().getName(), "postConstruct", ex);
        }
    }
    
    /**
     * @param username
     * @param url
     * @return
     */
    protected Token newToken(final String name, final Set<String> groups, final URL audience) {
    	final long now = Instant.now().getEpochSecond();
    	final Token token = new Token();
    	token.setAudience(new HashSet<>(Arrays.asList(String.format(AUDIENCE_FORMAT, audience.getProtocol(), audience.getHost(), audience.getPort()))));
    	token.setClaims(new HashMap<>());
    	token.setExpirationTime(now + Duration.of(expireAmount, expireTimeUnit).getSeconds());
    	token.setGroups(groups);
    	token.setIssuedAtTime(now);
    	token.setIssuer(issuer);
    	token.setName(name);
    	token.setSubject(name);
    	return token;
    }
    
    /**
     * @param jsonWebToken
     * @return
     */
    protected Token buildToken(final JsonWebToken jsonWebToken, final Set<String> groups) {
    	final long now = Instant.now().getEpochSecond();
		final Token token = TokenUtil.from(jsonWebToken);
		token.setClaims(TokenUtil.getClaims(jsonWebToken));
		token.setExpirationTime(now + Duration.of(expireAmount, expireTimeUnit).getSeconds());
		token.setIssuedAtTime(now);
		token.setGroups(groups);
		return token;
    }
    
    @PermitAll
    @Override
    public String login(
            final String username,
            final String passwordHash,
            final URL url) throws Exception {
    	final Password password = new Password(passwordHash);
    	final UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
    	persistence.findSession(username).ifPresent(session -> {
    		if(session.getPrincipal().equals(credential)) {
    			throw new BadRequestException();
    		}
    		else {
    			throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).build());
    		}
    	});
    	final CredentialValidationResult validationResult = identityStore.validate(credential);
    	if(Status.VALID.equals(validationResult.getStatus()) && validationResult.getCallerPrincipal() instanceof EPFPrincipal) {
    		final Set<String> roles = identityStore.getCallerGroups(validationResult.getCallerPrincipal());
    		final Token token = newToken(username, roles, url);
    		final TokenBuilder builder = new TokenBuilder(token, privateKey);
    		final Token newToken = builder.build();
    		final EPFPrincipal principal = (EPFPrincipal) validationResult.getCallerPrincipal();
    		persistence.putSession(principal, newToken);
    		return newToken.getRawToken();
    	}
    	throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).build());
    }
    
    @Override
    public String logOut() throws Exception {
    	final JsonWebToken jwt = (JsonWebToken)context.getUserPrincipal();
    	final Session session = persistence.removeSession(jwt).orElseThrow(ForbiddenException::new);
    	session.close();
    	session.getPrincipal().close();
    	return jwt.getName();
    }
    
    @Override
    public Token authenticate() {
    	final JsonWebToken jwt = (JsonWebToken)context.getUserPrincipal();
    	final Session session = persistence.getSession(jwt).orElseThrow(() -> new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).build()));
    	final Set<String> groups = identityStore.getCallerGroups(session.getPrincipal());
    	final Token token = TokenUtil.from(jwt);
		token.setClaims(TokenUtil.getClaims(jwt));
		token.setGroups(groups);
		token.setRawToken(null);
		return token;
    }
    
    @Override
	public void update(final String password) {
    	final JsonWebToken jwt = (JsonWebToken)context.getUserPrincipal();
    	final Session session = persistence.getSession(jwt).orElseThrow(ForbiddenException::new);
    	identityStore.setCallerPassword(session.getPrincipal(), new Password(password));
	}

	@Override
	public String revoke() throws Exception {
		final JsonWebToken jwt = (JsonWebToken)context.getUserPrincipal();
		final Session session = persistence.removeSession(jwt).orElseThrow(ForbiddenException::new);
		final Set<String> groups = identityStore.getCallerGroups(session.getPrincipal());
		session.close();
		final Token token = buildToken(jwt, groups);
		final TokenBuilder builder = new TokenBuilder(token, privateKey);
		final Token newToken = builder.build();
		persistence.putSession(session.getPrincipal(), newToken);
		return newToken.getRawToken();
	}

	@PermitAll
	@Override
	public String loginOneTime(final String username, final String passwordHash, final  URL url) throws Exception {
		final Password password = new Password(passwordHash);
    	final UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
    	final CredentialValidationResult validationResult = identityStore.validate(credential);
    	if(Status.VALID.equals(validationResult.getStatus()) && validationResult.getCallerPrincipal() instanceof EPFPrincipal) {
    		final Set<String> roles = identityStore.getCallerGroups(validationResult.getCallerPrincipal());
    		final Token token = newToken(username, roles, url);
    		final TokenBuilder builder = new TokenBuilder(token, privateKey);
    		final Token newToken = builder.build();
    		otpIdentityStore.putToken(newToken);
    		return newToken.getTokenID();
    	}
    	throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).build());
	}

	@PermitAll
	@Override
	public String authenticateOneTime(final String oneTimePassword) {
		return otpIdentityStore
				.removeToken(oneTimePassword)
				.orElseThrow(() -> new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).build()))
				.getRawToken();
	}

	@ActivateRequestContext
	@Override
	public Token login(final String username, final String passwordHash, final String url) throws RemoteException {
		try {
			final Password password = new Password(passwordHash);
	    	final UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
	    	final CredentialValidationResult validationResult = identityStore.validate(credential);
	    	final Set<String> roles = identityStore.getCallerGroups(validationResult.getCallerPrincipal());
    		final Token token = newToken(username, roles, new URL(url));
    		final TokenBuilder builder = new TokenBuilder(token, privateKey);
    		final Token newToken = builder.build();
    		final EPFPrincipal principal = (EPFPrincipal) validationResult.getCallerPrincipal();
    		persistence.putSession(principal, newToken);
    		return newToken;
		}
		catch(Exception ex) {
			throw new RemoteException(ex.getMessage(), ex);
		}
	}

	@ActivateRequestContext
	@Override
	public Token authenticate(final Token token) throws RemoteException {
		final Session session = persistence.getSession(token).orElseThrow(() -> new RemoteException());
    	return session.getToken();
	}

	@ActivateRequestContext
	@Override
	public String logout(final Token token) throws RemoteException {
		final Session session = persistence.removeSession(token).orElseThrow(() -> new RemoteException());
		session.close();
		return session.getToken().getName();
	}
}
