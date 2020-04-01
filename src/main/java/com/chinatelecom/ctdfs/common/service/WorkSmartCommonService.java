package com.chinatelecom.ctdfs.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.chinatelecom.ctdfs.common.mapper.CommonMapper;
import com.chinatelecom.udp.core.cmclient.CMClient;
import com.chinatelecom.udp.core.datarouter.IWorkService;
import com.chinatelecom.udp.core.datarouter.WebContextHolder;
import com.chinatelecom.udp.core.datarouter.exception.DataException;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.datarouter.utils.TypeHttpRequest;
import com.chinatelecom.udp.core.lang.json.JSONArray;
import com.chinatelecom.udp.core.lang.json.JSONObject;
import com.chinatelecom.udp.core.sharecontext.userrights.ILoginUserInfo;
import com.chinatelecom.udp.core.sharecontext.userrights.ISystemRoleInfo;
import com.chinatelecom.udp.core.userrights.LoginUserInfo;

@Service
public class WorkSmartCommonService implements IWorkService {
	
	private static Logger log = LogManager.getLogger(WorkSmartCommonService.class.getName());
	
	@Resource
	private CommonMapper commonMapper;

	@Override
	public String getCode() {
		return "workSmartMMCommonService";
	}

	@Override
	public String getName() {
		return "通用";
	}
	
	public HashMap<String, String> groupAreaMap = new HashMap<String, String>();
	
	public HashMap<String, Object> getUserInfoById(int id) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		HashMap<String, Object> userInfo = commonMapper.getUserById(params);
		if (userInfo != null) {
			return userInfo;
		} else {
			return null;
		}
	}
	
	public boolean getInnerUserFlag(ILoginUserInfo loginUserInfo) {
		
		boolean innerUserFlag = false;
		
		String kindName = loginUserInfo.getSTAFF_KIND();
		String innerKinds=CMClient.getInstance().getValue(WorkSmartCommonService.class.getName(), "inner_kind").getStringValue();
		List<ISystemRoleInfo> roleList = loginUserInfo.getRoles();
		//判断是否内部用户角色
		boolean innerRole = false;
		if(roleList != null && roleList.size() > 0) {
			for(ISystemRoleInfo role : roleList) {
				if("W1nzu3UuOsjCwzas".equals(role.getROLE_ID())) {
					innerRole = true;
					break;
				}
			}
		}
		
		//W1nzu3UuOsjCwzas
		if((innerKinds != null && ArrayUtils.contains(innerKinds.split(","),kindName)) || innerRole) {
			innerUserFlag = true;
		}
		return innerUserFlag;
	}
	
	
	public List<String> getWorkAppRole(ILoginUserInfo loginUserInfo){
		
		 List<String> appRole = new ArrayList<String>();
		 List<ISystemRoleInfo> roleList = loginUserInfo.getRoles();
		 if(roleList != null && roleList.size() > 0) {
				for(ISystemRoleInfo role : roleList) {
					if("GFj3CwV21FSxu1RO".equals(role.getPARENT_ROLE_ID())) {
						appRole.add(role.getROLE_ID());
					}
				}
			}
		 //如果没有角色信息，对于内部人员默认添加内部人员角色，获取发布到内部人员角色的APP信息
			 String kindName = loginUserInfo.getSTAFF_KIND();
			 String innerKinds=CMClient.getInstance().getValue(WorkSmartCommonService.class.getName(), "inner_kind").getStringValue(); 
			 if(innerKinds != null && ArrayUtils.contains(innerKinds.split(","),kindName)) {
				 appRole.add("W1nzu3UuOsjCwzas");
			}else {
				appRole.add("3CIpKrG9muA287Mx");
			}
			 
		 return appRole;
		
	}
	
	public String getEmpeeAcctByImAcct(String imAcct) {
		if(imAcct == null) {
			return imAcct;
		}
		 imAcct = imAcct.toUpperCase();
		 log.info("------imAcct---------" + imAcct);
		if(imAcct.contains("IM")) {
			String [] empeeInfo = imAcct.split("IM");
			log.info("------empeeInfo[0]---------" + empeeInfo[0]);
			return empeeInfo[0];
		}
		
		return null;
	}
	
	
	
	/**
	 * 根据token获取用户工号
	 * @param request
	 * @return
	 */
	public ILoginUserInfo getEmpeeInfoByToken(TypeHttpRequest request,String token){
		ILoginUserInfo userInfo=null;
		
		userInfo=WebContextHolder.getLoginUserInfo();
		if(userInfo != null) {
			log.info("》》》》》》》》》》》》》》》》》》获取用户userid后>>>>>>>>>>>>>>>>>>>>"+userInfo.getEmpeeAcct());
		}else {
			log.info("通过WebContextHolder未获取到用户信息！");
			log.info("根据用户token获取用户信息！");
			if(token == null || token.length() == 0) {
				log.info("用户token为空！");
				return null;
			}
			 HashMap<String,Object> map=commonMapper.getEmpeeAcctByToken(token);
			 if(map!=null) {
				String empeeAcct=(map.get("EMPEE_ACCT")==null?"-1":map.get("EMPEE_ACCT").toString());
				userInfo = LoginUserInfo.getFromCache(empeeAcct);
				log.info("》》》》》》》》》》》》》》》》》》根据用户token获取的用户信息>>>>>>>>>>>>>>>>>>>>"+userInfo.getEmpeeAcct());
			 }
		}
		return userInfo;
	}
	
	
	/**
	 * 根据token获取用户工号
	 * @param request
	 * @return
	 */
	public String getEmpeeAcctByToken(TypeHttpRequest request,String token){
		String userId = "-1";
		ILoginUserInfo userInfo=null;
		
		userInfo=WebContextHolder.getLoginUserInfo();
		if(userInfo != null) {
			log.info("》》》》》》》》》》》》》》》》》》获取用户userid后>>>>>>>>>>>>>>>>>>>>"+userInfo.getEmpeeAcct());
			return userInfo.getEmpeeAcct();
		}else {
			log.info("通过WebContextHolder未获取到用户信息！");
			log.info("根据用户token获取用户信息！");
			
			 HashMap<String,Object> map=commonMapper.getEmpeeAcctByToken(token);
			 if(map!=null) {
				String empeeAcct=(map.get("EMPEE_ACCT")==null?"-1":map.get("EMPEE_ACCT").toString());
				//userInfo = LoginUserInfo.getFromCache(empeeAcct);
				log.info("》》》》》》》》》》》》》》》》》》根据用户token获取的用户信息>>>>>>>>>>>>>>>>>>>>"+empeeAcct);
				return empeeAcct;
				
			 }
		}
		return userId;
	}
	
	
	
	/**
	 * 根据token获取用户工号
	 * @param request
	 * @return
	 */
	public String getEmpeeAcctByAppToken(TypeHttpRequest request,String appCode,String appToken){
		String userId="";
			if(WebContextHolder.getLoginUserInfo() != null) {
				userId=WebContextHolder.getLoginUserInfo().getEmpeeAcct();
				return userId;
			}
		try {
		 	 if(userId == null || "".equals(userId)){
		 		 
		 		HashMap<String,String> parmas = new HashMap<String,String>();
		 		
		 		if(appCode == null || appCode.length() == 0 || appToken == null || appToken.length() == 0 ) {
		 			return "-1";
		 		}
		 		 parmas.put("appCode",appCode);
		 		 parmas.put("appToken",appToken);
				 HashMap<String,Object> map=commonMapper.getEmpeeAcctByAppToken(parmas);
				 if(map!=null) {
					 userId=(map.get("EMPEE_ACCT")==null?"-1":map.get("EMPEE_ACCT").toString());
				 }
		 	 }
		} catch (Exception e) { 
			log.error("获取用户工号失败");
			e.printStackTrace();
		}
		return userId;
	}
	
	

	/**
	 * 根据token获取用户工号
	 * @param request
	 * @return
	 */
	public ILoginUserInfo getEmpeeInfoByAppToken(TypeHttpRequest request,String appCode,String appToken){
		ILoginUserInfo userInfo=null;
			if(WebContextHolder.getLoginUserInfo() != null) {
				return WebContextHolder.getLoginUserInfo();
			}
		try {
		 		 
		 		HashMap<String,String> parmas = new HashMap<String,String>();
		 		
		 		if(appCode == null || appCode.length() == 0 || appToken == null || appToken.length() == 0 ) {
		 			return null;
		 		}
		 		 parmas.put("appCode",appCode);
		 		 parmas.put("appToken",appToken);
				 HashMap<String,Object> map=commonMapper.getEmpeeAcctByAppToken(parmas);
				 if(map!=null) {
					String empeeAcct=(map.get("EMPEE_ACCT")==null?"-1":map.get("EMPEE_ACCT").toString());
					if(empeeAcct != "-1") {
						return LoginUserInfo.getFromCache(empeeAcct);
				 }
		 	 }
		} catch (Exception e) { 
			log.error("获取用户工号失败");
			e.printStackTrace();
		}
		return userInfo;
	}
	
	public String refresh(TypeHttpRequest request,String appCode,String appToken){
		String userId="";
			if(WebContextHolder.getLoginUserInfo() != null) {
				userId=WebContextHolder.getLoginUserInfo().getEmpeeAcct();
				return userId;
			}
		try {
		 	 if(userId == null || "".equals(userId)){
		 		 
		 		HashMap<String,String> parmas = new HashMap<String,String>();
		 		
		 		if(appCode == null || appCode.length() == 0 || appToken == null || appToken.length() == 0 ) {
		 			return "-1";
		 		}
		 		 parmas.put("appCode",appCode);
		 		 parmas.put("appToken",appToken);
				 HashMap<String,Object> map=commonMapper.getEmpeeAcctByAppToken(parmas);
				 if(map!=null) {
					 userId=(map.get("EMPEE_ACCT")==null?"-1":map.get("EMPEE_ACCT").toString());
				 }
		 	 }
		} catch (Exception e) { 
			log.error("获取用户工号失败");
			e.printStackTrace();
		}
		return userId;
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
