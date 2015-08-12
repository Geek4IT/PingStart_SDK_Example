package com.pingstart.fragment;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.pingstart.R;
import com.pingstart.adsdk.AdsClickListener;
import com.pingstart.adsdk.AdsLoadListener;
import com.pingstart.adsdk.AdsManager;
import com.pingstart.adsdk.model.Ads;
import com.pingstart.utils.CommonUtils;
import com.pingstart.utils.DataUtils;

public class LoadAdNativeFragment extends Fragment implements AdsLoadListener, OnClickListener {
	private View mAdViewNativeContainer;
	private TextView mLoadingStatus;
	private ProgressBar mLoadingProgressBar;
	private AdsManager mAdsManager;
	private String mStatusLabel = "";
	private String mLodingLabel = "";
	private Button mShowNative;
	private boolean mFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStatusLabel = getString(R.string.load_fail_native);
		mLodingLabel = getString(R.string.loading_status);
		mAdsManager = new AdsManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID);
		mAdsManager.loadAds(this,AdsManager.NATIVE_AD );
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mNativeView = inflater.inflate(R.layout.fragment_native, container, false);
		mAdViewNativeContainer = mNativeView.findViewById(R.id.native_container);
		mLoadingProgressBar = (ProgressBar) mNativeView.findViewById(R.id.loading_shuffle_progressBar);
		mLoadingStatus = (TextView) mNativeView.findViewById(R.id.loading_status);
		mShowNative = (Button) mNativeView.findViewById(R.id.show_native);
		mShowNative.setOnClickListener(this);
		if (mFlag)
			setViewVisible(View.INVISIBLE, View.VISIBLE, "");
		else
			setViewVisible(View.VISIBLE, View.INVISIBLE,mLodingLabel);

		return mNativeView;
	}

	@Override
	public void onClick(View v) {
		setViewVisible(View.VISIBLE, View.VISIBLE, mLodingLabel);
		if (!mFlag) {
			Toast.makeText(getActivity(), mLodingLabel, DataUtils.SHOW_TOAST_TIME).show();
		} else {
			mAdsManager.loadAds(this, AdsManager.NATIVE_AD);
		}
		mFlag = false;
	}

	@Override
	public void onLoadBannerSucceeded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadError() {
		mFlag = true;
		setViewVisible(View.INVISIBLE, View.VISIBLE, "");
		Toast.makeText(getActivity(), mStatusLabel, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoadInterstitialSucceeded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadNativeSucceeded(Ads ad) {
		setViewVisible(View.INVISIBLE, View.INVISIBLE, "");
		mFlag = true;
		String titleForAd = ad.getAdCallToAction();
		String titleForAdButton = ad.getAdCallToAction();
		String description = ad.getDescription();
		String title = ad.getTitle();
		ImageView nativeCoverImage = (ImageView) mAdViewNativeContainer.findViewById(R.id.native_coverImage);
		TextView nativeTitle = (TextView) mAdViewNativeContainer.findViewById(R.id.native_title);
		TextView nativeDescription = (TextView) mAdViewNativeContainer.findViewById(R.id.native_description);
		TextView nativeAdButton = (TextView) mAdViewNativeContainer.findViewById(R.id.native_titleForAdButton);
		TextView nativeAdflag = (TextView) mAdViewNativeContainer.findViewById(R.id.native_adflag);
		nativeAdflag.setText(getString(R.string.banner_adflag));
		if (!TextUtils.isEmpty(titleForAd) && !TextUtils.isEmpty(titleForAdButton)) {
			nativeAdButton.setText(titleForAdButton);
			nativeDescription.setText(description);
			nativeTitle.setText(title);
			downloadAndDisplayImage(ad.getPreview_link(), nativeCoverImage);
			mAdViewNativeContainer.setVisibility(View.VISIBLE);
			mAdsManager.registerNativeView(mAdViewNativeContainer, new AdsClickListener() {
				@Override
				public void onError() {
					Toast.makeText(getActivity(), getString(R.string.load_native_fail), DataUtils.SHOW_TOAST_TIME).show();
				}

				@Override
				public void onAdsClicked() {
				}
			});
		}

	}

	private void isParentNull() {
		if (mAdViewNativeContainer.getParent() != null) {
			ViewGroup parent = (ViewGroup) mAdViewNativeContainer.getParent();
			if (parent != null) {
				parent.removeView(mAdViewNativeContainer);
			}
		}
	}

	@Override
	public void onDestroyView() {
		mFlag = true;
		isParentNull();
		super.onDestroyView();
	}

	private void downloadAndDisplayImage(String imageUrl, final ImageView imageView) {
		RequestQueue imageRequestQueue = Volley.newRequestQueue(getActivity());
		@SuppressWarnings("deprecation")
		ImageRequest imageRequest = new ImageRequest(imageUrl, new

		Response.Listener<Bitmap>() {

			@Override
			public void onResponse(Bitmap response) {

				imageView.setImageBitmap(response);

			}

		}, 0, 0, Config.RGB_565, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				imageView.setImageResource(R.drawable.ic_launcher);

			}

		});

		imageRequestQueue.add(imageRequest);
	}

	@Override
	public void onDestroy() {
		mAdsManager.destroy();
		super.onDestroy();
	}

	private void setViewVisible(int mProgressvisible, int mButtonvisible, String str) {
		CommonUtils.setLabel(mLoadingStatus, str);
		mLoadingProgressBar.setVisibility(mProgressvisible);
		mShowNative.setVisibility(mButtonvisible);
	}

}