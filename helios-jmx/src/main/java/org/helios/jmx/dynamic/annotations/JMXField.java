/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package org.helios.jmx.dynamic.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.helios.jmx.dynamic.annotations.options.AttributeMutabilityOption;

/**
 * <p>Title: JMXField</p>
 * <p>Description: Annotation to allow fine grained control of how an object's fields are exposed as attributes through JMX.</p> 
 * <p>Company: Helios Development Group</p>
 * @author Whitehead (whitehead.nicholas@gmail.com)
 * @version $Revision$
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface JMXField  {
	
	
	
	public static final String DEFAULT_DESCRIPTION = "MBean Managed Field";
	
	/**
	 * Should attribute change notificiations be broadcast.
	 * The implementation of this callback is dependent on the underlying managed object.
	 * In the simplest case of a <code>JMXManagedObject</code> POJO, the object manager can easilly determine if the
	 * attribute change is actually  effecting a change. However, in other cases such as reflected non-annotated objects, or
	 * dynamically annotated objects, this may not be deterministic. For example, if a managed object has a <code>set</code> 
	 * but not a <code>get</code>, the broadcast will fire every time, since we cannot make a determination if the 
	 * value has changed or not.
	 * @return if true, attribute change notificiations will be broadcast.
	 */
	boolean broadcastAttributeChange() default false;
	
	
	/**
	 * Should the attribute be exposed.
	 * @return if true, the attribute will be exposed.
	 */
	boolean expose() default true;
	
	
	/**
	 * The attribute name.
	 * If <code>@introspectName</code> is true, this should be a method name in the underlying object that will return the name.
	 * @return The attribute name.
	 */
	String name() default "";
	
	/**
	 * If introscopectName is true, the name of the attribute specified in the <code>@name</code> annotation will be interpreted as a method 
	 * in the underlying object. The attribute name will be set to the return value of this method. Accordingly, the method must return the name
	 * and have no parameters.
	 * @return true if introspectName is on.
	 */
	boolean introspectName() default false;
	
	/**
	 * If introspectDescription is true, the description of the attribute specified in the <code>@description</code> annotation will be interpreted as a method 
	 * in the underlying object. The attribute description will be set to the return value of this method. Accordingly, the method must return the description
	 * and have no parameters.
	 * @return true if introspectDescription is on.
	 */
	boolean introspectDescription() default false;
	
	
	
	/**
	 * The attribute description.
	 * If <code>@introspectDescription</code> is true, this should be a method name in the underlying object that will return the description.
	 * @return The attribute description.
	 */
	String description() default DEFAULT_DESCRIPTION;
	
	
	/**
	 * Controls the read/write mutability of the attribute.
	 * These are the current states defined in {@link AttributeMutability}:<ul>
	 * <li>READ_ONLY: The attribute can only be read and not writen.
	 * <li>WRITE_ONLY: The attribute can only be writen and not read.
	 * <li>READ_WRITE: The attribute can be read or writen.
	 * <li>WRITE_ONCE: The attribute can be read, but only writen once.
	 * </ul>
	 * The actual mutability effected may differ from the specified if the underlying managed object does not cooperate.</p> 
	 * @return The attribute's mutability.
	 */
	AttributeMutabilityOption mutability() default AttributeMutabilityOption.READ_ONLY;
	
	
	
	/**
	 * When set to true, marks the attribute elligible for persistence loads in a <code>PersistentMBean</code>.
	 * <b><i>Unimplemented</i></b>.
	 * @return true if the attribute should loaded.
	 */
	boolean persistentLoad() default false;
	
	/**
	 * When set to true, marks the attribute elligible for persistence stores in a <code>PersistentMBean</code>.
	 * <b><i>Unimplemented</i></b>.
	 * @return true if the attribute should stored.
	 */
	boolean persistentStore() default false;
	
	/**
	 * The optional name of the ObjectName of a MODB where field should be exposed as an attribute. Ignores if value is blank. 
	 * If value is "this", will register in the defining object if it is a MODB.
	 * If introspectObjectName is true, the value of this attribute will assumed to be the name of the method that will return
	 * the ObjectName string.
	 * @return an ObjectName, "this", blank or the name of the method in the object that will return the ObjectName string.
	 */
	String objectName() default "";
	
	/**
	 * The optional name of the JMX Domain where the target MBeanServer can be located. Ignores if value is blank.
	 * If the value is "this", the field will be registered in the same domain as the containing MODB.
	 * If introspectJmxDomain is true, the value of this attribute will assumed to be the name of the method that will return
	 * the JMX Domain.  
	 * @return a JMX Domain Name, blank, "this" or the name of the method in the object that will return the JMX Domain.
	 */
	String jmxDomain() default "";
	
	/**
	 * Indicates if the objectName attribute value should be introspected from the target object.
	 * @return
	 */
	boolean introscpectObjectName() default false;

	/**
	 * Indicates if the JMX Domain attribute value should be introspected from the target object.
	 * @return
	 */	
	boolean introscpectJmxDomain() default false;
	
}
