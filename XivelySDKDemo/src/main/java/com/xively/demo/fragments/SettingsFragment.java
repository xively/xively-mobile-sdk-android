package com.xively.demo.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.xively.XiSdkConfig;
import com.xively.demo.MainActivity;
import com.xively.demo.R;
import com.xively.demo.XiSettings;

import static com.xively.XiSdkConfig.XI_ENVIRONMENT;
import static com.xively.demo.XiSettings.PREF_CLEAN_SESSION;
import static com.xively.demo.XiSettings.PREF_LAST_WILL_CHANNEL;
import static com.xively.demo.XiSettings.PREF_LAST_WILL_MESSAGE;
import static com.xively.demo.XiSettings.PREF_LAST_WILL_QOS;
import static com.xively.demo.XiSettings.PREF_LAST_WILL_RETAIN;

public class SettingsFragment extends Fragment {
    private CheckBox cleanSession;
    private EditText lastWillChannel;
    private EditText lastWillMessage;
    private RadioButton lastWillQoS0Button;
    private RadioButton lastWillQoS1Button;
    private CheckBox lastWillRetain;
    private Button cancelButton;
    private Button saveButton;

    private String[] logLevels = new String[]{
            XiSdkConfig.LogLevel.OFF.toString(),
            XiSdkConfig.LogLevel.ERROR.toString(),
            XiSdkConfig.LogLevel.WARNING.toString(),
            XiSdkConfig.LogLevel.INFO.toString(),
            XiSdkConfig.LogLevel.DEBUG.toString(),
            XiSdkConfig.LogLevel.TRACE.toString()
    };

    private final String[] sdkEnvValues =
            new String[]{
                    XI_ENVIRONMENT.DEV.toString(),
                    XI_ENVIRONMENT.STAGE.toString(),
                    XI_ENVIRONMENT.DEPLOY.toString(),
                    XI_ENVIRONMENT.DEMO.toString(),
                    XI_ENVIRONMENT.LIVE.toString()
            };

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cleanSession = (CheckBox) view.findViewById(R.id.Settings_checkBox_CleanSession);
        lastWillChannel = (EditText) view.findViewById(R.id.Settings_editText_LastWillChannel);
        lastWillMessage = (EditText) view.findViewById(R.id.Settings_editText_LastWillMessage);
        lastWillQoS0Button = (RadioButton) view.findViewById(R.id.Settings_radioButtonQoS0);
        lastWillQoS1Button = (RadioButton) view.findViewById(R.id.Settings_radioButtonQoS1);
        lastWillRetain = (CheckBox) view.findViewById(R.id.Settings_checkBoxRetain);
        cancelButton = (Button) view.findViewById(R.id.Settings_buttonCancel);
        saveButton = (Button) view.findViewById(R.id.Settings_buttonSave);

        lastWillQoS0Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lastWillQoS1Button.setChecked(false);
                }
            }
        });

        lastWillQoS1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lastWillQoS0Button.setChecked(false);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        loadSettings();
    }

    private void loadSettings(){

        cleanSession.setChecked(
                XiSettings.getBoolean(PREF_CLEAN_SESSION, true)
        );

        lastWillChannel.setText(
                XiSettings.getString(PREF_LAST_WILL_CHANNEL, "")
        );

        lastWillMessage.setText(
                XiSettings.getString(PREF_LAST_WILL_MESSAGE, "")
        );

        int qos = XiSettings.getInt(PREF_LAST_WILL_QOS, 0);
        if (qos == 0){
            lastWillQoS0Button.setChecked(true);
            lastWillQoS1Button.setChecked(false);
        } else {
            lastWillQoS0Button.setChecked(false);
            lastWillQoS1Button.setChecked(true);
        }

        lastWillRetain.setChecked(
                XiSettings.getBoolean(XiSettings.PREF_LAST_WILL_RETAIN, false)
        );
    }

    private void saveSettings(){
        XiSettings.setBoolean(PREF_CLEAN_SESSION,
                cleanSession.isChecked());
        XiSettings.setString(PREF_LAST_WILL_CHANNEL,
                lastWillChannel.getText().toString());
        XiSettings.setString(PREF_LAST_WILL_MESSAGE,
                lastWillMessage.getText().toString());
        int qos = 0;
        if (lastWillQoS1Button.isChecked()){
            qos = 1;
        }
        XiSettings.setInt(PREF_LAST_WILL_QOS, qos);
        XiSettings.setBoolean(PREF_LAST_WILL_RETAIN,
                lastWillRetain.isChecked());

        ((MainActivity) getActivity()).onDialogRequest("Settings saved",
                "Application settings saved.\n" +
                        "General settings are applied on application restart.\n" +
                        "Connection and last will settings are effective on reload of the Messaging screen.",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                });
    }

}
