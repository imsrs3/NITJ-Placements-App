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
import android.widget.ImageView;
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

public class Allcompanies extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    TextView nocompanies;
    private DatabaseReference mDatabase,myref,myref1;
    String authuserid;
    View.OnClickListener lis;
    public FirebaseRecyclerAdapter<Allcompaniesadapter, Allcompanies.allcompany_ViewHolder> mFirebaseAdapter;
    ValueEventListener companyListener,postListener;
    profileadapter vuser;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allcompanies);
        nocompanies= (TextView) findViewById(R.id.noallcompanytextView);
        recyclerView = (RecyclerView)findViewById(R.id.allcompany_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(Allcompanies.this);
        auth=FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Html.fromHtml("<font color=#FFFFFF>" + "All Companies " + "</font>"));
        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("allcompanies");
        myref = FirebaseDatabase.getInstance().getReference().child("allcompanies");
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

                    Allcompanies.this.finish();
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
        mFirebaseAdapter=new FirebaseRecyclerAdapter<Allcompaniesadapter, Allcompanies.allcompany_ViewHolder>(Allcompaniesadapter.class,R.layout.allcompanysingleitem,Allcompanies.allcompany_ViewHolder.class,myref) {
            @Override
            protected void populateViewHolder(final Allcompanies.allcompany_ViewHolder viewHolder, final Allcompaniesadapter model, int position) {
                final int pos=position;
                if(dialog!=null) {
                    dialog.cancel();
                }
                if(!model.name.equals("Null"))
                {viewHolder.setCompany_name(model.name);
                    viewHolder.setCompany_person(model.contactperson);


                }
                lis=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId()==viewHolder.remove.getId())
                        { PopupMenu popup=new PopupMenu(Allcompanies.this,viewHolder.remove);
                            popup.inflate(R.menu.removemenu);
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    dialog =new ProgressDialog(Allcompanies.this);
                                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    dialog.setMessage("removing ......");
                                    dialog.setCancelable(false);
                                    dialog.show();
                                    DatabaseReference ref = mFirebaseAdapter.getRef(pos);

                                    ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful())
                                            { if(dialog!=null)
                                            {
                                                dialog.cancel();
                                            }
                                                Toast.makeText(Allcompanies.this, "try again can't be removed now", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(Allcompanies.this, "removed", Toast.LENGTH_SHORT).show();


                                        }
                                    });

                                    return false;
                                }
                            });
                            popup.show();

                        }else  if(v.getId()==viewHolder.itemView.getId())
                        {
                            Intent intent = new Intent(getApplicationContext(), singleallcompanyactivity.class);
                            intent.putExtra("name", model.name);
                            intent.putExtra("link", model.link);
                            intent.putExtra("info", model.info);
                            intent.putExtra("email",model.contactemail);
                            intent.putExtra("number", model.contactnumber);
                            intent.putExtra("person", model.contactperson);

                            startActivity(intent);

                        }


                    }
                };


                viewHolder.remove.setOnClickListener(lis);

                //OnClick Item
                viewHolder.itemView.setOnClickListener(lis);


            }

        };
        recyclerView.setLayoutManager(mLinearLayoutManager);

        recyclerView.setAdapter(mFirebaseAdapter);





    }
    public static class allcompany_ViewHolder extends RecyclerView.ViewHolder {
        TextView company_name, company_person;
        ImageView remove;
        LinearLayout layout;
        LinearLayout.LayoutParams params;

        public allcompany_ViewHolder(View itemView) {
            super(itemView);
            company_name = (TextView) itemView.findViewById(R.id.allcompanyname);
            company_person = (TextView) itemView.findViewById(R.id.allcompanycontactperson);
           remove=(ImageView) itemView.findViewById(R.id.allcompanyremoving);
            layout = (LinearLayout)itemView.findViewById(R.id.allcompany_single_item_layout);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }


        public void setCompany_name(String title) {

            company_name.setText(title);

        }
        public void Layout_hide() {
            params.height = 0;

            layout.setLayoutParams(params);

        }


        public void setCompany_person(String title) {
            company_person.setText(title);
        }





    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            startActivity(new Intent(Allcompanies.this,addcompany.class));

        }


        return super.onOptionsItemSelected(item);
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
