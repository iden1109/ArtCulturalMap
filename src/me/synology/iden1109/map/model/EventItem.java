package me.synology.iden1109.map.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.synology.iden1109.hbase.HBase;
import me.synology.iden1109.map.util.JSonUtil;
import me.synology.iden1109.map.util.TimeStampUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventItem extends EventComponent {

	private static final Logger LOG = LoggerFactory.getLogger(EventItem.class);
	private Map<String, String> attributes;
	private UId uid;
	
	private String rowkey;
	private long endDateTimestamp;
	
	public EventItem(){
		attributes = new HashMap<String, String>();
		uid = UId.getInstance();
	}
	
	/**
	 * For store Info tag attribute
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
	
	public String getRowkey(){
		return this.rowkey;
	}
	
	public long getEndDateTimestamp() {
		return endDateTimestamp;
	}

	public void setEndDateTimestamp(long endDateTimestamp) {
		this.endDateTimestamp = endDateTimestamp;
	}

	/**
	 * Insert all of EventItems to HBase
	 */
	public int ingest(){
		int cnt = 0;
		if(attributes.size() > 0){
			long timeId = TimeStampUtil.transCustomTimeStamp(attributes.get(uid.getId("time")));
			try {
				HBase.addRecord(TABLE, this.getRowkey(), FAMILYS[1], String.valueOf(timeId), this.getEndDateTimestamp(), JSonUtil.encode(attributes));
				cnt++;
			} catch (IOException e) {
				LOG.error("Failed to Put!  row: " + this.getRowkey() + " of "+ FAMILYS[1]+":"+String.valueOf(timeId), e);
			}
		}
		return cnt;
	}
	
	/**
	 * print all of EventComponents info to log file
	 */
	public void print(){
		for(String k : attributes.keySet()){
			LOG.info("Item: "+k+" -> "+attributes.get(k));
		}
	}
	
	public EventComponent clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	/**
	 * Fill in all of attributes info
	 * @param m
	 */
	public void fillAttributes(Map<String, String> m){
		for(String k : attributes.keySet()){
			m.put(k, attributes.get(k));
		}
	}
}
