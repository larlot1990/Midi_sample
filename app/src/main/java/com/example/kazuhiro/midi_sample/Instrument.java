package com.example.kazuhiro.midi_sample;

public class Instrument {
    private MidiControl.MidiChannel channel ;

    public int getMIDIchannel(){
        return this.channel.getChannelNo();
    }

    public int getInstrumentNo(){
        return this.channel.getInstrumentNo();
    }

    public Instrument( MidiControl midiControl, int InstrumentNo, MidiControl.MIDImode midiMode){
        this.channel = midiControl.openChannel(InstrumentNo, midiMode) ;
    }
    public void noteOn( int pitch, int velocity){
        this.channel.noteOn((byte)pitch, (byte)velocity);
    }
    public void noteOn( int octave, int pitch, int velocity){
        byte pitchNo;
        pitchNo = (byte)(octave*12 + pitch%12) ;
        this.channel.noteOn(pitchNo, (byte)velocity);
    }
    public void noteOff( int pitch){
        this.channel.noteOff((byte)pitch);
    }
    public void noteOff( int octave, int pitch){
        byte pitchNo;
        pitchNo = (byte)(pitch*12 + pitch%12) ;
        this.channel.noteOff(pitchNo);
    }
    public void allOff(){
        this.channel.allOff();
    }
}
