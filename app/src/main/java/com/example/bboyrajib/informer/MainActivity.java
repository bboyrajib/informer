package com.example.bboyrajib.informer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;

import static com.example.bboyrajib.informer.R.id.roomtv;

public class MainActivity extends AppCompatActivity {

    String msg,messagehome,titlehome,usrtopic;

    ProgressDialog progressDialog;

    Intent intent;


    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences.Editor editor4=prefs.edit();
        //editor4.clear().commit();
        //getActionBar().setDisplayShowHomeEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
      //  Button register = (Button) findViewById(R.id.register);
        final Button mute=(Button)findViewById(R.id.unsub);
       // Button refresh=(Button)findViewById(R.id.refresh);
        final TextView messageupdate=(TextView)findViewById(R.id.MessagesView);
        TextView roomtv=(TextView)findViewById(R.id.roomtv);

       // new network().execute("");
       if(!getIntent().getExtras().getString("message").isEmpty()){
           if(prefs.getString("room",null).equalsIgnoreCase(getIntent().getExtras().getString("room"))) {
               roomtv.setText("Room: " + getIntent().getExtras().getString("room").toLowerCase());
               TextView update = (TextView) findViewById(R.id.MessagesView);
               update.setText("\n~Message: " + getIntent().getExtras().getString("message") + "\n" + prefs.getString("messages", null));
           }
           else {
               roomtv.setText("Room: " + getIntent().getExtras().getString("room").toLowerCase());
               TextView update = (TextView) findViewById(R.id.MessagesView);
               update.setText("\n~Message: " + getIntent().getExtras().getString("message") + "\n" );
           }
        }

      //  SharedPreferences.Editor editor=prefs.edit();
        //editor.putString("messages", messageupdate.getText().toString());
        //editor.commit();
        //  my_topic=intent.getStringExtra("topic_me");
     //   my_msg=intent.getStringExtra("message_me");

       // final EditText topic=(EditText)findViewById(R.id.topicHome);
        final EditText messages=(EditText)findViewById(R.id.messagesHome);
        final Button sendButton=(Button)findViewById(R.id.sendbuttonhome);


        final String room = "/topics/"+getIntent().getExtras().getString("room").toLowerCase();
        usrtopic = getIntent().getExtras().getString("room");

        SharedPreferences.Editor editor3=prefs.edit();
        editor3.putString("room",usrtopic.toLowerCase());
        editor3.commit();

        //toolbar.setLogoDescription(usrtopic);
        toolbar.setTitle(usrtopic.toLowerCase());
       // toolbar.setBackgroundColor(getResources().getColor(R.color.colorMain));


        roomtv.setText("Room: "+usrtopic.toLowerCase());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messagehome = messages.getText().toString();
              //  titlehome = topic.getText().toString();
                if (messagehome.isEmpty()) {
                  //  Toast.makeText(MainActivity.this, "Topic or Message cannot be blank", Toast.LENGTH_LONG).show();
                    return;
                    //FirebaseMessaging.getInstance().subscribeToTopic("news");

                } else {
                    // FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                    try  {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Informing");
                    progressDialog.setMessage("Please wait a moment");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try{
                                progressDialog.dismiss();
                                JSONObject jsonObject=new JSONObject(response);
                                Boolean res= jsonObject.getBoolean("success");
                                Toast.makeText(MainActivity.this,"Your Message has been Sent",Toast.LENGTH_LONG).show();
                               // topic.setText(null);
                                messages.setText(null);
                                messages.requestFocus();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // FirebaseMessaging.getInstance().subscribeToTopic("news");
                                    }
                                },2000);




                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this,"Catch: Message Sent",Toast.LENGTH_SHORT).show();
                            }

                        }


                    };
                 //   Toast.makeText(MainActivity.this,"Hi: "+room+" "+usrtopic+" "+messagehome,Toast.LENGTH_SHORT).show();
                    SendRequest sendRequest = new SendRequest(room, usrtopic, messagehome, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(sendRequest);

                }


            }
        });


      /*  register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Token = FirebaseInstanceId.getInstance().getToken();
                Log.d("Token:",Token);
               FirebaseMessaging.getInstance().subscribeToTopic("news");
                mute.setText("IRRITATED? MUTE!");
                Toast.makeText(MainActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                //sendNotification("Hello","Hi");
            }
        });*/

             mute.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     if(mute.getText().toString().equalsIgnoreCase("MUTE")) {
                         sendButton.setEnabled(false);
                         messages.setEnabled(false);
                         FirebaseMessaging.getInstance().unsubscribeFromTopic(usrtopic.toLowerCase());
                         mute.setText("UNMUTE");
                         Toast.makeText(MainActivity.this, "UnRegistered Successfully", Toast.LENGTH_SHORT).show();
                         return;
                     }

                     if(mute.getText().toString().equalsIgnoreCase("UNMUTE")){
                         sendButton.setEnabled(true);
                         messages.setEnabled(true);
                         FirebaseMessaging.getInstance().subscribeToTopic(usrtopic.toLowerCase());
                         mute.setText("MUTE");
                         Toast.makeText(MainActivity.this,"Receiving Messages",Toast.LENGTH_SHORT).show();
                         return;
                     }
                 }
             });

        /*refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic=prefs.getString("topic",null);intent.getStringExtra("Topic"))
                String msg=prefs.getString("message",null);
                messageupdate.setText(messageupdate.getText().toString()+"\n Topic: "+topic+"\n Message: "+msg);
            }
        });*/



         /*  Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        while (!isInterrupted()) {
                            Thread.sleep(1000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {



                                    if(prefs.getString("bg",null)!=null){
                                       // roomtv.setText(""+getIntent().getExtras().getString("room"));
                                        TextView update=(TextView)findViewById(R.id.MessagesView);
                                        update.setText("\n~Message: "+prefs.getString("bg",null)+"\n"+prefs.getString("messages",null));
                                        SharedPreferences.Editor editor=prefs.edit();
                                        editor.putString("bg",null).commit();
                                    }



                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();*/
        //  messageupdate.setText(finalString);

        }

      /*  private class network extends AsyncTask<String,String,String>{

            @Override
            protected String doInBackground(String... params) {
                publishProgress(params);
                return "";
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                TextView room=(TextView)findViewById(R.id.roomtv);

                    room.setText(""+getIntent().getExtras().getString("room"));
                    TextView update=(TextView)findViewById(R.id.MessagesView);
                    update.setText("\n~Message: "+getIntent().getExtras().getString("message")+"\n"+prefs.getString("messages",null));
                }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        } */




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //getSupportActionBar().setIcon(R.drawable.informer);
       // getActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear) {
            TextView update=(TextView)findViewById(R.id.MessagesView);
            update.setText(null);
          //  MainActivity.this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void sendNotification(String title,String body){
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }


    public void onResume() {
        super.onResume();
        Context context=this;
        context.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));



    }

    @Override
    protected void onStart() {

        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setTitle(prefs.getString("room",null));
        TextView mssgs=(TextView)findViewById(R.id.MessagesView);
        mssgs.setText(prefs.getString("messages",null));
        TextView room=(TextView)findViewById(R.id.roomtv);
        room.setText("Room: "+prefs.getString("room",null));

        Toast.makeText(MainActivity.this,"TV: "+room.getText().toString()+"Intent: "+getIntent().getExtras().getString("room"),Toast.LENGTH_LONG).show();

        if(prefs.getString("bg",null)!=null){
            //setText(""+getIntent().getExtras().getString("room"));
           // if(room.getText().toString().equalsIgnoreCase("Room: "+getIntent().getExtras().getString("room").toLowerCase())) {
                TextView update = (TextView) findViewById(R.id.MessagesView);
                update.setText("\n~Message: " + prefs.getString("bg", null) + "\n" + prefs.getString("messages", null));
            //}
         //   else {
               // room.setText("Room: "+usrtopic);
              //  TextView update = (TextView) findViewById(R.id.MessagesView);
               // update.setText("\n~Message: " + prefs.getString("bg", null) + "\n" );
               // SharedPreferences.Editor editor=prefs.edit();
               // editor.clear().commit();
              //  editor.putString("room",usrtopic).commit();
           // }
        }

        TextView messageupdate=(TextView)findViewById(R.id.MessagesView);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("messages", messageupdate.getText().toString());
        editor.putString("bg",null);
        editor.commit();
        super.onStart();
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        Context context=this;
        context.unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            msg = intent.getStringExtra("message");
            //topic=intent.getStringExtra("topic");
            TextView update=(TextView)findViewById(R.id.MessagesView);

            try {
                Uri notif= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r= RingtoneManager.getRingtone(getApplicationContext(),notif);
                r.play();
            }catch (Exception e){
                e.printStackTrace();
            }
            String messagesReceived="\n~Message: "+msg+"\n"+update.getText();
            SharedPreferences.Editor editor=prefs.edit();
            editor.putString("messages", messagesReceived);
            editor.commit();
            update.setText(messagesReceived);

           // finalString=update.getText().toString();
            //do other stuff here
        }
    };

    @Override
    public void onBackPressed() {

       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("What do you want to do?")
                .setCancelable(true)
                .setPositiveButton("LEAVE ROOM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear().commit();
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setTitle("Exiting");
                        progressDialog.setMessage("Leaving Room, Please wait");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                progressDialog.dismiss();
                                Intent intent=new Intent(MainActivity.this,TopicActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                MainActivity.this.finish();
                            }
                        }, 2000);

                    }
                })
                .setNegativeButton("QUIT APP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       // dialog.cancel();
                        moveTaskToBack(true);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }
}

