package example.hp.nitjplacements;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addpreviouscompany extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    EditText name,link,info,ctc,date,position;
    Button submit;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpreviouscompany);
        auth= FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("previouscompanies");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Add previousCompany " + "</font>"));
        }
        name= (EditText) findViewById(R.id.addpreviouscompanyname);
        link=(EditText) findViewById(R.id.addpreviouscompanylink);
        info=(EditText) findViewById(R.id.addpreviouscompanyinfo);
        ctc=(EditText) findViewById(R.id.addpreviouscompanyctc);
        date=(EditText) findViewById(R.id.addpreviouscompanydate);
        position=(EditText) findViewById(R.id.addpreviouscompanyposition);
        submit= (Button) findViewById(R.id.addpreviouscompanysubmit);
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    addpreviouscompany.this.finish();
                }
            }
        };
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name=name.getText().toString().trim();
                String Link=link.getText().toString().trim();
                String Info=info.getText().toString().trim();
                String Ctc=ctc.getText().toString().trim();
                String Date=date.getText().toString().trim();
                String Position=position.getText().toString().trim();
                if(Name.isEmpty()||Link.isEmpty()||Info.isEmpty()||Ctc.isEmpty()||Position.isEmpty()||Date.isEmpty())
                {
                    Toast.makeText(addpreviouscompany.this, "please fill  the Fields", Toast.LENGTH_SHORT).show();
                    return;
                }else
                {
                    previouscompaniesadapter company=new previouscompaniesadapter(Name,Position, Double.parseDouble(Ctc.trim()),Link,Info,Date);

                    mDatabase.push().setValue(company).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(addpreviouscompany.this,"Company added successfully", Toast.LENGTH_SHORT).show();
                                name.setText("");
                                link.setText("");
                                info.setText("");
                                ctc.setText("");
                                position.setText("");
                                date.setText("");
                            }else
                            {
                                Toast.makeText(addpreviouscompany.this,"Company is not added .Please try Again", Toast.LENGTH_SHORT).show();
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
