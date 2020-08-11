package com.eklavya.localhost;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Activity context;
    private List<Comment> mCommentList;

    CommentAdapter(Activity context, List<Comment> mCommentList){
        this.context = context;
        this.mCommentList = mCommentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        final Comment comment = mCommentList.get(position);
        holder.mFrom.setText(comment.getFrom());
        holder.mMessage.setText(comment.getMessage());
        holder.mFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("UserName", "Name");
                intent.putExtra("UserEmail", comment.getFrom());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView mFrom, mMessage;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            mFrom = itemView.findViewById(R.id.comment_layout_text_view_from);
            mMessage = itemView.findViewById(R.id.comment_layout_text_view_message);
        }
    }
}
