package org.bawaberkah.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentUpdate extends Fragment {

    TextView update;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_update, viewGroup, false);
        final Bundle args = getArguments();
        final String side = args.getString("update");
        update = (TextView) view.findViewById(R.id.lblUpdate);
        update.setText(side);
        return view;
    }
}
