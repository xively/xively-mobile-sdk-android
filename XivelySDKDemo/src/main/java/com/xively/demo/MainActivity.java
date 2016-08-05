package com.xively.demo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.xively.demo.R;
import com.xively.XiSdkConfig;
import com.xively.XiSession;
import com.xively.demo.fragments.DevicesFragment;
import com.xively.demo.fragments.MessagingFragment;
import com.xively.demo.fragments.OnFragmentInteractionListener;
import com.xively.demo.fragments.SettingsFragment;
import com.xively.demo.fragments.TimeSeriesFragment;
import com.xively.auth.XiAuthentication;
import com.xively.auth.XiAuthenticationCallback;
import com.xively.auth.XiAuthenticationFactory;
import com.xively.messaging.XiDeviceInfo;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener {

    /**
     * **************************************************
     * Important!
     *
     * Insert your account Id here before using the app.
     * **************************************************
     */
    private String xivelyAccountId =
            "ec274e02-a279-4faf-8901-ed503d80c55c";

    private enum UiState {unknown, login, DevicesFragment, MessagingFragment, TimeSeriesFragment, SettingsFragment}
    private UiState currentUiState = UiState.login;

    private XiSession xivelySession;

    private AlertDialog alertDialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("UiState", currentUiState.name());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (!isXivelyActive() || savedInstanceState == null){
            xivelyLogin();
        } else
        if (savedInstanceState.containsKey("UiState")){
            String savedStateString = savedInstanceState.getString("UiState");
            UiState savedState = UiState.unknown;
            try {
                savedState = UiState.valueOf(savedStateString);
            } catch (IllegalArgumentException ignored){}

            switch (savedState){

                case login:
                    xivelyLogin();
                    break;
                case DevicesFragment:
                    changeFragment(new DevicesFragment(), false);
                    break;
                case MessagingFragment:
                    changeFragment(new MessagingFragment(), false);
                    break;
                case TimeSeriesFragment:
                    changeFragment(new TimeSeriesFragment(), false);
                    break;
                case SettingsFragment:
                    changeFragment(new SettingsFragment(), false);
                    break;
                default:
                    xivelyLogin();
            }
            super.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            changeFragment(new SettingsFragment(), true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            xivelyLogin();
        } else if (id == R.id.nav_device_list) {
            if (isXivelyActive()){
                changeFragment(new DevicesFragment(), false);
            } else {
                showAlert("Error", "You must login to use this feature.", null);
            }

        } else if (id == R.id.nav_messaging) {
            if (isXivelyActive()){
                changeFragment(new MessagingFragment(), false);
            } else {
                showAlert("Error", "You must login to use this feature.", null);
            }

        } else if (id == R.id.nav_timeseries) {
            if (isXivelyActive()){
                changeFragment(new TimeSeriesFragment(), false);
            } else {
                showAlert("Error", "You must login to use this feature.", null);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //***** Devices Fragment callback listeners
    @Override
    public void onDialogRequest(String title, String message) {
        showAlert(title, message, null);
    }

    @Override
    public void onDialogRequest(String title, String message, DialogInterface.OnClickListener onClickListener) {
        showAlert(title, message, onClickListener);
    }

    @Override
    public void onMessagingRequest(XiDeviceInfo device) {
        changeFragment(MessagingFragment.newInstance(device), false);
    }

    @Override
    public void onTimeSeriesRequest(XiDeviceInfo device) {
        changeFragment(TimeSeriesFragment.newInstance(device), false);
    }

    @Override
    public void onDevicesRequest() {
        changeFragment(new DevicesFragment(), false);
    }

    //***** End of Devices Fragment callback listeners


    public XiSession getXivelySession(){
        return xivelySession;
    }

    private void xivelyLogin(){
        requestLogin(new LoginDialogResult() {
            @Override
            public void onSubmitCredentials(String userName, String password) {
                progressBar.setVisibility(View.VISIBLE);

                XiSdkConfig customConfig = new XiSdkConfig();

                String envValue = XiSettings.getString(XiSettings.PREF_SDK_ENVIRONMENT, null);
                if (envValue != null) {
                    try {
                        XiSdkConfig.XI_ENVIRONMENT env = XiSdkConfig.XI_ENVIRONMENT.valueOf(envValue);
                        customConfig.environment = env;
                    } catch (Exception ignored){}
                }

                String customLogLevelValue = XiSettings.getString(XiSettings.PREF_LOG_LEVEL, null);
                if (customLogLevelValue != null){
                    try {
                        XiSdkConfig.LogLevel customLogLevel = XiSdkConfig.LogLevel.valueOf(customLogLevelValue);
                        customConfig.logLevel = customLogLevel;
                    } catch (Exception ignored){}

                }

                XiAuthentication xiAuthentication =
                        XiAuthenticationFactory.createAuthenticationService(getApplicationContext(), customConfig);

                xiAuthentication.requestAuth(userName, password,
                        xivelyAccountId, new XiAuthenticationCallback() {
                            @Override
                            public void sessionCreated(XiSession xiSession) {
                                progressBar.setVisibility(View.GONE);
                                xivelySession = xiSession;
                                changeFragment(new DevicesFragment(), false);
                            }

                            @Override
                            public void authenticationFailed(XiAuthenticationError xiAuthenticationError) {
                                progressBar.setVisibility(View.GONE);

                                //TODO: add your localized error message based on the error type
                                showAlert("Login failed", xiAuthenticationError.toString(),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                xivelyLogin();
                                            }
                                        });
                            }
                        });
            }

            @Override
            public void onCanceled() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void requestLogin(final LoginDialogResult loginDialogResult){

        if (alertDialog != null &&
                alertDialog.isShowing()){
            alertDialog.dismiss();
            alertDialog = null;
        }

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.login_dialog, null, false);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.loginUserName);

        final EditText userInput2 = (EditText) promptsView
                .findViewById(R.id.loginPassword);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sign in",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                loginDialogResult.onSubmitCredentials(
                                        userInput.getText().toString(),
                                        userInput2.getText().toString());

                                //clear credentials from ui elements
                                userInput.setText("");
                                userInput2.setText("");
                            }
                        })
                .setNeutralButton("Settings",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                changeFragment(new SettingsFragment(), false);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                loginDialogResult.onCanceled();

                            }
                        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        currentUiState = UiState.login;
    }

    private interface LoginDialogResult {
        void onSubmitCredentials(String userName, String password);

        void onCanceled();
    }

    private boolean isXivelyActive() {
        return xivelySession != null && xivelySession.getState().equals(XiSession.State.Active);
    }

    private void showAlert(String title, String message, DialogInterface.OnClickListener onClickListener){
        if (isFinishing()){
            return;
        }

        if (alertDialog != null &&
                alertDialog.isShowing()){
            alertDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title)
                .setCancelable(false)
                .setPositiveButton("Ok", onClickListener)
                .setMessage(message);

        alertDialog = builder.create();
        alertDialog.show();
    }


    private void changeFragment(Fragment fragment, boolean addToBackStack){
        changeFragment(fragment, addToBackStack, true);
    }

    private void changeFragment(Fragment fragment, boolean addToBackStack, boolean enableCustomAnim){
        //TODO: Set active state in Drawer menu

        if (!getActiveFragmentName().equals(fragment.getClass().getName())) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (enableCustomAnim) {
                ft.setCustomAnimations(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
            }
            ft.replace(R.id.content_main, fragment);
            if (addToBackStack) {
                ft.addToBackStack(fragment.getClass().getName());
            }
            ft.commit();
            try {
                currentUiState = UiState.valueOf(fragment.getClass().getSimpleName());
            } catch (IllegalArgumentException ex){
                ex.printStackTrace();
            }
        }
    }

    private String getActiveFragmentName() {
        Fragment fragment = getActiveFragmentObject();
        String tag = "";
        if (fragment != null){
            tag = fragment.getClass().getName();
        }

        return tag;
    }

    private Fragment getActiveFragmentObject() {

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null){
            return null;
        }

        for(Fragment fragment : fragments){
            if (fragment != null && fragment.isVisible()){
                return fragment;
            }
        }

        return null;
    }
}
