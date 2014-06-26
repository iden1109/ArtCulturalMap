package me.synology.iden1109.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import me.synology.iden1109.map.model.EventComponent;
import me.synology.iden1109.map.test.ImportXMLFromURL;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportXMLfromURL {
	
	private static final String usage = "java -cp target/ArtCulturalMap-1.0.0.jar me.synology.iden1109.map.ImportXMLfromURL url.txt \n"
			+ "    url.txt - the URL of XML source.\n";
	
	private static final Logger LOG = LoggerFactory.getLogger(ImportXMLFromURL.class);
	private ImportXML imp;
	private EventComponent ec;
	
	ImportXMLfromURL(){
		imp = new ImportXML();
	}
	
	private void purgeTemp(String filePath) {
		File file = new File(filePath);
		if (file.exists())
			file.delete();
	}

	private void LoadfromURL(String urlPath, String outPath) {
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(urlPath);
			URLConnection conn = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) conn;

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build(new StringReader(sb.toString()));

			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.output(doc, new FileWriter(outPath));

		} catch (IllegalArgumentException e) {
			LOG.error("IllegalArgumentException -> " + e.toString());
		} catch (MalformedURLException e) {
			LOG.error("MalformedURLException -> " + e.toString());
		} catch (IOException e) {
			LOG.error("IOException -> " + e.toString());
		} catch (JDOMException e) {
			LOG.error("JDOMException -> " + e.toString());
		}
	}

	private int IngestXMLtoSystem(String filePath) {
		ec = imp.loadXML(filePath);
		return imp.ingest(ec);
	}
	
	public static void main(String[] args) {
		String filePath = "output.xml";
		String defaultUrlTxt = "conf/arturl.txt";
		
		if (args.length != 1) {
			System.out.println(usage);
			System.exit(0);
		}else{
			defaultUrlTxt = args[0];
		}
		
		ImportXMLfromURL ixfu = new ImportXMLfromURL();
		BufferedReader br;
		String line = null;
		try {
			br = new BufferedReader(new FileReader(defaultUrlTxt));
			while ((line = br.readLine()) != null) {
				ixfu.LoadfromURL(line, filePath);
				ixfu.IngestXMLtoSystem(filePath);
			}
		} catch (FileNotFoundException e) {
			LOG.error("FileNotFoundException" + e.toString());
		} catch (IOException e) {
			LOG.error("IOException" + e.toString());
		}
		ixfu.purgeTemp(filePath);
		
		LOG.info("Import XML from URL successfully");
	}

}
