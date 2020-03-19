package org.bawaberkah.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.bawaberkah.app.R;

public class hubungi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hubungi);
    }

    public void goToFaq(View view) {
        Intent i = new Intent(hubungi.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }

    public void goToProfile(View view) {
        Intent myIntent = new Intent(hubungi.this,
                profile.class);
        startActivity(myIntent);
    }
}
