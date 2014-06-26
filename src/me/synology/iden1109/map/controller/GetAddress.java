package me.synology.iden1109.map.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableNotFoundException;

import me.synology.iden1109.map.model.EventIndex;
import me.synology.iden1109.map.model.AddressJSON;
import me.synology.iden1109.map.util.StringUtil;

import com.opensymphony.xwork2.ActionSupport;

public class GetAddress extends ActionSupport{
	String query;
	List<AddressJSON> result;
	

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<AddressJSON> getResult() {
		return result;
	}

	public void setResult(List<AddressJSON> result) {
		this.result = result;
	}

	
	public String execute(){
		
		try {
			List<EventIndex> list = new QueryEventIndex().query(StringUtil.decode(getQuery()));
			List<AddressJSON> listRtn = new ArrayList<AddressJSON>();
			for(EventIndex idx : list){
				listRtn.add(idx.toJSON());
			}
			setResult(listRtn);
		} catch (TableNotFoundException e) {
			LOG.error("Table not found", e.toString());
		}
		
		
		return SUCCESS;
	}
}
