package com.cureme.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

public class CureMeCommon {
	private String uniqueId;
	private String title;
	private String description;
	private String imagePath;

	public CureMeCommon(String uniqueId, String title, String description,
			String imagePath) {
		this.uniqueId = uniqueId;
		this.title = title;
		this.description = description;
		this.imagePath = imagePath;
	}

	/**
	 * @return the Unique Id set for the Item
	 */
	public String get_uniqueId() {
		return uniqueId;
	}

	/**
	 * @param _uniqueId
	 *            the Id to set for Item
	 */
	public void set_uniqueId(String _uniqueId) {
		this.uniqueId = _uniqueId;
	}

	/**
	 * @return the title for the CureMe Item
	 */
	public String get_title() {
		return title;
	}

	/**
	 * @param _title
	 *            the Title to set
	 */
	public void set_title(String _title) {
		this.title = _title;
	}

	/**
	 * @return the _description
	 */
	public String get_description() {
		return description;
	}

	/**
	 * @param _description
	 *            the _description to set
	 */
	public void set_description(String _description) {
		this.description = _description;
	}

	/**
	 * @return the imageId
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param imageId
	 *            the imageId to set
	 */
	public void setImagePath(String imageId) {
		this.imagePath = imageId;
	}

	@Override
	public String toString() {
		return title + "\n";
	}

	public static Drawable getImageDrawable(Context myContext, String imagePath)
			throws IOException {
		
		AssetManager assetManager = myContext.getAssets();

		// get input stream
		InputStream ims = assetManager.open(imagePath);
		// load image as Drawable
		return Drawable.createFromStream(ims, null);

	}
}
