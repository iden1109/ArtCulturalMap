package me.synology.iden1109.map.model.test;

import static org.junit.Assert.*;
import junit.framework.Assert;
import me.synology.iden1109.map.model.UId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UIdTest {

	private String table = "uid";
    private String type = "cols";
    private UId uid;
    
	@Before
	public void setUp() throws Exception {
		//uid = new UId(table, type);
		uid = UId.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetId() {
		Assert.assertEquals("00001", uid.getId("test"));
		Assert.assertEquals("00001", uid.getId("test"));
		Assert.assertEquals("test", uid.getName("00001"));
	}
	

}
