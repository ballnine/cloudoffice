package com.chinatelecom.ctdfs.bean;

import com.chinatelecom.ctdfs.handler.IFormFieldInfo;

public class WebFileInfo implements IFormFieldInfo {
	
	private String fileName;//which is file name of original upload
	
	private String dfsFileName;//which is file name of store in hdfs
	
	private long size;
	
	private String path;// //which is directory name of store in hdfs

	public WebFileInfo(){
		
	}
	
	public WebFileInfo(String fileName, String dfsFileName, long size,
			String path) {
		super();
		this.fileName = fileName;
		this.dfsFileName = dfsFileName;
		this.size = size;
		this.path = path;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDfsFileName() {
		return dfsFileName;
	}

	public void setDfsFileName(String dfsFileName) {
		this.dfsFileName = dfsFileName;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isFileField() {
		return true;
	}

	
	
}
