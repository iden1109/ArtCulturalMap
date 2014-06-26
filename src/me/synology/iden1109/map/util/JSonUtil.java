package me.synology.iden1109.map.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.model.EventItem;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSonUtil {

	private static final Logger LOG = LoggerFactory.getLogger(JSonUtil.class);
	private static ContainerFactory factory = new ContainerFactory() {
		public List creatArrayContainer() {
			return new LinkedList();
		}

		public Map createObjectContainer() {
			return new LinkedHashMap();
		}
	};

	
	public static String encode(Map<String, String> m) {
		JSONObject json = new JSONObject();
		for (String k : m.keySet()) {
			json.put(k, m.get(k));
		}
		
		return json.toString();
	}

	public static EventComponent decode(String s) {
		EventComponent item = new EventItem();
		JSONParser parser = new JSONParser();
		try {
			Map json = (Map) parser.parse(s, factory);
			Iterator iter = (Iterator) json.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				item.putAttr(entry.getKey().toString(), entry.getValue()
						.toString());
				//LOG.info(entry.getKey() + "=>" + entry.getValue());
			}
		} catch (ParseException e) {
			LOG.error("Failed to parse JSON format !!", e);
		}

		return item;
	}

}
