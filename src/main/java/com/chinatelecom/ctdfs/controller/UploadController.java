package com.chinatelecom.ctdfs.controller;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinatelecom.udp.component.ctdfs.CTDFSFileSystem;
import com.chinatelecom.udp.component.ctdfs.MCTDFSFileSystem;

@Controller
public class UploadController {
	private static Logger log = LogManager.getLogger(UploadController.class.getName());
	private static MCTDFSFileSystem mctdfs=MCTDFSFileSystem.getInstance();
	private static CTDFSFileSystem ctdfs=CTDFSFileSystem.getInstance();
	
	@RequestMapping("/upload")
	@ResponseBody
	public String upload() {
		String localFile="/tmp/fxdfile";
		String filePath="ctdfs://dfs/test/fxdfile";
		try {
			mctdfs.uploadFile(localFile, filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
		return filePath;
	}
	
	@RequestMapping("/delete")
	@ResponseBody
	public String delete() {
		String filePath="ctdfs://dfs/test/fxd.202001071034";
		mctdfs.deleteFile(filePath);
		return filePath;
	}
	
	@RequestMapping("/rename")
	@ResponseBody
	public String rename() {
		return null;
		
	}
	
	@RequestMapping("/statue")
	@ResponseBody
	public String showstatue() {
		String filePath="ctdfs://dfs/test/fxd.202001071034";
		FileStatus[] fileStatus=mctdfs.showstatue(filePath);
		for(FileStatus fs:fileStatus)
			log.info("<<<<<<<<<<file status>>>>>>>>>"+fs.toString());
		return fileStatus.toString();
		
	}
}
