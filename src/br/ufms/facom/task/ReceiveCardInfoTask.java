package br.ufms.facom.task;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;

public class ReceiveCardInfoTask extends AsyncTask<Void, Void, byte[]> 
{
	private Activity activity;
	
	public ReceiveCardInfoTask(Activity act) {
		activity = act;
	}
	
	@Override
	protected byte[] doInBackground(Void... params) {
		byte[] buffer = new byte[1024];
		try {
			BluetoothHelper.getBtSocket().getInputStream().read(buffer);
		} catch (IOException e) {
			Log.i(getClass().getName(), e.getMessage().toString());
			return null;
		}
		return buffer;
	}
	
	@Override
	protected void onPostExecute(byte[] result) {
		super.onPostExecute(result);
		if (result == null)
		{
			Toast.makeText(activity, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
			BluetoothHelper.closeSocket();
			activity.finish();
		}
	}
}