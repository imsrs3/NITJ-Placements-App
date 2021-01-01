package example.hp.nitjplacements;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class changeuserdetails extends AppCompatActivity {
    EditText editpassword,editcontact;
    Button changepassword,changecontact;
    TextView show;
    private FirebaseAuth auth;
    String uname;
    DatabaseReference ref;
    ValueEventListener mlis;
    FirebaseAuth.AuthStateListener mstate;


    private FirebaseDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeuserdetails);
        auth= FirebaseAuth.getInstance();
        editpassword= (EditText) findViewById(R.id.editpassword);
        editcontact= (EditText) findViewById(R.id.editcontact);
        changepassword= (Button) findViewById(R.id.changepassword);
        changecontact= (Button) findViewById(R.id.changecontact);
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    changeuserdetails.this.finish();
                }
            }
        };


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" +"Settings" + "</font>"));
        }

        show= (TextView) findViewById(R.id.show);

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
            String userid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
            ref=dref.child("students").child(userid);
            ref.addValueEventListener(mlis=new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                  profileadapter  user= dataSnapshot.getValue(profileadapter.class);
                    show.setText("welcome " +user.name);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editpassword.getText().toString().isEmpty())
                {
                    Toast.makeText(changeuserdetails.this, "please fill  the Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                changepass(editpassword.getText().toString());


            }
        });
        changecontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editcontact.getText().toString().isEmpty())
                {Toast.makeText(changeuserdetails.this, "please fill  the Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
                String userid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
                dref.child("students").child(userid).child("contact").setValue(editcontact.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(changeuserdetails.this, "contact number changed", Toast.LENGTH_SHORT).show();
                        editcontact.setText("");
                    }
                });
            }
        });



    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void changepass(String pass)
    {
        auth.getCurrentUser().updatePassword(pass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(changeuserdetails.this,"password change succcesful", Toast.LENGTH_SHORT).show();
                    editpassword.setText("");
                }else
                {
                    Toast.makeText(changeuserdetails.this,"password change unsucccesful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mstate);
    }
    @Override
    protected void onStop() {
        super.onStop();
if(mlis!=null)
{
    ref.removeEventListener(mlis);
}
        auth.removeAuthStateListener(mstate);
    }
}
