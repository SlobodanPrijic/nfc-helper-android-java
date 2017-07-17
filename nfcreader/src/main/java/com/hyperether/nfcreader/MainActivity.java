package com.hyperether.nfcreader;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyperether.nfchelper.NfcParser;
import com.hyperether.nfchelper.NfcUtil;
import com.hyperether.nfcreader.api.KisiResponse;
import com.hyperether.nfcreader.api.retrofit.KisiService;
import com.hyperether.nfcreader.api.volley.ApiRequestManager;
import com.hyperether.nfcreader.api.volley.ApiResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NfcAdapter.CreateNdefMessageCallback {

    private NfcAdapter mNfcAdapter;
    private TextView txtNfcRead;
    private KisiService kisiApi;
    private ConstraintLayout contentMain;
    private String payload = NfcUtil.UNLOCK_CHECK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtNfcRead = (TextView) findViewById(R.id.txt_nfc_read);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.getkisi.com/locks/5124/access/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        kisiApi = retrofit.create(KisiService.class);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new NfcUtil().parseNfc(getIntent(), new NfcParser() {
            @Override
            public void onPayloadParsed(String result) {
                Log.e("reeader", "result: " + result);
                txtNfcRead.setText(result);
                if (NfcUtil.PAYLOAD_UNLOCK.equals(result)) {
//                        retrofitAPI();
                    volleyAPI();
                    showUnlockAnimation();
                }
            }
        });
    }

    private void retrofitAPI() {
        Call<ResponseBody> response = kisiApi.unlockKisi();
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                txtNfcRead.setText(response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                txtNfcRead.setText(t.getLocalizedMessage());
            }
        });
    }

    private void volleyAPI() {
        ApiRequestManager.getInstance().unlock(MainActivity.this, new ApiResponse() {
            @Override
            public void onSuccess(Object response) {
                KisiResponse apiResponse = new Gson()
                        .fromJson((String) response, KisiResponse.class);
                txtNfcRead.setText(apiResponse.getMessage());
            }

            @Override
            public void onError(String message) {
                txtNfcRead.setText(message);
            }
        });
    }

    private void showUnlockAnimation() {
        GifView gifView = new GifView(MainActivity.this);
        contentMain = (ConstraintLayout) findViewById(R.id.content_main);
        contentMain.addView(gifView);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.e("reeader", "createNdefMessage");
        NfcUtil nfcUtil = new NfcUtil();
        return nfcUtil.createNdefMessage2(payload);
    }
}

