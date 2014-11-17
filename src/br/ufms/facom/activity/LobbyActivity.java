package br.ufms.facom.activity;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;
import br.ufms.facom.truco.R;

public class LobbyActivity extends Activity {

	private Animation anim;
	private BluetoothHelper btHelper;
	private BluetoothServerSocket btServerSocket;
	private TextView txtDeviceName;
	private TextView txtOpponentDeviceName;
	private TextView txtWaitingPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lobby);
		
		init();
		
		btInit();
		
		setAnimation();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_CANCELED)
		{
			Toast.makeText(LobbyActivity.this, "O Bluetooth precisa estar ligado para jogar.", Toast.LENGTH_LONG).show();
			finish();
			Log.i(getClass().getName(), "Error / User did not accept to turn Bluetooth on.");
		}
		else
		{
			btServerSocket = BluetoothHelper.getBluetoothServerSocket();
			
			AsyncTask<Void, Void, Boolean> listenTask = new AsyncTask<Void, Void, Boolean>()
			{
				BluetoothSocket btSocket;
				
				@Override
				protected void onPreExecute() 
				{
					super.onPreExecute();
					txtDeviceName.startAnimation(anim);
					txtOpponentDeviceName.startAnimation(anim);
					txtWaitingPlayer.startAnimation(anim);
				}

				@Override
				protected Boolean doInBackground(Void... params) 
				{
					try {
						btSocket = btServerSocket.accept(60000);
						BluetoothHelper.setBtSocket(btSocket);
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
						Intent hostGameIntent = new Intent(LobbyActivity.this, HostGameActivity.class);
						startActivity(hostGameIntent);
					}
					else
					{
						Toast.makeText(LobbyActivity.this, "Nenhuma conexão feita, tente novamente!", Toast.LENGTH_LONG).show();
						finish();
					}
				}
			};
			
			listenTask.execute();
		}
	}
	
	@Override
	protected void onRestart() {
		finish();
		super.onRestart();
	}
	
	private void init() 
	{
		txtDeviceName = (TextView) findViewById(R.id.txtDeviceName);
		txtOpponentDeviceName = (TextView) findViewById(R.id.txtOpponentDeviceName);
		txtWaitingPlayer = (TextView) findViewById(R.id.txtWaitingPlayer);
	}

	private void btInit() 
	{
		btHelper = new BluetoothHelper(LobbyActivity.this);
		
		if (BluetoothHelper.getBtAdapter() == null)
        {
        	Toast.makeText(LobbyActivity.this, "O dispositivo não suporta conexões Bluetooth. Não será possível jogar!", Toast.LENGTH_LONG).show();
        	Log.i(getClass().getName(), "Device does not support Bluetooth");
        	finish();
        }
        else
        {
        	Log.i(getClass().getName(), "Device supports Bluetooth");
        	btHelper.makeItVisible();
        	txtDeviceName.setText(BluetoothHelper.getBtAdapter().getName().toString());
        }
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
