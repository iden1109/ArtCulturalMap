package me.synology.iden1109.map.model;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.synology.iden1109.hbase.HBase;

public class EventIndex {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventIndex.class);
	
	public static final String TABLE = "event_idx";
	public static final String FAMILY = "idx";
	public static final String KEYS = "keys";
	public static final String LOCATION = "L!";
	public static final String LOCATIONNAME = "N!";
	public static final String TITLE = "T!";
	
	private String keyword;
	private Category category;
	private List<String> rowkeys;
	
	
	public enum Category {
		
		LOCATIONNAME("1"), LOCATION("2"), TITLE("3");
	 
		private String code;
		private String[] name = {"場館","地址","藝文活動"};
	 
		private Category(String s) {
			code = s;
		}
		
		public String getCode(){
			return code;
		}
		
		public String getName() {
			return name[Integer.parseInt(code)-1];
		}
		
	}
	
	
	public EventIndex(){
		try {
			if(!HBase.existTable(TABLE))
				HBase.createTable(TABLE, FAMILY);
		} catch (IOException e) {
			LOG.error("Failed to Create table " + TABLE + " with family [" + FAMILY + "]");
		}
	}
	
	public void clear() throws IOException{
		if(HBase.existTable(TABLE)){
			HBase.dropTable(TABLE);
			HBase.createTable(TABLE, FAMILY);
		}
	}
	
	public AddressJSON toJSON(){	
		AddressJSON json = new AddressJSON();
		json.label = this.getKeyword();
		json.categoryName = this.getCategory().getName();
		json.category = this.getCategory().getCode();
		return json;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<String> getRowkeys() {
		return rowkeys;
	}

	public void setRowkeys(List<String> rowkeys) {
		this.rowkeys = rowkeys;
	}

	
	
}
