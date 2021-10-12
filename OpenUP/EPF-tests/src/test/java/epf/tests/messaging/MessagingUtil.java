/**
 * 
 */
package epf.tests.messaging;

import java.net.URI;
import java.net.URISyntaxException;
import epf.naming.Naming;

/**
 * @author PC
 *
 */
public class MessagingUtil {

	private static URI messagingUri;
	
	public static URI getMessagingUrl() throws URISyntaxException {
		if(messagingUri == null) {
			messagingUri = new URI(System.getProperty(Naming.Messaging.MESSAGING_URL));
		}
		return messagingUri;
	}
}
