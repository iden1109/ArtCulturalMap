package me.synology.iden1109.map.test;

import static org.junit.Assert.*;
import junit.framework.Assert;
import me.synology.iden1109.map.ImportXML;
import me.synology.iden1109.map.model.EventComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImportXMLLocalData {

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
	public void importCate1() {
		short expected = 459;
		ec = imp.loadXML("data/20140404_Cate1.xml"); //data/20140404_show.xml
		//ec.print();
		int cnt = imp.ingest(ec);//ec.ingest();
	
		Assert.assertEquals("Loading XML records count match", expected, ec.size());
		Assert.assertEquals("Ingest records count match", 1054, cnt);
	}
	
	@Test
	public void importCate2() {
		short expected = 171;
		ec = imp.loadXML("data/20140404_Cate2.xml"); //data/20140404_show.xml
		//ec.print();
		int cnt = imp.ingest(ec);//ec.ingest();
	
		Assert.assertEquals("Loading XML records count match", expected, ec.size());
		Assert.assertEquals("Ingest records count match", 1152, cnt);
	}
	
	@Test
	public void importCate6() {
		short expected = 279;
		ec = imp.loadXML("data/20140404_Cate6.xml"); //data/20140404_show.xml
		//ec.print();
		int cnt = imp.ingest(ec);//ec.ingest();
	
		Assert.assertEquals("Loading XML records count match", expected, ec.size());
		Assert.assertEquals("Ingest records count match", expected*2, cnt);
	}
	
	@Test
	public void importCate17() {
		short expected = 13;
		ec = imp.loadXML("data/20140404_Cate17.xml"); //data/20140404_show.xml
		//ec.print();
		int cnt = imp.ingest(ec);//ec.ingest();
	
		Assert.assertEquals("Loading XML records count match", expected, ec.size());
		Assert.assertEquals("Ingest records count match", 29, cnt);
	}

}
