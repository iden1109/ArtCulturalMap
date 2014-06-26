package me.synology.iden1109.hbase.mapreduce.example;

import java.io.IOException;

import me.synology.iden1109.hbase.HBase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

/**
 * 
 * [GOAL] 碼將輸入路徑的檔案內的字串取出做字數統計，再將結果塞回HTable內
 * ex: 
 * [input] I am a super super man 
 * 
 * [output] Table "wordcount"
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
 * @author zhengyu
 * @date 2013/10/23
 * date      		ver		programmer		description
 * =======================================================
 * 2013/10/23		0.1		zhengyu			initial
 *
 */
public class WordCount2HBase {
	
	
	public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
		private Text WORD = new Text();
		private static final IntWritable ONE = new IntWritable(1);
		
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			//ex: I am a super man
			String line = value.toString();
			String sAry[] = line.trim().split(" ");
			for(String s : sAry){
				WORD.set(s);
				context.write(WORD, ONE);
			}
		}
	}

	
	public static class Reduce extends TableReducer<Text, IntWritable, NullWritable> {
		private Put P;
		
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			P = new Put(key.toString().getBytes());
			P.add("content".getBytes(), "count".getBytes(), String.valueOf(sum).getBytes());
			context.write(NullWritable.get(), P);
		}
	}
	
	
	
	public static void main(String args[]) throws IOException, InterruptedException, ClassNotFoundException{
		Configuration conf = new Configuration();
		conf.set(TableOutputFormat.OUTPUT_TABLE, "wordcount");
		HBase.createTable("wordcount", "content");
		
		Job job = new Job(conf, "WordCount Hbase");
		job.setJarByClass(WordCount2HBase.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TableOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		
		System.exit(job.waitForCompletion(true)?0:1);
	}
}
