package com.howaboutthis.satyaraj.skive;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class SafeBunkingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<SafeBunkingListItem> mListItem;
    private float requiredPercentage;

    SafeBunkingAdapter(List<SafeBunkingListItem> listItem, float requiredPercentage) {
        this.mListItem = listItem;
        this.requiredPercentage = requiredPercentage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.safe_bunking_card_view,parent,false);
        return new ViewItem(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SafeBunkingListItem listItemView = mListItem.get(position);
        final ViewItem viewItem = (ViewItem) holder;

        @SuppressLint("DefaultLocale")
        String formattedValue = String.format("%.1f", listItemView.getSubjectPercentage());

        viewItem.subjectNameTextView.setText(listItemView.getSubjectName());
        if (requiredPercentage > listItemView.getSubjectPercentage()) {
            viewItem.subjectPercentageTextView.setTextColor(Color.RED);
            viewItem.subjectNameTextView.setTextColor(Color.RED);
            viewItem.comment.setTextColor(Color.RED);
            viewItem.viewDivider.setBackgroundColor(Color.RED);
        }



        viewItem.subjectPercentageTextView.setText(String.valueOf(formattedValue+"%"));

        viewItem.comment.setText(listItemView.getComment());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());

                float ifBunked = ((float) listItemView.getAttended()/(float) (listItemView.getTotal()+1))*100;
                float ifAttended = ((float) listItemView.getAttended()+1)/(float)(listItemView.getTotal()+1)*100;

                @SuppressLint("DefaultLocale")
                String bunked = String.format("%.1f", ifBunked);

                @SuppressLint("DefaultLocale")
                String attended = String.format("%.1f", ifAttended);

                alertbox.setMessage("If you attend the percentage will increase to "+attended+"%\n\n"+ "But if you bunk then it will decrease to "
                + bunked+"%.");
                alertbox.setTitle("What's going to happen to "+listItemView.getSubjectName()+" next.");
                alertbox.setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                            }
                        });

                alertbox.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    private class ViewItem extends RecyclerView.ViewHolder{
        TextView subjectNameTextView;
        TextView subjectPercentageTextView;
        TextView comment;
        View viewDivider;
        ViewItem(View itemView){
            super(itemView);

            subjectNameTextView = itemView.findViewById(R.id.subject_name);
            subjectPercentageTextView = itemView.findViewById(R.id.subject_percentage);
            comment = itemView.findViewById(R.id.comment);
            viewDivider = itemView.findViewById(R.id.view);
        }
    }
}
