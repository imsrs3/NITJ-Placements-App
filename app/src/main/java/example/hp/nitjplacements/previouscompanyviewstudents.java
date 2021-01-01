package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class previouscompanyviewstudents extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;

    TextView nostudents;
    private DatabaseReference mDatabase,myref,myref1;
    String key;
    String authuserid;
    public FirebaseRecyclerAdapter<placedstudentadapter, upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder> mFirebaseAdapter;
    ValueEventListener studentListener,postListener;
    profileadapter vuser;
    LinearLayoutManager mLinearLayoutManager;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previouscompanyviewstudents);
        nostudents= (TextView) findViewById(R.id.nopreviouscompanystudenttextView);
        recyclerView = (RecyclerView)findViewById(R.id.previouscompaniesstudentfilter_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(previouscompanyviewstudents.this);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Placed students" + "</font>"));
        }
        key=getIntent().getStringExtra("key");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("placedstudents").child(key);
        query=mDatabase.orderByChild("rollno");
        myref = FirebaseDatabase.getInstance().getReference().child("placedstudents").child(key);
        dialog =new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("loading ......");
        dialog.setCancelable(false);
        dialog.show();
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    previouscompanyviewstudents.this.finish();
                }
            }
        };
        studentListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChildren())
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    nostudents.setVisibility(View.VISIBLE);


                    if(dialog!=null)
                    {
                        dialog.cancel();
                    }
                }else
                {
                    nostudents.setVisibility(View.INVISIBLE);recyclerView.setVisibility(View.VISIBLE);

                }
                if(mFirebaseAdapter!=null)
                {

                    mFirebaseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mFirebaseAdapter=new FirebaseRecyclerAdapter<placedstudentadapter, upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder>(placedstudentadapter.class,R.layout.student_filter_singleitem,upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder.class,query) {
            @Override
            protected void populateViewHolder(upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder viewHolder, final placedstudentadapter model, int position) {
                final int pos=position;


                if(dialog!=null) {
                    dialog.cancel();
                }
                if(!model.name.equals("Null"))
                {viewHolder.setStudent_name(model.name);
                    viewHolder.setRollno(model.rollno);
                    viewHolder.setPlacement( "");

                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                  /*      Intent intent=new Intent(previouscompanyviewstudents.this,singlestudent.class);
                        intent.putExtra("name", model.name);
                        intent.putExtra("rollno", model.rollno);
                        intent.putExtra("email", model.email);
                        intent.putExtra("placement", model.placement);
                        intent.putExtra("x",model.x);
                        intent.putExtra("inter",model.inter);
                        intent.putExtra("cgpa",model.cgpa);
                        intent.putExtra("backlogs",model.backlog);
                        intent.putExtra("phno",model.contact);

                        startActivity(intent);*/
                        Intent intent=new Intent(previouscompanyviewstudents.this,profile.class);
                        intent.putExtra("flag", "yes");
                        intent.putExtra("email", model.email);
                        startActivity(intent);

                    }
                });



            }
        };
        recyclerView.setLayoutManager(mLinearLayoutManager);

        recyclerView.setAdapter(mFirebaseAdapter);
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mstate);

        query.addValueEventListener(studentListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
        query.removeEventListener(studentListener);

    }

}

