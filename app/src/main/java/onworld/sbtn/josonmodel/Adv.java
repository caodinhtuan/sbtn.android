package onworld.sbtn.josonmodel;

import java.util.HashMap;

public class Adv {
	private String linkClick;
	private String link_src;
	private String type;
	private long duration=0;
	public HashMap<String, String> mapTracking = new HashMap<String, String>();
	
	public String getLinkClick() {
		return linkClick;
	}
	public void setLinkClick(String linkClick) {
		this.linkClick = linkClick;
	}
	public String getLink_src() {
		return link_src;
	}
	public void setLink_src(String link_src) {
		this.link_src = link_src;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public long getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public HashMap<String, String> getMapTracking() {
		return mapTracking;
	}
	
	public void addTracking(String key, String value) {
		mapTracking.put(key, value);
	}
}
