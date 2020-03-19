package org.bawaberkah.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;

import static org.bawaberkah.app.MainActivity.EXTRA_ID;

public class dompet_dhuafa extends AppCompatActivity {

    private ImageView logo;
    private TextView nama, total, donatur, keterangan;
    private String id;
    private boolean success = false;

    //Declare Params for MySQL Connection
    private static final String USER = "u276179624_admin";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://194.59.164.7/u276179624_baber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dompet_dhuafa);

        logo = (ImageView)findViewById(R.id.logoMpz);
        nama = (TextView)findViewById(R.id.labelNama);
        total = (TextView)findViewById(R.id.totalZakat);
        donatur = (TextView)findViewById(R.id.totalDonatur);
        keterangan = (TextView)findViewById(R.id.lblDetail);

        Intent intent = getIntent();
        id = intent.getStringExtra(EXTRA_ID);

        SyncData orderData = new SyncData();
        orderData.execute("");
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(dompet_dhuafa.this, "BawaBerkah",
                    "Silahkan tunggu ...", true);
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
                    String query = "SELECT nama, tentang, logo, terkumpul, donator FROM tblmitra WHERE id = '" + id + "'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final String _nama = rs.getString(1);
                    final String _tentang = rs.getString(2);
                    final String _logo = rs.getString(3);
                    final int _terkumpul = Integer.parseInt(rs.getString(4));
                    final int _donatur = Integer.parseInt(rs.getString(5));

                    conn.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            nama.setText(_nama);
                            keterangan.setText(_tentang);
                            //format terkumpul
                            NumberFormat nf = NumberFormat.getNumberInstance();
                            nf.setMaximumFractionDigits(0);
                            String forDisplay = nf.format(_terkumpul);
                            total.setText(forDisplay);
                            //format donatur
                            NumberFormat nf1 = NumberFormat.getNumberInstance();
                            nf1.setMaximumFractionDigits(0);
                            String forDisplay1 = nf1.format(_donatur);
                            donatur.setText(forDisplay1);
                            Glide.with(dompet_dhuafa.this).load(_logo).into(logo);
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
        Intent myIntent = new Intent(dompet_dhuafa.this,
                MainActivity.class);
        startActivity(myIntent);
    }

    public void goToBayar(View view) {
        int x = Integer.parseInt(id);
        int y = 2000;
        int z = x + y;

        Intent myIntent = new Intent(dompet_dhuafa.this,
                donasi.class);
        myIntent.putExtra(EXTRA_ID, z);
        startActivity(myIntent);
    }

    public void goToProfile(View view) {
        Intent myIntent = new Intent(dompet_dhuafa.this,
                profile.class);
        startActivity(myIntent);
    }

    public void goToFaq(View view) {
        Intent i = new Intent(dompet_dhuafa.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }
}
