package com.chinatelecom.ctdfs.personalFavorite.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chinatelecom.ctdfs.personalFavorite.service.PersonalFavoriteService;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.lang.json.JSONObject;

@Controller
public class PersonalFavoriteController {
	@Resource
	private PersonalFavoriteService personalFavoriteService;
	
	private static Logger log = LogManager.getLogger(PersonalFavoriteController.class.getName());

	@RequestMapping("/addFavoriteFile")
	@ResponseBody
	public DataOutputFormat addFavoriteFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String type = request.getParameter("type");
		JSONObject parameters = new JSONObject();
		parameters.put("userId", user);
		parameters.put("fileId", fileId);
		parameters.put("type", type);
		DataOutputFormat result = personalFavoriteService.addFavoriteFile(parameters,request);
		return result;
	}
	
	@RequestMapping("/listFavoriteFile")
	@ResponseBody
	public DataOutputFormat listFavoriteFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		JSONObject parameters = new JSONObject();
		parameters.put("userId", user);
		DataOutputFormat result = personalFavoriteService.listFavoriteFile(parameters,request);
		return result;
	}
	
	@RequestMapping("/cancelFavoriteFile")
	@ResponseBody
	public DataOutputFormat cancelFavoriteFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int user = Integer.parseInt(request.getParameter("id"));
		String fileId = request.getParameter("fileId");
		String fileType = request.getParameter("type");
		JSONObject parameters = new JSONObject();
		parameters.put("userId", user);
		parameters.put("fileId", fileId);
		parameters.put("fileType", fileType);
		DataOutputFormat result = personalFavoriteService.cancelFavoriteFile(parameters,request);
		return result;
	}
}
