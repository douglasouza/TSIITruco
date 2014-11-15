package br.ufms.facom.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;
import br.ufms.facom.truco.R;

public class JoinGameActivity extends Activity {

	private Animation anim;
	private ArrayList<String> discoveryArrayList;
	private ArrayAdapter<String> discoveryArrayAdapter;
	private ArrayList<BluetoothDevice> btDeviceList;
	private BluetoothHelper btHelper;
	private BroadcastReceiver broadcastReceiver;
	private TextView txtSearchingDevices;
	private IntentFilter filter;
	private ListView discoveryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		
		txtSearchingDevices = (TextView) findViewById(R.id.txtSearchingDevices);
		
		btInit();
		
		setAnimation();
		
		Log.i(getClass().getName(), "Starting dicovery");
		btHelper.enableBt();
		BluetoothHelper.getBtAdapter().startDiscovery();
		broadcastReceiver = new BroadcastReceiver() 
		{
            public void onReceive(Context context, Intent intent) 
            {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) 
                {   	
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(getClass().getName(), "Discovered Device: " + device.getName() + "\n" + device.getAddress());
                    btDeviceList.add(device);
                    discoveryArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    discoveryList.setAdapter(discoveryArrayAdapter);
                }
            }
        };
        // Register the BroadcastReceiver
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, filter);
	}
	
	@Override
	protected void onRestart() {
		finish();
		super.onRestart();
	}
	
	@Override
	protected void onDestroy() 
	{
		Log.i(getClass().getName(), "Unregistering receiver and canceling search");
		unregisterReceiver(broadcastReceiver);
		BluetoothHelper.getBtAdapter().cancelDiscovery();
		super.onDestroy();
	}
	
	private void btInit()
	{
		btHelper = new BluetoothHelper(JoinGameActivity.this);
		
		btDeviceList = new ArrayList<BluetoothDevice>();
		
		discoveryList = (ListView) findViewById(R.id.list);
		
		discoveryList.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				final BluetoothDevice btDevice = btDeviceList.get(position);
				
				Builder waitDialog = new Builder(JoinGameActivity.this);
				waitDialog.setTitle("Aguarde...");
				waitDialog.show();
				
				AsyncTask<Void, Void, Boolean> connectTask = new AsyncTask<Void, Void, Boolean>()
				{
					@Override
					protected void onPreExecute() {
						txtSearchingDevices.setAnimation(anim);
						super.onPreExecute();
					}
					
					@Override
					protected Boolean doInBackground(Void... params) {
						try {
							BluetoothSocket socket = btDevice.createInsecureRfcommSocketToServiceRecord(BluetoothHelper.getUuid());
							socket.connect();
							BluetoothHelper.setBtSocket(socket);
						} catch (IOException e) {
							Log.i(getClass().getName(), e.getMessage().toString());
							return false;
						}
						return true;
					}
					
					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if (result == true)
						{
							BluetoothHelper.getBtAdapter().cancelDiscovery();
							Intent clientGameIntent = new Intent(JoinGameActivity.this, ClientGameActivity.class);
							startActivity(clientGameIntent);
						}
						else
						{
							BluetoothHelper.getBtAdapter().cancelDiscovery();
							Toast.makeText(JoinGameActivity.this, "Falha na conexão, tente novamente!", Toast.LENGTH_LONG).show();
							finish();
						}
					}			
				};
				
				connectTask.execute();
			}
		});
	        
        discoveryArrayList = new ArrayList<String>();
        
        discoveryArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, discoveryArrayList);
	}
	
	private void setAnimation() 
	{
		anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(600);
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
	}
}
