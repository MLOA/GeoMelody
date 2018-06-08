package com.example.leica.geomelodyprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import jp.co.yahoo.android.maps.*;

/*

String ltLat = "35.71449052870013", ltLon = "139.64799363376721", rbLat = "35.700565446224154", rbLon = "139.6708589294720";

String key = "?input1=" + ltLat + "&input2=" + ltLon + "&input3=" + rbLat + "&input4=" + rbLon;

String requestURL = "http://mloa.net/openhacku/sqlRequest.php" + key;
 */


public class MapActivity extends Activity {

    // map
    String appID = "dj0zaiZpPWpXN1B1TVduRXNHMSZzPWNvbnN1bWVyc2VjcmV0Jng9ZWI-";  // yahoo application ID

    private MapView mapView = null;            //MapViewメンバー
    MapController controller;

    GeoPoint mid;

    Activity activity = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        // map
        mapView = new MapView(this, appID);  // mapを作成

        mapView.setBuiltInZoomControls(true);  // map に縮尺バーを追加

        controller = mapView.getMapController();  // map操作

        controller.setCenter( new GeoPoint( 35665721, 139731006 ) );     //中心座標
        controller.setZoom( 1 );                                        //縮尺

        setContentView( mapView );        // map display!!



        mid = new GeoPoint( 35665721, 139731006 );      // pin

        PinOverlay pinOverlay = new PinOverlay( PinOverlay.PIN_VIOLET);

        mapView.getOverlays().add( pinOverlay );                //ピンを描画

        PopupOverlay popupOverlay = new PopupOverlay() {

            public void onTap( OverlayItem item ) {
                controller.animateTo( mid );        //マーカーへ移動

                Intent intent = new Intent( activity, PlayActivity.class );

                String id = "20141225210030555", title = "タイトル", user = "樹脂", date = id, comment = "yourcomment here!";
                intent.putExtra( "id", id );
                intent.putExtra( "title", title );
                intent.putExtra( "user", user );
                intent.putExtra( "date", date );
                intent.putExtra( "comment", comment );

                startActivityForResult( intent , 0 );

                overridePendingTransition( 0, 0 );      // screen transition

            }
        };
        mapView.getOverlays().add( popupOverlay );

        pinOverlay.setOnFocusChangeListener( popupOverlay );

        pinOverlay.addPoint( mid, "MID town", "mid town snippet" );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate( R.menu.menu_map, menu );

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settingsMain) {
            startActivityForResult( new Intent( this, MainActivity.class ) , 0 );
            overridePendingTransition( 0, 0 );      // screen transition

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
