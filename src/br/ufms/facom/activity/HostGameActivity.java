package br.ufms.facom.activity;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;
import br.ufms.facom.manager.TrucoManager;
import br.ufms.facom.truco.R;

public class HostGameActivity extends Activity implements OnClickListener{
	
	//private AnimationSet animSet;
	private ImageView card1;
	private ImageView card2;
	private ImageView card3;
	private ImageView opponentCard1;
	private ImageView opponentCard2;
	private ImageView opponentCard3;
	private ImageView playingCard;
	private ImageView opponentPlayingCard;
	private ImageView vira;
	private TrucoManager manager;
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
		
		manager = new TrucoManager();
		turnPlayed = false;
		card1Used = false;
		card2Used = false;
		card3Used = false;
		
		initCards();
		
		AsyncTask<Void, Void, Boolean> sendInitalInfo = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				String initialInfo = manager.playerTurn + "," + manager.handPlayer2[0].fileName + "," + manager.handPlayer2[1].fileName + "," + manager.handPlayer2[2].fileName + "," + manager.vira.fileName + ",";
				try {
					BluetoothHelper.getBtSocket().getOutputStream().write(initialInfo.getBytes());
				} catch (IOException e) {
					Log.i(getClass().getName(), e.getMessage().toString());
					return false;
				}
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				if (result == true)
					Toast.makeText(HostGameActivity.this, "SUCESSO", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(HostGameActivity.this, "DEU MERDA", Toast.LENGTH_LONG).show();
				super.onPostExecute(result);
			}
		};
		
		sendInitalInfo.execute();
		
		Toast.makeText(this, Integer.toString(manager.playerTurn), Toast.LENGTH_LONG).show();
	}
	
	private void initCards() {
		int resourceId;
		
		resourceId = getResources().getIdentifier(manager.handPlayer1[0].fileName, "drawable", getPackageName());
		card1.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(manager.handPlayer1[1].fileName, "drawable", getPackageName());
		card2.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(manager.handPlayer1[2].fileName, "drawable", getPackageName());
		card3.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(manager.vira.fileName, "drawable", getPackageName());
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
				if (manager.playerTurn == 0 && !turnPlayed && !card1Used)
				{
					int resourceId = getResources().getIdentifier(manager.handPlayer1[0].fileName, "drawable", getPackageName());
					playingCard.setImageDrawable(getResources().getDrawable(resourceId));
					card1.setAlpha(0);
					turnPlayed = true;
					card1Used= true;
					
					AsyncTask<Void, Void, Boolean> sendCard = new AsyncTask<Void, Void, Boolean>() {
						@Override
						protected Boolean doInBackground(Void... params) {
							String cardInfo = "lalala";
							try {
								BluetoothHelper.getBtSocket().getOutputStream().write(cardInfo.getBytes());
							} catch (IOException e) {
								Log.i(getClass().getName(), e.getMessage().toString());
								return false;
							}
							return true;
						}
						
						@Override
						protected void onPostExecute(Boolean result) {
							if (result == true)
								Toast.makeText(HostGameActivity.this, "SUCESSO", Toast.LENGTH_LONG).show();
							else
								Toast.makeText(HostGameActivity.this, "DEU MERDA", Toast.LENGTH_LONG).show();
							super.onPostExecute(result);
						}
					};
					
					sendCard.execute();
				}
				break;
			case R.id.imageViewCard2:
				if (manager.playerTurn == 0 && !turnPlayed && !card2Used)
				{
					int resourceId = getResources().getIdentifier(manager.handPlayer1[1].fileName, "drawable", getPackageName());
					playingCard.setImageDrawable(getResources().getDrawable(resourceId));
					card2.setAlpha(0);
					turnPlayed = true;
					card2Used= true;
				}
				break;
			case R.id.imageViewCard3:
				if (manager.playerTurn == 0 && !turnPlayed && !card3Used)
				{
					int resourceId = getResources().getIdentifier(manager.handPlayer1[2].fileName, "drawable", getPackageName());
					playingCard.setImageDrawable(getResources().getDrawable(resourceId));
					card3.setAlpha(0);
					turnPlayed = true;
					card3Used= true;
				}
				break;
		}
	}
	
//	private void setAnimation() 
//	{
//		Animation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
//		alphaAnim.setDuration(600);
//		
//		Animation translateAnim = new TranslateAnimation(0.0f, 0.0f, 200.0f, 0.0f);
//		translateAnim.setDuration(600);
//		
//		animSet = new AnimationSet(true);
//		animSet.addAnimation(alphaAnim);
//		animSet.addAnimation(translateAnim);
//	}
}
