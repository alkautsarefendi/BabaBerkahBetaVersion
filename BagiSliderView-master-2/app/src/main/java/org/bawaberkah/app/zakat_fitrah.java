package org.bawaberkah.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mysql.jdbc.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;


public class zakat_fitrah extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_FITRAH = "fitrah" ;
    private static final String EXTRA_ID = "id" ;
    Spinner customSpinner;
    EditText satuan, total;
    ArrayList<zakatItem> zakatList;
    CheckBox setuju;
    Button zakat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zakat_fitrah);

        customSpinner = (Spinner)findViewById(R.id.customIconSpinner);
        satuan = (EditText)findViewById(R.id.fitrahPersatuan);
        total = (EditText)findViewById(R.id.totalZakatFitrah);
        setuju = (CheckBox)findViewById(R.id.chkPersetujuan);
        zakat = (Button)findViewById(R.id.btnZakat);

        zakatList = getZakatList();
        zakatAdapter adapter = new zakatAdapter(this, zakatList);
        if (customSpinner != null) {
            customSpinner.setAdapter(adapter);
            customSpinner.setOnItemSelectedListener(this);
        }
    }

    public void itemClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        if(checkBox.isChecked()){
            zakat.setVisibility(View.VISIBLE);
        } else {
            zakat.setVisibility(View.GONE);
        }
    }

    private ArrayList<zakatItem> getZakatList(){
        zakatList = new ArrayList<>();
        zakatList.add(new zakatItem("1", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("2", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("3", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("4", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("5", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("6", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("7", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("8", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("9", R.drawable.ic_action_person));
        zakatList.add(new zakatItem("10", R.drawable.ic_action_person));
        return  zakatList;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String _satuan = satuan.getText().toString();
        _satuan = _satuan.replace(".","");
        _satuan = _satuan.substring(3);
        int x = Integer.parseInt(_satuan);
        String _total = total.getText().toString();
        if (_total.contains(".")){
            _total = _total.replace(".","");
            _total = _total.substring(3);
        } else if (_total.contains(",")){
            _total = _total.replace(",","");
            _total = _total.substring(3);
        }

        int z = Integer.parseInt(_total);
        zakatItem item = (zakatItem) adapterView.getSelectedItem();
        String _jml = item.getSpinnerItemName();
        int y = Integer.parseInt(_jml);
        int hasil = x * y;

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("Rp ");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

        String xyz = nf.format(hasil).trim();
        String substr = xyz.substring(xyz.length() - 3);
        if (substr.contains(".00")){
            xyz = xyz.substring(0, xyz.length() - 3);
        } else if (substr.contains(",00")){
            xyz = xyz.substring(0, xyz.length() - 3);
        }
        total.setText(xyz);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void goToBeranda(View view) {
        Intent myIntent = new Intent(zakat_fitrah.this,
                MainActivity.class);
        startActivity(myIntent);
    }

    public void goToProfile(View view) {
        Intent myIntent = new Intent(zakat_fitrah.this,
                profile.class);
        startActivity(myIntent);
    }

    public void goToFaq(View view) {
        Intent i = new Intent(zakat_fitrah.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }

    public void goToDonasi(View view) {
        String zakat = total.getText().toString();
        if (zakat.contains(".")){
            zakat = zakat.replace(".","");
            zakat = zakat.substring(3);
        } else if (zakat.contains(",")){
            zakat = zakat.replace(",","");
            zakat = zakat.substring(3);
        }

        Intent myIntent = new Intent(zakat_fitrah.this,
                donasi.class);
        myIntent.putExtra(EXTRA_ID, 2007);
        myIntent.putExtra(EXTRA_FITRAH, zakat);
        startActivity(myIntent);
    }
}
