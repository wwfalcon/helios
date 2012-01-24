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
package org.helios.net.ssh.auth;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.helios.helpers.FileHelper;
import org.helios.net.ssh.ApacheSSHDServer;
import org.helios.net.ssh.SSHAuthenticationException;
import org.helios.net.ssh.SSHService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * <p>Title: SSHAuthenticationTestCase</p>
 * <p>Description: Test cases to validate various different combinations of SSHd authentication.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.net.ssh.auth.SSHAuthenticationTestCase</code></p>
 */
public class SSHAuthenticationTestCase {
	/** The port the test server is listening on */
	protected static int SSHD_PORT = -1;
	/** Tracks the test name */
	@Rule
    public TestName testName= new TestName();
	/** Instance logger */
	protected static final Logger LOG = Logger.getLogger(SSHAuthenticationTestCase.class);
	
	/** Known good credentials  */
	static Map<Object, Object> goodPasswordAuths = FileHelper.loadProperties(new File("./src/test/resources/auth/password/credentials.properties"));
	/** Private Key Passphrases  */
	static Map<Object, Object> pkPassphrases = FileHelper.loadProperties(new File("./src/test/resources/auth/keys/passphrases.properties"));
	/** SSA Private Key Files keyed by user name */
	static Map<String, File> dsaPks = new HashMap<String, File>();	
	/** RSA Private Key Files keyed by user name */
	static Map<String, File> rsaPks = new HashMap<String, File>();
	
	
	/**
	 * Starts the test SSHD server
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ApacheSSHDServer.main();
		SSHD_PORT = ApacheSSHDServer.getPort(); 
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				System.out.println("\n\tStopping ApacheSSHDServer");
				try { ApacheSSHDServer.stop(true); } catch (Exception e) {} 
			}
		});
		// Load the RSA private keys
		for(File pk: new File("./src/test/resources/auth/keys").listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return name.endsWith("_rsa");
				}
			})) {
			rsaPks.put(pk.getName().replace("_rsa", ""), pk);
		}
		LOG.info("Loaded [" + rsaPks.size() + "] RSA Private Keys");
		// Load the DSA private keys
		for(File pk: new File("./src/test/resources/auth/keys").listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return name.endsWith("_dsa");
				}
			})) {
			dsaPks.put(pk.getName().replace("_dsa", ""), pk);
		}
		LOG.info("Loaded [" + dsaPks.size() + "] DSA Private Keys");
		
	}

	/**
	 * Stops the test SSHD server
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		try { ApacheSSHDServer.stop(true); } catch (Exception e) {} 
	}
	
	/**
	 * {@inheritDoc}
	 * @see junit.framework.TestCase#setUp()
	 */	
	@Before
	public void setUp() {
		ApacheSSHDServer.resetAuthenticators();
		String methodName = testName.getMethodName();
		LOG.debug("\n\t******\n\t Test [" + getClass().getSimpleName() + "." + methodName + "]\n\t******");
	}	

	/**
	 * Tests authentication with no credentials
	 * @throws Exception
	 */
	@Test
	public void testNoAuthSSHLogin() throws Exception {
		SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, "" + System.nanoTime())
		.sshUserPassword("" + System.nanoTime())
		.connect()
		.authenticate();
		Assert.assertEquals("The SSHService shared count", 1, ssh.getSharedCount());
		Assert.assertTrue("The SSHService is connected", ssh.isConnected());
		Assert.assertTrue("The SSHService is authenticated", ssh.isAuthenticated());
		ssh.close();
		Assert.assertEquals("The SSHService shared count", 0, ssh.getSharedCount());
		Assert.assertFalse("The SSHService is connected", ssh.isConnected());
		Assert.assertFalse("The SSHService is authenticated", ssh.isAuthenticated());		
	}
	
	/**
	 * Tests authentication with no credentials with an expected failure
	 * @throws Exception
	 */
	@Test(expected=SSHAuthenticationException.class)
	public void testBadNoAuthSSHLogin() throws Exception {
		ApacheSSHDServer.activatePasswordAuthenticator(true);
		SSHService.createSSHService("localhost", SSHD_PORT, "" + System.nanoTime())
		.sshUserPassword("" + System.nanoTime())
		.connect()
		.authenticate();
	}
	
	/**
	 * Tests authentication with known good username/password
	 * @throws Exception
	 */
	@Test()
	public void testPasswordSSHLogin() throws Exception {
		ApacheSSHDServer.activatePasswordAuthenticator(true);
		for(Map.Entry<Object, Object> creds: goodPasswordAuths.entrySet()) {
			SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, creds.getKey().toString())
				.sshUserPassword(creds.getValue().toString())
				.connect()
				.authenticate();
				Assert.assertEquals("The SSHService shared count", 1, ssh.getSharedCount());
				Assert.assertTrue("The SSHService is connected", ssh.isConnected());
				Assert.assertTrue("The SSHService is authenticated", ssh.isAuthenticated());
				ssh.close();
				Assert.assertEquals("The SSHService shared count", 0, ssh.getSharedCount());
				Assert.assertFalse("The SSHService is connected", ssh.isConnected());
				Assert.assertFalse("The SSHService is authenticated", ssh.isAuthenticated());					
		}
	}
	
	/**
	 * Tests authentication with known good username/password
	 * @throws Exception
	 */
	@Test()
	public void testSharedPasswordSSHLogin() throws Exception {
		ApacheSSHDServer.activatePasswordAuthenticator(true);		
		Map.Entry<Object, Object> creds = goodPasswordAuths.entrySet().iterator().next();
		SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, creds.getKey().toString())				
				.connect()
				.sshUserPassword(creds.getValue().toString())
				.authenticate();
		Assert.assertEquals("The SSHService1 shared count", 1, ssh.getSharedCount());
		Assert.assertTrue("The SSHService1 is shared connection", ssh.isSharedConnection());
		Assert.assertTrue("The SSHService1 is connected", ssh.isConnected());
		Assert.assertTrue("The SSHService1 is authenticated", ssh.isAuthenticated());
		
		SSHService ssh2 = SSHService.createSSHService("localhost", SSHD_PORT, creds.getKey().toString())				
				.connect()
				.sshUserPassword(creds.getValue().toString())
				.authenticate();
		Assert.assertEquals("The SSHService2 shared count", 2, ssh2.getSharedCount());	
		Assert.assertTrue("The SSHService2 is shared connection", ssh2.isSharedConnection());
		Assert.assertTrue("The SSHService2 is connected", ssh2.isConnected());
		Assert.assertTrue("The SSHService2 is authenticated", ssh2.isAuthenticated());

		Assert.assertEquals("The SSHService1 shared count", 2, ssh.getSharedCount());		
		Assert.assertTrue("The SSHService1 is connected", ssh.isConnected());
		Assert.assertTrue("The SSHService1 is authenticated", ssh.isAuthenticated());
		
		ssh.close();
		
		Assert.assertEquals("The SSHService2 shared count", 1, ssh2.getSharedCount());		
		Assert.assertTrue("The SSHService2 is connected", ssh2.isConnected());
		Assert.assertTrue("The SSHService2 is authenticated", ssh2.isAuthenticated());

		Assert.assertEquals("The SSHService1 shared count", 1, ssh.getSharedCount());		
		Assert.assertTrue("The SSHService1 is connected", ssh.isConnected());
		Assert.assertTrue("The SSHService1 is authenticated", ssh.isAuthenticated());
		
		ssh2.close();
		
		Assert.assertEquals("The SSHService2 shared count", 0, ssh2.getSharedCount());		
		Assert.assertFalse("The SSHService2 is connected", ssh2.isConnected());
		Assert.assertFalse("The SSHService2 is authenticated", ssh2.isAuthenticated());

		Assert.assertEquals("The SSHService1 shared count", 0, ssh.getSharedCount());		
		Assert.assertFalse("The SSHService1 is connected", ssh.isConnected());
		Assert.assertFalse("The SSHService1 is authenticated", ssh.isAuthenticated());
		
	}
	
	/**
	 * Tests authentication with known good username/password
	 * @throws Exception
	 */
	@Test()
	public void testExclusivePasswordSSHLogin() throws Exception {
		ApacheSSHDServer.activatePasswordAuthenticator(true);
		Map.Entry<Object, Object> creds = goodPasswordAuths.entrySet().iterator().next();		
		SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, creds.getKey().toString(), false, false)				
				.connect()
				.sshUserPassword(creds.getValue().toString())
				.authenticate();
		Assert.assertEquals("The SSHService1 shared count", 1, ssh.getSharedCount());
		Assert.assertFalse("The SSHService1 is shared connection", ssh.isSharedConnection());
		Assert.assertTrue("The SSHService1 is connected", ssh.isConnected());
		Assert.assertTrue("The SSHService1 is authenticated", ssh.isAuthenticated());
		
		SSHService ssh2 = SSHService.createSSHService("localhost", SSHD_PORT, creds.getKey().toString(), false, false)				
				.connect()
				.sshUserPassword(creds.getValue().toString())
				.authenticate();
		Assert.assertEquals("The SSHService2 shared count", 1, ssh2.getSharedCount());
		Assert.assertFalse("The SSHService2 is shared connection", ssh2.isSharedConnection());
		Assert.assertTrue("The SSHService2 is connected", ssh2.isConnected());
		Assert.assertTrue("The SSHService2 is authenticated", ssh2.isAuthenticated());

		SSHService ssh3 = SSHService.createSSHService("localhost", SSHD_PORT, creds.getKey().toString())				
				.connect()
				.sshUserPassword(creds.getValue().toString())
				.authenticate();
		Assert.assertEquals("The SSHService3 shared count", 1, ssh3.getSharedCount());
		Assert.assertTrue("The SSHService3 is shared connection", ssh3.isSharedConnection());
		Assert.assertTrue("The SSHService3 is connected", ssh3.isConnected());
		Assert.assertTrue("The SSHService3 is authenticated", ssh3.isAuthenticated());
		
		SSHService ssh4 = SSHService.createSSHService("localhost", SSHD_PORT, creds.getKey().toString())				
				.connect()
				.sshUserPassword(creds.getValue().toString())
				.authenticate();
		Assert.assertEquals("The SSHService4 shared count", 2, ssh4.getSharedCount());
		Assert.assertTrue("The SSHService4 is shared connection", ssh4.isSharedConnection());
		Assert.assertTrue("The SSHService4 is connected", ssh4.isConnected());
		Assert.assertTrue("The SSHService4 is authenticated", ssh4.isAuthenticated());
		
		ssh3.close();
		
		Assert.assertEquals("The SSHService3 shared count", 1, ssh3.getSharedCount());
		Assert.assertTrue("The SSHService3 is connected", ssh3.isConnected());
		Assert.assertTrue("The SSHService3 is authenticated", ssh3.isAuthenticated());
		
		Assert.assertEquals("The SSHService4 shared count", 1, ssh4.getSharedCount());
		Assert.assertTrue("The SSHService4 is connected", ssh4.isConnected());
		Assert.assertTrue("The SSHService4 is authenticated", ssh4.isAuthenticated());
		
		ssh4.close();

		Assert.assertEquals("The SSHService3 shared count", 0, ssh3.getSharedCount());
		Assert.assertFalse("The SSHService3 is connected", ssh3.isConnected());
		Assert.assertFalse("The SSHService3 is authenticated", ssh3.isAuthenticated());
		
		Assert.assertEquals("The SSHService4 shared count", 0, ssh4.getSharedCount());
		Assert.assertFalse("The SSHService4 is connected", ssh4.isConnected());
		Assert.assertFalse("The SSHService4 is authenticated", ssh4.isAuthenticated());
		
		ssh2.close();
		Assert.assertEquals("The SSHService2 shared count", 0, ssh2.getSharedCount());
		Assert.assertFalse("The SSHService2 is connected", ssh2.isConnected());
		Assert.assertFalse("The SSHService2 is authenticated", ssh2.isAuthenticated());
		
		ssh.close();
		Assert.assertEquals("The SSHService1 shared count", 0, ssh.getSharedCount());
		Assert.assertFalse("The SSHService1 is connected", ssh.isConnected());
		Assert.assertFalse("The SSHService1 is authenticated", ssh.isAuthenticated());
		
	}
	
	/**
	 * Tests authentication with RSA public/private key and known good passphrase
	 * @throws Exception
	 */
	@Test()
	public void testSimpleRSAPublicKeySSHLogin() throws Exception {		
		ApacheSSHDServer.activateKeyAuthenticator(true);
		int usersTested = 0;
		for(Map.Entry<Object, Object> pkp: pkPassphrases.entrySet()) {
			if(pkp.getValue()==null || pkp.getValue().toString().trim().isEmpty()) continue;
			String userName = pkp.getKey().toString();
			String passphrase = pkp.getValue().toString();
			File pkFile = rsaPks.get(userName);
			
			SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, userName)				
					.connect()
					.sshPassphrase(passphrase)
					.pemPrivateKeyFile(pkFile)					
					.authenticate();
			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 1, ssh.getSharedCount());
			Assert.assertTrue("The SSHService1 [" + userName + "] is shared connection", ssh.isSharedConnection());
			Assert.assertTrue("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertTrue("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
			
			ssh.close();

			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 0, ssh.getSharedCount());
			Assert.assertFalse("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertFalse("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
						
			usersTested++;
		}
		Assert.assertTrue("Tested at least 2 users", usersTested > 1);
	}
	
	/**
	 * Tests authentication with SSA public/private key and known good passphrase
	 * @throws Exception
	 */
	@Test()
	public void testSimpleDSAPublicKeySSHLogin() throws Exception {		
		ApacheSSHDServer.activateKeyAuthenticator(true);
		int usersTested = 0;
		for(Map.Entry<Object, Object> pkp: pkPassphrases.entrySet()) {
			if(pkp.getValue()==null || pkp.getValue().toString().trim().isEmpty()) continue;
			String userName = pkp.getKey().toString();
			String passphrase = pkp.getValue().toString();
			File pkFile = dsaPks.get(userName);
			
			SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, userName)				
					.connect()
					.sshPassphrase(passphrase)
					.pemPrivateKeyFile(pkFile)					
					.authenticate();
			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 1, ssh.getSharedCount());
			Assert.assertTrue("The SSHService1 [" + userName + "] is shared connection", ssh.isSharedConnection());
			Assert.assertTrue("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertTrue("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
			
			ssh.close();

			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 0, ssh.getSharedCount());
			Assert.assertFalse("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertFalse("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
						
			usersTested++;
		}
		Assert.assertTrue("Tested at least 2 users", usersTested > 1);
	}
	
	/**
	 * Tests authentication with RSA public/private key with no passphrase
	 * @throws Exception
	 */
	@Test()
	public void testSimpleNoPassPhraseRSAPublicKeySSHLogin() throws Exception {		
		ApacheSSHDServer.activateKeyAuthenticator(true);
		int usersTested = 0;
		for(Map.Entry<Object, Object> pkp: pkPassphrases.entrySet()) {
			if(!pkp.getValue().toString().trim().isEmpty()) continue;
			String userName = pkp.getKey().toString();			
			File pkFile = rsaPks.get(userName);
			SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, userName)				
					.connect()
					.pemPrivateKeyFile(pkFile)					
					.authenticate();
			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 1, ssh.getSharedCount());
			Assert.assertTrue("The SSHService1 [" + userName + "] is shared connection", ssh.isSharedConnection());
			Assert.assertTrue("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertTrue("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
			
			ssh.close();

			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 0, ssh.getSharedCount());
			Assert.assertFalse("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertFalse("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
						
			usersTested++;
		}
		Assert.assertTrue("Tested at least 1 user", usersTested > 0);
	}
	
	/**
	 * Tests authentication with DSA public/private key with no passphrase
	 * @throws Exception
	 */
	@Test()
	public void testSimpleNoPassPhraseDSAPublicKeySSHLogin() throws Exception {		
		ApacheSSHDServer.activateKeyAuthenticator(true);
		int usersTested = 0;
		for(Map.Entry<Object, Object> pkp: pkPassphrases.entrySet()) {
			if(!pkp.getValue().toString().trim().isEmpty()) continue;
			String userName = pkp.getKey().toString();			
			File pkFile = dsaPks.get(userName);
			SSHService ssh = SSHService.createSSHService("localhost", SSHD_PORT, userName)				
					.connect()
					.pemPrivateKeyFile(pkFile)					
					.authenticate();
			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 1, ssh.getSharedCount());
			Assert.assertTrue("The SSHService1 [" + userName + "] is shared connection", ssh.isSharedConnection());
			Assert.assertTrue("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertTrue("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
			
			ssh.close();

			Assert.assertEquals("The SSHService1 [" + userName + "] shared count", 0, ssh.getSharedCount());
			Assert.assertFalse("The SSHService1 [" + userName + "] is connected", ssh.isConnected());
			Assert.assertFalse("The SSHService1 [" + userName + "] is authenticated", ssh.isAuthenticated());
						
			usersTested++;
		}
		Assert.assertTrue("Tested at least 1 user", usersTested > 0);
	}
	

	
}
