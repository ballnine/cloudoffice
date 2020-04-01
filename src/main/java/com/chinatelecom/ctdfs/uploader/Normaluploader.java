package com.chinatelecom.ctdfs.uploader;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.chinatelecom.ctdfs.handler.IFormFieldInfo;
import com.chinatelecom.ctdfs.handler.WebFileHandler;
import com.chinatelecom.ctdfs.handler.WebFileHandler.FileStoreMode;

public class Normaluploader implements IUploader{
	private final static Logger logger=LogManager.getLogger(Normaluploader.class);
	@Autowired
	private WebFileHandler webfilehandler;	
	private String filePath="/user/hadoop/input/";
	
	private FileStoreMode fileStoreMode=FileStoreMode.SaveInHdfs;
	@Override
	public List<IFormFieldInfo> upload(HttpServletRequest request) {
		// TODO Auto-generated method stub
		//上传文件到本地或者hdfs
		try {
			List<IFormFieldInfo> webFields = webfilehandler.uploadFileForm(request,filePath,fileStoreMode);
			return webFields;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		return null;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public FileStoreMode getFileStoreMode() {
		return fileStoreMode;
	}
	public void setFileStoreMode(FileStoreMode fileStoreMode) {
		this.fileStoreMode = fileStoreMode;
	}
	
}
