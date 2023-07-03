package com.example.cosmotesmarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Lights extends AppCompatActivity {

    SwitchCompat switchButton;
    TextView textViewLight,textKitchen,textLivingR,textBedroom,textBathroom;

    ImageButton kitchenButton,livingRButton,bedroomButton,bathroomButton,backbtn,micButton;
    ImageView kitchenSettings,kitchenImage,kitchenBulb,livingRSettings,livingRImage,livingRBulb,bedroomSettings,bedroomImage,bedroomBulb,bathroomSettings,bathroomImage,bathroomBulb;

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
        setContentView(R.layout.activity_lights);

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

        switchButton = findViewById(R.id.switchButton);
        textViewLight = findViewById(R.id.textAllLights);

        kitchenButton = findViewById(R.id.kitchenButton);
        kitchenSettings = findViewById(R.id.kitchenSettings);
        kitchenImage = findViewById(R.id.kitchenImage);
        kitchenBulb = findViewById(R.id.kitchenBulb);
        textKitchen = findViewById(R.id.textKitchen);

        livingRButton = findViewById(R.id.livingRButton);
        livingRSettings = findViewById(R.id.livingRSettings);
        livingRImage = findViewById(R.id.livingRImage);
        livingRBulb = findViewById(R.id.livingRBulb);
        textLivingR = findViewById(R.id.textLivingR);

        bedroomButton = findViewById(R.id.bedroomButton);
        bedroomSettings = findViewById(R.id.bedroomSettings);
        bedroomImage = findViewById(R.id.bedroomImage);
        bedroomBulb = findViewById(R.id.bedroomBulb);
        textBedroom = findViewById(R.id.textBedroom);

        bathroomButton = findViewById(R.id.bathroomButton);
        bathroomSettings = findViewById(R.id.bathroomSettings);
        bathroomImage = findViewById(R.id.bathroomImage);
        bathroomBulb = findViewById(R.id.bathroomBulb);
        textBathroom = findViewById(R.id.textBathroom);

        kitchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempColor = textKitchen.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & tempColor));
                if (hexColor.equals("#FFFFFF")) {
                    textKitchen.setTextColor(Color.rgb(232, 241, 138));
                    kitchenSettings.setImageResource(R.drawable.room_settings_on);
                    kitchenImage.setImageResource(R.drawable.kitchen_on);
                    kitchenBulb.setImageResource(R.drawable.lightbulb_on);
                    roomsLighting.put("Κουζίνα",true);

                }
                if (hexColor.equals("#E8F18A")) {
                    textKitchen.setTextColor(Color.rgb(255, 255, 255));
                    kitchenSettings.setImageResource(R.drawable.room_settings);
                    kitchenImage.setImageResource(R.drawable.kitchen);
                    kitchenBulb.setImageResource(R.drawable.lightbulb_off);
                    roomsLighting.put("Κουζίνα",false);

                }
                checkIfAllLightsOpen();
            }
        });

        livingRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempColor = textLivingR.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & tempColor));
                if (hexColor.equals("#FFFFFF")) {
                    textLivingR.setTextColor(Color.rgb(232, 241, 138));
                    livingRSettings.setImageResource(R.drawable.room_settings_on);
                    livingRImage.setImageResource(R.drawable.living_room_on);
                    livingRBulb.setImageResource(R.drawable.lightbulb_on);
                    roomsLighting.put("Σαλόνι",true);

                }
                if (hexColor.equals("#E8F18A")) {
                    textLivingR.setTextColor(Color.rgb(255, 255, 255));
                    livingRSettings.setImageResource(R.drawable.room_settings);
                    livingRImage.setImageResource(R.drawable.living_room);
                    livingRBulb.setImageResource(R.drawable.lightbulb_off);
                    roomsLighting.put("Σαλόνι",false);
                }
                checkIfAllLightsOpen();
            }
        });

        bedroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempColor = textBedroom.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & tempColor));
                if (hexColor.equals("#FFFFFF")) {
                    textBedroom.setTextColor(Color.rgb(232, 241, 138));
                    bedroomSettings.setImageResource(R.drawable.room_settings_on);
                    bedroomImage.setImageResource(R.drawable.bedroom_on);
                    bedroomBulb.setImageResource(R.drawable.lightbulb_on);
                    roomsLighting.put("Υπνοδωμάτιο",true);
                }
                if (hexColor.equals("#E8F18A")) {
                    textBedroom.setTextColor(Color.rgb(255, 255, 255));
                    bedroomSettings.setImageResource(R.drawable.room_settings);
                    bedroomImage.setImageResource(R.drawable.bedroom);
                    bedroomBulb.setImageResource(R.drawable.lightbulb_off);
                    roomsLighting.put("Υπνοδωμάτιο",false);
                }
                checkIfAllLightsOpen();
            }
        });

        bathroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempColor = textBathroom.getCurrentTextColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & tempColor));
                if (hexColor.equals("#FFFFFF")) {
                    textBathroom.setTextColor(Color.rgb(232, 241, 138));
                    bathroomSettings.setImageResource(R.drawable.room_settings_on);
                    bathroomImage.setImageResource(R.drawable.bathroom_on);
                    bathroomBulb.setImageResource(R.drawable.lightbulb_on);
                    roomsLighting.put("Μπάνιο",true);

                }
                if (hexColor.equals("#E8F18A")) {
                    textBathroom.setTextColor(Color.rgb(255, 255, 255));
                    bathroomSettings.setImageResource(R.drawable.room_settings);
                    bathroomImage.setImageResource(R.drawable.bathroom);
                    bathroomBulb.setImageResource(R.drawable.lightbulb_off);
                    roomsLighting.put("Μπάνιο",false);
                }
                checkIfAllLightsOpen();
            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!compoundButton.isPressed()) {
                    return;
                }
                if (compoundButton.isChecked()){
                    textViewLight.setTextColor(Color.rgb(232, 241, 138));
                    textKitchen.setTextColor(Color.rgb(232, 241, 138));
                    kitchenSettings.setImageResource(R.drawable.room_settings_on);
                    kitchenImage.setImageResource(R.drawable.kitchen_on);
                    kitchenBulb.setImageResource(R.drawable.lightbulb_on);
                    textLivingR.setTextColor(Color.rgb(232, 241, 138));
                    livingRSettings.setImageResource(R.drawable.room_settings_on);
                    livingRImage.setImageResource(R.drawable.living_room_on);
                    livingRBulb.setImageResource(R.drawable.lightbulb_on);
                    textBedroom.setTextColor(Color.rgb(232, 241, 138));
                    bedroomSettings.setImageResource(R.drawable.room_settings_on);
                    bedroomImage.setImageResource(R.drawable.bedroom_on);
                    bedroomBulb.setImageResource(R.drawable.lightbulb_on);
                    textBathroom.setTextColor(Color.rgb(232, 241, 138));
                    bathroomSettings.setImageResource(R.drawable.room_settings_on);
                    bathroomImage.setImageResource(R.drawable.bathroom_on);
                    bathroomBulb.setImageResource(R.drawable.lightbulb_on);
                    roomsLighting.put("Σαλόνι",true);
                    roomsLighting.put("Κουζίνα",true);
                    roomsLighting.put("Υπνοδωμάτιο",true);
                    roomsLighting.put("Μπάνιο",true);
                }else {
                    textViewLight.setTextColor(Color.rgb(255, 255, 255));
                    textKitchen.setTextColor(Color.rgb(255, 255, 255));
                    kitchenSettings.setImageResource(R.drawable.room_settings);
                    kitchenImage.setImageResource(R.drawable.kitchen);
                    kitchenBulb.setImageResource(R.drawable.lightbulb_off);
                    textLivingR.setTextColor(Color.rgb(255, 255, 255));
                    livingRSettings.setImageResource(R.drawable.room_settings);
                    livingRImage.setImageResource(R.drawable.living_room);
                    livingRBulb.setImageResource(R.drawable.lightbulb_off);
                    textBedroom.setTextColor(Color.rgb(255, 255, 255));
                    bedroomSettings.setImageResource(R.drawable.room_settings);
                    bedroomImage.setImageResource(R.drawable.bedroom);
                    bedroomBulb.setImageResource(R.drawable.lightbulb_off);
                    textBathroom.setTextColor(Color.rgb(255, 255, 255));
                    bathroomSettings.setImageResource(R.drawable.room_settings);
                    bathroomImage.setImageResource(R.drawable.bathroom);
                    bathroomBulb.setImageResource(R.drawable.lightbulb_off);
                    roomsLighting.put("Σαλόνι",false);
                    roomsLighting.put("Κουζίνα",false);
                    roomsLighting.put("Υπνοδωμάτιο",false);
                    roomsLighting.put("Μπάνιο",false);
                }
            }
        });

        Bundle args = intent.getBundleExtra("BUNDLE");
        favouriteRadioStations = (HashMap<Double, String>) args.getSerializable("STATIONS");
        roomsLighting = (HashMap<String, Boolean>) args.getSerializable("LIGHTING");
        heatingSetting = (boolean) intent.getExtras().getSerializable("HEATINGSETTING");
        heatingOpen = (boolean) intent.getExtras().getSerializable("HEATINGOPEN");
        heatingTemperature = (int) intent.getExtras().getSerializable("HEATINGTEMPERATURE");
        lastStation = (double) intent.getExtras().getSerializable("LASTSTATION");
        roomLocks = (HashMap<String, Boolean>) args.getSerializable("LOCKS");
        savedScenarios = (ArrayList<Script>) args.getSerializable("SCENARIOS");
        setLights();


    }

    private void checkIfAllLightsOpen() {
        boolean livingRoom = roomsLighting.get("Σαλόνι");
        boolean bedroom = roomsLighting.get("Υπνοδωμάτιο");
        boolean bathroom= roomsLighting.get("Μπάνιο");
        boolean kitchen = roomsLighting.get("Κουζίνα");

        if(livingRoom && bedroom && bathroom && kitchen) {
            switchButton.setChecked(true);
            textViewLight.setTextColor(Color.rgb(232, 241, 138));
        }
        else {
            switchButton.setChecked(false);
            textViewLight.setTextColor(Color.rgb(255, 255, 255));
        }

    }

    private void setLights() {
        boolean livingRoom = roomsLighting.get("Σαλόνι");
        boolean bedroom = roomsLighting.get("Υπνοδωμάτιο");
        boolean bathroom= roomsLighting.get("Μπάνιο");
        boolean kitchen = roomsLighting.get("Κουζίνα");

        if(livingRoom && bedroom && bathroom && kitchen) {
            switchButton.setChecked(true);
            textViewLight.setTextColor(Color.rgb(232, 241, 138));
        }
        if(livingRoom) {
            textLivingR.setTextColor(Color.rgb(232, 241, 138));
            livingRSettings.setImageResource(R.drawable.room_settings_on);
            livingRImage.setImageResource(R.drawable.living_room_on);
            livingRBulb.setImageResource(R.drawable.lightbulb_on);
        }
        else {
            textLivingR.setTextColor(Color.rgb(255, 255, 255));
            livingRSettings.setImageResource(R.drawable.room_settings);
            livingRImage.setImageResource(R.drawable.living_room);
            livingRBulb.setImageResource(R.drawable.lightbulb_off);
        }

        if(bedroom) {
            textBedroom.setTextColor(Color.rgb(232, 241, 138));
            bedroomSettings.setImageResource(R.drawable.room_settings_on);
            bedroomImage.setImageResource(R.drawable.bedroom_on);
            bedroomBulb.setImageResource(R.drawable.lightbulb_on);
        }
        else {
            textBedroom.setTextColor(Color.rgb(255, 255, 255));
            bedroomSettings.setImageResource(R.drawable.room_settings);
            bedroomImage.setImageResource(R.drawable.bedroom);
            bedroomBulb.setImageResource(R.drawable.lightbulb_off);
        }

        if(bathroom) {
            textBathroom.setTextColor(Color.rgb(232, 241, 138));
            bathroomSettings.setImageResource(R.drawable.room_settings_on);
            bathroomImage.setImageResource(R.drawable.bathroom_on);
            bathroomBulb.setImageResource(R.drawable.lightbulb_on);
        }
        else {
            textBathroom.setTextColor(Color.rgb(255, 255, 255));
            bathroomSettings.setImageResource(R.drawable.room_settings);
            bathroomImage.setImageResource(R.drawable.bathroom);
            bathroomBulb.setImageResource(R.drawable.lightbulb_off);
        }

        if(kitchen) {
            textKitchen.setTextColor(Color.rgb(232, 241, 138));
            kitchenSettings.setImageResource(R.drawable.room_settings_on);
            kitchenImage.setImageResource(R.drawable.kitchen_on);
            kitchenBulb.setImageResource(R.drawable.lightbulb_on);
        }
        else {
            textKitchen.setTextColor(Color.rgb(255, 255, 255));
            kitchenSettings.setImageResource(R.drawable.room_settings);
            kitchenImage.setImageResource(R.drawable.kitchen);
            kitchenBulb.setImageResource(R.drawable.lightbulb_off);
        }
    }

    private void goBack(){
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
            setLights();
            checkIfAllLightsOpen();
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