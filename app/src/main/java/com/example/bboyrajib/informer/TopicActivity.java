package com.example.bboyrajib.informer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.IOException;

public class TopicActivity extends AppCompatActivity {

    Button join,unsubfromall;
    ProgressDialog progressDialog;
    EditText topic;

  SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        prefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());


      //  Toast.makeText(TopicActivity.this,prefs.getString("messages",null),Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor=prefs.edit();
        editor.clear().commit();

        join = (Button) findViewById(R.id.subscribe);
        topic =(EditText) findViewById(R.id.topic);

       // unsubfromall=(Button)findViewById(R.id.unsubfromall);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(!ConnectionDetector.isNetworkAvailable(TopicActivity.this)){

                  //  progressDialog.dismiss();
                    Toast.makeText(TopicActivity.this,"Please connect your phone to internet!",Toast.LENGTH_LONG).show();
                    return;
                }

                if(topic.getText().toString().isEmpty()){
                    Toast.makeText(TopicActivity.this,"Topic cannot be empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(topic.getText().toString().contains(" ")){
                    Toast.makeText(TopicActivity.this,"Spaces not allowed!",Toast.LENGTH_SHORT).show();
                    topic.setText(null);
                    return;
                }
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog=new ProgressDialog(TopicActivity.this);
                progressDialog.setTitle("Entering!");
                progressDialog.setMessage("Joining Room, Please wait");
                progressDialog.setCancelable(false);
                progressDialog.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        FirebaseMessaging.getInstance().subscribeToTopic(topic.getText().toString()+"");
                        progressDialog.dismiss();
                        Toast.makeText(TopicActivity.this, "Successfully Joined!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TopicActivity.this,MainActivity.class);
                        intent.putExtra("room",topic.getText().toString());
                        intent.putExtra("message","");
                        startActivity(intent);
                        TopicActivity.this.finish();
                    }
                }, 2000);


            }
        });

       /* unsubfromall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FirebaseInstanceId.getInstance().deleteToken(;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
