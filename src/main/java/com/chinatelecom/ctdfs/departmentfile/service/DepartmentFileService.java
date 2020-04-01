package com.chinatelecom.ctdfs.departmentfile.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.departmentfile.mapper.DepartmentFileMapper;
import com.chinatelecom.ctdfs.personalfile.mapper.PersonalFileMapper;
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
import com.chinatelecom.udp.core.sharecontext.userrights.ILoginUserInfo;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;

@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentFileService implements IWorkService{
	@Resource
	private DepartmentFileMapper departmentFileMapper;
	@Resource
    private WorkSmartCommonService commonService;
	
	private static Logger log = LogManager.getLogger(PersonalFileService.class.getName());
	private static String savePath = "C:\\test\\org\\qixin"; 

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
	public JSONObject addDepartmentFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
		    File file = new File(savePath);
		    if(!file.exists()&&!file.isDirectory()){
		        file.mkdir();
		    }
		    List<HashMap<String, Object>> personalFile = new ArrayList<HashMap<String, Object>>();
		    HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		    try {
		    	DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		    	ServletFileUpload fileUpload = new ServletFileUpload(diskFileItemFactory);
		    	fileUpload.setHeaderEncoding("UTF-8");
		    	if(!fileUpload.isMultipartContent(request)){
		    		resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_17);
					resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_17);
					//result.setJSON(resultJson);
					return resultJson;
		    	}
		    	List<FileItem> list = fileUpload.parseRequest(request);
		    	int userId = 0;
		    	String parentId = "";
		    	String content = "~";
		    	String groupId = "";
		    	String orgId = "";
		    	int groupLevel = 0;
		    	String fileName = "";
		    	for (FileItem item : list) {
		    		if(item.isFormField()){
		    			String name = item.getFieldName();
		    			String value = item.getString("UTF-8");
		    			String value1 = new String(name.getBytes("iso8859-1"),"UTF-8");
		    			if (name.equals("empeeAcct")) {
		    				HashMap<String, Object> loginInfo=commonService.getUserInfoById(Integer.parseInt(value));
		    				if(loginInfo == null) {
		    					resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
		    					resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
		    					result.setJSON(resultJson);
		    					return resultJson;
		    				}
		    				userId = Integer.parseInt(value);
		    			} else if (name.equals("parentId")) {
		    				if(value.isEmpty()) {
		    					resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
		    					resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
		    					result.setJSON(resultJson);
		    					return resultJson;
		    				}
		    				if(!value.equals("~")) {
		    					HashMap<String, Object> paraMap = new HashMap<String, Object>();
		    					paraMap.put("ownerId", userId);
		    					paraMap.put("fileId", value);
		    					HashMap<String, Object> parentFile = departmentFileMapper.getInfoById(paraMap);
		    					content = parentFile.get("FILE_PATH") + File.separator + parentFile.get("FILE_NAME");
		    					groupId = parentFile.get("GROUP_ID").toString();
		    					orgId = parentFile.get("ORG_ID").toString();
		    					BigDecimal bigDecimal = (BigDecimal) parentFile.get("GROUP_LEVEL");
		    					groupLevel = Integer.parseInt(bigDecimal.toString());
		    				}
		    				parentId = value;
		    			}
		    			fileInfo.put(name, value);
		            }else{
		            	fileName = item.getName();
		            	if(fileName==null||fileName.trim().equals("")){
		            		continue;
		            	}
		            	fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
		            	if(repeatName(fileName,parentId)) {
		            		resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
		    				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
		    				result.setJSON(resultJson);
		    				return resultJson;
		            	}
		            	String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
		            	String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
						fileInfo.put("ID", ID);
						fileInfo.put("name", fileName);
						fileInfo.put("type", fileExtName);
						fileInfo.put("createTime", StringUtil.dateToStr(new Date()));
						fileInfo.put("modificationTime", StringUtil.dateToStr(new Date()));
						fileInfo.put("status", 1);
						fileInfo.put("path", "/home/disk/public/files/"+fileName);
						fileInfo.put("size", 10);
						fileInfo.put("dfsFileName", "temp");
						fileInfo.put("class", 1);
						fileInfo.put("content",content);
						fileInfo.put("groupId", groupId);
						fileInfo.put("groupLevel", groupLevel % 2 == 0 ? groupLevel-1 : groupLevel);
						fileInfo.put("orgId", orgId);
						personalFile.add(fileInfo);
		            	InputStream is = item.getInputStream();
		            	FileOutputStream fos = new FileOutputStream(savePath+File.separator+fileName);
		            	byte buffer[] = new byte[1024];
		            	int length = 0;
		            	while((length = is.read(buffer))>0){
		            		fos.write(buffer, 0, length);
		            	}
		            	is.close();
		            	fos.close();
		            	item.delete();
		             }
		        }
		    	//TODO:文件上传和数据库修改为原子操作
		    	int num = departmentFileMapper.addOrgFile(fileInfo);
		    	//int num = personalFileMapper.addPersonalFile(personalFile);
				if (num > 0) {
					Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
					SSHUtil.putFile(conn, "C:\\test\\org\\qixin"+File.separator+fileName,"/home/disk/public/files/qixin");
					conn.close();
					resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                result.setJSON(resultJson);
	                return resultJson;
				}
				
		    } catch (FileUploadException e) {
		        e.printStackTrace();
		       	resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
				resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
				result.setJSON(resultJson);
				log.error(e.getMessage(), e);
				return resultJson;
		    }   

		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return resultJson;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return resultJson;
	}
	
	/**
	 * 获取部门文件列表
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject listDepartmentFile(JSONObject parameters, HttpServletRequest request) {
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
				return resultJson;
			}
			
			if(!parentId.isEmpty()) {//TODO:首页面显示
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("fileId", parentId);
	            
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
	            	if (hasRight(paraMap,1)) {
		            	fileInfo = departmentFileMapper.getFileByParent(paraMap);
		            }
	            }
	                     
	            if (fileInfo.size() >= 0) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                resultJson.put("fileInfo", fileInfo);
	                result.setJSON(resultJson);
	                return resultJson;
	            }
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return resultJson;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return resultJson;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return resultJson;
	}
	
	/**
	 * 下载部门文件
	 * @param parameters {token:用户信息(必填),fileId:文件ID(必填),groupId:组织父节点ID(必填),localPath:下载本地路径（必填）}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject downloadDepartmentFile(JSONObject parameters, HttpServletRequest request) {
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
				return resultJson;
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
                return resultJson;
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return resultJson;
			}
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return resultJson;
		}
	}
	
	/**
	 * 上传审核文件
	 * @param parameters {token:用户信息(必填),destId:目标文件ID}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject addAuditOrgFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
		    File file = new File(savePath);
		    if(!file.exists()&&!file.isDirectory()){
		        file.mkdir();
		    }
		    List<HashMap<String, Object>> personalFile = new ArrayList<HashMap<String, Object>>();
		    HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		    HashMap<String, Object> auditParamap = new HashMap<String, Object>();
		    try {
		    	DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		    	ServletFileUpload fileUpload = new ServletFileUpload(diskFileItemFactory);
		    	fileUpload.setHeaderEncoding("UTF-8");
		    	if(!fileUpload.isMultipartContent(request)){
		    		resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_17);
					resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_17);
					//result.setJSON(resultJson);
					return resultJson;
		    	}
		    	List<FileItem> list = fileUpload.parseRequest(request);
		    	int userId = 0;
		    	String parentId = "";
		    	String content = "~";
		    	String groupId = "";
		    	String orgId = "";
		    	int groupLevel = 0;
		    	String fileName = "";
		    	for (FileItem item : list) {
		    		if(item.isFormField()){
		    			String name = item.getFieldName();
		    			String value = item.getString("UTF-8");
		    			String value1 = new String(name.getBytes("iso8859-1"),"UTF-8");
		    			if (name.equals("empeeAcct")) {
		    				HashMap<String, Object> loginInfo=commonService.getUserInfoById(Integer.parseInt(value));
		    				auditParamap.put("applicant", value);
		    				auditParamap.put("userName", loginInfo.get("USERNAME").toString());
		    				if(loginInfo == null) {
		    					resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
		    					resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
		    					result.setJSON(resultJson);
		    					return resultJson;
		    				}
		    				userId = Integer.parseInt(value);
		    			} else if (name.equals("parentId")) {
		    				if(value.isEmpty()) {
		    					resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
		    					resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
		    					result.setJSON(resultJson);
		    					return resultJson;
		    				}
		    				if(!value.equals("~")) {
		    					HashMap<String, Object> paraMap = new HashMap<String, Object>();
		    					paraMap.put("ownerId", userId);
		    					paraMap.put("fileId", value);
		    					HashMap<String, Object> parentFile = departmentFileMapper.getInfoById(paraMap);
		    					content = parentFile.get("FILE_PATH") + File.separator + parentFile.get("FILE_NAME");
		    					groupId = parentFile.get("GROUP_ID").toString();
		    					orgId = parentFile.get("ORG_ID").toString();
		    					BigDecimal bigDecimal = (BigDecimal) parentFile.get("GROUP_LEVEL");
		    					groupLevel = Integer.parseInt(bigDecimal.toString());
		    					
		    					paraMap.put("groupId", groupId);
		    					int approver = departmentFileMapper.getReviewerByFile(paraMap);
								auditParamap.put("destPath", parentFile.get("FILE_PATH").toString());
								auditParamap.put("approver",approver);
		    				}
		    				parentId = value;
		    			}
		    			fileInfo.put(name, value);
		    			auditParamap.put(name, value);
		            }else{
		            	fileName = item.getName();
		            	if(fileName==null||fileName.trim().equals("")){
		            		continue;
		            	}
		            	fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
		            	if(repeatName(fileName,parentId)) {
		            		resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
		    				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
		    				result.setJSON(resultJson);
		    				return resultJson;
		            	}
		            	String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
		            	String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
						fileInfo.put("ID", ID);
						fileInfo.put("name", fileName);
						fileInfo.put("type", fileExtName);
						fileInfo.put("createTime", StringUtil.dateToStr(new Date()));
						fileInfo.put("modificationTime", StringUtil.dateToStr(new Date()));
						fileInfo.put("status", 6);
						fileInfo.put("path", "/home/disk/public/files/"+fileName);
						fileInfo.put("size", 10);
						fileInfo.put("dfsFileName", "temp");
						fileInfo.put("class", 1);
						fileInfo.put("content",content);
						fileInfo.put("groupId", groupId);
						fileInfo.put("groupLevel", groupLevel % 2 == 0 ? groupLevel-1 : groupLevel);
						fileInfo.put("orgId", orgId);
						personalFile.add(fileInfo);
						auditParamap.put("id", UUID.randomUUID().toString().replace("-", ""));
						auditParamap.put("fileId", ID);
						auditParamap.put("fileName", fileName);
						auditParamap.put("time",StringUtil.dateToStr(new Date()));
						auditParamap.put("status", 0);
		            	InputStream is = item.getInputStream();
		            	FileOutputStream fos = new FileOutputStream(savePath+File.separator+fileName);
		            	byte buffer[] = new byte[1024];
		            	int length = 0;
		            	while((length = is.read(buffer))>0){
		            		fos.write(buffer, 0, length);
		            	}
		            	is.close();
		            	fos.close();
		            	item.delete();
		             }
		        }
		    	//TODO:文件上传和数据库修改为原子操作
		    	int num = departmentFileMapper.addOrgFile(fileInfo);
		    	departmentFileMapper.addAuditFile(auditParamap);
				if (num > 0) {
					Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
					SSHUtil.putFile(conn, "C:\\test\\org\\qixin"+File.separator+fileName,"/home/disk/public/files");
					conn.close();
					resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                result.setJSON(resultJson);
	                return resultJson;
				}
				
		    } catch (FileUploadException e) {
		        e.printStackTrace();
		       	resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
				resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
				result.setJSON(resultJson);
				log.error(e.getMessage(), e);
				return resultJson;
		    }   

		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return resultJson;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return resultJson;
	}
	
	/**
	 * 显示被审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject listAuditedFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return resultJson;
			}
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("userId", id);
            List<HashMap<String, Object>> auditInfo = departmentFileMapper.getAuditedInfo(paraMap);
            if (auditInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("auditInfo", auditInfo);
                result.setJSON(resultJson);
                return resultJson;
            }
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return resultJson;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return resultJson;
	}
	
	/**
	 * 显示审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject listAuditFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return resultJson;
			}
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("userId", id);
            List<HashMap<String, Object>> auditInfo = departmentFileMapper.getAuditInfo(paraMap);
            if (auditInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("auditInfo", auditInfo);
                result.setJSON(resultJson);
                return resultJson;
            }
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return resultJson;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return resultJson;
	}
	
	/**
	 * 审核文件
	 * @param parameters {token:用户信息(必填),reviewId:审核项ID(必填),state:审核结果(必填) 1通过 2拒绝,fileId:被审核文件ID（必填）}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject auditFile(JSONObject parameters, HttpServletRequest request) {
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
				return resultJson;
			}
			
			if (reviewId.isEmpty() || state == 0) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_14);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_14);
				result.setJSON(resultJson);
				return resultJson;
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
                return resultJson;
            }
            
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return resultJson;
		}
		resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
		resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG);
		result.setJSON(resultJson);
		return resultJson;
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
		String fileId = paraMap.get("fileId").toString();
		fileId = departmentFileMapper.getInfoById(paraMap).get("group_id").toString();
		paraMap.put("fileId", fileId);
		int level = departmentFileMapper.getOrgAccess(paraMap);
		return level;
	}
	
	public int getStaffAccess(HashMap<String, Object> paraMap) {
		String fileId = paraMap.get("fileId").toString();
		fileId = departmentFileMapper.getInfoById(paraMap).get("group_id").toString();
		paraMap.put("fileId", fileId);
		int level = departmentFileMapper.getStaffAccess(paraMap);
		return level;
	}
	
	public int getGroupAccess(HashMap<String, Object> paraMap) {
		String fileId = paraMap.get("fileId").toString();
		fileId = departmentFileMapper.getInfoById(paraMap).get("group_id").toString();
		paraMap.put("fileId", fileId);
		int level = departmentFileMapper.getGroupAccess(paraMap);
		return level;
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
