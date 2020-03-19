package org.bawaberkah.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.SignInButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static org.bawaberkah.app.MainActivity.EXTRA_ID;
import static org.bawaberkah.app.MainActivity.EXTRA_JUDUL;
import static org.bawaberkah.app.MainActivity.EXTRA_TARGET;
import static org.bawaberkah.app.MainActivity.EXTRA_TERKUMPUL;
import static org.bawaberkah.app.MainActivity.EXTRA_URL;
import static org.bawaberkah.app.MainActivity.EXTRA_SINOPSIS;
import static org.bawaberkah.app.MainActivity.EXTRA_DETAIL;
import static org.bawaberkah.app.MainActivity.EXTRA_START;
import static org.bawaberkah.app.MainActivity.EXTRA_END;

public class detail extends AppCompatActivity{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static final String EXTRA_ID = "id";
    private static final String TAG = "AndroidClarified";
    private SignInButton googleSignInButton;

    //Declare Params for MySQL Connection
    private static final String USER = "u276179624_admin";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://194.59.164.7/u276179624_baber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Sinopsis"));
        tabLayout.addTab(tabLayout.newTab().setText("Update"));
        tabLayout.addTab(tabLayout.newTab().setText("Donatur"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Button btnDonasi = (Button)findViewById(R.id.btnOk);

        viewPager =(ViewPager)findViewById(R.id.viewpager);
        PagerAdapter tabsAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabsAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Declare Items
        ImageView imageCampaign = (ImageView)findViewById(R.id.imageCampaign);
        TextView judulCampaign = (TextView)findViewById(R.id.judulCampaign);
        TextView txtTerkumpul = (TextView)findViewById(R.id.txtTerkumpul);
        TextView txtTarget = (TextView)findViewById(R.id.target);
        TextView txtpercentase = (TextView)findViewById(R.id.persentase);
        TextView txtLama = (TextView)findViewById(R.id.lamacampaign);
        final TextView txtId = (TextView)findViewById(R.id.idcampaign);

        //Get Intent Data
        Intent intent = getIntent();
        final int id = intent.getIntExtra(EXTRA_ID,0);
        String judul = intent.getStringExtra(EXTRA_JUDUL);
        String target = intent.getStringExtra(EXTRA_TARGET);
        String terkumpul = intent.getStringExtra(EXTRA_TERKUMPUL);
        String path = intent.getStringExtra(EXTRA_URL);
        String _sinopsis = intent.getStringExtra(EXTRA_SINOPSIS);
        String _detail = intent.getStringExtra(EXTRA_DETAIL);
        String startx = intent.getStringExtra(EXTRA_START);
        String endx = intent.getStringExtra(EXTRA_END);

        txtId.setText(String.valueOf(id));

        Glide.with(this)
                .load(path)
                .into(imageCampaign);

        judulCampaign.setText(judul);

        if (terkumpul.contains(".")){
            terkumpul = terkumpul.replace(".","");
        } else if (terkumpul.contains(",")){
            terkumpul = terkumpul.replace(",","");
        }

        Double x1 = Double.parseDouble(target);
        Double x2 = Double.parseDouble(terkumpul);
        double percentage = 100.0D * x2 / x1;
        int y = (int)percentage;
        String perc = String.valueOf(y) + " %";

        //Set Currency Format String

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

        String hasil = nf.format(x2).trim();

        if (hasil.contains(".00")){
            hasil = hasil.substring(0, hasil.length() - 3);
        }

        txtTerkumpul.setText(hasil);
        txtpercentase.setText(perc);

        endx = endx.replace("-","");
        String tahun = endx.substring(0,4);
        endx = endx.substring(4);
        String bulan = endx.substring(0,2);
        String hari = endx.substring(2);

        String tglAwal = hari+"/"+bulan+"/"+tahun;
        txtLama.setText(tglAwal);

        //Send data to Fragment inside Tablayout

        FragmentManager manager = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction t = manager.beginTransaction();
        final FragmentSinopsis fragmentSinopsis = new FragmentSinopsis();

        Bundle bundle = new Bundle();
        bundle.putString("sinopsis",_sinopsis);
        bundle.putString("detail",_detail);
        bundle.putString("update","Belum ada Update");
        bundle.putString("idCampaign",String.valueOf(id));
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), bundle);
        viewPager.setAdapter(mPagerAdapter);

        final String danaTerkumpul = terkumpul;

        btnDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (detail.this, donasi.class);

                i.putExtra(EXTRA_ID, id);
                i.putExtra(EXTRA_TERKUMPUL, danaTerkumpul);
                startActivity(i);
            }
        });

        //Update Total Pengumpulan Dana Campaign

        Thread thread = new Thread() {
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); //Connection Object
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
                        String query = "SELECT SUM(nominal) AS TOTAL FROM `donaturCampaign` WHERE idCampaign = '" + txtId.getText().toString() + "' " +
                                "and status ='1'";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        rs.next();
                        final String total = rs.getString(1);
                        conn.close();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
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
                                                String query = "UPDATE campaign SET terkumpul= '" + total + "' WHERE id = '" + txtId.getText().toString() + "'";
                                                Statement stmt = conn.createStatement();
                                                int res = stmt.executeUpdate(query);
                                                if (res > 0) {
                                                    conn.close();
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            //Toast.makeText(getApplicationContext(), "Terima Kasih telah berdonasi di BawaBerkah" , Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                } else {
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Koneksi ke database gagal, silahkan periksa jaringan anda" , Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getApplicationContext(), x.getMessage().toString() , Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };
        thread.start();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private final Bundle fragmentBundle;

        public ScreenSlidePagerAdapter(FragmentManager fm, Bundle data){
            super(fm);
            fragmentBundle = data;
        }

        @Override
        public Fragment getItem(int arg0) {

            switch (arg0) {
                case 0:
                    final FragmentSinopsis fragmentSinopsis = new FragmentSinopsis();
                    fragmentSinopsis.setArguments(this.fragmentBundle);
                    return fragmentSinopsis;
                case 1:
                    final FragmentUpdate fragmentUpdate = new FragmentUpdate();
                    fragmentUpdate.setArguments(this.fragmentBundle);
                    return fragmentUpdate;
                case 2:
                    final FragmentDonatur fragmentDonatur = new FragmentDonatur();
                    fragmentDonatur.setArguments(this.fragmentBundle);
                    return fragmentDonatur;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return  3;
        }
    }
}
