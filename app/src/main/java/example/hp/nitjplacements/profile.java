package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class profile extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    private DatabaseReference mDatabase;
    String authuserid;
    ValueEventListener postListener;
    String labels[]={"Name","Roll Number","Email","Placement","10th percentage","12th percentage","Current CGPA","Backlogs to be cleared","Contact number"} ;
    List<String> values;
    ListView l;

    Myadapter adapter;
// ...
@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    auth=FirebaseAuth.getInstance();
   l=(ListView) findViewById(R.id.mylist);
    ActionBar actionBar = getSupportActionBar();
  if(getIntent().getStringExtra("flag")==null) {

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Your Profile" + "</font>"));
        }
    }
    else if(getIntent().getStringExtra("flag").equals("yes"))
   {
       if (actionBar != null) {
           actionBar.setDisplayHomeAsUpEnabled(true);
           actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Student Profile" + "</font>"));
       }
   }
    mstate=new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if(auth.getCurrentUser()==null)
            {

                profile.this.finish();
            }
        }
    };
    dialog =new ProgressDialog(this);
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setMessage("loading ......");
    dialog.setCancelable(false);
    dialog.show();
    if(getIntent().getStringExtra("flag")==null) {
        authuserid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
    }
  else  if(getIntent().getStringExtra("flag").equals("yes"))
    {
        authuserid=getIntent().getStringExtra("email").replace("@","").replace(".","");
    }

    mDatabase = FirebaseDatabase.getInstance().getReference().child("students").child(authuserid);
    // profileadapter user=new profileadapter("9440803255",0,7.8,95,96.8,"Mopati Bharath","14103082",0,"No","blosterbharath@gmail.com");
     //mDatabase.setValue(user);
      //DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("students").child("vivekgmailcom");
    //   user=new profileadapter("9440878684",0,7,95,95,"Vivek Gupta","14103073",0,"No","vivek@gmail.com");
  // myref.setValue(user);
    //myref=FirebaseDatabase.getInstance().getReference().child("students").child("vamsigmailcom");
    //user=new profileadapter("9430878884",0,7,95,95,"vamsi","14103028",1,"No","vamsi@gmail.com");
  //  myref.setValue(user);


    values=new ArrayList<String>();
   adapter =new Myadapter(profile.this, android.R.layout.simple_list_item_1, values);
    l.setAdapter(adapter);
     postListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            profileadapter vuser=dataSnapshot.getValue(profileadapter.class);

            adapter.add(vuser.name);  adapter.add(vuser.rollno);adapter.add(vuser.email);adapter.add(vuser.placement); adapter.add(""+vuser.x); adapter.add(""+vuser.inter); adapter.add(""+vuser.cgpa); adapter.add(""+vuser.backlog); adapter.add(vuser.contact);
            if(dialog!=null)
            dialog.cancel();



        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            if(dialog!=null)
            dialog.cancel();
            // ...
        }
    };

    mDatabase.addValueEventListener(postListener);




    }
    class Myadapter extends ArrayAdapter<String> {
        public Myadapter(@NonNull Context context, @LayoutRes int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater myinflator= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myview =myinflator.inflate(R.layout.mycustomlist,parent,false);

            TextView t1= (TextView) myview.findViewById(R.id.tv2);
            TextView t2= (TextView) myview.findViewById(R.id.tv3);




            t1.setText(labels[position]);

            t2.setText(values.get(position));

            return myview ;



        }
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(mstate);


    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
        mDatabase.removeEventListener(postListener);
    }
}
