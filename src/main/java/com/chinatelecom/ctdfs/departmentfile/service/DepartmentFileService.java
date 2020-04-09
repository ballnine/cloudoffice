package com.chinatelecom.ctdfs.departmentfile.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.departmentfile.mapper.DepartmentFileMapper;
import com.chinatelecom.ctdfs.personalfile.service.PersonalFileService;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.ctdfs.util.SSHUtil;
import com.chinatelecom.ctdfs.util.StringUtil;
import com.chinatelecom.udp.core.datarouter.IWorkService;
import com.chinatelecom.udp.core.datarouter.MatchMethod;
import com.chinatelecom.udp.core.datarouter.exception.DataException;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.datarouter.utils.TypeHttpRequest;
import com.chinatelecom.udp.core.lang.json.JSONObject;

import ch.ethz.ssh2.Connection;

@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentFileService implements IWorkService{
	@Resource
	private DepartmentFileMapper departmentFileMapper;
	@Resource
    private WorkSmartCommonService commonService;
	
	private static Logger log = LogManager.getLogger(PersonalFileService.class.getName());
	private static String savePath = "C:\\test\\org"; 

	@Override
	public String getCode() {
		return "DepartmentFileService";
	}

	@Override
	public String getName() {
		return "部门网盘管理";
	}
	
	/**
	 * 上传部门文件列表
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填),groupId:所属组织父文件ID(权限判断)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat addDepartmentFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
		    List<HashMap<String, Object>> personalFile = new ArrayList<HashMap<String, Object>>();
		    HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request; 
			if((CommonsMultipartFile)multipartRequest.getFile("file") == null){
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_17);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_17);
				result.setJSON(resultJson);
				return result;
			}
			CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("file"); 
			int userId = parameters.getInt("userId");
			String parentId = parameters.getString("parentId");
			String groupId = parameters.getString("groupId");
			String remoteContent = "/home/disk/public/files/org/";
			String relativeContent = "~";
			String content = "";
			String orgId = "";
			int groupLevel = 0;
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("ownerId", userId);
			paraMap.put("fileId", parentId);
			HashMap<String, Object> parentFile = departmentFileMapper.getInfoById(paraMap);
			groupId = parentFile.get("GROUP_ID").toString();
			orgId = parentFile.get("ORG_ID").toString();
			BigDecimal bigDecimal = (BigDecimal) parentFile.get("GROUP_LEVEL");
			groupLevel = Integer.parseInt(bigDecimal.toString());
			remoteContent = parentFile.get("FILE_SYS").toString();
			relativeContent = parentFile.get("FILE_PATH") + File.separator + parentFile.get("FILE_DFSNAME");
			String fileName = new String(parameters.getString("fileName").getBytes("iso8859-1"),"UTF-8");
			//TODO:文件名合法判断
			fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
			if(repeatName(fileName,parentId)) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
				result.setJSON(resultJson);
				return result;
			}
			
			String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
        	String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        	String remotePath = remoteContent + "/" + fileName;
        	String localPath = savePath+File.separator+relativeContent.substring(2)+File.separator+fileName;
    		File files = new File(savePath+File.separator+relativeContent.substring(2));
    		if(!files.exists()&&!files.isDirectory()){
    		   files.mkdir();
    		}
			fileInfo.put("ID", ID);
			fileInfo.put("name", fileName);
			fileInfo.put("type", fileExtName);
			fileInfo.put("createTime", StringUtil.dateToStr(new Date()));
			fileInfo.put("modificationTime", StringUtil.dateToStr(new Date()));
			fileInfo.put("status", 1);
			fileInfo.put("path", remotePath);
			fileInfo.put("sizes", file.getSize());
			fileInfo.put("dfsFileName", fileName);
			fileInfo.put("class", 1);
			fileInfo.put("content",relativeContent);
			fileInfo.put("groupId", groupId);
			fileInfo.put("groupLevel", groupLevel % 2 == 0 ? groupLevel-1 : groupLevel);
			fileInfo.put("orgId", orgId);
			fileInfo.put("empeeAcct", userId);
			fileInfo.put("parentId", parentId);
			InputStream is = file.getInputStream();
			FileOutputStream fos = new FileOutputStream(localPath);
			byte buffer[] = new byte[1024];
			int length = 0;
			while((length = is.read(buffer))>0){
				fos.write(buffer, 0, length);
			}
			is.close();
			fos.close();
			int num = 1;
			departmentFileMapper.addOrgFile(fileInfo);
			if (num > 0) {
				Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
				SSHUtil.putFile(conn, localPath,remoteContent);
				conn.close();
				resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
			    result.setJSON(resultJson);
			    return result;
			}   
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 新建部门文件夹
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填),groupId:所属组织父文件ID(权限判断)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat addDepartmentFolder(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
		    HashMap<String, Object> fileInfo = new HashMap<String, Object>();
			int userId = parameters.getInt("userId");
			String parentId = parameters.getString("parentId");
			String groupId = parameters.getString("groupId");
			String remoteContent = "/home/disk/public/files/org/";
			String relativeContent = "~";
			String orgId = "";
			int groupLevel = 0;
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("ownerId", userId);
			paraMap.put("fileId", parentId);
			HashMap<String, Object> parentFile = departmentFileMapper.getInfoById(paraMap);
			groupId = parentFile.get("GROUP_ID").toString();
			orgId = parentFile.get("ORG_ID").toString();
			BigDecimal bigDecimal = (BigDecimal) parentFile.get("GROUP_LEVEL");
			groupLevel = Integer.parseInt(bigDecimal.toString());
			remoteContent = parentFile.get("FILE_SYS").toString();
			relativeContent = parentFile.get("FILE_PATH") + File.separator + parentFile.get("FILE_DFSNAME");
			String fileName = new String(parameters.getString("fileName").getBytes("iso8859-1"),"UTF-8");
			String dfsName = StringUtil.dayToStr(new Date()) + fileName;
			//TODO:文件名合法判断
			fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
			if(repeatName(fileName,parentId)) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
				result.setJSON(resultJson);
				return result;
			}
			
        	String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        	String remotePath = remoteContent + "/" + dfsName;
        	String localPath = savePath+File.separator+relativeContent.substring(2)+File.separator+dfsName;
    		File files = new File(localPath);
    		if(!files.exists()&&!files.isDirectory()){
    		   files.mkdir();
    		}
			fileInfo.put("ID", ID);
			fileInfo.put("name", fileName);
			fileInfo.put("type", "");
			fileInfo.put("createTime", StringUtil.dateToStr(new Date()));
			fileInfo.put("modificationTime", StringUtil.dateToStr(new Date()));
			fileInfo.put("status", 1);
			fileInfo.put("path", remotePath);
			fileInfo.put("sizes", 0);
			fileInfo.put("dfsFileName", dfsName);
			fileInfo.put("class", 2);
			fileInfo.put("content",relativeContent);
			fileInfo.put("groupId", groupId);
			fileInfo.put("groupLevel", groupLevel % 2 == 0 ? groupLevel-1 : groupLevel);
			fileInfo.put("orgId", orgId);
			fileInfo.put("empeeAcct", userId);
			fileInfo.put("parentId", parentId);
			int num = 1;
			departmentFileMapper.addOrgFile(fileInfo);
			if (num > 0) {
				Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
				SSHUtil.putDir(conn, remotePath);
				conn.close();
				resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
			    result.setJSON(resultJson);
			    return result;
			}   
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 获取部门文件列表
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listDepartmentFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			String parentId = parameters.getString("parentId");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!parentId.isEmpty()) {//TODO:首页面显示
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("fileId", parentId);
	            paraMap.put("userId", id);
	            
	            List<HashMap<String, Object>> fileInfo = new ArrayList<HashMap<String, Object>>();
	            HashMap<String,Object> parent = departmentFileMapper.getInfoById(paraMap);
	            BigDecimal bigDecimal = (BigDecimal) parent.get("GROUP_LEVEL");
				int groupLevel = Integer.parseInt(bigDecimal.toString());
	            String groupId = departmentFileMapper.getInfoById(paraMap).get("GROUP_ID").toString();
	            paraMap.put("groupId", groupId);
	            if (groupLevel == 4) {
	            	//fileInfo = departmentFileMapper.getRightFile(paraMap);//查询父文件为父ID的所有文件中具有权限的文件列表
	            } else {
	            	//返回有阅读权限文件列表，没有则返回空列表
		            fileInfo = departmentFileMapper.getFileByParent(paraMap);
	            }
	                     
	            if (fileInfo.size() >= 0) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                resultJson.put("fileInfo", fileInfo);
	                result.setJSON(resultJson);
	                return result;
	            }
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return result;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 下载部门文件
	 * @param parameters {token:用户信息(必填),fileId:文件ID(必填),groupId:组织父节点ID(必填),localPath:下载本地路径（必填）}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat downloadDepartmentFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			String fileId = parameters.getString("fileId");
			String localPath = parameters.getString("localPath");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!fileId.isEmpty()) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("fileId", fileId);
				HashMap<String, Object> fileInfo = departmentFileMapper.getInfoById(paraMap);
				Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
				SSHUtil.getFile(conn, fileInfo.get("FILE_SYS").toString(),localPath);
				conn.close();
				
				resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                result.setJSON(resultJson);
                return result;
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return result;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
	}
	
	/**
	 * 获取部门文件列表详细信息
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listOrgFileInfo(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			String fileId = parameters.getString("fileId");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!fileId.isEmpty()) {//TODO:首页面显示
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("fileId", fileId);
	            HashMap<String, Object> fileInfo = departmentFileMapper.getInfoById(paraMap);              
	            if (fileInfo.size() >= 0) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                resultJson.put("fileInfo", fileInfo);
	                result.setJSON(resultJson);
	                return result;
	            }
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return result;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 文件重命名
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat renameFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			String fileId = parameters.getString("fileId");
			String fileName = new String(parameters.getString("fileName").getBytes("iso8859-1"),"UTF-8");
			int flag = 0;
			String fileSys="";
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!fileId.isEmpty()) {//TODO:首页面显示
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("fileId", fileId);
	            paraMap.put("ownerId", id);
	            HashMap<String, Object> fileInfo = departmentFileMapper.getInfoById(paraMap);
	            if (repeatName(fileName,fileInfo.get("FILE_PARENT").toString())) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
					resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
					result.setJSON(resultJson);
					return result;
	            }
	            
	            paraMap.put("fileName", fileName);
	            paraMap.put("updateTime", StringUtil.dateToStr(new Date()));
	            if (fileInfo.get("FILE_TYPE") != null) {
	            	flag = 1;
	            	fileSys = fileInfo.get("FILE_SYS").toString();
	            	fileSys = fileSys.substring(0,fileSys.lastIndexOf("/")+1) + fileName;
	            	paraMap.put("fileSys", fileSys);
	            	paraMap.put("type", fileName.substring(fileName.lastIndexOf(".")+1));
	            	paraMap.put("dfsName", fileName);
	            }
	            System.out.println("file1 " + fileInfo.get("FILE_SYS").toString());
	            System.out.println("file2 " + fileSys);
	            int count = departmentFileMapper.updateFileName(paraMap);
	            if (count > 0) {
	            	if (flag == 1) {
	            		Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
						SSHUtil.renameFile(conn, fileInfo.get("FILE_SYS").toString(), fileSys);
						conn.close();
	            	}
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                result.setJSON(resultJson);
	                return result;
	            }
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return result;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 上传审核文件
	 * @param parameters {token:用户信息(必填),destId:目标文件ID}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat addAuditOrgFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
		    HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request; 
			if((CommonsMultipartFile)multipartRequest.getFile("file") == null){
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_17);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_17);
				result.setJSON(resultJson);
				return result;
			}
			CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("file"); 
			int userId = parameters.getInt("userId");
			String parentId = parameters.getString("parentId");
			String groupId = parameters.getString("groupId");
			String remoteContent = "/home/disk/public/files/org/";
			String relativeContent = "~";
			String orgId = "";
			int groupLevel = 0;
		    
		    HashMap<String, Object> auditParamap = new HashMap<String, Object>();
		    HashMap<String, Object> loginInfo=commonService.getUserInfoById(Integer.parseInt(parameters.get("userId").toString()));
			auditParamap.put("empeeAcct", parameters.get("userId"));
			auditParamap.put("userName", loginInfo.get("USERNAME").toString());
			HashMap<String, Object> paras = new HashMap<String, Object>();
			paras.put("ownerId", userId);
			paras.put("fileId", parentId);
			HashMap<String, Object> parentFile = departmentFileMapper.getInfoById(paras);
			groupId = parentFile.get("GROUP_ID").toString();
			orgId = parentFile.get("ORG_ID").toString();
			BigDecimal bigDecimal = (BigDecimal) parentFile.get("GROUP_LEVEL");
			groupLevel = Integer.parseInt(bigDecimal.toString());
			paras.put("groupId", groupId);
			int approver = departmentFileMapper.getReviewerByFile(paras);
			auditParamap.put("approver",approver);	
			auditParamap.put("parentId", parentId);
			auditParamap.put("groupId", groupId);
			remoteContent = parentFile.get("FILE_SYS").toString();
			relativeContent = parentFile.get("FILE_PATH") + File.separator + parentFile.get("FILE_DFSNAME");
			String fileName = new String(parameters.getString("fileName").getBytes("iso8859-1"),"UTF-8");
			//TODO:文件名合法判断
			fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
			if(repeatName(fileName,parentId)) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
				result.setJSON(resultJson);
				return result;
			}
			
			String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
        	String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        	String remotePath = remoteContent + "/" + fileName;
        	String localPath = savePath+File.separator+relativeContent.substring(2)+File.separator+fileName;
    		File files = new File(savePath+File.separator+relativeContent.substring(2));
    		if(!files.exists()&&!files.isDirectory()){
    		   files.mkdir();
    		}
			fileInfo.put("ID", ID);
			fileInfo.put("name", fileName);
			fileInfo.put("type", fileExtName);
			fileInfo.put("createTime", StringUtil.dateToStr(new Date()));
			fileInfo.put("modificationTime", StringUtil.dateToStr(new Date()));
			fileInfo.put("status", 6);
			fileInfo.put("path", remotePath);
			fileInfo.put("sizes", file.getSize());
			fileInfo.put("dfsFileName", fileName);
			fileInfo.put("class", 1);
			fileInfo.put("content",relativeContent);
			fileInfo.put("groupId", groupId);
			fileInfo.put("groupLevel", groupLevel % 2 == 0 ? groupLevel-1 : groupLevel);
			fileInfo.put("orgId", orgId);
			fileInfo.put("empeeAcct", userId);
			fileInfo.put("parentId", parentId);
			auditParamap.put("id", UUID.randomUUID().toString().replace("-", ""));
			auditParamap.put("fileId", ID);
			auditParamap.put("fileName", fileName);
			auditParamap.put("time",StringUtil.dateToStr(new Date()));
			auditParamap.put("status", 0);
			auditParamap.put("destPath", relativeContent);
			InputStream is = file.getInputStream();
			FileOutputStream fos = new FileOutputStream(localPath);
			byte buffer[] = new byte[1024];
			int length = 0;
			while((length = is.read(buffer))>0){
				fos.write(buffer, 0, length);
			}
			is.close();
			fos.close();
		    //TODO:文件上传和数据库修改为原子操作
		   	int num = departmentFileMapper.addOrgFile(fileInfo);
		   	departmentFileMapper.addAuditFile(auditParamap);
			if (num > 0) {
				Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
				SSHUtil.putFile(conn, localPath,remoteContent);
				conn.close();
				resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    		resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	            result.setJSON(resultJson);
	            return result;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 显示被审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listAuditedFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("userId", id);
            List<HashMap<String, Object>> auditInfo = departmentFileMapper.getAuditedInfo(paraMap);
            if (auditInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("auditInfo", auditInfo);
                result.setJSON(resultJson);
                return result;
            }
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 显示审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listAuditFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("userId", id);
            List<HashMap<String, Object>> auditInfo = departmentFileMapper.getAuditInfo(paraMap);
            if (auditInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("auditInfo", auditInfo);
                result.setJSON(resultJson);
                return result;
            }
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 审核文件
	 * @param parameters {token:用户信息(必填),reviewId:审核项ID(必填),state:审核结果(必填) 1通过 2拒绝,fileId:被审核文件ID（必填）}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat auditFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			String reviewId = parameters.getString("reviewId");
			int state = parameters.getInt("state");
			String fileId = parameters.getString("fileId");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if (reviewId.isEmpty() || state == 0) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_14);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_14);
				result.setJSON(resultJson);
				return result;
			}
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("reviewId", reviewId);
            paraMap.put("state", state);
            paraMap.put("fileId", fileId);
            int count = departmentFileMapper.updateAuditState(paraMap);
            
            if (state == 1) {  	
            	paraMap.put("state", 1);
            	count = departmentFileMapper.updateState(paraMap);
            } else {
            	HashMap<String, Object> fileInfo = departmentFileMapper.getInfoById(paraMap);
            	departmentFileMapper.deleteFile(paraMap);
            	Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
            	SSHUtil.deleteFile(conn,fileInfo.get("FILE_SYS").toString());
				conn.close();
            }
            if (count > 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                result.setJSON(resultJson);
                return result;
            }
            
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}
	
	/**
	 * 获取部门文件列表
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	/*@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listDepartmentFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			String parentId = parameters.getString("parentId");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!"".equals(parentId)) {//TODO:首页面显示
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("fileId", parentId);
	            
	            List<HashMap<String, Object>> fileInfo = new ArrayList<HashMap<String, Object>>();
	            HashMap<String,Object> parent = departmentFileMapper.getInfoById(paraMap);
	            int groupLevel = (int)parent.get("GROUP_LEVEL");
	            String groupId = departmentFileMapper.getInfoById(paraMap).get("GROUP_ID").toString();
	            paraMap.put("groupId", groupId);
	            if (groupLevel == 4) {
	            	//fileInfo = departmentFileMapper.getRightFile(paraMap);//查询父文件为父ID的所有文件中具有权限的文件列表
	            } else {
	            	//返回有阅读权限文件列表，没有则返回空列表
	            	if (hasRight(paraMap,1)) {
		            	fileInfo = departmentFileMapper.getFileByParent(paraMap);
		            }
	            }
	                     
	            if (fileInfo.size() >= 0) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                resultJson.put("fileInfo", fileInfo);
	                result.setJSON(resultJson);
	                return result;
	            }
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return result;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}*/
	
	/**
	 * 显示被审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	/*@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listAuditedFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			} 
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("userId", loginInfo.getEmpeeAcct());
            List<HashMap<String, Object>> auditInfo = departmentFileMapper.getAuditedInfo(paraMap);
            if (auditInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("auditInfo", auditInfo);
                result.setJSON(resultJson);
                return result;
            }
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}*/
	
	/**
	 * 显示审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	/*@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listAuditFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			} 
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("userId", loginInfo.getEmpeeAcct());
            List<HashMap<String, Object>> auditInfo = departmentFileMapper.getAuditInfo(paraMap);
            if (auditInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("auditInfo", auditInfo);
                result.setJSON(resultJson);
                return result;
            }
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}*/
	
	/**
	 * 审核文件
	 * @param parameters {token:用户信息(必填),reviewId:审核项ID(必填),state:审核结果(必填)}
	 * @param request 
	 * @return
	 */
	/*@MatchMethod(matchPostfix = "login")
	public DataOutputFormat auditFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			String reviewId = parameters.getString("reviewId");
			String state = parameters.getString("state");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			} 	
			if (reviewId.isEmpty() || state.isEmpty()) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_14);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_14);
				result.setJSON(resultJson);
				return result;
			}
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("reviewId", reviewId);
            paraMap.put("state", state);
            int count = departmentFileMapper.updateState(paraMap);
            if (count > 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                result.setJSON(resultJson);
                return result;
            }
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return result;
	}*/
	
	private boolean hasRight(HashMap<String, Object> paraMap,int level) {
		//查询用户所属用户组，根据用户、用户组并文件关联表查询是否有groupId文件阅读权限
		//if (PersonalFileMappper.hasRight(paraMap).size > 0)
		return true;
	}
	
	private boolean repeatName(String fileName, String parentId) {
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		paraMap.put("fileName", fileName);
		paraMap.put("parentId", parentId);
		fileInfo = departmentFileMapper.getFileByName(paraMap);
		if (fileInfo != null) {
			return true;
		}
		return false;
	}
	
	public int getOrgAccess(HashMap<String, Object> paraMap) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result = departmentFileMapper.getOrgAccess(paraMap);
		int level = -1;
		if (result != null) {
			level = Integer.parseInt(result.get("PERMISSION_LEVEL").toString());
		}
		return level;
	}
	
	public int getStaffAccess(HashMap<String, Object> paraMap) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result = departmentFileMapper.getStaffAccess(paraMap);
		int level = -1;
		if (result != null) {
			level = Integer.parseInt(result.get("PERMISSION_LEVEL").toString());
		}
		return level;
	}
	
	public int getGroupAccess(HashMap<String, Object> paraMap) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result = departmentFileMapper.getGroupAccess(paraMap);
		int level = -1;
		if (result != null) {
			level = Integer.parseInt(result.get("PERMISSION_LEVEL").toString());
		}
		return level;
	}
	
	public HashMap<String, Object> getFileInfoById(String fileId,int user) {
		HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("fileId", fileId);
		paraMap.put("ownerId", user);
		fileInfo = departmentFileMapper.getInfoById(paraMap);
		return fileInfo;
	}

	@Override
	public DataOutputFormat list(JSONObject parameters, TypeHttpRequest request) throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataOutputFormat add(JSONObject parameters, TypeHttpRequest request) throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataOutputFormat update(JSONObject parameters, TypeHttpRequest request) throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataOutputFormat delete(JSONObject parameters, TypeHttpRequest request) throws DataException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataOutputFormat get(JSONObject parameters, TypeHttpRequest request) throws DataException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
