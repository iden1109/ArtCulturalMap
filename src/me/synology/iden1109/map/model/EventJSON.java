package me.synology.iden1109.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventJSON {
	String UID;
	String title;
	String category;
	String showUnit;
	String descriptionFilterHtml;
	String discountInfo;
	String imageUrl;
	String masterUnit;
	String subUnit;
	String supportUnit;
	String otherUnit;
	String webSales;
	String sourceWebPromote;
	String comment;
	String editModifyDate;
	String sourceWebName;
	String startDate;
	String endDate;
	String hitRate;
	
	String time;
	String location;
	String locationName;
	String onsales;
	String latitude;
	String longitude;
	String price;
	String endTime;
	
	List<EventItemJSON> showinfo = new ArrayList<EventItemJSON>();
	
	public List<EventItemJSON> getShowinfo() {
		return showinfo;
	}
	public void setShowinfo(List<EventItemJSON> showinfo) {
		this.showinfo = showinfo;
	}
	
	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getShowUnit() {
		return showUnit;
	}
	public void setShowUnit(String showUnit) {
		this.showUnit = showUnit;
	}
	public String getDescriptionFilterHtml() {
		return descriptionFilterHtml;
	}
	public void setDescriptionFilterHtml(String descriptionFilterHtml) {
		this.descriptionFilterHtml = descriptionFilterHtml;
	}
	public String getDiscountInfo() {
		return discountInfo;
	}
	public void setDiscountInfo(String discountInfo) {
		this.discountInfo = discountInfo;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getMasterUnit() {
		return masterUnit;
	}
	public void setMasterUnit(String masterUnit) {
		this.masterUnit = masterUnit;
	}
	public String getSubUnit() {
		return subUnit;
	}
	public void setSubUnit(String subUnit) {
		this.subUnit = subUnit;
	}
	public String getSupportUnit() {
		return supportUnit;
	}
	public void setSupportUnit(String supportUnit) {
		this.supportUnit = supportUnit;
	}
	public String getOtherUnit() {
		return otherUnit;
	}
	public void setOtherUnit(String otherUnit) {
		this.otherUnit = otherUnit;
	}
	public String getWebSales() {
		return webSales;
	}
	public void setWebSales(String webSales) {
		this.webSales = webSales;
	}
	public String getSourceWebPromote() {
		return sourceWebPromote;
	}
	public void setSourceWebPromote(String sourceWebPromote) {
		this.sourceWebPromote = sourceWebPromote;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getEditModifyDate() {
		return editModifyDate;
	}
	public void setEditModifyDate(String editModifyDate) {
		this.editModifyDate = editModifyDate;
	}
	public String getSourceWebName() {
		return sourceWebName;
	}
	public void setSourceWebName(String sourceWebName) {
		this.sourceWebName = sourceWebName;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getHitRate() {
		return hitRate;
	}
	public void setHitRate(String hitRate) {
		this.hitRate = hitRate;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getOnsales() {
		return onsales;
	}
	public void setOnsales(String onsales) {
		this.onsales = onsales;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
