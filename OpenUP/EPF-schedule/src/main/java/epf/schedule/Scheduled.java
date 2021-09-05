/**
 * 
 */
package epf.schedule;

import java.io.Closeable;
import java.io.Serializable;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import epf.util.logging.Logging;
import epf.util.websocket.Message;
import epf.util.websocket.MessageQueue;

/**
 * @author PC
 *
 */
public class Scheduled implements Runnable, Serializable, Closeable {
	
	/**
	 * 
	 */
	private static transient final Logger LOGGER = Logging.getLogger(Scheduled.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private transient final MessageQueue messages;
	
	/**
	 * 
	 */
	private final long id;
	
	/**
	 * 
	 */
	private ScheduledFuture<?> scheduled;
	
	/**
	 * @param id
	 * @param messages
	 */
	public Scheduled(final long id, final MessageQueue messages) {
		this.messages = messages;
		this.id = id;
	}

	@Override
	public void run() {
		try(Jsonb jsonb = JsonbBuilder.create()){
			messages.add(new Message(jsonb.toJson(this)));
		} 
		catch (Exception e) {
			LOGGER.throwing(LOGGER.getName(), "run", e);
		}
	}

	public void setScheduled(final ScheduledFuture<?> scheduled) {
		this.scheduled = scheduled;
	}

	public long getId() {
		return id;
	}
	
	public boolean isCancelled() {
		return scheduled.isCancelled();
	}
	
	public boolean isDone() {
		return scheduled.isDone();
	}
	
	public long getTime() {
		return Instant.now().toEpochMilli();
	}

	@Override
	public void close() {
		this.scheduled.cancel(true);
	}
}
