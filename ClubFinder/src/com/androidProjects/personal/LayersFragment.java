package com.androidProjects.personal;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class LayersFragment extends DialogFragment{
	
	private static int checkedItem = 0;
	
	// Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onLayerDialogItemClick(DialogFragment dialog, int number);
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
    
    public void setCheckedItem(int item)
	{
		checkedItem = item;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.layer);
        builder.setSingleChoiceItems(R.array.view_list, checkedItem, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   // The 'which' argument contains the index position
                	   // of the selected item
                	   mListener.onLayerDialogItemClick(LayersFragment.this, which);
                	   checkedItem = which;
                	   dialog.dismiss();
               }
        });
        return builder.create();
    }
}
