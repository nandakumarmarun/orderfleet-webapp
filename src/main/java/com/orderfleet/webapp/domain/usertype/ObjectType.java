package com.orderfleet.webapp.domain.usertype;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Type Mapping for java class and JSON data type of postgresql
 * 
 * @author Shaheer
 * @since October 21, 2016
 * 
 * 
 * @param UserType
 * @param ParameterizedType
 * 
 * @see https://docs.jboss.org/hibernate/orm/4.1/javadocs/org/hibernate/usertype/UserType.html
 * 
 */
public class ObjectType implements UserType , ParameterizedType {
	
	private static final Logger logger = LoggerFactory.getLogger(ObjectType.class);
	
	public static final String CLASS_TYPE = "classType";

	private static final int[] SQL_TYPES = new int[] { Types.LONGVARCHAR };

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final TypeReference<Class<?>> CLASS_TYPE_REF = new TypeReference<Class<?>>() {
	};

	private JavaType valueType = null;

	private Class<?> classType = null;

	
	@Override
	public void setParameterValues(Properties parameters) {
		String type = parameters.getProperty("type");
		if (type.equals(CLASS_TYPE)) {
			if (parameters.getProperty("element") != null) {
				try {
					valueType = OBJECT_MAPPER.getTypeFactory().constructType(Class.forName(parameters.getProperty("element")));
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException("Type " + type + " is not a valid type.");
				}
			} else {
				valueType = OBJECT_MAPPER.getTypeFactory().constructType(CLASS_TYPE_REF);
			}
			classType = valueType.getClass();
		}

	}

	/**
	 * Reconstruct an object from the cacheable representation. At the very
	 * least this method should perform a deep copy if the type is mutable.
	 * (optional operation)
	 *
	 * @param cached
	 *            the object to be cached
	 * @param owner
	 *            the owner of the cached object
	 * @return a reconstructed object from the cachable representation
	 * @throws HibernateException
	 */
	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	/**
	 * Return a deep copy of the persistent state, stopping at entities and
	 * collections. It is not necessary to copy immutable objects, or null
	 * values, in which case it is safe to simple return the argument.
	 *
	 * @param value
	 *            the object to be cloned, which may be null
	 *
	 * @return object a copy
	 * @throws HibernateException
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	/**
	 * Transform the object into its cacheable representation. At the very least
	 * this method should perform a deep copy if the type is mutable. That may
	 * not be enough for some implementations, however; for example,
	 * associations must be cached as identifier values. (optional operation)
	 *
	 * @param value
	 *            the object to be cached
	 * @return a cachable representation of the object
	 * @throws HibernateException
	 */
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	/**
	 * Compare two instances of the class mapped by this type for persistence
	 * "equality". Equality of the persistence state.
	 *
	 * @param x
	 * @param y
	 * @return boolean
	 * @throws HibernateException
	 */
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
			return Objects.equals(x, y);
	}

	/**
	 * Get a hashcode for the instance, consistent with persistence "equality".
	 * 
	 * @param x
	 */
	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}

	/**
	 * Are objects of this type mutable?
	 *
	 * @return boolean
	 */
	@Override
	public boolean isMutable() {
		return true;
	}

	/**
	 * Retrieve an instance of the mapped class from a JDBC resultset.
	 * Implementors should handle possibility of null values.
	 *
	 * @param rs
	 *            a JDBC result set
	 * @param names
	 *            the column names
	 * @param session
	 * @param owner
	 *            the containing entity
	 * @return
	 * @throws HibernateException
	 * @throws SQLException
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		return nullSafeGet(rs, names, owner);
	}

	/**
	 * Write an instance of the mapped class to a prepared statement.
	 * Implementors should handle possibility of null values. A multi-column
	 * type should be written to parameters starting from <tt>index</tt>
	 *
	 * @param st
	 *            a JDBC prepared statement
	 * @param value
	 *            the object to write
	 * @param index
	 *            statement parameter index
	 * @param session
	 * @throws HibernateException
	 * @throws SQLException
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		nullSafeSet(st, value, index);

	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
		String value = rs.getString(names[0]);
		Object result = null;
		if (valueType == null) {
			throw new HibernateException("Value type not set.");
		}
		if (value != null && !value.equals("")) {
			try {
				result = OBJECT_MAPPER.readValue(value, valueType);
			} catch (IOException e) {
				throw new HibernateException("Exception deserializing value " + value, e);
			}
		}
		return result;
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
		StringWriter sw = new StringWriter();
		if (value == null) {
			st.setNull(index, Types.OTHER);
		} else {
			try {
				OBJECT_MAPPER.writeValue(sw, value);
				st.setObject(index, sw.toString(), Types.OTHER);
			} catch (Exception e) {
				logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  Error : " + e.getMessage());
				logger.error(e.getMessage(), e);
				throw new HibernateException("Exception serializing value " + value, e);
			}
		}
	}

	/**
	 * During merge, replace the existing (target) values in the entity we are
	 * merging to with a new (original) value from the detched entity we are
	 * merging. For immutable objects, or null values, it is safe to return a
	 * copy of the first parameter. For the objects with component values, it
	 * might make sense to recursively replace component values
	 *
	 * @param original
	 *            the value from the detched entity being merged
	 * @param target
	 *            the value in the managed entity
	 * @param owner
	 * @return the value to be merged
	 * @throws HibernateException
	 */
	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}

	/**
	 * The class returned by <tt>nullSafeGet()</tt>
	 *
	 * @return Class
	 */
	@Override
	public Class<?> returnedClass() {
		return this.classType;
	}

	/**
	 * Returns the SQL type codes for the columns mapped by this type. The codes
	 * are defined on <tt>java.sql.Types</tt>
	 *
	 * @return int[] the typecodes
	 * @see java.sql.Types
	 */
	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

}
