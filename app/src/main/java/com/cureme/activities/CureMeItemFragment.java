package com.cureme.activities;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cureme.CureMeApplication;
import com.cureme.R;
import com.cureme.data.CureMeGroup;
import com.cureme.utils.ListTagHandler;

public class CureMeItemFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String desc = getArguments().getString(CureMeConstants.DESCRIPTION);
		String imagePath = getArguments().getString(CureMeConstants.IMAGEPATH);
		String titleVal = getArguments().getString(CureMeConstants.TITLE);

		CureMeApplication.getInstance().trackScreenView(titleVal);

		View view = inflater.inflate(R.layout.cure_me_item_layout, container,
				false);
		try {
			ImageView image = (ImageView) view.findViewById(R.id.icon);
			image.setImageDrawable(CureMeGroup.getImageDrawable(
					view.getContext(), imagePath));

			TextView descr = (TextView) view.findViewById(R.id.description);
			descr.setMovementMethod(LinkMovementMethod.getInstance());
			descr.setText(Html.fromHtml(desc, null, new ListTagHandler()));

			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(titleVal);

		} catch (Exception e) {

		}

		return view;
	}


}
