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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class upcomingcompaniesstudentfilter extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    double ctc,eligiblity;
    Query query;
    TextView nostudents;
    private DatabaseReference mDatabase,myref,myref1;
    String authuserid;
   public FirebaseRecyclerAdapter<profileadapter, upcomingcompanystudentfilter_ViewHolder> mFirebaseAdapter;
    ValueEventListener studentListener,postListener;
    profileadapter vuser;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcomingcompaniesstudentfilter);

        nostudents= (TextView) findViewById(R.id.noupcomingcompaniesstudenttextView);
        recyclerView = (RecyclerView)findViewById(R.id.upcomingcompaniesstudentfilter_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(upcomingcompaniesstudentfilter.this);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "ELigible students" + "</font>"));
        }
        String e=getIntent().getStringExtra("eligibility");
        eligiblity=Double.parseDouble(e.trim());
        ctc=getIntent().getDoubleExtra("CTC",0);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("students");
        myref = FirebaseDatabase.getInstance().getReference().child("students");
         query=myref.orderByChild("cgpa").startAt(eligiblity);
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

                    upcomingcompaniesstudentfilter.this.finish();
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


        mFirebaseAdapter=new FirebaseRecyclerAdapter<profileadapter, upcomingcompanystudentfilter_ViewHolder>(profileadapter.class,R.layout.student_filter_singleitem,upcomingcompanystudentfilter_ViewHolder.class,query) {
            @Override
            protected void populateViewHolder(upcomingcompanystudentfilter_ViewHolder viewHolder, final profileadapter model, int position) {
                final int pos=position;


                if(dialog!=null) {
                    dialog.cancel();
                }
                if(model.backlog>0)
                    viewHolder.Layout_hide();
                if(ctc>6)
                {if(model.placement.equals("dream"))
                    viewHolder.Layout_hide();

                }
                if(ctc<6)
                {

                    if(model.placement.equals("dream")||model.placement.equals("nondream"))
                    viewHolder.Layout_hide();
                }

                if(!model.name.equals("Null"))
                {viewHolder.setStudent_name(model.name);
                    viewHolder.setRollno(model.rollno);
                    viewHolder.setPlacement( model.placement);

                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(upcomingcompaniesstudentfilter.this,singlestudent.class);
                        intent.putExtra("name", model.name);
                        intent.putExtra("rollno", model.rollno);
                        intent.putExtra("email", model.email);
                        intent.putExtra("placement", model.placement);
                        intent.putExtra("x",model.x);
                        intent.putExtra("inter",model.inter);
                        intent.putExtra("cgpa",model.cgpa);
                        intent.putExtra("backlogs",model.backlog);
                        intent.putExtra("phno",model.contact);

                        startActivity(intent);
                    }
                });



            }
        };
        recyclerView.setLayoutManager(mLinearLayoutManager);

        recyclerView.setAdapter(mFirebaseAdapter);


    }

    public static class upcomingcompanystudentfilter_ViewHolder extends RecyclerView.ViewHolder {
        TextView student_name, rollno,placement;
        LinearLayout layout;
        LinearLayout.LayoutParams params;

        public upcomingcompanystudentfilter_ViewHolder(View itemView) {
            super(itemView);
            student_name = (TextView) itemView.findViewById(R.id.studentname);
            rollno = (TextView) itemView.findViewById(R.id.studentrollno);
            placement = (TextView) itemView.findViewById(R.id.studentplacement);
            layout = (LinearLayout)itemView.findViewById(R.id.student_filter_singleitem);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }


        public void setStudent_name(String title) {

            student_name.setText(title);

        }
        public void Layout_hide() {
            params.height = 0;

            layout.setLayoutParams(params);

        }


        public void setRollno(String title) {
            rollno.setText(title);
        }

        public void setPlacement(String title) {
            placement.setText(title);
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

        query.addValueEventListener(studentListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
        query.removeEventListener(studentListener);

    }
}
