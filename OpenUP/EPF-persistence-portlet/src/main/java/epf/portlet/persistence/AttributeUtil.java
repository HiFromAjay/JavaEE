/**
 * 
 */
package epf.portlet.persistence;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import epf.client.schema.Attribute;
import epf.client.schema.AttributeType;
import epf.client.schema.Entity;

/**
 * @author PC
 *
 */
public class AttributeUtil {
	
	/**
	 * @param attribute
	 * @return
	 */
	public static boolean isBasic(final Attribute attribute) {
		return !attribute.isAssociation() 
				&& !attribute.isCollection() 
				&& AttributeType.BASIC.equals(attribute.getAttributeType());
	}
	
	/**
	 * @param entity
	 * @param attribute
	 * @return
	 */
	public static String getId(final Entity entity, final Attribute attribute) {
		return entity.getType() + "." + attribute.getName();
	}
	
	/**
	 * @param builder
	 * @param attribute
	 */
	static void addDefault(final JsonObjectBuilder builder, final Attribute attribute) {
		if(BigDecimal.class.getName().equals(attribute.getType())) {
			builder.add(attribute.getName(), BigDecimal.ZERO);
		}
		else if(BigInteger.class.getName().equals(attribute.getType())) {
			builder.add(attribute.getName(), BigInteger.ZERO);
		}
		else if(Boolean.class.getName().equals(attribute.getType())) {
			builder.add(attribute.getName(), false);
		}
		else if(Double.class.getName().equals(attribute.getType())) {
			builder.add(attribute.getName(), Double.valueOf(0));
		}
		else if(Integer.class.getName().equals(attribute.getType())) {
			builder.add(attribute.getName(), Integer.valueOf(0));
		}
		else if(Long.class.getName().equals(attribute.getType())) {
			builder.add(attribute.getName(), Long.valueOf(0));
		}
		else if(String.class.getName().equals(attribute.getType())) {
			builder.add(attribute.getName(), "");
		}
		else if(attribute.isCollection()) {
			builder.add(attribute.getName(), JsonValue.EMPTY_JSON_ARRAY);
		}
		else if(attribute.isAssociation()) {
			builder.add(attribute.getName(), JsonValue.EMPTY_JSON_OBJECT);
		}
		else if(AttributeType.BASIC.equals(attribute.getAttributeType())) {
			builder.addNull(attribute.getName());
		}
		else {
			builder.add(attribute.getName(), JsonValue.EMPTY_JSON_OBJECT);
		}
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static String getAsString(final JsonValue value) {
		String string = null;
		if(value instanceof JsonString) {
			string = ((JsonString)value).getString();
		}
		else if(value != null){
			string = value.toString();
		}
		return string;
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static List<String> getAsStrings(final JsonValue value) {
		return null;
	}
	
	/**
	 * @param object
	 * @param attribute
	 * @param value
	 */
	public static void setValue(final EntityObject object, final Attribute attribute, final String value) {
		if(BigDecimal.class.getName().equals(attribute.getType())) {
			object.put(attribute.getName(), Json.createValue(new BigDecimal(value)));
		}
		else if(BigInteger.class.getName().equals(attribute.getType())) {
			object.put(attribute.getName(), Json.createValue(new BigInteger(value)));
		}
		else if(Boolean.class.getName().equals(attribute.getType())) {
			final boolean b = Boolean.valueOf(value);
			object.put(attribute.getName(), b ? JsonValue.TRUE : JsonValue.FALSE);
		}
		else if(Double.class.getName().equals(attribute.getType())) {
			object.put(attribute.getName(), Json.createValue(Double.valueOf(value)));
		}
		else if(Integer.class.getName().equals(attribute.getType())) {
			object.put(attribute.getName(), Json.createValue(Integer.valueOf(value)));
		}
		else if(Long.class.getName().equals(attribute.getType())) {
			object.put(attribute.getName(), Json.createValue(Long.valueOf(value)));
		}
		else if(String.class.getName().equals(attribute.getType())) {
			object.put(attribute.getName(), Json.createValue(value));
		}
		else if (value != null && !value.isEmpty()){
			try(StringReader reader = new StringReader(value)){
				try(JsonReader jsonReader = Json.createReader(reader)){
					final JsonValue jsonValue = jsonReader.readValue();
					object.put(attribute.getName(), jsonValue);
				}
			}
		}
	}
	
	/**
	 * @param object
	 * @param attribute
	 * @param values
	 */
	public static void setValues(final EntityObject object, final Attribute attribute, final List<String> values) {
		
	}
}
