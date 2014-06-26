package me.synology.iden1109.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.synology.iden1109.map.model.Record;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [GOAL] HBase table Operator <li>hbase shell</li> CLI get into operation
 * interface
 * 
 * @author zhengyu
 * @date 2013/10/23 
 * date        ver     programmer     description
 * 2013/10/23  0.1     zhengyu        initial
 * s
 */
public abstract class HBase extends HBaseBean{

	private static final Logger LOG = LoggerFactory.getLogger(HBase.class);
	
	protected abstract Object populate(Result r);
	
	
	public HBase(){
	}
	
	
	/**
	 * Create a new Table <li>create 'table', 'columnFamily'</li>
	 * 
	 * @param tableName
	 * @param familyName
	 * @throws IOException
	 */
	public static void createTable(String tableName, String familyName)
			throws IOException {
		HBaseAdmin admin = new HBaseAdmin(Conf);

		if (admin.tableExists(tableName))
			return;

		HTableDescriptor htd = new HTableDescriptor(tableName);
		HColumnDescriptor col = new HColumnDescriptor(familyName);
		htd.addFamily(col);
		admin.createTable(htd);
		admin.close();
	}

	/**
	 * Create a new Table <li>create 'table', 'columnFamily1',
	 * 'columnFamily2',...</li>
	 * 
	 * @param tableName
	 * @param familys
	 * @throws IOException
	 */
	public static void createTable(String tableName, String[] familys)
			throws IOException {
		HBaseAdmin admin = new HBaseAdmin(Conf);

		if (existTable(tableName))
			return;

		HTableDescriptor htd = new HTableDescriptor(tableName);
		for (int i = 0; i < familys.length; i++) {
			htd.addFamily(new HColumnDescriptor(familys[i]));
		}
		admin.createTable(htd);
		admin.close();
	}

	/**
	 * Does this table exist?
	 * 
	 * @param tableName
	 * @return True or False
	 * @throws IOException
	 */
	public static boolean existTable(String tableName) throws IOException {
		HBaseAdmin admin = new HBaseAdmin(Conf);
		return admin.tableExists(tableName);
	}
	
	/**
	 * Drop a table
	 * 
	 * > disable 'table' > drop 'table'
	 * 
	 * @param tableName
	 * @throws IOException
	 */
	public static void dropTable(String tableName) throws IOException {
		HBaseAdmin admin = new HBaseAdmin(Conf);
		if (existTable(tableName)) {
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
			LOG.info("Table[" + tableName + "] has been dropped.");
		} else {
			LOG.info("Table[" + tableName + "] not found!");
		}
		admin.close();
	}

	/**
	 * Insert a new record to HBase > put 'table', 'row1', 'family1:qualifier1',
	 * 'value1'
	 * 
	 * @param tableName
	 * @param row
	 * @param family
	 * @param colQualifier
	 * @param value
	 * @throws IOException
	 */
	public static void addRecord(String tableName, String rowKey,
			String family, String qualifier, String value) throws IOException {
		addRecord(tableName, rowKey, Bytes.toBytes(family),
				Bytes.toBytes(qualifier), Bytes.toBytes(value));
	}

	public static void addRecord(String tableName, String rowKey,
			String family, String qualifier, long value) throws IOException {
		addRecord(tableName, rowKey, Bytes.toBytes(family),
				Bytes.toBytes(qualifier), Bytes.toBytes(value));
	}

	public static void addRecord(String tableName, String rowKey,
			byte[] family, byte[] qualifier, byte[] value) throws IOException {
		
		HTable table = connectTable(tableName);
		Put p = new Put(Bytes.toBytes(rowKey));
		p.add(family, qualifier, value);
		table.put(p);
		releaseTable(table);
		LOG.info("insert record k:" + rowKey + " [" + new String(family) + ":"
				+ new String(qualifier) + " v:" + new String(value) + "] to table[" + tableName
				+ "] ok.");
	}
	
	public static void addRecord(String tableName, String rowKey,
			String family, String qualifier, long ts, String value) throws IOException {
		
		HTable table = connectTable(tableName);
		Put p = new Put(Bytes.toBytes(rowKey));
		p.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), ts, Bytes.toBytes(value));
		table.put(p);
		releaseTable(table);
		LOG.info("insert record k:" + rowKey + " [" + new String(family) + ":"
				+ new String(qualifier) + " v:" + new String(value) + " ts:"+ ts +"] to table[" + tableName
				+ "] ok.");
	}

	/**
	 * Insert a new record base on a ROWKEY
	 * 
	 * @param tableName
	 * @param family
	 * @param rowKey
	 * @param valueMap
	 * @throws IOException
	 */
	public static void addRecords(String tableName, String rowKey, String family,
			Map<String, String> vMap) throws IOException {

		HTable table = connectTable(tableName);
		table.setAutoFlush(false);
		Put p = new Put(Bytes.toBytes(rowKey));
		for (String k : vMap.keySet()) {
			// LOG.debug(k+" -> "+vMap.get(k));
			p.add(Bytes.toBytes(family), Bytes.toBytes(k),
					Bytes.toBytes(vMap.get(k)));
		}
		table.put(p);
		table.flushCommits();
		
		releaseTable(table);
		LOG.info("insert record k:" + rowKey + " to table[" + tableName
				+ "] ok.");
	}
	
	public static void addRecords(String tableName, String rowKey, String family,
			Map<String, String> vMap, long ts) throws IOException {

		HTable table = connectTable(tableName);
		table.setAutoFlush(false);
		Put p = new Put(Bytes.toBytes(rowKey));
		for (String k : vMap.keySet()) {
			// LOG.debug(k+" -> "+vMap.get(k));
			p.add(Bytes.toBytes(family), Bytes.toBytes(k),
					ts, Bytes.toBytes(vMap.get(k)));
		}
		table.put(p);
		table.flushCommits();
		
		releaseTable(table);
		LOG.info("insert record k:" + rowKey + " to table[" + tableName
				+ "] ok.");
	}

	/**
	 * Delete a record by [rowKey]
	 * 
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	public static void delRecord(String tableName, String rowKey)
			throws IOException {
		HTable table = connectTable(tableName);
		Delete del = new Delete(Bytes.toBytes(rowKey));
		table.delete(del);
		releaseTable(table);
		LOG.info("delete recored " + rowKey + " from table[" + tableName
				+ "] ok.");
	}

	/**
	 * Get one record
	 * 
	 * @param tableName
	 * @param rowKey
	 * @return Record
	 * @throws IOException
	 */
	public static Record getRecord(String tableName, String rowKey)
			throws IOException {
		HTable table = connectTable(tableName);
		Get get = new Get(rowKey.getBytes());
		Result rs = table.get(get);
		Record rec = new Record();
		for (KeyValue kv : rs.raw()) {
			rec.setTable(tableName);
			rec.setRowKey(new String(kv.getRow()));
			rec.setFamily(new String(kv.getFamily()));
			rec.setQualifier(new String(kv.getQualifier()));
			rec.setTimestamp(kv.getTimestamp());
			rec.setValue(new String(kv.getValue()));
		}
		releaseTable(table);
		return rec;
	}

	/**
	 * Get records by RowKey
	 * 
	 * @param tableName
	 * @param rowKey
	 * @return List<Record>
	 * @throws IOException
	 */
	public static List<Record> getRecords(String tableName, String rowKey)
			throws IOException {
		HTable table = connectTable(tableName);
		Scan s = new Scan(rowKey.getBytes());

		ResultScanner ss = table.getScanner(s);
		Record rec;
		List<Record> list = new ArrayList<Record>();
		for (Result r : ss) {
			for (KeyValue kv : r.raw()) {
				rec = new Record();
				rec.setTable(tableName);
				rec.setRowKey(new String(kv.getRow()));
				rec.setFamily(new String(kv.getFamily()));
				rec.setQualifier(new String(kv.getQualifier()));
				rec.setTimestamp(kv.getTimestamp());
				rec.setValue(new String(kv.getValue()));
				list.add(rec);
			}
		}
		releaseTable(table);
		return list;
	}
	
	public static List<Record> getRecordAll(String tableName)
			throws IOException {
		HTable table = connectTable(tableName);
		Scan s = new Scan();
		ResultScanner ss = table.getScanner(s);
		Record rec;
		List<Record> list = new ArrayList<Record>();
		for (Result r : ss) {
			for (KeyValue kv : r.raw()) {
				rec = new Record();
				rec.setTable(tableName);
				rec.setRowKey(new String(kv.getRow()));
				rec.setFamily(new String(kv.getFamily()));
				rec.setQualifier(new String(kv.getQualifier()));
				rec.setTimestamp(kv.getTimestamp());
				rec.setValue(new String(kv.getValue()));
				list.add(rec);
			}
		}
		releaseTable(table);
		return list;
	}

	/**
	 * Get value of a record > get 'table', 'row1'
	 * 
	 * @param tableName
	 * @param row
	 * @param family
	 * @param qualifier
	 * @return
	 * @throws IOException
	 */
	public static String getValue(String tableName, String row, String family,
			String qualifier) throws IOException {
		return getValue(tableName.getBytes(), row.getBytes(),
				family.getBytes(), qualifier.getBytes());
	}

	public static String getValue(byte[] tableName, byte[] row, byte[] family,
			byte[] qualifier) throws IOException {
		HTable table = connectTable(Bytes.toString(tableName));
		Get g = new Get(row);
		Result r = table.get(g);
		return Bytes.toString(r.getValue(family, qualifier));
	}

	/**
	 * Increment
	 * 
	 * @param tableName
	 * @param row
	 * @param family
	 * @param qualifier
	 * @return
	 * @throws IOException
	 */
	public static long increment(String tableName, byte[] row, byte[] family,
			byte[] qualifier) throws IOException {
		HTable table = connectTable(tableName);
		return table.incrementColumnValue(row, family, qualifier, 1);
	}

	public static long increment(String tableName, String row, String family,
			String qualifier) throws IOException {
		return increment(tableName, Bytes.toBytes(row), Bytes.toBytes(family),
				Bytes.toBytes(qualifier));
	}

	/**
	 * Scan all column value and print it on CLI > scan 'table'
	 * 
	 * @param tableName
	 * @param family
	 * @param qualifier
	 * @throws IOException
	 */
	public static void scanAllColumns(String tableName, String family,
			String qualifier) throws IOException {
		HTable table = connectTable(tableName);
		ResultScanner scanner = table.getScanner(family.getBytes());

		int i = 1;
		for (Result r : scanner) {
			String v = Bytes.toString(r.getValue(family.getBytes(),
					qualifier.getBytes()));
			LOG.info("row" + i + " is \"" + v + "\"");
			i++;
		}
		releaseTable(table);
	}

	
	public static void main(String[] agrs) {
		try {
			String tablename = "scores";
			String[] familys = { "grade", "course" };
			HBase.dropTable(tablename);
			HBase.createTable(tablename, familys);

			// add record zkb
			HBase.addRecord(tablename, "zkb", "grade", "", "5");
			HBase.addRecord(tablename, "zkb", "course", "", "90");
			HBase.addRecord(tablename, "zkb", "course", "math", "97");
			HBase.addRecord(tablename, "zkb", "course", "art", "87");
			// add record baoniu
			HBase.addRecord(tablename, "baoniu", "grade", "", "4");
			HBase.addRecord(tablename, "baoniu", "course", "math", "89");

			LOG.info("===========get one record========");
			Record rec = HBase.getRecord(tablename, "zkb");
			LOG.info(rec.toString());

			LOG.info("===========show all record========");
			List<Record> list = HBase.getRecordAll(tablename);
			for (Record r : list) {
				LOG.info(r.toString());
			}

			LOG.info("===========del one record========");
			HBase.delRecord(tablename, "baoniu");
			list = HBase.getRecordAll(tablename);
			for (Record r : list) {
				LOG.info(r.toString());
			}

			LOG.info("===========show all record========");
			list = HBase.getRecordAll(tablename);
			for (Record r : list) {
				LOG.info(r.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
