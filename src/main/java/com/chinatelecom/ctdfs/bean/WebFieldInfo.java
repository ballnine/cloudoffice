package com.chinatelecom.ctdfs.bean;

import com.chinatelecom.ctdfs.handler.IFormFieldInfo;

public class WebFieldInfo implements IFormFieldInfo{

	private String name;
	
	private String value;
	
	public WebFieldInfo(){
			
	}

	public WebFieldInfo(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isFileField() {
		return false;
	}


	
}
