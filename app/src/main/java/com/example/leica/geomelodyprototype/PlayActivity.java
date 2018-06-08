package com.example.leica.geomelodyprototype;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class PlayActivity extends Activity {

    Uri audioURL;

    String id, title, user, date, comment;

    TextView titleT, userT, dateT, commentT;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_play );

        setTitle("Geo Melody");

        titleT = (TextView) findViewById( R.id.titleText );
        userT = (TextView) findViewById( R.id.userText );
        dateT = (TextView) findViewById( R.id.dateText );
        commentT = (TextView) findViewById( R.id.commentText );

        Intent intent = getIntent();
        id = intent.getStringExtra( "id" );
        title = intent.getStringExtra( "title" );
        user = intent.getStringExtra( "user" );
        comment = intent.getStringExtra( "comment" );


        date =    toDate( intent.getStringExtra("date") );


        titleT.setText( title );
        userT.setText( user );
        dateT.setText( date );
        commentT.setText( comment );

        audioURL = Uri.parse( "http://mloa.net/geomelody/sounds/" + id + ".3gp" );


        mediaPlayer = MediaPlayer.create( this, audioURL );

        mediaPlayer.start();

    }

    public String toDate( String d ){       // 17 char -> 4444 2/2 22:22 (22s 333ms)

        String y = d.substring(0, 4) + " " + d.substring(4, 6) + "/" + d.substring(6, 8) + " " + d.substring(8, 10) + ":" + d.substring(10, 12);

        return y;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settingsMapReturn) {
            mediaPlayer.stop();
            mediaPlayer.release();

            //startActivityForResult( ,);
            finish();
            overridePendingTransition( 0, 0 );                                        // screen transition
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
