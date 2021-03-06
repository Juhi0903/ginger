package com.globocom.service;

import java.util.List;

import com.globocom.model.Content_Portal_Mapping;
import com.globocom.model.Category;
import com.globocom.model.Content;
import com.globocom.model.ContentProvider;
import com.globocom.model.Country;
import com.globocom.model.Operator;

public interface CategoryService {
	
	long saveCategory(Category category);
	
	List<Category> getCategoryList(int contentid);
	
	boolean deleteCategory(int id);
	
	boolean updateCategory(Category category);
	
	List<Category> getCategory(int id);
	
	List<Country> getCountry();
	
	List<Operator> getOperator(int id);
	
	int saveContentPortalMapping(Content_Portal_Mapping portalMapping);
	
	List<Content> getContentList(int contentType,int categoryId, int portalId, int operatorId);
	
	List<Content_Portal_Mapping> getContentMappingList(int portalid,int categoryId,int operatorid);
	
	int changeContentStatus(int id, String status);
	
	List<Content> getContent (int contentType, String categoryId , String status);
	
	int approveOrRejectContent(int id , String status);
	
	long saveContentProvider(ContentProvider contentprovider);
	
	List<Content> getHtmlGamesExcel(int id , int cp);
	
	List<ContentProvider> getContentProvider();

	int removeContentMapping(int id);




}
