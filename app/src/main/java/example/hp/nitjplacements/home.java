package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    Button t1,t2,t3,t4;
    ProgressDialog dialog;
    private DatabaseReference mDatabase;
    String authuserid;
    ValueEventListener postListener;
    String name;
    profileadapter vuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Student home");
        auth=FirebaseAuth.getInstance();
        t1=(Button)findViewById(R.id.ucompanies);
        t2=(Button)findViewById(R.id.pcompanies);
        t3=(Button)findViewById(R.id.prinfo);
        t4=(Button)findViewById(R.id.chat);
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
                    startActivity(new Intent(home.this,Login.class));
                    home.this.finish();
                }
            }
        };


        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(home.this,upcomingcompanies.class));

            }
        });
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(home.this,studentpreviouscompanies .class);

                startActivity(intent);
            }
        });

        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(home.this,prinfo.class));
            }
        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(home.this,chatconversation.class);
                intent.putExtra("name",name);
                startActivity(intent);
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
            startActivity(new Intent(home.this,changeuserdetails.class));

        }else if(item.getItemId()==R.id.userssignout)
        {     auth.signOut();


        }else if(item.getItemId()==R.id.profile)
        {
            startActivity(new Intent(home.this,profile.class));
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
