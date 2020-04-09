package com.chinatelecom.ctdfs.personalFavorite.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinatelecom.ctdfs.common.service.WorkSmartCommonService;
import com.chinatelecom.ctdfs.departmentfile.mapper.DepartmentFileMapper;
import com.chinatelecom.ctdfs.personalFavorite.mapper.PersonalFavoriteMapper;
import com.chinatelecom.ctdfs.personalfile.service.PersonalFileService;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.ctdfs.util.StringUtil;
import com.chinatelecom.udp.core.datarouter.IWorkService;
import com.chinatelecom.udp.core.datarouter.exception.DataException;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.datarouter.utils.TypeHttpRequest;
import com.chinatelecom.udp.core.lang.json.JSONObject;

@Service
@Transactional(rollbackFor = Exception.class)
public class PersonalFavoriteService implements IWorkService{
	@Resource
	private PersonalFavoriteMapper personalFavoriteMapper;
	@Resource
    private WorkSmartCommonService commonService;
	
	private static Logger log = LogManager.getLogger(PersonalFileService.class.getName());

	@Override
	public String getCode() {
		return "CloudFileService";
	}

	@Override
	public String getName() {
		return "网盘通用功能";
	}

	/**
	 * 收藏文件
	 * @param parameters {token:用户信息(必填)，fileId:文件ID(必填),type:文件种类（必填）}
	 * @param request 
	 * @return
	 */
	public DataOutputFormat addFavoriteFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("userId");
			String fileId = parameters.getString("fileId");
			String type = parameters.getString("type");
			
			if(!type.isEmpty()) {//TODO:文件已收藏返回提示（可前端直接判断）
				int fileType = Integer.parseInt(type);
				HashMap<String, Object> favoriteInfo = new HashMap<String, Object>();
	            favoriteInfo.put("userId", id);
	            favoriteInfo.put("fileId", fileId);
	            favoriteInfo.put("type", fileType);
	            favoriteInfo.put("time", StringUtil.dateToStr(new Date()));
	            favoriteInfo.put("state", 2);
	            
	            int count = personalFavoriteMapper.addFavorite(favoriteInfo);
	            if (count > 0) {
	            	if (fileType == 1) {
	            		personalFavoriteMapper.updateFavorite(favoriteInfo);
	            	}
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                result.setJSON(resultJson);
	                return result;
	            }
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_22);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_22);
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
	 * 查看收藏文件
	 * @param parameters {token:用户信息(必填)}
	 * @param request 
	 * @return
	 */
	public DataOutputFormat listFavoriteFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("userId");
			HashMap<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("userId", id);
            List<HashMap<String, Object>> favoriteList = new ArrayList<HashMap<String, Object>>();
            favoriteList = personalFavoriteMapper.getFavorite(paraMap);
            resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
			resultJson.put("favoriteList", favoriteList);
            result.setJSON(resultJson);
            return result;
		} catch (Exception e) {
			resultJson.put(RetCode.RESULT_KEY, RetCode.FAIL);
			resultJson.put(RetCode.RESULT_VALUE, RetCode.FAIL_MSG + e.getMessage());
			result.setJSON(resultJson);
			log.error(e.getMessage(), e);
			return result;
		}
	}

	public DataOutputFormat cancelFavoriteFile(JSONObject parameters, HttpServletRequest request) {
		DataOutputFormat result = new DataOutputFormat();
		JSONObject resultJson = new JSONObject();
		try {
			int id = parameters.getInt("userId");
			String fileId = parameters.getString("fileId");
			String type = parameters.getString("fileType");
			
			if(!type.isEmpty()) {
				int fileType = Integer.parseInt(type);
				HashMap<String, Object> paraMap = new HashMap<String, Object>();
	            paraMap.put("userId", id);
	            paraMap.put("fileId", fileId);
	            paraMap.put("state", 1);
	            int count = personalFavoriteMapper.deleteFavorite(paraMap);
	            if (count > 0) {
	            	if (fileType == 1) {
	            		personalFavoriteMapper.updateFavorite(paraMap);
	            	}
	            	resultJson.put(RetCode.RESULT_KEY, RetCode.SUCCESS);
	    			resultJson.put(RetCode.RESULT_VALUE,RetCode.SUCCESS_MSG);
	                result.setJSON(resultJson);
	                return result;
	            }
			} else {
				resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_22);
				resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_22);
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
