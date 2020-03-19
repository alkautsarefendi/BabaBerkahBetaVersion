package org.bawaberkah.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class zakat_kalkulator extends AppCompatActivity {

    private EditText penghasilanBulan, bonusBulan, zakatBulan, zakatTahun, pengeluaranBulanan;
    private Button hitung, bersih, zakat;

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_ZAKAT = "zakat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakat_kalkulator);

        penghasilanBulan = (EditText)findViewById(R.id.jumlahPenghasilan);
        penghasilanBulan.addTextChangedListener(new NumberTextWatcherForThousand(penghasilanBulan));
        bonusBulan = (EditText)findViewById(R.id.jumlahPendapatanLain);
        bonusBulan.addTextChangedListener(new NumberTextWatcherForThousand(bonusBulan));
        pengeluaranBulanan = (EditText)findViewById(R.id.jumlahPengeluaran);
        pengeluaranBulanan.addTextChangedListener(new NumberTextWatcherForThousand(pengeluaranBulanan));
        zakatBulan = (EditText)findViewById(R.id.jumlahZakatPerbulan);
        zakatTahun = (EditText)findViewById(R.id.jumlahZakatPertahun);

        hitung = (Button)findViewById(R.id.btnHitung);
        bersih = (Button)findViewById(R.id.btnBersih);
        zakat = (Button)findViewById(R.id.btnZakat);

    }

    public void hitungZakat(View view) {
        String a = penghasilanBulan.getText().toString();
        String b = bonusBulan.getText().toString();
        String c = pengeluaranBulanan.getText().toString();

        if (a.equals(null) || a.equals("")) {
            a = "0";
        } else if (b.equals(null) || b.equals("")) {
            bonusBulan.setText(0);
        } else if (c.equals(null) || c.equals("")){
            c = "0";
        } else {
            if (a.contains(",")){
                a = a.replace(",","");
            } else {
                a = a.replace(".","");
            }

            if (b.contains(",")){
                b = b.replace(",","");
            } else {
                b = b.replace(".","");
            }

            if (c.contains(",")){
                c = c.replace(",","");
            } else {
                c = c.replace(".","");
            }

            Double x = Double.parseDouble(a);
            Double y = Double.parseDouble(b);
            Double z = Double.parseDouble(c);
            Double hasil = (((x + y) - z) * 25)/1000;
            Double tahunan = hasil * 12;

            //Set Currency Format String

            NumberFormat nf = NumberFormat.getCurrencyInstance();
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

            String zakatBulanan = nf.format(hasil).trim();
            String zakatTahunan = nf.format(tahunan).trim();

            if (zakatBulanan.contains(",")){
                zakatBulanan = zakatBulanan.substring(0, zakatBulanan.length() - 3);
            }


            if (zakatTahunan.contains(",")){
                zakatTahunan = zakatTahunan.substring(0, zakatTahunan.length() - 3);
            }

            zakatBulan.setText(zakatBulanan);
            zakatTahun.setText(zakatTahunan);
        }
    }

    public void clearText(View view) {
        penghasilanBulan.setText("0");
        bonusBulan.setText("0");
        pengeluaranBulanan.setText("0");
        zakatBulan.setText("0");
        zakatTahun.setText("0");
    }

    public void bayarZakat(View view) {
        String _zakat = zakatBulan.getText().toString();

        if(_zakat.contains(",")){
            _zakat = _zakat.replace(",","");
        } else {
            _zakat = _zakat.replace(".","");
        }

        Intent myIntent = new Intent(zakat_kalkulator.this,
                donasi.class);
        myIntent.putExtra(EXTRA_ID, 2000);
        myIntent.putExtra(EXTRA_ZAKAT, _zakat);
        startActivity(myIntent);
    }
}
