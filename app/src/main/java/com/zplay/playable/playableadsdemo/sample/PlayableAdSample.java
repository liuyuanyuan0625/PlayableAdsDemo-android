package com.zplay.playable.playableadsdemo.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.playableads.PlayLoadingListener;
import com.zplay.playable.playableadsdemo.R;
import com.zplay.playable.playableadsdemo.ToolBarActivity;
import com.zplay.playable.playableadsdemo.util.Encrypter;
import com.zplay.playable.playableadsdemo.util.UserConfig;
import com.playableads.PlayPreloadingListener;
import com.playableads.PlayableAds;
import com.playableads.SimplePlayLoadingListener;
import com.playableads.constants.BusinessConstants;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;

import static com.zplay.playable.playableadsdemo.MainActivity.APP_ID;

public class PlayableAdSample extends ToolBarActivity {

    private static final String AD_UNIT_ID = "3FBEFA05-3A8B-2122-24C7-A87D0BC9FEEC";
    private static final int REQUEST_CODE = 2;
    private static String sCurrentAppId = APP_ID;
    private static String sCurrentUnitId = AD_UNIT_ID;

    @BindView(R.id.text)
    TextView info;
    @BindView(R.id.am_url_edit)
    EditText mUrlEdit;
    @BindView(R.id.unit_id)
    EditText mUnitIdEdit;
    @BindView(R.id.app_id)
    EditText mAppIdEdit;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.am_clear_url_edit)
    View mClearUrlEdit;
    @BindView(R.id.clear)
    View mClear;
    @BindView(R.id.clear2)
    View mClear2;

    PlayableAds mRewardVideo;
    UserConfig mConfig;
    RequestQueue mRequestQueue;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setInfo((String) msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playable_ad);
        ButterKnife.bind(this);

        showUpAction();
        showSettingsButton();

        mRewardVideo = PlayableAds.init(this, APP_ID);
        mConfig = UserConfig.getInstance(this);

        mRequestQueue = Volley.newRequestQueue(this);

        mAppIdEdit.setText(sCurrentAppId);
        setTitle("Video");
        mUnitIdEdit.setText(sCurrentUnitId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRewardVideo.setChannelId(mConfig.getChannelId());
        mRewardVideo.setAutoLoadAd(mConfig.isAutoload());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("logInfo", info.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        info.setText(savedInstanceState.getString("logInfo", ""));
    }

    @OnLongClick(R.id.text)
    public boolean clearLog() {
        info.setText("");
        return true;
    }

    @OnClick(R.id.request)
    public void request() {
        String uidStr = mUnitIdEdit.getText().toString().trim();
        final String unitId;
        if (!TextUtils.isEmpty(uidStr)) {
            unitId = uidStr;
        } else {
            unitId = AD_UNIT_ID;
        }

        setInfo(unitId + " " + getString(R.string.start_request));

        mRewardVideo.requestPlayableAds(unitId, newRequestListener(unitId));
    }

    private PlayPreloadingListener newRequestListener(final String unitId) {

        return new PlayPreloadingListener() {

            @Override
            public void onLoadFinished() {
                setInfo(unitId + " " + getString(R.string.pre_cache_finished));
            }

            @Override
            public void onLoadFailed(int errorCode, String msg) {
                setInfo(unitId + " " + msg);
            }
        };
    }

    @OnClick(R.id.present)
    public void present() {
        String editText = mUnitIdEdit.getText().toString().trim();
        final String adUnitId = TextUtils.isEmpty(editText) ? AD_UNIT_ID : editText;
        mRewardVideo.presentPlayableAD(adUnitId, newPlayListener(adUnitId));
    }

    private PlayLoadingListener newPlayListener(final String adUnitId) {
        return new SimplePlayLoadingListener() {

            @Override
            public void onVideoStart() {
                setInfo(adUnitId + " " + getString(R.string.ads_video_start));
            }

            @Override
            public void playableAdsIncentive() {
                setInfo(adUnitId + " " + getString(R.string.ads_incentive));
            }

            @Override
            public void onVideoFinished() {
                setInfo(adUnitId + " " + getString(R.string.ads_video_finished));
            }

            @Override
            public void onLandingPageInstallBtnClicked() {
                setInfo(adUnitId + " " + getString(R.string.landing_page_install_btn_clicked));
            }

            @Override
            public void onAdClosed() {
                setInfo(adUnitId + " " + getString(R.string.ad_present_closed));
            }

            @Override
            public void onAdsError(int code, String msg) {
                setInfo(adUnitId + " " + msg);
            }
        };
    }

    @OnTextChanged(R.id.am_url_edit)
    public void urlChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            mClearUrlEdit.setVisibility(View.GONE);
        } else {
            mClearUrlEdit.setVisibility(View.VISIBLE);
        }
    }

    @OnTextChanged(R.id.unit_id)
    public void unitIdChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            mClear.setVisibility(View.GONE);
            sCurrentUnitId = AD_UNIT_ID;
        } else {
            sCurrentUnitId = s.toString().trim();
            mClear.setVisibility(View.VISIBLE);
        }
    }

    @OnTextChanged(R.id.app_id)
    public void appIdChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s.toString().trim())) {
            mClear2.setVisibility(View.GONE);
            PlayableAds.init(this, APP_ID);
            sCurrentAppId = APP_ID;
        } else {
            sCurrentAppId = s.toString().trim();
            PlayableAds.init(this, sCurrentAppId);
            mClear2.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.am_clear_url_edit)
    void clearUrlEdit() {
        mUrlEdit.setText("");
    }

    @OnClick(R.id.clear)
    public void clearUnitId() {
        mUnitIdEdit.setText("");
    }

    @OnClick(R.id.clear2)
    public void clearAppId() {
        mAppIdEdit.setText("");
    }

    private void setInfo(final String msg) {
        Log.d("cccBBB", "=> " + msg);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                info.append(msg + "\n\n");
                mScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void scan(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                setInfo(getString(R.string.open_camera_permission));
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
                return;
            }
        }
        Intent intent = new Intent(PlayableAdSample.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String result = bundle.getString(CodeUtils.RESULT_STRING);
                    testAdvertising(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(PlayableAdSample.this, R.string.code_request_error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    // 将SDK中加载/解析预览内容的部分提取到此处
    private void testAdvertising(final String srcId) {
        setInfo("start request ad");
        final ValueCallback<JSONObject> callback = new ValueCallback<JSONObject>() {
            @Override
            public void onReceiveValue(JSONObject value) {
                mRewardVideo.requestPlayableAds(value, new PlayPreloadingListener() {
                    @Override
                    public void onLoadFinished() {
                        final String tag = Encrypter.doMD5Encode16(String.valueOf(this.hashCode()));
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRewardVideo.presentPlayableAD(tag, null);
                            }
                        }, 500);
                    }

                    @Override
                    public void onLoadFailed(int errorCode, String msg) {
                        sendDebugInfo(srcId, errorCode, msg);
                    }
                });
            }
        };

        if (srcId.startsWith("https://") || srcId.startsWith("http://")) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("response_target", "preview");
                jsonObject.putOpt("video_page_url", srcId);
            } catch (JSONException ignore) {
            }
            callback.onReceiveValue(jsonObject);
        } else {
            String url = BusinessConstants.HOST_SRC_ID() + "/" + srcId;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jo) {
                    try {
                        jo.putOpt("response_target", "preview");
                    } catch (JSONException ignore) {
                    }
                    callback.onReceiveValue(jo);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    sendDebugInfo(srcId, error.networkResponse.statusCode, error.getMessage());
                }
            });
            mRequestQueue.add(jsonObjectRequest);
        }
    }

    private void sendDebugInfo(String id, int errCode, String description) {
        Message message = mHandler.obtainMessage();
        message.obj = String.format(Locale.CHINA, "%s %d %s", id, errCode, description);
        mHandler.sendMessage(message);
    }

    public void verifyAssets(View view) {
        String url = mUrlEdit.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            info.setText("input ZPLAYAds' HTML url or AD_ID");
            return;
        }
        testAdvertising(url);
    }

    public static void launch(Context ctx) {
        Intent i = new Intent(ctx, PlayableAdSample.class);
        ctx.startActivity(i);
    }
}
