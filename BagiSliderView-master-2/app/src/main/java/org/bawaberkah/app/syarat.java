package org.bawaberkah.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class syarat extends AppCompatActivity {

    private TextView syarat, header;
    private String idSyarat;

    private boolean success = false;

    //Declare Params for MySQL Connection
    private static final String USER = "u279445120_tyo";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://185.224.137.216/u279445120_baber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syarat);

        syarat = (TextView)findViewById(R.id.labelSyarat);
        header = (TextView)findViewById(R.id.txtHeader);

        Intent intent = getIntent();
        idSyarat = intent.getExtras().getString("ID");

        if (idSyarat.equals("1")){
            header.setText("Syarat dan Ketentuan");
        } else if (idSyarat.equals("2")){
            header.setText("Kebijakan Privasi");
        } else if (idSyarat.equals("3")){
            header.setText("Tentang Bawa Berkah");
        } else if (idSyarat.equals("4")){
            header.setText("FAQ");
        }

        SyncData orderData = new SyncData();
        orderData.execute("");
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(syarat.this, "BawaBerkah",
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
                    String query = "SELECT keterangan FROM tblsyarat where id = '" + idSyarat + "'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final String keterangan = rs.getString(1);

                    conn.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            syarat.setText(keterangan);
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
}
