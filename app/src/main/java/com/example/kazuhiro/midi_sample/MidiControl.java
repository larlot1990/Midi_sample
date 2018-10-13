package com.example.kazuhiro.midi_sample;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;

public class MidiControl {
    private Context context ;
    private Toast toastMessage ;
    private MidiManager midiManager ;
    private MidiDevice midiDevice ;
    private int InputPortNum ;
    private MidiInputPort inPort ;

    class OnMidiDeviceOpenedListener implements MidiManager.OnDeviceOpenedListener{
        @Override
        public void onDeviceOpened(MidiDevice device) {
            // MIDIデバイスの入力ポートをオープンする。（MIDIデータの入力先）
            MidiControl.this.midiDevice = device ;
            inPort = MidiControl.this.midiDevice.openInputPort(MidiControl.this.InputPortNum);
        }
    }
    OnMidiDeviceOpenedListener OpenListener ;

    public boolean Initialize(Context context) {
        this.context = context;
        this.toastMessage = Toast.makeText(this.context, "Midi initialize", Toast.LENGTH_SHORT);
        this.toastMessage.setGravity(Gravity.CENTER, 0, 0);
        this.toastMessage.show();
        if (null == this.midiManager) {
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                this.midiManager = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
                this.toastMessage = Toast.makeText(this.context, "Midi was supported.", Toast.LENGTH_SHORT);
                this.toastMessage.setGravity(Gravity.CENTER, 0, 0);
                this.toastMessage.show();
            }
            else {
                this.toastMessage = Toast.makeText(this.context, "Midi was NOT supported.", Toast.LENGTH_SHORT);
                this.toastMessage.setGravity(Gravity.CENTER, 0, 0);
                this.toastMessage.show();
            }
        }
        if(null == this.midiManager){
            return false ;
        }
        return true ;
    }

    public MidiDeviceInfo[] getMidiDeviceInfo() {
        return this.midiManager.getDevices();
    }

    public MidiDeviceInfo   getSequencerInfo(){
        if(null != this.midiDevice) {
            return this.midiDevice.getInfo();
        }
        else{
            return  null ;
        }
    }

    public void openMidiDevice(){
        // MIDIデバイスをオープンする。
        MidiDeviceInfo[] deviceInfos = this.getMidiDeviceInfo();
        // 最初に見つけたMIDIシーケンサーに接続する。
        int i, j ;
        MIDI_SEARCH_LOOP:   for ( i = 0; i < deviceInfos.length ; i++) {
            MidiDeviceInfo.PortInfo[] portInfos = deviceInfos[i].getPorts();
            for ( j = 0; j < portInfos.length; j++){
                if (MidiDeviceInfo.PortInfo.TYPE_INPUT == portInfos[j].getType()) {
                    this.InputPortNum = j ;
                    this.OpenListener = new OnMidiDeviceOpenedListener() ;
                    // MIDIデバイスをオープンする。
                    this.midiManager.openDevice(deviceInfos[i], this.OpenListener, null);
                    break MIDI_SEARCH_LOOP;
                }
            }
        }
        if( i >= deviceInfos.length ) {
            this.toastMessage = Toast.makeText(this.context, "Midi sequencer was NOT found.", Toast.LENGTH_SHORT);
            this.toastMessage.setGravity(Gravity.CENTER, 0, 0);
            this.toastMessage.show();
        }
    }

    public void noteOn(){
        // MIDIデータの作成
        byte[] buffer = new byte[32];
        int numBytes = 0;
        int channel = 1; // MIDI channels 1-16 are encoded as 0-15.
        buffer[numBytes++] = (byte)(0x90 + (channel - 1)); // note on
        buffer[numBytes++] = (byte)60; // pitch is middle C
        buffer[numBytes++] = (byte)127; // max velocity
        int offset = 0;

        try {
            // MIDIデバイスへデータを送信
            this.inPort.send(buffer, offset, numBytes);
        }
        catch (IOException e){
            this.toastMessage = Toast.makeText(this.context, "Midi error.", Toast.LENGTH_SHORT);
            this.toastMessage.setGravity(Gravity.CENTER, 0, 0);
            this.toastMessage.show();
        }
    }
}