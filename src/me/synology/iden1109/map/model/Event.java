package me.synology.iden1109.map.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.synology.iden1109.hbase.HBase;
import me.synology.iden1109.map.util.TimeStampUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.geohash.GeoHash;


public class Event extends EventComponent{
	
	private static final Logger LOG = LoggerFactory.getLogger(Event.class);
	
	private static final int REGIONSERVER_NUM = 10;
	public static final String UUID = "UID";
	public static final String LONGITUDE = "longitude";
	public static final String LATITUDE = "latitude";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String TITLE = "title";
	private Map<String, String> attributes;
	private ArrayList<EventComponent> components;
	private UId uid;
	
	private String rowkey;
	private double lon;
	private double lat;
	private double distance = Double.NaN;


	public Event() {
		attributes = new HashMap<String, String>();
		components = new ArrayList<EventComponent>();
		uid = UId.getInstance();
	}
	
	
	public void add(EventComponent component){
		components.add(component);
	}
	
	public void remove(EventComponent component){
		components.remove(component);
	}
	
	public void clearItems(){
		components.clear();
	}
	
	public int size(){
		return components.size();
	}
	
	public EventComponent getChild(int i) throws IndexOutOfBoundsException{
		return (EventComponent)components.get(i);
	}
	
	/**
	 * For store element tag attribute
	 */
	public void putAttr(String k, String v){
		attributes.put(k, v);
	}
	
	public String getAttr(String k){
		return attributes.get(k);
	}
	
	
	public void setRowkey(String rowkey){
		this.rowkey = rowkey;
	}
	
	public void setLongitude(double v) {
		if(this.lon > 0 && v > 0){
			this.getChild(0).putAttr(uid.getId(LONGITUDE), String.valueOf(v));
			this.lon = v;
		}
	}
	
	public void setLatitude(double v) {
		if(this.lat > 0 && v > 0){
			this.getChild(0).putAttr(uid.getId(LATITUDE), String.valueOf(v));
			this.lat = v;
		}
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public String getRowkey(){
		if(rowkey == null || rowkey.isEmpty()){
			String geoHashKey = GeoHash.withCharacterPrecision(getLatitude(), getLongitude(), 12).toBase32();
			String uuid = attributes.get(uid.getId(UUID));
			String time = retrieveEndDateTime();
			long ts = transRevsTimeStamp(time);
			rowkey = geoHashKey +"_"+ uuid +"_"+ ts;
		}
		return rowkey;
	}
	
	public double getLongitude(){
		if(this.lon <= 0){
			try{
				String lon = getChildValue(0, LONGITUDE);
				if(lon == null || lon.equals(""))
					return this.lon = Double.NaN;
				this.lon = Double.parseDouble(lon);
			}catch(IndexOutOfBoundsException e){
				return this.lon = Double.NaN;
			}
		}
		return this.lon;
	}
	
	public double getLatitude(){
		if(this.lat <= 0){
			try{
				String lat = getChildValue(0, LATITUDE);
				if(lat == null || lat.equals(""))
					return this.lat = Double.NaN;
				this.lat = Double.parseDouble(lat);
			}catch(IndexOutOfBoundsException e){
				return this.lat = Double.NaN;
			}
		}
		return this.lat;
	}
	
	public double getDistance() {
		return distance;
	}


	
	/**
	 * Insert all of Event to HBase
	 */
	public int ingest(){
		int cnt = 0;
		long ts = transTimeStamp(retrieveEndDateTime());
		if(attributes.size() > 0){
			try {
				HBase.addRecords(TABLE, this.getRowkey(), FAMILYS[0], attributes, ts);
				cnt++;
			} catch (IOException e) {
				LOG.error("Failed to Put!  row: " + this.getRowkey() + " of CF: "+ FAMILYS[0], e);
			}
		}
		Iterator<EventComponent> ite = components.iterator();
		while(ite.hasNext()){
			EventComponent child = ite.next();
			//LOG.info(" this key --> "+ this.getRowkey());
			//LOG.info(" com  key --> "+ com.getRowkey());
			if(isReplaceKey(this, child)){
				child.setRowkey(this.getRowkey());
				child.setEndDateTimestamp(ts);
			}
			cnt+= child.ingest();
		}
		return cnt;
	}
	
	/**
	 * print all of EventComponents info to log file
	 */
	public void print(){
		LOG.info("=== event ===");
		LOG.info(this.toString());
		for(String k : attributes.keySet()){
			LOG.info("event: "+k+" -> "+attributes.get(k));
		}
		
		LOG.info("=== showinfo ===");
		Iterator<EventComponent> ite = components.iterator();
		int cnt = 1;
		while(ite.hasNext()){
			LOG.info("=== "+cnt+" ===");
			EventComponent com = ite.next();
			com.print();
			cnt++;
		}
	}

	@Override
	public String toString() {
		return String.format("<Event -> key:%12s, Lon:%3.7f, Lat:%3.7f, distance:%2.5f >", this.getRowkey(), this.getLongitude(), this.getLatitude(), distance);
	}
	
	/**
	 * Output JSON format for Ajax request of client
	 * @return
	 */
	public EventJSON toJSON(){
		
		EventJSON event = new EventJSON();
		event.UID = getValue(UUID);
		event.title = getValue("title");
		event.category = getValue("category");
		event.showUnit = getValue("showUnit");
		event.descriptionFilterHtml = getValue("descriptionFilterHtml");
		event.discountInfo = getValue("discountInfo");
		event.imageUrl = getValue("imageUrl");
		event.masterUnit = getValue("masterUnit");
		event.subUnit = getValue("subUnit");
		event.supportUnit = getValue("supportUnit");
		event.otherUnit = getValue("otherUnit");
		event.webSales = getValue("webSales");
		event.sourceWebPromote = getValue("sourceWebPromote");
		event.comment = getValue("comment");
		event.editModifyDate = getValue("editModifyDate");
		event.sourceWebName = getValue("sourceWebName");
		event.startDate = getValue("startDate");
		event.endDate = getValue("endDate");
		event.hitRate = getValue("hitRate");
		
		event.time = getChildValue(0, "time");
		event.location = getChildValue(0, "location");
		event.locationName = getChildValue(0, "locationName");
		event.onsales = getChildValue(0, "onsales");
		event.latitude = getChildValue(0, LATITUDE);
		event.longitude = getChildValue(0, LONGITUDE);
		event.price = getChildValue(0, "price");
		event.endTime = getChildValue(0, "endTime");
		
		for(int i=0;i<this.size();i++){
			EventItemJSON item = new EventItemJSON();
			item.time = getChildValue(i, "time");
			item.location = getChildValue(i, "location");
			item.locationName = getChildValue(i, "locationName");
			item.onsales = getChildValue(i, "onsales");
			item.latitude = getChildValue(i, LATITUDE);
			item.longitude = getChildValue(i, LONGITUDE);
			item.price = getChildValue(i, "price");
			item.endTime = getChildValue(i, "endTime");
			event.showinfo.add(item);
		}
		
		return event;
	}
	
	@Override
	public EventComponent clone() throws CloneNotSupportedException{
		Event clone = (Event) super.clone();
		clone.components = (ArrayList<EventComponent>) this.components.clone();
		for(String k : this.attributes.keySet()){
			clone.attributes.put(k, this.attributes.get(k));
		}
		
		return clone;
	}
	
	
	private String getValue(String name){
		return this.getAttr(uid.getId(name));
	}
	
	private String getChildValue(int idx, String name) throws IndexOutOfBoundsException{
		return this.getChild(idx).getAttr(uid.getId(name));
	}
	
	private long transRevsTimeStamp(String datetime){
		long ts;
		if(datetime != null){
			ts = TimeStampUtil.transRevsCustomTimeStamp(datetime);
		}else 
			ts = TimeStampUtil.getRevsCurrentTimeStamp();
		return ts;
	}
	
	private long transTimeStamp(String datetime){
		long ts;
		if(datetime != null)
			ts = TimeStampUtil.transCustomTimeStamp(datetime);
		else 
			ts = TimeStampUtil.getCurrentTimeStamp();
		return ts;
	}
	
	private boolean isReplaceKey(EventComponent father, EventComponent child){
		//rowkey case: wsqqmrph8hpy_5306c861cc4612021c8399f0_723984729587
		if(child.getRowkey() == null) 
			return true;
		
		String[] s1 = father.getRowkey().split("_");
		String[] s2 = child.getRowkey().split("_");
		if(compare(s1[2], s2[2])<0 && !s1[1].equalsIgnoreCase("null")) //Timestamp comparision and UID is not null
			return true;
		
		return false;
	}
	
	private int compare(String s1, String s2){
		return new Double(s1).compareTo(new Double(s2));
	}
	
	private String salt(long ts){
		short prefix = (short) (ts % REGIONSERVER_NUM);
		return prefix+""+ts;
	}
	
	private String deSalt(String salted){
		return salted.substring(1, salted.length());
	}
	
	private String retrieveEndDateTime() {
		String time = attributes.get(uid.getId("endDate"));
		if(time != null && time.length() < 12)
			time = time + " 23:59:59";
		return time;
	}
}
