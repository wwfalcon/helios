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
package org.helios.ot.helios;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Properties;

import org.helios.helpers.ConfigurationHelper;
import org.helios.helpers.InetAddressHelper;

/**
 * <p>Title: HeliosEndpointConfiguration</p>
 * <p>Description: Provides environment or system property overridable constant values for the {@link HeliosEndpoint}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ot.helios.HeliosEndpointConfiguration</code></p>
 */

public class HeliosEndpointConfiguration {
	/** The property name prefix for all connection properties */
	public static final String CONNECTION_PREFIX = "org.helios.ot.connection";	
	
	
	
	/** The property name for the helios ot server host name or ip address */
	public static final String HOST = CONNECTION_PREFIX + ".host";
	/** The property name for the helios ot server listening port */
	public static final String PORT = CONNECTION_PREFIX + ".port";
	/** The property name for the protocol the agent should use to comm with the helios-ot server */
	public static final String PROTOCOL = CONNECTION_PREFIX + ".protocol";
	/** The property name for the maximum size of the trace buffer before it is flushed */
	public static final String FLUSH_SIZE_MAX = CONNECTION_PREFIX + ".maxsize";
	/** The property name for the maximum amount of time in ms. before a non empty trace buffer is flushed */
	public static final String FLUSH_TIME_MAX = CONNECTION_PREFIX + ".maxtime";
	/** The property name for the connect timeout in ms. */
	public static final String CONNECT_TIMEOUT = CONNECTION_PREFIX + ".connect.timeout";
	/** The property name for synchronous operation timeouts in ms. */
	public static final String SYNCH_OP_TIMEOUT = CONNECTION_PREFIX + ".synchop.timeout";

	
	/** The default helios ot server host name or ip address */
	public static final String DEFAULT_HOST = "localhost";
	/** The default protocol the agent should use to comm with the helios-ot server (UDP or TCP) */
	public static final String DEFAULT_PROTOCOL = "TCP";
	/** The default maximum size of the trace buffer before it is flushed */
	public static final int DEFAULT_FLUSH_SIZE_MAX = 200;
	/** The default maximum amount of time in ms. before a non empty trace buffer is flushed */
	public static final long DEFAULT_FLUSH_TIME_MAX = 5000;
	/** The default connect timeout in ms. */
	public static final long DEFAULT_CONNECT_TIMEOUT = 3000;
	/** The default synchronous operation timeouts in ms. */
	public static final long DEFAULT_SYNCH_OP_TIMEOUT = 3000;
	
	//=============================================
	//   Discovery 
	//=============================================
	/** The property name prefix for discovery properties */
	public static final String DISCOVERY_PREFIX = CONNECTION_PREFIX + ".discovery";	
	/** The property name for the helios ot server discovery multicast network */
	public static final String DISCOVERY_NETWORK = DISCOVERY_PREFIX + ".network";
	/** The property name for the helios ot discovery enablement */
	public static final String DISCOVERY_ENABLED = DISCOVERY_PREFIX + ".enabled";
	
	/** The property name for the helios ot server discovery multicast port */
	public static final String DISCOVERY_PORT = DISCOVERY_PREFIX + ".port";
	/** The property name for the helios ot server discovery multicast timeout in ms. */
	public static final String DISCOVERY_TIMEOUT = DISCOVERY_PREFIX + ".timeout";
	/** The property name for the helios ot server discovery preferred protocol */
	public static final String DISCOVERY_PREF_PROTOCOL = DISCOVERY_PREFIX + ".prefprotocol";
	/** The property name for the helios ot server discovery listening interface */
	public static final String DISCOVERY_LISTEN_IFACE = DISCOVERY_PREFIX + ".interface";

	
	/** The default ot server discovery multicast network */
	public static final String DEFAULT_DISCOVERY_NETWORK = "224.9.3.7";
	/** The default ot server discovery multicast port */
	public static final int DEFAULT_DISCOVERY_PORT = 1836;
	/** The default helios ot server discovery multicast timeout in ms. */
	public static final int DEFAULT_DISCOVERY_TIMEOUT = 3000;
	/** The default helios ot server discovery preferred protocol */
	public static final String DEFAULT_DISCOVERY_PREF_PROTOCOL = "TCP";
	/** The default helios ot server discovery listening interface */
	public static final String DEFAULT_DISCOVERY_LISTEN_IFACE = InetAddressHelper.hostName();
	/** The default helios ot server discovery enablement */
	public static final boolean DEFAULT_DISCOVERY_ENABLED = true;
	
	//=============================================
	
	public static void main(String[] args) {
		try {
			for(Field f: HeliosEndpointConfiguration.class.getDeclaredFields()) {
				if(!Modifier.isStatic(f.getModifiers())) continue;
				System.out.println(f.getName() + ":" + f.get(null));
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * Captures a snapshot of all OT connection properties which means environment variables or system properties
	 * that start with {@link CONNECTION_PREFIX}. System properties will take presedence over environmental variables.
	 * @return a properties instance with all the OT connection properties that were set
	 */
	public static Properties getAllConnectionProperties() {
		Properties p = new Properties();
		for(Map.Entry<String, String> entry: System.getenv().entrySet()) {
			if(entry.getKey().startsWith(CONNECTION_PREFIX)) {
				p.setProperty(entry.getKey(), entry.getValue());
			}
		}
		for(Map.Entry<Object, Object> entry: System.getProperties().entrySet()) {
			String key = entry.getKey().toString();
			if(key.startsWith(CONNECTION_PREFIX)) {
				p.setProperty(key, entry.getValue().toString());
			}
		}
		return p;		
	}
	
	/**
	 * Returns the maximum amount of time in ms. before a non empty trace buffer is flushed
	 * @return the maximum amount of time in ms. before a non empty trace buffer is flushed
	 */
	public static long getMaxFlushTime() {
		return ConfigurationHelper.getLongSystemThenEnvProperty(FLUSH_TIME_MAX, DEFAULT_FLUSH_TIME_MAX);
	}
	
	/**
	 * Returns the connection timeout in ms.
	 * @return the connection timeout in ms.
	 */
	public static long getConnectTimeout() {
		return ConfigurationHelper.getLongSystemThenEnvProperty(CONNECT_TIMEOUT, DEFAULT_CONNECT_TIMEOUT);
	}
	
	
	/**
	 * Returns the maximum size of the trace buffer before it is flushed
	 * @return the maximum size of the trace buffer before it is flushed
	 */
	public static int getMaxFlushSize() {
		return ConfigurationHelper.getIntSystemThenEnvProperty(FLUSH_SIZE_MAX, DEFAULT_FLUSH_SIZE_MAX);
	}
	
	
	
	/**
	 * Returns the configured host
	 * @return the configured host
	 */
	public static String getHost() {
		return ConfigurationHelper.getSystemThenEnvProperty(HOST, DEFAULT_HOST);
	}

	/**
	 * Returns the configured port
	 * @return the configured port
	 */
	public static int getPort() {
		return ConfigurationHelper.getIntSystemThenEnvProperty(PORT, getProtocol().getDefaultPort());
	}
	
	/**
	 * Returns the configured protocol
	 * @return the configured protocol
	 */
	public static Protocol getProtocol() {
		return Protocol.forValue(ConfigurationHelper.getSystemThenEnvProperty(PROTOCOL, DEFAULT_PROTOCOL));
	}

	/**
	 * Returns the configured discovery response listening interface address
	 * @return the configured discovery response listening interface address
	 */
	public static String getDiscoveryListenAddress() {
		return ConfigurationHelper.getSystemThenEnvProperty(DISCOVERY_LISTEN_IFACE, DEFAULT_DISCOVERY_LISTEN_IFACE);
	}
	
	
	/**
	 * Returns the configured discovery preferred protocol
	 * @return the configured discovery preferred protocol
	 */
	public static String getDiscoveryPreferredProtocol() {
		return ConfigurationHelper.getSystemThenEnvProperty(DISCOVERY_PREF_PROTOCOL, DEFAULT_DISCOVERY_PREF_PROTOCOL);
	}
	
	
	
	
	/**
	 * Returns the configured discovery network
	 * @return the configured discovery network
	 */
	public static String getDiscoveryNetwork() {
		return ConfigurationHelper.getSystemThenEnvProperty(DISCOVERY_NETWORK, DEFAULT_DISCOVERY_NETWORK);
	}
	
	/**
	 * Returns the configured discovery port
	 * @return the configured discovery port
	 */
	public static int getDiscoveryPort() {
		return ConfigurationHelper.getIntSystemThenEnvProperty(DISCOVERY_PORT, DEFAULT_DISCOVERY_PORT);
	}
	
	/**
	 * Returns the configured discovery timeout in ms.
	 * @return the configured discovery timeout in ms.
	 */
	public static int getDiscoveryTimeout() {
		return ConfigurationHelper.getIntSystemThenEnvProperty(DISCOVERY_TIMEOUT, DEFAULT_DISCOVERY_TIMEOUT);
	}
	
	/**
	 * Returns the configured synchronous operation timeout in ms.
	 * @return the configured synchronous operation timeout in ms.
	 */
	public static long getSynchOpTimeout() {
		return ConfigurationHelper.getLongSystemThenEnvProperty(SYNCH_OP_TIMEOUT, DEFAULT_SYNCH_OP_TIMEOUT);
	}
	
	
	
	
	/**
	 * Returns the configured discovery enablement
	 * @return true if discovery is enabled, false otherwise
	 */
	public static boolean isDiscoveryEnabled() {
		return ConfigurationHelper.getBooleanSystemThenEnvProperty(DISCOVERY_ENABLED, DEFAULT_DISCOVERY_ENABLED);
	}
	
	
	
	
}
