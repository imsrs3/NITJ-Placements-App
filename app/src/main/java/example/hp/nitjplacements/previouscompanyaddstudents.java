package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class previouscompanyaddstudents extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog,dialog1;
    TextView nostudents;
    private DatabaseReference mDatabase,myref,myref1;
    String key;
    String authuserid;
    public FirebaseRecyclerAdapter<profileadapter, upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder> mFirebaseAdapter;
    ValueEventListener studentListener,postListener;
    profileadapter vuser;
    LinearLayoutManager mLinearLayoutManager;
    Query query,query1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previouscompanyaddstudents);
        nostudents= (TextView) findViewById(R.id.nopreviouscompanystudenttextView);
        recyclerView = (RecyclerView)findViewById(R.id.previouscompaniesstudentfilter_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(previouscompanyaddstudents.this);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Add Placed students" + "</font>"));
        }
        key=getIntent().getStringExtra("key");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("students");
        query1=mDatabase.orderByChild("rollno");
        myref = FirebaseDatabase.getInstance().getReference().child("placedstudents");
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

                    previouscompanyaddstudents.this.finish();
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

        mFirebaseAdapter=new FirebaseRecyclerAdapter<profileadapter, upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder>(profileadapter.class,R.layout.student_filter_singleitem,upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder.class,query1) {
            @Override
            protected void populateViewHolder(upcomingcompaniesstudentfilter.upcomingcompanystudentfilter_ViewHolder viewHolder, final profileadapter model, int position) {
                final int pos=position;


                if(dialog!=null) {
                    dialog.cancel();
                }
                if(!model.name.equals("Null"))
                {viewHolder.setStudent_name(model.name);
                    viewHolder.setRollno(model.rollno);
                    viewHolder.setPlacement( model.placement);

                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {  query=myref.child(key).orderByChild("email").equalTo(model.email);
                        dialog1 =new ProgressDialog(previouscompanyaddstudents.this);
                        dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog1.setMessage("adding ......");
                        dialog1.setCancelable(false);
                        dialog1.show();
                        final placedstudentadapter user1=new placedstudentadapter(model.email,model.name,model.rollno);

                                postListener=new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(!dataSnapshot.hasChildren())
                                {

                                    myref.child(key).push().setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(previouscompanyaddstudents.this,"student added ", Toast.LENGTH_SHORT).show();
                                                if(model.placement.equals("No")) {
                                                    DatabaseReference ref1 = mFirebaseAdapter.getRef(pos);
                                                    ref1.child("placement").setValue(getIntent().getStringExtra("placement"));
                                                }else if(model.placement.equals("dream"))
                                                {

                                                }else if(model.placement.equals("nondream")&&getIntent().getStringExtra("placement").equals("dream"))
                                                {
                                                    DatabaseReference ref1 = mFirebaseAdapter.getRef(pos);
                                                    ref1.child("placement").setValue(getIntent().getStringExtra("placement"));
                                                }

                                            }else
                                            {
                                                Toast.makeText(previouscompanyaddstudents.this," student not added.try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    if(dialog1!=null)
                                    {
                                        dialog1.cancel();
                                    }
                                    query.removeEventListener(postListener);
                                }else
                                {
                                    Toast.makeText(previouscompanyaddstudents.this," student already added.", Toast.LENGTH_SHORT).show();
                                    if(dialog1!=null)
                                    {
                                        dialog1.cancel();
                                    }
                                    query.removeEventListener(postListener);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                query.removeEventListener(postListener);
                            }
                        };

                        query.addValueEventListener(postListener);

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

        query1.addValueEventListener(studentListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
        query1.removeEventListener(studentListener);

    }
}
