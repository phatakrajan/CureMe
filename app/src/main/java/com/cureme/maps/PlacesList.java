package com.cureme.maps;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

@SuppressWarnings("serial")
public class PlacesList implements Serializable {

	@Key
	public String status;

	@Key
	public List<Place> results;

}
