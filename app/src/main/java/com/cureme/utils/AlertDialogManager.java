package com.cureme.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.cureme.R;


public class AlertDialogManager {
	/**
	 * Function to display simple Alert Dialog
	 * @param context - application context
	 * @param title - alert dialog title
	 * @param message - alert message
	 * @param status - success/failure (used to set icon)
	 * 				 - pass null if you don't want icon
	 * */
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		if(status != null)
			// Setting alert dialog icon
			alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
	
	public void showContactUsDialog(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Contact Us");
		builder.setIcon(R.drawable.store_logo);
		builder.setMessage(Html
				.fromHtml("For any queries or issues <a href=mailto:cure.me.feedback@outlook.com?subject=Feedback>reach us</a>"));

		builder.setCancelable(false);
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
		((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

	}
}
