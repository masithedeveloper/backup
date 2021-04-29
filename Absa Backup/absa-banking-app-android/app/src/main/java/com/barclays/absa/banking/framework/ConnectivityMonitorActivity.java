/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.barclays.absa.banking.R;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.presentation.connectivity.NoConnectivityActivity;

import static android.net.ConnectivityManager.EXTRA_IS_FAILOVER;
import static android.net.ConnectivityManager.EXTRA_NO_CONNECTIVITY;

public class ConnectivityMonitorActivity extends BaseActivity {

    private ConnectivityBroadcastReceiver networkConnectivityBroadcastReceiver;
    private boolean isBroadcastReceivedRegistered;

    @Override
    protected void onResume() {
        super.onResume();
        if (getAppCacheService().isPrimarySecondFactorDevice()) {
            createNetworkConnectivityBroadcastReceiver();
            if (BMBApplication.getInstance().isInForeground() && BMBApplication.getInstance().isNotListeningForAuth()) {
                showEntersektError();
                BMBApplication.getInstance().listenForAuth();
            }
        }
    }

    private void createNetworkConnectivityBroadcastReceiver() {
        if (!isBroadcastReceivedRegistered) {
            networkConnectivityBroadcastReceiver = new ConnectivityBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            networkConnectivityBroadcastReceiver.register(this, intentFilter);
        }
    }

    public void showEntersektError() {
        Intent intent = new Intent(this, NoConnectivityActivity.class);
        intent.putExtra(NoConnectivityActivity.INSTRUCTION, getString(R.string.connectivity_entersekt_instruction));
        intent.putExtra(NoConnectivityActivity.MESSAGE, getString(R.string.connectivity_entersekt_instruction));
        intent.putExtra(NoConnectivityActivity.CONNECTIVITY_TYPE, NoConnectivityActivity.CONNECTIVITY_ENTERSEKT);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        if (networkConnectivityBroadcastReceiver != null && isBroadcastReceivedRegistered) {
            networkConnectivityBroadcastReceiver.unregister(this);
        }
        super.onPause();
    }

    class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        public void register(Context context, IntentFilter intentFilter) {
            if (!isBroadcastReceivedRegistered && context != null) {
                context.registerReceiver(this, intentFilter);
                isBroadcastReceivedRegistered = true;
            }
        }

        void unregister(Context context) {
            if (isBroadcastReceivedRegistered) {
                context.unregisterReceiver(this);
                isBroadcastReceivedRegistered = false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                if (extras.getBoolean(EXTRA_IS_FAILOVER)) {
                    //this means your network has changed for whatever reason. We should probably have someone listening to this
                } else if (extras.getBoolean(EXTRA_NO_CONNECTIVITY)) {
                    //this means network has completely dropped
                    toastLong("Network connection dropped");
                    showEntersektError();
                }
            }
        }

    }

}