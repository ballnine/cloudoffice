package com.chinatelecom.ctdfs.departmentfile.mapper;

import java.util.HashMap;
import java.util.List;

public interface DepartmentFileMapper {
	int addOrgFile(HashMap<String, Object> fileInfo);
	
	int addAuditFile(HashMap<String, Object> fileInfo);
	
	HashMap<String, Object> getInfoById(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getFileByParent(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getAuditedInfo(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getAuditInfo(HashMap<String, Object> paraMap);
	
	int updateState(HashMap<String, Object> paraMap);
	
	int updateAuditState(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getRelationByUser(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getRelationByGroup(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getFileByName(HashMap<String, Object> paraMap);
	
	int getReviewerByFile(HashMap<String, Object> paraMap);
	
	void deleteFile(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getOrgAccess(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getStaffAccess(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getGroupAccess(HashMap<String, Object> paraMap);

	int updateFileName(HashMap<String, Object> paraMap);
}
