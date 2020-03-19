package org.bawaberkah.app;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import java.util.Calendar;
import java.util.Random;

public class pembayaran_donasi extends AppCompatActivity {
    private EditText totalDonasi;
    private TextView mDonasi, mRekening, mNama, mId, mMetode, mBank, namaDonatur, emailDonatur, handphoneDonatur, pesanDonatur,
            voucherDonasi, mTotal, danaTerkumpul, totalTerkumpul, googleId;
    private ImageView mLogo;
    private boolean success = false;

    //Declare Params for MySQL Connection
    private static final String USER = "u276179624_admin";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://194.59.164.7/u276179624_baber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_donasi);

        totalDonasi = (EditText)findViewById(R.id.totalDonasi);
        mDonasi = (TextView)findViewById(R.id.kodeDonasi);
        mTotal = (TextView)findViewById(R.id.lblTotalDonasi);
        mRekening = (TextView) findViewById(R.id.noRekening);
        mNama = (TextView) findViewById(R.id.atasNama);
        mLogo = (ImageView)findViewById(R.id.logoBank);
        mId = (TextView) findViewById(R.id.lblID);
        mMetode = (TextView) findViewById(R.id.lblMetode);
        mBank = (TextView) findViewById(R.id.namaBank);
        namaDonatur = (TextView) findViewById(R.id.lblNamaDonatur);
        emailDonatur = (TextView) findViewById(R.id.lblEmailDonatur);
        handphoneDonatur = (TextView) findViewById(R.id.lblHandphoneDonatur);
        pesanDonatur = (TextView) findViewById(R.id.lblPesanDonatur);
        voucherDonasi = (TextView) findViewById(R.id.lblVoucher);
        danaTerkumpul = (TextView) findViewById(R.id.lblDanaTerkumpul);
        totalTerkumpul = (TextView) findViewById(R.id.lblTotalTerkumpul);
        googleId = (TextView)findViewById(R.id.lblIdGoogle);

        Intent intent = getIntent();
        String nama = intent.getExtras().getString("CAMPAIGN_NAMA");
        namaDonatur.setText(nama);
        String idGoogle = intent.getExtras().getString("ID_GOOGLE");
        googleId.setText(idGoogle);
        String email = intent.getExtras().getString("CAMPAIGN_EMAIL");
        emailDonatur.setText(email);
        String handphone = intent.getExtras().getString("CAMPAIGN_HP");
        handphoneDonatur.setText(handphone);
        String pesan = intent.getExtras().getString("CAMPAIGN_PESAN");
        pesanDonatur.setText(pesan);
        String voucher = intent.getExtras().getString("CAMPAIGN_VOUCHER");
        voucherDonasi.setText(voucher);
        String terkumpul = intent.getExtras().getString("CAMPAIGN_TERKUMPUL");
        danaTerkumpul.setText(terkumpul);
        String easyPuzzle = intent.getExtras().getString("CAMPAIGN_NOMINAL");

        String middlePart = easyPuzzle.substring(easyPuzzle.length() - 3);
        Random random = new Random();
        int x,z;
        int y = Integer.parseInt(easyPuzzle);

        if (middlePart.equals("000")){
            x = random.nextInt(300) + 100;
            z = x + y;
        } else {
            z = y;
        }

        String idCampaign = intent.getExtras().getString("CAMPAIGN_ID");
        mId.setText(idCampaign);
        String metode = intent.getExtras().getString("CAMPAIGN_METODE");
        mMetode.setText(metode);
        mTotal.setText(String.valueOf(z));

        //Get Kode Donasi

        Calendar calendar = Calendar.getInstance();
        String thisYear = String.valueOf(calendar.get(Calendar.YEAR)); //4
        String thisMonth = String.valueOf(calendar.get(Calendar.MONTH)); //2
        String thisDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); //2
        String thisHour = String.valueOf(calendar.get(Calendar.HOUR)); //2
        String thisMinute = String.valueOf(calendar.get(Calendar.MINUTE)); //2
        String thisSecond = String.valueOf(calendar.get(Calendar.SECOND)); //2

        String kode = thisYear + thisMonth + thisDay + thisHour + thisMinute + thisSecond;
        mDonasi.setText(kode);

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        String hasil = nf.format(z).trim();
        if (hasil.contains(".00")){
            hasil = hasil.substring(0, hasil.length() - 3);
        }
        //hasil = hasil.substring(0, hasil.length() - 3);

        totalDonasi.setText(hasil);
        SyncData orderData = new SyncData();
        orderData.execute("");

        totalDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Bawaberkah", totalDonasi.getText().toString().replace(".",""));
                clipboardManager.setPrimaryClip(clipData);
                Snackbar snackbar = Snackbar.make(v, "Nilai donasi " + totalDonasi.getText().toString() + " tersalin", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        mRekening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Bawaberkah", mRekening.getText().toString().replace(".",""));
                clipboardManager.setPrimaryClip(clipData);
                Snackbar snackbar = Snackbar.make(v, "No Rekening " + mRekening.getText().toString() + " tersalin", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    public void goToProfile(View view) {
        if(googleId.getText().toString().equals(null) || googleId.getText().toString().equals(""))
        {
            SaveDataGuest orderData = new SaveDataGuest();
            orderData.execute("");
        } else {
            SaveData orderData = new SaveData();
            orderData.execute("");
        }

    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            int x = Integer.parseInt(mId.getText().toString());
            if (x < 2000){
                progress = ProgressDialog.show(pembayaran_donasi.this, "Sedang memproses ...",
                        "Selangkah lagi menjadi Pahlawan Donasi", true);
            } else if (x >= 2000 && x < 3000){
                progress = ProgressDialog.show(pembayaran_donasi.this, "Sedang memproses ...",
                        "Selangkah lagi menjadi Pahlawan Zakat", true);
            } else if (x >= 3000){
                progress = ProgressDialog.show(pembayaran_donasi.this, "Sedang memproses ...",
                        "Selangkah lagi menjadi Pahlawan Wakaf", true);
            }

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
                    String query = "SELECT no_rekening, atas_nama, logo, nama_bank FROM daftarRekening WHERE id = '" + mMetode.getText().toString() + "'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final String norek = rs.getString(1);
                    final String nama = rs.getString(2);
                    final String logo = rs.getString(3);
                    final String bank = rs.getString(4);

                    conn.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            mRekening.setText(norek);
                            mNama.setText(nama);
                            mBank.setText(bank);
                            Glide.with(pembayaran_donasi.this).load(logo).into(mLogo);
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

    private class SaveData extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        //Get Params

        final String _donasi = mTotal.getText().toString();
        final String _nama = namaDonatur.getText().toString();
        final String _email = emailDonatur.getText().toString();
        final String _handphone = handphoneDonatur.getText().toString();
        final String _pesan = pesanDonatur.getText().toString();
        final String _voucher = voucherDonasi.getText().toString();
        final String _id = mId.getText().toString();
        final String metode = mMetode.getText().toString();
        final String _idGoogle = googleId.getText().toString();
        final String _kode = mDonasi.getText().toString();



        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(pembayaran_donasi.this, "Bawa Berkah",
                    "Sedang proses penyimpanan ...", true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                final Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); //Connection Object
                if (conn == null) {
                    success = false;
                } else {
                    String query = "INSERT INTO donator (googleId, nama, email, handphone)" +
                            "SELECT * FROM (SELECT '" + _idGoogle + "', '" + _nama + "', '" + _email + "', '" + _handphone + "') AS tmp " +
                            "WHERE NOT EXISTS (" +
                            "    SELECT nama FROM donator WHERE googleId = '" + _idGoogle + "'" +
                            ") LIMIT 1;";
                    Statement stmt = conn.createStatement();
                    int  res = stmt.executeUpdate(query);
                    if(res>0) {
                        conn.close();
                        success = true;
                        msg = "Terima Kasih atas Bantuan Donasinya di Bawaberkah";
                    }
                    else
                    {
                        conn.close();
                        msg = "Donatur telah tersimpan";
                        success = true;
                    }
                    //conn.close();
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
        protected void onPostExecute(String msg)
        {
            progress.dismiss();
            //Update Total Dana Terkumpul

            String totalDonasi = mTotal.getText().toString();
            if (totalDonasi.contains(",")){
                totalDonasi = totalDonasi.replace(",","");
            } else {
                totalDonasi = totalDonasi.replace(".","");
            }
            String danaSaatIni = danaTerkumpul.getText().toString();
            final String metodeBayar = mMetode.getText().toString();

            /*int x = Integer.parseInt(totalDonasi);
            int y = Integer.parseInt(danaSaatIni);
            int z = x + y;
            final String totalDanaTerkumpul = String.valueOf(z);*/

            final int x = Integer.parseInt(_id);

            if (success = true){
                //Save Transaction

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
                                String query = "INSERT INTO donaturCampaign(idCampaign, nama, catatan, nominal, metode, status, googleId, kodeTrx) " +
                                        "VALUES ('" + _id + "','" + _nama + "','" + _pesan + "','" + _donasi + "', '" + metodeBayar + "'" +
                                        ", '0', '" + _idGoogle + "', '" + _kode + "')";
                                Statement stmt = conn.createStatement();
                                int res = stmt.executeUpdate(query);
                                if (res > 0) {
                                    conn.close();
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                        @Override
                                        public void run() {
                                            if(x > 2000 && x < 3000){
                                                Toast.makeText(getApplicationContext(), "Terima Kasih telah berzakat di BawaBerkah" , Toast.LENGTH_LONG).show();
                                            } else if (x > 3000){
                                                Toast.makeText(getApplicationContext(), "Terima Kasih telah berwakaf di BawaBerkah" , Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Terima Kasih telah berdonasi di BawaBerkah" , Toast.LENGTH_LONG).show();
                                            }
                                            sendMessage();
                                            Intent i = new Intent(pembayaran_donasi.this, profile.class);
                                            startActivity(i);
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Terdapat Kesalahan pada proses penyimpanan" , Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                        catch (final Exception x)
                        {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), x.getMessage().toString() , Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                };
                t.start();
            }
        }
    }

    private class SaveDataGuest extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        //Get Params

        final String _donasi = mTotal.getText().toString();
        final String _nama = namaDonatur.getText().toString();
        final String _email = emailDonatur.getText().toString();
        final String _handphone = handphoneDonatur.getText().toString();
        final String _pesan = pesanDonatur.getText().toString();
        final String _voucher = voucherDonasi.getText().toString();
        final String _id = mId.getText().toString();
        final String metode = mMetode.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(pembayaran_donasi.this, "Bawa Berkah",
                    "Sedang proses penyimpanan ...", true);
        }

        @Override
        protected String doInBackground(String... strings)  // Connect to the database, write query and add items to array list
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                final Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); //Connection Object
                if (conn == null) {
                    success = false;
                } else {
                    String query = "INSERT INTO donator (googleId, nama, email, handphone)" +
                            "SELECT * FROM (SELECT '" + _nama + "', '" + _email + "', '" + _handphone + "') AS tmp " +
                            "WHERE NOT EXISTS (" +
                            "    SELECT nama FROM donator WHERE email = '" + _email + "'" +
                            ") LIMIT 1;";
                    Statement stmt = conn.createStatement();
                    int  res = stmt.executeUpdate(query);
                    if(res>0) {
                        conn.close();
                        success = true;
                        msg = "Terima Kasih atas Bantuan Donasinya di Bawaberkah";
                    }
                    else
                    {
                        conn.close();
                        msg = "Donatur telah tersimpan";
                        success = true;
                    }
                    //conn.close();
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
        protected void onPostExecute(String msg)
        {
            progress.dismiss();

            final int x = Integer.parseInt(_id);

            if (success = true){
                //Save Transaction

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
                                String query = "INSERT INTO donaturCampaign(idCampaign, nama, catatan, nominal, metode, status) " +
                                        "VALUES ('" + _id + "','" + _nama + "','" + _pesan + "','" + _donasi + "', '" + metode + "'" +
                                        ", '0')";
                                Statement stmt = conn.createStatement();
                                int res = stmt.executeUpdate(query);
                                if (res > 0) {
                                    conn.close();
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                        @Override
                                        public void run() {
                                            if(x > 2000 && x < 3000){
                                                Toast.makeText(getApplicationContext(), "Terima Kasih telah berzakat di BawaBerkah" , Toast.LENGTH_LONG).show();
                                            } else if (x > 3000){
                                                Toast.makeText(getApplicationContext(), "Terima Kasih telah berwakaf di BawaBerkah" , Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Terima Kasih telah berdonasi di BawaBerkah" , Toast.LENGTH_LONG).show();
                                            }
                                            Intent i = new Intent(pembayaran_donasi.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Terdapat Kesalahan pada proses penyimpanan" , Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                        catch (final Exception x)
                        {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), x.getMessage().toString() , Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                };
                t.start();
            }
        }
    }

    private void sendMessage() {
        final ProgressDialog dialog = new ProgressDialog(pembayaran_donasi.this);
        final String norek = mRekening.getText().toString();
        final String namaBank = mBank.getText().toString();
        final String tDonasi = totalDonasi.getText().toString();
        dialog.setTitle("Sending Email");
        dialog.setMessage("Please wait");
        dialog.show();
        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("info@bawaberkah.org", "pH9xknnS#2v$#U8");
                    sender.sendMail("Notifikasi Pembayaran",
                            "Segera transfer kebaikan Anda sebesar " + tDonasi + " (sampai 3 digit akhir) ke " + namaBank + " rek " + norek + " a.n Yayasan Dompet Dhuafa Republika dalam 24 jam. ",
                            "bawaberkah.online@gmail.com",
                            emailDonatur.getText().toString());
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();
    }
}
