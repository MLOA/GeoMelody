package com.example.leica.geomelodyprototype;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import jp.co.yahoo.android.maps.*;


public class PinActivity extends MapActivity implements LocationListener {
    /** Called when the activity is first created. */
    int width, height;
    String ltLat,ltLon,rbLat,rbLon;

    double lat, lng;

    String appID = "dj0zaiZpPWpXN1B1TVduRXNHMSZzPWNvbnN1bWVyc2VjcmV0Jng9ZWI-";  // yahoo application ID

    Projection projection;

    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        setTitle("Geo Melody");


        mapView = new MapView( this, appID );
        mapView.setBuiltInZoomControls( true );
        mapView.setLongPress( true );

        Intent intent = getIntent();
        lat = Double.valueOf( intent.getStringExtra( "lat" ) );
        lng = Double.valueOf( intent.getStringExtra( "lng" ) );

        GeoPoint here = new GeoPoint( ( int )( lat * 1000000 ), ( int )( lng * 1000000 ) );


        MapController c = mapView.getMapController();
        c.setCenter( here );                              //初期表示の地図を指定
        c.setZoom( 3 );                                   //初期表示の縮尺を指定


        WindowManager wm = (WindowManager) getSystemService( WINDOW_SERVICE );
        Display disp = wm.getDefaultDisplay();
        width = disp.getWidth();
        height = disp.getHeight();

        FrameLayout mainLayout = (FrameLayout)findViewById( R.id.mapContainer );

        mapView.setMapTouchListener( new MapView.MapTouchListener() {

            @Override
            public boolean onTouch(MapView arg0, MotionEvent arg1) {

                projection = mapView.getProjection();
                String ltLat, ltLon, rbLat, rbLon;

                GeoPoint LT = projection.fromPixels( 0, 0 );                //地図の左上、右下の座標の緯度経度を取得
                GeoPoint RB = projection.fromPixels( width, height );

                ltLat = String.valueOf( LT.getLatitude() );
                ltLon = String.valueOf( LT.getLongitude() );
                rbLat = String.valueOf( RB.getLatitude() );
                rbLon = String.valueOf( RB.getLongitude() );
                Log.d("now" , ltLat+" , "+ltLon+" , "+rbLat+" , "+rbLon);
                return false;
            }

            @Override
            public boolean onPinchOut(MapView arg0) {
                return false;
            }

            @Override
            public boolean onPinchIn(MapView arg0) {
                return false;
            }

            @Override
            public boolean onLongPress(MapView arg0, Object arg1, PinOverlay arg2, GeoPoint arg3) {

                ArrayList<Overlay> overlays = (ArrayList<Overlay>) arg0.getOverlays();
                overlays.clear();
                arg2.clearPoint();

                PinOverlay pinOverlay = new PinOverlay( PinOverlay.PIN_RED );       // tap point pin
                mapView.getOverlays().add( pinOverlay );


                pinOverlay.addPoint( arg3, null );


                //String ltLat = "35.71449052870013", ltLon = "139.64799363376721", rbLat = "35.700565446224154", rbLon = "139.6708589294720";


                projection = mapView.getProjection();

                GeoPoint LT = projection.fromPixels( 0, 0 );                //地図の左上、右下の座標の緯度経度を取得
                GeoPoint RB = projection.fromPixels( width, height );

                ltLat = String.valueOf( LT.getLatitude() );
                ltLon = String.valueOf( LT.getLongitude() );
                rbLat = String.valueOf( RB.getLatitude() );
                rbLon = String.valueOf( RB.getLongitude() );


                String key = "?input1=" + ltLat + "&input2=" + ltLon + "&input3=" + rbLat + "&input4=" + rbLon;

                String requestURL = "http://mloa.net/geomelody/sqlRequest.php" + key;


                HttpAsyncTask httpClient = new HttpAsyncTask();
                httpClient.execute( requestURL );

                return false;
            }


        });

        //mainLayout.addView( mapView );


        ArrayList<Overlay> overlays = (ArrayList<Overlay>) mapView.getOverlays();
        overlays.clear();
        //arg2.clearPoint();

        PinOverlay pinOverlay = new PinOverlay( PinOverlay.PIN_RED );       // tap point pin
        mapView.getOverlays().add( pinOverlay );

        pinOverlay.addPoint( here, null );



        String ltLat, ltLon, rbLat, rbLon;

        projection = mapView.getProjection();

        GeoPoint LT = projection.fromPixels( 0, 0 );                //地図の左上、右下の座標の緯度経度を取得
        GeoPoint RB = projection.fromPixels( width, height );

        /*   ltLat = String.valueOf( LT.getLatitude() );
        ltLon = String.valueOf( LT.getLongitude() );
        rbLat = String.valueOf( RB.getLatitude() );
        rbLon = String.valueOf( RB.getLongitude() );
        */

        /*   ltLat = "35.71449052870013";
        ltLon = "139.64799363376721";
        rbLat = "35.700565446224154";
        rbLon = "139.6708589294720";
        */
        Point point = projection.toPixels(here,null);

        GeoPoint leftTop = projection.fromPixels(point.x - width/2, point.y - height/2);
        GeoPoint rightBottom = projection.fromPixels(point.x + width/2, point.y + height/2);

        ltLat = String.valueOf( leftTop.getLatitude() );
        ltLon = String.valueOf( leftTop.getLongitude() );
        rbLat = String.valueOf( rightBottom.getLatitude() );
        rbLon = String.valueOf( rightBottom.getLongitude() );

        String key = "?input1=" + ltLat + "&input2=" + ltLon + "&input3=" + rbLat + "&input4=" + rbLon;

        String requestURL = "http://mloa.net/geomelody/sqlRequest.php" + key;

        //Log.d( "key", key );
        //Log.d( "width, height", "" + width + "," + height);

        HttpAsyncTask httpClient = new HttpAsyncTask();
        httpClient.execute( requestURL );

        mainLayout.addView( mapView );
    }

    // GPS
    @Override
    public void onLocationChanged(Location location) {              // GPS lat lng set
        lat = location.getLatitude();
        lng = location.getLongitude() ;
    }
    @Override
    public void onProviderDisabled(String provider) {    }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }  // GPS



    public  class HttpAsyncTask extends AsyncTask<String, Integer, JSONArray>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPostExecute(JSONArray result) {
            super.onPostExecute( result );

            try {
                for (int i = 0; i < result.length(); i++) {

                    JSONObject item = result.getJSONObject( i );

                    if ( item.getString( "Title" ) == null ) return;        // null jason = no hit


                    final String iID = item.getString( "ID" ), iTitle = item.getString( "Title" ), iUser = item.getString( "User" ), iComment = item.getString( "Comment" );

                    double iLat = Double.valueOf( item.getString( "Lat" ) ), iLng = Double.valueOf( item.getString( "Lng" ) );


                    GeoPoint mid = new GeoPoint( ( int )( iLat * 1000000 ), ( int )( iLng * 1000000 ) );        // 35665721, 139731006 <- 35.7474041  139.8582225

                    PinOverlay pinOverlay = new PinOverlay( PinOverlay.PIN_VIOLET );

                    mapView.getOverlays().add( pinOverlay );

                    PopupOverlay popupOverlay = new PopupOverlay() {

                        public void onTap( OverlayItem item ) {
                            Intent intent = new Intent( activity, PlayActivity.class );                            // page transition

                            intent.putExtra( "id", iID );
                            intent.putExtra( "title", iTitle );
                            intent.putExtra( "user", iUser );
                            intent.putExtra( "date", iID );
                            intent.putExtra( "comment", iComment );

                            startActivityForResult( intent , 0 );

                            overridePendingTransition( 0, 0 );      // screen transition

                        }
                    };
                    mapView.getOverlays().add( popupOverlay );
                    pinOverlay.setOnFocusChangeListener( popupOverlay );


                    pinOverlay.addPoint( mid, iTitle, iTitle );

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog( PinActivity.this);
            progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
            progressDialog.setMessage( "Send Request..." );
            progressDialog.setCancelable( true );
            progressDialog.show();

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            synchronized (this) {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(params[0]);

                try {
                    JSONArray result = httpClient.execute(request,
                            new ResponseHandler<JSONArray>() {

                                @Override
                                public JSONArray handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                                    switch (response.getStatusLine().getStatusCode()) {
                                        case HttpStatus.SC_OK:

                                            try {
                                                String data = EntityUtils.toString(response.getEntity(),"UTF-8");
                                                //Log.d("data",data);
                                                return new JSONArray( data );

                                            } catch (ParseException e) {
                                                e.printStackTrace();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        case HttpStatus.SC_NOT_FOUND:
                                            throw new RuntimeException("not found");

                                        default:
                                            throw new RuntimeException("error");
                                    }
                                }
                            });
                    return result;
                } catch (ClientProtocolException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
    }

}