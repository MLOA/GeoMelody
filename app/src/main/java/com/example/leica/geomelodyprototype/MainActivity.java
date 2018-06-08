package com.example.leica.geomelodyprototype;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity implements LocationListener {

    String title, user, date, lat, lng, file_Name;

    EditText user_edit, title_edit;

    Runnable updateText;

    Handler mHandler = new Handler();    // スレッドUI操作用ハンドラ

    boolean buttonState = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // GPS

        LocationManager mLocationManager =  (LocationManager) getSystemService( Context.LOCATION_SERVICE );   // LocationManagerを取得
        Criteria criteria = new Criteria();                                  // Criteriaオブジェクトを生成
        criteria.setAccuracy( Criteria.ACCURACY_COARSE );                      // Accuracyを指定(低精度)
        criteria.setPowerRequirement( Criteria.POWER_LOW );                    // PowerRequirementを指定(低消費電力)
        String provider = mLocationManager.getBestProvider( criteria, true );  // ロケーションプロバイダの取得
        mLocationManager.requestLocationUpdates( provider, 0, 0, this );       // LocationListenerを登録

    }

    @Override
    public void onLocationChanged(Location location) {              // GPS lat lng set
        lat = String.valueOf( location.getLatitude() );
        lng = String.valueOf( location.getLongitude() );
    }
    @Override
    public void onProviderDisabled(String provider) {    }
    @Override
    public void onProviderEnabled(String provider) {    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }  // GPS


    // record

    MediaRecorder recorder;

    int count;

    Context c = this;

    public void onClick(View v){

        int id = v.getId();

        if ( id == R.id.recordButton && buttonState ){           // start recording

            count = 10;

            buttonState = false;

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

            // Date
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );     // make format
            date = sdf.format( new Date() );
            file_Name = date + ".3gp";

            String filePath = Environment.getExternalStorageDirectory() + "/" + file_Name;                //保存先
            recorder.setOutputFile( filePath );

            try {
                recorder.prepare();                    //録音準備＆録音開始

            } catch (Exception e) {
                e.printStackTrace();
            }

            recorder.start();                   //録音開始


            updateText = new Runnable() {                                        // time count

                @Override
                public void run() {

                    if( count > 0 ) {
                        mHandler.removeCallbacks(updateText);
                        mHandler.postDelayed(updateText, 1000);                    // 10s count

                        count--;
                        setTitle( "Now Recording " + count + "s left");

                    } else if ( count == 0 ){           // stop record
                        recorder.stop();
                        recorder.reset();        //オブジェクトのリセット
                        recorder.release(); //Recorderオブジェクトの解放

                        String filePath = Environment.getExternalStorageDirectory() + "/" + file_Name;

                        UploadAsyncTask upload = new UploadAsyncTask( c );  // HTTP communicator


                        // get text from editor

                        user_edit = (EditText)findViewById(R.id.editUser);
                        title_edit = (EditText)findViewById(R.id.editTitle);


                        String user = user_edit.getText().toString();
                        String title = title_edit.getText().toString();

                        upload.lat = lat;
                        upload.lng = lng;
                        upload.date = date; // 2014 12/25 12:30 30 555 ms
                        upload.title = title;
                        upload.user = user;

                        upload.execute( filePath );         // サーバにアップロード

                        title_edit.setText("");         // reset title edit box

                        setTitle( "Geo Melody" );

                        buttonState = true;     // unlock start button
                    }
                }

            };
            if( count > 0 ){
                mHandler.postDelayed(updateText, 1000);                    // 10s count
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settingsMap) {
            Intent intent = new Intent( this, PinActivity.class );

            intent.putExtra( "lat", lat );      // send to pin lat lng
            intent.putExtra( "lng", lng );

            startActivityForResult( intent , 0 );

            overridePendingTransition( 0, 0 );      // screen transition
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}

// upload class

@SuppressWarnings("deprecation")
class UploadAsyncTask extends AsyncTask<String , Integer, Integer> {

    String date, title, user, lat, lng;

    private Context context;
    private String ReceiveStr;

    public UploadAsyncTask(Context context) {
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Integer doInBackground(String... params) {
        try {
            /*
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http");
            builder.authority("mloa.net");
            builder.path("/openhacku/sqlDeb.php");
            builder.appendQueryParameter( "date", date );
            builder.appendQueryParameter( "title", title );
            builder.appendQueryParameter( "user", date );
            builder.appendQueryParameter( "lat", lat );
            builder.appendQueryParameter( "lng", lng );

            URL = builder.build().toString();
            */

            String URL = "http://mloa.net/geomelody/sqlDeb.php" + "?date=" + date + "&title=" + title + "&user=" + user + "&lat=" + lat + "&lng=" + lng;       // URL !!

            String fileName = params[0];

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost( URL );

            ResponseHandler<? extends String> responseHandler = new BasicResponseHandler();

            MultipartEntity multipartEntity = new MultipartEntity();

            multipartEntity.addPart( "upfile", new FileBody( new File( fileName ) ) );

            httpPost.setEntity( multipartEntity );

            ReceiveStr = httpClient.execute( httpPost, responseHandler );

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        //Toast.makeText(context, ReceiveStr, Toast.LENGTH_LONG).show();   // サーバ側phpでechoした内容を表示
        Toast.makeText(context, "Complete", Toast.LENGTH_LONG).show();
    }


}