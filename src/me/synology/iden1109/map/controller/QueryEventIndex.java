package me.synology.iden1109.map.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.synology.iden1109.hbase.HBase;
import me.synology.iden1109.map.model.EventIndex;
import me.synology.iden1109.map.model.AddressJSON;
import me.synology.iden1109.map.model.Event;
import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.model.EventItem;
import me.synology.iden1109.map.util.JSonUtil;

public class QueryEventIndex extends HBase {
	private static final Logger LOG = LoggerFactory.getLogger(QueryEventIndex.class);
	
	@Override
	protected EventIndex populate(Result r) {
		EventIndex eventIdx = new EventIndex();
		String s = new String(r.getRow());
		String[] keys = null;
		String f, k, v;
		
		eventIdx.setKeyword(s.substring(EventIndex.LOCATION.length(), s.length()));
		if(s.startsWith(EventIndex.LOCATION))
			eventIdx.setCategory(EventIndex.Category.LOCATION);
		else if(s.startsWith(EventIndex.LOCATIONNAME))
			eventIdx.setCategory(EventIndex.Category.LOCATIONNAME);
		else
			eventIdx.setCategory(EventIndex.Category.TITLE);
		
		for(KeyValue kv : r.list()){
			f = new String(kv.getFamily());
			k = new String(kv.getQualifier());
			v = new String(kv.getValue());
			if(f.equalsIgnoreCase(EventIndex.FAMILY) && k.equalsIgnoreCase(EventIndex.KEYS)){// e
				keys = v.split(",");
			}
		}
		eventIdx.setRowkeys(Arrays.asList(keys));
		
		return eventIdx;
	}

	
	public List<EventIndex> query(String prefix) throws TableNotFoundException{
		List<EventIndex> list = new ArrayList<EventIndex>();
		try {
			this.scanbyPrefix(EventIndex.TABLE, new String[]{EventIndex.FAMILY}, EventIndex.TITLE + prefix, list);
			this.scanbyPrefix(EventIndex.TABLE, new String[]{EventIndex.FAMILY}, EventIndex.LOCATIONNAME + prefix, list);
			this.scanbyPrefix(EventIndex.TABLE, new String[]{EventIndex.FAMILY}, EventIndex.LOCATION + prefix, list);
			LOG.info(String.format("Scan '%s' returned %s candidates.", prefix, list.size()));
		} catch (TableNotFoundException e1) {
			throw e1;
		} catch (IOException e) {
			LOG.error("Failed to scan table "+EventComponent.TABLE+" with family ["+EventComponent.FAMILYS.toString()+"]", e);
		}
		
		return list;
	}
	

}
