package br.ufms.facom.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;
import br.ufms.facom.manager.TrucoManager;
import br.ufms.facom.truco.R;

public class HostGameActivity extends Activity implements OnClickListener{
	
	public static final String P1_WINNER = "Player 1 Won";
	public static final String P2_WINNER = "Player 2 Won";
	
	private Animation fadeIn;
	private Animation fadeOut;
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
	private boolean card1Used;
	private boolean card2Used;
	private boolean card3Used;
	private boolean startedGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		setFadeIn();
		
		setFadeOut();
		
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
		card1Used = false;
		card2Used = false;
		card3Used = false;
		
		manager.playerTurn = 0;
		
		if (manager.playerTurn == 0)
			startedGame = true;
		else
			startedGame = false;
		
		initCards();
		
		doSendInitialInfo();
		
		if (manager.playerTurn == 0)
			Toast.makeText(this, "Faça Sua Jogada", Toast.LENGTH_LONG).show();
		else
		{
			Toast.makeText(this, "Turno do Oponente", Toast.LENGTH_LONG).show();
			
			AsyncTask<Void, Void, byte[]> receiveCardInfo = new AsyncTask<Void, Void, byte[]>() {
				@Override
				protected byte[] doInBackground(Void... params) {
					byte[] buffer = new byte[128];
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
						Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					} 
					else
					{
						try {
							String temp = new String(result, "UTF-8");
							String[] cardName = temp.split(",");
							Log.i(getClass().getName(), cardName[0]);
							setOpponentPlayingCard(cardName[0]);
						} catch (UnsupportedEncodingException e) {
							Log.i(getClass().getName(), e.getMessage().toString());
							Toast.makeText(HostGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
							BluetoothHelper.closeSocket();
							finish();
						}
					}
				}
			};
			
			receiveCardInfo.execute();
		}
	}
	
	private void initCards() {
		int resourceId;
		
		resourceId = getResources().getIdentifier(manager.handPlayer1[0].fileName, "drawable", getPackageName());
		card1.setImageDrawable(getResources().getDrawable(resourceId));
		card1.startAnimation(fadeIn);
		
		resourceId = getResources().getIdentifier(manager.handPlayer1[1].fileName, "drawable", getPackageName());
		card2.setImageDrawable(getResources().getDrawable(resourceId));
		card2.startAnimation(fadeIn);
		
		resourceId = getResources().getIdentifier(manager.handPlayer1[2].fileName, "drawable", getPackageName());
		card3.setImageDrawable(getResources().getDrawable(resourceId));
		card3.startAnimation(fadeIn);
		
		resourceId = getResources().getIdentifier(manager.vira.fileName, "drawable", getPackageName());
		vira.setImageDrawable(getResources().getDrawable(resourceId));
		vira.startAnimation(fadeIn);
	}
	
	private void setOpponentPlayingCard(String cardName)
	{		
		int resourceId = getResources().getIdentifier(cardName, "drawable", getPackageName());
		opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
		opponentPlayingCard.startAnimation(fadeIn);
		
		manager.usedCardPlayer2++;
		
		if (manager.usedCardPlayer2 == 1)
			opponentCard3.startAnimation(fadeOut);
		if (manager.usedCardPlayer2 == 2)
			opponentCard2.startAnimation(fadeOut);
		if (manager.usedCardPlayer2 == 3)
			opponentCard1.startAnimation(fadeOut);
		
		manager.playerTurn = 0;
		Toast.makeText(this, "Faça Sua Jogada", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onBackPressed() {
		BluetoothHelper.closeSocket();
		finish();
	}

	@Override
	public void onClick(View v) 
	{
		if (manager.playerTurn == 0)
		{
			switch (v.getId())
			{
				case R.id.imageViewCard1:
					if (!card1Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer1[0].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card1.startAnimation(fadeOut);
						playingCard.startAnimation(fadeIn);
						card1Used = true;
						manager.playerTurn = 1;
						manager.usedCardPlayer1++;
						
						if (manager.compareCards(0, 0) == 1)
							doSendRoundResult(P1_WINNER);
						else
							doSendRoundResult(P2_WINNER);
							
						//doSendCardInfo(manager.handPlayer1[0].fileName);
						
						doReceiveCardInfo();
					}
					break;
				case R.id.imageViewCard2:
					if (!card2Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer1[1].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card2.startAnimation(fadeOut);
						playingCard.startAnimation(fadeIn);
						card2Used = true;
						manager.playerTurn = 1;
						manager.usedCardPlayer1++;
						
						doSendCardInfo(manager.handPlayer1[1].fileName);
						
						doReceiveCardInfo();
					}
					break;
				case R.id.imageViewCard3:
					if (!card3Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer1[2].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card3.startAnimation(fadeOut);
						playingCard.startAnimation(fadeIn);
						card3Used = true;
						manager.playerTurn = 1;
						manager.usedCardPlayer1++;
						
						doSendCardInfo(manager.handPlayer1[2].fileName);
						
						doReceiveCardInfo();
					}
					break;
			}
		}
	}
	
	private void setFadeIn() 
	{
		fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(900);
	}
	
	private void setFadeOut() 
	{
		fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(900);
	}
	
	private void doSendInitialInfo() {
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
				super.onPostExecute(result);
				if (result == false)
				{
					Toast.makeText(HostGameActivity.this, "Erro de conexão", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
			}
		};
		
		sendInitalInfo.execute();
	}
	
	private void doSendCardInfo(String cardName) {
		AsyncTask<String, Void, Boolean> sendCardInfo = new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				try {
					BluetoothHelper.getBtSocket().getOutputStream().write((params[0] + ",").getBytes());
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
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
			}
		};
		
		sendCardInfo.execute(cardName);
	}
	
	private void doReceiveCardInfo() {
		AsyncTask<Void, Void, byte[]> receiveCardInfo = new AsyncTask<Void, Void, byte[]>() {
			@Override
			protected byte[] doInBackground(Void... params) {
				byte[] buffer = new byte[128];
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
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				} 
				else
				{
					try {
						String temp = new String(result, "UTF-8");
						String[] cardName = temp.split(",");
						Log.i(getClass().getName(), cardName[0]);
						setOpponentPlayingCard(cardName[0]);
					} catch (UnsupportedEncodingException e) {
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(HostGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
				}
			}
		};
		
		receiveCardInfo.execute();
	}
	
	private void doSendRoundResult(String cardName) {
		AsyncTask<String, Void, Boolean> sendCardInfo = new AsyncTask<String, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				try {
					BluetoothHelper.getBtSocket().getOutputStream().write((params[0] + ",").getBytes());
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
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
			}
		};
		
		sendCardInfo.execute(cardName);
	}
}