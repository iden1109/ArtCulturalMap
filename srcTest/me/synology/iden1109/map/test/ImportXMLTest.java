package me.synology.iden1109.map.test;

import static org.junit.Assert.*;
import junit.framework.Assert;
import me.synology.iden1109.map.ImportXML;
import me.synology.iden1109.map.model.EventComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportXMLTest {

	private static final Logger LOG = LoggerFactory.getLogger(ImportXMLTest.class);
	private ImportXML imp;
	private EventComponent ec;
	
	@Before
	public void setUp() throws Exception {
		imp = new ImportXML();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testImport() {
		short expected = 34;
		ec = imp.loadXML("data/20140404_test_perform.xml"); //data/20140404_show.xml
		//ec.print();
		int cnt = imp.ingest(ec);//ec.ingest();
	
		Assert.assertEquals("Loading XML records count match", expected, ec.size());
		//Assert.assertEquals("Ingest records count match", expected*2, cnt);
	}


}
