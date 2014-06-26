package me.synology.iden1109.map.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.Assert;
import me.synology.iden1109.map.ImportXML;
import me.synology.iden1109.map.model.EventComponent;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportXMLFromURL {

	private static final Logger LOG = LoggerFactory.getLogger(ImportXMLFromURL.class);
	private ImportXML imp;
	private EventComponent ec;

	@Before
	public void setUp() throws Exception {
		imp = new ImportXML();
	}

	@After
	public void tearDown() throws Exception {
	}

	// 展覽資訊
	@Test
	public void testImport6012() {
		String filePath = "output.xml";
		String urlPath = "http://data.gov.tw/iisi/logaccess?dataUrl=http%3A%2F%2Fcloud.culture.tw%2Ffrontsite%2Ftrans%2FSearchShowAction.do%3Fmethod%3DdoFindTypeX%26category%3D6&type=XML&nid=6012";
		LoadfromURL(urlPath, filePath);
		int cnt = IngestXMLtoSystem(filePath);

		purgeTemp(filePath);
		Assert.assertTrue("Loading XML done", cnt > 0);
	}

	// 音樂表演資訊
	@Test
	public void testImport6017() {
		String filePath = "output.xml";
		String urlPath = "http://data.gov.tw/iisi/logaccess?dataUrl=http%3A%2F%2Fcloud.culture.tw%2Ffrontsite%2Ftrans%2FSearchShowAction.do%3Fmethod%3DdoFindTypeX%26category%3D1&type=XML&nid=6017";
		LoadfromURL(urlPath, filePath);
		int cnt = IngestXMLtoSystem(filePath);

		purgeTemp(filePath);
		Assert.assertTrue("Loading XML done", cnt > 0);
	}

	// 戲劇表演資訊
	@Test
	public void testImport6016() {
		String filePath = "output.xml";
		String urlPath = "http://data.gov.tw/iisi/logaccess?dataUrl=http%3A%2F%2Fcloud.culture.tw%2Ffrontsite%2Ftrans%2FSearchShowAction.do%3Fmethod%3DdoFindTypeX%26category%3D2&type=XML&nid=6016";
		LoadfromURL(urlPath, filePath);
		int cnt = IngestXMLtoSystem(filePath);

		purgeTemp(filePath);
		Assert.assertTrue("Loading XML done", cnt > 0);
	}

	// 舞蹈表演資訊
	@Test
	public void testImport6015() {
		String filePath = "output.xml";
		String urlPath = "http://data.gov.tw/iisi/logaccess?dataUrl=http%3A%2F%2Fcloud.culture.tw%2Ffrontsite%2Ftrans%2FSearchShowAction.do%3Fmethod%3DdoFindTypeX%26category%3D3&type=XML&nid=6015";
		LoadfromURL(urlPath, filePath);
		int cnt = IngestXMLtoSystem(filePath);

		purgeTemp(filePath);
		Assert.assertTrue("Loading XML done", cnt > 0);
	}

	// 親子活動
	@Test
	public void testImport6014() {
		String filePath = "output.xml";
		String urlPath = "http://data.gov.tw/iisi/logaccess?dataUrl=http%3A%2F%2Fcloud.culture.tw%2Ffrontsite%2Ftrans%2FSearchShowAction.do%3Fmethod%3DdoFindTypeX%26category%3D4&type=XML&nid=6014";
		LoadfromURL(urlPath, filePath);
		int cnt = IngestXMLtoSystem(filePath);

		purgeTemp(filePath);
		Assert.assertTrue("Loading XML done", cnt > 0);
	}

	// 演唱會
	@Test
	public void testImport6013() {
		String filePath = "output.xml";
		String urlPath = "http://data.gov.tw/iisi/logaccess?dataUrl=http%3A%2F%2Fcloud.culture.tw%2Ffrontsite%2Ftrans%2FSearchShowAction.do%3Fmethod%3DdoFindTypeX%26category%3D17&type=XML&nid=6013";
		LoadfromURL(urlPath, filePath);
		int cnt = IngestXMLtoSystem(filePath);

		purgeTemp(filePath);
		Assert.assertTrue("Loading XML done", cnt > 0);
	}

	// 其他藝文資訊
	@Test
	public void testImport6018() {
		String filePath = "output.xml";
		String urlPath = "http://data.gov.tw/iisi/logaccess?dataUrl=http%3A%2F%2Fcloud.culture.tw%2Ffrontsite%2Ftrans%2FSearchShowAction.do%3Fmethod%3DdoFindTypeX%26category%3D15&type=XML&nid=6018";
		LoadfromURL(urlPath, filePath);
		int cnt = IngestXMLtoSystem(filePath);

		purgeTemp(filePath);
		Assert.assertTrue("Loading XML done", cnt > 0);
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
		// ec.print();
		return imp.ingest(ec);
	}

}
