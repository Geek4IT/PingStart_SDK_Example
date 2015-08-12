package com.pingstart.fragment;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class LoadAdShuffFragment extends Fragment implements OnClickListener, AdsLoadListener {
	private RelativeLayout mAdViewBannerContainer;
	private ImageView mShowImage;
	private TextView mLoadingStatus;
	private ProgressBar mLoadingProgressBar;
	private AdsManager mAdsManager;
	private View mShuffView;
	private View mNativeContainView;
	private View mLoadAds;
	private int mFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdsManager = new AdsManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mShuffView = inflater.inflate(R.layout.fragment_shuffle, container, false);
		mNativeContainView =mShuffView.findViewById(R.id.native_container);
		mAdViewBannerContainer = (RelativeLayout) mShuffView.findViewById(R.id.adShuff_BannerViewContainer);
		mShowImage = (ImageView) mShuffView.findViewById(R.id.show_image);
		mLoadingStatus = (TextView) mShuffView.findViewById(R.id.loading_status);
		mLoadingProgressBar = (ProgressBar) mShuffView.findViewById(R.id.loading_shuffle_progressBar);
		mShowImage.setVisibility(View.VISIBLE);
		mShowImage.setOnClickListener(this);
		setViewVisible(View.INVISIBLE, "");
		return mShuffView;
	}

	@Override
	public void onClick(View v) {
		if (CommonUtils.isFastDoubleClick()) {
			return;
		}
		setViewVisible(View.VISIBLE, this.getString(R.string.loading_status));
		mNativeContainView.setVisibility(View.INVISIBLE);
		if (mLoadAds != null) {
			mAdViewBannerContainer.removeView(mLoadAds);
		}
		Random mRan = new Random();
		mFlag = mRan.nextInt(3) + 1;
		switch (mFlag) {
		case DataUtils.BANNER_FIRST:
			mAdsManager.loadAds(this, AdsManager.BANNER_AD);
			break;
		case DataUtils.INTERSTITIAL_SECOND:
			mAdsManager.loadAds(this, AdsManager.INTETSTITIAL_AD);
			break;
		case DataUtils.NATIVE_THIRD:
			mAdsManager.loadAds(this, AdsManager.NATIVE_AD);
			break;
		default:
			break;
		}
	}

	private void setViewVisible(int mProgressvisible, String str) {
		CommonUtils.setLabel(mLoadingStatus, str);
		mLoadingProgressBar.setVisibility(mProgressvisible);
	}

	@Override
	public void onDestroyView() {
		mAdViewBannerContainer.removeView(mLoadAds);
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		mLoadAds = null;
		mAdsManager.destroy();
		super.onDestroy();
	}

	@Override
	public void onLoadBannerSucceeded() {
		setViewVisible(View.INVISIBLE, "");
		mLoadAds = mAdsManager.getBannerView();
		mAdViewBannerContainer.addView(mLoadAds);
	}

	@Override
	public void onLoadError() {
		setViewVisible(View.INVISIBLE, "");

		String errorMessage = "";
		switch (mFlag) {
		case DataUtils.BANNER_FIRST:
			errorMessage = this.getString(R.string.load_fail_banner);
			break;
		case DataUtils.INTERSTITIAL_SECOND:
			errorMessage = this.getString(R.string.load_fail_inter);
			break;
		case DataUtils.NATIVE_THIRD:
			errorMessage = this.getString(R.string.load_fail_native);
			break;

		default:
			break;
		}

		Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoadInterstitialSucceeded() {
		setViewVisible(View.INVISIBLE, "");
		mAdsManager.showInterstitialAd();
	}

	@Override
	public void onLoadNativeSucceeded(Ads ad) {
		mNativeContainView.setVisibility(View.VISIBLE);
		setViewVisible(View.INVISIBLE, "");

		/**
		 * If you want to implement the Native style, you should get all
		 * elements of native ad, customize your native UI and register your
		 * NativeView
		 */
		String description = ad.getDescription();
		String titleForAd = ad.getAdCallToAction();
		String titleForAdButton = ad.getAdCallToAction();
		String title = ad.getTitle();
		ImageView nativeCoverImage = (ImageView) mNativeContainView.findViewById(R.id.native_coverImage);
		TextView nativeTitle = (TextView) mNativeContainView.findViewById(R.id.native_title);
		TextView nativeDescription = (TextView) mNativeContainView.findViewById(R.id.native_description);
		TextView nativeAdButton = (TextView) mNativeContainView.findViewById(R.id.native_titleForAdButton);

		if (!TextUtils.isEmpty(titleForAd) && !TextUtils.isEmpty(titleForAdButton)) {
			nativeDescription.setText(titleForAd);
			nativeAdButton.setText(titleForAdButton);
			nativeTitle.setText(title);
			nativeDescription.setText(description);
			downloadAndDisplayImage(ad.getPreview_link(), nativeCoverImage);
			mAdsManager.registerNativeView(mNativeContainView, new AdsClickListener() {
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
}
