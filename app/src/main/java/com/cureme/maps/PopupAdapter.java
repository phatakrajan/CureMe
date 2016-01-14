package com.cureme.maps;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cureme.R;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class PopupAdapter implements InfoWindowAdapter {

	LayoutInflater inflater = null;

	public PopupAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View popup = inflater.inflate(R.layout.popup, null);

		TextView tv = (TextView) popup.findViewById(R.id.title);

		tv.setText(marker.getTitle());
		tv = (TextView) popup.findViewById(R.id.snippet);

		String snippet = marker.getSnippet();
		String[] tokens = snippet.split("\\|");
		tv.setText(tokens[0]);

		if ((tokens.length > 1)
				&& (tokens[1].equalsIgnoreCase("null") == false)) {
			tv = (TextView) popup.findViewById(R.id.phone);
			tv.setText(tokens[1]);
		}

		// marker.showInfoWindow();
		return (popup);
	}

}
