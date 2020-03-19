package org.bawaberkah.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentSinopsis extends Fragment {

    TextView sinopsis, detail;
    Bundle bundle;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_sinopsis, viewGroup, false);
        final Bundle args = getArguments();
        final String side = args.getString("sinopsis");
        final String end = args.getString("detail");
        sinopsis = (TextView) view.findViewById(R.id.lblsinopsis);
        detail = (TextView) view.findViewById(R.id.lblDetail);
        sinopsis.setText(side);
        detail.setText(end);
        return view;
    }
}