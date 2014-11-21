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
	private TextView gameScore;
	private TextView matchScore;
	private TrucoManager manager;
	private boolean card1Used;
	private boolean card2Used;
	private boolean card3Used;
	private boolean startedRound;
	private int hostCardIndex;
	private int clientCardIndex;
	private int player2CardsUsed;
	private int roundCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		initViewComponents();
		
		manager = new TrucoManager();
		
		newGame();
	}
	
	// Inicializa os views da activity
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
	}

	// Inicia uma nova rodada e inicializa flags utilizadas durante o processamento do jogo. Chamado a cada nova rodada
	private void newGame() 
	{
		manager.newGame();
		
		/*
		 * Contagem de cartas utilizadas pelo cliente. Usado para
		 * esconder as cartas do cliente, dependendo do numero utilizado.
		 */
		player2CardsUsed = 0;
		roundCount = 1; // Contagem de rounds
		
		hostCardIndex = 0; // Index no vetor handPlayer1, referente a carta jogada pelo host
		clientCardIndex = 0; // Index no vetor handPlayer2, referente a carta jogada pelo client
		
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
	
	private void setOpponentPlayingCard(int cardIndex)
	{		
		int resourceId = getResources().getIdentifier(manager.handPlayer2[cardIndex].fileName, "drawable", getPackageName());
		opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
		
		player2CardsUsed++;
		
		if (player2CardsUsed == 1)
			opponentCard3.setVisibility(ImageView.INVISIBLE);
		else if (player2CardsUsed == 2)
			opponentCard2.setVisibility(ImageView.INVISIBLE);
		else if (player2CardsUsed == 3)
			opponentCard1.setVisibility(ImageView.INVISIBLE);
	}
	
	private void verifyWinner() {
		
		manager.compareCards(hostCardIndex, clientCardIndex, roundCount);
		
		if (roundCount == 1)
		{
			// Define quem inicia o prox round (quem ganha), e informa ao usuario o resultado do round
			if (manager.firstRoundResult == TrucoManager.ROUND_P1_WINNER)
			{
				manager.playerTurn = 1;
				startedRound = true;
				Toast.makeText(HostGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.firstRoundResult == TrucoManager.ROUND_P2_WINNER)
			{
				manager.playerTurn = 2;
				startedRound = false;
				Toast.makeText(HostGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.firstRoundResult == TrucoManager.ROUND_DRAW)
			{
				if (startedRound)
				{
					manager.playerTurn = 1;
					startedRound = true;
				}
				else
				{
					manager.playerTurn = 2;
					startedRound = false;
				}
				
				Toast.makeText(HostGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			}
			
			// Atualiza o placar da rodada
			gameScore.setText(String.valueOf(manager.player1GameScore) + " x " + String.valueOf(manager.player2GameScore));
			
			// Incremena o contador de rounds
			roundCount++;
		}
		else if (roundCount == 2)
		{
			// Define quem inicia o prox round (quem ganha), e informa ao usuario o resultado do round
			if (manager.secondRoundResult == TrucoManager.ROUND_P1_WINNER)
			{
				manager.playerTurn = 1;
				startedRound = true;
				Toast.makeText(HostGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.secondRoundResult == TrucoManager.ROUND_P2_WINNER)
			{
				manager.playerTurn = 2;
				startedRound = false;
				Toast.makeText(HostGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.secondRoundResult == TrucoManager.ROUND_DRAW)
			{
				if (startedRound)
				{
					manager.playerTurn = 1;
					startedRound = true;
				}
				else
				{
					manager.playerTurn = 2;
					startedRound = false;
				}
				
				Toast.makeText(HostGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			}

			gameScore.setText(String.valueOf(manager.player1GameScore) + " x " + String.valueOf(manager.player2GameScore));
			
			int winner = manager.secondRoundWinner();			
			if (winner == TrucoManager.NO_WINNER_YET)
			{
				// Se ainda nao existe vencedor, incrementa o contador de rounds
				roundCount++;
			}
			else if (winner == TrucoManager.GAME_P1_WINNER)
				;//TODO
			else if (winner == TrucoManager.GAME_P2_WINNER)
				;//TODO
		}
		else if (roundCount == 3)
		{
			// Informa ao usuario quem ganhou, ou se aconteceu um empate
			if (manager.thirdRoundResult == TrucoManager.ROUND_P1_WINNER)
				Toast.makeText(HostGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			else if (manager.thirdRoundResult == TrucoManager.ROUND_P2_WINNER)
				Toast.makeText(HostGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			else if (manager.thirdRoundResult == TrucoManager.ROUND_DRAW)
				Toast.makeText(HostGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			
			gameScore.setText(String.valueOf(manager.player1GameScore) + " x " + String.valueOf(manager.player2GameScore));
			
			if (manager.gameResult() == TrucoManager.GAME_DRAW)
				;// TODO
			else if (manager.gameResult() == TrucoManager.GAME_P1_WINNER)
				;//TODO
			else if (manager.gameResult() == TrucoManager.GAME_P2_WINNER)
				;//TODO
		}
		
		roundCount++;
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
						hostCardIndex = 0;
						card1Used = true;
						manager.playerTurn = 1;
						
						doSendCardInfo(0);
					}
					break;
				case R.id.imageViewCard2:
					if (!card2Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer1[1].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card2.setVisibility(ImageView.INVISIBLE);
						hostCardIndex = 1;
						card2Used = true;
						manager.playerTurn = 1;
						
						doSendCardInfo(1);
					}
					break;
				case R.id.imageViewCard3:
					if (!card3Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer1[2].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card3.setVisibility(ImageView.INVISIBLE);
						hostCardIndex = 2;
						card3Used = true;
						manager.playerTurn = 1;
						
						doSendCardInfo(2);
					}
					break;
			}
		}
	}
	
	private void doSendInitialInfo() 
	{
		AsyncTask<Void, Void, Boolean> sendInitalInfo = new AsyncTask<Void, Void, Boolean>() {
			
			@Override
			protected Boolean doInBackground(Void... params) {
				/*
				 * Envia:
				 * playerTurn
				 * cartas do player1
				 * cartas do player2
				 * vira
				 * manilha
				 */
				String initialInfo = manager.playerTurn + "," 
							       + manager.handPlayer1[0].suit + ","
							       + manager.handPlayer1[0].cardValue + ","
							       + manager.handPlayer1[1].suit + ","
							       + manager.handPlayer1[1].cardValue + ","
							       + manager.handPlayer1[2].suit + ","
							       + manager.handPlayer1[2].cardValue + ","
							       + manager.handPlayer2[0].suit + ","
							       + manager.handPlayer2[0].cardValue + ","
							       + manager.handPlayer2[1].suit + ","
							       + manager.handPlayer2[1].cardValue + ","
							       + manager.handPlayer2[2].suit + ","
							       + manager.handPlayer2[2].cardValue + ","
							       + manager.vira.suit + ","
							       + manager.vira.cardValue + ","
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
	
	private void doSendCardInfo(int cardIndex) 
	{
		AsyncTask<Integer, Void, Boolean> sendCardInfo = new AsyncTask<Integer, Void, Boolean>() {
			
			@Override
			protected void onPreExecute() 
			{
				if (!startedRound)
				{
					verifyWinner();
				}
				super.onPreExecute();
			}
			
			@Override
			protected Boolean doInBackground(Integer... params) 
			{
				/*
				 * Envia:
				 * Index da carta jogada pelo host
				 */
				try 
				{
					BluetoothHelper.getBtSocket().getOutputStream().write((params[0].toString() + ",").getBytes());
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
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
			}
		};
		
		sendCardInfo.execute(cardIndex);
	}
	
	private void doReceiveCardInfo() 
	{
		AsyncTask<Void, Void, byte[]> receiveCardInfo = new AsyncTask<Void, Void, byte[]>() {
			
			@Override
			protected byte[] doInBackground(Void... params) 
			{
				/*
				 * Envia:
				 * Index da carta jogada pelo cliente
				 */
				byte[] buffer = new byte[128];
				try 
				{
					BluetoothHelper.getBtSocket().getInputStream().read(buffer);
				} 
				catch (IOException e) 
				{
					Log.i(getClass().getName(), e.getMessage().toString());
					return null;
				}
				return buffer;
			}
			
			@Override
			protected void onPostExecute(byte[] result) 
			{
				super.onPostExecute(result);
				if (result == null) // Falha
				{
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				} 
				else // Sucesso
				{
					try 
					{
						String temp = new String(result, "UTF-8");
						String[] cardIndex = temp.split(",");
						clientCardIndex = Integer.parseInt(cardIndex[0]);
						setOpponentPlayingCard(clientCardIndex);
					} 
					catch (UnsupportedEncodingException e) 
					{
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(HostGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
					
					if (startedRound)
					{
						verifyWinner();
					}
				}
			}
		};
		
		receiveCardInfo.execute();
	}
}