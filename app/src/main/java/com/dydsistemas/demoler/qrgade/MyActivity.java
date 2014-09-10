package com.dydsistemas.demoler.qrgade;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //Declaramos las variables de los botones
        final Button btnScan = (Button)findViewById(R.id.btnScan);
        final TextView txtResult = (TextView)findViewById(R.id.tvResult);

        //Listener para el boton de Jugar
        btnScan.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //new IntentIntegrator(QrReaderActity.this).initiateScan();
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                } catch (ActivityNotFoundException exception) {
                   // Toast.makeText(viewGroup.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == 0) && (resultCode == -1)) {
            updateUITextViews(data.getStringExtra("SCAN_RESULT"), data.getStringExtra("SCAN_RESULT_FORMAT"));
            mostrarPagina(data.getStringExtra("SCAN_RESULT"));
        } else {
            // Toast.makeText(viewGroup.getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleResult(IntentResult scanResult) {
        if (scanResult != null) {
            updateUITextViews(scanResult.getContents(), scanResult.getFormatName());
        } else {
            Toast.makeText(this, "No se ha leído nada :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUITextViews(String scan_result, String scan_result_format) {
        //((TextView)findViewById(R.id.tvFormat)).setText(scan_result_format);
        final TextView tvResult = (TextView)findViewById(R.id.tvResult);
        tvResult.setText(scan_result);
        //Linkify.addLinks(tvResult, Linkify.ALL);
    }

    private void mostrarPagina(String scan_result){
        final TextView tvResult = (TextView)findViewById(R.id.tvResult);
        //tvResult.setText(scan_result);
        //final EditText txtCodigo = (EditText)findViewById(R.id.editText);
        tvResult.setText(scan_result);

        //Creamos el Intent
        //Intent intent = new Intent(MyActivity.this, ScanActivity.class);
        Intent intent = new Intent(MyActivity.this, ResultActivity.class);

        //Creamos la información a pasar entre actividades
        Bundle b = new Bundle();
        b.putString("CODIGO", tvResult.getText().toString());

        //Añadimos la información al intent
        intent.putExtras(b);

        //Iniciamos la nueva actividad
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.about) {
            //abro activity about
            return true;
        }
        if (id == R.id.about) {
            //abro activity about
            return true;
        }

        }
        return super.onOptionsItemSelected(item);*/

    // Handle item selection
        switch (item.getItemId()) {
            case R.id.about:
                //abro activity about
                Intent intent = new Intent(MyActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.exit:
                //abro activity cerrar
                //Intent salida=new Intent( Intent.ACTION_MAIN); //Llamando a la activity principal
                //startActivity(salida);
                finish(); // La cerramos.
                System.exit(0);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
