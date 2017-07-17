package com.hyperether.nfctag;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hyperether.nfchelper.NfcParser;
import com.hyperether.nfchelper.NfcUtil;

public class MainActivity extends AppCompatActivity implements
        NfcAdapter.CreateNdefMessageCallback {

    private NfcAdapter mNfcAdapter;
    private Handler payloadHandler;
    private Runnable payloadRunnable;
    private String payload = NfcUtil.PAYLOAD_NOTHING;

    private TextView txtLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLock = (TextView) findViewById(R.id.txt_lock);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        payloadHandler = new Handler();
        payloadRunnable = new Runnable() {
            @Override
            public void run() {
                payload = NfcUtil.PAYLOAD_NOTHING
                        .equals(payload) ? NfcUtil.PAYLOAD_UNLOCK : NfcUtil.PAYLOAD_NOTHING;
                txtLock.setText(payload);
                payloadHandler.postDelayed(this, 3000);
            }
        };
        payloadHandler.post(payloadRunnable);

        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new NfcUtil().parseNfc(getIntent(), new NfcParser() {
            @Override
            public void onPayloadParsed(String result) {
                if (NfcUtil.UNLOCK_CHECK.equals(result)) {
                    Log.e("taaag", "result: " + result);
                }
            }
        });
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.e("taaag", "createNdefMessage");
        NfcUtil nfcUtil = new NfcUtil();
        return nfcUtil.createNdefMessage2(payload);
    }
}
