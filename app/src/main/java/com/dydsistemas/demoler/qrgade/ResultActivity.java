package com.dydsistemas.demoler.qrgade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JGriguol on 05/09/2014.
 */
public class ResultActivity extends Activity {

    TextView txtPrecio;
    TextView txtDescripcion;
    ImageView imgImagen;

    String pid;

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParserProducto jsonParser = new JSONParserProducto();
    // single product url
    private static final String url_product_detials = "http://www.gadeweb.com.ar/android-mysql/get_product_details.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_PID = "pid";
    private static final String TAG_PRICE = "precio";
    private static final String TAG_DESCRIPTION = "descripcion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //habilita permisos para versiones de SDK superiores a 9
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (!verificaConexion(this)) {


            /*    Dialog compatible SDK 7   anda bien  */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.noconnection))
                    .setTitle(getString(R.string.atencion))
                    .setCancelable(false)
                    .setNeutralButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //Vuelvo al main activity
                                    Intent intent = new Intent(ResultActivity.this, MyActivity.class);
                                    //Iniciamos la nueva actividad
                                    startActivity(intent);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();


        } else {
            //Hago consulta y muestro datos
            TextView codigo = (TextView) findViewById(R.id.txtCodigo);
            Bundle bundle = this.getIntent().getExtras();
            codigo.setText(bundle.getString("CODIGO"));

            // getting product details from intent
            //Intent i = getIntent();

            //getting product id (pid) from intent
            //pid = i.getStringExtra(TAG_PID);
            pid = bundle.getString("CODIGO");

            // Getting complete product details in background thread
            new GetProductDetails().execute();


        }

    }


    /**
     * Background Async Task to Get complete product details
     * */
    class GetProductDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ResultActivity.this);
            pDialog.setMessage("Loading product details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("pid", pid));

                        // getting product details by making HTTP request
                        // Note that product details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_product_detials, "GET", params);

                        // check your log for json response
                        Log.d("Single Product Details", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received product details
                            JSONArray productObj = json
                                    .getJSONArray(TAG_PRODUCT); // JSON Array

                            // get first product object from JSON Array
                            JSONObject product = productObj.getJSONObject(0);

                            // product with this pid found
                            // Edit Text
                            TextView Precio = (TextView) findViewById(R.id.txtPrecio);
                            TextView Descripcion = (TextView) findViewById(R.id.txtDescripcion);
                            ImageView Imagen = (ImageView) findViewById(R.id.imgImagen);


                            // display product data in EditText
                            Precio.setText("$ " + product.getString(TAG_PRICE));
                            if(product.getString(TAG_DESCRIPTION)!=null) {
                                Descripcion.setText(product.getString(TAG_DESCRIPTION));
                            }
                            else{
                                Descripcion.setText("Este producto no cuenta con una descripción");
                            }

                            //Imagen.setImageURI(Uri.parse("http://www.gadeweb.com.ar/images/ofertas/" + product.get(TAG_IMAGE)));
                            String ursString = "http://www.gadeweb.com.ar/images/ofertas/" + product.get(TAG_IMAGE);
                            try {

                                URL myFileUrl = new URL(ursString);
                                HttpURLConnection conn =
                                        (HttpURLConnection) myFileUrl.openConnection();
                                conn.setDoInput(true);
                                conn.connect();

                                InputStream is = conn.getInputStream();
                                Imagen.setImageBitmap(BitmapFactory.decodeStream(is));



                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }else{
                            // product with pid not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }

    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // No sólo wifi, también GPRS
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        // este bucle debería no ser tan ñapa
        for (int i = 0; i < 2; i++) {
            // ¿Tenemos conexión? ponemos a true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.about:
                //abro activity about
                Intent intent = new Intent(ResultActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
