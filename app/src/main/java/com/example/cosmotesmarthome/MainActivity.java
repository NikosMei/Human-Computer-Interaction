package com.example.cosmotesmarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button radioButton,lightButton,heatingButton,alarmButton, scenarioButton;

    private ImageButton micImageButton;

    private HashMap<Double,String> favouriteRadioStations = new HashMap<>();
    private HashMap<String,Boolean> roomsLighting = new HashMap<>();

    private HashMap<String,Boolean> roomLocks = new HashMap<>();
    private boolean heatingSetting; //0 for cold 1 for heat

    private boolean heatingOpen; //0 for closed 1 for open
    private int heatingTemperature;

    private double lastStation;

    private ArrayList<Script> savedScenarios = new ArrayList<>();

    private String mAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        radioButton = (Button) findViewById(R.id.radio);
        lightButton = (Button) findViewById(R.id.lights);
        heatingButton = (Button) findViewById(R.id.heating);
        alarmButton = (Button) findViewById(R.id.alarm);
        scenarioButton = (Button) findViewById(R.id.scripts);


        micImageButton = (ImageButton) findViewById(R.id.micImageButton);

        favouriteRadioStations = new HashMap<>();

        roomsLighting.put("Σαλόνι",false);
        roomsLighting.put("Κουζίνα",false);
        roomsLighting.put("Υπνοδωμάτιο",false);
        roomsLighting.put("Μπάνιο",false);

        roomLocks.put("κεντρική",false);
        roomLocks.put("γκαράζ",false);
        roomLocks.put("μπαλκόνι",false);
        roomLocks.put("πίσω",false);

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityRadio();
            }
        });

        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityLights();
            }
        });

        heatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityHeating();
            }
        });

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityAlarm();
            }
        });

        scenarioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScenarioActivty();
            }
        });

        micImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognizer();
            }
        });

        //Returning from another activity
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

    }

    private void openActivityRadio() {
        Intent intent = new Intent(this, Radio.class);
       Bundle args = new Bundle();
        args.putSerializable("STATIONS",(Serializable) favouriteRadioStations);
        args.putSerializable("LIGHTING",(Serializable) roomsLighting);
        intent.putExtra("HEATINGSETTING",heatingSetting);
        intent.putExtra("HEATINGTEMPERATURE",heatingTemperature);
        intent.putExtra("HEATINGOPEN", heatingOpen);
        intent.putExtra("LASTSTATION", lastStation);
        args.putSerializable("LOCKS",(Serializable) roomLocks);
        args.putSerializable("SCENARIOS",(Serializable) savedScenarios);
        args.putSerializable("LOCKS",(Serializable) roomLocks);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    private void openActivityLights() {
        Intent intent = new Intent(this, Lights.class);
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

    private void openActivityHeating() {
        Intent intent = new Intent(this, Heating.class);
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

    private void openActivityAlarm() {
        Intent intent = new Intent(this, Locks.class);
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

    private void openScenarioActivty() {
        Intent intent = new Intent(this, Scenario.class);
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