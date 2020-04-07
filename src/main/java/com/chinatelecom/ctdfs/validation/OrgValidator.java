package com.chinatelecom.ctdfs.validation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.departmentfile.service.DepartmentFileService;
import com.chinatelecom.ctdfs.personalfile.service.PersonalFileService;
import com.chinatelecom.ctdfs.util.JSONUtil;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.ctdfs.util.SpringContextUtil;
import com.chinatelecom.udp.core.lang.json.JSONObject;
import com.chinatelecom.udp.core.lang.text.TextHelper;

public class OrgValidator implements IValidator{
	private CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();

	@Override
	public Boolean validate(HttpServletRequest request,HttpServletResponse response) {
		String requestName = TextHelper.getRightPart(request.getRequestURI(), "/", false);
		JSONObject config = JSONUtil.readJSONFile("stategy.json");
		int user = 0;
		String fileId = "";
		String groupId = "";
		
		if(ServletFileUpload.isMultipartContent(request)) {
			MultipartHttpServletRequest multiReq = multipartResolver.resolveMultipart(request);
	        user = Integer.parseInt(multiReq.getParameter("empeeAcct"));
	        fileId = multiReq.getParameter("parentId");
	        groupId = multiReq.getParameter("groupId");
		} else {
			user = Integer.parseInt(request.getParameter("id"));
			groupId = request.getParameter("groupId");
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
		System.out.println("groupId is " + groupId);
		
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
		DepartmentFileService orgFileService = (DepartmentFileService) SpringContextUtil.getApplicationContext().getBean(DepartmentFileService.class);
		HashMap<String, Object> fileInfo = orgFileService.getFileInfoById(fileId,user);
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
			
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("userId", user);
		paraMap.put("fileId", groupId);
		int level = orgFileService.getOrgAccess(paraMap);
		int accessLevel = 0;
		
		if (config.getJSONObject(requestName) != null) {
			JSONObject partConfig = new JSONObject();
			partConfig = config.getJSONObject(requestName);
			if (partConfig.get("access") != null) {
				accessLevel = partConfig.getInt("access");
			}
		}
		if (level >= accessLevel) {
			return true;
		}
		return false;
	}

}
