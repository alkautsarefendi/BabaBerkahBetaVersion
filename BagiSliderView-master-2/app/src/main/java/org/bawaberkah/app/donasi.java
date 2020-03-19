package org.bawaberkah.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.bawaberkah.app.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.bawaberkah.app.MainActivity.EXTRA_ID;
import static org.bawaberkah.app.MainActivity.EXTRA_TERKUMPUL;
import static org.bawaberkah.app.zakat_fitrah.EXTRA_FITRAH;
import static org.bawaberkah.app.zakat_kalkulator.EXTRA_ZAKAT;

public class donasi extends AppCompatActivity {
    private TextView idCampaign, _metode, idGoogle, labelHandphone, labelTerkumpul, header;
    private EditText namaDonatur, emailDonatur, handphoneDonatur, pesanDonatur, jmlDonasi, voucher;
    private CheckBox anonym;
    private Button btndonasi, signout, signin;
    private RelativeLayout lineBorder, layoutNama;
    private LinearLayout layoutLogin;
    int id;
    String danaTerkumpul, danaZakat, danaFitrah;
    private RadioButton dana, ovo, gopay, bca, mandiri, bni, bri, alfa, virtual;
    private boolean success = false; // boolean

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
        setContentView(R.layout.activity_donasi);

        /*imei_ = (TextView)findViewById(R.id.lblImei);
        TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = tm.getDeviceId();
        imei_.setText(device_id);*/

        Intent intent = getIntent();
        id = intent.getIntExtra(EXTRA_ID,0);

        danaTerkumpul = intent.getStringExtra(EXTRA_TERKUMPUL);

        _metode = (TextView)findViewById(R.id.lblMetode);
        idCampaign = (TextView)findViewById(R.id.lblIdCampaign);
        String a = String.valueOf(id);
        idCampaign.setText(a);
        labelTerkumpul = (TextView)findViewById(R.id.lblDanaTerkumpul) ;
        labelTerkumpul.setText(danaTerkumpul);

        //Cek Minimal & Maksimal Donasi
        jmlDonasi = (EditText)findViewById(R.id.txtJumlah);
            jmlDonasi.addTextChangedListener(new NumberTextWatcherForThousand(jmlDonasi));

        header = (TextView)findViewById(R.id.txtHeader);

        namaDonatur = (EditText)findViewById(R.id.txtNama);
        emailDonatur = (EditText)findViewById(R.id.txtEmail);
        handphoneDonatur = (EditText)findViewById(R.id.txthandphone);
        pesanDonatur = (EditText)findViewById(R.id.txtpesan);
        anonym = (CheckBox)findViewById(R.id.chkanonim);

        //RadioButton Metode Pembayaran

        dana = (RadioButton)findViewById(R.id.metDana);
        ovo = (RadioButton)findViewById(R.id.metOvo);
        gopay = (RadioButton)findViewById(R.id.metGopay);
        bca = (RadioButton)findViewById(R.id.metBca);
        mandiri = (RadioButton)findViewById(R.id.metMandiri);
        bni = (RadioButton)findViewById(R.id.metBni);
        bri = (RadioButton)findViewById(R.id.metBri);
        alfa = (RadioButton)findViewById(R.id.metAlfa);
        virtual = (RadioButton)findViewById(R.id.metVirtual);
        voucher = (EditText)findViewById(R.id.txtVoucher);

        btndonasi = (Button)findViewById(R.id.btnDonasi);
        btndonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regisDonatur();
            }
        });

        if (id == 2000 || id == 2001 || id == 2002 || id == 2003 || id == 2004 || id == 2005 || id == 2006){
            danaZakat = intent.getStringExtra(EXTRA_ZAKAT);
            jmlDonasi.setText(danaZakat);
            jmlDonasi.setFocusable(false);
            header.setText("Zakat");
            //btndonasi.setText("Bayar Zakat");
        } else if (id == 2007){
            danaFitrah = intent.getStringExtra(EXTRA_FITRAH);
            jmlDonasi.setText(danaFitrah);
            jmlDonasi.setFocusable(false);
            header.setText("Zakat Fitrah");
            //btndonasi.setText("Bayar Zakat");
        } else if (id > 3000){
            header.setText("Wakaf");
            //btndonasi.setText("Bayar Wakaf");
        }

        //Google Sign-in
        signInButton = (SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        idGoogle = (TextView)findViewById(R.id.lblIdGoogle);
        labelHandphone = (TextView)findViewById(R.id.lblhandphone);
        lineBorder = (RelativeLayout)findViewById(R.id.lineIsiForm);
        layoutNama = (RelativeLayout)findViewById(R.id.layoutnama);
        layoutLogin = (LinearLayout)findViewById(R.id.layoutLogin);

        //signin = (Button)findViewById(R.id.sign_in_button);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
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

        /*
        //Check Google Account Last Signin
        signout = (Button)findViewById(R.id.log_out);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(donasi.this);
        if (acct != null){
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            Uri personPhoto = acct.getPhotoUrl();

            namaDonatur.setText(personName);
            namaDonatur.setEnabled(false);
            anonym.setEnabled(false);
            emailDonatur.setText(personEmail);
            emailDonatur.setEnabled(false);

            //Glide.with(this).load(personPhoto).into(ImageView);

            signInButton.setVisibility(View.GONE);
            signout.setVisibility(View.VISIBLE);

        }

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });*/

    }

    private void signOut (){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(donasi.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                idGoogle.setText("");
                layoutNama.setVisibility(View.GONE);
                //namaDonatur.setText("");
                //namaDonatur.setClickable(true);
                //namaDonatur.setFocusableInTouchMode(true);
                //namaDonatur.setFocusable(true);
                //anonym.setVisibility(View.VISIBLE);
                //anonym.setEnabled(true);
                //emailDonatur.setText("");
                //emailDonatur.setFocusableInTouchMode(true);
                //emailDonatur.setFocusable(true);
                labelHandphone.setVisibility(View.GONE);
                handphoneDonatur.setVisibility(View.GONE);
                lineBorder.setVisibility(View.GONE);

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
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            layoutNama.setVisibility(View.VISIBLE);
            namaDonatur.setText(personName);
            namaDonatur.setFocusable(false);
            emailDonatur.setText(personEmail);
            emailDonatur.setFocusable(false);
            lineBorder.setVisibility(View.GONE);
            anonym.setVisibility(View.GONE);
            labelHandphone.setVisibility(View.GONE);
            handphoneDonatur.setVisibility(View.GONE);
            idGoogle.setText(personId);

            //Glide.with(this).load(personPhoto).into(ImageView);

            signInButton.setVisibility(View.GONE);
            signout.setVisibility(View.VISIBLE);
        } catch (ApiException e){
            Toast.makeText(donasi.this, "Failed to connect Google", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStart(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(donasi.this);
        if (acct != null){
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            layoutNama.setVisibility(View.VISIBLE);
            namaDonatur.setText(personName);
            namaDonatur.setFocusable(false);
            anonym.setVisibility(View.GONE);
            emailDonatur.setText(personEmail);
            emailDonatur.setFocusable(false);
            idGoogle.setText(personId);
            labelHandphone.setVisibility(View.GONE);
            handphoneDonatur.setVisibility(View.GONE);
            lineBorder.setVisibility(View.GONE);

            //Glide.with(this).load(personPhoto).into(ImageView);

            signInButton.setVisibility(View.GONE);
            signout.setVisibility(View.VISIBLE);

        }
        super.onStart();
    }

    private void regisDonatur(){

        final String id = idCampaign.getText().toString();
        final String donasi = jmlDonasi.getText().toString().replace(",","");
        final String nama = namaDonatur.getText().toString();
        final String email = emailDonatur.getText().toString();
        final String handphone = handphoneDonatur.getText().toString();
        final String pesan = pesanDonatur.getText().toString();
        final String _voucher = voucher.getText().toString();
        final String metode = _metode.getText().toString();
        final String terkumpul = labelTerkumpul.getText().toString();
        final String googleId = idGoogle.getText().toString();
        Double dasar;
        Double u = Double.parseDouble(id);
        if (u > 2000){
            dasar = 50000.0;
        } else {
            dasar = 10000.0;
        }

        //Check if Google Account signed in
        if (idGoogle.equals(null) | idGoogle.equals("")){
            //Check empty fields

            if(donasi.isEmpty()){
                Toast.makeText(donasi.this,"Nilai tidak boleh kosong",Toast.LENGTH_SHORT).show();
                jmlDonasi.setFocusableInTouchMode(true);
                jmlDonasi.setFocusable(true);
            } else {
                final Double nilai = Double.parseDouble(donasi);
                if(nilai < dasar){
                    Toast.makeText(donasi.this,"Nilai minimal adalah Rp "+ String.valueOf(dasar)+",-",Toast.LENGTH_SHORT).show();
                } else if (nilai > 999999999999.0){
                    Toast.makeText(donasi.this,"Nilai maksimal adalah Rp 999 Milyar",Toast.LENGTH_SHORT).show();
                } else if(nama.isEmpty()){
                    Toast.makeText(donasi.this,"Nama tidak boleh kosong",Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(donasi.this,"Email tidak boleh kosong",Toast.LENGTH_SHORT).show();
                } else if(handphone.isEmpty()){
                    Toast.makeText(donasi.this,"No Handphone tidak boleh kosong",Toast.LENGTH_SHORT).show();
                } else if(dana.isChecked() | ovo.isChecked() | gopay.isChecked()| bca.isChecked()| mandiri.isChecked()
                        | bni.isChecked()| bri.isChecked()| alfa.isChecked()| virtual.isChecked()){

                    if(dana.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan DANA belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(ovo.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan OVO belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(gopay.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan GOPAY belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(bca.isChecked()){
                        _metode.setText("4");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(mandiri.isChecked()){
                        _metode.setText("5");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(bni.isChecked()){
                        _metode.setText("6");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(bri.isChecked()){
                        _metode.setText("7");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(alfa.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan Alfamart belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(virtual.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan Virtual Account belum tersedia",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(donasi.this,"Silahkan pilih metode pembayaran Donasi",Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            //Check empty fields

            if(donasi.isEmpty()){
                Toast.makeText(donasi.this,"Nilai tidak boleh kosong",Toast.LENGTH_SHORT).show();
                jmlDonasi.setFocusableInTouchMode(true);
                jmlDonasi.setFocusable(true);
            } else {
                final Double nilai = Double.parseDouble(donasi);
                if(nilai < dasar){
                    Toast.makeText(donasi.this,"Nilai minimal adalah Rp "+ String.valueOf(dasar)+",-",Toast.LENGTH_SHORT).show();
                } else if (nilai > 999999999999.0){
                    Toast.makeText(donasi.this,"Nilai maksimal adalah Rp 999 Milyar",Toast.LENGTH_SHORT).show();
                } else if(nama.isEmpty()){
                    Toast.makeText(donasi.this,"Nama tidak boleh kosong",Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(donasi.this,"Email tidak boleh kosong",Toast.LENGTH_SHORT).show();
                } else if(dana.isChecked() | ovo.isChecked() | gopay.isChecked()| bca.isChecked()| mandiri.isChecked()
                        | bni.isChecked()| bri.isChecked()| alfa.isChecked()| virtual.isChecked()){

                    if(dana.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan DANA belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(ovo.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan OVO belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(gopay.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan GOPAY belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(bca.isChecked()){
                        _metode.setText("4");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        bundle.putString("ID_GOOGLE", googleId);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(mandiri.isChecked()){
                        _metode.setText("5");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        bundle.putString("ID_GOOGLE", googleId);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(bni.isChecked()){
                        _metode.setText("6");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        bundle.putString("ID_GOOGLE", googleId);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(bri.isChecked()){
                        _metode.setText("7");
                        Intent i = new Intent(donasi.this, pembayaran_donasi.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("CAMPAIGN_ID", id);
                        bundle.putString("CAMPAIGN_NAMA", nama);
                        bundle.putString("CAMPAIGN_EMAIL", email);
                        bundle.putString("CAMPAIGN_HP", handphone);
                        bundle.putString("CAMPAIGN_PESAN", pesan);
                        bundle.putString("CAMPAIGN_METODE", _metode.getText().toString());
                        bundle.putString("CAMPAIGN_VOUCHER", _voucher);
                        bundle.putString("CAMPAIGN_NOMINAL", donasi);
                        bundle.putString("CAMPAIGN_TERKUMPUL", terkumpul);
                        bundle.putString("ID_GOOGLE", googleId);
                        i.putExtras(bundle);
                        startActivity(i);
                    } else if(alfa.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan Alfamart belum tersedia",Toast.LENGTH_SHORT).show();
                    } else if(virtual.isChecked()){
                        Toast.makeText(donasi.this,"Pembayaran dengan Virtual Account belum tersedia",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(donasi.this,"Silahkan pilih metode pembayaran Donasi",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void changeAnonim(View view) {

        if(anonym.isChecked()){
            namaDonatur.setText("Anonim");
            namaDonatur.setFocusable(false);
        } else {
            namaDonatur.setText("");
            namaDonatur.setFocusableInTouchMode(true);
            namaDonatur.setFocusable(true);
        }

    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        //Get Params
        final String _donasi = jmlDonasi.getText().toString().replace(",","");
        final String _nama = namaDonatur.getText().toString();
        final String _email = emailDonatur.getText().toString();
        final String _handphone = handphoneDonatur.getText().toString();
        final String _pesan = pesanDonatur.getText().toString();
        final String _voucher = voucher.getText().toString();
        final String _id = idCampaign.getText().toString();
        final String metode = _metode.getText().toString();


        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(donasi.this, "Sedang memproses ...",
                    "Selangkah lagi menjadi Pahlawan Donasi", true);
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
                    String query = "INSERT INTO donator(nama, email, handphone) VALUES ('" + _nama + "','" + _email + "'," +
                            "'" + _handphone + "')";
                    Statement stmt = conn.createStatement();
                    int  res = stmt.executeUpdate(query);
                    if(res>0) {
                        conn.close();
                        msg = "Lanjutkan ke pembayaran donasi anda";
                        success = true;
                    }
                    else
                    {
                        conn.close();
                        msg = "Terdapat kesalahan dalam proses penyimpanan";
                        success = false;
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
            Intent i = new Intent(donasi.this, pembayaran_donasi.class);
            Bundle bundle = new Bundle();
            bundle.putString("CAMPAIGN_ID", _id);
            bundle.putString("CAMPAIGN_NAMA", _nama);
            bundle.putString("CAMPAIGN_EMAIL", _email);
            bundle.putString("CAMPAIGN_HP", _handphone);
            bundle.putString("CAMPAIGN_PESAN", _pesan);
            bundle.putString("CAMPAIGN_METODE", metode);
            bundle.putString("CAMPAIGN_VOUCHER", _voucher);
            bundle.putString("CAMPAIGN_NOMINAL", _donasi);
            i.putExtras(bundle);
            startActivity(i);
        }
    }
}
