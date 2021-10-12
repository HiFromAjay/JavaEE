/**
 * 
 */
package epf.shell.persistence;

import javax.ws.rs.core.Response;
import epf.client.gateway.GatewayUtil;
import epf.client.util.Client;
import epf.naming.Naming;
import epf.shell.Function;
import epf.shell.client.ClientUtil;
import epf.shell.security.Credential;
import epf.shell.security.CallerPrincipal;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * @author PC
 *
 */
@Command(name = Naming.PERSISTENCE)
@RequestScoped
@Function
public class Persistence {
	
	/**
	 * 
	 */
	@Inject
	private transient ClientUtil clientUtil;
	
	/**
	 * @param credential
	 * @param schema
	 * @param entity
	 * @param data
	 */
	@Command(name = "persist")
	public String persist(
			@ArgGroup(exclusive = true, multiplicity = "1")
			@CallerPrincipal
			final Credential credential,
			@Option(names = {"-s", "--schema"}, description = "Schema")
			final String schema,
			@Option(names = {"-e", "--entity"}, description = "Entity")
			final String entity, 
			@Option(names = {"-d", "--data"}, description = "Entity", interactive = true, echo = true)
			final String data) throws Exception {
		try(Client client = clientUtil.newClient(GatewayUtil.get(Naming.PERSISTENCE))){
			client.authorization(credential.getToken());
			try(Response response = epf.client.persistence.Entities.persist(client, schema, entity, data)){
				return response.readEntity(String.class);
			}
		}
	}
	
	/**
	 * @param credential
	 * @param schema
	 * @param entity
	 * @param entityId
	 * @param data
	 */
	@Command(name = "merge")
	public void merge(
			@ArgGroup(exclusive = true, multiplicity = "1")
			@CallerPrincipal
			final Credential credential,
			@Option(names = {"-s", "--schema"}, description = "Schema")
			final String schema,
			@Option(names = {"-e", "--entity"}, description = "Entity")
			final String entity, 
			@Option(names = {"-i", "--id"}, description = "ID")
			final String entityId,
			@Option(names = {"-d", "--data"}, description = "Entity", interactive = true, echo = true)
			final String data) throws Exception {
		try(Client client = clientUtil.newClient(GatewayUtil.get(Naming.PERSISTENCE))){
			client.authorization(credential.getToken());
			epf.client.persistence.Entities.merge(client, schema, entity, entityId, data);
		}
	}
	
	/**
	 * @param credential
	 * @param schema
	 * @param entity
	 * @param entityId
	 * @throws Exception
	 */
	@Command(name = "remove")
	public void remove(
			@ArgGroup(exclusive = true, multiplicity = "1")
			@CallerPrincipal
			final Credential credential,
			@Option(names = {"-s", "--schema"}, description = "Schema")
			final String schema,
			@Option(names = {"-e", "--entity"}, description = "Entity")
			final String entity, 
			@Option(names = {"-i", "--id"}, description = "ID")
			final String entityId) throws Exception {
		try(Client client = clientUtil.newClient(GatewayUtil.get(Naming.PERSISTENCE))){
			client.authorization(credential.getToken());
			epf.client.persistence.Entities.remove(client, schema, entity, entityId);
		}
	}
	
	/**
	 * @param credential
	 * @param schema
	 * @param entity
	 * @param entityId
	 */
	@Command(name = "find")
	public String find(
			@ArgGroup(exclusive = true, multiplicity = "1")
			@CallerPrincipal
			final Credential credential,
			@Option(names = {"-s", "--schema"}, description = "Schema")
			final String schema,
			@Option(names = {"-e", "--entity"}, description = "Entity")
			final String entity, 
			@Option(names = {"-i", "--id"}, description = "ID")
			final String entityId) throws Exception {
		try(Client client = clientUtil.newClient(GatewayUtil.get(Naming.PERSISTENCE))){
			client.authorization(credential.getToken());
			try(Response response = epf.client.persistence.Entities.find(client, schema, entity, entityId)){
				return response.readEntity(String.class);
			}
		}
	}
}
