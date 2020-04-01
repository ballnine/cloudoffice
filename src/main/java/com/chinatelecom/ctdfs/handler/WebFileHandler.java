package com.chinatelecom.ctdfs.handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinatelecom.ctdfs.bean.WebFieldInfo;
import com.chinatelecom.ctdfs.bean.WebFileInfo;
import com.chinatelecom.udp.component.ctdfs.CTDFSFileSystem;
import com.chinatelecom.udp.core.lang.text.TextHelper;

public class WebFileHandler {
	
	/**
	 * 文件存储模式
	 * @author lichao
	 *
	 */
	public enum FileStoreMode{
		SaveInHdfs,//存储在HDFS中
		SaveInFile,//存储在本地文件系统中
		SaveInAll//两个地方都进行存储
	}
	
	private final static Logger logger=LogManager.getLogger(WebFileHandler.class); 

	
	/**
	 * 分析文件上传Form并做持久化
	 * @param request
	 * @param response
	 * @param filePath
	 * @param isStoreInHdfs 存在本地文件系统或者是Hdfs上
	 * @return
	 * @throws IOException
	 * @throws FileUploadException
	 */
	public  List<IFormFieldInfo> uploadFileForm(HttpServletRequest request, String filePath,FileStoreMode storeMode) throws IOException, FileUploadException{
		if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
		ServletFileUpload uploadHandler=new ServletFileUpload();
		List<IFormFieldInfo> webFields=new ArrayList<IFormFieldInfo>();
		FileItemIterator  iter  = uploadHandler.getItemIterator(request);
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			if (!item.isFormField()) {
				String newName=String.format("%s.%s", UUID.randomUUID().toString(), TextHelper.getRightPart(item.getName(), ".", false));
				int size=0;
				CTDFSFileSystem fileSystem=CTDFSFileSystem.getInstance();
				OutputStream outStream=null;
				try{
					String tempFileName=filePath + newName;
					if(storeMode==FileStoreMode.SaveInFile || storeMode==FileStoreMode.SaveInAll){
						outStream=new FileOutputStream(tempFileName);
						logger.info(String.format("write stream to local file %s",tempFileName));
						size=fileSystem.writeFile(outStream, item.openStream());
						outStream.close();
						outStream=null;
					}
					if(storeMode==FileStoreMode.SaveInHdfs){
						size=fileSystem.writeFile(tempFileName, item.openStream());
					}
					else if (storeMode==FileStoreMode.SaveInAll){
						fileSystem.uploadFile(tempFileName, tempFileName);
					}
				}
				catch(IOException e){
					throw e;
				}
				finally{
					if(outStream!=null) outStream.close();
				}
				WebFileInfo webFile=new WebFileInfo();
				webFile.setSize(size);
				webFile.setFileName(item.getName());
				webFile.setDfsFileName(newName);
				webFile.setPath(filePath);
				webFields.add(webFile);
			}
			else{
				InputStream stringStream = null;
				try{
					stringStream=item.openStream();
					webFields.add(new WebFieldInfo(item.getFieldName(), Streams.asString(stringStream)));
				}
				catch(IOException e){
					throw e;
				}
				finally{
					if(stringStream!=null) stringStream.close();
				}
			}
		}
		return webFields;
	}
	
	
	
	public ArrayList<WebFileInfo> uploadFile(HttpServletRequest request, HttpServletResponse response, String filePath) throws Exception{
		if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
        }
		ServletFileUpload uploadHandler=new ServletFileUpload();
		ArrayList<WebFileInfo> webFiles=new ArrayList<WebFileInfo>();
		FileItemIterator  iter  = uploadHandler.getItemIterator(request);
		while (iter.hasNext()) {
			FileItemStream item = iter.next();
			if (!item.isFormField()) {
				CTDFSFileSystem fileSystem=new CTDFSFileSystem();
				String newName=UUID.randomUUID().toString() + "." + TextHelper.getRightPart(item.getName(), ".", false);
				int size=fileSystem.writeFile(filePath + newName, item.openStream());
				WebFileInfo webFile=new WebFileInfo();
				webFile.setSize(size);
				webFile.setFileName(item.getName());
				webFile.setDfsFileName(newName);
				webFile.setPath(filePath);
				webFiles.add(webFile);
			}
		}
		return webFiles;
	}
	
}
