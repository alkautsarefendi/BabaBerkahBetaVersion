package org.bawaberkah.app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.bawaberkah.app.MainActivity.EXTRA_JUDUL;
import static org.bawaberkah.app.MainActivity.EXTRA_TARGET;
import static org.bawaberkah.app.MainActivity.GOOGLE_ID;
import static org.bawaberkah.app.MainActivity.GOOGLE_DISPLAY;
import static org.bawaberkah.app.MainActivity.GOOGLE_EMAIL;

public class register_campaigner extends AppCompatActivity {

    private ImageView imageView, imageKtp;
    private CheckBox chkSetuju;
    private Button btnSetuju;
    private TextView idGoogle;
    private EditText lblNama, lblEmail, lblHandphone;
    private boolean success = false; // boolean
    private RelativeLayout layoutNama;

    //Google Sign-in
    int RC_SIGN_IN = 3;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    //Declare Params for MySQL Connection
    private static final String USER = "u279445120_tyo";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://185.224.137.216/u279445120_baber";

    private Button signout;

    private static String URL_UPLOAD = "http://gicell.online/upload.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_campaigner);

        imageView = (ImageView)this.findViewById(R.id.photoCampaigner);
        imageKtp = (ImageView)this.findViewById(R.id.photoKTP);
        chkSetuju = (CheckBox)this.findViewById(R.id.chkSetuju);
        btnSetuju = (Button)this.findViewById(R.id.btnRegistrasi);
        lblNama = (EditText)this.findViewById(R.id.txtNama);
        lblEmail = (EditText)this.findViewById(R.id.txtEmail);
        lblHandphone = (EditText)this.findViewById(R.id.txthandphone);
        layoutNama = (RelativeLayout) this.findViewById(R.id.layoutnama);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });

        imageKtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });

        //Google Sign-in
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        idGoogle = (TextView)findViewById(R.id.lblIdGoogle);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Check Google Account Last Signin
        signout = (Button)findViewById(R.id.log_out);
        signout.setTextAppearance(this, R.style.ButtonFontStyle);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        //Get Intent Data
        Intent intent = getIntent();
        String id = intent.getStringExtra(GOOGLE_ID);
        String nama = intent.getStringExtra(GOOGLE_DISPLAY);
        String email = intent.getStringExtra(GOOGLE_EMAIL);

        if (id.equals("")){
            signInButton.setVisibility(View.VISIBLE);
            signout.setVisibility(View.GONE);
            layoutNama.setVisibility(View.GONE);
        } else {
            SyncData orderData = new SyncData();
            orderData.execute("");
            layoutNama.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            signout.setVisibility(View.VISIBLE);
            idGoogle.setText(id);
            lblNama.setText(nama);
            lblEmail.setText(email);
        }

        btnSetuju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lblHandphone.getText().toString().equals("")){
                    Toast.makeText(register_campaigner.this, "Nomer Handphone tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (imageView.getDrawable() == null){
                    Toast.makeText(register_campaigner.this, "Poto Wajah/KTP tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (imageKtp.getDrawable() == null){
                    Toast.makeText(register_campaigner.this, "Poto Wajah/KTP tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else {
                    //Check if Donatur already Exist in Database
                }
            }
        });
    }

    private void signOut (){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(register_campaigner.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                idGoogle.setText("");
                lblNama.setText("");
                lblEmail.setText("");
                lblHandphone.setText("");
                layoutNama.setVisibility(View.GONE);

                signInButton.setVisibility(View.VISIBLE);
                signout.setVisibility(View.GONE);
            }
        });
    }

    private void signIn (){
        Intent signInIntent =  mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageKtp.setImageBitmap(bitmap);
        } else if (requestCode ==  RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),"no image has been taken", Toast.LENGTH_LONG).show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completeTask){
        try{
            GoogleSignInAccount account = completeTask.getResult(ApiException.class);
            final String personId = account.getId();
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            idGoogle.setText(personId);
            lblNama.setText(personName);
            lblEmail.setText(personEmail);
            layoutNama.setVisibility(View.VISIBLE);

            SyncData orderData = new SyncData();
            orderData.execute("");

            signInButton.setVisibility(View.GONE);
            signout.setVisibility(View.VISIBLE);

        } catch (ApiException e){
            Toast.makeText(register_campaigner.this, "Failed to connect Google", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCheckSetuju(View view) {
        if (chkSetuju.isChecked()){
            btnSetuju.setVisibility(View.VISIBLE);
        } else {
            btnSetuju.setVisibility(View.GONE);
        }
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        //Get Params
        final String googleId = idGoogle.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(register_campaigner.this, "BawaBerkah",
                    "Sinkronisasi profil ...", true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); //Connection Object
                if (conn == null) {
                    success = false;
                } else {
                    try{
                        String query = "SELECT handphone FROM `donator` WHERE googleId = '" + googleId + "'";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();
                        final String hp = rs.getString(1);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                lblHandphone.setText(hp);
                            }
                        });
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                lblHandphone.setText("");
                            }
                        });
                    }
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my ListView
        {
            progress.dismiss();
        }
    }

    public void goToProfile(View view) {
        Intent myIntent = new Intent(register_campaigner.this,
                profile.class);
        startActivity(myIntent);
    }

    public void goToFaq(View view) {
        Intent i = new Intent(register_campaigner.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }

    public void goToBeranda(View view) {
        Intent myIntent = new Intent(register_campaigner.this,
                MainActivity.class);
        startActivity(myIntent);
    }

    private class SaveDonatur extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        //Get Params
        final String googleId = idGoogle.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(register_campaigner.this, "BawaBerkah",
                    "Registrasi Campaigner ...", true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); //Connection Object
                if (conn == null) {
                    success = false;
                } else {
                    try{
                        String query = "SELECT nama FROM `donator` WHERE googleId = '" + googleId + "'";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();
                        final String hp = rs.getString(1);
                        conn.close();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                //Update Donatur
                                Thread t = new Thread() {
                                    public void run() {
                                        try {
                                            Class.forName("com.mysql.jdbc.Driver");
                                            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
                                            if (conn == null)
                                            {
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Koneksi ke database gagal, silahkan periksa jaringan anda " , Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                            else {
                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String response) {

                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {

                                                            }
                                                        })
                                                {

                                                };
                                            }
                                        }
                                        catch (final Exception x)
                                        {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Terdapat kesalaha pada data" , Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                };
                                t.start();
                            }
                        });
                    } catch (Exception e) {
                        conn.close();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                //Simpan Donatur Baru
                            }
                        });
                    }
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = writer.toString();
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my ListView
        {
            progress.dismiss();
        }
    }

    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }

}
