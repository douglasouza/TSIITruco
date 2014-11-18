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
import android.widget.TextView;
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
	private String[] turnAndCardsNames;
	private TextView gameScore;
	private TextView matchScore;
	private boolean card1Used;
	private boolean card2Used;
	private boolean card3Used;
	private boolean startedGame;
	private int playerTurn;
	private int player1GameScore;
	private int player2GameScore;
	private int player1MatchScore;
	private int player2MatchScore;
	private int usedCardPlayer1;
	
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
		gameScore = (TextView) findViewById(R.id.txtViewGameScore);
		matchScore = (TextView) findViewById(R.id.txtViewMatchScore);
		
		card1.setOnClickListener(this);
		card2.setOnClickListener(this);
		card3.setOnClickListener(this);
		
		doReceiveInitialInfo();
		
		player1GameScore = 0;
		player2GameScore = 0;
		player1MatchScore = 0;
		player2MatchScore = 0;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void setCards(String card1Name, String card2Name, String card3Name, String viraName) {
		int resourceId;
		
		resourceId = getResources().getIdentifier(card1Name, "drawable", getPackageName());
		card1.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(card2Name, "drawable", getPackageName());
		card2.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(card3Name, "drawable", getPackageName());
		card3.setImageDrawable(getResources().getDrawable(resourceId));
		
		resourceId = getResources().getIdentifier(viraName, "drawable", getPackageName());
		vira.setImageDrawable(getResources().getDrawable(resourceId));
		
		usedCardPlayer1 = 0;
	}
	
	private void setOpponentPlayingCard(String cardName)
	{
		int resourceId = getResources().getIdentifier(cardName, "drawable", getPackageName());
		opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
		
		usedCardPlayer1++;
		
		if (usedCardPlayer1 == 1)
			opponentCard3.setVisibility(ImageView.INVISIBLE);
		if (usedCardPlayer1 == 2)
			opponentCard2.setVisibility(ImageView.INVISIBLE);
		if (usedCardPlayer1 == 3)
			opponentCard1.setVisibility(ImageView.INVISIBLE);
		
		playerTurn = 1;
	}
	
	private void setNewGame(String winner)
	{
		if (winner.equals(HostGameActivity.P1_WINNER))
		{
			player1MatchScore++;
			Toast.makeText(ClientGameActivity.this, "Você perdeu a rodada!", Toast.LENGTH_LONG).show();
		}
		else
		{
			player2MatchScore++;
			Toast.makeText(ClientGameActivity.this, "Você venceu a rodada!", Toast.LENGTH_LONG).show();
		}
		
		matchScore.setText(String.valueOf(player1MatchScore) + " x " + String.valueOf(player2MatchScore));
		
		setCards("decks_back", "decks_back", "decks_back", "decks_back");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Log.i(getClass().getName(), e.getMessage().toString());
			Toast.makeText(ClientGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
			BluetoothHelper.closeSocket();
			finish();
		}
		
		doReceiveInitialInfo();
	}
	
	private void setNewRound(String winner)
	{
		if (winner.equals(HostGameActivity.P1_WINNER))
		{
			player1GameScore++;
			Toast.makeText(ClientGameActivity.this, "Você perdeu!", Toast.LENGTH_LONG).show();
		}
		else if (winner.equals(HostGameActivity.P2_WINNER))
		{
			player2GameScore++;
			Toast.makeText(ClientGameActivity.this, "Você venceu!", Toast.LENGTH_LONG).show();
		}
		else
		{
			player1GameScore++;
			player2GameScore++;
			Toast.makeText(ClientGameActivity.this, "Empate!", Toast.LENGTH_LONG).show();
		}
		
		gameScore.setText(String.valueOf(player1GameScore) + " x " + String.valueOf(player2GameScore));
	}
	
	@Override
	public void onBackPressed() {
		BluetoothHelper.closeSocket();
		super.onBackPressed();
	}
	
	@Override
	public void onClick(View v) 
	{
		if (playerTurn == 1)
		{
			switch (v.getId())
			{
				case R.id.imageViewCard1:
					if (!card1Used)
					{
						int resourceId = getResources().getIdentifier(turnAndCardsNames[1], "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card1.setVisibility(ImageView.INVISIBLE);
						card1Used = true;
						playerTurn = 0;
						
						//send card name and card index
						doSendCardInfo(turnAndCardsNames[1] + ",0,");
					}
					break;
				case R.id.imageViewCard2:
					if (!card2Used)
					{
						int resourceId = getResources().getIdentifier(turnAndCardsNames[2], "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card2.setVisibility(ImageView.INVISIBLE);
						card2Used = true;
						playerTurn = 0;
						
						//send card name and card index
						doSendCardInfo(turnAndCardsNames[2] + ",1,");
					}
					break;
				case R.id.imageViewCard3:
					if (!card3Used)
					{
						int resourceId = getResources().getIdentifier(turnAndCardsNames[3], "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card3.setVisibility(ImageView.INVISIBLE);
						card3Used = true;
						playerTurn = 0;
						
						//send card name and card index
						doSendCardInfo(turnAndCardsNames[3] + ",2,");
					}
					break;
			}
		}
	}
	
	private void doReceiveInitialInfo() {
		AsyncTask<Void, Void, byte[]> receiveInitialInfo = new AsyncTask<Void, Void, byte[]>() {
			@Override
			protected byte[] doInBackground(Void... params) {
				byte[] buffer = new byte[256];
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
					try 
					{
						String temp = new String(result, "UTF-8");
						turnAndCardsNames = temp.split(",");
						playerTurn = Integer.parseInt(turnAndCardsNames[0]);
						setCards(turnAndCardsNames[1], turnAndCardsNames[2], turnAndCardsNames[3], turnAndCardsNames[4]);
						
						if (playerTurn == 1)
						{
							startedGame = true;
							Toast.makeText(ClientGameActivity.this, "Faça Sua Jogada", Toast.LENGTH_SHORT).show();
						}
						else
						{
							startedGame = false;
							Toast.makeText(ClientGameActivity.this, "Turno do Oponente", Toast.LENGTH_SHORT).show();
							
							doReceiveCardInfo();
						}
					} 
					catch (UnsupportedEncodingException e) 
					{
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(ClientGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
				}
				else
				{
					Toast.makeText(ClientGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
				super.onPostExecute(result);
			}
		};
		
		receiveInitialInfo.execute();
	}
	
	private void doSendCardInfo(String sendData) {
		AsyncTask<String, Void, Boolean> sendCardInfo = new AsyncTask<String, Void, Boolean>() {
			
			@Override
			protected void onPreExecute() {
				if (startedGame)
					Toast.makeText(ClientGameActivity.this, "Turno do Oponente!", Toast.LENGTH_SHORT).show();
				super.onPreExecute();
			}
			
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
					Toast.makeText(ClientGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
				else
				{
					if (startedGame)
					{
						doReceiveCardInfo();
					}
					else
					{
						doReceiveRoundResult();
					}
				}
			}
		};
		
		sendCardInfo.execute(sendData);
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
					Toast.makeText(ClientGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				} 
				else
				{
					try 
					{
						String temp = new String(result, "UTF-8");
						String[] cardName = temp.split(",");
						setOpponentPlayingCard(cardName[0]);
					} 
					catch (UnsupportedEncodingException e) 
					{
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(ClientGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
					
					if (startedGame)
						doReceiveRoundResult();
				}
			}
		};
	
		receiveCardInfo.execute();
	}
	
	private void doReceiveGameResult() {
		AsyncTask<Void, Void, byte[]> receiveGameResult = new AsyncTask<Void, Void, byte[]>() {
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
					Toast.makeText(ClientGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				} 
				else
				{
					try {
						String temp = new String(result, "UTF-8");
						String[] gameResult = temp.split(",");
						setNewGame(gameResult[0]);
					} catch (UnsupportedEncodingException e) {
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(ClientGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
				}
			}
		};
		
		receiveGameResult.execute();
	}
	
	private void doReceiveRoundResult() {
		AsyncTask<Void, Void, byte[]> receiveRoundResult = new AsyncTask<Void, Void, byte[]>() {
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
					Toast.makeText(ClientGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				} 
				else
				{
					String[] gameResult = {""};
					try {
						String temp = new String(result, "UTF-8");
						gameResult = temp.split(",");
						setNewRound(gameResult[0]);
					} catch (UnsupportedEncodingException e) {
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(ClientGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
					
					if (gameResult[0].equals(HostGameActivity.P1_WINNER))
					{
						playerTurn = 0;
						startedGame = false;
						Toast.makeText(ClientGameActivity.this, "Turno do Oponente!", Toast.LENGTH_SHORT).show();
						doReceiveCardInfo();
					}
					else if (gameResult[0].equals(HostGameActivity.P2_WINNER))
					{
						playerTurn = 1;
						startedGame = true;
						Toast.makeText(ClientGameActivity.this, "Faça sua Jogada!", Toast.LENGTH_SHORT).show();
					}
					else if (gameResult[0].equals(HostGameActivity.DRAW))
					{
						if (startedGame)
						{
							startedGame = true;
							playerTurn = 0;
							Toast.makeText(ClientGameActivity.this, "Faça sua Jogada!", Toast.LENGTH_SHORT).show();
						}
						else
						{
							startedGame = false;
							playerTurn = 1;
							Toast.makeText(ClientGameActivity.this, "Turno do Oponente", Toast.LENGTH_SHORT).show();
							doReceiveCardInfo();
						}
					}
				}
			}
		};
		
		receiveRoundResult.execute();
	}
}