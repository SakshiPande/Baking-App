package com.baking_app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baking_app.R;
import com.baking_app.model.Step;
import com.baking_app.utils.OnClick;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private List<Step> mStepList;
    private OnClick mOnClick;

    public VideoAdapter(Context context, List<Step> stepList) {
        this.mContext = context;
        this.mStepList = stepList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_list, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, final int position) {
        String shortDescription = mStepList.get(position).getShortDescription();
        holder.tvStep.setText(shortDescription);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClick.onClick(mContext, mStepList.get(position).getId(),
                        mStepList.get(position).getDescription(),
                        mStepList.get(position).getVideoURL(),
                        mStepList.get(position).getThumbnailURL());
            }
        });
    }

    public void setOnClick(OnClick onClick) {
        this.mOnClick = onClick;
    }

    @Override
    public int getItemCount() {
        return mStepList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView tvStep;

        public VideoViewHolder(View itemView) {
            super(itemView);
            tvStep =itemView.findViewById(R.id.tv_step);
        }
    }
}
