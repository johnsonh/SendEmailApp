package com.javacodegeeks.android.sendemailtest;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private static final String TAG = "Johnson App";

    private EditText recipient;
    private EditText subject;
    private EditText body;

    private ImageView mImageView;

    private static final String SAVED_IMAGE_MIME_TYPE = "image/png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // auto attach the image as an attachment?
        recipient = (EditText) findViewById(R.id.recipient);
        subject = (EditText) findViewById(R.id.subject);
        body = (EditText) findViewById(R.id.body);

        Button sendBtn = (Button) findViewById(R.id.sendEmail);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendEmail();
                // after sending the email, clear the fields
                recipient.setText("");
                subject.setText("");
                body.setText("");
            }
        });

        mImageView = (ImageView) findViewById(R.id.image);

        // hax happen here
        Intent intent = getIntent();
        if (intent.getType().equals(SAVED_IMAGE_MIME_TYPE)) {
            // it has an image that we can steal
            Uri uri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
            try {
                // this should really happen in a background service thread but time constraints
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Log.v(TAG, "Loaded the URI: " + uri.toString());
                mImageView.setImageBitmap(bitmap);
                Log.v(TAG, "Sending your private photo to bad-guy server...");
                // http://stackoverflow.com/questions/20322528/uploading-images-to-server-android
            } catch(Exception e) {
                Log.v(TAG, "Couldn't load the URI");
            }
        }


    }

    protected void sendEmail() {
        String[] recipients = {recipient.getText().toString()};
        Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
        // prompts email clients only
        email.setType("message/rfc822");

        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, body.getText().toString());

        try {
            // the user can choose the email client
            startActivity(Intent.createChooser(email, "Choose an email client from..."));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "No email client installed.",
                    Toast.LENGTH_LONG).show();
        }
    }

}
