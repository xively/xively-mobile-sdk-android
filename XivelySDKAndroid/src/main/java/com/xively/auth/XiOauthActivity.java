package com.xively.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.xively.internal.DependencyInjector;

import java.util.Locale;

/**
 * {@hide}
 *
 * TODO: check if intent scheme callback processing is possible inside the SDK
 */
public class XiOauthActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        if (getIntent() != null) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        processIntent(intent);
    }

    private void processIntent(Intent intent){
        if (intent.getData() != null){
            Uri data = intent.getData();

            if (data.getScheme().toLowerCase(Locale.US).startsWith("xi")){
                String value = data.getEncodedSchemeSpecificPart().substring(2);//skip leading //
                if (!value.equals("")){
                    //TODO: validate token?
                    DependencyInjector.get().provisionWebServices().setBearerAuthorizationHeader(value);
                    DependencyInjector.get().timeSeriesWebServices().setBearerAuthorizationHeader(value);
                    DependencyInjector.get().blueprintWebServices().setBearerAuthorizationHeader(value);
                }
            }
        }
    }
}
