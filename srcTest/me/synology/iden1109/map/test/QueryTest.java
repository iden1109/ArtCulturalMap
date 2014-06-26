package me.synology.iden1109.map.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import me.synology.iden1109.map.controller.Query;
import me.synology.iden1109.map.model.Event;
import me.synology.iden1109.map.model.EventComponent;

import org.apache.hadoop.hbase.TableNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class QueryTest {

	private static final Logger LOG = LoggerFactory.getLogger(QueryTest.class);
	private Query q;
	private GeometryFactory factory = new GeometryFactory();
	
	@Before
	public void setUp() throws Exception {
		q = new Query();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testQueryNN() {
		Queue<EventComponent> queue;
		try {
			queue = q.queryNN(120.9809230, 24.8036511, 12);
			EventComponent e;
			while (( e = queue.poll()) != null) {
				LOG.info("RESULT: " + e.toString());
				//e.print();
			}
			Assert.assertNotNull("Found !!", queue);
			Assert.assertFalse("Queue not empty", !queue.isEmpty());
		} catch (TableNotFoundException e1) {
			Assert.fail(e1.getMessage());
		}
		
	}
	
	
	@Test
	public void testQuerybyGeometry(){

		double minLat = 24.08892134559496;
		double maxLat = 24.16333787305604;
		double minLon = 120.61093783637102;
		double maxLon = 120.73530650397356;

		Geometry geometry = q.queryEnvelope(factory, minLon, maxLon, minLat, maxLat);
		LOG.info("query geometry -> "+geometry);
		Queue<EventComponent> results;
		try {
			results = q.query(geometry, null, null);
			EventComponent e;
			int cnt = 0;
			while (( e = results.poll()) != null) {
				LOG.info("query result -> " + e.toString());
				//e.print();
				cnt++;
			}
			/*
			Set<EventComponent> results = q.query(geometry);
			for(EventComponent e : results){
				LOG.info("result: " + e.toString());
			}
			*/
			LOG.info("How many result : " + cnt);
			Assert.assertNotNull("Found !!", results);
			Assert.assertFalse("Queue not empty", !results.isEmpty());
		} catch (TableNotFoundException e1) {
			Assert.fail(e1.getMessage());
		}
		
	}
	
	

}
