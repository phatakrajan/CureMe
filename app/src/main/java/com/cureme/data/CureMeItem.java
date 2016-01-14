package com.cureme.data;

public class CureMeItem extends CureMeCommon {
	
	private String content ;
	private CureMeGroup group;
	public CureMeItem(String uniqueId, String title, String description, String imagePath,String content, CureMeGroup group ){
		super(uniqueId,title,description,imagePath);
		this.content = content ;
		this.group = group;
	}
	
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the group
	 */
	public CureMeGroup getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(CureMeGroup group) {
		this.group = group;
	}

}
