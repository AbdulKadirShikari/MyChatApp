package com.example.mychatapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public MessageAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList=userMessagesList;
    }




    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText,receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverpicture;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText =(TextView)itemView.findViewById(R.id.sender_message_text);
            receiverMessageText =(TextView)itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage =(CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverpicture=itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture=itemView.findViewById(R.id.message_sender_image_view);
        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout,parent,false);
        mAuth=FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {
      String  messageSenderId=mAuth.getCurrentUser().getUid();
      Messages messages=userMessagesList.get(position);

      String fromUserID=messages.getFrom();
      String fromMessageType=messages.getType();

      usersRef= FirebaseDatabase.getInstance().getReference().child("users").child(fromUserID);

      usersRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot)
          {
              if(dataSnapshot.hasChild("image"))
              {
                  String receiverImage=dataSnapshot.child("image").getValue().toString();
                  Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(holder.receiverProfileImage);
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError)
          {

          }
      });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverpicture.setVisibility(View.GONE);



        if(fromMessageType.equals("text"))
      {


          if (fromUserID.equals(messageSenderId)) {
              holder.senderMessageText.setVisibility(View.VISIBLE);
              holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
              holder.senderMessageText.setTextColor(Color.BLACK);
              holder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate() );
          } else {

              holder.receiverProfileImage.setVisibility(View.VISIBLE);
              holder.receiverMessageText.setVisibility(View.VISIBLE);

              holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
              holder.receiverMessageText.setTextColor(Color.BLACK);
              holder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
          }
      }
       else if(fromMessageType.equals("image"))
        {
            if(fromUserID.equals(messageSenderId))
            {
              holder.messageSenderPicture.setVisibility(View.VISIBLE);
              Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverpicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverpicture);
            }
        }
       else if(fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {
            if(fromUserID.equals(messageSenderId))
            {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

              Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/mychatapp-1c4e3.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=3b16c3ff-8bd3-472c-9979-835b0dd90e96").into(holder.messageSenderPicture);
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverpicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/mychatapp-1c4e3.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=3b16c3ff-8bd3-472c-9979-835b0dd90e96").into(holder.messageReceiverpicture);

            }
        }


       if(fromUserID.equals(messageSenderId))
       {
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view)
               {
                   if(userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx"))
                   {
                       CharSequence options[] = new CharSequence[]
                               {
                                       "Delete for me",
                                       "Downlaod and View This Document",
                                       "Cancel",
                                       "Delete for Everyone"
                               };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("delete Message");

                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i)
                           {
                                if(i==0)
                                {
                                deleteSentMessage(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                               if(i==1)
                               {
                                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                   holder.itemView.getContext().startActivity(intent);

                               }

                               if(i==3)
                               {
                                  deleteMessageForEveryone(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);
                               }
                           }
                       });
                       builder.show();
                   }
                  else if(userMessagesList.get(position).getType().equals("text") )
                   {
                       CharSequence options[] = new CharSequence[]
                               {
                                       "Delete for me",
                                       "Cancel",
                                       "Delete for Everyone"
                               };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("delete Message");

                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i)
                           {
                               if(i==0)
                               {
                                  deleteSentMessage(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);

                               }
                               if(i==2)
                               {
                                 deleteMessageForEveryone(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);
                               }
                           }
                       });
                       builder.show();
                   }
                  else if(userMessagesList.get(position).getType().equals("image"))
                   {
                       CharSequence options[] = new CharSequence[]
                               {
                                       "Delete for me",
                                       " View This Image",
                                       "Cancel",
                                       "Delete for Everyone"
                               };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("delete Message");

                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i)
                           {
                               if(i==0)
                               {
                               deleteSentMessage(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);

                               }
                               if(i==1)
                               {
                                   Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                                   intent.putExtra("url",userMessagesList.get(position).getMessage());
                                   holder.itemView.getContext().startActivity(intent);
                               }

                               if(i==3)
                               {
                                 deleteMessageForEveryone(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);
                               }
                           }
                       });
                       builder.show();
                   }
               }
           });
       }
       else
       {
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view)
               {
                   if(userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx"))
                   {
                       CharSequence options[] = new CharSequence[]
                               {
                                       "Delete for me",
                                       "Downlaod and View This Document",
                                       "Cancel"
                               };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("delete Message");

                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i)
                           {
                               if(i==0)
                               {

                                deleteReceiveMessage(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);
                               }
                               if(i==1)
                               {
                                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                                   holder.itemView.getContext().startActivity(intent);

                               }

                           }
                       });
                       builder.show();
                   }
                   else if(userMessagesList.get(position).getType().equals("text") )
                   {
                       CharSequence options[] = new CharSequence[]
                               {
                                       "Delete for me",
                                       "Cancel"
                               };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("delete Message");

                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i)
                           {
                               if(i==0)
                               {
                                     deleteReceiveMessage(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);

                               }

                           }
                       });
                       builder.show();
                   }
                   else if(userMessagesList.get(position).getType().equals("image"))
                   {
                       CharSequence options[] = new CharSequence[]
                               {
                                       "Delete for me",
                                       " View This Image",
                                       "Cancel"
                               };
                       AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                       builder.setTitle("delete Message");

                       builder.setItems(options, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i)
                           {
                               if(i==0)
                               {
                                 deleteReceiveMessage(position,holder);
                                   Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                   holder.itemView.getContext().startActivity(intent);

                               }
                               if(i==1)
                               {
                                 Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                                       intent.putExtra("url",userMessagesList.get(position).getMessage());
                                       holder.itemView.getContext().startActivity(intent);

                               }


                           }
                       });
                       builder.show();
                   }
               }
           });

       }

    }

    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

private  void deleteSentMessage(final int position, final MessageViewHolder holder)
{
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    rootRef.child("Messages")
            .child(userMessagesList.get(position).getFrom())
            .child(userMessagesList.get(position).getTo())
            .child(userMessagesList.get(position).getMessageID())
            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task)
        {
            if(task.isSuccessful())
            {
                Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        }
    });
}
    private  void deleteReceiveMessage(final int position, final MessageViewHolder holder)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private  void deleteMessageForEveryone(final int position, final MessageViewHolder holder)
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    rootRef.child("Messages")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                          if(task.isSuccessful())
                          {
                              Toast.makeText(holder.itemView.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                          }
                        }
                    });

                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
