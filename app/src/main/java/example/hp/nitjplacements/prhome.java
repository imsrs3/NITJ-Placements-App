package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class prhome extends AppCompatActivity {
    private FirebaseAuth auth;
    ProgressDialog dialog;
    private DatabaseReference mDatabase;
    String authuserid;
    ValueEventListener postListener;
    String name;
    FirebaseAuth.AuthStateListener mstate;
    Button t1,t2,t3,t4,t5;
    profileadapter vuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prhome);
        setTitle("PR home");

        auth= FirebaseAuth.getInstance();
        t1=(Button)findViewById(R.id.allcompanies);
        t2=(Button)findViewById(R.id.prupcomingcompanies);
        t3=(Button)findViewById(R.id.prpreviouscompanies);
        t4=(Button)findViewById(R.id.chatroom);
        t5=(Button)findViewById(R.id.studentzone);
        dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
        authuserid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("students").child(authuserid);
        postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 vuser=dataSnapshot.getValue(profileadapter.class);
name=vuser.name;

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

        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {
                    startActivity(new Intent(prhome.this,Login.class));
                    prhome.this.finish();
                }
            }
        };


        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(prhome.this,Allcompanies.class));

            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(prhome.this,prupcomingcompanies.class));

            }
        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent=new Intent(prhome.this,chatconversation.class);
               intent.putExtra("name",name);
                startActivity(intent);
            }
        });
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(prhome.this,prpreviouscompanies.class));
            }
        });
        t5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(prhome.this,studentzone.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.changedetails)
        {
            startActivity(new Intent(prhome.this,changeuserdetails.class));

        }else if(item.getItemId()==R.id.userssignout)
        {     auth.signOut();


        }else if(item.getItemId()==R.id.profile)
        {
            startActivity(new Intent(prhome.this,profile.class));
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mstate);
        mDatabase.addValueEventListener(postListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
        mDatabase.removeEventListener(postListener);
    }
}
