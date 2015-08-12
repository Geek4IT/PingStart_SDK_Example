package com.pingstart.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pingstart.R;
import com.pingstart.adsdk.AdsLoadListener;
import com.pingstart.adsdk.AdsManager;
import com.pingstart.adsdk.model.Ads;
import com.pingstart.utils.CommonUtils;
import com.pingstart.utils.DataUtils;

public class LoadAdInterFragment extends Fragment implements OnClickListener, AdsLoadListener {
	private Button mShowInterstitial;
	private TextView mLoadingStatus;
	private ProgressBar mLoadingProgressBar;
	private AdsManager mAdsManager;
	private String mStatusLabel = "";
	private String mLodingLabel = "";
	private boolean mFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdsManager = new AdsManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID);
		mStatusLabel = getString(R.string.load_fail_inter);
		mLodingLabel = getString(R.string.loading);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mInterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_interstitial, null, false);
		mLoadingProgressBar = (ProgressBar) mInterView.findViewById(R.id.loading_shuffle_progressBar);
		mLoadingStatus = (TextView) mInterView.findViewById(R.id.loading_status);
		mShowInterstitial = (Button) mInterView.findViewById(R.id.show_interstitial);
		mShowInterstitial.setOnClickListener(this);
		setViewVisible(View.INVISIBLE, "", true);

		return mInterView;
	}

	@Override
	public void onClick(View v) {
		setViewVisible(View.VISIBLE, this.getString(R.string.loading_status), true);
		if (mFlag) {
			Toast.makeText(getActivity(), mLodingLabel, DataUtils.SHOW_TOAST_TIME).show();
		} else {
			mAdsManager.loadAds(this, AdsManager.INTETSTITIAL_AD);
		}
		mFlag = true;
	}

	private void setViewVisible(int mProgressvisible, String str, boolean flag) {
		CommonUtils.setLabel(mLoadingStatus, str);
		mLoadingProgressBar.setVisibility(mProgressvisible);
		mShowInterstitial.setClickable(flag);
	}

	@Override
	public void onLoadBannerSucceeded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadError() {
		if (getActivity() != null && !getActivity().isFinishing()) {
			Toast.makeText(getActivity(), mStatusLabel, Toast.LENGTH_SHORT).show();
			setViewVisible(View.INVISIBLE, "", true);
			mFlag = false;
		}
	}

	@Override
	public void onLoadInterstitialSucceeded() {
		if (getActivity() != null && !getActivity().isFinishing()) {
			setViewVisible(View.INVISIBLE, "", true);
			mAdsManager.showInterstitialAd();
			mFlag = false;
		}
	}

	@Override
	public void onDestroyView() {
		mFlag = false;
		super.onDestroyView();
	}

	@Override
	public void onLoadNativeSucceeded(Ads arg0) {

	}

	@Override
	public void onDestroy() {
		mAdsManager.destroy();
		super.onDestroy();
	}
}