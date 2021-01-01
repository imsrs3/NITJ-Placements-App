package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class studentpreviouscompanies extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    TextView nocompanies;
    private DatabaseReference mDatabase,myref,myref1;
    String authuserid;
    public FirebaseRecyclerAdapter<previouscompaniesadapter, prpreviouscompanies.previouscompany_ViewHolder> mFirebaseAdapter;
    ValueEventListener companyListener,postListener;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentpreviouscompanies);
        nocompanies= (TextView) findViewById(R.id.noprpreviouscompanytextView);
        recyclerView = (RecyclerView)findViewById(R.id.prpreviouscompany_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(studentpreviouscompanies.this);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Visited companies" + "</font>"));
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("previouscompanies");
        myref = FirebaseDatabase.getInstance().getReference().child("previouscompanies");
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

                    studentpreviouscompanies.this.finish();
                }
            }
        };
        companyListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChildren())
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    nocompanies.setVisibility(View.VISIBLE);

                    if(dialog!=null)
                    {
                        dialog.cancel();
                    }
                }else
                {
                    nocompanies.setVisibility(View.INVISIBLE);recyclerView.setVisibility(View.VISIBLE);

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
        mFirebaseAdapter=new FirebaseRecyclerAdapter<previouscompaniesadapter, prpreviouscompanies.previouscompany_ViewHolder>(previouscompaniesadapter.class,R.layout.company_single_item,prpreviouscompanies.previouscompany_ViewHolder.class,myref) {

            @Override
            public previouscompaniesadapter getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }
            @Override
            protected void populateViewHolder( final prpreviouscompanies.previouscompany_ViewHolder viewHolder, final previouscompaniesadapter model, final int position) {
                final int pos=position;
                if(dialog!=null) {
                    dialog.cancel();
                }
                if(!model.name.equals("Null"))
                {viewHolder.setCompany_name(model.name);
                    viewHolder.setCompany_position(model.position);
                    viewHolder.setSalary(model.date);

                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popup=new PopupMenu(studentpreviouscompanies.this,viewHolder.itemView);
                        popup.inflate(R.menu.studentpreviouscompanysingleitem);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getItemId()==R.id.viewstudents)
                                {Intent intent=new Intent(studentpreviouscompanies.this,previouscompanyviewstudents .class);
                                    final DatabaseReference refer = mFirebaseAdapter.getRef(getItemCount()-1-position);
                                    intent.putExtra("key", refer.getKey());
                                    startActivity(intent);

                                }else if(item.getItemId()==R.id.viewoffer)
                                {Intent intent=new Intent(studentpreviouscompanies.this,singlepreviouscompanyactivity.class);
                                    intent.putExtra("name", model.name);
                                    intent.putExtra("position", model.position);
                                    intent.putExtra("CTC", model.CTC);
                                    intent.putExtra("link", model.link);
                                    intent.putExtra("info", model.info);
                                    intent.putExtra("date",model.date);
                                    startActivity(intent);
                                }

                                return false;
                            }
                        });
                        popup.show();
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

        myref.addValueEventListener(companyListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
        myref.removeEventListener(companyListener);

    }
}
