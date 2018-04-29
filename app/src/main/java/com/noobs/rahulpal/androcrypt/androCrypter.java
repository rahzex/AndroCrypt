package com.noobs.rahulpal.androcrypt;

/**
 * Created by Rahul Pal on 4/17/2017.
 */

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class androCrypter {


    private static final int pswdIterations = 65536  ;
    private static final int keySize = 256;
    private static byte[] ivBytes;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    static void encryptFile(byte[] hashedKey, Uri inFilePath, String algo, String vaultName) throws BadPaddingException, IllegalBlockSizeException, FileNotFoundException {
        //inFile and outFile to be declared
        File inDir = new File(inFilePath.getPath());
        FileInputStream inFile = new FileInputStream(inDir);

        File outDir = new File(Environment.getExternalStorageDirectory() + "/AndroCrypt/" + vaultName + "/");
        File filePath = new File(outDir,inDir.getName());
        FileOutputStream outFile = new FileOutputStream(filePath);


        SecretKeySpec secret = new SecretKeySpec(hashedKey, algo);

        //initializing Cipher
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(algo+"/CBC/PKCS7Padding","SC");
            cipher.init(Cipher.ENCRYPT_MODE, secret,new IvParameterSpec(ivBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        //file encryption
        byte[] input = new byte[64];
        int bytesRead;

        try {
            while ((bytesRead = inFile.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null)
                    outFile.write(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] output = cipher.doFinal();

        if (output != null)
            try {
                outFile.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }


        //delete original file
        //deleteFile(plainFilePath);

        try {
            inFile.close();
            outFile.flush();
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        resetValues(cipher);

    }

     static void decryptFile(byte[] hashedKey,Uri file,String algo) throws IOException {

         File filePath = new File(file.getPath());
         File decryptedFilePath = new File(Environment.getExternalStorageDirectory() + "/Decrypted Files/");
         File decryptedFile = new File(decryptedFilePath,filePath.getName());

        //create path if does not exist
        if(!decryptedFilePath.exists())
            decryptedFilePath.mkdir();

        FileInputStream fis = new FileInputStream(filePath.getPath());
        FileOutputStream fos = new FileOutputStream(decryptedFile);

        SecretKeySpec secret = new SecretKeySpec(hashedKey, algo);
        //test bc
         String proC = null;
        // file decryption
         Cipher cipher = null;
         try {
             cipher = Cipher.getInstance(algo+"/CBC/PKCS5Padding","SC");
             proC = cipher.getProvider().toString();
             cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
         } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
             e.printStackTrace();
         } catch (NoSuchProviderException e) {
             e.printStackTrace();
         }

         byte[] in = new byte[64];
        int read;
        while ((read = fis.read(in)) != -1) {
            byte[] output = cipher.update(in, 0, read);
            if (output != null)
                fos.write(output);
        }

         byte[] output = new byte[0];
         try {
             output = cipher.doFinal();
         } catch (IllegalBlockSizeException | BadPaddingException e) {
             e.printStackTrace();
         }
         if (output != null)
            fos.write(output);

         //delete original file
         deleteFile(filePath);

        fis.close();
        fos.flush();
        fos.close();
        resetValues(cipher);
    }

    protected static byte[] getHashedKey(String password,String MODE,String VaultName) {

        byte[] saltIV = new byte[48];
        byte[] salt = new byte[32];
        ivBytes = new byte[16];

        if(MODE.equals("CREATE_VAULT")){
            //generate salt ,iv & save them
            salt = generateSalt();
            ivBytes = generateIV();

            System.arraycopy(salt, 0, saltIV, 0, salt.length);
            System.arraycopy(ivBytes, 0, saltIV, salt.length, ivBytes.length);

            // save salt & iv
            // get the path to sdcard
            // to this path add a new directory path

            File dir = new File(Environment.getExternalStorageDirectory() + "/AndroCrypt/" + VaultName + "/");

            // create this directory if not already created
            if(!dir.exists())
                 dir.mkdirs();

            // create the file in which we will write the contents
            File file = new File(dir, VaultName);
            FileOutputStream saltIvOutFile = null;
            try {
                saltIvOutFile = new FileOutputStream(file);
                saltIvOutFile.write(saltIV);
                saltIvOutFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(MODE.equals("OPEN_VAULT")){
            //read the salt nad iv
            String path = Environment.getExternalStorageDirectory() + "/AndroCrypt/" + VaultName + "/" + VaultName;
            //Toast.makeText(context, path, Toast.LENGTH_SHORT).show();
            FileInputStream saltIvInFile = null;
            try {
                saltIvInFile = new FileInputStream(path);
                saltIvInFile.read(saltIV);
                saltIvInFile.close();
                //Toast.makeText(context, "Salt Iv Read Done", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(context, "Salt Iv Read Ex", Toast.LENGTH_SHORT).show();
            }

            System.arraycopy(saltIV, 0, salt, 0, salt.length);
            System.arraycopy(saltIV, salt.length, ivBytes, 0, ivBytes.length);
        }

        // Derive the key
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256","SC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //Toast.makeText(context, "NoSuchAlgorithm Ex", Toast.LENGTH_SHORT).show();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            //Toast.makeText(context, "NoSuchProvider Ex", Toast.LENGTH_SHORT).show();
        }
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                pswdIterations,
                keySize
        );

        SecretKey secretKey = null;
        try {
            secretKey = factory.generateSecret(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            //Toast.makeText(context, "Invalid Key Ex", Toast.LENGTH_SHORT).show();
        }

        return secretKey.getEncoded();
    }

    private static void deleteFile(File filePath){
        filePath.delete();
    }

    private static byte[] generateSalt() {
        byte[] saltValue = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(saltValue);
        return saltValue;
    }

    private static byte[] generateIV() {
        byte[] ivValue = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivValue);
        return ivValue;
    }

    private static void resetValues(Cipher cipher)
    {
        cipher = null;
    }


}




