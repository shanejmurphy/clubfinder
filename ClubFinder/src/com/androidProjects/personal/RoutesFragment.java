package com.androidProjects.personal;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RoutesFragment extends DialogFragment{
	
	private ArrayList<Route> routes;
	private static int checkedItem = 0;
	
	// Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onRouteDialogItemClick(DialogFragment dialog, int number);
    }
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		List<String> strings = new ArrayList<String>();
		for(int i=1; i<=routes.size(); i++)
		{
			strings.add("Route " + i + ": " + routes.get(i-1).getTotalDistance() + " - " + routes.get(i-1).getTotalDuration() );
		}
		
		final CharSequence[] items = strings.toArray(new String[strings.size()]);
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.route);
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   // The 'which' argument contains the index position
                	   // of the selected item
                	   mListener.onRouteDialogItemClick(RoutesFragment.this, which);
                	   checkedItem = which;
                	   dialog.dismiss();
               }
        });
        return builder.create();
    }
	
	public void setRouteList(ArrayList<Route> routes)
	{
		this.routes = routes;
	}
	
	public void setCheckedItem(int item)
	{
		checkedItem = item;
	}
}
