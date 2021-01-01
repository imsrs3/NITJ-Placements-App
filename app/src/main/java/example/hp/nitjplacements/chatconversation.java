package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class chatconversation extends AppCompatActivity {

    public RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef,myRef2;
    static String name;
    private FirebaseRecyclerAdapter<Show_Chat_Conversation_Data_Items, Chat_Conversation_ViewHolder> mFirebaseAdapter;
    public LinearLayoutManager mLinearLayoutManager;
    static String Sender_Name;


    ImageView attach_icon,send_icon,no_data_available_image;
    EditText message_area;
    TextView no_chat;

    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    ProgressBar progressBar;
    public static final int READ_EXTERNAL_STORAGE = 0,MULTIPLE_PERMISSIONS = 10;
    Uri mImageUri = Uri.EMPTY;

    private String pictureImagePath = "";
    final CharSequence[] options = {"Camera", "Gallery"};
    String[] permissions= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,};
    private FirebaseAuth auth;
    private static int Device_Width;
    FirebaseAuth.AuthStateListener mstate;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatconversation);
        auth=FirebaseAuth.getInstance();
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        Device_Width = metrics.widthPixels;

        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    chatconversation.this.finish();
                }
            }
        };

        String USER_ID = auth.getCurrentUser().getEmail();
        name= getIntent().getStringExtra("name");
        if(name==null)
        {
            name=USER_ID;
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
         myRef = FirebaseDatabase.getInstance().getReference().child("messages");
         // myRef.keepSynced(true);
            if(myRef==null)
        {
            onDestroy();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Chat room" + "</font>"));
        }
        //Sender_Name = getIntent().getStringExtra("name");
        recyclerView = (RecyclerView)findViewById(R.id.fragment_chat_recycler_view);
        attach_icon = (ImageView)findViewById(R.id.attachButton);
        send_icon = (ImageView)findViewById(R.id.sendButton);
        no_data_available_image = (ImageView)findViewById(R.id.no_data_available_image);
        message_area = (EditText)findViewById(R.id.messageArea);
        mProgressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar)findViewById(R.id.progressBar3);
        no_chat = (TextView)findViewById(R.id.no_chat_text);
        mLinearLayoutManager = new LinearLayoutManager(chatconversation.this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mLinearLayoutManager.setStackFromEnd(true);

        dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
        send_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message_area.getText().toString().isEmpty())
                {
                    return;
                }
                String messageText = message_area.getText().toString().trim();

                if(!messageText.equals("")){
                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("message", messageText);
                    map.put("sender", name);
                    myRef.push().setValue(map);

                    message_area.setText("");
                    recyclerView.postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);

                        }
                    }, 500);
                }
            }
        });
        attach_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(chatconversation.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(chatconversation.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                }
                else
                {
                    callgalary();
                }
            }
        });







    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callgalary();
                return;


        }
    }
    private void callgalary() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("LOGGED", "InSIDE onActivityResult : ");


        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mImageUri = data.getData();
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Chat_Images").child(mImageUri.getLastPathSegment());



            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();

                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("message", downloadUri.toString());
                    map.put("sender", name);
                    myRef.push().setValue(map);

                    mProgressDialog.dismiss();
                }
            });
        }



        else if (requestCode == 5)
        {
            Toast.makeText(this, "resultCode : "+ resultCode, Toast.LENGTH_SHORT).show();
        }
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                this.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Log.d("LOGGED", "On Start : " );
        auth.addAuthStateListener(mstate);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Show_Chat_Conversation_Data_Items, Chat_Conversation_ViewHolder>(Show_Chat_Conversation_Data_Items.class, R.layout.show_chat_conversation_single_item, Chat_Conversation_ViewHolder.class, myRef) {


            public void populateViewHolder(final Chat_Conversation_ViewHolder viewHolder, Show_Chat_Conversation_Data_Items model, final int position) {

                viewHolder.getSender(model.getSender());
                viewHolder.getMessage(model.getMessage());
                //Log.d("LOGGED", "Sender : " + model.getSender());
                //Log.d("LOGGED", "Message : " + model.getMessage());
                if(dialog!=null)
                    dialog.cancel();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {

                        final DatabaseReference ref = mFirebaseAdapter.getRef(position);
                        ref.keepSynced(true);
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String retrieve_image_url = dataSnapshot.child("message").getValue(String.class);
                                if(retrieve_image_url.startsWith("https"))
                                {
                                    //Toast.makeText(ChatConversationActivity.this, "URL : " + retrieve_image_url, Toast.LENGTH_SHORT).show();
                                    Intent intent = (new Intent(chatconversation.this,enlargeview.class));
                                    intent.putExtra("url",retrieve_image_url);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);





        myRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren())
                {
                    //Log.d("LOGGED", "Data SnapShot : " +dataSnapshot.toString());
                    progressBar.setVisibility(ProgressBar.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    no_data_available_image.setVisibility(View.GONE);
                    no_chat.setVisibility(View.GONE);
                    recyclerView.postDelayed(new Runnable() {
                        @Override public void run()
                        {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                        }
                    }, 500);
                    recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v,
                                                   int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                recyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                    }
                                }, 100);
                            }
                        }
                    });
                }
                else {
                    //Log.d("LOGGED", "NO Data SnapShot : " +dataSnapshot.toString());
                    if(dialog!=null)
                        dialog.cancel();
                    progressBar.setVisibility(ProgressBar.GONE);
                    recyclerView.setVisibility(View.GONE);
                    no_data_available_image.setVisibility(View.VISIBLE);
                    no_chat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static class Chat_Conversation_ViewHolder extends RecyclerView.ViewHolder {
        private final TextView message, sender;
        private final ImageView chat_image_incoming,chat_image_outgoing;
        View mView;
        final LinearLayout.LayoutParams params,text_params;
        LinearLayout layout;
        Context context;


        public Chat_Conversation_ViewHolder(final View itemView) {
            super(itemView);
            //Log.d("LOGGED", "ON Chat_Conversation_ViewHolder : " );
            mView = itemView;
            context=itemView.getContext();
            message = (TextView) mView.findViewById(R.id.fetch_chat_messgae);
            sender = (TextView) mView.findViewById(R.id.fetch_chat_sender);
            chat_image_incoming = (ImageView) mView.findViewById(R.id.chat_uploaded_image_incoming);
            chat_image_outgoing = (ImageView) mView.findViewById(R.id.chat_uploaded_image_outgoing);

            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            text_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout = (LinearLayout) mView.findViewById(R.id.chat_linear_layout);
        }

        private void getSender(String title) {


            if(title.equals(name))
            {
                //Log.d("LOGGED", "getSender: ");
                params.setMargins((chatconversation.Device_Width/3),5,10,10);
                text_params.setMargins(15,10,0,5);
                sender.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_outcoming_message);
                sender.setText("YOU");
                chat_image_outgoing.setVisibility(View.VISIBLE);
                chat_image_incoming.setVisibility(View.GONE);

            }
            else
            {
                params.setMargins(10,0,(chatconversation.Device_Width/3),10);
                sender.setGravity(Gravity.START);
                text_params.setMargins(60,10,0,5);
                sender.setLayoutParams(text_params);
                mView.setLayoutParams(params);
                mView.setBackgroundResource(R.drawable.shape_incoming_message);
                sender.setText(title);
                chat_image_outgoing.setVisibility(View.GONE);
                chat_image_incoming.setVisibility(View.VISIBLE);
            }
        }

        private void getMessage(String title) {

            if(!title.startsWith("https"))
            {

                if(sender.getText().equals("YOU"))
                {
                    text_params.setMargins(15,10,22,15);
                }
                else
                {
                    text_params.setMargins(65,10,22,15);
                }

                message.setLayoutParams(text_params);
                message.setText(title);
                message.setTextColor(Color.parseColor("#FFFFFF"));
                message.setVisibility(View.VISIBLE);
                chat_image_incoming.setVisibility(View.GONE);
                chat_image_outgoing.setVisibility(View.GONE);
            }
            else
            {
                if (chat_image_outgoing.getVisibility()==View.VISIBLE && chat_image_incoming.getVisibility()==View.GONE)
                {if(context!=null){
                    chat_image_outgoing.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(title)
                            .crossFade()
                            .fitCenter()
                            .placeholder(R.drawable.no_chat_available)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_outgoing);}
                }
                else
                {if(context!=null){
                    chat_image_incoming.setVisibility(View.VISIBLE);
                    message.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(title)
                            .crossFade()
                            .fitCenter()
                            .placeholder(R.drawable.no_chat_available)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(chat_image_incoming);}
                }
            }

        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Glide.clear(imageView);
        Glide.get(getApplicationContext()).clearMemory();
    }






    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);

    }

}

