package com.example.nfcreader2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfcreader2.R;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter intentFilter;
    Tag myTag;
    Context context;
    IntentFilter[] intentFiltersArray;
    TextView textViewShow;
    Button buttonRead;
    private String[][] techListsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewShow = findViewById(R.id.textViewShow);
        buttonRead = findViewById(R.id.buttonRead);

//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if(nfcAdapter == null){
//            Toast.makeText(this,"This device does not support NFC", Toast.LENGTH_SHORT).show();
//        }
//
//        //readFromContent(getIntent());
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
//                PendingIntent.FLAG_MUTABLE);
//        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        try {
//            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
//                                       You should specify only the ones that you need. */
//        }
//        catch (IntentFilter.MalformedMimeTypeException e) {
//            throw new RuntimeException("fail", e);
//        }
//        intentFiltersArray = new IntentFilter[]{ndef,};
//
//        Intent tagDetected = null;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if(msgs == null || msgs.length == 0) return;

        String text = "";
        byte[] playLoad = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((playLoad[0] & 128) == 0) ? "UTF-8": "UTF-16";
        int languageEncodedLength = playLoad[0] & 0063;

        try{
            text = new String(playLoad, languageEncodedLength + 1,playLoad.length - languageEncodedLength, textEncoding);
        }catch (UnsupportedEncodingException e){
            Log.e("UnsupportedEncoding",e.toString());
        }
        Log.e("check", text);
        textViewShow.setText(text);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareUltralight.get(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        MifareUltralightTagTester mifareUltralightTagTester = new MifareUltralightTagTester();
        textViewShow.setText(mifareUltralightTagTester.readTag(tagFromIntent));
    }

    public void onPause() {
        super.onPause();
        //nfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        //nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

}
//    private void readFromContent(Intent intent){
//        String action = intent.getAction();
//        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
//                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
//            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage[] msgs = null;
//            if(rawMsgs != null){
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++){
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                    Log.e("check", "read from content");
//                }
//            }
//            buildViewTag(msgs);
//        }
//    }
//
//    private void buildViewTag(NdefMessage[] msgs){
//        if(msgs == null || msgs.length == 0) return;
//
//        String text = "";
//        byte[] playLoad = msgs[0].getRecords()[0].getPayload();
//        String textEncoding = ((playLoad[0] & 128) == 0) ? "UTF-8": "UTF-16";
//        int languageEncodedLength = playLoad[0] & 0063;
//
//        try{
//            text = new String(playLoad, languageEncodedLength + 1,playLoad.length - languageEncodedLength, textEncoding);
//        }catch (UnsupportedEncodingException e){
//            Log.e("UnsupportedEncoding",e.toString());
//        }
//        Log.e("check", text);
//        textViewShow.setText(text);
//    }