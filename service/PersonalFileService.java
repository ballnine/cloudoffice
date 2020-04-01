package com.chinatelecom.ctdfs.personalfile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.personalfile.mapper.PersonalFileMapper;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.ctdfs.util.StringUtil;
import com.chinatelecom.udp.core.datarouter.IWorkService;
import com.chinatelecom.udp.core.datarouter.MatchMethod;
import com.chinatelecom.udp.core.datarouter.exception.DataException;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.datarouter.utils.TypeHttpRequest;
import com.chinatelecom.udp.core.lang.json.JSONArray;
import com.chinatelecom.udp.core.lang.json.JSONObject;
import com.chinatelecom.udp.core.sharecontext.userrights.ILoginUserInfo;

@Service
@Transactional(rollbackFor = Exception.class)
public class PersonalFileService implements IWorkService{
	@Resource
	private PersonalFileMapper personalFileMapper;
	
	@Resource
    private WorkSmartCommonService commonService;
	
	private static Logger log = LogManager.getLogger(PersonalFileService.class.getName());

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
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat testPersonal(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
					resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                result.setJSON(resultJson);
	                return result;
	}
	
	/**
	 * 上传文件至个人文件夹
	 * @param parameters {token:用户信息(必填)，fileName:文件名(必填),parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat addPersonalFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			String parentId = parameters.getString("parentId");
			JSONArray webFields = parameters.getJSONArray("webFields");
			
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
		
			if (webFields.length() == 0 || webFields == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_12);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_12);
				result.setJSON(resultJson);
				return result;
			}
			if(!parentId.isEmpty()) {
				List<HashMap<String, Object>> personalFile = new ArrayList<HashMap<String, Object>>();
				for (int i = 0;i < webFields.length();i++) {
					JSONObject item = webFields.getJSONObject(i);
					HashMap<String, Object> fileInfo = (HashMap<String, Object>) item.toMap();
					String ID = UUID.randomUUID().toString().replace("-", "").toUpperCase();
					fileInfo.put("ID", ID);
					fileInfo.put("parent", parentId);
					fileInfo.put("empeeAcct", loginInfo.getEmpeeAcct());
					fileInfo.put("type", "txt");
					fileInfo.put("createTime", StringUtil.dateToStr(new Date()));
					fileInfo.put("modificationTime", StringUtil.dateToStr(new Date()));
					fileInfo.put("status", 1);
					personalFile.add(fileInfo);
				}
				int num = personalFileMapper.addPersonalFile(personalFile);
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
	 * 获取个人文件夹列表
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listPersonalFile(JSONObject parameters, TypeHttpRequest request) {
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
		
			if(!"".equals(parentId)) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("parentId", parentId);
	            paraMap.put("ownerId", loginInfo.getEmpeeAcct());
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
	 * 下载个人文件
	 * @param parameters {token:用户信息(必填)，fileId:文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat downloadFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			String fileId = parameters.getString("fileId");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!fileId.isEmpty()) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("fileId", fileId);
				HashMap<String, Object> fileInfo = personalFileMapper.getFileInfoById(paraMap);
				if (fileInfo != null) {
					//WebFileHandler.getFile(request, response, fileInfo.getPath() + fileInfo.getStoreFileName(),request.getParameter("attachment"));
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
	 * 查看文件信息
	 * @param parameters {token:用户信息(必填)，fileId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat listPersonalFileInfo(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		int flag = 0;//记录是否修改成功
		try {
			String token = parameters.getString("token");
			String fileId = parameters.getString("fileId");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if (!fileId.isEmpty()) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("fileId", fileId);
				paraMap.put("ownerId", loginInfo.getEmpeeAcct());
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
	public DataOutputFormat deletePersonalFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		int flag = 0;//记录是否修改成功
		try {
			String token = parameters.getString("token");
			String fileId = parameters.getString("fileId");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			if(!"".equals(fileId)) {
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("ownerId", loginInfo.getEmpeeAcct());
	            List<String> filelist = getSublist(fileId,1);//获取正常状态的文件子列表
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
	 * 查看回收站文件信息
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat getRecycler(JSONObject parameters, TypeHttpRequest request) {
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
            paraMap.put("ownerId", loginInfo.getEmpeeAcct());
			List<HashMap<String, Object>> fileInfo = personalFileMapper.getRecyclerFile(paraMap);
            if (fileInfo.size() >= 0) {
            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
                resultJson.put("fileInfo", fileInfo);
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
	 * 还原回收站文件
	 * @param parameters {token:用户信息(必填)， fileId:文件ID(必填),fileParent:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat restoreRecycler(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		int flag = 0;//记录是否修改成功
		try {
			String token = parameters.getString("token");
			String fileId = parameters.getString("fileId");
			String parentId = parameters.getString("parentId");
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
			
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("parentId", parentId);
            paraMap.put("status", "4");
			HashMap<String, Object> parent = personalFileMapper.getParentById(paraMap);
			
			//TODO:若父文件已删除，新建文件
			if (parent.isEmpty() && !parentId.equals("~")) {
				//TODO:新建父文件
			}
			int fileStatue = (int)parent.get("FILE_STATUS");
			while (!parentId.equals("~") && fileStatue != 1 && fileStatue != 4) {
				//父文件也被删除，修改父文件列表状态为4或5,否则直接还原文件，不用对父文件操作
				//判断父文件状态位是2，修改为4，状态位是3，修改为5
				if (fileStatue == 2) {
					paraMap.put("status", 4);
				} else if (fileStatue == 3) {
					paraMap.put("status", 5);
				}
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
				return result;
            }
			
			//子文件状态，若状态不为2，修改为1
			List<String> filelist = getSublist(fileId,2);//获取回收站状态的文件子列表
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
	 * 删除回收站文件
	 * @param parameters {token:用户信息(必填)， fileId:文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat deleteRecycler(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			JSONArray fileList = parameters.getJSONArray("fileList");
			int flag = 0;
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
			}
				
			if(fileList!=null) {
				List<String> idStrListDel = new ArrayList<String>();
				List<String> idStrListUp = new ArrayList<String>();
				for(int i=0;i<fileList.length();i++) {
					JSONObject item = fileList.getJSONObject(i);
					String idItem = item.get("fileId").toString();
					int statusItem = item.getInt("status");
					List<String> subfileDel = getSublist(idItem,3);//获取回收站可彻底删除的文件子列表
					List<String> subfileUp = getSublist(idItem,4);//获取回收站可彻底删除的文件子列表
					if (statusItem == 4 || statusItem == 5) {//处理父文件
						subfileUp.add(idItem);
					} else {
						subfileDel.add(idItem);
					}
					idStrListDel.addAll(subfileDel);
					idStrListUp.addAll(subfileUp);
				}
				
				HashMap<String, Object> paraMap = new HashMap<String, Object>();//判断删除文件状态若为4、5，状态改为1	
				for(String file : idStrListUp) {
	            	paraMap.put("fileId", file);
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
	                return result;
	            }
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
	 * @param parameters {token:用户信息(必填)，fileId:文件ID(必填)，fileName:文件新名称(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
	public DataOutputFormat renameFile(JSONObject parameters, TypeHttpRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			String token = parameters.getString("token");
			String fileId = parameters.getString("fileId");
			String fileName = parameters.getString("fileName");
			int flag = 0;
			ILoginUserInfo loginInfo=commonService.getEmpeeInfoByToken(request,token);
			if(loginInfo == null) {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_11);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_11);
				result.setJSON(resultJson);
				return result;
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
	
	private List<String> getSublist(String fileId,int state) {
		//TODO：功能放在sql语句中
		HashMap<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("fileId", fileId);
		List<String> filelist = new ArrayList<String>();
        filelist.add(fileId);
        int index = 1;
        while(index <= filelist.size()) {
        	paraMap.put("fileId", filelist.get(index-1));
        	index++;
        	List<HashMap<String, Object>> fileInfo = new ArrayList<>();
        	if (state == 1) {//获得正常状态子文件
        		fileInfo = personalFileMapper.getPersonalFileInfo(paraMap);
        	} else if (state == 2) {//获得回收站子文件
        		fileInfo = personalFileMapper.getRecycleSubFile(paraMap);
        	} else if (state == 3) {//获得可彻底删除子文件
        		fileInfo = personalFileMapper.getDeleteSubFile(paraMap);
        	}
        	for (HashMap<String, Object> file : fileInfo) {
        		filelist.add((String) file.get("FILE_ID"));
        	}
        }
        filelist.remove(0);
        return filelist;
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
