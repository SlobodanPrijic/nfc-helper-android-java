package com.hyperether.nfchelper;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import static android.nfc.NdefRecord.createMime;

/**
 * Created by Slobodan on 7/17/2017.
 */

public class NfcUtil {

    public static final String UNLOCK_CHECK = "check_unlock";
    public static final String PAYLOAD_UNLOCK = "unlock";
    public static final String PAYLOAD_NOTHING = "nothing";

    /*
    <intent-filter>
    <action android:name="android.nfc.action.NDEF_DISCOVERED" />
    <category android:name="android.intent.category.DEFAULT" />
    <data android:mimeType="text/plain" />
    </intent-filter>
     */
    public NdefRecord createTextRecord(String payload, boolean encodeInUtf8) {
        Locale locale = Locale.getDefault();
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }

    /*
    <intent-filter>
    <action android:name="android.nfc.action.NDEF_DISCOVERED" />
    <category android:name="android.intent.category.DEFAULT" />
    <data android:scheme="http"
        android:host="example.com"
        android:pathPrefix="" />
    </intent-filter>
     */
    public NdefRecord createNdefRecordUri(String uriString) {
        return NdefRecord.createUri(uriString);
    }

    public NdefRecord createNdefRecordMime(String payload) {
        return NdefRecord.createMime("application/vnd.com.example.android.beam",
                payload.getBytes(Charset.forName("US-ASCII")));
    }

    public NdefRecord createNdefRecordMime2(String payload) {
        return new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "application/vnd.com.example.android.beam".getBytes(Charset.forName("US-ASCII")),
                new byte[0],
                payload.getBytes(Charset.forName("US-ASCII")));
    }

    public NdefMessage createNdefMessage1(String payload) {
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{createMime(
                        "application/vnd.com.example.android.beam", payload.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        //, NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }

    public NdefMessage createNdefMessage2(String payload) {
        return new NdefMessage(new NdefRecord[]{createTextRecord(payload, true)});
    }

    public NdefMessage createNdefMessageUri(String payload) {
        return new NdefMessage(new NdefRecord[]{createNdefRecordUri(payload)});
    }

    public NdefMessage createNdefMessageMime(String payload) {
        return new NdefMessage(new NdefRecord[]{createNdefRecordMime(payload)});
    }

    public NdefMessage createNdefMessageMime2(String payload) {
        return new NdefMessage(new NdefRecord[]{createNdefRecordMime2(payload)});
    }

    public void parseNfc(Intent intent, NfcParser callback) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                Parcelable[] rawMessages =
                        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (rawMessages != null) {
                    NdefMessage[] messages = new NdefMessage[rawMessages.length];
                    for (int i = 0; i < rawMessages.length; i++) {
                        messages[i] = (NdefMessage) rawMessages[i];
                    }
                    // Process the messages array.
                    String txtReceived = "";
                    NdefRecord firstRecord = messages[0].getRecords()[0];
                    byte[] payload = firstRecord.getPayload();
                    byte[] data = new byte[payload.length - 3];
                    System.arraycopy(payload, 3, data, 0, data.length);
                    try {
                        txtReceived = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    if (callback != null)
                        callback.onPayloadParsed(txtReceived);
                }
            }
        }
    }
}
