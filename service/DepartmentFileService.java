package com.chinatelecom.ctdfs.departmentfile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.departmentfile.mapper.DepartmentFileMapper;
import com.chinatelecom.ctdfs.personalfile.mapper.PersonalFileMapper;
import com.chinatelecom.ctdfs.personalfile.service.PersonalFileService;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.udp.core.datarouter.IWorkService;
import com.chinatelecom.udp.core.datarouter.MatchMethod;
import com.chinatelecom.udp.core.datarouter.exception.DataException;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.datarouter.utils.TypeHttpRequest;
import com.chinatelecom.udp.core.lang.json.JSONObject;
import com.chinatelecom.udp.core.sharecontext.userrights.ILoginUserInfo;

@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentFileService implements IWorkService{
	@Resource
	private DepartmentFileMapper departmentFileMapper;
	
	@Resource
    private WorkSmartCommonService commonService;
	
	private static Logger log = LogManager.getLogger(PersonalFileService.class.getName());

	@Override
	public String getCode() {
		return "DepartmentFileService";
	}

	@Override
	public String getName() {
		return "部门网盘管理";
	}
	
	/**
	 * 获取部门文件列表
	 * @param parameters {token:用户信息(必填)，parentId:父文件ID(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
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
	}
	
	/**
	 * 显示被审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
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
	}
	
	/**
	 * 显示审核文件列表
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
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
	}
	
	/**
	 * 审核文件
	 * @param parameters {token:用户信息(必填),reviewId:审核项ID(必填),state:审核结果(必填)}
	 * @param request 
	 * @return
	 */
	@MatchMethod(matchPostfix = "login")
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
	}
	
	private boolean hasRight(HashMap<String, Object> paraMap,int level) {
		//查询用户所属用户组，根据用户、用户组并文件关联表查询是否有groupId文件阅读权限
		//if (PersonalFileMappper.hasRight(paraMap).size > 0)
		return true;
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
