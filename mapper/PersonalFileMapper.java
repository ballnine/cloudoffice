package com.chinatelecom.ctdfs.personalfile.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface PersonalFileMapper {
	int addPersonalFile(List<HashMap<String, Object>> fileInfo);

	List<HashMap<String, Object>> getPersonalFileInfo(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getRecycleSubFile(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getDeleteSubFile(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getFileInfoById(HashMap<String, Object> paraMap);

	int updateFileStatus(HashMap<String, Object> paraMap);
	
	List<HashMap<String, Object>> getRecyclerFile(HashMap<String, Object> paraMap);
	
	HashMap<String, Object> getParentById(HashMap<String, Object> paraMap);
	
	void deleteFile(List<String> idStrList);
	
	int updateFileName(HashMap<String, Object> paraMap);
}
