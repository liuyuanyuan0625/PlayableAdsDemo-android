package com.zplay.playable.playableadsdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zplay.playable.playableadsdemo.sample.InterstitialSample;
import com.zplay.playable.playableadsdemo.sample.NativeAdRecyclerViewSample;
import com.zplay.playable.playableadsdemo.sample.NativeAdSample;
import com.zplay.playable.playableadsdemo.sample.PlayableAdSample;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zplay.playable.playableadsdemo.MainActivity.AdType.INTERSTITIAL;
import static com.zplay.playable.playableadsdemo.MainActivity.AdType.NATIVE_MANAGED;
import static com.zplay.playable.playableadsdemo.MainActivity.AdType.NATIVE_SELF;
import static com.zplay.playable.playableadsdemo.MainActivity.AdType.STATISTICS;
import static com.zplay.playable.playableadsdemo.MainActivity.AdType.VIDEO;

/**
 * Description:
 * <p>
 * Created by lgd on 2018/9/26.
 */

public class MainActivity extends ToolBarActivity {
    public static final String APP_ID = "5C5419C7-A2DE-88BC-A311-C3E7A646F6AF";
    enum AdType {
        VIDEO("Video", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayableAdSample.launch(v.getContext());
            }
        }),
        INTERSTITIAL("Interstitial", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterstitialSample.launch(v.getContext());
            }
        }),
        NATIVE_SELF("Native(Self Rendering)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeAdSample.launch(v.getContext());
            }
        }),
        NATIVE_MANAGED("Native(Managed Rendering)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeAdRecyclerViewSample.launch(v.getContext());
            }
        }),
        STATISTICS("Statistics", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsActivity.launch(v.getContext());
            }
        });

        private String name;
        private View.OnClickListener clickListener;

        AdType(String name, View.OnClickListener clickListener) {
            this.name = name;
            this.clickListener = clickListener;
        }
    }

    private static List<AdType> sAdTypeArray =
            Collections.unmodifiableList(Arrays.asList(VIDEO, INTERSTITIAL, NATIVE_SELF, NATIVE_MANAGED, STATISTICS));

    @BindView(R.id.ad_list)
    RecyclerView mAdList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showSettingsButton();
        ButterKnife.bind(this);
        mAdList.setAdapter(new AdListAdapter(sAdTypeArray));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(mAdList.getContext(), linearLayoutManager.getOrientation());
        mAdList.setLayoutManager(linearLayoutManager);
        mAdList.addItemDecoration(dividerItemDecoration);
    }

    static class AdListAdapter extends RecyclerView.Adapter<AdListVH> {

        List<AdType> mData;

        AdListAdapter(List<AdType> data) {
            mData = data;
        }


        @Override
        public AdListVH onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ad_list, parent, false);
            return new AdListVH(view);
        }

        @Override
        public void onBindViewHolder(AdListVH holder, int position) {
            holder.adName.setText(mData.get(position).name);
            holder.itemView.setOnClickListener(mData.get(position).clickListener);
        }

        @Override
        public int getItemCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }
    }

    static class AdListVH extends RecyclerView.ViewHolder {
        @BindView(R.id.ad_name)
        TextView adName;

        AdListVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static void launch(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
    }
}
