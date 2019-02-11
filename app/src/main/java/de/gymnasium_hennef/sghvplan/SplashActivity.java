package de.gymnasium_hennef.sghvplan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Simon Koch on 20.02.2016.
 * Klasse wird genutzt um MainActivity zu laden und einen Hinweis zu zeigen.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen_layout);
        /*
        ImageView img = new ImageView(SplashActivity.this);
        img.setBackgroundResource(R.drawable.logoohnehintergrund);
        //img.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, CENTER));
        img.setVisibility(View.VISIBLE);*/

        final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SplashActivity.this);

        dlgAlert.setMessage("Keine Gewähr für diese Angaben. Maßgeblich ist immer der Vertretungsplan in der Pausenhalle.");
        dlgAlert.setTitle("Wichtig");
        dlgAlert.setPositiveButton("Verstanden!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dlgAlert.setCancelable(true);
        AlertDialog dlg = dlgAlert.create();

        /* // Dialog wird an den unteren bildschirmrand gesetzt
        Window window = dlg.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        */
        dlg.show();

    }
}
