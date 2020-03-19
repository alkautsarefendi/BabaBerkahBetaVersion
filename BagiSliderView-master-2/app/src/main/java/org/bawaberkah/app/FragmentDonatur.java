package org.bawaberkah.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FragmentDonatur extends Fragment{
    private final String URL_DONATUR = "http://bawaberkah.online/api/donatur_campaign.php";
    private ArrayList<Donatur> itemArrayList;
    private ListView lvMsg; // ListView
    private boolean success = false; // boolean
    private MyAppAdapter myAppAdapter;
    private Bundle args;
    private String idCampaign;
    private TextView lblidCampaign;

    //Declare Params for MySQL Connection
    private static final String USER = "u276179624_admin";
    private static final String PASS = "Iloveyou101";
    private static final String DB_URL = "jdbc:mysql://194.59.164.7/u276179624_baber";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_donatur, viewGroup, false);
        args = getArguments();
        idCampaign = args.getString("idCampaign");
        lblidCampaign = (TextView)view.findViewById(R.id.lblIdCampaign);
        lblidCampaign.setText(idCampaign);
        lvMsg = (ListView) view.findViewById(R.id.lvlDonatur); //ListView Declaration
        itemArrayList = new ArrayList<Donatur>(); // Arraylist Initialization
        SyncData orderData = new SyncData();
        orderData.execute("");
        return view;
    }

    public class MyAppAdapter extends BaseAdapter
    {
        public class ViewHolder {
            TextView id;
            TextView nama;
            TextView nominal;
            TextView catatan;
        }

        public List<Donatur> donaturList;

        public Context context;
        ArrayList<Donatur> arraylist;

        private MyAppAdapter(ArrayList<Donatur> apps, Context context) {
            this.donaturList = apps;
            this.context = context;
            arraylist = new ArrayList<Donatur>();
            arraylist.addAll(donaturList);
        }

        @Override
        public int getCount() {
            return donaturList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // inflating the layout and initializing widgets
        {

            View rowView = convertView;
            ViewHolder viewHolder = null;
            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.row_donatur, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.id = (TextView) rowView.findViewById(R.id.lblId);
                viewHolder.nama = (TextView) rowView.findViewById(R.id.lblDonatur);
                viewHolder.nominal = (TextView) rowView.findViewById(R.id.lblNominal);
                viewHolder.catatan = (TextView) rowView.findViewById(R.id.lblCatatan);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // here setting up names and images
            viewHolder.id.setText(donaturList.get(position).getIdDonatur() + "");
            viewHolder.nama.setText(donaturList.get(position).getNama() + "");
            viewHolder.nominal.setText(donaturList.get(position).getNominal() + "");
            viewHolder.catatan.setText(donaturList.get(position).getCatatan() + "");

            return rowView;
        }
    }

    private class SyncData extends AsyncTask<String, String, String> {
        String msg ;
        ProgressDialog progress;
        String _idCampaign = lblidCampaign.getText().toString();

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show( getContext(), null, null, false, true );
            progress.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            progress.setContentView( R.layout.progressdialog );
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
                    String query = "SELECT idDonatur, nama, catatan, Format(nominal, '##,##0') as donasi FROM donaturCampaign " +
                            "WHERE idCampaign = '"+ _idCampaign + "' AND status = '1'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs != null) // if resultset not null, I add items to itemArraylist using class created
                    {
                        while (rs.next()) {
                            try {
                                itemArrayList.add(new Donatur(rs.getString("idDonatur"), rs.getString("nama"),
                                        rs.getString("donasi"), rs.getString("catatan")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        rs.close();
                        conn.close();
                        msg = "Sinkronisasi Selesai";
                        success = true;
                    } else {
                        msg = "Data tidak ditemukan!";
                        success = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Writer writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                msg = "Koneksi terputus dengan server";
                success = false;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) // disimissing progress dialoge, showing error and setting up my ListView
        {
            progress.dismiss();
            Toast.makeText(getActivity(), msg + "", Toast.LENGTH_LONG).show();
            if (success == false) {
            } else {
                try {
                    myAppAdapter = new MyAppAdapter(itemArrayList, getActivity());
                    lvMsg.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    lvMsg.setAdapter(myAppAdapter);
                } catch (Exception ex) {

                }
            }
        }
    }
}
