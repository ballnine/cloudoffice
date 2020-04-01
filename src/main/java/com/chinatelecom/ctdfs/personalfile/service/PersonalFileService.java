package com.chinatelecom.ctdfs.personalfile.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.cypher.internal.compiler.v2_0.functions.Id;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.personalfile.mapper.PersonalFileMapper;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.ctdfs.util.SSHUtil;
import com.chinatelecom.ctdfs.util.StringUtil;
import com.chinatelecom.udp.core.datarouter.IWorkService;
import com.chinatelecom.udp.core.datarouter.MatchMethod;
import com.chinatelecom.udp.core.datarouter.exception.DataException;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.datarouter.utils.TypeHttpRequest;
import com.chinatelecom.udp.core.lang.json.JSONArray;
import com.chinatelecom.udp.core.lang.json.JSONObject;
import com.chinatelecom.udp.core.sharecontext.userrights.ILoginUserInfo;

import ch.ethz.ssh2.Connection;

@Service
@Transactional(rollbackFor = Exception.class)
public class PersonalFileService implements IWorkService{
	@Resource
	private PersonalFileMapper personalFileMapper;
	
	@Resource
	private WorkSmartCommonService commonService;
	
	private static Logger log = LogManager.getLogger(PersonalFileService.class.getName());
	private static String savePath = "C:\\test\\personal"; 

	@Override
	public String getCode() {
		return "PersonalFileService";
	}

	@Override
	public String getName() {
		return "个人网盘管理";
	}
	
	/**
	 * 上传文件至个人文件夹
	 * @param parameters {token:用户信息(必填)，fileName:文件名(必填),parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	public DataOutputFormat addPersonalFile(JSONObject parameters,HttpServletRequest request) {
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
			String remoteContent = "/home/disk/public/files/personal/" + userId;
			String relativeContent = "~";
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(userId);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			if(parentId.isEmpty()) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return result;
			}
			if(!parentId.equals(String.valueOf(userId))) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("ownerId", userId);
				paraMap.put("fileId", parentId);
				HashMap<String, Object> parentFile = personalFileMapper.getFileInfoById(paraMap);
				remoteContent = parentFile.get("FILE_SYS").toString();
				relativeContent = parentFile.get("FILE_PATH") + File.separator + parentFile.get("FILE_NAME");
			}
			String fileName = new String(parameters.getString("fileName").getBytes("iso8859-1"),"UTF-8");
			//TODO:文件名合法判断
			fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
			if(repeatName(fileName,parentId,userId)) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
				result.setJSON(resultJson);
				return result;
			}
			String fileExtName = fileName.substring(fileName.lastIndexOf(".")+1);
			String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
			String remotePath = remoteContent + "/" + fileName;
			String localPath = savePath+File.separator+userId+File.separator+fileName;
			if (relativeContent.length() > 2) {
				localPath = savePath+File.separator+userId+File.separator+relativeContent.substring(2)+File.separator+fileName;
			}
			File files = new File(localPath);
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
			fileInfo.put("dfsFileName", "temp");
			fileInfo.put("class", 1);
			fileInfo.put("content",relativeContent);
			fileInfo.put("empeeAcct", userId);
			fileInfo.put("parentId", parentId);
			System.out.println(fileInfo.toString());
			System.out.println("localPath " + localPath);
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
			personalFileMapper.addPersonalFile(fileInfo);
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
	 * 获取个人文件夹列表
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listPersonalFile(JSONObject parameters, HttpServletRequest request) {
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
		
			if(!parentId.isEmpty()) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("parentId", parentId);
	            paraMap.put("ownerId", id);
	            //TODO：按照类型排序
	            List<HashMap<String, Object>> fileInfo = personalFileMapper.getPersonalFileInfo(paraMap);
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
	 * 新建个人文件夹
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat createPersonalFolder(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			String parentId = parameters.getString("parentId");
			String fileName = parameters.getString("fileName");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
		
			if(!parentId.isEmpty()) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("fileId", parentId);
	            paraMap.put("ownerId", id);
	            HashMap<String, Object> fileInfo = new HashMap<String, Object>();
	            fileInfo = personalFileMapper.getFileInfoById(paraMap);
	            if (!parentId.equals("~") && fileInfo == null) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_19);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_19);
	                result.setJSON(resultJson);
	                return result;
	            }
	            //TODO:新建文件夹重名
	            if(repeatName(fileName,parentId,id)) {
            		resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_20);
    				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_20);
    				result.setJSON(resultJson);
    				return result;
            	}
	            fileInfo = new HashMap<String, Object>();
	            String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
				fileInfo.put("ID", ID);
				fileInfo.put("empeeAcct", id);
				fileInfo.put("name", fileName);
	            fileInfo.put("parentId", parentId);
				fileInfo.put("type", "");
				fileInfo.put("createTime", StringUtil.dateToStr(new Date()));
				fileInfo.put("modificationTime", StringUtil.dateToStr(new Date()));
				fileInfo.put("status", 1);
				fileInfo.put("path", "c:\\test");
				fileInfo.put("sizes", 0);
				fileInfo.put("dfsFileName", "temp");
				fileInfo.put("class", 2);
				fileInfo.put("content", "/");
				int num = personalFileMapper.addPersonalFile(fileInfo);
	            if (num > 0) {
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
	 * 下载个人文件
	 * @param parameters {token:用户信息(必填)，fileId:文件ID(必填),path:本机地址}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat downloadFile(JSONObject parameters, HttpServletRequest request, HttpServletResponse response) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
			String fileId = parameters.getString("fileId");
			String path = parameters.getString("path");
			path = new String(path.getBytes("iso8859-1"),"UTF-8");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!fileId.isEmpty()) {
				/*HashMap<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("fileId", fileId);
				paraMap.put("ownerId", id);
				HashMap<String, Object> fileInfo = personalFileMapper.getFileInfoById(paraMap);
				if (fileInfo != null) {
					String realPath = fileInfo.get("FILE_SYS").toString();
					String realName = fileInfo.get("FILE_NAME").toString();
					realPath = realPath + File.separator + realName;
					System.out.println(realPath);
					File file = new File(realPath);		
					if (!file.exists()) {
						resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_18);
						resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_18);
						result.setJSON(resultJson);
						return resultJson;
					}
					InputStream inStream = new FileInputStream(realPath);
					response.reset();
			        //response.setContentType("bin");
			        response.addHeader("Content-Disposition", "attachment; filename=\"" + realName + "\"");
			        byte[] b = new byte[100];
			        int len;
			        try {
			            while ((len = inStream.read(b)) > 0)
			                response.getOutputStream().write(b, 0, len);
			            inStream.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
					//WebFileHandler.getFile(request, response, fileInfo.getPath() + fileInfo.getStoreFileName(),request.getParameter("attachment"));
				}*/
				
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("fileId", fileId);
				paraMap.put("ownerId", id);
				HashMap<String, Object> fileInfo = personalFileMapper.getFileInfoById(paraMap);
				Connection conn = SSHUtil.getSSHConnection("122.51.38.46",22,"root","Hudiewang$0","C:\\study\\rsy");
				System.out.println("localPath is " + path);
				System.out.println("remotePath is " + fileInfo.get("FILE_SYS"));
				SSHUtil.getFile(conn, fileInfo.get("FILE_SYS").toString(),path);
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
	 * 查看文件信息
	 * @param parameters {token:用户信息(必填)，fileId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listPersonalFileInfo(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		int flag = 0;//记录是否修改成功
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
			
			if (!fileId.isEmpty()) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("fileId", fileId);
				paraMap.put("ownerId", id);
				HashMap<String, Object> fileInfo = personalFileMapper.getFileInfoById(paraMap);
				if (fileInfo != null) {
					resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                resultJson.put("fileInfo", fileInfo);
	                result.setJSON(resultJson);
	                return result;
				} else {
					resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_16);
					resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_16);
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
	}
	
	/**
	 * 删除个人文件夹文件到回收站，文件状态改为2回收站
	 * @param parameters {token:用户信息(必填)，fileId:文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject deletePersonalFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		int flag = 0;//记录是否修改成功
		try {
			int id = parameters.getInt("empeeAcct");
			String fileId = parameters.getString("fileId");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return resultJson;
			}
			
			if(!fileId.isEmpty()) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("ownerId", id);
	            List<String> filelist = getSublist(fileId,1,id);//获取正常状态的文件子列表
	            int count;
	            paraMap.put("status", "3");//子文件状态位回收隐藏
	            for(String file : filelist) {
	            	paraMap.put("fileId", file);
	            	count = personalFileMapper.updateFileStatus(paraMap);
	            	if (count <= 0) {
	            		flag = 1;
	            		break;
	            	}
	            }
	            paraMap.put("fileId", fileId);
	            paraMap.put("status", "2");//删除文件状态为2
	            count = personalFileMapper.updateFileStatus(paraMap);
	            if (count <= 0) {
	            	flag = 1;
	            }
	            if (flag == 0) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
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
	 * 查看回收站文件信息
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject getRecycler(JSONObject parameters, HttpServletRequest request) {
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
            paraMap.put("ownerId", id);
			List<HashMap<String, Object>> fileInfo = personalFileMapper.getRecyclerFile(paraMap);
            if (fileInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("fileInfo", fileInfo);
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
	 * 删除回收站文件
	 * @param parameters {token:用户信息(必填)， fileId:文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject deleteRecycler(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
		    String fileId = parameters.getString("fileId");
		    int statusItem = parameters.getInt("status");
			int flag = 0;
			//JSONArray fileList = parameters.getJSONArray("fileList");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return resultJson;
			}		
				
			if(fileId!=null) {
				List<String> idStrListDel = new ArrayList<String>();
				List<String> idStrListUp = new ArrayList<String>();
				List<String> subfileDel = getSublist(fileId,3,id);//获取回收站可彻底删除的文件子列表
				List<String> subfileUp = getSublist(fileId,4,id);//获取回收站状态变为1的文件子列表
				if (statusItem == 4 || statusItem == 5) {//处理父文件
					subfileUp.add(fileId);
				} else {
					subfileDel.add(fileId);
				}
				idStrListDel.addAll(subfileDel);
				idStrListUp.addAll(subfileUp);

				
				HashMap<String, Object> paraMap = new HashMap<String, Object>();//判断删除文件状态若为4、5，状态改为1	
				for(String file : idStrListUp) {
	            	paraMap.put("fileId", file);
	            	paraMap.put("status", 1);
	            	int count = personalFileMapper.updateFileStatus(paraMap);
	            	if (count <= 0) {
	            		flag = 1;
	            		break;
	            	}
	            }
				personalFileMapper.deleteFile(idStrListDel);//彻底删除子文件
				if (flag == 0) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                result.setJSON(resultJson);
	                return resultJson;
	            }
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
	 * 还原回收站文件
	 * @param parameters {token:用户信息(必填)， fileId:文件ID(必填),fileParent:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject restoreRecycler(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		int flag = 0;//记录是否修改成功
		try {
			int id = parameters.getInt("empeeAcct");
		    String fileId = parameters.getString("fileId");
		    String parentId = parameters.getString("parentId");
			HashMap<String, Object> loginInfo=commonService.getUserInfoById(id);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return resultJson;
			}		
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("parentId", parentId);
            paraMap.put("status", "4");
            HashMap<String, Object> parent = new HashMap<String, Object>();
			parent = personalFileMapper.getParentById(paraMap);
			
			//TODO:若原文件有同名，直接合并
			//TODO:若父文件已删除，新建文件
			if (!parentId.equals("~") && parent == null) {
				//TODO:新建父文件
			}
			int fileStatue = 0;
			if (!parentId.equals("~")) {
				fileStatue = (int)parent.get("FILE_STATUS");
			}
			while (!parentId.equals("~") && fileStatue != 1 && fileStatue != 4) {
				//父文件也被删除，修改父文件列表状态为4或5,否则直接还原文件，不用对父文件操作
				//判断父文件状态位是2，修改为4，状态位是3，修改为5
				if (fileStatue == 2) {
					paraMap.put("status", 4);
				} else if (fileStatue == 3) {
					paraMap.put("status", 5);
				}
				paraMap.put("fileId", parentId);
				int count = personalFileMapper.updateFileStatus(paraMap);
				if (count <= 0) {
					flag = 1;
					break;
				}
				parentId = parent.get("FILE_PARENT").toString();
				paraMap.put("parentId", parentId);
				parent = personalFileMapper.getParentById(paraMap);
				if (!parentId.equals("~")) {
					fileStatue = (int)parent.get("FILE_STATUS");
				}
			}
			if (flag != 0) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_13);
				resultJson.put(RetCode.RESULT_VALUE, RetCode.ERROR_DESC_13);
				result.setJSON(resultJson);
				return resultJson;
            }
			
			//子文件状态，若状态不为2，修改为1
			List<String> filelist = getSublist(fileId,2,id);//获取回收站状态的文件子列表
			filelist.add(fileId);
			paraMap.put("status", "1");//子文件状态为正常
            for(String file : filelist) {
            	paraMap.put("fileId", file);
            	int count = personalFileMapper.updateFileStatus(paraMap);
            	if (count <= 0) {
            		flag = 1;
            		break;
            	}
            }
            if (flag == 0) {
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
	 * 文件重命名
	 * @param parameters {token:用户信息(必填)，fileId:文件ID(必填)，fileName:文件新名称(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public JSONObject renameFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("empeeAcct");
		    String fileId = parameters.getString("fileId");
		    String fileName = parameters.getString("fileName");
		    int flag = 0;
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
	            paraMap.put("fileName", fileName);
	            int count = personalFileMapper.updateFileName(paraMap);
	            if (count > 0) {
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
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
	
	private boolean repeatName(String fileName, String parentId, int userId) {
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		paraMap.put("fileName", fileName);
		paraMap.put("parentId", parentId);
		paraMap.put("userId", userId);
		fileInfo = personalFileMapper.getFileByName(paraMap);
		if (fileInfo != null) {
			return true;
		}
		return false;
	}
	
	private List<String> getSublist(String fileId,int state,int id) {
		//TODO：功能放在sql语句中
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("fileId", fileId);
		List<String> filelist = new ArrayList<String>();
        filelist.add(fileId);
        int index = 1;
        while(index <= filelist.size()) {
        	paraMap.put("fileId", filelist.get(index-1));
        	paraMap.put("parentId", filelist.get(index-1));
        	paraMap.put("ownerId", id);
        	index++;
        	List<HashMap<String, Object>> fileInfo = new ArrayList<>();
        	if (state == 1) {//获得正常状态子文件    		
        		fileInfo = personalFileMapper.getPersonalFileInfo(paraMap);
        	} else if (state == 2) {//获得回收站子文件
        		fileInfo = personalFileMapper.getRecycleSubFile(paraMap);
        	} else if (state == 3) {//获得可彻底删除子文件
        		fileInfo = personalFileMapper.getDeleteSubFile(paraMap);
        	} else if (state == 4) {
        		fileInfo = personalFileMapper.getRecycleUpFile(paraMap);
        	}
        	for (HashMap<String, Object> file : fileInfo) {
        		filelist.add((String) file.get("FILE_ID"));
        	}
        }
        filelist.remove(0);
        return filelist;
	}
	
	public Boolean getAccess(HashMap<String, Object> paraMap) {
		HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		fileInfo = personalFileMapper.getAccess(paraMap);
		if (fileInfo == null) {
			return false;
		}
		return true;
	}
	
	public HashMap<String, Object> getFileInfoById(String fileId,int user) {
		HashMap<String, Object> fileInfo = new HashMap<String, Object>();
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("fileId", fileId);
		paraMap.put("ownerId", user);
		fileInfo = personalFileMapper.getFileInfoById(paraMap);
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
