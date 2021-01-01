package example.hp.nitjplacements;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class updatestudents extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    EditText name,rollno,contact,backlog,email,cgpa,x,inter;
    RadioGroup placementgroup,prgroup;
    RadioButton plradio,prradio;
    Button update;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatestudents);
        auth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("students");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Update Student info " + "</font>"));
        }
        name= (EditText) findViewById(R.id.name);
        email= (EditText) findViewById(R.id.email);
        contact= (EditText) findViewById(R.id.contactnumber);
        rollno= (EditText) findViewById(R.id.rollno);
        backlog= (EditText) findViewById(R.id.backlogs);
        x= (EditText) findViewById(R.id.xpercentage);
        inter= (EditText) findViewById(R.id.interpercentage);
        cgpa= (EditText) findViewById(R.id.cgpa);
        update= (Button) findViewById(R.id.updatestudentsubmit);
        placementgroup= (RadioGroup) findViewById(R.id.placementgroup);
        prgroup= (RadioGroup) findViewById(R.id.prgroup);
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    updatestudents.this.finish();
                }
            }
        };

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=name.getText().toString().trim();
                String Rollno=rollno.getText().toString().trim();
                String Email=email.getText().toString().trim();
                String Contact=contact.getText().toString().trim();
                String Backlog=backlog.getText().toString().trim();
                String X=x.getText().toString().trim();
                String Inter=inter.getText().toString().trim();
                String Cgpa=cgpa.getText().toString().trim();
                int plid,prid;
                plid=placementgroup.getCheckedRadioButtonId();
                prid=prgroup.getCheckedRadioButtonId();

                if(Name.isEmpty()||Rollno.isEmpty()||Email.isEmpty()||Contact.isEmpty()||Backlog.isEmpty()||X.isEmpty()||Inter.isEmpty()||Cgpa.isEmpty()||plid==-1||prid==-1)
                {
                    Toast.makeText(updatestudents.this, "please fill  the Fields", Toast.LENGTH_SHORT).show();
                    return;
                }else
                {plradio= (RadioButton) findViewById(plid);
                    prradio= (RadioButton) findViewById(prid);
                    String Placement=plradio.getText()+"";
                    if(Placement.equals("Dream"))
                    {
                        Placement="dream";
                    }else if(Placement.equals("Non dream"))
                    {
                     Placement="nondream";
                    }else
                    {
                        Placement="No";
                    }  int Pr=0;
                    String PR=prradio.getText()+"";
                    if(PR.equals("Yes"))
                    {
                        Pr=1;
                    }else if(PR.equals("No"))
                    {
                        Pr=0;
                    }

                    profileadapter user=new profileadapter(Contact,Integer.parseInt(Backlog),Double.parseDouble(Cgpa),Double.parseDouble(X),Double.parseDouble(Inter),Name,Rollno,Pr,Placement,Email);
                    String userid=Email.replace("@","").replace(".","");

                    mDatabase.child(userid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(updatestudents.this,"Student Info updated ", Toast.LENGTH_SHORT).show();
                                name.setText("");
                                email.setText("");
                                contact.setText("");
                                rollno.setText("");
                                x.setText("");
                                inter.setText("");
                                cgpa.setText("");
                                backlog.setText("");
                                plradio.setChecked(false);
                                prradio.setChecked(false);
                            }else
                            {
                                Toast.makeText(updatestudents.this,"Student Info not updated.Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });



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
    }
}
