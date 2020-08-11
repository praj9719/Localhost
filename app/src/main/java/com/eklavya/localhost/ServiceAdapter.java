package com.eklavya.localhost;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ImageViewHolder> {
    private Activity context;
    private List<Host> eventList;
    private boolean showMore = true;
    private DatabaseReference mDatabaseReference;
    private boolean mGreat = false, mGood = false, mOk = false;
    ServiceAdapter(Activity context, List<Host> eventList){
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(context).inflate(R.layout.host_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position)   {
        final Host host = eventList.get(position);
        final Host editHost = eventList.get(position);
        holder.mTitle.setText(host.getTitle());
        holder.mCategory.setText(host.getCategory());
        holder.mFrom.setText(host.getFrom());
        holder.mDetails.setText(host.getDetails());
        holder.mLink.setText(host.getLink());
        holder.mContact.setText(host.getContact());
        holder.mGreatStar.setText(String.valueOf(host.getGreat()));
        holder.mGoodStar.setText(String.valueOf(host.getGood()));
        holder.mOkStar.setText(String.valueOf(host.getOk()));
        Picasso.with(context)
                .load(host.getImgUri())
                .centerCrop()
                .placeholder(R.drawable.snail)
                .fit()
                .into(holder.mImageView);
        holder.mDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationLink = "https://www.google.com/maps/search/?api=1&query="+host.getLatitude()+","+host.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationLink));
                context.startActivity(intent);
            }
        });
        holder.mLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(host.getLink()));
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.mContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+host.getContact()));
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showMore){
                    holder.mLinearLayout.setVisibility(View.VISIBLE);
                    holder.mMore.setText("hide");
                    showMore = false;
                }else {
                    holder.mLinearLayout.setVisibility(View.GONE);
                    holder.mMore.setText("more");
                    showMore = true;
                }
            }
        });
        holder.mGreatStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGreat){
                    holder.mGreatStar.setText(String.valueOf(host.getGreat()+1));
                    holder.mGreatStar.setTextColor(Color.DKGRAY);
                    if (mGood){
                        holder.mGoodStar.setText(String.valueOf(host.getGood()-1));
                        editHost.setGood(host.getGood()-1);
                        mGood = !mGood;
                    }
                    else if (mOk){
                        holder.mOkStar.setText(String.valueOf(host.getOk()-1));
                        editHost.setOk(host.getOk()-1);
                        mOk = !mOk;
                    }

                    editHost.setGreat(editHost.getGreat()+1);
                    mDatabaseReference.child(host.getId()).setValue(editHost);
                    mGreat = !mGreat;
                }
            }
        });
        holder.mGoodStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGood){
                    holder.mGoodStar.setText(String.valueOf(host.getGood()+1));
                    holder.mGoodStar.setTextColor(Color.DKGRAY);
                    if (mGreat){
                        holder.mGreatStar.setText(String.valueOf(host.getGreat()-1));
                        editHost.setGreat(host.getGreat()-1);
                        mGreat = !mGreat;
                    }
                    else if (mOk){
                        holder.mOkStar.setText(String.valueOf(host.getOk()-1));
                        editHost.setOk(host.getOk()-1);
                        mOk = !mOk;
                    }
                    editHost.setGood(editHost.getGood()+1);
                    mDatabaseReference.child(host.getId()).setValue(editHost);
                    mGood = !mGood;
                }
            }
        });
        holder.mOkStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mOk){
                    holder.mOkStar.setText(String.valueOf(host.getOk()+1));
                    holder.mOkStar.setTextColor(Color.DKGRAY);
                    if (mGreat){
                        holder.mGreatStar.setText(String.valueOf(host.getGreat()-1));
                        editHost.setGreat(host.getGreat()-1);
                        mGreat = !mGreat;
                    }
                    else if (mGood){
                        holder.mGoodStar.setText(String.valueOf(host.getGood()-1));
                        editHost.setGood(host.getGood()-1);
                        mGood = !mGood;
                    }
                    editHost.setOk(editHost.getOk()+1);
                    mDatabaseReference.child(host.getId()).setValue(editHost);
                    mOk = !mOk;
                }
            }
        });
        holder.mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("HostId", host.getId());
                intent.putExtra("Title", host.getTitle());
                intent.putExtra("Details", host.getDetails());
                context.startActivity(intent);
            }
        });
        holder.mFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("UserName", "Name");
                intent.putExtra("UserEmail", host.getFrom());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle, mCategory, mFrom, mDetails, mLink, mContact, mDirection, mComment;
        public LinearLayout mLinearLayout;
        public ImageView mImageView;
        public TextView mGreatStar, mGoodStar, mOkStar, mMore;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.host_layout_text_view_title);
            mCategory = itemView.findViewById(R.id.host_layout_text_view_category);
            mFrom = itemView.findViewById(R.id.host_layout_text_view_from);
            mDetails = itemView.findViewById(R.id.host_layout_text_view_details);
            mLink = itemView.findViewById(R.id.host_layout_text_view_link);
            mContact = itemView.findViewById(R.id.host_layout_text_view_contact);
            mDirection = itemView.findViewById(R.id.host_layout_text_view_direction);
            mImageView = itemView.findViewById(R.id.host_layout_image_view);
            mLinearLayout = itemView.findViewById(R.id.host_layout_linear_layout);
            mGreatStar = itemView.findViewById(R.id.host_layout_text_view_one_star);
            mGoodStar = itemView.findViewById(R.id.host_layout_text_view_half_star);
            mOkStar = itemView.findViewById(R.id.host_layout_text_view_zero_star);
            mMore = itemView.findViewById(R.id.host_layout_text_view_more);
            mComment = itemView.findViewById(R.id.host_layout_text_view_comment);
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Service");
        }
    }
}
