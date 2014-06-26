package me.synology.iden1109.map.filter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import me.synology.iden1109.map.model.UId;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class QueryFilter extends FilterBase {

	private static final Logger LOG = LoggerFactory.getLogger(QueryFilter.class);
	private UId uid;
	private String lonKey;
	private String latKey;
	private GeometryFactory factory = new GeometryFactory();
	private Geometry geo;
	private boolean filterRow = false;
	
	
	public QueryFilter(Geometry geo){
		this.geo = geo;
		
		this.uid = UId.getInstance();
		lonKey = uid.getId("longitude");
		latKey = uid.getId("latitude");
	}
	
	@Override
	public void reset(){
		this.filterRow = false;
	}
	
	@Override
	public boolean filterRow(){
		return this.filterRow; // True if this filter actively uses filterRow(List).
	}
	
	@Override
	public void filterRow(List<KeyValue> kvs){
		//Filters that never filter by modifying the returned List of KeyValues can inherit this implementation that does nothing.
		if(kvs == null || kvs.size() ==0){
			this.filterRow = true;
			return;
		}
		
		double lon = Double.NaN, lat = Double.NaN;
		for(KeyValue kv :  kvs){
			String qual = new String(kv.getQualifier());
			if(lonKey.equalsIgnoreCase(qual))
				lon = Double.parseDouble(new String(kv.getValue()));
			if(latKey.equalsIgnoreCase(qual))
				lat = Double.parseDouble(new String(kv.getValue()));
		}
		
		if(Double.isNaN(lon) || Double.isNaN(lat)){
			this.filterRow = true;
			return;
		}
		
		Coordinate coord = new Coordinate(lon, lat);
		Geometry point = factory.createPoint(coord);
		if(!geo.contains(point))
			this.filterRow = true;
	}
	
	@Override
	public boolean hasFilterRow(){
		return true; // True if this filter actively uses filterRow(List).
	}
	
	
	public void readFields(DataInput in) throws IOException {
		String inStr = in.readUTF();
		WKTReader reader = new WKTReader(factory);
		try {
			this.geo = reader.read(inStr);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeUTF(geo.toText());
	}

}
