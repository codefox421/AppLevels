/**
 * Copyright 2013 Nick Iaconis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Filename:	ManagedPackage.java
 * Class:		ManagedPackage
 * 
 * Purpose:		Stores information about an application that AppLevels is managing.
 */

package com.codefox421.applevels;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class ManagedPackage {

	private Drawable packageIcon;
	private String packageName;
	private int packageVolume;
	
	public ManagedPackage(String packageName, Drawable packageIcon, int packageVolume) {
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "ctor");
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
