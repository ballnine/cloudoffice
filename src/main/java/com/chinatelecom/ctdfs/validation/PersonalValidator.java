package com.chinatelecom.ctdfs.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.personalfile.service.PersonalFileService;
import com.chinatelecom.ctdfs.util.HttpHelper;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.ctdfs.util.SpringContextUtil;
import com.chinatelecom.udp.core.lang.json.JSONObject;

public class PersonalValidator implements IValidator{
	private CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();

	@Override
	public Boolean validate(HttpServletRequest request,HttpServletResponse response) {
		int user = 0;
		String fileId = "";
		
		if(ServletFileUpload.isMultipartContent(request)) {
			MultipartHttpServletRequest multiReq = multipartResolver.resolveMultipart(request);
	        user = Integer.parseInt(multiReq.getParameter("empeeAcct"));
	        fileId = multiReq.getParameter("parentId");
		} else {
			user = Integer.parseInt(request.getParameter("id"));
			if (request.getParameter("fileId") != null) {
				fileId = request.getParameter("fileId").toString();
			} else if (request.getParameter("parentId") != null) {
				fileId = request.getParameter("parentId").toString();
			} else {
				return true;
			}
		}
		System.out.println("filter is here:");
		System.out.println("fileId is " + fileId);
		System.out.println("userId is " + user);
		
		WorkSmartCommonService commonService = (WorkSmartCommonService) SpringContextUtil.getApplicationContext().getBean(WorkSmartCommonService.class);
		HashMap<String, Object> loginInfo=commonService.getUserInfoById(user);
		if(loginInfo == null) {
			PrintWriter out = null;
			JSONObject resultJson = new JSONObject();
			resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
			resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
			try {
				out = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.setHeader("message", "true");
			out.append(resultJson.toString());
			return false;
		}
		PersonalFileService personalFileService = (PersonalFileService) SpringContextUtil.getApplicationContext().getBean(PersonalFileService.class);
		HashMap<String, Object> fileInfo = personalFileService.getFileInfoById(fileId,user);
		if(fileId.isEmpty() || fileInfo == null) {
			PrintWriter out = null;
			JSONObject resultJson = new JSONObject();
			resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
			resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
			try {
				out = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.setHeader("message", "true");
			out.append(resultJson.toString());
			return false;
		}
        
		if (fileId.equals("~")) {
			return false;
		} else {
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
			/*paraMap.put("userId", paramInfo.get("id"));
			if (paramInfo.get("fileId") != null) {
				paraMap.put("fileId", paramInfo.get("fileId"));
			} else {
				paraMap.put("fileId", paramInfo.get("parentId"));
			}*/
			paraMap.put("userId", user);
			paraMap.put("fileId", fileId);
			Boolean result = personalFileService.getAccess(paraMap);
			return result;
		}	
	}

}
