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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class upcomingcompanies extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mstate;
    ProgressDialog dialog;
    TextView nocompanies;
    private DatabaseReference mDatabase,myref,myref1;
    String authuserid;
    public FirebaseRecyclerAdapter<upcomingcompaniesadapter, upcomingcompanies.upcomingcompany_ViewHolder> mFirebaseAdapter;
    ValueEventListener companyListener,postListener;
    profileadapter vuser;
    LinearLayoutManager mLinearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcomingcompanies);
        nocompanies= (TextView) findViewById(R.id.noupcomingcompanytextView);
        recyclerView = (RecyclerView)findViewById(R.id.upcomingcompany_recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(upcomingcompanies.this);
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

      //  upcomingcompaniesadapter company=new upcomingcompaniesadapter("Samsung","Software Engineer",8.85,"7","http://www.samsung.com/in/","Samsung is a South Korean multinational conglomerate headquartered in Samsung Town, Seoul. It comprises numerous affiliated businesses, most of them united under the Samsung brand, and is the largest South Korean chaebol.","Mar,26,2018");
//mDatabase.push().setValue(company);
  //          upcomingcompaniesadapter company1=new upcomingcompaniesadapter("Tata Consultancy Services","Software Engineer",7,"6.5","https://www.tcs.com/","Tata Consultancy Services Limited is an Indian multinational information technology service, consulting and business solutions company headquartered in Mumbai, Maharashtra. It is a subsidiary of the Tata Group and operates in 46 countries.","Mar,29,2018");
    //    mDatabase.push().setValue(company1);
        mstate=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(auth.getCurrentUser()==null)
                {

                    upcomingcompanies.this.finish();
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

        mFirebaseAdapter=new FirebaseRecyclerAdapter<upcomingcompaniesadapter, upcomingcompany_ViewHolder>(upcomingcompaniesadapter.class,R.layout.company_single_item,upcomingcompany_ViewHolder.class,myref) {
            @Override
            public upcomingcompaniesadapter getItem(int position) {
                return super.getItem(getItemCount() - 1 - position);
            }


            @Override
            protected void populateViewHolder(upcomingcompany_ViewHolder viewHolder,final upcomingcompaniesadapter model, int position) {
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
        getMenuInflater().inflate(R.menu.home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.changedetails)
        {
            startActivity(new Intent(upcomingcompanies.this,changeuserdetails.class));

        }else if(item.getItemId()==R.id.userssignout)
        {     auth.signOut();


        }else if(item.getItemId()==R.id.profile)
        {
            startActivity(new Intent(upcomingcompanies.this,profile.class));
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
