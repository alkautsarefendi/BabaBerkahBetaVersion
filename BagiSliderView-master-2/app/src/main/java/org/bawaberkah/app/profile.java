package org.bawaberkah.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class profile extends AppCompatActivity {

    TextView namaDonatur, emailDonatur, idDonatur, handphoneDonatur, idGoogle, totalDonasi, totalZakat, totalWakaf;
    Button signout;
    ImageView photo;
    private boolean success = false;

    //Declare Params for MySQL Connection
    private static final String USER = "u276179624_admin";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://194.59.164.7/u276179624_baber";

    //Google Sign-in
    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        namaDonatur = (TextView)findViewById(R.id.txtNama);
        emailDonatur = (TextView)findViewById(R.id.txtEmail);
        idDonatur = (TextView)findViewById(R.id.lblIdDonatur);
        handphoneDonatur = (TextView)findViewById(R.id.lblHandphoneDonatur);
        signout = (Button)findViewById(R.id.log_out);
        photo = (ImageView)findViewById(R.id.profile_image);


        totalDonasi = (TextView)findViewById(R.id.lblTotalDonasi);
        totalZakat = (TextView)findViewById(R.id.lblTotalZakat);
        totalWakaf = (TextView)findViewById(R.id.lblTotalWakaf);

        //Google Sign-in
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        idGoogle = (TextView)findViewById(R.id.lblIdGoogle);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //Check Google Account Last Signin
        signout.setTextAppearance(this, R.style.ButtonFontStyle);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    private void signOut (){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(profile.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                namaDonatur.setText("Profile Name");
                emailDonatur.setText("user@profile.com");
                totalDonasi.setText("Rp 0");
                totalZakat.setText("Rp 0");
                totalWakaf.setText("Rp 0");
                idGoogle.setText("");

                //Glide.with(this).load(personPhoto).into(ImageView);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==  RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completeTask){
        try{
            GoogleSignInAccount account = completeTask.getResult(ApiException.class);
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            Uri personPhoto = account.getPhotoUrl();
            String personId = account.getId();

            namaDonatur.setText(personName);
            emailDonatur.setText(personEmail);
            idGoogle.setText(personId);

            //Glide.with(this).load(personPhoto).into(photo);

            String idGoogleAnda = idGoogle.getText().toString();

            if (idGoogleAnda.equals("") || idGoogleAnda.equals(null)){

            } else {
                SyncDonasi orderDonasi = new SyncDonasi();
                orderDonasi.execute("");
            }


            signInButton.setVisibility(View.GONE);
            signout.setVisibility(View.VISIBLE);
        } catch (ApiException e){
            Toast.makeText(profile.this, "Failed to connect Google", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(profile.this);
        if (acct != null){
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            namaDonatur.setText(personName);
            emailDonatur.setText(personEmail);
            idGoogle.setText(personId);

            String idGoogleAnda = idGoogle.getText().toString();

            if (idGoogleAnda.equals("") || idGoogleAnda.equals(null)){

            } else {
                SyncDonasi orderDonasi = new SyncDonasi();
                orderDonasi.execute("");
            }


            //Glide.with(this).load(personPhoto).into(photo);

            signInButton.setVisibility(View.GONE);
            signout.setVisibility(View.VISIBLE);

        }
        super.onStart();
    }

    private class SyncDonasi extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;
        final String _idGoogle = idGoogle.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(profile.this, "BawaBerkah",
                    "Sinkronisasi profil anda", true);
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
                    // Change below query according to your own database.
                    String query = "SELECT SUM(nominal) as ZAKAT FROM donaturCampaign WHERE googleId = '" + _idGoogle + "' " +
                            "AND idCampaign < 2000 AND status = 1";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final String hasil = rs.getString(1);
                    String _hasil;
                    if (hasil == null){
                        _hasil = "0";
                    } else {
                        int x = Integer.parseInt(hasil);
                        NumberFormat nf = NumberFormat.getCurrencyInstance();
                        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
                        decimalFormatSymbols.setCurrencySymbol("Rp ");
                        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

                        String xyz = nf.format(x).trim();
                        _hasil = xyz.substring(0, xyz.length() - 3);
                    }
                    final String akhir = _hasil;
                    conn.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            if (akhir == "0"){
                                totalDonasi.setText("Rp " + akhir);
                            } else {
                                totalDonasi.setText(akhir);
                            }
                        }
                    });
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
            if (success = true){
                Thread t = new Thread() {
                    public void run() {
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
                            if (conn == null)
                            {

                            }
                            else {
                                String query = "SELECT SUM(nominal) as ZAKAT FROM donaturCampaign WHERE googleId = '" + _idGoogle + "' " +
                                        "AND idCampaign BETWEEN 2000 and 3000 AND status = 1";
                                Statement stmt = conn.createStatement();
                                ResultSet rs = stmt.executeQuery(query);
                                rs.next();
                                final String hasil = rs.getString(1);
                                String _hasil;
                                if (hasil == null){
                                    _hasil = "0";
                                } else {
                                    int x = Integer.parseInt(hasil);
                                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                                    DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
                                    decimalFormatSymbols.setCurrencySymbol("Rp ");
                                    ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

                                    String xyz = nf.format(x).trim();
                                    _hasil = xyz.substring(0, xyz.length() - 3);
                                }
                                final String akhir = _hasil;
                                conn.close();
                                new Handler(Looper.getMainLooper()).post(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (akhir == "0"){
                                            totalZakat.setText("Rp " + akhir);
                                        } else {
                                            totalZakat.setText(akhir);
                                        }

                                        Thread t = new Thread() {
                                            public void run() {
                                                try {
                                                    Class.forName("com.mysql.jdbc.Driver");
                                                    Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);
                                                    if (conn == null)
                                                    {

                                                    }
                                                    else {
                                                        String query = "SELECT SUM(nominal) as ZAKAT FROM donaturCampaign WHERE googleId = '" + _idGoogle + "' " +
                                                                "AND idCampaign >= 3000 AND status = 1";
                                                        Statement stmt = conn.createStatement();
                                                        ResultSet rs = stmt.executeQuery(query);
                                                        rs.next();
                                                        final String hasil = rs.getString(1);
                                                        String _hasil;
                                                        if (hasil == null){
                                                            _hasil = "0";
                                                        } else {
                                                            int x = Integer.parseInt(hasil);
                                                            NumberFormat nf = NumberFormat.getCurrencyInstance();
                                                            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
                                                            decimalFormatSymbols.setCurrencySymbol("Rp ");
                                                            ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

                                                            String xyz = nf.format(x).trim();
                                                            _hasil = xyz.substring(0, xyz.length() - 3);
                                                        }
                                                        final String akhir = _hasil;
                                                        conn.close();
                                                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                if (akhir == "0"){
                                                                    totalWakaf.setText("Rp " + akhir);
                                                                } else {
                                                                    totalWakaf.setText(akhir);
                                                                }
                                                            }
                                                        });
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

            progress.dismiss();
        }
    }

    private class SyncZakat extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;
        final String _idGoogle = idGoogle.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            /*progress = ProgressDialog.show(profile.this, "BawaBerkah",
                    "Sinkronisasi profil anda", true);*/
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
                    // Change below query according to your own database.
                    String query = "SELECT SUM(nominal) as ZAKAT FROM donaturCampaign WHERE googleId = '" + _idGoogle + "' " +
                            "AND idCampaign BETWEEN 2000 and 3000 AND status = 1";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final String hasil = rs.getString(1);
                    String _hasil;
                    if (hasil == null){
                        _hasil = "0";
                    } else {
                        int x = Integer.parseInt(hasil);
                        NumberFormat nf = NumberFormat.getCurrencyInstance();
                        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
                        decimalFormatSymbols.setCurrencySymbol("Rp ");
                        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

                        String xyz = nf.format(x).trim();
                        _hasil = xyz.substring(0, xyz.length() - 3);
                    }
                    final String akhir = _hasil;
                    conn.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            if (akhir == "0"){
                                totalZakat.setText("Rp " + akhir);
                            } else {
                                totalZakat.setText(akhir);
                            }
                        }
                    });
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

    private class SyncWakaf extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;
        final String _idGoogle = idGoogle.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            /*progress = ProgressDialog.show(profile.this, "BawaBerkah",
                    "Sinkronisasi profil anda", true);*/
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
                    // Change below query according to your own database.
                    String query = "SELECT SUM(nominal) as ZAKAT FROM donaturCampaign WHERE googleId = '" + _idGoogle + "' " +
                            "AND idCampaign >= 3000 AND status = 1";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final String hasil = rs.getString(1);
                    String _hasil;
                    if (hasil == null){
                        _hasil = "0";
                    } else {
                        int x = Integer.parseInt(hasil);
                        NumberFormat nf = NumberFormat.getCurrencyInstance();
                        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
                        decimalFormatSymbols.setCurrencySymbol("Rp ");
                        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

                        String xyz = nf.format(x).trim();
                        _hasil = xyz.substring(0, xyz.length() - 3);
                    }
                    final String akhir = _hasil;
                    conn.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            totalZakat.setText(akhir);
                        }
                    });
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

    public void goToBeranda(View view) {
        Intent myIntent = new Intent(profile.this,
                MainActivity.class);
        startActivity(myIntent);
    }

    public void goToSyarat(View view) {
        Intent i = new Intent(profile.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "1");
        i.putExtras(bundle);
        startActivity(i);
    }

    public void goToKebijakan(View view) {
        Intent i = new Intent(profile.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "2");
        i.putExtras(bundle);
        startActivity(i);
    }

    public void goToTentang(View view) {
        Intent i = new Intent(profile.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "3");
        i.putExtras(bundle);
        startActivity(i);
    }

    public void goToHubungi(View view) {
        Intent i = new Intent(profile.this, hubungi.class);
        startActivity(i);
    }

    public void goToFaq(View view) {
        Intent i = new Intent(profile.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }
}
