/**
 * 
 */
package epf.messaging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.eclipse.microprofile.context.ManagedExecutor;
import epf.client.messaging.MessageDecoder;
import epf.client.messaging.MessageEncoder;
import epf.util.logging.Logging;
import epf.util.websocket.Server;

/**
 * @author PC
 *
 */
@ServerEndpoint(value = "/messaging/{path}/{event}", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
@ApplicationScoped
public class Event {
	
	/**
	 * 
	 */
	private static final Logger LOGGER = Logging.getLogger(Event.class.getName());
	
	/**
	 * 
	 */
	private final static String PATH = "path";
	
	/**
	 * 
	 */
	private final static String EVENT = "event";
	
	/**
	 * 
	 */
	private final transient Map<String, Map<String, Server>> servers = new ConcurrentHashMap<>();
	
	/**
	 * 
	 */
	@Inject
	private transient ManagedExecutor executor;
	
	/**
	 * 
	 */
	@PostConstruct
	protected void postConstruct() {
		Map<String, Server> server = new ConcurrentHashMap<>();
		Server event = new Server();
		server.put("shell", event);
		servers.put("schedule", server);
		executor.submit(event);
	}
	
	/**
	 * 
	 */
	@PreDestroy
	protected void preDestroy() {
		servers.values().parallelStream().forEach(server -> {
			server.values().parallelStream().forEach(event -> {
				try {
					event.close();
				} 
				catch (Exception e) {
					LOGGER.throwing(LOGGER.getName(), "preDestroy", e);
				}
			});
			server.clear();
		});
		servers.clear();
	}

	/**
	 * @param path
	 * @param event
	 * @param session
	 */
	@OnOpen
    public void onOpen(@PathParam(PATH) final String path, @PathParam(EVENT) final String event, final Session session) {
		servers.computeIfPresent(path, (p, server) -> {
			server.computeIfPresent(event, (e, eventServer) -> {
				eventServer.onOpen(session);
				return eventServer;
			});
			return server;
			}
		);
	}
	
	/**
	 * @param path
	 * @param event
	 * @param session
	 * @param closeReason
	 */
	@OnClose
    public void onClose(@PathParam(PATH) final String path, @PathParam(EVENT) final String event, final Session session, final CloseReason closeReason) {
		servers.computeIfPresent(path, (p, server) -> {
			server.computeIfPresent(event, (e, eventServer) -> {
				eventServer.onClose(session, closeReason);
				return eventServer;
			});
			return server;
			}
		);
	}
	
	/**
	 * @param path
	 * @param event
	 * @param message
	 * @param session
	 */
	@OnMessage
    public void onMessage(@PathParam(PATH) final String path, @PathParam(EVENT) final String event, final String message, final Session session) {
		servers.computeIfPresent(path, (p, server) -> {
			server.computeIfPresent(event, (e, eventServer) -> {
				eventServer.onMessage(message, session);
				return eventServer;
			});
			return server;
			}
		);
	}
	
	/**
	 * @param path
	 * @param event
	 * @param session
	 * @param throwable
	 */
	@OnError
    public void onError(@PathParam(PATH) final String path, @PathParam(EVENT) final String event, final Session session, final Throwable throwable) {
		servers.computeIfPresent(path, (p, server) -> {
			server.computeIfPresent(event, (e, eventServer) -> {
				eventServer.onError(session, throwable);
				return eventServer;
			});
			return server;
			}
		);
	}
}
