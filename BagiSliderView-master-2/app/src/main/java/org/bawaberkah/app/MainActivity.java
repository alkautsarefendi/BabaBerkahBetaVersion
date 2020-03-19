package org.bawaberkah.app;

import android.app.NativeActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.bawaberkah.app._sliders.FragmentSlider;
import org.bawaberkah.app._sliders.SliderIndicator;
import org.bawaberkah.app._sliders.SliderPagerAdapter;
import org.bawaberkah.app._sliders.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.interfaces.DSAPublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import static org.bawaberkah.app.ProductsAdapter.*;

public class MainActivity extends AppCompatActivity implements ProductsAdapter.OnItemClickListener, BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_URL = "path";
    public static final String EXTRA_JUDUL = "judul";
    public static final String EXTRA_TARGET = "target";
    public static final String EXTRA_TERKUMPUL = "terkumpul";
    public static final String EXTRA_SINOPSIS = "sinopsis";
    public static final String EXTRA_DETAIL = "detail";
    public static final String EXTRA_START = "start";
    public static final String EXTRA_END = "end";

    public static final String GOOGLE_ID = "id";
    public static final String GOOGLE_DISPLAY = "display";
    public static final String GOOGLE_EMAIL = "email";

    private SliderPagerAdapter mAdapter;
    private SliderIndicator mIndicator;
    private Parcelable recyclerViewState;
    ImageView favorit;
    //private SliderView sliderView;
    private SliderLayout sliderLayout;
    private LinearLayout mLinearLayout;
    private RecyclerView recyclerView;
    private List<Product> productList;
    private EditText editText;
    private final String URL_PRODUCTS = "http://bawaberkah.online/api/load_data.php";
    SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton btnLainnya;
    boolean doubleBackToExitPressedOnce = false;

    HashMap<String,String> Hash_file_maps ;
    private final String URL_BANNER = "http://bawaberkah.online/api/load_banner.php";

    //Google Sign-in
    int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private TextView idGoogle, judulCampaign, userDisplay, userEmail;

    private boolean success = false;

    //Declare Params for MySQL Connection
    private static final String USER = "u276179624_admin";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://194.59.164.7/u276179624_baber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        mLinearLayout = (LinearLayout) findViewById(R.id.pagesContainer);
        //this.setupSlider();
        SyncBanner();

        this.productList = new ArrayList();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerTemp);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.loadProducts();

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);
                loadProducts();
            }
        });

        editText = (EditText)findViewById(R.id.search);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(MainActivity.this  , search.class);
                startActivity(detailIntent);
            }
        });

        btnLainnya = (ImageButton)findViewById(R.id.lainnya);

        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        idGoogle = (TextView)findViewById(R.id.lblIdGoogle);
        judulCampaign = (TextView)findViewById(R.id.lblJudulCampaign);
        userDisplay = (TextView)findViewById(R.id.lblUserDisplay);
        userEmail = (TextView)findViewById(R.id.lblUserEmail);
        signIn();
    }

    @Override
    protected void onStart(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (acct != null){
            String personId = acct.getId();
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();

            idGoogle.setText(personId);
            userDisplay.setText(personName);
            userEmail.setText(personEmail);

        }
        super.onStart();
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
            String personId = account.getId();
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();

            idGoogle.setText(personId);
            userDisplay.setText(personName);
            userEmail.setText(personEmail);

        } catch (ApiException e){
            Toast.makeText(MainActivity.this, "Failed to connect Google", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void setupSlider() {
        sliderView.setDurationScroll(800);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FragmentSlider.newInstance("http://gicell.online/banner/bg_campaign1.jpeg"));
        fragments.add(FragmentSlider.newInstance("http://gicell.online/banner/bg_campaign2.jpg"));
        fragments.add(FragmentSlider.newInstance("http://gicell.online/banner/bg_campaign3.jpeg"));
        fragments.add(FragmentSlider.newInstance("http://gicell.online/banner/bg_campaign4.jpeg"));
        fragments.add(FragmentSlider.newInstance("http://gicell.online/banner/bg_campaign5.jpeg"));

        mAdapter = new SliderPagerAdapter(getSupportFragmentManager(), fragments);
        mAdapter.notifyDataSetChanged();
        sliderView.setAdapter(mAdapter);
        mIndicator = new SliderIndicator(this, mLinearLayout, sliderView, R.drawable.indicator_circle);
        mIndicator.setPageCount(fragments.size());
        mIndicator.show();
    }*/

    private void loadProducts() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        productList.clear();
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                int id = product.getInt("id");
                                String judul = product.getString("judul");
                                String target = product.getString("target");
                                String terkumpul = product.getString("terkumpul");
                                String path = product.getString("path");
                                String sinopsis = product.getString("sinopsis");
                                String detail = product.getString("detail");
                                String start = product.getString("start");
                                String end = product.getString("end");
                                int b = product.getInt("percentage");

                                Double a = Double.parseDouble(terkumpul);
                                NumberFormat nf = NumberFormat.getNumberInstance();
                                nf.setMaximumFractionDigits(0);
                                String forDisplay = nf.format(a);

                                Double x1 = Double.parseDouble(target);
                                Double x2 = Double.parseDouble(terkumpul);
                                double percentage = 100.0D * x2 / x1;
                                int y = (int)percentage;
                                int z;

                                if(y>100){
                                    z = 100;
                                }else{
                                    z = y;
                                }


                                Product _list = new Product(id, judul, target, forDisplay, path, z, sinopsis
                                ,detail, start, end);
                                MainActivity.this.productList.add(_list);
                            }

                            ProductsAdapter adapter = new ProductsAdapter(MainActivity.this, productList);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListener(MainActivity.this);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onDonasiClick(int position) {
        Intent detailIntent = new Intent(this, donasi.class);
        Product clickedItem = productList.get(position);

        detailIntent.putExtra(EXTRA_ID, clickedItem.getId());
        /*detailIntent.putExtra(EXTRA_JUDUL, clickedItem.getJudul());
        detailIntent.putExtra(EXTRA_TARGET, clickedItem.getTarget());
        detailIntent.putExtra(EXTRA_TERKUMPUL, clickedItem.getTerkumpul());
        detailIntent.putExtra(EXTRA_URL, clickedItem.getPath());*/

        startActivity(detailIntent);
    }

    @Override
    public void onDetailClick(int position) {
        Intent detailIntent = new Intent(this, detail.class);
        Product clickedItem = productList.get(position);

        detailIntent.putExtra(EXTRA_ID, clickedItem.getId());
        detailIntent.putExtra(EXTRA_JUDUL, clickedItem.getJudul());
        detailIntent.putExtra(EXTRA_TARGET, clickedItem.getTarget());
        detailIntent.putExtra(EXTRA_TERKUMPUL, clickedItem.getTerkumpul());
        detailIntent.putExtra(EXTRA_URL, clickedItem.getPath());
        detailIntent.putExtra(EXTRA_SINOPSIS, clickedItem.getSinopsis());
        detailIntent.putExtra(EXTRA_DETAIL, clickedItem.getDetail());
        detailIntent.putExtra(EXTRA_START, clickedItem.getStart());
        detailIntent.putExtra(EXTRA_END, clickedItem.getEnd());

        startActivity(detailIntent);
    }

    @Override
    public void onImageClick(int position) {
        Intent detailIntent = new Intent(this, detail.class);
        Product clickedItem = productList.get(position);

        detailIntent.putExtra(EXTRA_ID, clickedItem.getId());
        detailIntent.putExtra(EXTRA_JUDUL, clickedItem.getJudul());
        detailIntent.putExtra(EXTRA_TARGET, clickedItem.getTarget());
        detailIntent.putExtra(EXTRA_TERKUMPUL, clickedItem.getTerkumpul());
        detailIntent.putExtra(EXTRA_URL, clickedItem.getPath());
        detailIntent.putExtra(EXTRA_SINOPSIS, clickedItem.getSinopsis());
        detailIntent.putExtra(EXTRA_DETAIL, clickedItem.getDetail());
        detailIntent.putExtra(EXTRA_START, clickedItem.getStart());
        detailIntent.putExtra(EXTRA_END, clickedItem.getEnd());

        startActivity(detailIntent);
    }

    @Override
    public void onJudulClick(int position) {
        Intent detailIntent = new Intent(this, detail.class);
        Product clickedItem = productList.get(position);

        detailIntent.putExtra(EXTRA_ID, clickedItem.getId());
        detailIntent.putExtra(EXTRA_JUDUL, clickedItem.getJudul());
        detailIntent.putExtra(EXTRA_TARGET, clickedItem.getTarget());
        detailIntent.putExtra(EXTRA_TERKUMPUL, clickedItem.getTerkumpul());
        detailIntent.putExtra(EXTRA_URL, clickedItem.getPath());
        detailIntent.putExtra(EXTRA_SINOPSIS, clickedItem.getSinopsis());
        detailIntent.putExtra(EXTRA_DETAIL, clickedItem.getDetail());
        detailIntent.putExtra(EXTRA_START, clickedItem.getStart());
        detailIntent.putExtra(EXTRA_END, clickedItem.getEnd());

        startActivity(detailIntent);
    }

    public void goToProfile(View view) {
        Intent myIntent = new Intent(MainActivity.this,
                profile.class);
        startActivity(myIntent);
    }

    public void goToDondasiCampaign(View view) {
        Intent myIntent = new Intent(MainActivity.this,
                search.class);
        startActivity(myIntent);
    }

    public void goToMenuZakat(View view) {
        Intent myIntent = new Intent(MainActivity.this,
                fitur_zakat.class);
        startActivity(myIntent);
    }

    public void goToMenuWakaf(View view) {
        Intent myIntent = new Intent(MainActivity.this,
                search_wakaf.class);
        startActivity(myIntent);
    }

    public void goToImsakiyah(View view) {
        Intent myIntent = new Intent(MainActivity.this,
                imsakiyah.class);
        startActivity(myIntent);
    }

    public void goToCampaigner(View view) {
        SyncId syncId = new SyncId();
        syncId.execute("");
    }

    public void goToFaq(View view) {
        Intent i = new Intent(MainActivity.this, syarat.class);
        Bundle bundle = new Bundle();
        bundle.putString("ID", "4");
        i.putExtras(bundle);
        startActivity(i);
    }

    public void goToPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle("BawaBerkah");
        builder.setIcon(R.drawable.ic_launcher_bb);
        builder.setMessage("\n"+"Nantikan fitur kami lainnya");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "tekan BACK sekali lagi untuk keluar aplikasi", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onSliderClick(final BaseSliderView slider) {

        judulCampaign.setText(slider.getBundle().get("extra").toString());

        SyncCampaign orderData = new SyncCampaign();
        orderData.execute("");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {

        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void SyncBanner() {

        Hash_file_maps = new HashMap<String, String>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);
                                String judul = product.getString("judul");
                                String path = product.getString("pathImage");

                                Hash_file_maps.put(judul, path);

                            }

                            for(String name : Hash_file_maps.keySet()){

                                TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                                textSliderView
                                        .description(name)
                                        .image(Hash_file_maps.get(name))
                                        .setScaleType(BaseSliderView.ScaleType.Fit)
                                        .setOnSliderClickListener(MainActivity.this);
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle()
                                        .putString("extra",name);
                                sliderLayout.addSlider(textSliderView);
                            }
                            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
                            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                            sliderLayout.setCustomAnimation(new DescriptionAnimation());
                            sliderLayout.setDuration(5000);
                            sliderLayout.addOnPageChangeListener(MainActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onStop() {

        sliderLayout.startAutoCycle();
        //sliderLayout.stopAutoCycle();
        super.onStop();
    }

    private class SyncCampaign extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        //Get Params
        final String jdlCampaign = judulCampaign.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(MainActivity.this, "BawaBerkah",
                    "Menuju Campaign " + jdlCampaign + "...", true);
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
                    String query = "SELECT id, dana, terkumpul, path, sinopsis, detail, start, end FROM `campaign` where judul = '" + jdlCampaign + "'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final int id = rs.getInt(1);
                    final String target = rs.getString(2);
                    final String terkumpul = rs.getString(3);
                    final String path = rs.getString(4);
                    final String sinopsis = rs.getString(5);
                    final String detail = rs.getString(6);
                    final String start = rs.getString(7);
                    final String end = rs.getString(8);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Intent detailIntent = new Intent(MainActivity.this, detail.class);

                            detailIntent.putExtra(EXTRA_ID, id);
                            detailIntent.putExtra(EXTRA_JUDUL, jdlCampaign);
                            detailIntent.putExtra(EXTRA_TARGET, target);
                            detailIntent.putExtra(EXTRA_TERKUMPUL, terkumpul);
                            detailIntent.putExtra(EXTRA_URL, path);
                            detailIntent.putExtra(EXTRA_SINOPSIS, sinopsis);
                            detailIntent.putExtra(EXTRA_DETAIL, detail);
                            detailIntent.putExtra(EXTRA_START, start);
                            detailIntent.putExtra(EXTRA_END, end);

                            startActivity(detailIntent);
                        }
                    });
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
        }
    }

    private class SyncId extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;

        //Get Params
        final String googleId = idGoogle.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(MainActivity.this, "BawaBerkah",
                    "Menuju Fitur Galang Dana ...", true);
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
                    String query = "SELECT status FROM `donator` where googleId = '" + googleId + "'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    rs.next();
                    final int id = rs.getInt(1);
                    conn.close();
                    if (id == 1){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Intent detailIntent = new Intent(MainActivity.this, create_campaign.class);

                                detailIntent.putExtra(GOOGLE_ID, idGoogle.getText().toString());
                                detailIntent.putExtra(GOOGLE_DISPLAY, userDisplay.getText().toString());
                                detailIntent.putExtra(GOOGLE_EMAIL, userEmail.getText().toString());

                                startActivity(detailIntent);
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Intent detailIntent = new Intent(MainActivity.this, register_campaigner.class);

                                detailIntent.putExtra(GOOGLE_ID, idGoogle.getText().toString());
                                detailIntent.putExtra(GOOGLE_DISPLAY, userDisplay.getText().toString());
                                detailIntent.putExtra(GOOGLE_EMAIL, userEmail.getText().toString());

                                startActivity(detailIntent);
                            }
                        });
                    }

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
