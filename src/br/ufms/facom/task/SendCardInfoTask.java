package br.ufms.facom.task;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;

public class SendCardInfoTask extends AsyncTask<String, Void, Boolean> 
{
	private Activity activity;
	
	public SendCardInfoTask(Activity act) {
		activity = act;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		try {
			BluetoothHelper.getBtSocket().getOutputStream().write(params[0].getBytes());
		} catch (IOException e) {
			Log.i(getClass().getName(), e.getMessage().toString());
			return false;
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result == false)
		{
			Toast.makeText(activity, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
			BluetoothHelper.closeSocket();
			activity.finish();
		}
	}
}