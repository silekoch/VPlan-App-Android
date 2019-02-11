package de.gymnasium_hennef.sghvplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Simon on 01.02.2016.
 * Klasse wird genutzt um Benutzer Einstellungen anpassen zu lassen.
 */
public class SettingsActivity  extends Activity {

    private FrameLayout f;
    //private final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private Button buttonSek1;
    private Button buttonSek2;
    private Button buttonLehrer;
    private Button kursHinzufügen;
    private Button kursLoeschen;
    private Button klasseWaehlen;
    private TextView klasseAusgabe;
    private String mode;
    private String passwordString;
    private EditText password;
    private EditText lehrerKuerzel;
    private String einstellungKlasse;
    private String lehrerKuerzelString;
    private String einstellungKurse;
    private ListView list;
    private boolean activ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        f = (FrameLayout) findViewById(R.id.settings_mitte);
        SharedPreferences sPrefs = getSharedPreferences("preferences.xml", MODE_PRIVATE);
        einstellungKlasse = sPrefs.getString("klasse", "Keine Klasse gewählt");
        einstellungKurse = sPrefs.getString("kurse", "");


        Button buttonAbbrechen;
        buttonAbbrechen = (Button) findViewById(R.id.settings_abbrechen);
        buttonAbbrechen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MainActivity wird wieder gestartet
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });


        Button buttonSichern;
        buttonSichern = (Button) findViewById(R.id.settings_sichern);
        buttonSichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inhalt von password wird in passwordString geschrieben
                passwordString = password.getText().toString();
                //mode wird in preferences gesetzt
                /*SharedPreferences settings = getSharedPreferences("mode", MODE_PRIVATE); //>problem mode statt preferences.xml wird aufgerufen
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("mode", mode);*/
                SharedPreferences settings = getSharedPreferences("preferences.xml", MODE_PRIVATE); //>lösung für problem preferences aus anderen klasse zu setzen
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("mode", mode);
                editor.putString("password", passwordString);
                editor.apply();                                                        //<

                //dem mode entsprechend wird die eingabe abgespeichert
                SharedPreferences sPrefs = getSharedPreferences("preferences.xml", MODE_PRIVATE);
                String prefMode = sPrefs.getString("mode", "sek1");
                /* //replaced by switch
                if (prefMode.equals("sek1")) {
                    editor.putString("klasse", einstellungKlasse);
                    editor.apply();
                } else if (prefMode.equals("sek2")) {
                    editor.putString("kurse", einstellungKurse);
                    editor.apply();
                } else if (prefMode.equals("teacher")) {
                    lehrerKuerzelString = lehrerKuerzel.getText().toString();
                    editor.putString("lehrer_kuerzel", lehrerKuerzelString);
                    editor.apply();
                }*/
                switch (prefMode) {
                    case "sek1":
                        editor.putString("klasse", einstellungKlasse);
                        break;
                    case "sek2":
                        editor.putString("kurse", einstellungKurse);
                        break;
                    case "teacher":
                        lehrerKuerzelString = lehrerKuerzel.getText().toString();
                        editor.putString("lehrer_kuerzel", lehrerKuerzelString);
                        break;
                    default:
                        //passiert nicht
                }
                editor.apply();
                //MainActivity wird wieder gestartet
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });

        buttonSek1 = (Button) findViewById(R.id.settings_sek1);
        buttonSek1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this, v);
                buttonSelector("buttonSek1");
                //alle vorherigen views werden entfernt
                f.removeAllViews();
                //Einstellungen werden entsprechend dem button gesetzt
                mode = "sek1";
                //frame_settings_sek1 wird geladen
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout frameSek1 = (LinearLayout) inflater.inflate(R.layout.frame_settings_sek1, f, false);
                f.addView(frameSek1);
                //button klasseWaehlen wird eingebunden
                klasseWaehlen = (Button) findViewById(R.id.frame_settings_sek1_klasse_wählen_button);
                klasseWaehlen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickerSek1();
                    }
                });
                //klassenAusgabe wird entsprechend den einstellungen angezeigt
                klasseAusgabe = (TextView) findViewById(R.id.frame_settings_sek1_klasse);
                final SharedPreferences sPrefs = getSharedPreferences("preferences.xml", MODE_PRIVATE); //>lösung wichtig:so ist richtig im gegensatz zu dadrunter, da unten die nur defaultwerte geladen wurden
                //SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                String prefKlasse = sPrefs.getString("klasse", "Keine Klasse gewählt");
                klasseAusgabe.setText(prefKlasse);
                //passwort textfeld wird eingebunden
                password = (EditText) findViewById(R.id.frame_settings_sek1_pw);
                //password wird mit preferences gefüllt
                String prefPasswort = sPrefs.getString("password", "");
                if (!prefPasswort.equals("")) {
                    password.setText(prefPasswort);
                }
                //focus wird auf focusCatcher gesetzt
                password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            hideSoftKeyboard(SettingsActivity.this, v);
                            LinearLayout focusCatcher = (LinearLayout) findViewById(R.id.focus_catcher_sek1);
                            focusCatcher.requestFocus();
                            System.out.println("funktioniert");
                            handled = true;
                        }
                        return handled;
                    }
                });
            }
        });

        buttonSek2 = (Button) findViewById(R.id.settings_sek2);
        buttonSek2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this, v);
                buttonSelector("buttonSek2");
                //alle vorherigen views werden entfernt
                f.removeAllViews();
                //Einstellungen werden entsprechend dem button gesetzt
                mode = "sek2";
                //frame_settings_sek2 wird geladen
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout frameSek2 = (LinearLayout) inflater.inflate(R.layout.frame_settings_sek2, f, false);
                f.addView(frameSek2);
                //preferences werden geladen
                final SharedPreferences sPrefs = getSharedPreferences("preferences.xml", MODE_PRIVATE);
                //kursliste wird geladen
                list = (ListView) findViewById(R.id.frame_settings_sek2_kurse);
                reloadKursList(einstellungKurse);
                //hinzufügen button wird eingebunden
                kursHinzufügen = (Button) findViewById(R.id.frame_settings_sek2_kurs_hinzufügen_button);
                kursHinzufügen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickerSek2();
                        //hier muss die kursliste neu geladen werden
                        reloadKursList(einstellungKurse);
                        if (activ) {
                            changeKurslisteBearbeiten();
                        }
                    }
                });
                //löschen button wird eingebunden
                activ = false;
                kursLoeschen = (Button) findViewById(R.id.frame_settings_sek2_kurs_loeschen_button);
                kursLoeschen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        //hier muss die kursliste bearbeiten geladen werden
                        reloadKursListBearbeiten(einstellungKurse);
                        //im onclick listener vom kursLöschenButton muss die kursliste bearbeiten neu geladen werden und der kurs aus preferences gelöscht werden
                        */
                        changeKurslisteBearbeiten();
                    }
                });
                //password wird eingebunden
                password = (EditText) findViewById(R.id.frame_settings_sek2_pw);
                //password wird mit preferences gefüllt
                String prefPasswort = sPrefs.getString("password", "");
                if (!prefPasswort.equals("")) {
                password.setText(prefPasswort);
                }
                //focus wird auf focusCatcher gesetzt
                password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            hideSoftKeyboard(SettingsActivity.this, v);
                            LinearLayout focusCatcher = (LinearLayout) findViewById(R.id.focus_catcher_sek2);
                            focusCatcher.requestFocus();
                            System.out.println("funktioniert");
                            handled = true;
                        }
                        return handled;
                    }
                });
            }
        });

        buttonLehrer = (Button) findViewById(R.id.settings_lehrer);
        buttonLehrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this, v);
                buttonSelector("buttonLehrer");
                //alle vorherigen views werden entfernt
                f.removeAllViews();
                //Einstellungen werden entsprechend dem button gesetzt
                mode = "teacher";
                //frame_settings_lehrer wird geladen
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout frameLehrer = (LinearLayout) inflater.inflate(R.layout.frame_settings_lehrer, f, false);
                f.addView(frameLehrer);
                final SharedPreferences sPrefs = getSharedPreferences("preferences.xml", MODE_PRIVATE);
                //passwort textfeld wird eingebunden
                password = (EditText) findViewById(R.id.frame_settings_lehrer_pw);
                //password wird mit preferences gefüllt
                String prefPasswort = sPrefs.getString("password", "");
                if (!prefPasswort.equals("")) {
                    password.setText(prefPasswort);
                }
                //lehrerkurzel textfeld wird eingebunden
                lehrerKuerzel = (EditText) findViewById(R.id.frame_settings_lehrer_kuerzel);
                //lehrerkurzel wird mit preferences gefüllt
                String prefLehrerKuerzel = sPrefs.getString("lehrer_kuerzel", "");
                if (!prefLehrerKuerzel.equals("")) {
                    lehrerKuerzel.setText(prefLehrerKuerzel);
                }
                //focus wird auf focusCatcher gesetzt
                password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            hideSoftKeyboard(SettingsActivity.this, v); //wird vielleicht von focusCatcher ersetzt; focuscatcher vielleicht auch woanders einsetzbar
                            LinearLayout focusCatcher = (LinearLayout) findViewById(R.id.focus_catcher_lehrer);
                            focusCatcher.requestFocus();
                            System.out.println("funktioniert");
                            handled = true;
                        }
                        return handled;
                    }
                });
                lehrerKuerzel.setOnEditorActionListener(new TextView.OnEditorActionListener() { // focuscatcher funktioniert bei lehrerkuerzel aus irgendeinem grund noch nicht
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            hideSoftKeyboard(SettingsActivity.this, v); //wird vielleicht von focusCatcher ersetzt; focuscatcher vielleicht auch woanders einsetzbar
                            LinearLayout focusCatcher = (LinearLayout) findViewById(R.id.focus_catcher_lehrer);
                            focusCatcher.requestFocus();
                            System.out.println("funktioniert");
                            handled = true;
                        }
                        return handled;
                    }
                });
            }
        });

        //der button zum aktuell ausgewählten mode wird geklickt
        //SharedPreferences sPrefs = getSharedPreferences("preferences.xml", MODE_PRIVATE);     //wird schon am anfang der methode gemacht
        String sMode = sPrefs.getString("mode", "sek1");
        /* //replaced by switch
        if (sMode.equals("sek1")) {
            buttonSek1.performClick();
        } else if (sMode.equals("sek2")) {
            buttonSek2.performClick();
        } else if (sMode.equals("teacher")) {
            buttonLehrer.performClick();
        }*/
        switch (sMode) {
            case "sek1":
                buttonSek1.performClick();
                break;
            case "sek2":
                buttonSek2.performClick();
                break;
            case "teacher":
                buttonLehrer.performClick();
                break;
            default:
                //passiert nicht
        }
    }

    public void reloadKursList (String sPrefsKurse) {
        if (sPrefsKurse == null) {
            sPrefsKurse = "";
        }
        //Kurslisten adapter wird aufgerufen
        CustomListAdapterKursliste adapterKursliste;
        adapterKursliste = new CustomListAdapterKursliste(SettingsActivity.this, sPrefsKurse);
        list.setAdapter(adapterKursliste);
    }

    public void reloadKursListBearbeiten (String sPrefsKurse) {
        //kursList adapter bearbeiten wird aufgerufen
        CustomListAdapterKurslisteBearbeiten adapterKurslisteBearbeiten;
        if (sPrefsKurse != null) {
            adapterKurslisteBearbeiten = new CustomListAdapterKurslisteBearbeiten(SettingsActivity.this, sPrefsKurse);
            list.setAdapter(adapterKurslisteBearbeiten);
        } else {
            reloadKursList(einstellungKurse);
            //hier muss noch ein dialog der über nichtvorhanden sein von kursen informiert hin
        }

    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public void removeKursHandler(View v) {
        /*final View vFinal = v;
        //abfrage ob man das wirklich will
        DialogInterface.OnClickListener dialogButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE: {
                        //bei klick auf abbrechen
                        dialog.dismiss();
                        break;
                    }
                    case DialogInterface.BUTTON_POSITIVE: {
                        //bei klick auf ok
                        //kurs wird gelöscht
                        String itemToRemove = (String)vFinal.getTag();
                        SharedPreferences sPrefsRemoveKursHandler = getSharedPreferences("preferences.xml", MODE_PRIVATE);
                        String prefKurse = sPrefsRemoveKursHandler.getString("kurse", "");
                        ArrayList<String> kursList = new ArrayList<String>();
                        //kursePrefs wird in einzelne kurse zerlegt
                        String[] kurseSplitet = prefKurse.split("_");
                        for (int i = 0; i<= kurseSplitet.length-1; i++) {
                            kursList.add(kurseSplitet[i]);
                        }
                        for (int i = 0; i<= kursList.size()-1; i++) {
                            if (kursList.get(i).equals(itemToRemove)) {
                                kursList.remove(i);
                            }
                        }
                        String neueKurse = null;
                        String save = "";
                        for (int i = 0; i<= kursList.size()-1; i++) {
                            if (neueKurse == null && kursList.size() >= 1) {
                                neueKurse = kursList.get(i);
                            } else if (kursList.size() <= 0) {
                                neueKurse = "";
                            } else {
                                neueKurse = save + "_" +kursList.get(i);
                            }
                            save = neueKurse;
                        }
                        einstellungKurse = neueKurse;
                        SharedPreferences.Editor editor = sPrefsRemoveKursHandler.edit();
                        editor.putString("kurse", einstellungKurse);
                        editor.commit();
                        reloadKursListBearbeiten(einstellungKurse);
                    }
                    default: {
                        dialog.dismiss();
                        break;
                    }
                }
            }
        };
        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity.this);

        dlgAlert.setMessage("Wollen sie diesen Kurs wirklich löschen");
        dlgAlert.setTitle("Sind sie sicher");
        dlgAlert.setPositiveButton("Ok", null);
        dlgAlert.setNegativeButton("Abbrechen", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();*/
        //kurs wird gelöscht
        String itemToRemove = (String)v.getTag();
        SharedPreferences sPrefsRemoveKursHandler = getSharedPreferences("preferences.xml", MODE_PRIVATE);
        String prefKurse = sPrefsRemoveKursHandler.getString("kurse", "");
        ArrayList<String> kursList = new ArrayList<>();
        //kursePrefs wird in einzelne kurse zerlegt
        String[] kurseSplitet = prefKurse.split("_");
        /* //replaced by addAll()
        for (int i = 0; i<= kurseSplitet.length-1; i++) {
            kursList.add(kurseSplitet[i]);
        }*/
        kursList.addAll(Arrays.asList(kurseSplitet));
        for (int i = 0; i<= kursList.size()-1; i++) {
            if (kursList.get(i).equals(itemToRemove)) {
                kursList.remove(i);
            }
        }
        String neueKurse = null;
        String save = "";
        for (int i = 0; i <= kursList.size() - 1; i++) {
            if (neueKurse == null && kursList.size() >= 1) {
                neueKurse = kursList.get(i);
            } else if (kursList.size() <= 0) {
                neueKurse = "";
            } else {
                neueKurse = save + "_" +kursList.get(i);
            }
            save = neueKurse;
        }
        einstellungKurse = neueKurse;
        SharedPreferences.Editor editor = sPrefsRemoveKursHandler.edit();
        editor.putString("kurse", einstellungKurse);
        editor.apply();
        reloadKursListBearbeiten(einstellungKurse);
    }

    public void buttonSelector(String buttonName) {
        //der button der als parameter angegeben wird wird selected - kann noch optimiert werden
        if (buttonName.equals("buttonSek1")) {
            buttonSek1.setSelected(true);
            buttonSek1.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            buttonSek1.setSelected(false);
            buttonSek1.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        if (buttonName.equals("buttonSek2")) {
            buttonSek2.setSelected(true);
            buttonSek2.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            buttonSek2.setSelected(false);
            buttonSek2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        if (buttonName.equals("buttonLehrer")) {
            buttonLehrer.setSelected(true);
            buttonLehrer.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            buttonLehrer.setSelected(false);
            buttonLehrer.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    public void changeKurslisteBearbeiten () {
        /*
        if (activ) {
            //löschen button wird eingebunden
            kursLoeschen = (Button) findViewById(R.id.frame_settings_sek2_kurs_loeschen_button);
            kursLoeschen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //hier muss die kursliste geladen werden
                    reloadKursList(einstellungKurse);
                    //cangeKurslisteBearbeiten wird aufgerufen
                    changeKurslisteBearbeiten(false);
                    kursLoeschen.setText("Bearbeitung abbrechen");
                }
            });
        } else {
            //löschen button wird eingebunden
            kursLoeschen = (Button) findViewById(R.id.frame_settings_sek2_kurs_loeschen_button);
            kursLoeschen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //hier muss die kursliste geladen werden
                    reloadKursListBearbeiten(einstellungKurse);
                    //cangeKurslisteBearbeiten wird aufgerufen
                    changeKurslisteBearbeiten(true);
                    kursLoeschen.setText("Kurse bearbeiten");
                }
            });
        }*/
        if (!activ) {
            //hier muss die kursliste geladen werden
            reloadKursListBearbeiten(einstellungKurse);
            //activ wird geändert
            activ = true;
            kursLoeschen.setText(R.string.bearbeitung_abbrechen);
        } else {
            //hier muss die kursliste geladen werden
            reloadKursList(einstellungKurse);
            //activ wird geändert
            activ = false;
            kursLoeschen.setText(R.string.kurse_bearbeiten);
        }
    }

    /* // muss noch implementiert werden
    public boolean passwordTest() {
        boolean result = false;
        //url muss mit password abgefragt werden und damit getestet ob das Passwort richtig ist
        return result;
    }*/

    public void pickerSek1 () {
        //ab hier Picker für sek 1
        final String klasse[] = {"a", "b", "c", "d", "e", "f"};
        View multiPickerLayout = LayoutInflater.from(this).inflate(R.layout.picker_sek1, f, false);
        final NumberPicker myNumberPicker = (NumberPicker) multiPickerLayout.findViewById(R.id.picker_sek1_stufe);
        myNumberPicker.setMaxValue(9);
        myNumberPicker.setMinValue(5);
        myNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);                     // verhindert dass man die auswahlmöglichkeiten als benutzer ändern kann
        final NumberPicker myTextPicker = (NumberPicker) multiPickerLayout.findViewById(R.id.picker_sek1_klasse);
        myTextPicker.setMinValue(0);
        myTextPicker.setMaxValue(klasse.length - 1);
        myTextPicker.setDisplayedValues(klasse);
        myTextPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        DialogInterface.OnClickListener dialogButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE: {
                        // user tapped "cancel"
                        dialog.dismiss();
                        break;
                    }
                    case DialogInterface.BUTTON_POSITIVE: {
                        // user tapped "set"
                        // here, use the "multiPickerDate" and "multiPickerTime" objects to retreive the date/time the user selected
                        //eingabe wird ausgegeben
                        String klasseString = klasse[myTextPicker.getValue()];
                        String stufeString = Integer.toString(myNumberPicker.getValue());
                        TextView klasseAusgabe = (TextView) findViewById(R.id.frame_settings_sek1_klasse);
                        String klasseAusgabeString = stufeString + klasseString;
                        klasseAusgabe.setText(klasseAusgabeString);
                        //eingabe wird in preferences übertragen
                        einstellungKlasse = stufeString + klasseString;
                        SharedPreferences settings = getSharedPreferences("preferences.xml", MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("klasse", einstellungKlasse);
                        editor.apply();                                                              //wird von sichern übernommen
                        break;
                    }
                    default: {
                        dialog.dismiss();
                        break;
                    }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setView(multiPickerLayout);
        builder.setPositiveButton("Sichern", dialogButtonListener);
        builder.setNegativeButton("Abbrechen", dialogButtonListener);
        builder.show();
        //picker für sek 1 fertig
    }

    public void pickerSek2 () {
        final String stufe[] = {"EF", "Q1", "Q2"};
        final String gkLkSwitch[] = {"GK", "LK", "VK", "ZK"};
        final String fach[] = {"Bi", "Ch", "D", "E", "Ek", "Er", "F", "G", "Ge", "If", "Kr", "Ku", "L", "M", "Mu", "Pa", "Ph", "Pl", "S", "Sp", "Sw"};
        View multiPickerLayout = LayoutInflater.from(this).inflate(R.layout.picker_sek2, f, false);

        final NumberPicker pickerStufe = (NumberPicker) multiPickerLayout.findViewById(R.id.picker_sek2_stufe);
        pickerStufe.setMinValue(0);
        pickerStufe.setMaxValue(stufe.length - 1);
        pickerStufe.setDisplayedValues(stufe);
        pickerStufe.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final NumberPicker pickerFach = (NumberPicker) multiPickerLayout.findViewById(R.id.picker_sek2_fach);
        pickerFach.setMinValue(0);
        pickerFach.setMaxValue(fach.length - 1);
        pickerFach.setDisplayedValues(fach);
        pickerFach.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final NumberPicker pickerGkLkSwitch = (NumberPicker) multiPickerLayout.findViewById(R.id.picker_sek2_gk_lk_switch);
        pickerGkLkSwitch.setMinValue(0);
        pickerGkLkSwitch.setMaxValue(gkLkSwitch.length - 1);
        pickerGkLkSwitch.setDisplayedValues(gkLkSwitch);
        pickerGkLkSwitch.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        final NumberPicker pickerKursNr = (NumberPicker) multiPickerLayout.findViewById(R.id.picker_sek2_kursNr);
        pickerKursNr.setMaxValue(9);
        pickerKursNr.setMinValue(1);
        pickerKursNr.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        DialogInterface.OnClickListener dialogButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_NEGATIVE: {
                        // user tapped "cancel"
                        dialog.dismiss();
                        break;
                    }
                    case DialogInterface.BUTTON_POSITIVE: {
                        // user tapped "set"
                        // here, use the "multiPickerDate" and "multiPickerTime" objects to retreive the date/time the user selected
                        //eingegebene daten werden in strings übertragen
                        String stufeString = stufe[pickerStufe.getValue()];
                        String fachString = fach[pickerFach.getValue()];
                        String gkLkSwitchString = gkLkSwitch[pickerGkLkSwitch.getValue()];
                        String kursNrString = Integer.toString(pickerKursNr.getValue());
                        //preferences werden geladen
                        SharedPreferences settings = getSharedPreferences("preferences.xml", MODE_PRIVATE);
                        //String mit neuem kurs wird erstellt
                        String neuerKurs;
                        if (settings.getString("kurse", "").equals("")) {
                            neuerKurs =stufeString + "-" + fachString + "-" + gkLkSwitchString + "-" + kursNrString;
                        } else {
                            neuerKurs = "_" + stufeString + "-" + fachString + "-" + gkLkSwitchString + "-" + kursNrString;
                        }
                        // es wird geprüft ob der kurs schon hinzugefügt wurde
                        String prefKurse = settings.getString("kurse", "");
                        Boolean kursDopplung = false;
                        String[] kurseSplitet = prefKurse.split("_");
                        for (int i = 0; i<= kurseSplitet.length-1; i++) {
                            if (("_" + kurseSplitet[i]).equals(neuerKurs)) { // problem war, dass bei jedem hinzugefuegten kurs der möglicherweise doppelt ist noch ein unterstrich davor steht und bei den gesplitteten kursen nicht
                                kursDopplung = true;
                            }
                        }
                        if (kursDopplung) {
                            final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity.this); //funktioniert noch nicht
                            dlgAlert.setMessage("Sie können keine Kurse doppelt speichern");
                            dlgAlert.setTitle("Fehler");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            break;
                        } else {
                            //String wird aus alten kursen + neue kurse erstellt und gespeichert
                            einstellungKurse =settings.getString("kurse", "") + neuerKurs;
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("kurse", einstellungKurse);
                            editor.apply();
                            reloadKursList(einstellungKurse);
                            break;
                        }

                    }
                    default: {
                        dialog.dismiss();
                        break;
                    }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setView(multiPickerLayout);
        builder.setPositiveButton("Sichern", dialogButtonListener);
        builder.setNegativeButton("Abbrechen", dialogButtonListener);
        builder.show();
    }
}

