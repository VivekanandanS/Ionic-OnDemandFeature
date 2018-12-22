package org.bionworks.cordova;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Context;

import com.google.android.play.core.listener.StateUpdatedListener;
import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;

/**
 * This class echoes a string called from JavaScript.
 */
public class AppBundle extends CordovaPlugin implements StateUpdatedListener<SplitInstallSessionState> {

    private SplitInstallManager splitInstallManager = null;
    private int mySessionId = 0;
    CallbackContext mycallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            mycallbackContext = callbackContext;
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        splitInstallManager = SplitInstallManagerFactory.create(cordova.getActivity().getWindow().getContext());
        loadFeatureOne();
    }

    public void loadFeatureOne() {
        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule("tokbox").build();
        splitInstallManager.startInstall(request).addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer sessionId) {
                mySessionId =sessionId;
                android.widget.Toast.makeText(cordova.getActivity().getWindow().getContext(), "Success", android.widget.Toast.LENGTH_SHORT).show();
                loadAndLaunchModule();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                android.widget.Toast.makeText(cordova.getActivity().getWindow().getContext(), "Fail", android.widget.Toast.LENGTH_SHORT).show();
                switch (((SplitInstallException) e).getErrorCode()) {
                    case SplitInstallErrorCode.NETWORK_ERROR:
                        // Display a message that requests the user to establish a
                        // network connection.
                        break;
                    case SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED:
                        checkForActiveDownloads();
                        break;
                }
            }


        });
    }


    public void loadAndLaunchModule() {
        if (splitInstallManager.getInstalledModules().contains("tokbox")) {
            // onSuccessfulLoad();
            android.widget.Toast.makeText(cordova.getActivity().getWindow().getContext(), "Avilable", android.widget.Toast.LENGTH_SHORT).show();
            mycallbackContext.success();

        } else {
            android.widget.Toast.makeText(cordova.getActivity().getWindow().getContext(), "Fuck off", android.widget.Toast.LENGTH_SHORT).show();


        }
    }

    void checkForActiveDownloads() {
//        splitInstallManager.getSessionStates( states -> {
//                    // Check for active sessions.
//                    for (SplitInstallSessionState state : states) {
//                        if (state.status() == SplitInstallSessionStatus.DOWNLOADING) {
//                            // Cancel the request, or request a deferred installation.
//                        }
//                    }
//                });

        splitInstallManager.getSessionStates();
    }





    @Override
    public void onStateUpdate(SplitInstallSessionState state) {
        if (state.status() == SplitInstallSessionStatus.FAILED
                ) {
            // Retry the request.
            return;
        }
        if (state.sessionId() == mySessionId) {
            switch (state.status()) {
                case SplitInstallSessionStatus.DOWNLOADING:
                    int totalBytes = Integer.parseInt(""+ state.totalBytesToDownload());
                    int progress =  Integer.parseInt(""+ state.bytesDownloaded());
                    android.widget.Toast.makeText(cordova.getActivity().getWindow().getContext(), ""+state.totalBytesToDownload(), android.widget.Toast.LENGTH_SHORT).show();
                    break;
                case SplitInstallSessionStatus.INSTALLED:
                    break;
            }
        }
    }
}
