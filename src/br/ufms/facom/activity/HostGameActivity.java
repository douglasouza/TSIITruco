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
import br.ufms.facom.manager.TrucoManager;
import br.ufms.facom.truco.R;

public class HostGameActivity extends Activity implements OnClickListener{
	
	private ImageView card1;
	private ImageView card2;
	private ImageView card3;
	private ImageView opponentCard1;
	private ImageView opponentCard2;
	private ImageView opponentCard3;
	private ImageView playingCard;
	private ImageView opponentPlayingCard;
	private ImageView vira;
	private String winner;
	private TextView gameScore;
	private TextView matchScore;
	private TrucoManager manager;
	private boolean card1Used;
	private boolean card2Used;
	private boolean card3Used;
	private boolean startedRound;
	private int roundCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		initViewComponents();
		
		newGame();
	}
	
	// Inicia os views da activity e inicia uma nova partida
	private void initViewComponents()
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
		
		manager = new TrucoManager();
	}

	// Inicia uma nova rodada e inicializa flags utilizadas durante o processamento do jogo. Chamado a cada nova rodada
	private void newGame() 
	{
		manager.newGame();
		
		roundCounter = 1; // Contagem de rounds
		
		/*
		 * Flag que possibilita saber quando calcular o resultado do round.
		 * Quando o host comeca o round, so eh possivel calcular o resultado
		 * apos receber a carta do cliente. Caso contrario, deve ser calculado 
		 * ao escolher (clicar) a carta para enviar ao cliente.
		 */
		if (manager.playerTurn == 0)
			startedRound = true;
		else
			startedRound = false;				
				
		card1Used = false;
		card2Used = false;
		card3Used = false;
		
		initCards();
		
		// Envia os dados do manager para o cliente. Assim o cliente pode inicializar o seu proprio manager com os mesmos dados
		doSendInitialInfo();
	}

	// Ajusta as imagens das cartas
	private void initCards() 
	{
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
	
	private void setOpponentPlayingCard(String cardName)
	{		
		int resourceId = getResources().getIdentifier(cardName, "drawable", getPackageName());
		opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
		
		manager.usedCardPlayer2++;
		
		if (manager.usedCardPlayer2 == 1)
			opponentCard3.setVisibility(ImageView.INVISIBLE);
		else if (manager.usedCardPlayer2 == 2)
			opponentCard2.setVisibility(ImageView.INVISIBLE);
		else if (manager.usedCardPlayer1 == 3)
			opponentCard2.setVisibility(ImageView.INVISIBLE);
		
		manager.playerTurn = 0;
	}
	
	@Override
	public void onBackPressed() {
		BluetoothHelper.closeSocket();
		super.onBackPressed();
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
						card1.setVisibility(ImageView.INVISIBLE);
						cardPlayedIndex = 0;
						card1Used = true;
						manager.playerTurn = 1;
						manager.usedCardPlayer1++;
						
						doSendCardInfo(manager.handPlayer1[0].fileName);
					}
					break;
				case R.id.imageViewCard2:
					if (!card2Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer1[1].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card2.setVisibility(ImageView.INVISIBLE);
						cardPlayedIndex = 1;
						card2Used = true;
						manager.playerTurn = 1;
						manager.usedCardPlayer1++;
						
						doSendCardInfo(manager.handPlayer1[1].fileName);
					}
					break;
				case R.id.imageViewCard3:
					if (!card3Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer1[2].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card3.setVisibility(ImageView.INVISIBLE);
						cardPlayedIndex = 2;
						card3Used = true;
						manager.playerTurn = 1;
						manager.usedCardPlayer1++;
						
						doSendCardInfo(manager.handPlayer1[2].fileName);
					}
					break;
			}
		}
	}
	
	private void calculateGameWinner() 
	{
		if (manager.compareCards(cardPlayedIndex, opponentPlayedCardIndex) == 1)
		{
			player1GameScore++;
			
			gameScore.setText(String.valueOf(player1GameScore) + " x " + String.valueOf(player2GameScore));
											
			Toast.makeText(HostGameActivity.this, "Você venceu!", Toast.LENGTH_LONG).show();
			
			winner = P1_WINNER;
		}
		else if (manager.compareCards(cardPlayedIndex, opponentPlayedCardIndex) == 2)
		{
			player2GameScore++;
			
			gameScore.setText(String.valueOf(player1GameScore) + " x " + String.valueOf(player2GameScore));
			
			Toast.makeText(HostGameActivity.this, "Você perdeu!", Toast.LENGTH_LONG).show();
			
			winner = P2_WINNER;
		}
		else
		{
			player1GameScore++;
			player2GameScore++;
			
			gameScore.setText(String.valueOf(player1GameScore) + " x " + String.valueOf(player2GameScore));
			
			Toast.makeText(HostGameActivity.this, "Empate!", Toast.LENGTH_LONG).show();
			
			winner = DRAW;
		}
	}

	private void doSendInitialInfo() {
		AsyncTask<Void, Void, Boolean> sendInitalInfo = new AsyncTask<Void, Void, Boolean>() {
			
			@Override
			protected Boolean doInBackground(Void... params) {
				/*
				 * Envia:
				 * playerTurn
				 * nome das cartas do player1
				 * nome das cartas do player2
				 * vira
				 * manilha
				 */
				String initialInfo = manager.playerTurn + "," 
							       + manager.handPlayer1[0].fileName + "," 
						           + manager.handPlayer1[1].fileName + "," 
							       + manager.handPlayer1[2].fileName + ","
						           + manager.handPlayer2[0].fileName + "," 
						           + manager.handPlayer2[1].fileName + "," 
							       + manager.handPlayer2[2].fileName + ","
							       + manager.vira.fileName + ","
							       + manager.manilha + ",";
				
				try 
				{
					BluetoothHelper.getBtSocket().getOutputStream().write(initialInfo.getBytes());
				} 
				catch (IOException e) 
				{
					Log.i(getClass().getName(), e.getMessage().toString());
					return false;
				}
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result == false) // Falha
				{
					Toast.makeText(HostGameActivity.this, "Erro de conexão", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
				else // Sucesso
				{
					if (manager.playerTurn == 0) // Informa ao host que eh sua vez de jogar
						Toast.makeText(HostGameActivity.this, "Faça Sua Jogada", Toast.LENGTH_SHORT).show();
					else // Aguarda receber informacoes sobre a carta jogada pelo cliente
					{
						Toast.makeText(HostGameActivity.this, "Turno do Oponente", Toast.LENGTH_SHORT).show();
						doReceiveCardInfo();
					}
				}
			}
		};
		
		sendInitalInfo.execute();
	}
	
	private void doSendCardInfo(final String cardName) {
		AsyncTask<String, Void, Boolean> sendCardInfo = new AsyncTask<String, Void, Boolean>() {
			
			@Override
			protected void onPreExecute() {
				if (startedGame)
					Toast.makeText(HostGameActivity.this, "Turno do Oponente", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
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
						calculateGameWinner();
						doSendRoundResult(winner);
					}
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
						String[] receivedData = temp.split(",");
						Log.i(getClass().getName(), receivedData[0]);
						opponentPlayedCardIndex = Integer.parseInt(receivedData[1]);
						setOpponentPlayingCard(receivedData[0]);
						manager.playerTurn = 0;
					} catch (UnsupportedEncodingException e) {
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(HostGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
					
					if (startedGame)
					{
						calculateGameWinner();
						doSendRoundResult(winner);
					}
					else
						Toast.makeText(HostGameActivity.this, "Faça sua Jogada!", Toast.LENGTH_SHORT).show();
				}
			}
		};
		
		receiveCardInfo.execute();
	}
	
	private void doSendRoundResult(final String winner) {
		AsyncTask<String, Void, Boolean> sendRoundResult = new AsyncTask<String, Void, Boolean>() {
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
				else
				{
					if (winner == P2_WINNER)
					{
						startedGame = false;
						manager.playerTurn = 1;
						Toast.makeText(HostGameActivity.this, "Turno do Oponente", Toast.LENGTH_SHORT).show();
						doReceiveCardInfo();
					}
					else if (winner == P1_WINNER)
					{
						startedGame = true;
						manager.playerTurn = 0;
						Toast.makeText(HostGameActivity.this, "Faça sua Jogada!", Toast.LENGTH_SHORT).show();
					}
					else if (winner == DRAW)
					{
						if (startedGame)
						{
							startedGame = true;
							manager.playerTurn = 0;
							Toast.makeText(HostGameActivity.this, "Faça sua Jogada!", Toast.LENGTH_SHORT).show();
						}
						else
						{
							startedGame = false;
							manager.playerTurn = 1;
							Toast.makeText(HostGameActivity.this, "Turno do Oponente", Toast.LENGTH_SHORT).show();
							doReceiveCardInfo();
						}
					}
				}
			}
		};
		
		sendRoundResult.execute(winner);
	}
	
	private void doSendCardRoundResult(String cardWinner) {
		AsyncTask<String, Void, Boolean> sendCardRoundResult = new AsyncTask<String, Void, Boolean>() {
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
		
		sendCardRoundResult.execute(cardWinner);
	}
}