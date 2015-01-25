package com.androidProjects.personal;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class InfoPopupAdapter implements InfoWindowAdapter{
	private LayoutInflater inflater;
	
	public InfoPopupAdapter(LayoutInflater l){
		inflater = l;
	}
	
	@Override
	public View getInfoWindow(Marker m){
		return null;
	}
	
	@Override
	public View getInfoContents(Marker m){
		View v = inflater.inflate(R.layout.info_window_layout, null);
		
		TextView tvTitle = (TextView) v.findViewById(R.id.title);
		TextView tvSnippet = (TextView) v.findViewById(R.id.snippet);
		tvTitle.setText(m.getTitle());
		tvSnippet.setText(m.getSnippet());
		
		return v;
	}
}
