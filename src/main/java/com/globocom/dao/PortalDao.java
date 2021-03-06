package com.globocom.dao;

import java.util.List;

import com.globocom.model.Content_Portal_Mapping;
import com.globocom.model.Portal;

public interface PortalDao {
	
	int save(Portal portal);
	
	int update(Portal portal);
	
	Portal get(int id);
	
	List<Portal> list();

	List<Content_Portal_Mapping> getPortalMappingList();
}
