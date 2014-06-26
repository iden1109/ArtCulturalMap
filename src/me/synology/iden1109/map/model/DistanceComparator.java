package me.synology.iden1109.map.model;

import java.awt.geom.Point2D;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DistanceComparator implements Comparator<EventComponent> {

	private static final Logger LOG = LoggerFactory.getLogger(DistanceComparator.class);

	private Point2D orig;
	
	
	public DistanceComparator(double lon, double lat){
		this.orig = new Point2D.Double(lon, lat);
	}
	
	public int compare(EventComponent o1, EventComponent o2) {
		if(Double.isNaN(o1.getDistance()))
			o1.setDistance(orig.distance(o1.getLongitude(), o1.getLatitude()));
		if(Double.isNaN(o2.getDistance()))
			o2.setDistance(orig.distance(o2.getLongitude(), o2.getLatitude()));
		
		if(o1.getDistance() < 0 || o2.getDistance() < 0)
			LOG.warn("!!! distance CAN NOT be negative !!!");
		
		return Double.compare(o1.getDistance(), o2.getDistance());
	}

}
