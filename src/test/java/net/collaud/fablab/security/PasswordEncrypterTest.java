package net.collaud.fablab.security;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gaetan
 */
public class PasswordEncrypterTest {
	
	public PasswordEncrypterTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}


	/**
	 * Test of encryptMdp method, of class PasswordEncrypter.
	 */
	@Test
	public void testEncryptMdp() {
		String mdp = "emf123";
		String expResult = "517019c04805bc62c786ba774254472382894a87d15260e93360efa4261c2f31";
		String result = PasswordEncrypter.encryptMdp(mdp);
		assertEquals(expResult, result);
	}
	
}
