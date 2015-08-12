package com.pingstart.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pingstart.R;
import com.pingstart.adsdk.AdsLoadListener;
import com.pingstart.adsdk.AdsManager;
import com.pingstart.adsdk.model.Ads;
import com.pingstart.utils.CommonUtils;
import com.pingstart.utils.DataUtils;

public class LoadAdBannerFragment extends Fragment implements AdsLoadListener, OnClickListener {
	private RelativeLayout mAdViewBannerContainer;
	private View mLoadAds;
	private TextView mLoadingStatus;
	private ProgressBar mLoadingProgressBar;
	private Button mRefreshButton;
	private String mStatusLabel = "";
	private String mLodingLabel = "";
	private AdsManager mAdsManager;
	private boolean mFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStatusLabel = getString(R.string.load_fail_banner);
		mLodingLabel = getString(R.string.loading_status);
		mAdsManager = new AdsManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID);
		mAdsManager.loadAds(this, AdsManager.BANNER_AD);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_banner, container, false);
		mRefreshButton = (Button) view.findViewById(R.id.refreshButton);
		mAdViewBannerContainer = (RelativeLayout) view.findViewById(R.id.adViewContainer);
		mLoadingProgressBar = (ProgressBar) view.findViewById(R.id.loading_shuffle_progressBar);
		mLoadingStatus = (TextView) view.findViewById(R.id.loading_status);
		if (mLoadAds != null) {
			ViewGroup parent = (ViewGroup) mLoadAds.getParent();
			if (parent != null) {
				parent.removeView(mLoadAds);
			}
			setViewVisible(View.INVISIBLE, View.INVISIBLE, "");
			mAdViewBannerContainer.addView(mLoadAds);
		} else {
			if (mFlag)
				setViewVisible(View.INVISIBLE, View.VISIBLE, "");
			else
				setViewVisible(View.VISIBLE, View.INVISIBLE, mLodingLabel);
		}
		mRefreshButton.setOnClickListener(this);
		return view;
	}

	private void setViewVisible(int mProgressvisible, int mButtonvisible, String str) {
		CommonUtils.setLabel(mLoadingStatus, str);
		mLoadingProgressBar.setVisibility(mProgressvisible);
		mRefreshButton.setVisibility(mButtonvisible);
	}

	@Override
	public void onClick(View v) {
		setViewVisible(View.VISIBLE, View.VISIBLE, mLodingLabel);
		if (!mFlag) {
			Toast.makeText(getActivity(), mLodingLabel, DataUtils.SHOW_TOAST_TIME).show();
		} else {
			mAdsManager.loadAds(this, AdsManager.INTETSTITIAL_AD);
		}
		mFlag = false;
		setViewVisible(View.VISIBLE, View.INVISIBLE, mLodingLabel);
		mAdsManager.loadAds(this, AdsManager.BANNER_AD);
	}

	@Override
	public void onDestroyView() {
		mAdViewBannerContainer.removeView(mLoadAds);
		mFlag = true;
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		mAdsManager.destroy();
		super.onDestroy();
	}

	@Override
	public void onLoadBannerSucceeded() {
		mLoadAds = mAdsManager.getBannerView();
		mAdViewBannerContainer.addView(mLoadAds);
		setViewVisible(View.INVISIBLE, View.INVISIBLE, "");
		mFlag = true;
	}

	@Override
	public void onLoadError() {
		setViewVisible(View.INVISIBLE, View.VISIBLE, "");
		mFlag = true;
		Toast.makeText(getActivity(), mStatusLabel, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoadInterstitialSucceeded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadNativeSucceeded(Ads arg0) {
		// TODO Auto-generated method stub

	}

}