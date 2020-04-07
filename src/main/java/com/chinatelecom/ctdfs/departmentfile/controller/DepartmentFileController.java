package com.chinatelecom.ctdfs.departmentfile.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinatelecom.ctdfs.controller.UploadController;
import com.chinatelecom.ctdfs.departmentfile.service.DepartmentFileService;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.lang.json.JSONObject;

@Controller
public class DepartmentFileController {
	@Resource
	private DepartmentFileService departmentFileService;
	
	private static Logger log = LogManager.getLogger(UploadController.class.getName());

	@RequestMapping("/addDepartmentFile")
	@ResponseBody
	public DataOutputFormat addDepartmentFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("empeeAcct"));
		String parentId = request.getParameter("parentId");
		String fileName = request.getParameter("fileName");
		String groupId = request.getParameter("groupId");
		JSONObject parameters = new JSONObject();
		parameters.put("userId", user);
		parameters.put("parentId", parentId);
		parameters.put("groupId", groupId);
		parameters.put("fileName", fileName);
		DataOutputFormat result = departmentFileService.addDepartmentFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/addDepartmentFolder")
	@ResponseBody
	public DataOutputFormat addDepartmentFolder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String parentId = request.getParameter("parentId");
		String fileName = request.getParameter("fileName");
		String groupId = request.getParameter("groupId");
		JSONObject parameters = new JSONObject();
		parameters.put("userId", user);
		parameters.put("parentId", parentId);
		parameters.put("groupId", groupId);
		parameters.put("fileName", fileName);
		DataOutputFormat result = departmentFileService.addDepartmentFolder(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/listDepartmentFile")
	@ResponseBody
	public DataOutputFormat listDepartmentFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String parent = request.getParameter("parentId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("parentId", parent);
		DataOutputFormat result = departmentFileService.listDepartmentFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/downloadDepartmentFile")
	@ResponseBody
	public DataOutputFormat downloadDepartmentFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String groupId = request.getParameter("groupId");
		String localPath = request.getParameter("localPath");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		parameters.put("groupId", groupId);
		parameters.put("localPath", localPath);
		DataOutputFormat result = departmentFileService.downloadDepartmentFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/listOrgFileInfo")
	@ResponseBody
	public DataOutputFormat listOrgFileInfo(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		DataOutputFormat result = departmentFileService.listOrgFileInfo(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/renameOrgFile")
	@ResponseBody
	public DataOutputFormat renameFile(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String fileName = request.getParameter("fileName");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		parameters.put("fileName", fileName);
		DataOutputFormat result = departmentFileService.renameFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/addAuditOrgFile")
	@ResponseBody
	public DataOutputFormat addAuditOrgFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("empeeAcct"));
		String parentId = request.getParameter("parentId");
		String fileName = request.getParameter("fileName");
		String groupId = request.getParameter("groupId");
		JSONObject parameters = new JSONObject();
		parameters.put("userId", user);
		parameters.put("parentId", parentId);
		parameters.put("groupId", groupId);
		parameters.put("fileName", fileName);
		DataOutputFormat result = departmentFileService.addAuditOrgFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/listAuditedFile")
	@ResponseBody
	public DataOutputFormat listAuditedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		DataOutputFormat result = departmentFileService.listAuditedFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/listAuditFile")
	@ResponseBody
	public DataOutputFormat listAuditFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		DataOutputFormat result = departmentFileService.listAuditFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/auditFile")
	@ResponseBody
	public DataOutputFormat auditFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String reviewId = request.getParameter("auditId");
		int state = Integer.parseInt(request.getParameter("state"));
		String fileId = request.getParameter("fileId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("reviewId", reviewId);
		parameters.put("state", state);
		parameters.put("fileId", fileId);
		DataOutputFormat result = departmentFileService.auditFile(parameters,request);
		String message = result.toString();
		return result;
	}
}
