package org.bawaberkah.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import static org.bawaberkah.app.MainActivity.EXTRA_ID;

public class fitur_zakat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitur_zakat);
    }

    public void goToKalkulator(View view) {
        Intent myIntent = new Intent(fitur_zakat.this,
                zakat_kalkulator.class);
        startActivity(myIntent);
    }

    public void goToMitra(View view) {
        Intent myIntent = new Intent(fitur_zakat.this,
                mitra_zakat.class);
        startActivity(myIntent);
    }

    public void goToBeranda(View view) {
        Intent myIntent = new Intent(fitur_zakat.this,
                MainActivity.class);
        startActivity(myIntent);
    }

    public void goToFitrah(View view) {
        Intent myIntent = new Intent(fitur_zakat.this,
                zakat_fitrah.class);
        startActivity(myIntent);
    }

    public void goToProfile(View view) {
        Intent myIntent = new Intent(fitur_zakat.this,
                profile.class);
        startActivity(myIntent);
    }

    public void goToFaq(View view) {
        Intent i = new Intent(fitur_zakat.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }
}
