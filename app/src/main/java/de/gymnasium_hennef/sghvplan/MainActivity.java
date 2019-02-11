package de.gymnasium_hennef.sghvplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {
    private JSONObject json;
    private CustomListAdapter adapter;
    private FrameLayout f;
    private LinearLayout b;
    private ArrayList<JSONObject> vertsSortByDate;

    private final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private final LayoutParams paramsButton = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    private TextView stand;

    private AsyncTask<String, Void, JSONObject> requestTask; //var3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_sec);
        f = (FrameLayout) findViewById(R.id.mitte);
        b = (LinearLayout) findViewById(R.id.oben);
        //settingsButton wird gesetzt
        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsbutton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        //refreshButton wird gesetzt
        ImageButton refreshButton = (ImageButton) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNewUrlRequest();

            }
        });
        //hier wird die URL erzeugt und dann an execute gegeben
        /*String url = buildUrl();
        System.out.println(url);*/
        doNewUrlRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doNewUrlRequest();
    }

    public void setJson(JSONObject jsonOb) {
        System.out.println("setJson wurde aufgerufen");
        json = jsonOb;
        vertsSortByDate = splitJsonByDate(jsonOb);
        final int count = vertsSortByDate.size();
        //b wird geleert
        b.removeAllViews();
        //buttons werden erzeugt
        final Button[] buttons = new Button[count];
        for (int i = 0; i<=(count-1); i++) {
            Button datebutton = new Button(this);
            datebutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            if (i == 0 && count != 1) {
                //linker button
                datebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.settings_tab_button_selector_left));
            } else if (i != count-1 && count != 1) {
                //ein button in der mitte
                datebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.settings_tab_button_selector_mid));
            } else if (i == count-1 && count != 1) {
                //rechter button
                datebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.settings_tab_button_selector_right));
            } else {
                //nur ein button
                datebutton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_selector));
            }
            datebutton.setPadding(5,0,5,0);
            try {
                datebutton.setText(formatDate(vertsSortByDate.get(i).getJSONArray("verts").getJSONObject(0).getString("datum")));
            } catch (JSONException e) {
                System.out.println("error at set datumbutton");
            }
            datebutton.setGravity(Gravity.CENTER);
            buttons[i] = datebutton;
            b.addView(datebutton, paramsButton);
        }
        //1. button wird ausgewählt
        buttons[0].setSelected(true);
        buttons[0].setTextColor(ContextCompat.getColor(this, R.color.white));
        //ListView wird erzeugt
        final ListView list = new ListView(this);
        f.removeAllViews();
        f.addView(list, params);
        //ListView wird mit dem Aktuellstem Datum gefüllt
        adapter = new CustomListAdapter(MainActivity.this, vertsSortByDate.get(0));
        list.setAdapter(adapter);
        //OnclickListener werden gesetzt
        for (int i = 0; i<=(count-1); i++) {
            final int x = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int y = 0; y<=(count-1); y++) {
                        if (buttons[y] == buttons[x]) {
                            buttons[y].setSelected(true);
                            buttons[y].setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
                        } else {
                            buttons[y].setSelected(false);
                            buttons[y].setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
                        }
                    }
                    //ListView wird gefüllt, je nachdem welcher button angeklickt wird
                    adapter = new CustomListAdapter(MainActivity.this, vertsSortByDate.get(x)); //Lösung für Problem mit kontext zwischen inner und outer class -MainActivity.this
                    list.setAdapter(adapter);
                }
            });
        }
        //stand wird angezeigt
        stand = (TextView) findViewById(R.id.stand);
        try {
            String standText = "Stand:  " + formatDateWithTime(json.getString("lastedit"));
            stand.setText(standText);
        } catch (JSONException e) {
            System.out.println("error at set lastedit");
        }
    }

    public ArrayList<JSONObject> splitJsonByDate(JSONObject jsonOrg) {
        ArrayList<JSONObject> listen = new ArrayList<>();
        Queue<JSONObject> vertListOrg = new Queue<>();
            //abfrage ob überhaupt etwas ankommt muss hier noch hin

        try{
            int count = (jsonOrg.getJSONArray("verts").length()-1);
            for (int i = 0; i <= count; i++) {
                JSONObject vert = jsonOrg.getJSONArray("verts").getJSONObject(i);
                if (vertListOrg.isEmpty() || vertListOrg.front().getString("datum").equals(vert.getString("datum"))) {
                    vertListOrg.enqueue(vert);
                } else {
                    JSONObject jsonSor = new JSONObject();
                    JSONArray verts = new JSONArray();
                    while (!vertListOrg.isEmpty()) {
                        verts.put(vertListOrg.front());
                        vertListOrg.dequeue();
                    }
                    jsonSor.put("lastedit", jsonOrg.get("lastedit"));
                    jsonSor.put("verts", verts);
                    listen.add(jsonSor);
                    vertListOrg.enqueue(vert); //>lösung für problem dass der aktuelle index nicht in queue und liste übernommen wird<
                }

            }
            JSONObject jsonSor = new JSONObject();//>lösung für problem das die letzte erstellte queue nicht übernommen wird
            JSONArray verts = new JSONArray();
            while (!vertListOrg.isEmpty()) {
                verts.put(vertListOrg.front());
                vertListOrg.dequeue();
            }
            jsonSor.put("lastedit", jsonOrg.get("lastedit"));
            jsonSor.put("verts", verts);
            listen.add(jsonSor);                    //<
        }catch (JSONException e) {
            System.out.println("errorSorInList");
        }
        return listen; //möglichkeit : den jsonobjekten für die einzelnen daten das datum noch mal hinzufügen
    }

    //datum wird formatiert für buttons
    public String formatDate(String dateOrg) {
        String formattedDate;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateOrg);
            //formattedDate = new SimpleDateFormat("dd.MM.yyyy").format(date); // angepasst zu nächste zeile
            formattedDate = new SimpleDateFormat("dd.MM.").format(date);
        } catch (ParseException e) {
            formattedDate = dateOrg;
        }
        return formattedDate;

    }
    //datum wird formatiert für stand
    public String formatDateWithTime(String dateOrg) {
        String formattedDate;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateOrg);
            //formattedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(date); // angepasst zu nächste zeile
            formattedDate = new SimpleDateFormat("dd.MM. HH:mm").format(date);
        } catch (ParseException e) {
            formattedDate = dateOrg;
        }
        return formattedDate;
    }

    public String buildUrl () {
        String main  = "https://ws.vplan.sgh.mobi/1/vertrequest?appbuildno=1&appos=and&mode=";
        String klasse;
        String lehrerKuerzel;
        String kurse;
        //mode wird abgerufen aus sharedPreferences
        SharedPreferences sPrefs = getSharedPreferences("preferences.xml", MODE_PRIVATE);
        String mode = sPrefs.getString("mode", "sek1");
        //password wird abgerufen aus sharedPreferences
        String password = sPrefs.getString("password", "");
        String variable = "";
        String selection = "&selection=";
        /* //replace by switch
        if (mode.equals("sek1")) {
            klasse = sPrefs.getString("klasse", "");
            variable = selection + klasse;
        } else if (mode.equals("sek2")) {
            kurse = sPrefs.getString("kurse", "");
            variable = selection + kurse;
        } else if (mode.equals("teacher")) {
            lehrerKuerzel = sPrefs.getString("lehrer_kuerzel", "");
            variable = selection + lehrerKuerzel;
        }*/
        switch (mode) {
            case "sek1":
                klasse = sPrefs.getString("klasse", "");
                variable = selection + klasse;
                break;
            case "sek2":
                kurse = sPrefs.getString("kurse", "");
                variable = selection + kurse;
                break;
            case "teacher":
                lehrerKuerzel = sPrefs.getString("lehrer_kuerzel", "");
                variable = selection + lehrerKuerzel;
        }
        return main + mode + "&password=" + password + variable;
    }

    public void setVertsEmpty (JSONObject jsonOb) {
        json = jsonOb;
        f.removeAllViews();
        b.removeAllViews();
        //textView -keine vert
        TextView textView = new TextView(this);
        textView.setText(R.string.keine_vertretungen);
        textView.setGravity(Gravity.CENTER);
        f.addView(textView, params);
        //stand wird angezeigt
        stand = (TextView) findViewById(R.id.stand);
        try {
            String standText = "Stand:  " + formatDateWithTime(json.getString("lastedit"));
            stand.setText(standText);
        } catch (JSONException e) {
            System.out.println("error at set lastedit");
        }
    }

    //methode aus buch übernommen (seite 280) Titel: Android 5 - Apps entwickeln mit Android Studio | Autor: Thomas Künneth| Verlag: Rheinwerk Computing
    public boolean isConnected() {
        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    public void dialogIsNotConnected() {

        if (json == null) {
            f.removeAllViews();
            b.removeAllViews();
            //textView -keine vert
            TextView textView = new TextView(this);
            textView.setText(R.string.aktualiesieren_vertretungen);
            textView.setGravity(Gravity.CENTER);
            f.addView(textView, params);
        }

        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);

        dlgAlert.setMessage("Keine Internetverbindung");
        dlgAlert.setTitle("Fehler");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.show();

    }

    public void doNewUrlRequest () {
        if (isConnected()) {
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); //var2
            requestTask = new UrlRequest().execute(buildUrl()); //var3
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //var2
        } else {
            dialogIsNotConnected();
        }
    }

    public static String reasonQuery (String reason) {
        //hier muss der entsprechende reason interpretiert und zurückgegeben werden
        /* //ersetzt durch switch
        if (reason.equals("Wrong password.")) {
            return "Das Passwort ist falsch";
        } else if (reason.equals("possibleConnectionError")) {
            return "Verbindungsfehler, prüfen sie die Internetverbindung und versuchen sie es erneut.";
        } else {
            return reason;
        }*/
        switch (reason) {
            case "Wrong password.":
                return "Das Passwort ist falsch";
            case "possibleConnectionError":
                return "Verbindungsfehler, prüfen sie die Internetverbindung und versuchen sie es erneut.";
            default:
                return reason;
        }
    }




    private class UrlRequest extends AsyncTask<String, Void, JSONObject> { //in android müssen alle langwierigen aufgaben ausgelagert werden zb in asynctask
        ProgressDialog mDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); //var1

            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Bitte warten...");
            mDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;
            try {
                InputStream is = new URL(params[0]).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                String jsonText = sb.toString();
                json = new JSONObject(jsonText);
                is.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            if (json == null) {
                try {
                    json = new JSONObject();
                    json.put("success", "false");
                    json.put("reason", "possibleConnectionError");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            //super.onPostExecute(result);
            // Do things like hide the progress bar or change a TextView
            //es wird geprüft ob die erwarteten daten da sind , wenn ja wird setJson() aufgerufen und dadurch die Vertretungen bearbeitet und angezeigt
            boolean urlRequestPositive;
            boolean vertsFilled;
            try {
                result.getString("lastedit");
                urlRequestPositive = true;
            } catch (JSONException e) {
                System.out.println("lastedit nicht gefunden");
                urlRequestPositive = false;
            }
            try {
                result.getJSONArray("verts").getJSONObject(0);
                vertsFilled = true;
            } catch (JSONException e) {
                System.out.println("verts ist leer");
                vertsFilled = false;
            }
            if (urlRequestPositive && vertsFilled) {
                MainActivity.this.setJson(result);
            } else if (urlRequestPositive) {
                MainActivity.this.setVertsEmpty(result);
            }
            mDialog.dismiss();

            try {
                if (result.getString("success").equals("false")) {
                    String reason = reasonQuery(result.getString("reason"));
                    final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MainActivity.this);

                    dlgAlert.setMessage(reason);
                    dlgAlert.setTitle("Fehler");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.show();
                }
            } catch (JSONException e) {
                System.out.println("success nicht gefunden");
            }
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //var1
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        requestTask.cancel(true); //var3
    }
}
