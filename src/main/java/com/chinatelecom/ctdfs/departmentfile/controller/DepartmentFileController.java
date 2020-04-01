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
import com.chinatelecom.udp.core.lang.json.JSONObject;

@Controller
public class DepartmentFileController {
	@Resource
	private DepartmentFileService departmentFileService;
	
	private static Logger log = LogManager.getLogger(UploadController.class.getName());

	@RequestMapping("/addDepartmentFile")
	@ResponseBody
	public String addDepartmentFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//int user = Integer.parseInt(request.getParameter("id"));
		//String parent = request.getParameter("parentId");
		JSONObject parameters = null;
		//parameters.put("empeeAcct", user);
		//parameters.put("parent", parent);
		JSONObject result = departmentFileService.addDepartmentFile(parameters,request);
		String message = result.toString();
		return message;
	}
	
	@RequestMapping("/listDepartmentFile")
	@ResponseBody
	public String listDepartmentFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String parent = request.getParameter("parentId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("parentId", parent);
		JSONObject result = departmentFileService.listDepartmentFile(parameters,request);
		String message = result.toString();
		return message;
	}
	
	@RequestMapping("/downloadDepartmentFile")
	@ResponseBody
	public String downloadDepartmentFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String groupId = request.getParameter("groupId");
		String localPath = request.getParameter("localPath");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		parameters.put("groupId", groupId);
		parameters.put("localPath", localPath);
		JSONObject result = departmentFileService.downloadDepartmentFile(parameters,request);
		String message = result.toString();
		return message;
	}
	
	@RequestMapping("/addAuditOrgFile")
	@ResponseBody
	public String addAuditOrgFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//int user = Integer.parseInt(request.getParameter("id"));
		//String fileId = request.getParameter("destId");
		JSONObject parameters = new JSONObject();
		//parameters.put("empeeAcct", user);
		//parameters.put("destId", destId);
		JSONObject result = departmentFileService.addAuditOrgFile(parameters,request);
		String message = result.toString();
		return message;
	}
	
	@RequestMapping("/listAuditedFile")
	@ResponseBody
	public String listAuditedFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		JSONObject result = departmentFileService.listAuditedFile(parameters,request);
		String message = result.toString();
		return message;
	}
	
	@RequestMapping("/listAuditFile")
	@ResponseBody
	public String listAuditFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		JSONObject result = departmentFileService.listAuditFile(parameters,request);
		String message = result.toString();
		return message;
	}
	
	@RequestMapping("/auditFile")
	@ResponseBody
	public String auditFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String reviewId = request.getParameter("auditId");
		int state = Integer.parseInt(request.getParameter("state"));
		String fileId = request.getParameter("fileId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("reviewId", reviewId);
		parameters.put("state", state);
		parameters.put("fileId", fileId);
		JSONObject result = departmentFileService.auditFile(parameters,request);
		String message = result.toString();
		return message;
	}
}
