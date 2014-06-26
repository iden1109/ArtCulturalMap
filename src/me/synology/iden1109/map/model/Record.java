package me.synology.iden1109.map.model;

import me.synology.iden1109.map.controller.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [GOAL] Store a HBase record
 * 
 * @author zhengyu
 * @date 2013/10/25
 * date      		ver		programmer		description
 * =======================================================
 * 2013/10/25		0.1		zhengyu			initial
 *
 */
public class Record {
	
	private static final Logger LOG = LoggerFactory.getLogger(Record.class);
	
	private String table;
	
	private String rowKey;
	private String family;
	private String qualifier;
	private long timestamp;
	private String value;
	
	
	@Override
	public String toString() {
		String s = "Table [" + table +"], Record [rowKey=" + rowKey + ", family=" + family
				+ ":" + qualifier + ", timestamp=" + timestamp
				+ ", value=" + value + "]";
		LOG.info(s);
		return s;
	}
	
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}
	public String getRowKey() {
		return rowKey;
	}
	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
