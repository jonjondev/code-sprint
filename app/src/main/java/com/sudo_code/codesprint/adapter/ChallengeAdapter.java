package com.sudo_code.codesprint.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sudo_code.codesprint.R;
import com.sudo_code.codesprint.model.Challenge;
import java.util.ArrayList;
import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ViewHolder> {
    private List<Challenge> challenges;

    /**
     * a constructor for the class
     *
     * @param challenges
     */
    public ChallengeAdapter(ArrayList<Challenge> challenges) {
        this.challenges = challenges;
    }

    /**
     * things to do on the creation of the viewholder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.challenge_item, parent, false);

        return new ViewHolder(contactView);
    }

    /**
     * things to do when binding the viewholder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Challenge challenge = challenges.get(position);

        holder.gradeTextView.setText(challenge.getGrade());
        holder.timeTextView.setText(Double.toString(challenge.getTime()));
    }

    /**
     * returns the current size of the adapter items
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return challenges.size();
    }

    /**
     * a class that is the viewholder for the adapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView gradeTextView;
        private TextView timeTextView;

        /**
         * a constructor that has an onclick listener
         *
         * @param itemView
         */
        private ViewHolder(final View itemView) {
            super(itemView);
            ImageView rightIcon = (ImageView) itemView.findViewById(R.id.challenge_item_right_image_view);
            if (rightIcon != null) {
                rightIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
            gradeTextView = (TextView) itemView.findViewById(R.id.challenge_item_grade);
            timeTextView = (TextView) itemView.findViewById(R.id.challenge_item_time);
        }
    }
}