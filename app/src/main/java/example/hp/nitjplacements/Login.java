package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class Login extends AppCompatActivity {
    EditText email,password;
    TextView signup,forgot;
    Button login;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    String authuserid;
    ValueEventListener studentListener;
    FirebaseAuth.AuthStateListener mstate;
    int pr;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();


        login= (Button) findViewById(R.id.signin);

        forgot= (TextView) findViewById(R.id.forgot);
        email= (EditText) findViewById(R.id.email);
        password= (EditText) findViewById(R.id.password);

        studentListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileadapter vuser=dataSnapshot.getValue(profileadapter.class);

               pr=vuser.pr;
               if(pr==1)
               {
                   startActivity(new Intent(Login.this,prhome.class));
               }else
                   startActivity(new Intent(Login.this,home.class));



                if(dialog!=null)
                    dialog.cancel();
                Login.this.finish();

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
                if(auth.getCurrentUser()!=null)
                {  if(dialog==null)
                {

                    dialog =new ProgressDialog(Login.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setMessage("loading ......");
                    dialog.setCancelable(false);
                    dialog.show();
                }

                    authuserid=auth.getCurrentUser().getEmail().replace("@","").replace(".","");
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("students").child(authuserid);
                    mDatabase.addValueEventListener(studentListener);

                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()||password.getText().toString().isEmpty())
                {Toast.makeText(Login.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginuser(email.getText().toString(),password.getText().toString());

            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this,forgotpasswordActivity.class));


            }
        });





    }
    public void loginuser(String user, final String password1)
    { dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
        auth.signInWithEmailAndPassword(user,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful())
                {    dialog.cancel();

                    if(password1.length()<6)
                    {
                        Toast.makeText(Login.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    }else
                    Toast.makeText(Login.this,"Login failed.Please try again", Toast.LENGTH_SHORT).show();
                }else
                {//dialog.cancel();

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

        auth.removeAuthStateListener(mstate);
        if(mDatabase!=null)
        mDatabase.removeEventListener(studentListener);
    }
}
