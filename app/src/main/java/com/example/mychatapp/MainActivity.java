package com.example.mychatapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ViewPager myViewpager;
    private TabLayout myTablayout;
    private ChatAccessorAdapter myChatAccessorAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();

        RootRef= FirebaseDatabase.getInstance().getReference();

        mtoolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("MyChatApp");
        myViewpager=(ViewPager) findViewById(R.id.main_tabs_pager);
        myChatAccessorAdapter=new ChatAccessorAdapter(getSupportFragmentManager());
        myViewpager.setAdapter(myChatAccessorAdapter);
        myTablayout=(TabLayout) findViewById(R.id.maintabs);
        myTablayout.setupWithViewPager(myViewpager);
    }

    @Override
    protected void onStart() {
        super.onStart();
      FirebaseUser  currentuser=mAuth.getCurrentUser();
        if(currentuser==null){
            sendUserToLoginActivity();
        }
        else
        {

            UpdateUserStatus("online");


            VerifyUserExistance();
        }

    }


    @Override
    protected void onStop()
    {
        super.onStop();
        FirebaseUser  currentuser=mAuth.getCurrentUser();
        if(currentuser != null)
        {
            UpdateUserStatus("offline");

        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FirebaseUser  currentuser=mAuth.getCurrentUser();
        if(currentuser != null)
        {
            UpdateUserStatus("offline");

        }

    }



    private void VerifyUserExistance() {
        String currentUserID=mAuth.getCurrentUser().getUid();
        RootRef.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Wellcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(loginIntent);
        finish();
    }
    private void sendUserToSettingsActivity() {
        Intent settingsIntent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingsIntent);
    }
    private void sendUserToFindFriendsActivity() {
        Intent finfFriendsIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(finfFriendsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.option_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
      if(item.getItemId()==R.id.main_find_Friends_option)
      {
         sendUserToFindFriendsActivity();
      }
        if(item.getItemId()==R.id.main_settings_option)
        {
             sendUserToSettingsActivity();
        }
        if(item.getItemId()==R.id.main_logout_option)
        {
            UpdateUserStatus("offline");
           mAuth.signOut();

           sendUserToLoginActivity();
        }
        if(item.getItemId()==R.id.main_creat_group_option)
        {
            RequestNewGroup();
        }
        return true;

    }

    private void RequestNewGroup() {

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");
        final EditText groupNameField=new EditText(MainActivity.this);
        groupNameField.setHint("e.g coding cafe");
        builder.setView(groupNameField);
        builder.setPositiveButton("Creat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName=groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please write group name...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void CreateNewGroup(final String groupName) {
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName+" Group is Created successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }



    private void UpdateUserStatus(String state)
    {
        String saveCurrentDate,saveCurrentTime;

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());


        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");;
        saveCurrentTime = currentTime.format(calendar.getTime());


        HashMap<String,Object> onlineStateMap= new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",state);

        currentUserID=mAuth.getCurrentUser().getUid();

        RootRef.child("users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
}
