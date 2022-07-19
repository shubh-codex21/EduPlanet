package com.example.eduplanet_e_learningapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eduplanet_e_learningapp.Activities.ShowingProfileActivity;
import com.example.eduplanet_e_learningapp.Activities.UserProfileActivity;
import com.example.eduplanet_e_learningapp.Modals.Message;
import com.example.eduplanet_e_learningapp.Modals.User;
import com.example.eduplanet_e_learningapp.R;
import com.example.eduplanet_e_learningapp.databinding.DiscussReceivedMessagesItemviewBinding;
import com.example.eduplanet_e_learningapp.databinding.DiscussSentMessagesItemviewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class DiscussAdapter extends RecyclerView.Adapter {
    ArrayList<Message> messages;
    Context context;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVED = 2;

    public DiscussAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.discuss_sent_messages_itemview, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.discuss_received_messages_itemview, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getMessagedBy())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;

            if (message.getMessageType() != null && message.getMessageType().equals("image")) {
                viewHolder.binding.sendedImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.sendedImage);
            } else {
                viewHolder.binding.sendedImage.setVisibility(View.GONE);
            }

            if (message.getMessageType() != null && message.getMessageType().equals("image") && !message.getMessage().equals("")) {
                viewHolder.binding.sendingMessage.setVisibility(View.VISIBLE);
                viewHolder.binding.sendingMessage.setText(message.getMessage());
            }
            else if (message.getMessageType() != null && message.getMessageType().equals("text")) {
                viewHolder.binding.sendingMessage.setVisibility(View.VISIBLE);
                viewHolder.binding.sendingMessage.setText(message.getMessage());
            }
                else {
                viewHolder.binding.sendingMessage.setVisibility(View.GONE);
            }

//            viewHolder.binding.messagedTime.setText((int) message.getMessagedAt()+"");

            FirebaseFirestore.getInstance().collection("User").document(message.getMessagedBy())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                            if (value.exists()) {
                                User user = value.toObject(User.class);
                                Glide.with(context).load(user.getUserImage()).into(viewHolder.binding.sendedUserImage);
                                viewHolder.binding.sendedUserName.setText(user.getUsername());
                            }
                        }
                    });

                viewHolder.binding.sendedUserImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            context.startActivity(new Intent(context, UserProfileActivity.class));
                    }
                });

                viewHolder.binding.sendedUserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, UserProfileActivity.class));
                    }
                });

        } else {
            ReceivedViewHolder viewHolder = (ReceivedViewHolder) holder;

            if (message.getMessageType() != null) {

                if (message.getMessageType().equals("image")) {
                    viewHolder.binding.receivedImage.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getImageUrl()).into(viewHolder.binding.receivedImage);
                } else {
                    viewHolder.binding.receivedImage.setVisibility(View.GONE);
                }

                if (message.getMessageType().equals("image") && !message.getMessage().equals("")) {
                    viewHolder.binding.receivedMessage.setVisibility(View.VISIBLE);
                    viewHolder.binding.receivedMessage.setText(message.getMessage());
                } else if (message.getMessageType().equals("text")) {
                    viewHolder.binding.receivedMessage.setVisibility(View.VISIBLE);
                    viewHolder.binding.receivedMessage.setText(message.getMessage());
                } else {
                    viewHolder.binding.receivedMessage.setVisibility(View.GONE);
                }
            }

//            viewHolder.binding.messagedTime.setText((int) message.getMessagedAt()+"");

            FirebaseFirestore.getInstance().collection("User").document(message.getMessagedBy())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(DocumentSnapshot value, FirebaseFirestoreException error) {
                            if (value.exists()) {
                                User user = value.toObject(User.class);
                                Glide.with(context).load(user.getUserImage()).into(viewHolder.binding.messagedUserImage);
                                viewHolder.binding.messageUserName.setText(user.getUsername());
                                Random rnd = new Random();
                                int color = Color.argb(255, rnd.nextInt(256),
                                        rnd.nextInt(256), rnd.nextInt(256));
                                viewHolder.binding.messageUserName.setTextColor(color);
                            }
                        }
                    });

            viewHolder.binding.messagedUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (message.getMessagedBy().equals(FirebaseAuth.getInstance().getUid())){
                        context.startActivity(new Intent(context, UserProfileActivity.class));
                    } else {
                        Intent intent = new Intent(context, ShowingProfileActivity.class);
                        intent.putExtra("profileId",message.getMessagedBy());
                        context.startActivity(intent);
                    }
                }
            });

            viewHolder.binding.messageUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (message.getMessagedBy().equals(FirebaseAuth.getInstance().getUid())){
                        context.startActivity(new Intent(context, UserProfileActivity.class));
                    } else {
                        Intent intent = new Intent(context, ShowingProfileActivity.class);
                        intent.putExtra("profileId",message.getMessagedBy());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class SentViewHolder extends RecyclerView.ViewHolder {
        DiscussSentMessagesItemviewBinding binding;

        public SentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = DiscussSentMessagesItemviewBinding.bind(itemView);
        }
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder {
        DiscussReceivedMessagesItemviewBinding binding;

        public ReceivedViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = DiscussReceivedMessagesItemviewBinding.bind(itemView);
        }
    }
}
