/**
 * 
 */
package epf.portlet.persistence;

import epf.client.schema.Attribute;
import epf.client.schema.AttributeType;

/**
 * @author PC
 *
 */
public class BasicAttribute {

	/**
	 * 
	 */
	private final Attribute attribute;
	/**
	 * 
	 */
	private final EntityObject object;
	
	/**
	 * @param object
	 * @param attribute
	 */
	protected BasicAttribute(final EntityObject object, final Attribute attribute) {
		this.attribute = attribute;
		this.object = object;
	}
	
	/**
	 * @return
	 */
	public String getValue() {
		return AttributeUtil.getAsString(object.get(attribute.getName()));
	}
	
	/**
	 * @param value
	 */
	public void setValue(final String value) {
		AttributeUtil.setValue(object, attribute, value);
	}

	public Attribute getAttribute() {
		return attribute;
	}
	
	public boolean isBasic() {
		return AttributeUtil.isBasic(attribute);
	}
	
	public boolean isEmbedded() {
		return AttributeType.EMBEDDED.equals(attribute.getAttributeType());
	}
	
	public boolean isSingular() {
		return AttributeType.MANY_TO_ONE.equals(attribute.getAttributeType())
				|| AttributeType.ONE_TO_ONE.equals(attribute.getAttributeType());
	}
}
