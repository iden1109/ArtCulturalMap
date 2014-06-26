package me.synology.iden1109.map.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import me.synology.iden1109.hbase.HBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unique ID implementation
 * 
 * @author zhengyu
 * 
 */
public class UId {

	private static final Logger LOG = LoggerFactory.getLogger(UId.class);

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final short MAX_ID_LENGTH = 5;
	private static final String[] FAMILYS = {"sys", "id", "name"};
	private static final String TABLE = "uid";
	private static final String TYPE = "cols";
	private static final Charset CHARSET = Charset.forName("ISO-8859-1");
	
	private ConcurrentHashMap<String, String> idCache = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, String> nameCache = new ConcurrentHashMap<String, String>();
	private static UId uid;

	private String table; // define 'uid' table
	private String type; // define type, means the column qualifier
	

	/**
	 * Constructor
	 * 
	 * @param table
	 *            The name of the HBase table to use
	 * @param type
	 *            The type of UID this instance will deal with
	 */
	private UId(String table, String type) {
		if (table.isEmpty())
			throw new IllegalArgumentException("The 'table' argument is empty");
		this.table = table;
		if (type.isEmpty())
			throw new IllegalArgumentException("The 'type' argument is empty");
		this.type = type;
		
		try {
			if(!HBase.existTable(table)){
				HBase.createTable(table, FAMILYS);
				getId("test"); //for jUnit test
			}
		} catch (IOException e) {
			LOG.error("Failed to Create table "+table+" with family ["+FAMILYS.toString()+"]");
		}
	}
	
	/**
	 * Constructor 
	 * assign default TABLE and TYPE
	 */
	private UId(){
		this(TABLE, TYPE);
	}
	
	public static UId getInstance(){
		if(uid == null){
			uid = new UId();
		}
		
		return uid;
	}

	/**
	 * Get or Generate a new Id of UID from Name This new name will be inserted
	 * to Table of HBase if success
	 * 
	 * @param name
	 * @return
	 */
	public String getId(String name) {
		short attempt = 3;
		NoSuchUName nsu = null;

		while (attempt-- > 0) {
			String newId = getIdFromCache(name);
			if (newId == null) {
				newId = getIdFromHBase(name);
				if (newId == null) {
					long id = increase();
					//LOG.info("UId.getId() ==> "+name + " -> UID.id:" + id);
					newId = String.format("%0" + String.valueOf(MAX_ID_LENGTH)
							+ "d", id);
					
					try {
						addToHBase(newId, name);
					} catch (IOException e) {
						nsu = new NoSuchUName(type, name);
						continue;
					}
				}
				
				addIdToCache(name, newId);
				addNameToCache(newId, name);
			}
			
			return newId;
		}
		if (nsu instanceof NoSuchUName) {
			LOG.error("Failed to assign an ID for type='" + type + "' name='"
					+ name + "'", nsu);
		}
		throw nsu;
	}

	/**
	 * Get name of UID from ID
	 * 
	 * @param id
	 * @return
	 */
	public String getName(String id) {
		String name = getNameFromCache(id);
		if (name == null) {
			try {
				name = getNameFromHBase(id);
			} catch (RuntimeException e) {
				throw new NoSuchUId(type, id);
			}
			if (name != null) {
				addIdToCache(name, id);
				addNameToCache(id, name);
			}
		}

		return name;
	}

	/**
	 * Discard all its in-memory caches.
	 */
	public void dropCaches() {
		idCache.clear();
		nameCache.clear();
	}

	/**
	 * Increase the serial number for every Rowkeys
	 * @return
	 */
	private long increase() {
		long id = 1;
		String family = "sys";
		String quailier = "c";
		String row = "2014";
		List<Record> list;
		try {
			list = HBase.getRecords(table, row);
			if (list.isEmpty())
				HBase.addRecord(table, row, family, quailier, 1);
			else
				id = HBase.increment(table, row, family, quailier);
		} catch (IOException e) {
			LOG.error("Failed to increase "+row+" "+family+":"+quailier+" counter! ", e);
		}
		return id;
	}

	private String getIdFromCache(String name) {
		return nameCache.get(name);
	}

	private String getIdFromHBase(String name) throws RuntimeException {
		return getHBase(name, ID);
	}

	private void addIdToCache(String name, String id) {
		String found = nameCache.get(name);
		if (found == null) {
			found = nameCache.putIfAbsent(name, id);
		}
		if (found != null && found != id) {
			throw new IllegalStateException("name=" + name + " => id=" + id
					+ ", already mapped to " + found);
		}
	}

	private String getNameFromCache(String id) {
		return idCache.get(id);
	}

	private String getNameFromHBase(String id) throws RuntimeException {
		return getHBase(id, NAME);
	}

	private void addNameToCache(String id, String name) {
		final String key = id;
		String found = idCache.get(key);
		if (found == null) {
			found = idCache.putIfAbsent(key, name);
		}
		if (found != null && !found.equals(name)) {
			throw new IllegalStateException("id=" + id + " => name=" + name
					+ ", already mapped to " + found);
		}
	}

	private String getHBase(String key, String family) {
		String value;
		try {
			value = HBase.getValue(table, key, family, type);
		} catch (IOException e) {
			throw new RuntimeException(key + " Should never be "+table, e);
		}
		return value;
	}

	/**
	 * Insert two new UID record to HBase for ID and name
	 * 
	 * @param id
	 * @param name
	 * @throws IOException
	 */
	private void addToHBase(String id, String name) throws IOException {
		try {
			HBase.addRecord(table, name, ID, type, id);
		} catch (IOException e) {
			LOG.error("Failed to Put!  NAME leaked: " + name + " of type: "
					+ type, e);
			throw e;
		}
		try {
			HBase.addRecord(table, id, NAME, type, name);
		} catch (IOException e) {
			LOG.error("Failed to Put!  ID leaked: " + id + " of type: " + type,
					e);
			throw e;
		}

	}

	private static byte[] toBytes(final String s) {
		return s.getBytes(CHARSET);
	}

	private static String fromBytes(final byte[] b) {
		return new String(b, CHARSET);
	}
}
