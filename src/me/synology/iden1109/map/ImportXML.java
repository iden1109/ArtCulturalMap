package me.synology.iden1109.map;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import me.synology.iden1109.map.model.Event;
import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.model.EventItem;
import me.synology.iden1109.map.model.UId;
import me.synology.iden1109.map.util.StringUtil;
import me.synology.iden1109.map.util.TimeStampUtil;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.hadoop.hbase.client.HTable;

import ch.hsr.geohash.GeoHash;

import com.google.common.base.Splitter;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Import performing XML format data to HBase implementation
 * 
 * @author zhengyu
 * 
 */
public class ImportXML {

	private static final Logger LOG = LoggerFactory
			.getLogger(ImportXML.class);

	private static final String usage = "java -cp target/ArtCulturalMap-1.0.0.jar me.synology.iden1109.map.ImportXML source.xml \n"
			+ "    source.xml - path to the XML file to load.\n"
			+ "Records are stored in columns in the 's','e' familt, columns are: lon,lat,id,name,address,city,url,phone,type,zip\n";


	private UId uid;
	private static ImportXML imp;

	
	public ImportXML() {
		uid = UId.getInstance();
	}


	/**
	 * Load XML file
	 * @param fileName  XML file full path and name
	 * @return
	 */
	public EventComponent loadXML(String fileName) {
		
		if(fileName == null || fileName.isEmpty())
			throw new IllegalArgumentException("The 'fileName' argument need to be provided");
		
		EventComponent total = new Event();
		long start = System.currentTimeMillis();
		int records = 0;
		String oldLonLat="", thisLonLat;
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File(fileName)); // file: XXX.xml
			Element root = doc.getRootElement();
			List<Element> eis = root.elements();
			for (Element ei : eis) {//eventItem
				List<Element> es = ei.elements();
				for (Element e : es) {//event
					List<Element> infos = e.elements();
					for (Element info : infos) {
						
						//Info
						EventComponent main = new Event();
						oldLonLat = "";
						List<Attribute> attriList1 = info.attributes();
						for (Attribute attr : attriList1) {
							//LOG.debug("Attributes -> "+attr.getName() + ": " + attr.getValue());
							main.putAttr(uid.getId(attr.getName()), StringUtil.replace(attr.getValue()));
						}
						
						//showInfo / element
						Element si = info.element("showInfo");
						List<Element> els = si.elements();
						for (Element el : els) {
							//LOG.debug("Elements -> "+el.getName() + ": " + el.getText());
							EventComponent sub = new EventItem();
							
							List<Attribute> attriList2 = el.attributes();
							for (Attribute attr : attriList2) {
								//LOG.debug("Attributes -> "+attr.getName() + ": " + attr.getValue());
								sub.putAttr(uid.getId(attr.getName()), StringUtil.replace(attr.getValue()));
							}//end of element
							
							if(oldLonLat.isEmpty())
								oldLonLat = sub.getAttr(uid.getId("longitude"))+sub.getAttr(uid.getId("latitude"));
							thisLonLat = sub.getAttr(uid.getId("longitude"))+sub.getAttr(uid.getId("latitude"));
							
							if(thisLonLat.equals(oldLonLat)){
								main.add(sub);
							}else{
								oldLonLat = thisLonLat;
								total.add(main);
								records++;
								try {
									main = main.clone();
								} catch (CloneNotSupportedException e1) {
									LOG.error("Clone Event object failed", e1);
								}
								main.clearItems();
								main.add(sub);
							}

						}//end of showInfo
						if(main.getLongitude() > 0 && main.getLatitude() > 0){
							total.add(main);
							records++;
						}
						
					}//end of Info
				}//end of event
			}//end of eventItem
		} catch (DocumentException e) {
			LOG.error("XML file loading error", e);
		} finally {
			LOG.info(String.format("loadXML() %s records in %sms.", records, System.currentTimeMillis() - start));
		}
		return total;
	}
	
	/**
	 * store data into HBase
	 * @param mainEvent
	 * @return how many records stores
	 */
	public int ingest(EventComponent mainEvent) {
		long start = System.currentTimeMillis();
		LOG.info("ingest() --> " + mainEvent+"  size:"+mainEvent.size());
		int records = mainEvent.ingest();
		LOG.info(String.format("ingest()  %s records in %sms.", records, System.currentTimeMillis() - start));
		return records;
	}
	
	
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.out.println(usage);
			System.exit(0);
		}

		imp = new ImportXML();
		imp.ingest(imp.loadXML(args[0]));
		
		LOG.info("Import XML successfully");
	}
	
	
}
