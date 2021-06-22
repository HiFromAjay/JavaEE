/**
 * 
 */
package epf.portlet;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.portlet.Event;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.faces.BridgeEventHandler;
import javax.portlet.faces.event.EventNavigationResult;
import javax.xml.namespace.QName;

/**
 * @author PC
 *
 */
public class EventHandler implements BridgeEventHandler {
	
	@Override
	public EventNavigationResult handleEvent(final FacesContext facesContext, final Event event) {
		final PortletSession session = (PortletSession) facesContext.getExternalContext().getSession(false);
		session.setAttribute(event.getQName().toString(), event.getValue());
		final EventNavigationResult result = new EventNavigationResult();
		return result;
	}
	
	/**
	 * @param qname
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T getValue(final QName qname, final PortletRequest request) {
		return (T) request.getPortletSession().getAttribute(qname.toString());
	}
}
