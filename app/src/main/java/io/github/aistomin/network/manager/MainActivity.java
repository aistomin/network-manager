package io.github.aistomin.network.manager;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends ListActivity {

    private WifiManager mainWifi;

    private WifiScanReceiver wifiReciever;

    private ListView list;

    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = getListView();
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!mainWifi.isWifiEnabled()) {
            Toast.makeText(MainActivity.this, "WIFI is not enabled. Let's enable it.", Toast.LENGTH_LONG).show();
            mainWifi.setWifiEnabled(true);
        }
        wifiReciever = new WifiScanReceiver();
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mainWifi.startScan();
        // listening to single list item on click
        list.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // selected item
                    String ssid = ((TextView) view).getText().toString();
                    connectToWifi(ssid);
                    Toast.makeText(MainActivity.this, "Wifi SSID : " + ssid, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }

    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(final Context c, final Intent intent) {
            final List<ScanResult> results = mainWifi.getScanResults();
            final Set<String> filtered = new HashSet<>();
            for (final ScanResult result : results) {
                filtered.add(result.toString().split(",")[0].substring(5).trim());
            }
            list.setAdapter(
                new ArrayAdapter<>(
                    getApplicationContext(), R.layout.list_item, R.id.label,
                    filtered.toArray()
                )
            );
        }
    }

    private void finallyConnect(String networkPass, String networkSSID) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        // remember id
        int netId = mainWifi.addNetwork(wifiConfig);
        mainWifi.disconnect();
        mainWifi.enableNetwork(netId, true);
        mainWifi.reconnect();
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        mainWifi.addNetwork(conf);
    }

    private void connectToWifi(final String wifiSSID) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.connect);
        dialog.setTitle("Connect to Network");
        Button dialogButton = dialog.findViewById(R.id.okButton);
        pass = dialog.findViewById(R.id.textPassword);
        dialog.<TextView>findViewById(R.id.textSSID1).setText(wifiSSID);
        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();
                finallyConnect(checkPassword, wifiSSID);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
