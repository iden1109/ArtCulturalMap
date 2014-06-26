package me.synology.iden1109.map.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.synology.iden1109.hbase.HBase;
import me.synology.iden1109.map.filter.QueryFilter;
import me.synology.iden1109.map.model.DistanceComparator;
import me.synology.iden1109.map.model.Event;
import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.model.EventItem;
import me.synology.iden1109.map.util.JSonUtil;
import me.synology.iden1109.map.util.TimeStampUtil;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;

import com.google.common.collect.MinMaxPriorityQueue;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;

public class Query extends HBase{
	
	private static final Logger LOG = LoggerFactory.getLogger(Query.class);
	
	private static final int PRECESION = 7;
	private static final int QUERY_LENGTH = 3;
	
	private static ConcurrentHashMap<String, Queue<EventComponent>> resultCache = new ConcurrentHashMap<String, Queue<EventComponent>>();
	private GeometryFactory factory = new GeometryFactory();

	
	public Query(){
	}
	
	
	/**
	 * populate model object for scanbyPrefix()
	 */
	@Override
	protected EventComponent populate(Result r) {
		EventComponent e = new Event();
		EventComponent ei = new EventItem();
		String f, k, v;
		for(KeyValue kv : r.list()){
			f = new String(kv.getFamily());
			k = new String(kv.getQualifier());
			v = new String(kv.getValue());
			if(f.equalsIgnoreCase(EventComponent.FAMILYS[0])){// e
				e.putAttr(k, v);
			}
			if(f.equalsIgnoreCase(EventComponent.FAMILYS[1])){// s
				ei = JSonUtil.decode(v);
				e.add(ei);
			}
			//LOG.info(new String(kv.getFamily())+":"+new String(kv.getQualifier())+" ["+new String(kv.getValue())+"]");
		}
		e.setRowkey(new String(r.getRow()));
		
		return e;
	}
	
	
	/**
	 * Retrieve EventComponent info base on ROWKEY similar with prefix 
	 * @param comp   Comparator of EventComponent
	 * @param prefix    prefix of ROWKEY
	 * @param maxSize    max size of retrieved data
	 * @return
	 */
	protected Queue<EventComponent> takeNeighbors(Comparator<EventComponent> comp, String prefix, int maxSize) throws TableNotFoundException{
		Queue<EventComponent> queue = MinMaxPriorityQueue.orderedBy(comp).maximumSize(maxSize).create();

		Queue<EventComponent> queueCache = getfromCache(prefix);
		if(queueCache == null){
			try {
				this.scanbyPrefix(EventComponent.TABLE, EventComponent.FAMILYS, prefix, queue);
				LOG.info(String.format("Scan '%s' returned %s candidates.", prefix, queue.size()));
			} catch (TableNotFoundException e1) {
				throw e1;
			} catch (IOException e) {
				LOG.error("Failed to scan table "+EventComponent.TABLE+" with family ["+EventComponent.FAMILYS.toString()+"]", e);
			}
			addtoCache(prefix, queue, comp);
		}else{
			queue.addAll(queueCache);
		}
		return queue;
	}
	
	/**
	 * Query nearest neighbors, included N, NE, E, SE, S, SW, W, NW and central (9 areas)
	 * @param lon    Longitude
	 * @param lat    Latitude
	 * @param maxSize   max size of retrieved data
	 * @return
	 */
	public Queue<EventComponent> queryNN(double lon, double lat, int maxSize) throws TableNotFoundException{
		long start = System.currentTimeMillis();
		DistanceComparator comp = new DistanceComparator(lon, lat);
		Queue<EventComponent> queue = MinMaxPriorityQueue.orderedBy(comp).maximumSize(maxSize).create(); //Limit results, nearest, sorted by distance

		GeoHash center = GeoHash.withCharacterPrecision(lat, lon, PRECESION);
		queue.addAll(this.takeNeighbors(comp, center.toBase32(), maxSize)); //target
		for(GeoHash h : center.getAdjacent()){ //for all neighbors
			queue.addAll(this.takeNeighbors(comp, h.toBase32(), maxSize));
		}
		
		long end = System.currentTimeMillis();
		LOG.info(String.format(" %s records in %sms.", queue.size(), end - start));
		return queue;
	}
	
	public Set<EventComponent> queryAll(){
		long start = System.currentTimeMillis();
		
		Set<EventComponent> set = new HashSet<EventComponent>();
		try {
			this.scanAll(EventComponent.TABLE, EventComponent.FAMILYS, set);
			LOG.info(String.format("Scan all returned %s candidates.", set.size()));
		} catch (IOException e) {
			LOG.error("Failed to scan table "+EventComponent.TABLE+" with family ["+EventComponent.FAMILYS.toString()+"]", e);
		}
		
		LOG.info(String.format(" %s records in %sms.", set.size(), System.currentTimeMillis() - start));
		return set;
	}
	
	/**
	 * Query by Geometry rectangle
	 * @param query
	 * @return
	 */
	public Queue<EventComponent> query(Geometry geometry, String startDate, String endDate) throws TableNotFoundException{
		long start = System.currentTimeMillis();
		
		if(startDate!=null && !startDate.equals("")){
			LOG.info("===  set startDate -> "+startDate+" "+endDate+ "  ===");
		}

		Point center = geometry.getCentroid();
		DistanceComparator comp = new DistanceComparator(center.getX(), center.getY());
		Queue<EventComponent> queue = MinMaxPriorityQueue.orderedBy(comp).create();
		Queue<EventComponent> queueCache;
		GeoHash[] prefixes = this.minBoundingPrefix(geometry);
		//Filter qfilter = new QueryFilter(query);
		for(GeoHash prefix : prefixes){
			byte[] p = prefix.toBase32().getBytes();
			String pStr = new String(p);
			LOG.info("prefix -> "+pStr);
			
			if(startDate!=null && !startDate.equals(""))
				queueCache = getfromCache(pStr+startDate+endDate);
			else
				queueCache = getfromCache(pStr);
			if(queueCache == null){
				if(pStr.length() <= QUERY_LENGTH)
					continue;
				try {
					scanbyPrefix(EventComponent.TABLE, EventComponent.FAMILYS, p, startDate, endDate, queue);
				} catch (TableNotFoundException e1) {
					throw e1;
				} catch (IOException e) {
					LOG.error("Failed to scan table "+EventComponent.TABLE+" with family ["+EventComponent.FAMILYS.toString()+"]", e);
				}
				if(startDate!=null && !startDate.equals(""))
					addtoCache(pStr+startDate+endDate, queue, comp);
				else
					addtoCache(pStr, queue, comp); //put new query result into cache
				
			}else{
				queue.addAll(queueCache);
			}
		}
		
		LOG.info(String.format("query() returned %s candidates in %sms", queue.size(), System.currentTimeMillis() - start));
		return queue;
	}

	public Queue<EventComponent> querybyRowkey(String rowkey, String startDate, String endDate) throws TableNotFoundException{
		long start = System.currentTimeMillis();
		
		if(startDate!=null && !startDate.equals("")){
			LOG.info("===  set startDate -> "+startDate+" "+endDate+ "  ===");
		}

		//DistanceComparator comp = new DistanceComparator(120.9809230, 24.8036511);
		//Queue<EventComponent> queue = MinMaxPriorityQueue.orderedBy(comp).create();
		Queue<EventComponent> queue = new LinkedList<EventComponent>();
		try {
			scanbyRowkey(EventComponent.TABLE, EventComponent.FAMILYS, rowkey, startDate, endDate, queue);
		} catch (TableNotFoundException e1) {
			throw e1;
		} catch (IOException e) {
			LOG.error("Failed to scan table "+EventComponent.TABLE+" with family ["+EventComponent.FAMILYS.toString()+"]", e);
		}

		LOG.info(String.format("queryByRowkey() returned %s candidates in %sms", queue.size(), System.currentTimeMillis() - start));
		return queue;
	}
	
	/**
	 * translate google bounds to a query geometry
	 * @param gf   GeometryFactory
	 * @param minLon
	 * @param maxLon
	 * @param minLat
	 * @param maxLat
	 * @return Geometry
	 */
	public Geometry queryEnvelope(GeometryFactory gf, double minLon, double maxLon, double minLat, double maxLat) {
		LinearRing ret = gf.createLinearRing(
				new Coordinate[] {
					new Coordinate(minLon, minLat),
					new Coordinate(minLon, maxLat),
					new Coordinate(maxLon, maxLat),
					new Coordinate(maxLon, minLat),
					new Coordinate(minLon, minLat)});
		
		return gf.createPolygon(ret, new LinearRing[] {});
	}
	
	
	/**
	 * Scan by prefix, start date and end date
	 * scan default in 3 months range if the end date is not setting.
	 * @param table
	 * @param family
	 * @param prefix
	 * @param startDate
	 * @param endDate
	 * @param queue
	 * @throws IOException
	 */
	public void scanbyPrefix(String table, String[] family, byte[] prefix, String startDate, String endDate, Queue<EventComponent> queue) throws TableNotFoundException, IOException {
		HTable ht = connectTable(table);
		
		Scan scan = new Scan(prefix);
		scan.setFilter(new PrefixFilter(prefix));
		for(String fami : family)
			scan.addFamily(Bytes.toBytes(fami));
		scan.setMaxVersions(1);
		scan.setCaching(CACHE_SIZE);
		
		prepareTimeRange(startDate, endDate, scan);
		 
		ResultScanner scanner = ht.getScanner(scan);
		for (Result r : scanner) {
			queue.add(populate(r));
		}
	}
	
	/**
	 * Retrieve prefixes of minimized boundaries
	 * @param geoQuery
	 * @return Array of GeoHash
	 */
	private GeoHash[] minBoundingPrefix(Geometry geoQuery){
		GeoHash hash;
		Geometry geometry;
		Point center = geoQuery.getCentroid();
		//LOG.info("center -> "+center);
		for(int precision=7 ; precision>0 ; precision--){
			hash = GeoHash.withCharacterPrecision(center.getY(), center.getX(), precision);
			geometry = this.convex(new GeoHash[]{hash});
			
			if(geometry.contains(geoQuery)){
				return new GeoHash[]{hash};
			}
				
			geometry = this.convex(hash.getAdjacent());
			if(geometry.contains(geoQuery)){
				GeoHash[] hashes = Arrays.copyOf(hash.getAdjacent(), 9);
				hashes[8] = hash;
				return hashes;
			}
		}
		
		throw new IllegalArgumentException("Geometry cannot by contained by GeoHashes");
	}
	
	private Geometry convex(GeoHash[] hashes){
		Set<Coordinate> coords = new HashSet<Coordinate>();
		for(GeoHash hash : hashes){
			coords.addAll(getCoords(hash));
		}
		Geometry geometry = factory.createMultiPoint(coords.toArray(new Coordinate[0]));
		return geometry.convexHull();
	}
	
	/**
	 * translates GeoHash to Coordinate set
	 * @param hash
	 * @return Coordinate set
	 */
	private Set<Coordinate> getCoords(GeoHash hash){
		BoundingBox box = hash.getBoundingBox();
		Set<Coordinate> coords = new HashSet<Coordinate>();
		coords.add(new Coordinate(box.getMinLon(), box.getMinLat()));
		coords.add(new Coordinate(box.getMinLon(), box.getMaxLat()));
		coords.add(new Coordinate(box.getMaxLon(), box.getMaxLat()));
		coords.add(new Coordinate(box.getMaxLon(), box.getMinLat()));
		return coords;
	}
	
	private Queue<EventComponent> getfromCache(final String prefix) {
		return resultCache.get(prefix);
	}
	
	private void addtoCache(final String prefix, final Queue<EventComponent> queue, Comparator<EventComponent> comp) {
		Queue<EventComponent> found = resultCache.get(prefix);
		if (found == null) {
			Queue<EventComponent> newQ = MinMaxPriorityQueue.orderedBy(comp).create();
			newQ.addAll(queue);
			found = resultCache.putIfAbsent(prefix, newQ);
		}
		if (found != null && found != queue) {
			throw new IllegalStateException("prefix=" + prefix + " => queue=" + queue + ", already mapped to " + found);
		}
	}
	
	private void dropCache(){
		resultCache.clear();
	}
}
