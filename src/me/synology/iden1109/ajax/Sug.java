package me.synology.iden1109.ajax;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

public class Sug extends ActionSupport{
	String query;
	List<String> result;
	
	
	
	public String getQuery() {
		return query;
	}



	public void setQuery(String query) {
		this.query = query;
	}



	public List<String> getResult() {
		result = new ArrayList<String>();
		result.add("a");
		result.add("b");
		result.add("c");
		return result;
	}



	public void setResult(List<String> result) {
		this.result = result;
	}



	public String execute(){
		
		return SUCCESS;
	}
}
