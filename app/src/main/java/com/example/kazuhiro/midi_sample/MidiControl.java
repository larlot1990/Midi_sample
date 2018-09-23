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
    private MidiInputPort inPort ;

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

    public void openMidiDevice(){
        // MIDIデバイスをオープンする。
        MidiDeviceInfo[] infos = this.getMidiDeviceInfo();
        // 暫定的にMIDIデバイスは1台のみの想定
        this.midiManager.openDevice(infos[0], new MidiManager.OnDeviceOpenedListener() {
            @Override
            public void onDeviceOpened(MidiDevice device) {
                midiDevice = device ;
                // MIDIデバイスの入力ポートをオープンする。（MIDIデータの入力先）
                inPort = midiDevice.openInputPort(0);
            }
        }, null);
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