package com.codefox421.applevels;

import android.graphics.drawable.Drawable;

public class ManagedPackage {

	private Drawable packageIcon;
	private String packageName;
	private int packageVolume;
	
	public ManagedPackage(String packageName, Drawable packageIcon, int packageVolume) {
		this.packageName = packageName;
		this.packageIcon = packageIcon;
		this.packageVolume = packageVolume;
	}
	
	public Drawable getIcon() {
		return packageIcon;
	}
	
	public String getName() {
		return packageName;
	}
	
	public int getVolume() {
		return packageVolume;
	}
}
