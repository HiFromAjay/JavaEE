package epf.util.logging;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class Factory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Produces 
	public Logger getLogger(InjectionPoint injectionPoint) {
		return Logging.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
}
