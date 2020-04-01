package com.chinatelecom.ctdfs.departmentfile.mapper;

import java.util.HashMap;
import java.util.List;

public interface DepartmentFileMapper {
	HashMap<String, Object> getInfoById(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getFileByParent(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getAuditedInfo(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getAuditInfo(HashMap<String, Object> paraMap);
	
	int updateState(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getRelationByUser(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getRelationByGroup(HashMap<String, Object> paraMap);
}
