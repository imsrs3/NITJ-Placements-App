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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addcompany extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    EditText name,link,info,number,email,person;
    Button submit;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcompany);
        auth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("allcompanies");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Add a Company " + "</font>"));
        }
        name= (EditText) findViewById(R.id.companyname);
        link= (EditText) findViewById(R.id.companylink);
        info= (EditText) findViewById(R.id.companyinfo);
        number= (EditText) findViewById(R.id.contactnumber);
        email= (EditText) findViewById(R.id.contactemail);
        person= (EditText) findViewById(R.id.contactperson);
        submit= (Button) findViewById(R.id.companysubmit);
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    addcompany.this.finish();
                }
            }
        };
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=name.getText().toString().trim();
                String Link=link.getText().toString().trim();
                String Info=info.getText().toString().trim();
                String Number=number.getText().toString().trim();
                String Email=email.getText().toString().trim();
                String Person=person.getText().toString().trim();
                if(Name.isEmpty()||Link.isEmpty()||Info.isEmpty()||Number.isEmpty()||Email.isEmpty()||Person.isEmpty())
                {
                    Toast.makeText(addcompany.this, "please fill  the Fields", Toast.LENGTH_SHORT).show();
                    return;
                }else
                {
                    Allcompaniesadapter company=new Allcompaniesadapter(Name,Link,Info,Person,Number,Email);

                    mDatabase.push().setValue(company).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(addcompany.this,"Company added successfully", Toast.LENGTH_SHORT).show();
                                name.setText("");
                                link.setText("");
                                info.setText("");
                                number.setText("");
                                email.setText("");
                                person.setText("");
                            }else
                            {
                                Toast.makeText(addcompany.this,"Company is not added .Please try Again", Toast.LENGTH_SHORT).show();
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
