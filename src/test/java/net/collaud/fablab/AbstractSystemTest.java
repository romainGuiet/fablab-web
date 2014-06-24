package net.collaud.fablab;

import net.collaud.fablab.cron.system.Door;
import net.collaud.fablab.cron.system.SimpleHost;
import net.collaud.fablab.exceptions.FablabException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gaetan
 */
public class AbstractSystemTest {
	
	public static final String HOST_1 = "myhost1.fablab.local";
	public static final String HOST_2 = "myhost2.fablab.local";
	public static final int APP_PORT_1 = 8083;
	
	public AbstractSystemTest() {
	}
	
	@Test
	public void door() throws FablabException {
		Door door1 = new Door(HOST_1, APP_PORT_1);
		door1.setAlarmOn(true);
		door1.setDoorOpen(false);
		door1.setPingAppOk(true);
		door1.setPingIcmpOk(true);
		
		String xml = door1.marshal();
		
		System.out.println(xml);
		
		Door door2 = Door.unmarshal(xml);
		
		Assert.assertEquals("Host are not the same", door1.getHost(), door2.getHost());
		Assert.assertEquals("App port are not the same", door1.getAppPingPort(), door2.getAppPingPort());
		Assert.assertEquals("Ping icmp are not the same", door1.isPingIcmpOk(), door2.isPingIcmpOk());
		Assert.assertEquals("ping app are not the same", door1.isPingAppOk(), door2.isPingAppOk());
		Assert.assertEquals("DoorOpen are not the same", door1.isDoorOpen(), door2.isDoorOpen());
		Assert.assertEquals("AlarmOn are not the same", door1.isAlarmOn(), door2.isAlarmOn());
	}
	
	@Test
	public void simplehost() throws FablabException {
		SimpleHost door1 = new SimpleHost(HOST_2);
		door1.setPingIcmpOk(false);
		
		String xml = door1.marshal();
		
		System.out.println(xml);
		
		SimpleHost door2 = SimpleHost.unmarshal(xml);
		
		Assert.assertEquals("Host are not the same", door1.getHost(), door2.getHost());
		Assert.assertEquals("Ping icmp are not the same", door1.isPingIcmpOk(), door2.isPingIcmpOk());
	}

	
}
