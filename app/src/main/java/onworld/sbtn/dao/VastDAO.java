/**
 * @author cakiet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package onworld.sbtn.dao;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import onworld.sbtn.josonmodel.Adv;
import onworld.sbtn.utils.Utils;

public class VastDAO {
	public static final VastDAO Singleton = new VastDAO();
	private VastDAO() {}
	
	public Adv getAdvContent(String link) {
		try {
			Adv adv = new Adv();
			Document doc = getDocument(link);
			NodeList nodeNonLinear = doc.getElementsByTagName("NonLinear");
			for (int i = 0; i < nodeNonLinear.getLength(); i++) { //Only One
				Node node = nodeNonLinear.item(i);
				Element fstElmnt = (Element) node;
				adv.setLink_src(getValue(fstElmnt, "URL"));
				adv.setType("image");
			}
			NodeList nodeClickThrough = doc.getElementsByTagName("NonLinearClickThrough");
			for (int i = 0; i < nodeClickThrough.getLength(); i++) { //Only One
				Node node = nodeClickThrough.item(i);
				Element fstElmnt = (Element) node;
				adv.setLinkClick(getValue(fstElmnt, "URL"));
			}
			
			if (adv.getLink_src() == null || adv.getLink_src().isEmpty()) {
				NodeList nodeMediaFile = doc.getElementsByTagName("MediaFile");
				for (int i = 0; i < nodeMediaFile.getLength(); i++) { //Only One
					Node node = nodeMediaFile.item(i);
					Element fstElmnt = (Element) node;
					adv.setLink_src(getValue(fstElmnt, "URL"));
					adv.setType("video");
				}
				NodeList nodeClickThrough1 = doc.getElementsByTagName("ClickThrough");
				for (int i = 0; i < nodeClickThrough1.getLength(); i++) { //Only One
					Node node = nodeClickThrough1.item(i);
					Element fstElmnt = (Element) node;
					adv.setLinkClick(getValue(fstElmnt, "URL"));
				}
				
				NodeList nodeTrackingEvents = doc.getElementsByTagName("Tracking");
				for (int i = 0; i < nodeTrackingEvents.getLength(); i++) {
					Node node = nodeTrackingEvents.item(i);
					Element fstElmnt = (Element) node;
					String key = fstElmnt.getAttribute("event");
					String value = getValue(fstElmnt, "URL");
					adv.addTracking(key, value);
				}
				
				NodeList nodeVideo = doc.getElementsByTagName("Video");
				for (int i = 0; i < nodeVideo.getLength(); i++) { //Only One
					Node node = nodeVideo.item(i);
					Element fstElmnt = (Element) node;
					String sDuration = getValue(fstElmnt, "Duration");
					adv.setDuration(Utils.Singleton.parseTime(sDuration));
				}
			}
			return adv;
		} catch(Exception ex){}
		
		return null;
	}
	
	//-----------------------------
	private Document getDocument(String link) throws Exception {
		URL url = new URL(link);
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(url.openStream()));
        doc.getDocumentElement().normalize();
        return doc;
	}
	
	private String getValue(Element element, String name) {
		try {
			NodeList list = element.getElementsByTagName(name);
	        Element e = (Element) list.item(0);
	        list = e.getChildNodes();
	        String value = ((Node) list.item(0)).getNodeValue();
	        value = value.trim();
	        return value;
		} catch (Exception ex){
		}
		return "";
	}
}
