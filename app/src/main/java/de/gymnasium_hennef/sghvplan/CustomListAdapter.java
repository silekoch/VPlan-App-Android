package de.gymnasium_hennef.sghvplan;

/**
 * Created by Simon on 20.01.2016.
 * Klasse wird genutzt um VertretungsListe zu laden.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import org.json.JSONArray;

public class CustomListAdapter extends BaseAdapter {

    private final List<JSONObject> vertList;
    private final LayoutInflater inflator;
    //private JSONObject jsonFromUrl;



    public CustomListAdapter (Context context, JSONObject jsonFromUrl) {
        JSONObject vert;
        inflator = LayoutInflater.from(context);
        vertList = new ArrayList<>();
        //werte werden erzeugt ab hier-------------------------------
        /*jsonFromUrl = new JSONObject();
        try {
            JSONObject vert1 = new JSONObject();
            JSONObject vert2 = new JSONObject();
            String stufe = "Q1";
            String stunde = "1.Stunde";
            String kurs = "Sowi";
            String useless = "bla";
            vert1.put("stufe", stufe);
            vert1.put("stunde", stunde);
            vert1.put("kurs", kurs);
            vert1.put("useless", useless);
            vert2.put("stufe", stufe);
            vert2.put("stunde", stunde);
            vert2.put("kurs", kurs);
            vert2.put("useless", useless);
            JSONArray verts = new JSONArray();
            verts.put(vert1);
            verts.put(vert2);
            jsonFromUrl.put("lastedit", "20.01.2016");
            jsonFromUrl.put("verts", verts);
        } catch (JSONException e) {
            System.out.println("ERROR");
        }*/
        //Werte sind erzeugt----------------------------------------------
        try {
            int count = (jsonFromUrl.getJSONArray("verts").length()-1);
            for (int i = 0; i <= count; i++) {
                vert = jsonFromUrl.getJSONArray("verts").getJSONObject(i);
                vertList.add(vert);
            }
        } catch (JSONException e) {
            System.out.println("fillListError");
        }
    }

    public int getCount() {
        return vertList.size();
    }

    public Object getItem(int position, String id) {
        try {
            return vertList.get(position).get(id);
        } catch (JSONException e) {
            System.out.println("getItemError");
            return "getItemError";
            //return null;  <-in der endfassung
        }
    }

    public Object getItem(int position) {
        return vertList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.list_row, parent, false);
            holder = new ViewHolder();
            holder.stunde = (TextView) convertView.findViewById(R.id.stunde);
            holder.stufe = (TextView) convertView.findViewById(R.id.stufe);
            holder.kurs = (TextView) convertView.findViewById(R.id.kurs);
            holder.raum = (TextView) convertView.findViewById(R.id.raum);
            holder.neuerRaum = (TextView) convertView.findViewById(R.id.neuerRaum);
            holder.bemerkung = (TextView) convertView.findViewById(R.id.bemerkung);
            holder.lehrerNeu = (TextView) convertView.findViewById(R.id.lehrer_neu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String varStunde = getItem(position, "stunde") + ". Stunde";
        String varStufe = (String) getItem(position, "kurs");
        String varKurs = (String) getItem(position, "fach_alt");
        String varRaum = (String) getItem(position, "raum_alt");
        String varNeuerRaum = (String) getItem(position, "raum_neu");
        String varBemerkung = (String) getItem(position, "bemerkung");
        String varLehrerNeu = (String) getItem(position, "lehrer_neu");
        holder.stunde.setText(varStunde);
        holder.stufe.setText(varStufe);
        holder.kurs.setText(varKurs);
        holder.raum.setText(varRaum);
        holder.neuerRaum.setText(varNeuerRaum);
        holder.bemerkung.setText(varBemerkung);
        holder.lehrerNeu.setText(varLehrerNeu);
        return convertView;
    }

    static class ViewHolder {
        TextView stunde;
        TextView stufe;
        TextView kurs;
        TextView raum;
        TextView neuerRaum;
        TextView bemerkung;
        TextView lehrerNeu;
    }
}
