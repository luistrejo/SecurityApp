package jairfranco.com.tec2.pfran.calendario;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarioFragment extends Fragment {
    TextView tvMyDate;
    CalendarView calendarView;
    Button btnSig;
    String date;
    Intent i;

    public CalendarioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_calendario, container, false);


        tvMyDate = layout.findViewById(R.id.mydate);
        calendarView = layout.findViewById(R.id.calendarView);

        btnSig = layout.findViewById(R.id.btnOkDay);

        i = new Intent(getContext(), CalendarioFragment.class);


        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            date = dayOfMonth + " / " + (month + 1) + " / " + year;
            tvMyDate.setText(date);
        });
        btnSig.setOnClickListener(v -> startActivity(i));

        return layout;
    }

}
