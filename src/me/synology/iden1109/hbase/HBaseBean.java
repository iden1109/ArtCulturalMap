package me.synology.iden1109.hbase;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import me.synology.iden1109.map.filter.QueryFilter;
import me.synology.iden1109.map.model.Event;
import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.util.TimeStampUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HBaseBean {

	private static final Logger LOG = LoggerFactory.getLogger(HBaseBean.class);
	protected static Configuration Conf;
	public static final int CACHE_SIZE =  75;

	static {
		/*
		 * Configuration HBASE_CONFIG = new Configuration();
		 * HBASE_CONFIG.set("hbase.master", "localhost:60000");
		 * HBASE_CONFIG.set("hbase.zookeeper.quorum", "localhost.133");
		 * HBASE_CONFIG.set("hbase.zookeeper.property.clientPort", "2181"); Conf
		 * = HBaseConfiguration.create(HBASE_CONFIG);
		 */
		Conf = HBaseConfiguration.create();
	}

	protected HBaseBean() {
		// HRegionServer hRegionServer = new HRegionServer(Conf) ;
		// HConnection connection = HConnectionManager.createConnection(Conf);
		// hConnection = new CoprocessorHConnection(connection,hRegionServer);
	}

	protected abstract Object populate(Result r);

	
	
	/**
	 * scan data by PREFIX
	 * @param table
	 * @param family
	 * @param prefix
	 * @param queue
	 * @throws IOException
	 */
	public void scanbyPrefix(String table, String[] family, String prefix, List list) throws TableNotFoundException, IOException {
		HTable ht = connectTable(table);
		
		Scan scan = new Scan();
		scan.setFilter(new PrefixFilter(Bytes.toBytes(prefix)));
		for(String fami : family)
			scan.addFamily(Bytes.toBytes(fami));
		scan.setMaxVersions(1);
		scan.setCaching(CACHE_SIZE);  //sets scan cache, cuts down RPC calls

		ResultScanner scanner = ht.getScanner(scan);
		for (Result r : scanner) {
			Object obj = populate(r);
			if(obj instanceof java.util.List){
				list.addAll((List)obj);
			}else{
				list.add(obj);
			}
		}
	}
	
	/**
	 * scan data by PREFIX
	 * @param table
	 * @param family
	 * @param prefix
	 * @param queue
	 * @throws IOException
	 */
	
	public void scanbyPrefix(String table, String[] family, String prefix, Queue<EventComponent> queue) throws TableNotFoundException, IOException {
		HTable ht = connectTable(table);
		
		Scan scan = new Scan();
		scan.setFilter(new PrefixFilter(Bytes.toBytes(prefix)));
		for(String fami : family)
			scan.addFamily(Bytes.toBytes(fami));
		scan.setMaxVersions(1);
		scan.setCaching(CACHE_SIZE);  //sets scan cache, cuts down RPC calls

		ResultScanner scanner = ht.getScanner(scan);
		for (Result r : scanner) {
			queue.add((EventComponent)populate(r));
		}
	}
	
	/**
	 * Scan prefix keyword with Filter
	 * @param table
	 * @param family
	 * @param prefix
	 * @param filter
	 * @param queue
	 * @throws TableNotFoundException
	 * @throws IOException
	 */
	public void scanbyQueryFilter(String table, String[] family, byte[] prefix, Filter filter, Queue<EventComponent> queue) throws TableNotFoundException, IOException {
		HTable ht = connectTable(table);
		
		Filter f = new FilterList(new PrefixFilter(prefix), filter);
		Scan scan = new Scan(prefix, f);
		scan.setFilter(new PrefixFilter(prefix));
		for(String fami : family)
			scan.addFamily(Bytes.toBytes(fami));
		scan.setMaxVersions(1);
		scan.setCaching(CACHE_SIZE);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 3);
		scan.setTimeRange(TimeStampUtil.getCurrentTimeStamp(), cal.getTimeInMillis());
		 
		ResultScanner scanner = ht.getScanner(scan);
		for (Result r : scanner) {
			queue.add((EventComponent)populate(r));
		}
	}
	
	
	/**
	 * Scan all table data
	 * @param table
	 * @param family
	 * @param set
	 * @throws TableNotFoundException
	 * @throws IOException
	 */
	public void scanAll(String table, String[] family,
			Set<EventComponent> set) throws TableNotFoundException, IOException {
		HTable ht = connectTable(table);
		Scan scan = new Scan();
		ResultScanner scanner = ht.getScanner(scan);
		for (Result r : scanner) {
			set.add((EventComponent)populate(r));
		}
	}
	
	/**
	 * Get data by RowKey
	 * @param table
	 * @param family
	 * @param rowkey
	 * @param startDate
	 * @param endDate
	 * @param queue
	 * @throws TableNotFoundException
	 * @throws IOException
	 */
	public void scanbyRowkey(String table, String[] family, String rowkey, String startDate, String endDate, Queue<EventComponent> queue) throws TableNotFoundException, IOException {
		HTable ht = connectTable(table);
		
		Scan scan = new Scan();
		scan.setStartRow(Bytes.toBytes(rowkey));
		scan.setStopRow(Bytes.toBytes(rowkey));
		for(String fami : family)
			scan.addFamily(Bytes.toBytes(fami));
		scan.setMaxVersions(1);
		scan.setCaching(CACHE_SIZE);
		
		prepareTimeRange(startDate, endDate, scan);
		
		ResultScanner scanner = ht.getScanner(scan);
		for (Result r : scanner) {
			queue.add((EventComponent)populate(r));
		}
	}

	/**
	 * Define startDate and endDate if startDate is null, will be defined during 3 month
	 * @param startDate
	 * @param endDate
	 * @param scan
	 * @throws IOException
	 */
	protected void prepareTimeRange(String startDate, String endDate, Scan scan)
			throws IOException {
		Calendar cal = Calendar.getInstance();
		if(startDate!=null && !startDate.equals("") && endDate!=null && !endDate.equals("")){
			scan.setTimeRange(TimeStampUtil.transCustomTimeStamp(startDate+" 00:00:00"), TimeStampUtil.transCustomTimeStamp(endDate+" 23:59:59"));
		}else if(startDate!=null && !startDate.equals("")){
			long start = TimeStampUtil.transCustomTimeStamp(startDate+" 00:00:00");
			cal.setTimeInMillis(start);
			cal.add(Calendar.MONTH, 3);
			scan.setTimeRange(start, cal.getTimeInMillis());
		}else{
			cal.add(Calendar.MONTH, 3);
			scan.setTimeRange(TimeStampUtil.getCurrentTimeStamp(), cal.getTimeInMillis());
		}
	}

	protected static void releaseTable(HTable table) throws IOException {
		table.close();
	}

	protected static HTable connectTable(String tableName) throws TableNotFoundException, IOException {
		HTable table = new HTable(Conf, tableName);
		return table;
	}

}
