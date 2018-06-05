package com.noobs.rahulpal.androcrypt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileBrowser;
import com.aditya.filebrowser.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class VaultFiles extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 4;
    private static final int REMOVE_FILE_REQUEST = 5;
    Context context = null;
    private byte[] Key;
    private String vname = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_files);

        //close previous activity i.e vaultlogin
        //Intent closeActivity = new Intent(this,VaultFiles.class);
        //closeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final Intent i2 = new Intent(getApplicationContext(), FileChooser.class);
        final Intent i3 = new Intent(getApplicationContext(), FileChooser.class);
        //getting values from previous activity i.e VaultLogin
        Intent info = getIntent();
        Bundle bundle = info.getExtras();
        Key = bundle.getByteArray("KEY");
        vname = bundle.getString("vname");

        setTitle(vname + " Files");

        Button addFiles,removeFiles,viewFiles,closeVault;

        addFiles = (Button)findViewById(R.id.buttonAddFiles);
        addFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = v.getContext();
                i2.putExtra(Constants.SELECTION_MODE,Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
                startActivityForResult(i2,PICK_FILE_REQUEST);
            }
        });

        removeFiles = (Button)findViewById(R.id.buttonRemoveFiles);
        removeFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = v.getContext();
                i3.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory() + "/AndroCrypt/" + vname + "/").getAbsolutePath());
                i3.putExtra(Constants.SELECTION_MODE,Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
                startActivityForResult(i3,REMOVE_FILE_REQUEST);
            }
        });

        viewFiles = (Button)findViewById(R.id.buttonViewFiles);
        viewFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i4 = new Intent(getApplicationContext(), FileBrowser.class);
                i4.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory() + "/AndroCrypt/" + vname + "/").getAbsolutePath());
                startActivity(i4);
            }
        });

        closeVault = (Button)findViewById(R.id.buttonCloseVault);
        closeVault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Key = null;
                vname = null;
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FILE_REQUEST && data!=null) {
            if (resultCode == RESULT_OK) {
            // multiple file will be encrypted one by one
                ArrayList<Uri> selectedFiles  = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
                Uri addFilePath = null;
                for(int i =0;i<selectedFiles.size();i++){
                    addFilePath = selectedFiles.get(i);
                    encryptFiles(addFilePath);
                }
            }
        }
        if (requestCode == REMOVE_FILE_REQUEST && data!=null) {
            if (resultCode == RESULT_OK) {
            // multiple file will be Decrypted one by one
                ArrayList<Uri> selectedFiles  = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
                Uri removeFilePath = null;
                for(int i =0;i<selectedFiles.size();i++){
                    removeFilePath = selectedFiles.get(i);
                    decryptFiles(removeFilePath);
                }
            }
        }
    }

    private void encryptFiles(Uri file){

        final Uri filePath = file;
         class addingFiles extends AsyncTask<Void,Void,Void>{

             private ProgressDialog progressDialog = new ProgressDialog(VaultFiles.this);

             @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setTitle("Adding File");
                progressDialog.setMessage("Encrypting...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMax(100);
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
            }
             @Override
             protected Void doInBackground(Void... params) {
                 try {
                     androCrypter.encryptFile(Key,filePath,new TableController(context).getAlgo(vname),vname);
                 } catch (BadPaddingException e) {
                     e.printStackTrace();
                 } catch (IllegalBlockSizeException e) {
                     e.printStackTrace();
                 } catch (FileNotFoundException e) {
                     e.printStackTrace();
                 }
                 return null;
             }


            @Override
            protected void onPostExecute(Void result){
                progressDialog.dismiss();
                Toast.makeText(context,"File Added",Toast.LENGTH_SHORT).show();
            }
        }
        addingFiles ad = new addingFiles();
        ad.execute();
    }

    private void decryptFiles(Uri file){

        final Uri filePath = file;
        class movingFiles extends AsyncTask<Void,Void,Void>{

            private ProgressDialog progressDialog = new ProgressDialog(VaultFiles.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.setTitle("Moving Files");
                progressDialog.setMessage("Decrypting...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMax(100);
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
            }
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    androCrypter.decryptFile(Key,filePath,new TableController(context).getAlgo(vname));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void result){
                progressDialog.dismiss();
                Toast.makeText(context,"File Decrypted",Toast.LENGTH_SHORT).show();
            }
        }
        movingFiles mv = new movingFiles();
        mv.execute();
    }

}

