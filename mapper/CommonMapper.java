package com.chinatelecom.ctdfs.common.mapper;

import java.util.HashMap;

public interface CommonMapper {
HashMap<String, Object> getEmpeeAcctByToken(String token);
	
	HashMap<String, Object> getEmpeeAcctByAppToken(HashMap<String,String> parmas);
	
	HashMap<String,Object> getClientInstallFile(HashMap<String,String> parmas);
	
	int createLoginQrCode(HashMap<String,Object> parmas);
}
