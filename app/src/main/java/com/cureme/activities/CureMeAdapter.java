package com.cureme.activities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cureme.R;
import com.cureme.data.CureMeCommon;
import com.cureme.data.CureMeGroup;

import java.util.List;

public class CureMeAdapter extends ArrayAdapter<CureMeCommon> {

	Context context;

	public CureMeAdapter(Context context, int resourceId,
			List<CureMeCommon> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/* private view holder class */
	private class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		try {
			ViewHolder holder = null;
			CureMeCommon rowItem = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.activity_grid_item,
						null);
				holder = new ViewHolder();
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.title);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.icon);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.txtTitle.setText(rowItem.get_title());
			holder.imageView.setImageDrawable(CureMeGroup.getImageDrawable(
					context, rowItem.getImagePath()));

		} catch (Exception e) {

		}
		return convertView;
	}

}
