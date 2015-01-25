package com.androidProjects.personal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;

public class RemoveAdsFragment extends DialogFragment{
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		ContextThemeWrapper context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Dialog_NoActionBar);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.remove_ads);
        builder.setMessage(R.string.remove_ads_message)
        	.setPositiveButton(R.string.remove_ads, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// jump to google play store location for clubfinder pro
					try {
	            	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getResources().getString(R.string.pro_pkg))));
	            	} catch (android.content.ActivityNotFoundException anfe) {
	            	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+getResources().getString(R.string.pro_pkg))));
	            	}
					dialog.dismiss();
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
        
        return builder.create();
    }
}
