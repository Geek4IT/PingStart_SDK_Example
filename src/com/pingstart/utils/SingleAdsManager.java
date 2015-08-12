package com.pingstart.utils;

import android.content.Context;

import com.pingstart.adsdk.AdsManager;
import com.pingstart.data.DataUtils;

public class SingleAdsManager {

	private static SingleAdsManager instance = new SingleAdsManager();

	public static SingleAdsManager getInstance() {
		return instance;

	}

	public static void setInstance(SingleAdsManager instance) {
		SingleAdsManager.instance = instance;
	}

	private AdsManager adsManager = null;

	public AdsManager getAdsManager(Context context) {
		if (adsManager == null) {
			adsManager = new AdsManager(context, DataUtils.ADS_APPID,
					DataUtils.ADS_SLOTID);
		}
		return adsManager;
	}
}
