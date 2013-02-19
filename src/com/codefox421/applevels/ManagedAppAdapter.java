package com.codefox421.applevels;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagedAppAdapter extends ArrayAdapter<ManagedPackage> {
	
	Context context;
	int layoutResourceId;
	ManagedPackage data[] = null;
	
	public ManagedAppAdapter(Context context, int layoutResourceId,
			ManagedPackage[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		PackageHolder packageHolder = null;
		
		if(row == null) {
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			packageHolder = new PackageHolder();
			packageHolder.appIcon = (ImageView)row.findViewById(R.id.app_icon);
			packageHolder.packageName = (TextView)row
					.findViewById(R.id.package_name_text);
			packageHolder.volumeLevel = (TextView)row
					.findViewById(R.id.volume_level_text);
			
			row.setTag(packageHolder);
		} else {
			packageHolder = (PackageHolder)row.getTag();
		}
		
		ManagedPackage managedPackage = data[position];
		packageHolder.appIcon.setImageDrawable(managedPackage.getIcon());
		packageHolder.packageName.setText(managedPackage.getName());
		packageHolder.volumeLevel.setText(
				Integer.toString(managedPackage.getVolume()));
		
		return row;
	}
	
	static class PackageHolder {
		ImageView appIcon;
		TextView packageName;
		TextView volumeLevel;
	}

}
