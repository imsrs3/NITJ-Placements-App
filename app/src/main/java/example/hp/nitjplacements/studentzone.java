package example.hp.nitjplacements;

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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class studentzone extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    EditText cg;
    RadioGroup cggroup,placementgroup;
    RadioButton cgradio,placementradio;
    Button filter1,filter2,filter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentzone);
        auth= FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Student Zone " + "</font>"));
        } mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    studentzone.this.finish();
                }
            }
        };
        cg= (EditText) findViewById(R.id.cgedittext);
        filter1= (Button) findViewById(R.id.cgbutton);
        filter2= (Button) findViewById(R.id.placementbutton);
        filter3= (Button) findViewById(R.id.allstudents);
        cggroup= (RadioGroup) findViewById(R.id.cggroup);
        placementgroup= (RadioGroup) findViewById(R.id.placementgroup);
        filter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cgpa=cg.getText().toString().trim();
                if(cgpa.equals("")||cggroup.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(studentzone.this, "Please fill the fields ", Toast.LENGTH_SHORT).show();
                }else
                {   int selectedid= cggroup.getCheckedRadioButtonId();
                cgradio= (RadioButton) findViewById(selectedid);
                String sort=cgradio.getText()+"";

                    double cgpa1=Double.parseDouble(cgpa);
                    Intent intent=new Intent(studentzone.this,filterstudents.class);
                    intent.putExtra("flag",1);
                    intent.putExtra("sort",sort);
                    intent.putExtra("cgpa",cgpa1);
                    cg.setText("");
                    cgradio.setChecked(false);
                    startActivity(intent);

                }
            }
        });
        filter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(placementgroup.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(studentzone.this, "Please pick an option ", Toast.LENGTH_SHORT).show();
                }else
                {
                    int selectedid= placementgroup.getCheckedRadioButtonId();
                    placementradio= (RadioButton) findViewById(selectedid);
                    String place=placementradio.getText()+"";
                    if(place.equals("Not placed"))
                    {place="No";

                    }else if(place.equals("Dream"))
                    {
                      place="dream";
                    }
                    else if(place.equals("Non dream"))
                    {place="nondream";

                    }
                    Intent intent=new Intent(studentzone.this,filterstudents.class);
                    intent.putExtra("flag",2);
                    intent.putExtra("placement",place);

                    placementradio.setChecked(false);
                    startActivity(intent);

                }
            }
        });
        filter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(studentzone.this,filterstudents.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.studentzonemenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add)
        {
            startActivity(new Intent(studentzone.this,updatestudents.class));

        }


        return super.onOptionsItemSelected(item);
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
