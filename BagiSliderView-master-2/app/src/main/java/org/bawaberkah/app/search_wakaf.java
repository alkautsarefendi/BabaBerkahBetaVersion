package org.bawaberkah.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class search_wakaf extends AppCompatActivity  implements ProductsAdapter.OnItemClickListener {
    private boolean success = false; // boolean

    //Declare Params for MySQL Connection
    private static final String USER = "u276179624_admin";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://194.59.164.7/u276179624_baber";

    private RecyclerView recyclerView;
    private List<Product> productList;
    private Parcelable recyclerViewState;

    private ImageView buttonView;

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_URL = "path";
    public static final String EXTRA_JUDUL = "judul";
    public static final String EXTRA_TARGET = "target";
    public static final String EXTRA_TERKUMPUL = "terkumpul";
    public static final String EXTRA_SINOPSIS = "sinopsis";
    public static final String EXTRA_DETAIL = "detail";
    public static final String EXTRA_START = "start";
    public static final String EXTRA_END = "end";

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_wakaf);

        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        this.productList = new ArrayList();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerTemp);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        loadProducts();
    }

    private void loadProducts() {

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://bawaberkah.online/api/load_wakaf_campaign.php",
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
                                search_wakaf.this.productList.add(_list);

                            }

                            ProductsAdapter adapter = new ProductsAdapter(search_wakaf.this, productList);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListener(search_wakaf.this);


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
        Volley.newRequestQueue(this).add(stringRequest);new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        },3000);
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

    public void goBack(View view) {
        Intent myIntent = new Intent(search_wakaf.this,
                MainActivity.class);
        startActivity(myIntent);
    }
}
