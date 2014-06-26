package me.synology.iden1109.map.model;

import java.io.IOException;

import me.synology.iden1109.hbase.HBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class EventComponent implements Cloneable {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventComponent.class);
	
	public static final String TABLE = "event";
	public static final String[] FAMILYS = new String[] { "e", "s" };
	
	public EventComponent(){
		try {
			if(!HBase.existTable(TABLE)){
				HBase.createTable(TABLE, FAMILYS);
			}
		} catch (IOException e) {
			LOG.error("Failed to Create table " + TABLE + " with family ["
					+ FAMILYS.toString() + "]");
		}
	}
	
	public void add(EventComponent component){
		throw new UnsupportedOperationException();
	}
	
	public void remove(EventComponent component){
		throw new UnsupportedOperationException();
	}
	
	public void clearItems(){
		throw new UnsupportedOperationException();
	}
	
	public int size(){
		throw new UnsupportedOperationException();
	}
	
	public EventComponent getChild(int i){
		throw new UnsupportedOperationException();
	}
	
	public void putAttr(String k, String v){
		throw new UnsupportedOperationException();
	}
	
	public String getAttr(String k){
		throw new UnsupportedOperationException();
	}
	
	public void setRowkey(String rowkey){
		throw new UnsupportedOperationException();
	}
	
	public void setLongitude(double distance) {
		throw new UnsupportedOperationException();
	}
	
	public void setLatitude(double v) {
		throw new UnsupportedOperationException();
	}

	public void setDistance(double v) {
		throw new UnsupportedOperationException();
	}
	
	public void setEndDateTimestamp(long endDateTimestamp) {
		throw new UnsupportedOperationException();
	}
	
	public String getRowkey(){
		throw new UnsupportedOperationException();
	}
	
	public double getLongitude(){
		throw new UnsupportedOperationException();
	}
	
	public double getLatitude(){
		throw new UnsupportedOperationException();
	}
	
	public double getDistance() {
		throw new UnsupportedOperationException();
	}
	
	public long getEndDateTimestamp() {
		throw new UnsupportedOperationException();
	}
	

	public int ingest(){
		throw new UnsupportedOperationException();
	}
	
	public void print(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public EventComponent clone() throws CloneNotSupportedException{
		return (EventComponent) super.clone();
	}
}
