package com.example.kvo.bearchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kvo on 4/11/18.
 */

public class LandmarkAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Landmark> mLandmarks;
    private String username;

    public LandmarkAdapter(Context context, ArrayList<Landmark> landmarks) {
        mContext = context;
        mLandmarks = landmarks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // here, we specify what kind of view each cell should have. In our case, all of them will have a view
        // made from comment_cell_layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.landmark_cell_layout, parent, false);
        LandmarkViewHolder lvh = new LandmarkViewHolder(view);
        lvh.setmContext(mContext);
        lvh.setUsername(username);
        return lvh;
    }


    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // here, we the comment that should be displayed at index `position` in our recylcer view
        // everytime the recycler view is refreshed, this method is called getItemCount() times (because
        // it needs to recreate every cell).
        Landmark landmark = mLandmarks.get(position);
        ((LandmarkViewHolder) holder).bind(landmark);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mLandmarks.size();
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

class LandmarkViewHolder extends RecyclerView.ViewHolder {

    // each data item is just a string in this case
    public RelativeLayout mLandmarkBubbleLayout;
    public TextView mLandmarkTextView;
    public TextView mDistanceText;
    public ImageView mLandmarkImage;

    private Context mContext;

    public String username;

    public LandmarkViewHolder(View itemView) {
        super(itemView);
        mLandmarkBubbleLayout = itemView.findViewById(R.id.landmark_cell_layout);
        mLandmarkTextView = mLandmarkBubbleLayout.findViewById(R.id.landmark_text);
        mDistanceText = mLandmarkBubbleLayout.findViewById(R.id.landmark_distance);
        mLandmarkImage = mLandmarkBubbleLayout.findViewById(R.id.landmark_image);
        
        mLandmarkBubbleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Landmark: ", mLandmarkTextView.getText().toString());
                Intent commentFeedIntent = new Intent(mContext, CommentFeedActivity.class);
                commentFeedIntent.putExtra("usernameString", username);
                commentFeedIntent.putExtra("landmarkTextString", mLandmarkTextView.getText().toString());
                mContext.startActivity(commentFeedIntent);
            }
        });

    }

    void bind(Landmark landmark) {
        mLandmarkTextView.setText(landmark.getName());
        mLandmarkImage.setImageResource(landmark.getFilename());
        int rounded_distance = (int) Math.round(landmark.getDistance());
        if (rounded_distance < 10) {
            mDistanceText.setText("less than 10 meters away");
        } else {
            mDistanceText.setText(rounded_distance + " meters away");
            mDistanceText.setTextColor(Color.BLACK);
        }

    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}