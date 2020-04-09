package com.chinatelecom.ctdfs.personalfile.controller;

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
import com.chinatelecom.ctdfs.personalfile.service.PersonalFileService;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.lang.json.JSONObject;

@Controller
public class PersonalFileController {
	@Resource
    private PersonalFileService personalFileService;
	
	private static Logger log = LogManager.getLogger(PersonalFileController.class.getName());

	@RequestMapping("/addPersonalFile")
	@ResponseBody
	public DataOutputFormat addPersonalFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("empeeAcct"));
		String parentId = request.getParameter("parentId");
		String fileName = request.getParameter("fileName");
		JSONObject parameters = new JSONObject();
		parameters.put("userId", user);
		parameters.put("parentId", parentId);
		parameters.put("fileName", fileName);
		DataOutputFormat result = personalFileService.addPersonalFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/listPersonalFile")
	@ResponseBody
	public DataOutputFormat listPersonalFile(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String parent = request.getParameter("parentId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("parentId", parent);
		DataOutputFormat result = personalFileService.listPersonalFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/downloadPersonalFile")
	@ResponseBody
	public DataOutputFormat downloadPersonalFile(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String path = request.getParameter("path");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		parameters.put("path", path);
		DataOutputFormat result = personalFileService.downloadFile(parameters,request,response);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/listPersonalFileInfo")
	@ResponseBody
	public DataOutputFormat listPersonalFileInfo(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		DataOutputFormat result = personalFileService.listPersonalFileInfo(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/deletePersonalFile")
	@ResponseBody
	public DataOutputFormat deletePersonalFile(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		DataOutputFormat result = personalFileService.deletePersonalFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/addPersonalFolder")
	@ResponseBody
	public DataOutputFormat createPersonalFolder(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String parentId = request.getParameter("parentId");
		String fileName = request.getParameter("fileName");
		JSONObject parameters = new JSONObject();
		parameters.put("fileName", fileName);
		parameters.put("empeeAcct", user);
		parameters.put("parentId", parentId);
		DataOutputFormat result = personalFileService.createPersonalFolder(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/listRecycler")
	@ResponseBody
	public DataOutputFormat getRecycler(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		DataOutputFormat result = personalFileService.getRecycler(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/deleteRecycler")
	@ResponseBody
	public DataOutputFormat deleteRecycler(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		int status = Integer.parseInt(request.getParameter("status"));
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		parameters.put("status", status);
		DataOutputFormat result = personalFileService.deleteRecycler(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/restoreRecycler")
	@ResponseBody
	public DataOutputFormat restoreRecycler(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String parentId = request.getParameter("parentId");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		parameters.put("parentId", parentId);
		DataOutputFormat result = personalFileService.restoreRecycler(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/renamePersonalFile")
	@ResponseBody
	public DataOutputFormat renameFile(HttpServletRequest request, HttpServletResponse response) {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String fileName = request.getParameter("fileName");
		JSONObject parameters = new JSONObject();
		parameters.put("empeeAcct", user);
		parameters.put("fileId", fileId);
		parameters.put("fileName", fileName);
		DataOutputFormat result = personalFileService.renameFile(parameters,request);
		String message = result.toString();
		return result;
	}
	
	@RequestMapping("/testAction")
	@ResponseBody
	public String testAction(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("hello,world");
		String message = "this is testFilter";
		return message;
	}
}
