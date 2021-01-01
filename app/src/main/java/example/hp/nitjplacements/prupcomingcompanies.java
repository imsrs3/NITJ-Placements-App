package example.hp.nitjplacements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class prupcomingcompanies extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    TextView nocompanies;
    private DatabaseReference mDatabase,myref,myref1;
    String authuserid;
    public FirebaseRecyclerAdapter<upcomingcompaniesadapter, prupcomingcompanies.upcomingcompany_ViewHolder> mFirebaseAdapter;
    ValueEventListener companyListener,postListener;
    profileadapter vuser;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prupcomingcompanies);
        nocompanies= (TextView) findViewById(R.id.noprupcomingcompanytextView);
        recyclerView = (RecyclerView)findViewById(R.id.prupcomingcompany_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(prupcomingcompanies.this);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "Upcoming companies" + "</font>"));
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("upcomingcompanies");
        myref = FirebaseDatabase.getInstance().getReference().child("upcomingcompanies");
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

                    prupcomingcompanies.this.finish();
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
        mFirebaseAdapter=new FirebaseRecyclerAdapter<upcomingcompaniesadapter, prupcomingcompanies.upcomingcompany_ViewHolder>(upcomingcompaniesadapter.class,R.layout.company_single_item,prupcomingcompanies.upcomingcompany_ViewHolder.class,myref) {
            @Override
            public upcomingcompaniesadapter getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }
            @Override
            protected void populateViewHolder( final prupcomingcompanies.upcomingcompany_ViewHolder viewHolder, final upcomingcompaniesadapter model, int position) {
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
                        PopupMenu popup=new PopupMenu(prupcomingcompanies.this,viewHolder.itemView);
                        popup.inflate(R.menu.prupcomingcompanysingleitemmenu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getItemId()==R.id.remove)
                                {
                                    dialog =new ProgressDialog(prupcomingcompanies.this);
                                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    dialog.setMessage("removing ......");
                                    dialog.setCancelable(false);
                                    dialog.show();
                                    DatabaseReference ref = mFirebaseAdapter.getRef(getItemCount() - 1 - pos);

                                    ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful())
                                            { if(dialog!=null)
                                            {
                                                dialog.cancel();
                                            }
                                                Toast.makeText(prupcomingcompanies.this, "Company can't be removed.Try again.", Toast.LENGTH_SHORT).show();
                                            }else  if(task.isSuccessful())
                                            {
                                            }
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            //  mFirebaseAdapter.notifyItemRemoved(pos);
                                            // mFirebaseAdapter.notifyItemRangeRemoved(pos,getItemCount());

                                            mFirebaseAdapter.notifyDataSetChanged();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if(dialog!=null)
                                                    {
                                                        dialog.cancel();
                                                    }
                                                }
                                            },500);

                                            Toast.makeText(prupcomingcompanies.this, "Company is  removed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else if(item.getItemId()==R.id.filter)
                                {Intent intent=new Intent(prupcomingcompanies.this,upcomingcompaniesstudentfilter.class);
                                    intent.putExtra("CTC", model.CTC);
                                    intent.putExtra("eligibility",model.eligibility);
                                    startActivity(intent);
                                }
                                else if(item.getItemId()==R.id.view)
                                {
                                    Intent intent = new Intent(getApplicationContext(), singlecompanyActivity.class);
                                    intent.putExtra("name", model.name);
                                    intent.putExtra("position", model.position);
                                    intent.putExtra("CTC", model.CTC);
                                    intent.putExtra("eligibility",model.eligibility);
                                    intent.putExtra("link", model.link);
                                    intent.putExtra("info", model.info);
                                    intent.putExtra("date",model.date);
                                    startActivity(intent);
                                }
                                else if(item.getItemId()==R.id.add)
                                {
                                    previouscompaniesadapter company=new previouscompaniesadapter(model.name,model.position,  model.CTC,model.link,model.info,model.date);
                                   DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference().child("previouscompanies");
                                    mDatabase1.push().setValue(company).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(prupcomingcompanies.this,"Company added successfully", Toast.LENGTH_SHORT).show();

                                            }else
                                            {
                                                Toast.makeText(prupcomingcompanies.this,"Company is not added .Please try Again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
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
    public static class upcomingcompany_ViewHolder extends RecyclerView.ViewHolder {
        TextView company_name, company_position,salary;
        LinearLayout layout;
        LinearLayout.LayoutParams params;

        public upcomingcompany_ViewHolder(View itemView) {
            super(itemView);
            company_name = (TextView) itemView.findViewById(R.id.upcomingcompanyname);
            company_position = (TextView) itemView.findViewById(R.id.upcomingcompanyposition);
            salary = (TextView) itemView.findViewById(R.id.upcomingcompanysalary);
            layout = (LinearLayout)itemView.findViewById(R.id.company_single_item_layout);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }


        public void setCompany_name(String title) {

            company_name.setText(title);

        }
        public void Layout_hide() {
            params.height = 0;

            layout.setLayoutParams(params);

        }


        public void setCompany_position(String title) {
            company_position.setText(title);
        }

        public void setSalary(String title) {
            salary.setText(title);
        }



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add)
        {
            startActivity(new Intent(prupcomingcompanies.this,addupcomingcompany.class));

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

        myref.addValueEventListener(companyListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        auth.removeAuthStateListener(mstate);
        myref.removeEventListener(companyListener);

    }
}
