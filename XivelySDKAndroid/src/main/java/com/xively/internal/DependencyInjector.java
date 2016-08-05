package com.xively.internal;

import android.content.Context;

import com.xively.auth.XiAuthentication;
import com.xively.internal.auth.XiAuthenticationImpl;
import com.xively.internal.connection.impl.XiMqttConnectionPool;
import com.xively.internal.device.DeviceInfo;
import com.xively.internal.device.impl.DeviceInfoImpl;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.rest.access.AccessWebServices;
import com.xively.internal.rest.auth.AuthWebServices;
import com.xively.internal.rest.blueprint.BlueprintWebServices;
import com.xively.internal.rest.provision.ProvisionWebServices;
import com.xively.internal.rest.timeseries.TimeSeriesWebServices;
import com.xively.internal.util.AsyncTimerTask;
import com.xively.messaging.XiMessaging;
import com.xively.internal.messaging.XiMessagingImpl;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class DependencyInjector {

	private static DependencyInjector instance;
	
	private Context context;
	private AccessWebServices accessWebServicesInstance;
    private ProvisionWebServices provisionWebServicesInstance;
	private AuthWebServices authWebServicesInstance;
	private BlueprintWebServices blueprintWebServicesInstance;
	private TimeSeriesWebServices timeSeriesWebServicesInstance;
	private DeviceInfo deviceInfoInstance;
	private String authorizationHeader;

	public static DependencyInjector get(){
		if (instance == null){
			instance = new DependencyInjector();
		}
		
		return instance;
	}

    //only for unit testing purposes
    public static void setInstance(DependencyInjector injectorInstance){
        instance = injectorInstance;
    }

	public void setAuthorizationHeader(String header){
		authorizationHeader = header;
	}

	public void initDependencies(Context context){
		this.context = context;
	}
	
	public Context getContext(){
		return context;
	}

    public ProvisionWebServices provisionWebServices(){
        if (provisionWebServicesInstance == null){
            provisionWebServicesInstance = new ProvisionWebServices();
        }

        return provisionWebServicesInstance;
    }

	public TimeSeriesWebServices timeSeriesWebServices(){
		if (timeSeriesWebServicesInstance == null){
			timeSeriesWebServicesInstance = new TimeSeriesWebServices();
		}

		return timeSeriesWebServicesInstance;
	}

	public AccessWebServices accessWebServices(){
		if (accessWebServicesInstance == null){
			accessWebServicesInstance = new AccessWebServices();
		}

		return accessWebServicesInstance;
	}

	public AuthWebServices authWebServices(){
		if (authWebServicesInstance == null){
			authWebServicesInstance = new AuthWebServices();
		}

		return authWebServicesInstance;
	}

    public BlueprintWebServices blueprintWebServices(){
        if (blueprintWebServicesInstance == null){
            blueprintWebServicesInstance = new BlueprintWebServices();
        }

        return blueprintWebServicesInstance;
    }

	public DeviceInfo deviceInfo(){
		if (deviceInfoInstance == null){
			deviceInfoInstance = new DeviceInfoImpl();
		}
		
		return deviceInfoInstance;
	}

	public XiMessaging createXiMessaging(XiMqttConnectionPool connectionPool){
		return new XiMessagingImpl(connectionPool,authorizationHeader);
	}

	public XiMqttConnectionPool createMqttConnectionPool(XivelyAccount account){
		return new XiMqttConnectionPool(account);
	}

    public MqttAndroidClient createMqttAndroidClient(Context context, String serverUri, String clientId){
        return new MqttAndroidClient(context, serverUri, clientId);
    }

	public AsyncTimerTask createAsyncTimerTask(long timerMillis, Runnable onPostExecute)
            throws IllegalThreadStateException {
		return new AsyncTimerTask(timerMillis, onPostExecute);
	}

    public XiAuthentication createXiOAuth(){
        return new XiAuthenticationImpl();
    }

	public void resetInstances(){
        accessWebServicesInstance = null;
        provisionWebServicesInstance = null;
        authWebServicesInstance = null;
        blueprintWebServicesInstance = null;
        timeSeriesWebServicesInstance = null;
        deviceInfoInstance = null;
	}
}
