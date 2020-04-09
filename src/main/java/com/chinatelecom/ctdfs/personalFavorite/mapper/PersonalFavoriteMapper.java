package com.chinatelecom.ctdfs.personalFavorite.mapper;

import java.util.HashMap;
import java.util.List;

public interface PersonalFavoriteMapper {

	int addFavorite(HashMap<String, Object> favoriteInfo);

	void updateFavorite(HashMap<String, Object> favoriteInfo);

	List<HashMap<String, Object>> getFavorite(HashMap<String, Object> paraMap);

	int deleteFavorite(HashMap<String, Object> paraMap);

}
