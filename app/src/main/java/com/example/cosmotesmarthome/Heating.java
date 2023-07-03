package com.example.cosmotesmarthome;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import me.tankery.lib.circularseekbar.CircularSeekBar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Heating extends AppCompatActivity {
    int cold;
    boolean cold_flag,warm_flag;
    ImageButton coldbtn,warmbtn,upbtn,downbtn,backbtn,micButton;
    ImageView cold_fan;
    TextView warm_temperature,cold_temperature,air_con_text;

    CircularSeekBar circleHeat;

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
        setContentView(R.layout.activity_heating);

        int warm_max = 30;
        cold = 15;
        cold_flag = false;

        micButton = (ImageButton) findViewById(R.id.micImageButton);

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognizer();
            }
        });

        Intent intent = getIntent();

        backbtn = (ImageButton) findViewById(R.id.radioBackButton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        coldbtn = (ImageButton) findViewById(R.id.coldbtn);
        warmbtn = (ImageButton) findViewById(R.id.sunbtn);
        circleHeat = findViewById(R.id.circle_heat);
        circleHeat.setMax(warm_max);
        circleHeat.setProgress(15);
        warm_temperature = (TextView) findViewById(R.id.temperature);
        cold_temperature = (TextView) findViewById(R.id.cold_temperature);
        cold_fan = (ImageView) findViewById(R.id.cold_fan);
        upbtn = (ImageButton) findViewById(R.id.arrow_up);
        downbtn = (ImageButton) findViewById(R.id.arrow_down);
        air_con_text = (TextView) findViewById(R.id.aircondition);

        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cold += 1;
                cold_temperature.setText(cold + "°C");
            }
        });

        downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cold -= 1;
                cold_temperature.setText(cold + "°C");
            }
        });



        circleHeat.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }

            @Override
            public void onStartTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }

            @Override
            public void onProgressChanged(@Nullable CircularSeekBar circularSeekBar, float v, boolean b) {
                int temp = (int) circleHeat.getProgress();
                warm_temperature.setText(temp + "°C");
            }
        });

        coldbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cold_flag & !warm_flag) {
                    coldbtn.setBackgroundResource(R.drawable.circle1);
                    cold_flag = true;
                    cold_fan.setVisibility(View.VISIBLE);
                    cold_temperature.setVisibility(View.VISIBLE);
                    air_con_text.setVisibility(View.VISIBLE);
                    cold_fan.setVisibility(View.VISIBLE);
                    upbtn.setVisibility(View.VISIBLE);
                    downbtn.setVisibility(View.VISIBLE);
                    heatingOpen = true;
                    heatingSetting = false;

                } else {
                    coldbtn.setBackgroundColor(Color.parseColor("#092E49"));
                    cold_flag = false;
                    cold_fan.setVisibility(View.INVISIBLE);
                    cold_temperature.setVisibility(View.INVISIBLE);
                    air_con_text.setVisibility(View.INVISIBLE);
                    cold_fan.setVisibility(View.INVISIBLE);
                    upbtn.setVisibility(View.INVISIBLE);
                    downbtn.setVisibility(View.INVISIBLE);
                    heatingOpen = false;
                }
            }
        });

        warmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!warm_flag & !cold_flag) {
                    warmbtn.setBackgroundResource(R.drawable.circle1);
                    warm_flag = true;
                    circleHeat.setVisibility(View.VISIBLE);
                    warm_temperature.setVisibility(View.VISIBLE);
                    heatingOpen = true;
                    heatingSetting = true;
                } else if (warm_flag & !cold_flag) {
                    warmbtn.setBackgroundColor(Color.parseColor("#092E49"));
                    warm_flag = false;
                    circleHeat.setVisibility(View.INVISIBLE);
                    warm_temperature.setVisibility(View.INVISIBLE);
                    heatingOpen = false;
                }
            }
        });

        Bundle args = intent.getBundleExtra("BUNDLE");
        favouriteRadioStations = (HashMap<Double, String>) args.getSerializable("STATIONS");
        roomsLighting = (HashMap<String, Boolean>) args.getSerializable("LIGHTING");
        heatingSetting = (boolean) intent.getExtras().getSerializable("HEATINGSETTING");
        heatingTemperature = (int) intent.getExtras().getSerializable("HEATINGTEMPERATURE");
        heatingOpen = (boolean) intent.getExtras().getSerializable("HEATINGOPEN");
        lastStation = (double) intent.getExtras().getSerializable("LASTSTATION");
        roomLocks = (HashMap<String,Boolean>) args.getSerializable("LOCKS");
        savedScenarios = (ArrayList<Script>) args.getSerializable("SCENARIOS");
        lastSetting();

    }

    private void lastSetting() {
        if(heatingTemperature == 0) {
            heatingTemperature = 15;
        }
        if(heatingOpen) {
            if(heatingSetting) {
                warmbtn.performClick();
            }
            else {
                coldbtn.performClick();
            }
        }
        else {
            if(warm_flag) {
                warmbtn.performClick();
            }
            else if(cold_flag) {
                coldbtn.performClick();
            }
        }

        if(heatingSetting) {
            circleHeat.setProgress((float) heatingTemperature);
            warm_temperature.setText((int) circleHeat.getProgress() + "°C");
        }
        else {
            cold = heatingTemperature;
            cold_temperature.setText(cold + "°C");
        }

    }

    private void goBack(){
        if(heatingSetting) {
            heatingTemperature = (int) circleHeat.getProgress();
            
        }
        else {
            
            heatingTemperature = cold;
        }
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
            for(int i =0; i < text.size(); i++) {
                
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

            lastSetting();
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