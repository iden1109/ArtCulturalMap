package me.synology.iden1109.hbase.mapreduce;

import java.io.IOException;
import java.util.Calendar;

import me.synology.iden1109.map.model.EventIndex;
import me.synology.iden1109.map.model.Event;
import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.model.UId;
import me.synology.iden1109.map.util.JSonUtil;
import me.synology.iden1109.map.util.StringUtil;
import me.synology.iden1109.map.util.TimeStampUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;

/**
 * Generate index data of event_idx table during the latest 6 month
 * source from event table
 * @author zhengyu
 *
 */
public class MR_AddrSecondIndex {
	
	
	public static class Mapper extends TableMapper<ImmutableBytesWritable, ImmutableBytesWritable> {

		@Override
		/**
		 * 
		 * @param row : Table RowKey
		 * @param values : RowKey mapping Columns
		 */
		public void map(ImmutableBytesWritable rowkey, Result result,
				Context context) throws InterruptedException, IOException {
			
			ImmutableBytesWritable title = null;
			ImmutableBytesWritable location = null;
			ImmutableBytesWritable locationName = null;
			EventComponent ei = null;
			String tmp = null;
			for (KeyValue kv : result.list()) {
				
				if (Event.FAMILYS[0].equals(Bytes.toString(kv.getFamily()))
						&& UId.getInstance().getId(Event.TITLE).equals(Bytes.toString(kv.getQualifier()))) {
					tmp = StringUtil.replace(new String(kv.getValue()));
					title = new ImmutableBytesWritable(Bytes.toBytes(EventIndex.TITLE + tmp));
				}
				
				if (Event.FAMILYS[1].equals(Bytes.toString(kv.getFamily()))){
					ei = JSonUtil.decode(new String(kv.getValue()));
					tmp = StringUtil.replace(ei.getAttr(UId.getInstance().getId("location")));
					location = new ImmutableBytesWritable(Bytes.toBytes(EventIndex.LOCATION + tmp));
					tmp = StringUtil.replace(ei.getAttr(UId.getInstance().getId("locationName")));
					locationName = new ImmutableBytesWritable(Bytes.toBytes(EventIndex.LOCATIONNAME + tmp));
				}
			}
			
			try {
				// (location, rowkey)
				context.write(title, rowkey);
				context.write(location, rowkey);
				context.write(locationName, rowkey);
			} catch (InterruptedException e) {
				throw new IOException(e);
			}

		}
		
	}

	
	public static class Reducer extends TableReducer<ImmutableBytesWritable, ImmutableBytesWritable, ImmutableBytesWritable> {
		
		@Override
		/**
		 * 
		 * @param key : tag
		 * @param values : nickname Array
		 */
		public void reduce(ImmutableBytesWritable rowkey, Iterable<ImmutableBytesWritable> values,
				Context context) throws IOException, InterruptedException {
			
			String keys = "";
			for (ImmutableBytesWritable val : values) {
				keys += (keys.length() > 0 ? "," : "")
						+ Bytes.toString(val.get());
			}
			Put put = new Put(rowkey.get());
			put.add(Bytes.toBytes(EventIndex.FAMILY), Bytes.toBytes(EventIndex.KEYS),
					Bytes.toBytes(keys));
			context.write(rowkey, put);
		}
		
	}

	
	public static void main(String[] args) throws Exception {
		
		EventIndex eventIdx = new EventIndex();
		eventIdx.clear();
		
		Configuration conf = new Configuration();
		conf = HBaseConfiguration.create(conf);
		Job job = new Job(conf, "HBase_Address_Secondary_Index");
		job.setJarByClass(MR_AddrSecondIndex.class);
		
		Scan scan = new Scan();
		scan.setCaching(500);
		scan.setCacheBlocks(false); // don't set to true for MR jobs
		scan.addColumn(Bytes.toBytes(Event.FAMILYS[0]), Bytes.toBytes(UId.getInstance().getId(Event.TITLE)));
		scan.addFamily(Bytes.toBytes(Event.FAMILYS[1]));
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 6);
		scan.setTimeRange(TimeStampUtil.getCurrentTimeStamp(), cal.getTimeInMillis());
		
		
		// SOURCE: event table
		TableMapReduceUtil.initTableMapperJob(Event.TABLE, 
				scan,
				MR_AddrSecondIndex.Mapper.class, 
				ImmutableBytesWritable.class,
				ImmutableBytesWritable.class, 
				job);
		
		// TARGET: event_idx table
		TableMapReduceUtil.initTableReducerJob(EventIndex.TABLE,
				MR_AddrSecondIndex.Reducer.class, 
				job);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
