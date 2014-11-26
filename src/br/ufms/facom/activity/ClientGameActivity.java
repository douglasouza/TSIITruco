package br.ufms.facom.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;
import br.ufms.facom.manager.TrucoManager;
import br.ufms.facom.truco.R;

public class ClientGameActivity extends Activity implements OnClickListener{

	private AlertDialog.Builder winnerLoserAlert;
	private AlertDialog.Builder exitAlert;
	private Button btnTruco;
	private ImageView card1;
	private ImageView card2;
	private ImageView card3;
	private ImageView opponentCard1;
	private ImageView opponentCard2;
	private ImageView opponentCard3;
	private ImageView playingCard;
	private ImageView opponentPlayingCard;
	private ImageView vira;
	private TextView p1GameScore;
	private TextView p2GameScore;
	private TextView p1MatchScore;
	private TextView p2MatchScore;
	private TrucoManager manager;
	private boolean card1Used;
	private boolean card2Used;
	private boolean card3Used;
	private boolean startedRound;
	private int hostCardIndex;
	private int clientCardIndex;
	private int player1CardsUsed;
	private int roundCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		initViewComponents();
		
		manager = new TrucoManager();
		
		doReceiveInitialInfo(); // O cliente ja comeca recebendo as informacoes iniciais sobre a partida (manager)
	}

	// Inicializa os views da activity
	private void initViewComponents() 
	{
		btnTruco = (Button) findViewById(R.id.btnTruco);
		card1 = (ImageView) findViewById(R.id.imageViewCard1);
		card2 = (ImageView) findViewById(R.id.imageViewCard2);
		card3 = (ImageView) findViewById(R.id.imageViewCard3);
		opponentCard1 = (ImageView) findViewById(R.id.imageViewOpponentCard1);
		opponentCard2 = (ImageView) findViewById(R.id.imageViewOpponentCard2);
		opponentCard3 = (ImageView) findViewById(R.id.imageViewOpponentCard3);
		playingCard = (ImageView) findViewById(R.id.imageViewPlayingCard);
		opponentPlayingCard = (ImageView) findViewById(R.id.imageViewOpponentPlayingCard);
		vira = (ImageView) findViewById(R.id.imageViewVira);
		p1GameScore = (TextView) findViewById(R.id.txtViewPlayer1GameScore);
		p1GameScore.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
		p2GameScore = (TextView) findViewById(R.id.txtViewPlayer2GameScore);
		p1MatchScore = (TextView) findViewById(R.id.txtViewPlayer1MatchScore);
		p1MatchScore.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
		p2MatchScore = (TextView) findViewById(R.id.txtViewPlayer2MatchScore);
		
		btnTruco.setOnClickListener(this);
		card1.setOnClickListener(this);
		card2.setOnClickListener(this);
		card3.setOnClickListener(this);
		
		winnerLoserAlert = new AlertDialog.Builder(ClientGameActivity.this);
		winnerLoserAlert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		
		winnerLoserAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				BluetoothHelper.closeSocket();
				finish();
			}
		});
		
		exitAlert = new AlertDialog.Builder(ClientGameActivity.this);
		exitAlert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		exitAlert.setTitle("Atenção!");
		exitAlert.setMessage("Você Realmente Deseja Sair do Jogo?");
		
		exitAlert.setPositiveButton("Sim", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				BluetoothHelper.closeSocket();
				finish();
			}
		});
		
		exitAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// Não faz nada
			}
		});
	}

	// Inicializa variáveis locais e flags
	private void newGame()
	{
		setCards(manager.handPlayer2[0].fileName, manager.handPlayer2[1].fileName, manager.handPlayer2[2].fileName, manager.vira.fileName);
		
		manager.player1GameScore = 0;
		manager.player2GameScore = 0;
		
		/*
		 * Contagem de cartas utilizadas pelo host. Usado para
		 * esconder as cartas do host, dependendo do numero utilizado.
		 */
		player1CardsUsed = 0;
		roundCount = 1; // Contagem de rounds
		
		hostCardIndex = -1; // Index no vetor handPlayer1, referente a carta jogada pelo host
		clientCardIndex = -1; // Index no vetor handPlayer2, referente a carta jogada pelo client
		
		/*
		 * Flag que possibilita saber quando calcular o resultado do round.
		 * Quando o cliente comeca o round, so eh possivel calcular o resultado
		 * apos receber a carta do host. Caso contrario, deve ser calculado 
		 * ao escolher (clicar) a carta para enviar ao host.
		 */
		if (manager.playerTurn == 2)
			startedRound = true;
		else
			startedRound = false;				
				
		card1Used = false;
		card2Used = false;
		card3Used = false;
		
		Log.i("NewGame", "Started Round:" + startedRound);
		
		// Inicializa o placar da rodada
		p1GameScore.setText(String.valueOf(manager.player1GameScore));
		p2GameScore.setText(String.valueOf(manager.player2GameScore));
	}
	
	// Configura as imagens das cartas na interface
	private void setCards(String card1Name, String card2Name, String card3Name, String viraName) 
	{
		int resourceId;
		
		if (manager.player1MatchScore == 11 && manager.player2MatchScore == 11) // Mão de ferro, jogadores recebem as cartas cobertas
		{
			resourceId = getResources().getIdentifier("decks_back", "drawable", getPackageName());
			
			card1.setImageDrawable(getResources().getDrawable(resourceId));
			card1.setVisibility(ImageView.VISIBLE);
			
			card2.setImageDrawable(getResources().getDrawable(resourceId));
			card2.setVisibility(ImageView.VISIBLE);
			
			card3.setImageDrawable(getResources().getDrawable(resourceId));
			card3.setVisibility(ImageView.VISIBLE);
			
			vira.setImageDrawable(getResources().getDrawable(resourceId));
			vira.setVisibility(ImageView.VISIBLE);
			
			playingCard.setImageDrawable(getResources().getDrawable(resourceId));
			opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
		}
		else
		{
			resourceId = getResources().getIdentifier(card1Name, "drawable", getPackageName());
			card1.setImageDrawable(getResources().getDrawable(resourceId));
			card1.setVisibility(ImageView.VISIBLE);
			
			resourceId = getResources().getIdentifier(card2Name, "drawable", getPackageName());
			card2.setImageDrawable(getResources().getDrawable(resourceId));
			card2.setVisibility(ImageView.VISIBLE);
			
			resourceId = getResources().getIdentifier(card3Name, "drawable", getPackageName());
			card3.setImageDrawable(getResources().getDrawable(resourceId));
			card3.setVisibility(ImageView.VISIBLE);
			
			resourceId = getResources().getIdentifier(viraName, "drawable", getPackageName());
			vira.setImageDrawable(getResources().getDrawable(resourceId));
			vira.setVisibility(ImageView.VISIBLE);
			
			resourceId = getResources().getIdentifier("decks_back", "drawable", getPackageName());
			playingCard.setImageDrawable(getResources().getDrawable(resourceId));
			opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
			
			opponentCard1.setVisibility(ImageView.VISIBLE);
			opponentCard2.setVisibility(ImageView.VISIBLE);
			opponentCard3.setVisibility(ImageView.VISIBLE);
		}
	}
	
	private void setOpponentPlayingCard()
	{		
		int resourceId = getResources().getIdentifier(manager.handPlayer1[hostCardIndex].fileName, "drawable", getPackageName());
		opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
		
		player1CardsUsed++;
		
		if (player1CardsUsed == 1)
			opponentCard3.setVisibility(ImageView.INVISIBLE);
		else if (player1CardsUsed == 2)
			opponentCard2.setVisibility(ImageView.INVISIBLE);
		else if (player1CardsUsed == 3)
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
				startedRound = false;
				Toast.makeText(ClientGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.firstRoundResult == TrucoManager.ROUND_P2_WINNER)
			{
				manager.playerTurn = 2;
				startedRound = true;
				Toast.makeText(ClientGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.firstRoundResult == TrucoManager.ROUND_DRAW)
			{
				if (startedRound)
				{
					manager.playerTurn = 2;
					startedRound = true;
				}
				else
				{
					manager.playerTurn = 1;
					startedRound = false;
				}
				
				Toast.makeText(ClientGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			}
			
			// Atualiza o placar da rodada
			p1GameScore.setText(String.valueOf(manager.player1GameScore));
			p2GameScore.setText(String.valueOf(manager.player2GameScore));
			
			// Incremena o contador de rounds
			roundCount++;
			
			// Se o host ganhou, entao ele comeca o prox round. Portanto deve-se esperar sua jogada
			if (manager.playerTurn == 1)
				doReceiveCardInfo();
		}
		else if (roundCount == 2)
		{
			// Define quem inicia o prox round (quem ganha), e informa ao usuario o resultado do round
			if (manager.secondRoundResult == TrucoManager.ROUND_P1_WINNER)
			{
				manager.playerTurn = 1;
				startedRound = false;
				Toast.makeText(ClientGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.secondRoundResult == TrucoManager.ROUND_P2_WINNER)
			{
				manager.playerTurn = 2;
				startedRound = true;
				Toast.makeText(ClientGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.secondRoundResult == TrucoManager.ROUND_DRAW)
			{
				if (startedRound)
				{
					manager.playerTurn = 2;
					startedRound = true;
				}
				else
				{
					manager.playerTurn = 1;
					startedRound = false;
				}
				
				Toast.makeText(ClientGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			}

			p1GameScore.setText(String.valueOf(manager.player1GameScore));
			p2GameScore.setText(String.valueOf(manager.player2GameScore));
			
			// Verifica se ja existe um vencedor
			int winner = manager.secondRoundWinner();			
			if (winner == TrucoManager.NO_WINNER_YET)
			{
				// Se ainda nao existe vencedor, incrementa o contador de rounds
				roundCount++;
				
				// Se o host ganhou, entao ele comeca o prox round. Portanto deve-se esperar sua jogada
				if (manager.playerTurn == 1)
					doReceiveCardInfo();
			}
			else if (winner == TrucoManager.GAME_P2_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(ClientGameActivity.this, "Você Venceu a Rodada!", Toast.LENGTH_SHORT).show();
				
				if (manager.player2MatchScore == 12) // Cliente venceu a partida
				{
					winnerLoserAlert.setTitle("Partida Encerrada!");
					winnerLoserAlert.setMessage("Você Venceu a Partida :D!");
					winnerLoserAlert.show();
				}
				else // Partida nao acabou ainda
				{
					doReceiveInitialInfo();
				}
			}
			else if (winner == TrucoManager.GAME_P1_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(ClientGameActivity.this, "Você Perdeu a Rodada!", Toast.LENGTH_SHORT).show();

				if (manager.player1MatchScore == 12) // Host venceu a partida
				{
					winnerLoserAlert.setTitle("Partida Encerrada!");
					winnerLoserAlert.setMessage("Você Perdeu a Partida D:!");
					winnerLoserAlert.show();
				}
				else // Partida nao acabou ainda
				{
					doReceiveInitialInfo();
				}
			}
		}
		else if (roundCount == 3)
		{
			// Informa ao usuario quem ganhou o round, ou se aconteceu um empate
			if (manager.thirdRoundResult == TrucoManager.ROUND_P1_WINNER)
				Toast.makeText(ClientGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			else if (manager.thirdRoundResult == TrucoManager.ROUND_P2_WINNER)
				Toast.makeText(ClientGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			else if (manager.thirdRoundResult == TrucoManager.ROUND_DRAW)
				Toast.makeText(ClientGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			
			p1GameScore.setText(String.valueOf(manager.player1GameScore));
			p2GameScore.setText(String.valueOf(manager.player2GameScore));
			
			int result = manager.gameResult();
			
			if (result == TrucoManager.GAME_DRAW)
			{
				Toast.makeText(ClientGameActivity.this, "A Rodada Terminou Empatada!", Toast.LENGTH_SHORT).show();
				doReceiveInitialInfo();
			}
			else if (result == TrucoManager.GAME_P1_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(ClientGameActivity.this, "Você Perdeu a Rodada!", Toast.LENGTH_SHORT).show();

				if (manager.player1MatchScore == 12) // Host venceu a partida
				{
					winnerLoserAlert.setTitle("Partida Encerrada!");
					winnerLoserAlert.setMessage("Você Perdeu a Partida D:!");
					winnerLoserAlert.show();
				}
				else // Partida nao acabou ainda
				{
					doReceiveInitialInfo();
				}
			}
			else if (result == TrucoManager.GAME_P2_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(ClientGameActivity.this, "Você Venceu a Rodada!", Toast.LENGTH_SHORT).show();

				if (manager.player2MatchScore == 12) // Cliente venceu a partida
				{
					winnerLoserAlert.setTitle("Partida Encerrada!");
					winnerLoserAlert.setMessage("Você Venceu a Partida :D!");
					winnerLoserAlert.show();
				}
				else // Partida nao acabou ainda
				{
					doReceiveInitialInfo();
				}
			}
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		exitAlert.show();
	}
	
	@Override
	public void onClick(View v) 
	{
		if (manager.playerTurn == 2)
		{
			switch (v.getId())
			{
				case R.id.imageViewCard1:
					if (!card1Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer2[0].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card1.setVisibility(ImageView.INVISIBLE);
						clientCardIndex = 0;
						card1Used = true;
						manager.playerTurn = 1;
						
						doSendCardInfo(0);
					}
					break;
				case R.id.imageViewCard2:
					if (!card2Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer2[1].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card2.setVisibility(ImageView.INVISIBLE);
						clientCardIndex = 1;
						card2Used = true;
						manager.playerTurn = 1;
						
						doSendCardInfo(1);
					}
					break;
				case R.id.imageViewCard3:
					if (!card3Used)
					{
						int resourceId = getResources().getIdentifier(manager.handPlayer2[2].fileName, "drawable", getPackageName());
						playingCard.setImageDrawable(getResources().getDrawable(resourceId));
						card3.setVisibility(ImageView.INVISIBLE);
						clientCardIndex = 2;
						card3Used = true;
						manager.playerTurn = 1;
						
						doSendCardInfo(2);
					}
					break;
			}
		}
	}
	
	private void doReceiveInitialInfo() 
	{
		Log.i("doReceiveInitialInfo", "Entrou");
		AsyncTask<Void, Void, byte[]> receiveInitialInfo = new AsyncTask<Void, Void, byte[]>() {
			@Override
			protected byte[] doInBackground(Void... params) 
			{
				/*
				 * Recebe:
				 * playerTurn
				 * cartas do player1
				 * cartas do player2
				 * vira
				 * manilha
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
			protected void onPostExecute(byte[] result) {
				if (result != null) // Sucesso
				{
					try 
					{
						// Passa os dados recebidos para que o manager seja inicializado
						manager.setClientManager(new String(result, "UTF-8")); 
						newGame();
						
						if (manager.playerTurn == 2) // Informa ao cliente que eh sua vez de jogar
							Toast.makeText(ClientGameActivity.this, "Faça Sua Jogada", Toast.LENGTH_SHORT).show();
						else // Aguarda receber informacoes sobre a carta jogada pelo host
						{
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
				else // Falha
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
	
	private void doSendCardInfo(int cardIndex) 
	{
		Log.i("doSendCardInfo", "Entrou");
		AsyncTask<Integer, Void, Boolean> sendCardInfo = new AsyncTask<Integer, Void, Boolean>() {
			
			@Override
			protected Boolean doInBackground(Integer... params) 
			{
				/*
				 * Envia:
				 * Index da carta jogada pelo cliente
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
					Toast.makeText(ClientGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
				else
				{
					if (startedRound)
						doReceiveCardInfo();
					else
						verifyWinner();
				}
			}
		};
		
		sendCardInfo.execute(cardIndex);
	}
	
	private void doReceiveCardInfo()
	{
		Log.i("doReceiveCardInfo", "Entrou");
		AsyncTask<Void, Void, byte[]> receiveCardInfo = new AsyncTask<Void, Void, byte[]>() {
			
			@Override
			protected byte[] doInBackground(Void... params) 
			{
				/*
				 * Recebe:
				 * Index da carta jogada pelo host
				 */
				byte[] buffer = new byte[32];
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
					Log.i("doReceiveCardInfo", "Falha");
					Toast.makeText(ClientGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				} 
				else // Sucesso
				{
					Log.i("doReceiveCardInfo", "Sucesso");
					try 
					{
						String temp = new String(result, "UTF-8");
						String[] cardIndex = temp.split(",");
						hostCardIndex = Integer.parseInt(cardIndex[0]);
						Log.i("doReceiveCardInfo", "HostCardIndex: " + String.valueOf(hostCardIndex));
						setOpponentPlayingCard();
					} 
					catch (UnsupportedEncodingException e) 
					{
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(ClientGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}
					
					if (startedRound)
					{
						Log.i("doReceiveCardInfo", "VerifyWinner");
						verifyWinner();
					}
					else
					{
						Log.i("doReceiveCardInfo", "PlayerTurn");
						manager.playerTurn = 2;
					}
				}
			}
		};
		Log.i("doReceiveCardInfo", "Execute");
		receiveCardInfo.execute();
	}
}