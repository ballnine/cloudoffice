package com.chinatelecom.ctdfs.printer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chinatelecom.ctdfs.attach.AttachmentService;
import com.chinatelecom.ctdfs.bean.WebFieldInfo;
import com.chinatelecom.ctdfs.bean.WebFileInfo;
import com.chinatelecom.ctdfs.handler.IFormFieldInfo;
import com.chinatelecom.udp.core.lang.json.JSONArray;
import com.chinatelecom.udp.core.lang.json.JSONObject;

public class UploadOutputPrinter implements IPrinter {
	private static final String Encoding="UTF-8";
	private final static Logger logger=LogManager.getLogger(UploadOutputPrinter.class);
	private Boolean isAddtoDB;
	public Boolean getIsAddtoDB() {
		return isAddtoDB;
	}
	public void setIsAddtoDB(Boolean isAddtoDB) {
		this.isAddtoDB = isAddtoDB;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	private String reference;
	@Override
	public void print(HttpServletResponse response, List<IFormFieldInfo> webFields) {
		// TODO Auto-generated method stub
		JSONArray result = new JSONArray();
		String redirect=null;
		for(IFormFieldInfo webField:webFields){
			if(webField.isFileField()){
				WebFileInfo webFile=(WebFileInfo) webField;
				//TODO add user info
				String fileId=null;
				if(isAddtoDB){
					fileId=AttachmentService.getInstance().addFile(webFile,"");
				}
				JSONObject jsonInfo = new JSONObject();
		   jsonInfo.put("name", webFile.getFileName());
		   jsonInfo.put("size", webFile.getSize());
		   if(isAddtoDB){
		    	jsonInfo.put("id", fileId);
		    	}
		   jsonInfo.put("sname",webFile.getDfsFileName());
		   result.put(jsonInfo);
		   logger.info(String.format("uploaded file %s,reference=%s",webFile.getDfsFileName(),reference));
			}
			else{
				WebFieldInfo fieldInfo=(WebFieldInfo) webField;
				if("redirect".equals(fieldInfo.getName())){
					redirect=fieldInfo.getValue();
				}
			}
		}
		if(redirect!=null){//use for old browser which use iframe transport
			try {
				response.sendRedirect(String.format(redirect, URLEncoder.encode(result.toString(), "utf-8")));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			response.setCharacterEncoding(Encoding);
			response.setContentType("text/html;charset="+ Encoding);
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.write(result.toString());
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
