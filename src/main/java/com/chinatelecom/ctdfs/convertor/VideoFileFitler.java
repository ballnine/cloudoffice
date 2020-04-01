package com.chinatelecom.ctdfs.convertor;

import java.io.File;
import java.io.FilenameFilter;

public class VideoFileFitler implements FilenameFilter {

	private String fileName;
	
	public VideoFileFitler(String fileName){
		this.fileName=fileName;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		if(name.startsWith(fileName) ){
			return true;
		}
		return false;
	}
}
