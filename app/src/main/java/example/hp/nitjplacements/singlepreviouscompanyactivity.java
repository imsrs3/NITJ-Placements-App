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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class singlepreviouscompanyactivity extends AppCompatActivity {
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    private DatabaseReference mDatabase;
    profileadapter vuser;

    String authuserid;
    ValueEventListener postListener;
    String labels[]={"Company Name:","Position:","Salary: ","Date of VISIT","Company Link:","Info:"} ;
    List<String> values;
    ListView l;

    Myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepreviouscompanyactivity);
        auth=FirebaseAuth.getInstance();
        l=(ListView) findViewById(R.id.previousstudentlist);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Company Info" + "</font>"));
        }
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    singlepreviouscompanyactivity.this.finish();
                }
            }
        };
        dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
        values=new ArrayList<String>();
        adapter =new Myadapter(singlepreviouscompanyactivity.this, android.R.layout.simple_list_item_1, values);
        l.setAdapter(adapter);
        adapter.add(getIntent().getStringExtra("name"));
        adapter.add(getIntent().getStringExtra("position"));
        adapter.add(getIntent().getDoubleExtra("CTC",0)+" lakhs");
        adapter.add(getIntent().getStringExtra("date"));
        adapter.add(getIntent().getStringExtra("link"));
        adapter.add(getIntent().getStringExtra("info"));

        if(dialog!=null)
        {
            dialog.cancel();
        }

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

    }
}
