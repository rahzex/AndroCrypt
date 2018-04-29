package com.noobs.rahulpal.androcrypt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Arrays;

public class CreateVault extends AppCompatActivity {

    private RadioGroup rg;
    private RadioButton Algo;
    Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Create New Vault");
        setContentView(R.layout.activity_vault_creation);


            Button CreateVault = (Button) findViewById(R.id.buttonCreateVault);
            CreateVault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context = view.getContext();
                    final Intent it = new Intent(getApplicationContext(), MainActivity.class);

                    EditText Vault_Name = (EditText) findViewById(R.id.editTextVaultName);
                    EditText Password = (EditText) findViewById(R.id.editTextPassword);
                    EditText Confirm_Password = (EditText) findViewById(R.id.editTextConfirmPass);

                    rg = (RadioGroup) findViewById(R.id.radioGroup);
                    int selectedAlgo = rg.getCheckedRadioButtonId();
                    Algo = (RadioButton) findViewById(selectedAlgo);

                    vault v = new vault();

                    v.vaultName = Vault_Name.getText().toString();
                    v.password = Password.getText().toString();
                    v.confirmPass = Confirm_Password.getText().toString();
                    v.algorithm = Algo.getText().toString();

                    String finalKey = "";

                    //Vault form fields checking and database entry
                    boolean ifFieldEmpty = nullFieldCheck(v);
                    if (ifFieldEmpty) {
                        Toast.makeText(context, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                    } else {

                        if (checkingSpace(v.vaultName)) {
                            Toast.makeText(context, "Fields can't contain Space", Toast.LENGTH_SHORT).show();
                        } else {

                            boolean passOk = passwordCheck(v.password);
                            if (passOk) {

                                if ((v.password).equals(v.confirmPass)) {

                                    boolean ifExits = new TableController(context).ifVaultNameAlreadyExists(v.vaultName);

                                    if (ifExits) {
                                        Toast.makeText(context, v.vaultName + " Already Exists", Toast.LENGTH_SHORT).show();
                                    } else {

                                        finalKey = Arrays.toString(androCrypter.getHashedKey(v.password,"CREATE_VAULT",v.vaultName));

                                        //Toast.makeText(context, finalKey, Toast.LENGTH_LONG).show();

                                        boolean vaultCreationSuccessful = new TableController(context).addNewVault(finalKey,v.vaultName,v.algorithm);

                                        if (vaultCreationSuccessful) {
                                            new AlertDialog.Builder(context)
                                                    .setMessage("Vault Created Successfully. Please Open Vault to Proceed.")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    }).show();

                                        } else {
                                            Toast.makeText(context, "Unable to Create Vault!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Passwords Did not Match", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                new AlertDialog.Builder(context)
                                        .setMessage("Password must contain a Uppercase, a Lowercase letter, a Number & a Special Character ")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    public boolean passwordCheck(String Pass) {

        int i, j, k, l;
        boolean passOK = false;
        boolean passNumChk = false;
        boolean passUppercaseChk = false;
        boolean passLowerChk = false;
        boolean passCharChk = false;
        String Password = Pass;
        if (Password.length() >= 8) {
            for (i = 0; i < Password.length(); i++) {
                boolean NumChk = false;
                boolean UpperCaseChk = false;
                boolean LowerCaseChk = false;

                //Number Check
                for (k = 48; k <= 57; k++) {
                    if (Password.charAt(i) == k) {
                        NumChk = true;
                        if(passNumChk == false)
                            passNumChk = true;
                        break;
                    }
                }

                //LowerCase Check
                for (j = 97; j <= 122; j++) {
                    if (Password.charAt(i) == j) {
                        LowerCaseChk = true;
                        if(passLowerChk == false)
                            passLowerChk = true;
                        break;
                    }
                }

                //UpperCase Check
                for (l = 65; l <= 90; l++) {
                    if (Password.charAt(i) == l) {
                        UpperCaseChk = true;
                        if(passUppercaseChk == false)
                            passUppercaseChk = true;
                        break;
                    }
                }

                //Char Check
                if (passCharChk == false) {
                    if(NumChk == false && UpperCaseChk == false && LowerCaseChk == false)
                        passCharChk = true;
                }

                if (passNumChk == true && passUppercaseChk == true && passLowerChk == true && passCharChk == true) {
                    passOK = true;
                    break;
                }
            }
        }
        else
            Toast.makeText(context,"Password must be minimum 8 Characters Long",Toast.LENGTH_SHORT).show();

        Password = null;
        return passOK;
    }

    private boolean nullFieldCheck(vault V){
        String Empty = "";
        boolean ifFieldIsEmpty = false;
        if(V.vaultName.equals(Empty) || V.vaultName == null ||
                V.password.equals(Empty) || V.password == null ||
                V.confirmPass.equals(Empty) || V.confirmPass == null){

            ifFieldIsEmpty = true;
        }

        return ifFieldIsEmpty;
    }

    public boolean checkingSpace(String  str){

        boolean ifSpacePresent = false;

        if(spaceCheck(str))
            ifSpacePresent = true;

        return ifSpacePresent;
    }

    public boolean spaceCheck(String str){

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

