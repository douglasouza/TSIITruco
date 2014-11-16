package br.ufms.facom.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

public class BluetoothHelper {
	
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_DISCOVERABLE_BT = 1;
	private static final String UUID_STRING = "a60f35f0-b93a-11de-8a39-08002009c666";
	
	private static BluetoothServerSocket btServerSocket;
	private static BluetoothSocket btSocket;
	private static UUID uuid = UUID.fromString(UUID_STRING);
	
	private static BluetoothAdapter btAdapter;
	
	private static Activity activity;
	
	public BluetoothHelper(Activity act)
	{
		activity = act;
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public static BluetoothAdapter getBtAdapter() 
	{
		return btAdapter;
	}
	
	public static BluetoothServerSocket getBluetoothServerSocket()
	{
		if (btServerSocket == null)
		{
			try 
			{
				btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("btServer", uuid);
			} catch (IOException e) { }
		}
		
		return btServerSocket;
	}
	
	public static void setBtSocket(BluetoothSocket bluetoothSocket)
	{
		btSocket = bluetoothSocket;
	}
	
	public static BluetoothSocket getBtSocket()
	{
		return btSocket;
	}
	
	public static UUID getUuid()
	{
		return uuid;
	}

	public void enableBt()
	{
		if (!btAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
	}
	
	public void makeItVisible()
	{
		 Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	     discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	     activity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
	}
	
	public static void closeSocket()
	{
		try {
			btSocket.close();
			btSocket = null;
		} catch (IOException e) { }
	}
}
