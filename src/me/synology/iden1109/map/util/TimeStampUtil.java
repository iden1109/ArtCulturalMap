package me.synology.iden1109.map.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TimeStampUtil {

	private static final Logger LOG = LoggerFactory.getLogger(TimeStampUtil.class);
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * Get the custom timestamp long
	 * @param dateTime
	 * @return
	 */
	public static long transCustomTimeStamp(String dateTime){
		Date date = formatDatetime(dateTime); 
		return date.getTime() ;
	}
	
	/**
	 * Get the custom timestamp long reverse
	 * @param dateTime
	 * @return
	 */
	public static long transRevsCustomTimeStamp(String dateTime){
		Date date = formatDatetime(dateTime); 
		return Long.MAX_VALUE - date.getTime() ;
	}
	
	public static long getCurrentTimeStamp(){
		return System.currentTimeMillis();
	}
	
	/**
	 * Get current timestamp long
	 * @return
	 */
	public static long getRevsCurrentTimeStamp(){
		return Long.MAX_VALUE - System.currentTimeMillis();
	}
	
	/**
	 * datetime long to datetime String
	 * @param datetime
	 * @return
	 */
	public static String transDateTimeString(long datetime){
		return FORMAT.format(datetime);
	}
	
	
	private static Date formatDatetime(String dateTime) {
		//Timestamp.valueOf(dateTime)
		Date date = Calendar.getInstance().getTime();
		try {
			date = FORMAT.parse(dateTime);
		} catch (ParseException e) {
			LOG.error("Failed to parse the time for "+dateTime, e);
		}
		//LOG.debug("getRevsCustomTimeStamp("+FORMAT.format(date)+")"+ date.getTime());
		return date;
	}
	
}
