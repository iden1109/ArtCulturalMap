package me.synology.iden1109.hbase.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import me.synology.iden1109.map.model.Record;
import me.synology.iden1109.hbase.HBase;


import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [GOAL] HBase Tester
 * 
 * 
 * @author zhengyu
 * @date 2013/10/25
 * date      		ver		programmer		description
 * =======================================================
 * 2013/10/25		0.1		zhengyu			initial
 *
 */
public class HBaseTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(HBaseTest.class);
	
	String table = "scores";
    String[] familys = {"grade", "course"};
    
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreateTable() {
        try {
			HBase.dropTable(table);
			HBase.createTable(table, familys);
			Assert.assertTrue("Create table OK!", HBase.existTable(table));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddRecord() {
		try {
			HBase.addRecord(table, "zkb", "grade", "", "5");
			HBase.addRecord(table, "zkb", "course", "", "90");
			HBase.addRecord(table, "zkb", "course", "math", "97");
			HBase.addRecord(table, "zkb", "course", "art", "87");
			HBase.addRecord(table, "baoniu", "grade", "", "4");
			HBase.addRecord(table, "baoniu", "course", "math", "89");
			List<Record> list = HBase.getRecords(table, "zkb");
			for(Record r : list){
				LOG.info(r.toString());
			}
			Assert.assertEquals("Add record count match", 4, list.size());			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDelRecord() {
		try {
			HBase.delRecord(table, "zkb");
			List<Record> list = HBase.getRecords(table, "zkb");
			Assert.assertEquals("Delete record OK", 0, list.size());			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetRecord() {
		try {
			String sRtn = HBase.getValue(table, "baoniu", "course", "math");
			Assert.assertEquals("record value match", "89", sRtn);			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testDropRecord() {
		try {
			HBase.dropTable(table);
			Assert.assertFalse("Drop table OK!", HBase.existTable(table));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
