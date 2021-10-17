package epf.client.file;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * @author PC
 *
 */
public class PathEvent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	private String source;
	
	/**
	 * 
	 */
	private String context;
	
	/**
	 * 
	 */
	private int count;
	
	/**
	 * 
	 */
	private EventKind kind;
	
	/**
	 * 
	 */
	public PathEvent() {
		
	}

	/**
	 * @param path
	 * @param context
	 * @param count
	 * @param kind
	 */
	public PathEvent(final Path path, final Object context, final int count, final EventKind kind) {
		this.source = path.toString();
		this.context = String.valueOf(context);
		this.count = count;
		this.kind = kind;
	}

	public int getCount() {
		return count;
	}

	public EventKind getKind() {
		return kind;
	}

	public String getContext() {
		return context;
	}

	public String getSource() {
		return source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	public void setCount(final int count) {
		this.count = count;
	}

	public void setKind(final EventKind kind) {
		this.kind = kind;
	}
}
