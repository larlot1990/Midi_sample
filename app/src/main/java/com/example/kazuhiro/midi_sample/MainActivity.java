package com.example.kazuhiro.midi_sample;

import android.hardware.usb.UsbDevice;
import android.media.midi.MidiDeviceInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    MidiControl midiControl;
    private Toast toastMessage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        this.midiControl = new MidiControl();
        if( this.midiControl.Initialize(MainActivity.this)) {
            this.midiControl.openMidiDevice() ;
            MidiDeviceInfo info ;

            int retry = 0 ;
            do {
                info = this.midiControl.getSequencerInfo();
                if(null == info){
                    try {
                        Thread.sleep(100);
                    }
                    catch(InterruptedException e){
                        this.toastMessage = Toast.makeText(this, "Thread.Sleep exception!.", Toast.LENGTH_SHORT);
                        this.toastMessage.setGravity(Gravity.CENTER, 0, 0);
                        this.toastMessage.show();
                    }
                    retry++ ;
                }
            }while( null == info && 3 > retry) ;

            if (null != info) {
                Bundle prop = info.getProperties();

                // 製造元文字列（"Roland"など）
                String stringData = prop.getString(info.PROPERTY_MANUFACTURER);
                TextView textView = (TextView) findViewById(R.id.textViewOfManufacture);
                textView.setText(stringData);

                // 製品名文字列（"EDIROL SD-90"など）
                stringData = prop.getString(info.PROPERTY_PRODUCT);
                textView = (TextView) findViewById(R.id.textViewOfProduct);
                textView.setText(stringData);

                // 機器名文字列（"Roland EDIROL SD-90"など）
                stringData = prop.getString(info.PROPERTY_NAME);
                textView = (TextView) findViewById(R.id.textViewOfName);
                textView.setText(stringData);

                // バージョン文字列（"1.16"など）
                stringData = prop.getString(info.PROPERTY_VERSION);
                textView = (TextView) findViewById(R.id.textViewOfVersion);
                textView.setText(stringData);

                // Midiデバイスの接続方法確認
                UsbDevice usbDevice = (UsbDevice) prop.get(info.PROPERTY_USB_DEVICE);
                if (prop.getBoolean(info.PROPERTY_USB_DEVICE)) {
                    textView = (TextView) findViewById(R.id.textViewOfConnectType);
                    textView.setText("USB connect");
                } else if (prop.getBoolean(info.PROPERTY_BLUETOOTH_DEVICE)) {
                    textView = (TextView) findViewById(R.id.textViewOfConnectType);
                    textView.setText("Bluetooth connect");
                } else {
                    textView = (TextView) findViewById(R.id.textViewOfConnectType);
                    textView.setText("Inner MIDI device");
                }

                // 入力ポート数確認
                int numInputs = info.getInputPortCount();
                textView = (TextView) findViewById(R.id.textViewOfImports);
                textView.setText( "Number of input ports: " + Integer.toString(numInputs));

                // 出力ポート数確認
                int numOutputs = info.getOutputPortCount();
                textView = (TextView) findViewById(R.id.textViewOfOutports);
                textView.setText("Number of output ports: " + Integer.toString(numOutputs));
            }
        }
        // ボタン押下時の処理を追加
        Button enevtButton = (Button)findViewById(R.id.buttonOfSoundTest) ;
        enevtButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onClick(View v){
/*
        this.toastMessage = Toast.makeText(MainActivity.this, "Clicked button!", Toast.LENGTH_SHORT);
        this.toastMessage.setGravity(Gravity.CENTER, 0, 0);
        this.toastMessage.show();
*/
        // ボタン押下時に音を出す
        this.midiControl.noteOn() ;
    }
}
