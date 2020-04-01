package com.chinatelecom.ctdfs.validation;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.chinatelecom.ctdfs.departmentfile.service.DepartmentFileService;
import com.chinatelecom.ctdfs.util.JSONUtil;
import com.chinatelecom.ctdfs.util.SpringContextUtil;
import com.chinatelecom.udp.core.lang.json.JSONObject;
import com.chinatelecom.udp.core.lang.text.TextHelper;

public class OrgValidater implements IValidator{

	@Override
	public Boolean validate(HttpServletRequest request,HttpServletResponse response) {
		/*String requestName = TextHelper.getRightPart(request.getRequestURI(), "/", false);
		JSONObject config = JSONUtil.readJSONFile("stategy.json");
		int user = 0;
		String fileId = "";
		
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
    	ServletFileUpload fileUpload = new ServletFileUpload(diskFileItemFactory);
		if(ServletFileUpload.isMultipartContent(request)) {
			try {
				List<FileItem> list = fileUpload.parseRequest(request);
				for (FileItem item : list) {
		    		if(item.isFormField()){
		    			String name = item.getFieldName();
		    			String value = item.getString("UTF-8");
		    			if (name.equals("empeeAcct")) {
		    				user = Integer.parseInt(value);
		    			} else if (name.equals("parentId")) {
		    				fileId = value;
		    			}
		    		}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			user = Integer.parseInt(request.getParameter("id"));
			if (request.getParameter("fileId").toString() != null) {
				fileId = request.getParameter("fileId");
			} else if (request.getParameter("parentId").toString() != null) {
				fileId = request.getParameter("parentId");
			} else {
				return true;
			}
		}
			
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("userId", user);
		paraMap.put("fileId", fileId);
	    DepartmentFileService orgFileService = (DepartmentFileService) SpringContextUtil.getApplicationContext().getBean(DepartmentFileService.class);
		int level = orgFileService.getOrgAccess(paraMap);
		int accessLevel = 0;
		
		if (config.getJSONObject(requestName) != null) {
			JSONObject partConfig = new JSONObject();
			partConfig = config.getJSONObject(requestName);
			if (partConfig.getJSONObject("access") != null) {
				accessLevel = partConfig.getInt("access");
			}
		}
		if (level >= accessLevel) {
			return true;
		}*/
		return false;
	}

}
