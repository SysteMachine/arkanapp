package com.example.android.arkanoid.FragmentMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.AudioUtil;

public class impostazioni_fragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private SeekBar musicaSeek;
    private SeekBar effettiSeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impostazioni_fragment, container, false);

        this.musicaSeek = view.findViewById(R.id.volumeMusicaSeek);
        this.effettiSeek = view.findViewById(R.id.volumeEffettiSeek);

        if(this.musicaSeek != null) {
            this.musicaSeek.setProgress(AudioUtil.getVolumeMusicaAudio());
            this.musicaSeek.setOnSeekBarChangeListener(this);
        }
        if(this.effettiSeek != null){
            this.effettiSeek.setProgress(AudioUtil.getVolumeEffettiAudio());
            this.effettiSeek.setOnSeekBarChangeListener(this);
        }

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar.equals(this.effettiSeek)) {
            AudioUtil.setVolumeAudioEffetti(progress);
        }
        if(seekBar.equals(this.musicaSeek)) {
            AudioUtil.setVolumeAudioMusica(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}