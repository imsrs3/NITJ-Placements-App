package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class prinfo extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    private DatabaseReference mDatabase,myref;
    String authuserid;
    ValueEventListener postListener,prlis;
    String labels[]={"Name","Roll Number","Email","Contact number"} ;
    List<String> values;
    ListView l;

    prinfo.Myadapter adapter;
    // ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prinfo);

        auth=FirebaseAuth.getInstance();
        l=(ListView) findViewById(R.id.mylist);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "PR information" + "</font>"));
        }

        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    prinfo.this.finish();
                }
            }
        };
        dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
       myref = FirebaseDatabase.getInstance().getReference().child("prinfo");
      // myref.setValue("vamsigmailcom");
        myref.addValueEventListener(prlis=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                authuserid=dataSnapshot.getValue(String.class);
                mDatabase = FirebaseDatabase.getInstance().getReference().child("students").child(authuserid);
                mDatabase.addValueEventListener(postListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        values=new ArrayList<String>();
        adapter =new prinfo.Myadapter(prinfo.this, android.R.layout.simple_list_item_1, values);
        l.setAdapter(adapter);
        postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileadapter vuser=dataSnapshot.getValue(profileadapter.class);

                adapter.add(vuser.name);  adapter.add(vuser.rollno);adapter.add(vuser.email); adapter.add(vuser.contact);
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
        myref.removeEventListener(prlis);
        mDatabase.removeEventListener(postListener);
    }
}
