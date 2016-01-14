package com.cureme.data;

import java.util.ArrayList;
import java.util.List;

public class CureMeGroup extends CureMeCommon {

	private List<CureMeItem> items = new ArrayList<CureMeItem>();
	
	public CureMeGroup(String uniqueId, String title, String description, String imagePath) {
		super(uniqueId,title,description,imagePath);
	}

	/**
	 * @return the items
	 */
	public List<CureMeItem> getItems() {
		return items;
	}

}
