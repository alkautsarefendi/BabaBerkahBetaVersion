package org.bawaberkah.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class mitra_zakat extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitra_zakat);
    }

    public void goToDompet(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                dompet_dhuafa.class);
        myIntent.putExtra(EXTRA_ID, "1");
        startActivity(myIntent);
    }

    public void goToYht(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                dompet_dhuafa.class);
        myIntent.putExtra(EXTRA_ID, "6");
        startActivity(myIntent);
    }

    public void goToPitaKuning(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                dompet_dhuafa.class);
        myIntent.putExtra(EXTRA_ID, "2");
        startActivity(myIntent);
    }

    public void goToSyaakirin(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                dompet_dhuafa.class);
        myIntent.putExtra(EXTRA_ID, "3");
        startActivity(myIntent);
    }

    public void goToYmai(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                dompet_dhuafa.class);
        myIntent.putExtra(EXTRA_ID, "4");
        startActivity(myIntent);
    }

    public void goToMuhajirin(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                dompet_dhuafa.class);
        myIntent.putExtra(EXTRA_ID, "5");
        startActivity(myIntent);
    }

    public void goToBeranda(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                MainActivity.class);
        startActivity(myIntent);
    }

    public void goToProfile(View view) {
        Intent myIntent = new Intent(mitra_zakat.this,
                profile.class);
        startActivity(myIntent);
    }

    public void goToFaq(View view) {
        Intent i = new Intent(mitra_zakat.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }
}
