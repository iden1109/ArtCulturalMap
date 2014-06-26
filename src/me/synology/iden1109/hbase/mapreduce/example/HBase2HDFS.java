package me.synology.iden1109.hbase.mapreduce.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


/**
 * 
 * [GOAL] 將HBase的資料取出來，再將結果塞回hdfs上
 * 
 * [input] Table "wordcount"
 * 
 * 			column-family
 * 			column-qualifier	
 * row		value
 * ===========================
 * 			content
 * 			count
 * I		1
 * am		1
 * a		1
 * super	2
 * man		1
 * 
 * [output]
 * 
 * @author zhengyu
 * @date 2013/10/24
 * date      		ver		programmer		description
 * =======================================================
 * 2013/10/24		0.1		zhengyu			initial
 *
 */
public class HBase2HDFS extends Configured implements Tool {

	public static class Map extends TableMapper<Text, Text> {
		
		public void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException{
			String sVal = Bytes.toString(value.getValue("content".getBytes(), "count".getBytes()));
			String k = new String(key.get(),"UTF-8");
			context.write(new Text(k), new Text(sVal));
		}
	}

	
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
	
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String valSum = "";
			for (Text v : values) {
				valSum += v.toString();
			}
			context.write(key, new Text(valSum));
		}
	}
	
	
	
	public static void main(String args[]) throws Exception{
		int ret = ToolRunner.run(new HBase2HDFS(), args);
		System.exit(ret);
	}



	
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();

		String tableName = "wordcount";		
		Job job = new Job(conf, "Table:" + tableName + " HBase2HDFS");
		job.setJarByClass(HBase2HDFS.class);
		TableMapReduceUtil.initTableMapperJob(tableName, new Scan(), Map.class, Text.class, Text.class, job);
		
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setInputFormatClass(TableInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileOutputFormat.setOutputPath(job, new Path(args[0]));
		return job.waitForCompletion(true)?0:1;
	}
}
