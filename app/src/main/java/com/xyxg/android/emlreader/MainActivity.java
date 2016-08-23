package com.xyxg.android.emlreader;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.emailcommon.internet.MimeMessage;
import com.android.emailcommon.internet.MimeUtility;
import com.android.emailcommon.mail.Message;
import com.android.emailcommon.mail.MessagingException;
import com.android.emailcommon.mail.Part;
import com.android.emailcommon.utility.ConversionUtilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseEml(null);
            }
        }).start();
    }

    private void parseEml(File eml) {
        if (eml == null || !eml.exists() || eml.length() <= 0) {
            return;
        }
        try {
            InputStream in = new FileInputStream(eml);
            MimeMessage mimeMessage = new MimeMessage(in);
            Log.e(TAG, "Subject : " + mimeMessage.getSubject());
            Log.e(TAG, "From : " + Arrays.toString(mimeMessage.getFrom()));
            Log.e(TAG, "To : " + Arrays.toString(mimeMessage.getRecipients(Message.RecipientType.TO)));
            Log.e(TAG, "CC : " + Arrays.toString(mimeMessage.getRecipients(Message.RecipientType.CC)));
            Log.e(TAG, "BCC : " + Arrays.toString(mimeMessage.getRecipients(Message.RecipientType.BCC)));
            Log.e(TAG, "Date : " + mimeMessage.getSentDate());

            ArrayList<Part> viewables = new ArrayList<>();
            ArrayList<Part> attachments = new ArrayList<>();
            MimeUtility.collectParts(mimeMessage, viewables, attachments);

            ConversionUtilities.BodyFieldData data = ConversionUtilities.parseBodyFields(viewables);

            for (Part viewable : viewables) {
                if (viewable.getMimeType().startsWith("image")) {
                    final String contentTypeHeader = MimeUtility.unfoldAndDecode(viewable.getContentType());
                    String name = MimeUtility.getHeaderParameter(contentTypeHeader, "name");
                    Log.e(TAG, "parseBodyFields: " + name);
                    Log.e(TAG, "parseBodyFields: " + viewable.getContentType());
                    Log.e(TAG, "parseBodyFields: " + viewable.getContentId());
                    writeToFile(viewable, md5(viewable.getContentId()) + ".0");
                }
            }

            Log.e(TAG, "BodyText : " + data.textContent);
            Log.e(TAG, "BodyHtml : " + data.htmlContent);

            for (Part part : attachments) {// save attachments
                final String contentTypeHeader = MimeUtility.unfoldAndDecode(part.getContentType());
                String name = MimeUtility.getHeaderParameter(contentTypeHeader, "name");
                if (name == null) {
                    final String contentDisposition =
                            MimeUtility.unfoldAndDecode(part.getDisposition());
                    name = MimeUtility.getHeaderParameter(contentDisposition, "filename");
                }
                Log.e(TAG, "attachment : " + name);
                Log.e(TAG, "attachment type : " + part.getMimeType());
                writeToFile(part, name);
            }
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(Part viewable, String name) throws MessagingException, IOException {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + name;
        final InputStream inputStream = viewable.getBody().getInputStream();
        OutputStream out = new BufferedOutputStream(new FileOutputStream(path));
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        out.write(buffer);
        out.flush();
        out.close();
        inputStream.close();
    }

    public static String md5(String src) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return src;
        }

        char[] srcArray = src.toCharArray();
        byte[] byteArray = new byte[srcArray.length];

        for (int i = 0; i < srcArray.length; i++) {
            byteArray[i] = (byte) srcArray[i];
        }

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuilder hexValue = new StringBuilder();

        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append(0);
            }
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }
}
