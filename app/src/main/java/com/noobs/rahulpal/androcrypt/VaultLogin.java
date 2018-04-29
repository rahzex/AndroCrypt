package com.noobs.rahulpal.androcrypt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;




public class VaultLogin extends AppCompatActivity {

    Context context = null;
    String vname = null;
    private byte[] Key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_login);

        Button vaultLogin = (Button) findViewById(R.id.buttonVaultLogin);
        vaultLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context = v.getContext();

                final Intent Files = new Intent(getApplicationContext(), VaultFiles.class);

                EditText Vault_Name = (EditText)findViewById(R.id.editTextLoginVName);
                EditText Attempted_Password = (EditText)findViewById(R.id.editTextLoginPass);

                vname = Vault_Name.getText().toString();
                String attemptedPassword = Attempted_Password.getText().toString();

                boolean ifFieldEmpty = nullFieldCheck(vname,attemptedPassword);
                if (ifFieldEmpty) {
                    Toast.makeText(context, "Both fields are Mandatory", Toast.LENGTH_SHORT).show();
                } else {

                    if (checkingSpace(vname)) {
                        Toast.makeText(context, "Vault Name can't contain Space", Toast.LENGTH_SHORT).show();
                    } else {
                        if(authenticateAttemptedPass(attemptedPassword)){
                            new AlertDialog.Builder(context)
                                    .setMessage("Vault Open Successful.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Files.putExtra("KEY",Key)
                                                    .putExtra("vname",vname);
                                            startActivity(Files);
                                            finish();
                                        }
                                    }).show();
                        }
                        else{
                            new AlertDialog.Builder(context)
                                    .setMessage("INVALID PASSWORD!!")
                                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                        }
                    }
                }


            }
        });

    }

    private boolean authenticateAttemptedPass(String Password){

        boolean authPassed;
        Key = androCrypter.getHashedKey(Password,"OPEN_VAULT",vname);
        String attemptedPassword = Arrays.toString(Key);
        //Toast.makeText(context, attemptedPassword, Toast.LENGTH_SHORT).show();
        authPassed = new TableController(context).vaultLogin(vname,attemptedPassword);

        return authPassed;
    }

    private boolean nullFieldCheck(String vaultName,String password){
        String Empty = "";
        boolean ifFieldIsEmpty = false;
        if(vaultName.equals(Empty) || vaultName == null ||
                password.equals(Empty) || password == null ){

            ifFieldIsEmpty = true;
        }

        return ifFieldIsEmpty;
    }

    private boolean checkingSpace(String  str){

        boolean ifSpacePresent = false;

        if(spaceCheck(str))
            ifSpacePresent = true;

        return ifSpacePresent;
    }

    private boolean spaceCheck(String str){

        int i;
        boolean containsSpace = false;
        for(i=0;i<str.length();i++)
        {
            if(str.charAt(i) == ' ') {
                containsSpace = true;
                break;
            }
        }

        return containsSpace;
    }
}
