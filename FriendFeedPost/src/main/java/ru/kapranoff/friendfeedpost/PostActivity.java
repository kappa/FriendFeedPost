package ru.kapranoff.friendfeedpost;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;
import com.friendfeed.api.*;
import android.os.AsyncTask;
import java.io.IOException;


/**
 * Created by kappa on 25.05.13.
 */
public class PostActivity extends Activity {
    private String login;
    private String remotekey;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_layout);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        login = prefs.getString("login", "");
        remotekey = prefs.getString("remotekey", "");

        while (login.equals("") || remotekey.equals("")) {
            start_settings_activity();
        }

        if (is_connected()) { // TODO: offline operation

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                start_settings_activity();
                return true;
            case R.id.help:
                //showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean is_connected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void start_settings_activity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private class FriendFeedPostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... texts) {
            FriendFeedService frf = new FriendFeedServiceImpl(login, remotekey);
            frf.setUseCompression(true);

            try {
                Entry entry = frf.publish(texts[0]);
                return "ok";
            } catch (RuntimeException e) {
                return e.toString();
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(PostActivity.this, "Posted: " + result, Toast.LENGTH_LONG).show();
        }
    }


    public void button_post_onClick(View view) {
        new FriendFeedPostTask().execute("takoe");

    }
}