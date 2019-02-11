package de.gymnasium_hennef.sghvplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Simon on 02.02.2016.
 * Klasse wird genutzt um Kursliste mit Löschenbuttons zu laden
 */
public class CustomListAdapterKurslisteBearbeiten extends BaseAdapter {

    private final List<String> kursList;
    private final LayoutInflater inflator;


    public CustomListAdapterKurslisteBearbeiten (Context context, String kursePrefs) {
        inflator = LayoutInflater.from(context);
        kursList = new ArrayList<>();
        //kursePrefs wird in anzeigbare teile zerlegt und in kursList gespeichert
        String[] kurseSplitet = kursePrefs.split("_");
        if (!kurseSplitet[0].equals("")) { //lösung für problem, dass wenn keine kurse gespeichert wurden der erste kurs ein leerzeichen ist
            /* replaced by kurslist.addAll()
            for (int i = 0; i<= kurseSplitet.length-1; i++) {
                kursList.add(kurseSplitet[i]);
            }*/
            kursList.addAll(Arrays.asList(kurseSplitet));
        }
    }

    public int getCount() {
        return kursList.size();
    }

    public Object getItem(int position) {
        return kursList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.list_row_kurse_bearbeiten, parent, false);
            holder = new ViewHolder();
            holder.rowData = kursList.get(position);
            holder.kurs = (TextView) convertView.findViewById(R.id.list_row_kurse_bearbeiten_kurs);
            holder.removeKurs = (ImageButton) convertView.findViewById(R.id.list_row_kurse_löschen_button);
            holder.removeKurs.setTag(holder.rowData);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String varKurs = (String) getItem(position);
        holder.kurs.setText(varKurs);
        return convertView;
    }

    static class ViewHolder {
        String rowData;
        TextView kurs;
        ImageButton removeKurs;
    }
}

