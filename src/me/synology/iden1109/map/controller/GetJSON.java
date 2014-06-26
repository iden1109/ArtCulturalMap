package me.synology.iden1109.map.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.hadoop.hbase.TableNotFoundException;

import me.synology.iden1109.map.model.Event;
import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.model.EventIndex;
import me.synology.iden1109.map.model.EventJSON;
import me.synology.iden1109.map.util.StringUtil;

import com.opensymphony.xwork2.ActionSupport;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GetJSON extends ActionSupport {

	private static final double INCRES = 0.00005;
	private static final long serialVersionUID = 6276066368028849157L;
	private Map<String, Integer> idxMap = new HashMap<String, Integer>();
	private GeometryFactory factory = new GeometryFactory();
	
	
	private Double minLat;
	private Double maxLat;
	private Double minLon;
	private Double maxLon;
	private String startDate;
	private String endDate;
	private String keyword;
	
	private List<EventJSON> result =  new ArrayList<EventJSON>();
	private String error;
	

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Double getMinLat() {
		return minLat;
	}

	public void setMinLat(Double minLat) {
		this.minLat = minLat;
	}

	public Double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(Double maxLat) {
		this.maxLat = maxLat;
	}

	public Double getMinLon() {
		return minLon;
	}

	public void setMinLon(Double minLon) {
		this.minLon = minLon;
	}

	public Double getMaxLon() {
		return maxLon;
	}

	public void setMaxLon(Double maxLon) {
		this.maxLon = maxLon;
	}

	public List<EventJSON> getResult() {
		return result;
	}

	public void setResult(List<EventJSON> result) {
		this.result = result;
	}

	
	public String execute(){
		
		LOG.info(" minLat: "+minLat+" maxLat: "+maxLat+" minLon: "+minLon+" maxLon: "+maxLon+" startDate: "+startDate+" endDate: "+endDate+" keyword: "+keyword);
		Query query = new Query();
		Queue<EventComponent> results = null;
		
		if(keyword != null && !keyword.isEmpty()){
			try {
				boolean isFirst = true;
				List<EventIndex> list = new QueryEventIndex().query(StringUtil.decode(keyword));
				for(EventIndex idx : list){
					for(String rowkey : idx.getRowkeys()){
						if(isFirst){
							results = query.querybyRowkey(rowkey, startDate, endDate);
							isFirst = false;
						}else{
							results.addAll(query.querybyRowkey(rowkey, startDate, endDate));
						}
					}
				}
				LOG.info("===results=== "+results.size());
			} catch (TableNotFoundException e) {
				LOG.error("Table not found", e.toString());
			}
		}else{
			
			Geometry geometry = query.queryEnvelope(factory, minLon, maxLon, minLat, maxLat);
			try {
				results = query.query(geometry, startDate, endDate);
			} catch (TableNotFoundException e1) {
				LOG.error("Table not found", e1.toString());
			}
		}
		
		String oldLonLat="", newLonLat="";
		EventComponent e;
		while (( e = results.poll()) != null) {
			newLonLat = String.format("%3.7f", e.getLongitude()) + String.format("%3.7f", e.getLatitude());
			if(!newLonLat.equals(oldLonLat)){
				oldLonLat = String.format("%3.7f", e.getLongitude()) + String.format("%3.7f", e.getLatitude());
			}else if(newLonLat.equals(oldLonLat)){
				assignPosition(e);
			}
			result.add(((Event)e).toJSON());
			//LOG.info("result: " + e.toString());
		}
		LOG.info("how many result : " + result.size());
		
		return SUCCESS;
	}
	
	/**
	 * Reassign new position for duplicated point, this function only support 48 duplicated points
	 * 
	 * number is idx 
	 * ===========================
	 * 25   26   27   28   29   30   31
	 * 32   9    10   11   12   13   33
	 * 34   14   1    2    3    15   35
	 * 36   16   4  first  5    17   37
	 * 38   18   6    7    8    19   39
	 * 40   20   21   22   23   24   41
	 * 42   43   44   45   46   47   48
	 * @param e
	 */
	private void assignPosition(EventComponent e){
		int idx = 1;
		double[] orig = {e.getLongitude(), e.getLatitude()};
		double[] lonlat = {e.getLongitude(), e.getLatitude()};
		String sLonlat = String.format("%3.7f", e.getLongitude()) + String.format("%3.7f", e.getLatitude());
		if(idxMap.containsKey(sLonlat))
			idx = idxMap.get(sLonlat)+1;
		idxMap.put(sLonlat, idx);
		
		if(idx == 1){ //NW
			lonlat = getWestern(getNorthern(orig));
		}else if(idx == 2){ //N
			lonlat = getNorthern(orig);
		}else if(idx == 3){ //NE
			lonlat = getEastern(getNorthern(orig));
		}else if(idx == 4){ //W
			lonlat = getWestern(orig);
		}else if(idx == 5){ //E
			lonlat = getEastern(orig);
		}else if(idx == 6){ //SW
			lonlat = getWestern(getSouthern(orig));
		}else if(idx == 7){ //S
			lonlat = getSouthern(orig);
		}else if(idx == 8){ //SE
			lonlat = getEastern(getSouthern(orig));
		}else if(idx == 9){ //NWNW
			lonlat = getWestern(getNorthern(getWestern(getNorthern(orig))));
		}else if(idx == 10){ //NWN
			lonlat = getWestern(getNorthern(getNorthern(orig)));
		}else if(idx == 11){ //NN
			lonlat = getNorthern(getNorthern(orig));
		}else if(idx == 12){ //NEN
			lonlat = getEastern(getNorthern(getNorthern(orig)));
		}else if(idx == 13){ //NENE
			lonlat = getEastern(getNorthern(getEastern(getNorthern(orig))));
		}else if(idx == 14){ //NWW
			lonlat = getWestern(getWestern(getNorthern(orig)));
		}else if(idx == 15){ //NEE
			lonlat = getEastern(getEastern(getNorthern(orig)));
		}else if(idx == 16){ //WW
			lonlat = getWestern(getWestern(orig));
		}else if(idx == 17){ //EE
			lonlat = getEastern(getEastern(orig));
		}else if(idx == 18){ //SWW
			lonlat = getWestern(getWestern(getSouthern(orig)));
		}else if(idx == 19){ //SEE
			lonlat = getEastern(getEastern(getSouthern(orig)));
		}else if(idx == 20){ //SWSW
			lonlat = getWestern(getSouthern(getWestern(getSouthern(orig))));
		}else if(idx == 21){ //SWS
			lonlat = getSouthern(getWestern(getSouthern(orig)));
		}else if(idx == 22){ //SS
			lonlat = getSouthern(getSouthern(orig));
		}else if(idx == 23){ //SES
			lonlat = getSouthern(getEastern(getSouthern(orig)));
		}else if(idx == 24){ //SESE
			lonlat = getEastern(getSouthern(getEastern(getSouthern(orig))));
		}else if(idx == 25){ //NWNWNW
			lonlat = getWestern(getNorthern(getWestern(getNorthern(getWestern(getNorthern(orig))))));
		}else if(idx == 26){ //NWNWN
			lonlat = getWestern(getNorthern(getWestern(getNorthern(getNorthern(orig)))));
		}else if(idx == 27){ //NWNN
			lonlat = getWestern(getNorthern(getNorthern(getNorthern(orig))));
		}else if(idx == 28){ //NNN
			lonlat = getNorthern(getNorthern(getNorthern(orig)));
		}else if(idx == 29){ //NENN
			lonlat = getEastern(getNorthern(getNorthern(getNorthern(orig))));
		}else if(idx == 30){ //NENEN
			lonlat = getEastern(getNorthern(getEastern(getNorthern(getNorthern(orig)))));
		}else if(idx == 31){ //NENENE
			lonlat = getEastern(getNorthern(getEastern(getNorthern(getEastern(getNorthern(orig))))));
		}else if(idx == 32){ //NWNWW
			lonlat = getWestern(getNorthern(getWestern(getNorthern(getWestern(orig)))));
		}else if(idx == 33){ //NENEE
			lonlat = getEastern(getNorthern(getEastern(getNorthern(getEastern(orig)))));
		}else if(idx == 34){ //NWWW
			lonlat = getNorthern(getWestern(getWestern(getWestern(orig))));
		}else if(idx == 35){ //NEEE
			lonlat = getNorthern(getEastern(getEastern(getEastern(orig))));
		}else if(idx == 36){ //WWW
			lonlat = getWestern(getWestern(getWestern(orig)));
		}else if(idx == 37){ //EEE
			lonlat = getEastern(getEastern(getEastern(orig)));
		}else if(idx == 38){ //SWWW
			lonlat = getSouthern(getWestern(getWestern(getWestern(orig))));
		}else if(idx == 39){ //SEEE
			lonlat = getEastern(getEastern(getEastern(getSouthern(orig))));
		}else if(idx == 40){ //SWSWW
			lonlat = getWestern(getWestern(getSouthern(getWestern(getSouthern(orig)))));
		}else if(idx == 41){ //SESEE
			lonlat = getEastern(getEastern(getSouthern(getEastern(getSouthern(orig)))));
		}else if(idx == 42){ //SWSWSW
			lonlat = getWestern(getSouthern(getWestern(getSouthern(getWestern(getSouthern(orig))))));
		}else if(idx == 43){ //SWSWS
			lonlat = getSouthern(getWestern(getSouthern(getWestern(getSouthern(orig)))));
		}else if(idx == 44){ //SWSS
			lonlat = getSouthern(getSouthern(getWestern(getSouthern(orig))));
		}else if(idx == 45){ //SSS
			lonlat = getSouthern(getSouthern(getSouthern(orig)));
		}else if(idx == 46){ //SESS
			lonlat = getSouthern(getSouthern(getEastern(getSouthern(orig))));
		}else if(idx == 47){ //SESES
			lonlat = getSouthern(getEastern(getSouthern(getEastern(getSouthern(orig)))));
		}else if(idx == 48){ //SESESE
			lonlat = getSouthern(getEastern(getSouthern(getEastern(getSouthern(getEastern(orig))))));
		}
		e.setLongitude(lonlat[0]);
		e.setLatitude(lonlat[1]);
	}
	
	private double[] getNorthern(double[] lonlat){
		double[] dd = {lonlat[0], lonlat[1]+INCRES*1};
		return dd;
	}
	
	private double[] getEastern(double[] lonlat){
		double[] dd = {lonlat[0]+INCRES*1, lonlat[1]};
		return dd;
	}
	
	private double[] getSouthern(double[] lonlat){
		double[] dd = {lonlat[0], lonlat[1]+INCRES*-1};
		return dd;
	}
	
	private double[] getWestern(double[] lonlat){
		double[] dd = {lonlat[0]+INCRES*-1, lonlat[1]};
		return dd;
	}
}
