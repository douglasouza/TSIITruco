package br.ufms.facom.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;
import br.ufms.facom.truco.R;

public class ClientGameActivity extends Activity implements OnClickListener{

	private ImageView card1;
	private ImageView card2;
	private ImageView card3;
	private ImageView opponentCard1;
	private ImageView opponentCard2;
	private ImageView opponentCard3;
	private ImageView playingCard;
	private ImageView opponentPlayingCard;
	private ImageView vira;
	private int playerTurn;
	private boolean turnPlayed;
	private boolean card1Used;
	private boolean card2Used;
	private boolean card3Used;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		init();
	}

	private void init() 
	{
		card1 = (ImageView) findViewById(R.id.imageViewCard1);
		card2 = (ImageView) findViewById(R.id.imageViewCard2);
		card3 = (ImageView) findViewById(R.id.imageViewCard3);
		opponentCard1 = (ImageView) findViewById(R.id.imageViewOpponentCard1);
		opponentCard2 = (ImageView) findViewById(R.id.imageViewOpponentCard2);
		opponentCard3 = (ImageView) findViewById(R.id.imageViewOpponentCard3);
		playingCard = (ImageView) findViewById(R.id.imageViewPlayingCard);
		opponentPlayingCard = (ImageView) findViewById(R.id.imageViewOpponentPlayingCard);
		vira = (ImageView) findViewById(R.id.imageViewVira);
		
		card1.setOnClickListener(this);
		card2.setOnClickListener(this);
		card3.setOnClickListener(this);
		
		AsyncTask<Void, Void, byte[]> receiveInitialInfo = new AsyncTask<Void, Void, byte[]>() {
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
				if (result != null)
				{
					try {
						String temp = new String(result, "UTF-8");
						String[] names = temp.split(",");
						initCards(names[1], names[2], names[3], names[4]);
						Toast.makeText(ClientGameActivity.this, new String(result, "UTF-8"), Toast.LENGTH_LONG).show();
					} catch (UnsupportedEncodingException e) {
						Log.i(getClass().getName(), e.getMessage().toString());
					}
				}
				else
				{
					Toast.makeText(ClientGameActivity.this, "NAO CHEGOU", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}
		};
		
		receiveInitialInfo.execute();
	}
	
	private void initCards(String card1Name, String card2Name, String card3Name, String viraName) {
		int resourceId;
		
		resourceId = getResources().getIdentifier(card1Name, "drawable", getPackageName());
		card1.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(card2Name, "drawable", getPackageName());
		card2.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(card3Name, "drawable", getPackageName());
		card3.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(viraName, "drawable", getPackageName());
		vira.setImageDrawable(getResources().getDrawable(resourceId));
	}
	
	@Override
	public void onBackPressed() {
		BluetoothHelper.closeSocket();
		super.onBackPressed();
	}
	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
			case R.id.imageViewCard1:
				break;
			case R.id.imageViewCard2:
				break;
			case R.id.imageViewCard3:
				break;
		}
	}
}
