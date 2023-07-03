package com.example.cosmotesmarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Radio extends AppCompatActivity {

    private TextView radioNameText,radioFreqText;
    private boolean radioPlaying = true;
    private SeekBar radioVolumeSeekBar,radioFreqSeekBar;
    private ImageButton radioVolumeMute,radioVolumeFull,backbtn,radioPlayPauseButton,radioFavouritesButton,radioQuestionButton,radioAddFavouriteButton, micButton;


    private HashMap<Double,String> radioStations = new HashMap<>();

    private HashMap<Double,String> favouriteRadioStations;
    private HashMap<String,Boolean> roomsLighting;
    private boolean heatingSetting; //0 for cold 1 for heat
    private boolean heatingOpen; //0 for closed 1 for open
    private int heatingTemperature;
    private double lastStation;
    private ArrayList<Script> savedScenarios;

    private HashMap<String,Boolean> roomLocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        Intent intent = getIntent();

        micButton = (ImageButton) findViewById(R.id.micImageButton);

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognizer();
            }
        });

        backbtn = (ImageButton) findViewById(R.id.radioBackButton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        radioQuestionButton = (ImageButton) findViewById(R.id.radioQuestionButton);

        radioNameText = (TextView) findViewById(R.id.radioNameText);
        radioFreqText = (TextView) findViewById(R.id.radioFreqText);

        radioFreqSeekBar = (SeekBar) findViewById(R.id.radioFreqSeekBar);
        FreqBar();
        fillStations();

        radioFavouritesButton = (ImageButton) findViewById(R.id.radioFavouriteButton);
        radioPlayPauseButton = (ImageButton) findViewById(R.id.radioPlayPauseButton);
        setRadioPlayPauseButton();

        radioAddFavouriteButton = (ImageButton) findViewById(R.id.radioAddFavouriteButton);
        addFavouriteButton();

        radioVolumeSeekBar = (SeekBar) findViewById(R.id.radioVolumeSeekBar);
        radioVolumeMute = (ImageButton) findViewById(R.id.radioVolumeMute);
        radioVolumeFull = (ImageButton) findViewById(R.id.radioVolumeFull);
        openFavourites();

        //From MainActivity
        if(intent.hasExtra("BUNDLE")) {
            Bundle args = intent.getBundleExtra("BUNDLE");
            favouriteRadioStations = (HashMap<Double, String>) args.getSerializable("STATIONS");
            roomsLighting = (HashMap<String, Boolean>) args.getSerializable("LIGHTING");
            heatingSetting = (boolean) intent.getExtras().getSerializable("HEATINGSETTING");
            heatingTemperature = (int) intent.getExtras().getSerializable("HEATINGTEMPERATURE");
            heatingOpen = (boolean) intent.getExtras().getSerializable("HEATINGOPEN");
            lastStation = (double) intent.getExtras().getSerializable("LASTSTATION");
            roomLocks = (HashMap<String,Boolean>) args.getSerializable("LOCKS");
            savedScenarios = (ArrayList<Script>) args.getSerializable("SCENARIOS");
        }

        //Back from RadioFavourites
        if(intent.hasExtra("StationName")) {
            double frequency =(double) intent.getExtras().getSerializable("StationFrequency");
            String name = (String) intent.getExtras().getSerializable("StationName");

            ArrayList<FavouriteStation> tempList = (ArrayList<FavouriteStation>) intent.getExtras().getSerializable("StationFavourites");

            for(FavouriteStation fs : tempList) {
                favouriteRadioStations.put(fs.getStationFrequency(),fs.getStationName());
            }

            double temp = (frequency - 87.5) / 0.05;
            int value = (int) temp;

            radioFreqSeekBar.setProgress(value);
            radioFreqText.setText(String.valueOf(frequency));
            radioNameText.setText(name);

        }
        else if(lastStation != 0.0){
            double temp = (lastStation - 87.5) / 0.05;
            int value = (int) temp;

            radioFreqSeekBar.setProgress(value);
            radioFreqText.setText(String.valueOf(lastStation));
            radioNameText.setText(radioStations.get(lastStation));
        }
        HashMap<Double,String> temp = new HashMap<>();
        for (Map.Entry<Double, String> set : favouriteRadioStations.entrySet()) {
            temp.put(set.getKey(),radioStations.get(set.getKey()));
        }
        for(Map.Entry<Double,String> set : temp.entrySet()) {
            favouriteRadioStations.put(set.getKey(),set.getValue());
        }
        
    }

    private void addFavourite(Double frequency, String name ) {
        Resources res = getResources();
        Drawable drawable1 = ResourcesCompat.getDrawable(res, R.drawable.icons8_favorite_50_selected, null);
        Drawable drawable2 = ResourcesCompat.getDrawable(res, R.drawable.icons8_favorite_50, null);
        if(!favouriteRadioStations.containsKey(frequency)) {
            favouriteRadioStations.put(frequency,name);
            radioAddFavouriteButton.setImageDrawable(drawable1);

        }
        else {
            favouriteRadioStations.remove(frequency);
            radioAddFavouriteButton.setImageDrawable(drawable2);

        }
    }

    private void isFavourite(Double frequency) {
        Resources res = getResources();
        Drawable drawable1 = ResourcesCompat.getDrawable(res, R.drawable.icons8_favorite_50_selected, null);
        Drawable drawable2 = ResourcesCompat.getDrawable(res, R.drawable.icons8_favorite_50, null);
        if(favouriteRadioStations.containsKey(frequency)) {
            radioAddFavouriteButton.setImageDrawable(drawable1);
        }
        else {
            radioAddFavouriteButton.setImageDrawable(drawable2);
        }
    }

    private void addFavouriteButton() {
        radioAddFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double currentFrequency = Double.valueOf((String) radioFreqText.getText());
                String currentName = String.valueOf(radioNameText.getText());
                addFavourite(currentFrequency,currentName);
            }
        });
    }

    private void openFavourites() {
        radioFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIntentFavourites();
            }
        });
    }

    private void setRadioPlayPauseButton() {
        radioPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioPlaying) {
                    radioPlayPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icons8_play_button_circled_50, null));
                    radioPlaying = false;
                }
                else {
                    radioPlayPauseButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.icons8_pause_button_64, null));
                    radioPlaying = true;
                }

            }
        });
    }

    private void setIntentFavourites() {
        List<FavouriteStation> favouriteStations = new ArrayList<>();

        for(Map.Entry<Double,String> set : favouriteRadioStations.entrySet()) {
            favouriteStations.add(new FavouriteStation(set.getValue(),set.getKey()));
        }
        Intent intent = new Intent(this, RadioFavourites.class);
        Bundle args = new Bundle();
        args.putSerializable("STATIONSLIST",(Serializable) favouriteStations);
        args.putSerializable("STATIONS",(Serializable) favouriteRadioStations);
        args.putSerializable("LIGHTING",(Serializable) roomsLighting);
        args.putSerializable("LOCKS", (Serializable) roomLocks);
        args.putSerializable("SCENARIOS", (Serializable) savedScenarios);
        intent.putExtra("HEATINGSETTING",heatingSetting);
        intent.putExtra("HEATINGTEMPERATURE",heatingTemperature);
        intent.putExtra("HEATINGOPEN", heatingOpen);
        intent.putExtra("LASTSTATION", lastStation);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    private void createFavList() {
        List<String> stationName = new ArrayList<>();
        List<Double> stationFrequency = new ArrayList<>();

        for(Map.Entry<Double,String> set : favouriteRadioStations.entrySet()) {
            stationName.add(set.getValue());
            stationFrequency.add(set.getKey());
        }
    }

    private void FreqBar() {
        radioFreqSeekBar.setMax(410);

        radioFreqSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double value = 87.5 + i*0.05;
                radioFreqText.setText(String.valueOf(value));
                if(radioStations.containsKey(value)) {
                    radioNameText.setText(radioStations.get(value));
                    isFavourite(value);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void fillStations() {
        radioStations.put(87.5,"Real FM");
        radioStations.put(87.55,"Real FM");
        radioStations.put(87.6,"Real FM");
        radioStations.put(87.65,"Akous - Palko");
        radioStations.put(87.7,"Akous - Palko");
        radioStations.put(87.75,"Sfera FM");
        radioStations.put(87.8,"Sfera FM");
        radioStations.put(87.85,"Sfera FM");
        radioStations.put(87.9,"Sfera FM");
        radioStations.put(87.95,"Sfera FM");
        radioStations.put(88.0,"Sfera FM");
        radioStations.put(88.05,"Rebel State Radio");
        radioStations.put(88.1,"Rebel State Radio");
        radioStations.put(88.15,"Rebel State Radio");
        radioStations.put(88.2,"Rebel State Radio");
        radioStations.put(88.25,"Rebel State Radio");
        radioStations.put(88.3,"Rebel State Radio");
        radioStations.put(88.35,"Rebel State Radio");
        radioStations.put(88.4,"Rebel State Radio");
        radioStations.put(88.45,"Rebel State Radio");
        radioStations.put(88.5,"Rebel State Radio");
        radioStations.put(88.55,"Rebel State Radio");
        radioStations.put(88.6,"Rebel State Radio");
        radioStations.put(88.65,"Rebel State Radio");
        radioStations.put(88.7,"Rebel State Radio");
        radioStations.put(88.75,"Rebel State Radio");
        radioStations.put(88.8,"Rebel State Radio");
        radioStations.put(88.85,"Menta 89.1");
        radioStations.put(88.9,"Menta 89.1");
        radioStations.put(88.95,"Menta 89.1");
        radioStations.put(89.0,"Menta 89.1");
        radioStations.put(89.05,"Menta 89.1");
        radioStations.put(89.1,"Menta 89.1");
        radioStations.put(89.15,"Alpha FM");
        radioStations.put(89.2,"Alpha FM");
        radioStations.put(89.25,"Alpha FM");
        radioStations.put(89.3,"Alpha FM");
        radioStations.put(89.35,"Alpha FM");
        radioStations.put(89.4,"Alpha FM");
        radioStations.put(89.45,"Radio Deejay");
        radioStations.put(89.5,"Radio Deejay");
        radioStations.put(89.55,"Radio Deejay");
        radioStations.put(89.6,"Radio Deejay");
        radioStations.put(89.65,"Radio Deejay");
        radioStations.put(89.7,"Radio Deejay");
        radioStations.put(89.75,"Radio Deejay");
        radioStations.put(89.8,"Radio Deejay");
        radioStations.put(89.85,"Radio Deejay");
        radioStations.put(89.9,"Radio Deejay");
        radioStations.put(89.95,"Radio Deejay");
        radioStations.put(90.0,"Radio Deejay");
        radioStations.put(90.05,"Radio Deejay");
        radioStations.put(90.1,"Radio Deejay");
        radioStations.put(90.15,"Radio Deejay");
        radioStations.put(90.2,"Radio Deejay");
        radioStations.put(90.25,"Radio Deejay");
        radioStations.put(90.3,"Radio Deejay");
        radioStations.put(90.35,"Radio Deejay");
        radioStations.put(90.4,"Radio Deejay");
        radioStations.put(90.45,"Sport FM");
        radioStations.put(90.5,"Sport FM");
        radioStations.put(90.55,"Sport FM");
        radioStations.put(90.6,"Sport FM");
        radioStations.put(90.65,"Sport FM");
        radioStations.put(90.7,"Sport FM");
        radioStations.put(90.75,"Sport FM");
        radioStations.put(90.8,"Sport FM");
        radioStations.put(90.85,"Sport FM");
        radioStations.put(90.9,"Sport FM");
        radioStations.put(90.95,"Sto Kokkino FM");
        radioStations.put(91.0,"Sto Kokkino FM");
        radioStations.put(91.05,"Sto Kokkino FM");
        radioStations.put(91.1,"Sto Kokkino FM");
        radioStations.put(91.15,"Rise FM");
        radioStations.put(91.2,"Rise FM");
        radioStations.put(91.25,"Rise FM");
        radioStations.put(91.3,"Rise FM");
        radioStations.put(91.35,"Rise FM");
        radioStations.put(91.4,"Rise FM");
        radioStations.put(91.45,"Rise FM");
        radioStations.put(91.5,"Rise FM");
        radioStations.put(91.55,"Rise FM");
        radioStations.put(91.6,"Rise FM");
        radioStations.put(91.65,"Rise FM");
        radioStations.put(91.7,"Rise FM");
        radioStations.put(91.75,"Rise FM");
        radioStations.put(91.8,"Rise FM");
        radioStations.put(91.85,"Rise FM");
        radioStations.put(91.9,"Rise FM");
        radioStations.put(91.95,"Rise FM");
        radioStations.put(92.0,"Rise FM");
        radioStations.put(92.05,"Rise FM");
        radioStations.put(92.1,"Rise FM");
        radioStations.put(92.15,"Rise FM");
        radioStations.put(92.2,"Rise FM");
        radioStations.put(92.25,"Rise FM");
        radioStations.put(92.3,"Rise FM");
        radioStations.put(92.35,"Kiss FM");
        radioStations.put(92.4,"Kiss FM");
        radioStations.put(92.45,"Kiss FM");
        radioStations.put(92.5,"Kiss FM");
        radioStations.put(92.55,"Kiss FM");
        radioStations.put(92.6,"Kiss FM");
        radioStations.put(92.65,"Kiss FM");
        radioStations.put(92.7,"Kiss FM");
        radioStations.put(92.75,"Kiss FM");
        radioStations.put(92.8,"Kiss FM");
        radioStations.put(92.85,"Kiss FM");
        radioStations.put(92.9,"Kiss FM");
        radioStations.put(92.95,"Rythmos 93.2");
        radioStations.put(93.0,"Rythmos 93.2");
        radioStations.put(93.05,"Rythmos 93.2");
        radioStations.put(93.1,"Rythmos 93.2");
        radioStations.put(93.15,"Rythmos 93.2");
        radioStations.put(93.2,"Rythmos 93.2");
        radioStations.put(93.25,"Athens DeeJay");
        radioStations.put(93.3,"Athens DeeJay");
        radioStations.put(93.35,"Athens DeeJay");
        radioStations.put(93.4,"Athens DeeJay");
        radioStations.put(93.45,"Athens DeeJay");
        radioStations.put(93.5,"Athens DeeJay");
        radioStations.put(93.55,"Athens DeeJay");
        radioStations.put(93.6,"Athens DeeJay");
        radioStations.put(93.65,"Athens DeeJay");
        radioStations.put(93.7,"Athens DeeJay");
        radioStations.put(93.75,"Athens DeeJay");
        radioStations.put(93.8,"Athens DeeJay");
        radioStations.put(93.85,"Athens DeeJay");
        radioStations.put(93.9,"Athens DeeJay");
        radioStations.put(93.95,"Athens DeeJay");
        radioStations.put(94.0,"Athens DeeJay");
        radioStations.put(94.05,"Athens DeeJay");
        radioStations.put(94.1,"Athens DeeJay");
        radioStations.put(94.15,"Athens DeeJay");
        radioStations.put(94.2,"Athens DeeJay");
        radioStations.put(94.25,"Mad Radio");
        radioStations.put(94.3,"Mad Radio");
        radioStations.put(94.35,"Mad Radio");
        radioStations.put(94.4,"Mad Radio");
        radioStations.put(94.45,"Mad Radio");
        radioStations.put(94.5,"Mad Radio");
        radioStations.put(94.55,"Mad Radio");
        radioStations.put(94.6,"Mad Radio");
        radioStations.put(94.65,"Love Radio");
        radioStations.put(94.7,"Love Radio");
        radioStations.put(94.75,"Love Radio");
        radioStations.put(94.8,"Love Radio");
        radioStations.put(94.85,"Love Radio");
        radioStations.put(94.9,"Love Radio");
        radioStations.put(94.95,"Love Radio");
        radioStations.put(95.0,"Love Radio");
        radioStations.put(95.05,"Love Radio");
        radioStations.put(95.1,"Love Radio");
        radioStations.put(95.15,"Love Radio");
        radioStations.put(95.2,"Love Radio");
        radioStations.put(95.25,"Radio Polis 99.4 FM");
        radioStations.put(95.3,"Radio Polis 99.4 FM");
        radioStations.put(95.35,"Radio Polis 99.4 FM");
        radioStations.put(95.4,"Radio Polis 99.4 FM");
        radioStations.put(95.45,"Radio Polis 99.4 FM");
        radioStations.put(95.5,"Radio Polis 99.4 FM");
        radioStations.put(95.55,"Radio Polis 99.4 FM");
        radioStations.put(95.6,"Radio Polis 99.4 FM");
        radioStations.put(95.65,"Radio Polis 99.4 FM");
        radioStations.put(95.7,"Radio Polis 99.4 FM");
        radioStations.put(95.75,"Radio Polis 99.4 FM");
        radioStations.put(95.8,"Radio Polis 99.4 FM");
        radioStations.put(95.85,"Kosmos FM");
        radioStations.put(95.9,"Kosmos FM");
        radioStations.put(95.95,"Kosmos FM");
        radioStations.put(96.0,"Kosmos FM");
        radioStations.put(96.05,"Kosmos FM");
        radioStations.put(96.1,"Kosmos FM");
        radioStations.put(96.15,"Easy 97.2");
        radioStations.put(96.2,"Easy 97.2");
        radioStations.put(96.25,"Easy 97.2");
        radioStations.put(96.3,"Easy 97.2");
        radioStations.put(96.35,"Easy 97.2");
        radioStations.put(96.4,"Easy 97.2");
        radioStations.put(96.45,"Easy 97.2");
        radioStations.put(96.5,"Easy 97.2");
        radioStations.put(96.55,"Easy 97.2");
        radioStations.put(96.6,"Easy 97.2");
        radioStations.put(96.65,"Easy 97.2");
        radioStations.put(96.7,"Easy 97.2");
        radioStations.put(96.75,"Easy 97.2");
        radioStations.put(96.8,"Easy 97.2");
        radioStations.put(96.85,"Easy 97.2");
        radioStations.put(96.9,"Easy 97.2");
        radioStations.put(96.95,"Easy 97.2");
        radioStations.put(97.0,"Easy 97.2");
        radioStations.put(97.05,"Easy 97.2");
        radioStations.put(97.1,"Easy 97.2");
        radioStations.put(97.15,"Easy 97.2");
        radioStations.put(97.2,"Easy 97.2");
        radioStations.put(97.25,"Kiss FM 97.5");
        radioStations.put(97.3,"Kiss FM 97.5");
        radioStations.put(97.35,"Kiss FM 97.5");
        radioStations.put(97.4,"Kiss FM 97.5");
        radioStations.put(97.45,"Kiss FM 97.5");
        radioStations.put(97.5,"Kiss FM 97.5");
        radioStations.put(97.55,"Dromos FM");
        radioStations.put(97.6,"Dromos FM");
        radioStations.put(97.65,"Dromos FM");
        radioStations.put(97.7,"Dromos FM");
        radioStations.put(97.75,"Dromos FM");
        radioStations.put(97.8,"Dromos FM");
        radioStations.put(97.85,"Dromos FM");
        radioStations.put(97.9,"Dromos FM");
        radioStations.put(97.95,"Dromos FM");
        radioStations.put(98.0,"Dromos FM");
        radioStations.put(98.05,"Radio Nowhere");
        radioStations.put(98.1,"Radio Nowhere");
        radioStations.put(98.15,"Radio Nowhere");
        radioStations.put(98.2,"Radio Nowhere");
        radioStations.put(98.25,"Radio Nowhere");
        radioStations.put(98.3,"Radio Nowhere");
        radioStations.put(98.35,"Radio Nowhere");
        radioStations.put(98.4,"Radio Nowhere");
        radioStations.put(98.45,"Melodia FM");
        radioStations.put(98.5,"Melodia FM");
        radioStations.put(98.55,"Melodia FM");
        radioStations.put(98.6,"Melodia FM");
        radioStations.put(98.65,"Melodia FM");
        radioStations.put(98.7,"Melodia FM");
        radioStations.put(98.75,"Melodia FM");
        radioStations.put(98.8,"Melodia FM");
        radioStations.put(98.85,"Melodia FM");
        radioStations.put(98.9,"Melodia FM");
        radioStations.put(98.95,"Melodia FM");
        radioStations.put(99.0,"Melodia FM");
        radioStations.put(99.05,"Melodia FM");
        radioStations.put(99.1,"Melodia FM");
        radioStations.put(99.15,"Melodia FM");
        radioStations.put(99.2,"Melodia FM");
        radioStations.put(99.25,"Melodia FM");
        radioStations.put(99.3,"Melodia FM");
        radioStations.put(99.35,"Melodia FM");
        radioStations.put(99.4,"Melodia FM");
        radioStations.put(99.45,"Melodia FM");
        radioStations.put(99.5,"Melodia FM");
        radioStations.put(99.55,"Soho Radio");
        radioStations.put(99.6,"Soho Radio");
        radioStations.put(99.65,"Soho Radio");
        radioStations.put(99.7,"Soho Radio");
        radioStations.put(99.75,"Soho Radio");
        radioStations.put(99.8,"Soho Radio");
        radioStations.put(99.85,"Soho Radio");
        radioStations.put(99.9,"Soho Radio");
        radioStations.put(99.95,"Red FM");
        radioStations.put(100.0,"Red FM");
        radioStations.put(100.05,"Red FM");
        radioStations.put(100.1,"Red FM");
        radioStations.put(100.15,"Red FM");
        radioStations.put(100.2,"Red FM");
        radioStations.put(100.25,"Red FM");
        radioStations.put(100.3,"Red FM");
        radioStations.put(100.35,"Rock FM");
        radioStations.put(100.4,"Rock FM");
        radioStations.put(100.45,"Rock FM");
        radioStations.put(100.5,"Rock FM");
        radioStations.put(100.55,"Rock FM");
        radioStations.put(100.6,"Rock FM");
        radioStations.put(100.65,"En Lefko");
        radioStations.put(100.7,"En Lefko");
        radioStations.put(100.75,"En Lefko");
        radioStations.put(100.8,"En Lefko");
        radioStations.put(100.85,"En Lefko");
        radioStations.put(100.9,"En Lefko");
        radioStations.put(100.95,"En Lefko");
        radioStations.put(101.0,"En Lefko");
        radioStations.put(101.05,"En Lefko");
        radioStations.put(101.1,"En Lefko");
        radioStations.put(101.15,"En Lefko");
        radioStations.put(101.2,"En Lefko");
        radioStations.put(101.25,"En Lefko");
        radioStations.put(101.3,"En Lefko");
        radioStations.put(101.35,"Best Radio 92.6");
        radioStations.put(101.4,"Best Radio 92.6");
        radioStations.put(101.45,"Best Radio 92.6");
        radioStations.put(101.5,"Best Radio 92.6");
        radioStations.put(101.55,"Best Radio 92.6");
        radioStations.put(101.6,"Best Radio 92.6");
        radioStations.put(101.65,"Best Radio 92.6");
        radioStations.put(101.7,"Best Radio 92.6");
        radioStations.put(101.75,"Rythmos FM");
        radioStations.put(101.8,"Rythmos FM");
        radioStations.put(101.85,"Rythmos FM");
        radioStations.put(101.9,"Rythmos FM");
        radioStations.put(101.95,"Rythmos FM");
        radioStations.put(102.0,"Rythmos FM");
        radioStations.put(102.05,"Parapolitika FM");
        radioStations.put(102.1,"Parapolitika FM");
        radioStations.put(102.15,"Parapolitika FM");
        radioStations.put(102.2,"Parapolitika FM");
        radioStations.put(102.25,"Parapolitika FM");
        radioStations.put(102.3,"Parapolitika FM");
        radioStations.put(102.35,"Parapolitika FM");
        radioStations.put(102.4,"Parapolitika FM");
        radioStations.put(102.45,"Parapolitika FM");
        radioStations.put(102.5,"Parapolitika FM");
        radioStations.put(102.55,"Parapolitika FM");
        radioStations.put(102.6,"Parapolitika FM");
        radioStations.put(102.65,"Parapolitika FM");
        radioStations.put(102.7,"Parapolitika FM");
        radioStations.put(102.75,"Derti FM");
        radioStations.put(102.8,"Derti FM");
        radioStations.put(102.85,"Derti FM");
        radioStations.put(102.9,"Derti FM");
        radioStations.put(102.95,"Derti FM");
        radioStations.put(103.0,"Derti FM");
        radioStations.put(103.05,"Derti FM");
        radioStations.put(103.1,"Derti FM");
        radioStations.put(103.15,"Derti FM");
        radioStations.put(103.2,"Derti FM");
        radioStations.put(103.25,"Derti FM");
        radioStations.put(103.3,"Derti FM");
        radioStations.put(103.35,"Derti FM");
        radioStations.put(103.4,"Derti FM");
        radioStations.put(103.45,"Derti FM");
        radioStations.put(103.5,"Derti FM");
        radioStations.put(103.55,"Derti FM");
        radioStations.put(103.6,"Derti FM");
        radioStations.put(103.65,"Derti FM");
        radioStations.put(103.7,"Derti FM");
        radioStations.put(103.75,"Rock Fm 104.7");
        radioStations.put(103.8,"Rock Fm 104.7");
        radioStations.put(103.85,"Rock Fm 104.7");
        radioStations.put(103.9,"Rock Fm 104.7");
        radioStations.put(103.95,"Rock Fm 104.7");
        radioStations.put(104.0,"Rock Fm 104.7");
        radioStations.put(104.05,"Athens Party");
        radioStations.put(104.1,"Athens Party");
        radioStations.put(104.15,"Athens Party");
        radioStations.put(104.2,"Athens Party");
        radioStations.put(104.25,"Athens Party");
        radioStations.put(104.3,"Athens Party");
        radioStations.put(104.35,"Athens Party");
        radioStations.put(104.4,"Athens Party");
        radioStations.put(104.45,"Athens Party");
        radioStations.put(104.5,"Athens Party");
        radioStations.put(104.55,"Athens Party");
        radioStations.put(104.6,"Athens Party");
        radioStations.put(104.65,"Athens Party");
        radioStations.put(104.7,"Athens Party");
        radioStations.put(104.75,"Athens Party");
        radioStations.put(104.8,"Athens Party");
        radioStations.put(104.85,"Athens Party");
        radioStations.put(104.9,"Athens Party");
        radioStations.put(104.95,"Athens Party");
        radioStations.put(105.0,"Athens Party");
        radioStations.put(105.05,"Athens Party");
        radioStations.put(105.1,"Athens Party");
        radioStations.put(105.15,"Athens Party");
        radioStations.put(105.2,"Athens Party");
        radioStations.put(105.25,"Athens Party");
        radioStations.put(105.3,"Athens Party");
        radioStations.put(105.35,"Athens Party");
        radioStations.put(105.4,"Athens Party");
        radioStations.put(105.45,"Athens Party");
        radioStations.put(105.5,"Athens Party");
        radioStations.put(105.55,"Radiofonia 105.8 FM");
        radioStations.put(105.6,"Radiofonia 105.8 FM");
        radioStations.put(105.65,"Radiofonia 105.8 FM");
        radioStations.put(105.7,"Radiofonia 105.8 FM");
        radioStations.put(105.75,"Radiofonia 105.8 FM");
        radioStations.put(105.8,"Radiofonia 105.8 FM");
        radioStations.put(105.85,"NRJ Greece");
        radioStations.put(105.9,"NRJ Greece");
        radioStations.put(105.95,"NRJ Greece");
        radioStations.put(106.0,"NRJ Greece");
        radioStations.put(106.05,"NRJ Greece");
        radioStations.put(106.1,"NRJ Greece");
        radioStations.put(106.15,"NRJ Greece");
        radioStations.put(106.2,"NRJ Greece");
        radioStations.put(106.25,"NRJ Greece");
        radioStations.put(106.3,"NRJ Greece");
        radioStations.put(106.35,"NRJ Greece");
        radioStations.put(106.4,"NRJ Greece");
        radioStations.put(106.45,"NRJ Greece");
        radioStations.put(106.5,"NRJ Greece");
        radioStations.put(106.55,"NRJ Greece");
        radioStations.put(106.6,"NRJ Greece");
        radioStations.put(106.65,"NRJ Greece");
        radioStations.put(106.7,"NRJ Greece");
        radioStations.put(106.75,"Vima FM");
        radioStations.put(106.8,"Vima FM");
        radioStations.put(106.85,"Vima FM");
        radioStations.put(106.9,"Vima FM");
        radioStations.put(106.95,"Vima FM");
        radioStations.put(107.0,"Vima FM");
        radioStations.put(107.05,"Vima FM");
        radioStations.put(107.1,"Vima FM");
        radioStations.put(107.15,"Vima FM");
        radioStations.put(107.2,"Vima FM");
        radioStations.put(107.25,"Vima FM");
        radioStations.put(107.3,"Vima FM");
        radioStations.put(107.35,"Vima FM");
        radioStations.put(107.4,"Vima FM");
        radioStations.put(107.45,"Vima FM");
        radioStations.put(107.5,"Vima FM");
        radioStations.put(107.55,"Vima FM");
        radioStations.put(107.6,"Vima FM");
        radioStations.put(107.65,"Vima FM");
        radioStations.put(107.7,"Vima FM");
        radioStations.put(107.75,"Vima FM");
        radioStations.put(107.8,"Vima FM");
        radioStations.put(107.85,"Vima FM");
        radioStations.put(107.9,"Vima FM");
        radioStations.put(107.95,"Vima FM");
        radioStations.put(108.0,"Vima FM");

    }

    private void goBack(){
        lastStation = Double.valueOf((String) radioFreqText.getText());
        Intent intent = new Intent(this, MainActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("STATIONS",(Serializable) favouriteRadioStations);
        args.putSerializable("LIGHTING",(Serializable) roomsLighting);
        intent.putExtra("HEATINGSETTING",heatingSetting);
        intent.putExtra("HEATINGTEMPERATURE",heatingTemperature);
        intent.putExtra("HEATINGOPEN", heatingOpen);
        intent.putExtra("LASTSTATION", lastStation);
        args.putSerializable("LOCKS",(Serializable) roomLocks);
        args.putSerializable("SCENARIOS",(Serializable) savedScenarios);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    private void startSpeechRecognizer() {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Ποια ενέργεια θέλετε να πραγματοποιήσετε;");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el");
        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"el"});
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNIZER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int REQUEST_SPEECH_RECOGNIZER = 3000;
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH_RECOGNIZER && resultCode == Activity.RESULT_OK && data != null){
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayList<String> list = new ArrayList<>(Arrays.asList(text.get(0).split(" ")));
            text = new ArrayList<>();
            for(int i = 0; i < list.size(); i++) {
                String tmp = list.get(i).toLowerCase();
                
                text.add(tmp);
            }
            
            if(text.get(0).equals("φωτισμός")) {
                if(text.get(1).equals("άναψε") || text.get(1).equals("άνοιξε")) {
                    if(text.get(2).equals("όλα")) {
                        roomsLighting.put("Σαλόνι",true);
                        roomsLighting.put("Κουζίνα",true);
                        roomsLighting.put("Υπνοδωμάτιο",true);
                        roomsLighting.put("Μπάνιο",true);
                    }
                    else {
                        if(text.get(2).equals("σαλόνι") || text.get(2).equals("κουζίνα") || text.get(2).equals("υπνοδωμάτιο") || text.get(2).equals("μπάνιο")) {
                            roomsLighting.put(roomTransalate(text.get(2)),true);
                        }


                    }

                }
                else if(text.get(1).equals("σβήσε") || text.get(1).equals("κλείσε")) {
                    if(text.get(2).equals("όλα")) {
                        roomsLighting.put("Σαλόνι",false);
                        roomsLighting.put("Κουζίνα",false);
                        roomsLighting.put("Υπνοδωμάτιο",false);
                        roomsLighting.put("Μπάνιο",false);
                    }
                    else {
                        if(text.get(2).equals("σαλόνι") || text.get(2).equals("κουζίνα") || text.get(2).equals("υπνοδωμάτιο") || text.get(2).equals("μπάνιο")) {
                            roomsLighting.put(roomTransalate(text.get(2)),false);
                        }
                    }
                }
            }
            else if(text.get(0).equals("ράδιο")) {

                if(isNumeric(text.get(1))) {
                    lastStation = Double.parseDouble(text.get(1));
                    double temp = (lastStation - 87.5) / 0.05;
                    int value = (int) temp;
                    radioFreqSeekBar.setProgress(value);
                    radioFreqText.setText(String.valueOf(lastStation));
                    radioNameText.setText(radioStations.get(lastStation));
                }
                else {
                    if(!favouriteRadioStations.containsKey(Double.parseDouble(text.get(2) ) ) ) {
                        favouriteRadioStations.put(Double.parseDouble(text.get(2)),"");
                        
                    }
                    else {
                        favouriteRadioStations.remove(Double.parseDouble(text.get(2)));
                    }

                }


            }
            else if(text.get(0).equals("θέρμανση")) {
                
                if(text.get(1).equals("άναψε") || text.get(1).equals("άνοιξε")) {
                    heatingOpen = true;
                    if(text.get(2).equals("κρύο")) {
                        heatingSetting = false;
                        heatingTemperature = 15;
                    }
                    else if(text.get(2).equals("ζεστό")) {
                        heatingSetting = true;
                        heatingTemperature = 15;
                    }
                }
                else if(text.get(1).equals("σβήσε") || text.get(1).equals("κλείσε")) {
                    heatingOpen = false;
                }
                else if(text.get(1).equals("κρύο")) {
                    if(isNumeric(text.get(2))) {
                        heatingSetting = false;
                        heatingTemperature = Integer.parseInt(text.get(2));
                        heatingOpen = true;
                    }
                }
                else if(text.get(1).equals("ζεστό")) {
                    if(isNumeric(text.get(2))) {
                        heatingTemperature = Integer.parseInt(text.get(2));
                        heatingSetting = true;
                        heatingOpen = true;
                    }
                }
            }
            else if(text.get(0).equals("συναγερμός")) {

                if(text.get(1).equals("άνοιξε") || text.get(1).equals("ξεκλείδωσε")) {
                    if(text.get(2).equals("κεντρική") || text.get(2).equals("γκαράζ") || text.get(2).equals("μπαλκόνι") || text.get(2).equals("πίσω")) {
                        roomLocks.put(text.get(2),true);
                    }
                    else if(text.get(2).equals("όλες")) {
                        roomLocks.put("κεντρική",true);
                        roomLocks.put("γκαράζ",true);
                        roomLocks.put("μπαλκόνι",true);
                        roomLocks.put("πίσω",true);
                    }
                }
                else if(text.get(1).equals("κλείσε") ||text.get(1).equals("κλείδωσε")) {
                    if(text.get(2).equals("κεντρική") || text.get(2).equals("γκαράζ") || text.get(2).equals("μπαλκόνι") || text.get(2).equals("πίσω")) {
                        roomLocks.put(text.get(2),false);
                    }
                    else if(text.get(2).equals("όλες")) {
                        roomLocks.put("κεντρική",false);
                        roomLocks.put("γκαράζ",false);
                        roomLocks.put("μπαλκόνι",false);
                        roomLocks.put("πίσω",false);
                    }

                }
            }
            else if(text.get(0).equals("σενάρια")) {
                if(text.get(1).equals("αποθήκευσε")) {
                    savedScenarios.add(new Script(favouriteRadioStations,roomsLighting,roomLocks,heatingSetting,heatingOpen,heatingTemperature,lastStation));
                }
            }

        }
    }

    private String roomTransalate(String room) {

        if(room.equals("σαλόνι")) {
            return "Σαλόνι";
        }
        else if (room.equals("κουζίνα")) {
            return  "Κουζίνα";
        }
        else if (room.equals("υπνοδωμάτιο")) {
            return "Υπνοδωμάτιο";
        }
        else if(room.equals("μπάνιο")) {
            return "Μπάνιο";
        }
        return "";
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}