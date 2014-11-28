package br.ufms.facom.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.ufms.facom.bluetooth.BluetoothHelper;
import br.ufms.facom.manager.TrucoManager;
import br.ufms.facom.model.MatchDAO;
import br.ufms.facom.truco.R;

public class HostGameActivity extends Activity implements OnClickListener{
	
	private Animation oppAnim;
	private Animation yourAnim;
	private AlertDialog.Builder exitAlert;
	private AlertDialog.Builder trucoAlert;
	private AlertDialog.Builder trucoResponseAlert;
	private AlertDialog.Builder winnerLoserAlert;
	private Button btnTruco;
	private ImageView card1;
	private ImageView card2;
	private ImageView card3;
	private ImageView opponentCard1;
	private ImageView opponentCard2;
	private ImageView opponentCard3;
	private ImageView playingCard;
	private ImageView opponentPlayingCard;
	private ImageView opponentTurn;
	private ImageView yourTurn;
	private ImageView vira;
	private MatchDAO matchDAO;
	private TextView p1GameScore;
	private TextView p2GameScore;
	private TextView p1MatchScore;
	private TextView p2MatchScore;
	private TrucoManager manager;
	private Vibrator vibrator;
	private boolean backPressed;
	private boolean card1Used;
	private boolean card2Used;
	private boolean card3Used;
	private boolean startedRound;
	private boolean turnUsed;
	private int hostCardIndex;
	private int clientCardIndex;
	private int player2CardsUsed;
	private int roundCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		matchDAO = new MatchDAO(HostGameActivity.this);
		matchDAO.open();
		
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		setAnimations();
		
		initViewComponents();
		
		manager = new TrucoManager();
		
		newGame();
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
		opponentTurn = (ImageView) findViewById(R.id.imageViewOpponentTurn);
		yourTurn = (ImageView) findViewById(R.id.imageViewYourTurn);
		p1GameScore = (TextView) findViewById(R.id.txtViewPlayer1GameScore);
		p1GameScore.setTextColor(getResources().getColor(R.color.yellow));
		p2GameScore = (TextView) findViewById(R.id.txtViewPlayer2GameScore);
		p2GameScore.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
		p1MatchScore = (TextView) findViewById(R.id.txtViewPlayer1MatchScore);
		p1MatchScore.setTextColor(getResources().getColor(R.color.yellow));
		p2MatchScore = (TextView) findViewById(R.id.txtViewPlayer2MatchScore);
		p2MatchScore.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
		
		btnTruco.setOnClickListener(this);
		card1.setOnClickListener(this);
		card2.setOnClickListener(this);
		card3.setOnClickListener(this);
		
		backPressed = false;
		
		winnerLoserAlert = new AlertDialog.Builder(HostGameActivity.this);
		winnerLoserAlert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		winnerLoserAlert.setCancelable(false);
		
		winnerLoserAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				saveMatch();
				matchDAO.close();
				BluetoothHelper.closeSocket();
				finish();
			}
		});
		
		exitAlert = new AlertDialog.Builder(HostGameActivity.this);
		exitAlert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		exitAlert.setTitle("Atenção!");
		exitAlert.setMessage("Você Realmente Deseja Sair do Jogo?");
		
		exitAlert.setPositiveButton("Sim", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				backPressed = true;
				BluetoothHelper.closeSocket();
				matchDAO.close();
				finish();
			}
		});
		
		exitAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// Nao faz nada
			}
		});
		
		trucoAlert = new AlertDialog.Builder(HostGameActivity.this);
		trucoAlert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		trucoAlert.setCancelable(false);
		
		trucoAlert.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				manager.increaseGameValue();
				doSendInfo(9);
			}
		});
		
		trucoAlert.setNegativeButton("Correr", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				doSendInfo(8);
			}
		});
		
		trucoResponseAlert = new AlertDialog.Builder(HostGameActivity.this);
		trucoResponseAlert.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		
		trucoResponseAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				// Nao faz nada
			}
		});
	}

	// Inicia uma nova rodada e inicializa flags utilizadas durante o processamento do jogo. Chamado a cada nova rodada
	private void newGame() 
	{
		if (manager.player1MatchScore == 12) // Host venceu a partida
		{
			clearAnimations();
			winnerLoserAlert.setTitle("Partida Encerrada!");
			winnerLoserAlert.setMessage("Você Venceu a Partida :D!");
			winnerLoserAlert.show();
		}
		else if (manager.player2MatchScore == 12) // Cliente venceu a partida
		{
			clearAnimations();
			winnerLoserAlert.setTitle("Partida Encerrada!");
			winnerLoserAlert.setMessage("Você Perdeu a Partida D:!");
			winnerLoserAlert.show();
		}
		else
		{
			Log.i("NewGame", "Entrou");
			manager.newGame();
			/*
			 * Contagem de cartas utilizadas pelo cliente. Usado para
			 * esconder as cartas do cliente, dependendo do numero utilizado.
			 */
			player2CardsUsed = 0;
			roundCount = 1; // Contagem de rounds
			
			hostCardIndex = -1; // Index no vetor handPlayer1, referente a carta jogada pelo host
			clientCardIndex = -1; // Index no vetor handPlayer2, referente a carta jogada pelo client
			
			/*
			 * Flag que possibilita saber quando calcular o resultado do round.
			 * Quando o host comeca o round, so eh possivel calcular o resultado
			 * apos receber a carta do cliente. Caso contrario, deve ser calculado 
			 * ao escolher (clicar) a carta para enviar ao cliente.
			 */
			if (manager.playerTurn == 1)
			{
				startYourTurnAnim();
				startedRound = true;
			}
			else
			{
				startOpponentTurnAnim();
				startedRound = false;
			}
					
			card1Used = false;
			card2Used = false;
			card3Used = false;
			turnUsed = false;
			
			Log.i("NewGame", "Started Round:" + startedRound);
			
			initCards();
			
			// Inicializa o placar da rodada
			p1GameScore.setText(String.valueOf(manager.player1GameScore));
			p2GameScore.setText(String.valueOf(manager.player2GameScore));
			
			// TODO mao de onze
			
			// Envia os dados do manager para o cliente. Assim o cliente pode inicializar o seu proprio manager com os mesmos dados
			doSendInitialInfo();
		}
	}

	// Ajusta as imagens das cartas
	private void initCards() 
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
			resourceId = getResources().getIdentifier(manager.handPlayer1[0].fileName, "drawable", getPackageName());
			card1.setImageDrawable(getResources().getDrawable(resourceId));
			card1.setVisibility(ImageView.VISIBLE);
			
			resourceId = getResources().getIdentifier(manager.handPlayer1[1].fileName, "drawable", getPackageName());
			card2.setImageDrawable(getResources().getDrawable(resourceId));
			card2.setVisibility(ImageView.VISIBLE);
			
			resourceId = getResources().getIdentifier(manager.handPlayer1[2].fileName, "drawable", getPackageName());
			card3.setImageDrawable(getResources().getDrawable(resourceId));
			card3.setVisibility(ImageView.VISIBLE);
			
			resourceId = getResources().getIdentifier(manager.vira.fileName, "drawable", getPackageName());
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
		int resourceId = getResources().getIdentifier(manager.handPlayer2[clientCardIndex].fileName, "drawable", getPackageName());
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
				startYourTurnAnim();
				startedRound = true;
				Toast.makeText(HostGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.firstRoundResult == TrucoManager.ROUND_P2_WINNER)
			{
				manager.playerTurn = 2;
				startOpponentTurnAnim();
				startedRound = false;
				Toast.makeText(HostGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.firstRoundResult == TrucoManager.ROUND_DRAW)
			{
				if (startedRound)
				{
					manager.playerTurn = 1;
					startYourTurnAnim();
					startedRound = true;
				}
				else
				{
					manager.playerTurn = 2;
					startOpponentTurnAnim();
					startedRound = false;
				}
				
				Toast.makeText(HostGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			}
			
			// Atualiza o placar da rodada
			p1GameScore.setText(String.valueOf(manager.player1GameScore));
			p2GameScore.setText(String.valueOf(manager.player2GameScore));
			
			// Incrementa o contador de rounds
			roundCount++;
			
			doWaitAndRefreshPlayingCards();
			
			// Se o cliente ganhou, entao ele comeca o prox round. Portanto deve-se esperar sua jogada
			if (manager.playerTurn == 2)
				doReceiveInfo();
		}
		else if (roundCount == 2)
		{
			// Define quem inicia o prox round (quem ganha), e informa ao usuario o resultado do round
			if (manager.secondRoundResult == TrucoManager.ROUND_P1_WINNER)
			{
				manager.playerTurn = 1;
				startYourTurnAnim();
				startedRound = true;
				Toast.makeText(HostGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.secondRoundResult == TrucoManager.ROUND_P2_WINNER)
			{
				manager.playerTurn = 2;
				startOpponentTurnAnim();
				startedRound = false;
				Toast.makeText(HostGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			}
			else if (manager.secondRoundResult == TrucoManager.ROUND_DRAW)
			{
				if (startedRound)
				{
					manager.playerTurn = 1;
					startYourTurnAnim();
					startedRound = true;
				}
				else
				{
					manager.playerTurn = 2;
					startOpponentTurnAnim();
					startedRound = false;
				}
				
				Toast.makeText(HostGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			}

			p1GameScore.setText(String.valueOf(manager.player1GameScore));
			p2GameScore.setText(String.valueOf(manager.player2GameScore));
			
			// Verifica se ja existe um vencedor
			int winner = manager.secondRoundWinner();			
			if (winner == TrucoManager.NO_WINNER_YET)
			{
				// Se ainda nao existe vencedor, incrementa o contador de rounds
				roundCount++;
				
				doWaitAndRefreshPlayingCards();
				
				// Se o cliente ganhou, entao ele comeca o prox round. Portanto deve-se esperar sua jogada
				if (manager.playerTurn == 2)
					doReceiveInfo();
			}
			else if (winner == TrucoManager.GAME_P1_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(HostGameActivity.this, "Você Venceu a Rodada!", Toast.LENGTH_SHORT).show();
				doWaitAndNewGame();
			}
			else if (winner == TrucoManager.GAME_P2_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(HostGameActivity.this, "Você Perdeu a Rodada!", Toast.LENGTH_SHORT).show();
				doWaitAndNewGame();
			}
		}
		else if (roundCount == 3)
		{
			// Informa ao usuario quem ganhou o round, ou se aconteceu um empate
			if (manager.thirdRoundResult == TrucoManager.ROUND_P1_WINNER)
				Toast.makeText(HostGameActivity.this, "Você Venceu!", Toast.LENGTH_SHORT).show();
			else if (manager.thirdRoundResult == TrucoManager.ROUND_P2_WINNER)
				Toast.makeText(HostGameActivity.this, "Você Perdeu!", Toast.LENGTH_SHORT).show();
			else if (manager.thirdRoundResult == TrucoManager.ROUND_DRAW)
				Toast.makeText(HostGameActivity.this, "Empate!", Toast.LENGTH_SHORT).show();
			
			p1GameScore.setText(String.valueOf(manager.player1GameScore));
			p2GameScore.setText(String.valueOf(manager.player2GameScore));
			
			int result = manager.gameResult();
			
			if (result == TrucoManager.GAME_DRAW)
			{
				Toast.makeText(HostGameActivity.this, "A Rodada Terminou Empatada!", Toast.LENGTH_SHORT).show();
				doWaitAndNewGame();
			}
			else if (result == TrucoManager.GAME_P1_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(HostGameActivity.this, "Você Venceu a Rodada!", Toast.LENGTH_SHORT).show();
				doWaitAndNewGame();
			}
			else if (result == TrucoManager.GAME_P2_WINNER)
			{
				p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
				p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
				Toast.makeText(HostGameActivity.this, "Você Perdeu a Rodada!", Toast.LENGTH_SHORT).show();
				doWaitAndNewGame();
			}
		}
	}
	
	public void saveMatch() 
	{
		matchDAO.createMatch(BluetoothHelper.getBtAdapter().getName(),
	                         BluetoothHelper.getBtAdapter().getAddress(),
	                         BluetoothHelper.getBtSocket().getRemoteDevice().getName(),
	                         BluetoothHelper.getBtSocket().getRemoteDevice().getAddress(),
	                         manager.player1MatchScore, manager.player2MatchScore);
	}
	
	private void setAnimations() 
	{
		oppAnim = new TranslateAnimation(0.0f, 40.0f, 0.0f, 0.0f); 
		oppAnim.setDuration(500);
		oppAnim.setRepeatMode(Animation.REVERSE);
		oppAnim.setRepeatCount(Animation.INFINITE);
		
		yourAnim = new TranslateAnimation(0.0f, 0.0f, -10.0f, 30.0f); 
		yourAnim.setDuration(500);
		yourAnim.setRepeatMode(Animation.REVERSE);
		yourAnim.setRepeatCount(Animation.INFINITE);
	}
	
	private void startOpponentTurnAnim()
	{
		yourTurn.clearAnimation();
		yourTurn.setVisibility(ImageView.INVISIBLE);
		opponentTurn.setVisibility(ImageView.VISIBLE);
		opponentTurn.startAnimation(oppAnim);
	}
	
	private void startYourTurnAnim()
	{
		opponentTurn.clearAnimation();
		opponentTurn.setVisibility(ImageView.INVISIBLE);
		yourTurn.setVisibility(ImageView.VISIBLE);
		yourTurn.startAnimation(yourAnim);
	}
	
	private void clearAnimations()
	{
		yourTurn.clearAnimation();
		yourTurn.setVisibility(ImageView.INVISIBLE);
		opponentTurn.clearAnimation();
		opponentTurn.setVisibility(ImageView.INVISIBLE);
	}
	
	@Override
	public void onBackPressed() 
	{
		exitAlert.show();
	}

	@Override
	public void onClick(View v) 
	{
		if (manager.playerTurn == 1 && !turnUsed)
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
						turnUsed = true;
						manager.playerTurn = 2;
						startOpponentTurnAnim();
						
						doSendInfo(0);
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
						turnUsed = true;
						manager.playerTurn = 2;
						startOpponentTurnAnim();
						
						doSendInfo(1);
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
						turnUsed = true;
						manager.playerTurn = 2;
						startOpponentTurnAnim();
						
						doSendInfo(2);
					}
					break;
				case R.id.btnTruco:
					if (manager.gameValue < 12)
						doSendInfo(7);
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
							       + manager.handPlayer1[0].suit.ordinal() + ","
							       + manager.handPlayer1[0].cardValue.ordinal() + ","
							       + manager.handPlayer1[1].suit.ordinal() + ","
							       + manager.handPlayer1[1].cardValue.ordinal() + ","
							       + manager.handPlayer1[2].suit.ordinal() + ","
							       + manager.handPlayer1[2].cardValue.ordinal() + ","
							       + manager.handPlayer2[0].suit.ordinal() + ","
							       + manager.handPlayer2[0].cardValue.ordinal() + ","
							       + manager.handPlayer2[1].suit.ordinal() + ","
							       + manager.handPlayer2[1].cardValue.ordinal() + ","
							       + manager.handPlayer2[2].suit.ordinal() + ","
							       + manager.handPlayer2[2].cardValue.ordinal() + ","
							       + manager.vira.suit.ordinal() + ","
							       + manager.vira.cardValue.ordinal() + ","
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
					Toast.makeText(HostGameActivity.this, "Erro de Conexão", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
				else // Sucesso
				{
					if (manager.playerTurn == 2)
						doReceiveInfo(); // Recebe carta do cliente
				}
			}
		};
		
		if (BluetoothHelper.getBtAdapter().isEnabled())
		{
			if (BluetoothHelper.getBtSocket().isConnected())
			{
				sendInitalInfo.execute();
			}
			else
			{
				Toast.makeText(HostGameActivity.this, "Erro de Conexão", Toast.LENGTH_LONG).show();
				BluetoothHelper.closeSocket();
				finish();
			}
		}
		else
		{
			Toast.makeText(HostGameActivity.this, "O Bluetooth Foi Desligado", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private void doSendInfo(final int sendCode) 
	{
		/*
		 * Dados que podem ser enviados:
		 * 0,1,2: referentes a posicao da carta jogada pelo host no vetor handPlayer1 do manager
		 * 7: pedido de truco
		 * 8: recusa do pedido de truco ou de aumento da aposta do cliente
		 * 9: aceitação do pedido de truco ou de aumento da aposta do cliente
		 * 10: aumento da aposta
		 */
		Log.i("doSendCardInfo", "Entrou");
		AsyncTask<Integer, Void, Boolean> sendInfo = new AsyncTask<Integer, Void, Boolean>() 
		{
			@Override
			protected Boolean doInBackground(Integer... params) 
			{
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
				if (result == false && !backPressed) // Falha
				{
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				}
				else
				{
					if (sendCode == 0 || sendCode == 1 || sendCode == 2) // No envio de cartas eh necessario fazer a verificacao abaixo
					{
						if (startedRound)
							doReceiveInfo(); // Se o host comeca o round, deve-se esperar pela carta ou pedido de truco do cliente
						else
							verifyWinner(); // Se o host termina o round, apos o envio da sua carta deve-se calcular o resultado
					}
					else // No envio de pedidos de truco e afins so eh necessario verificar se o host correu
					{
						if (sendCode == 8) // Correu do pedido de truco
						{
							manager.player2MatchScore += manager.gameValue;
							p2MatchScore.setText(String.valueOf(manager.player2MatchScore));
							newGame();
						}
						else
							doReceiveInfo();
					}
				}
			}
		};
		
		if (BluetoothHelper.getBtAdapter().isEnabled())
		{
			if (BluetoothHelper.getBtSocket().isConnected())
			{
				sendInfo.execute(sendCode);
			}
			else
			{
				Toast.makeText(HostGameActivity.this, "Erro de Conexão", Toast.LENGTH_LONG).show();
				BluetoothHelper.closeSocket();
				finish();
			}
		}
		else
		{
			Toast.makeText(HostGameActivity.this, "O Bluetooth Foi Desligado", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private void doReceiveInfo()
	{
		/*
		 * Dados que podem ser recebidos:
		 * 0,1,2: referentes a posicao da carta jogada pelo cliente no vetor handPlayer2 do manager
		 * 7: pedido de truco
		 * 8: recusa do pedido de truco ou de aumento da aposta do host
		 * 9: aceitação do pedido de truco ou de aumento da aposta do host
		 * 10: aumento da aposta
		 */
		AsyncTask<Void, Void, byte[]> receiveInfo = new AsyncTask<Void, Void, byte[]>() {
			
			@Override
			protected byte[] doInBackground(Void... params) 
			{
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
					Toast.makeText(HostGameActivity.this, "Falha de conexão. Jogo encerrado!", Toast.LENGTH_LONG).show();
					BluetoothHelper.closeSocket();
					finish();
				} 
				else // Sucesso
				{
					int codeReceived = -1;
					
					try 
					{
						String temp = new String(result, "UTF-8");
						String[] code = temp.split(",");
						codeReceived = Integer.parseInt(code[0]);
					} 
					catch (UnsupportedEncodingException e) 
					{
						Log.i(getClass().getName(), e.getMessage().toString());
						Toast.makeText(HostGameActivity.this, "Infelizmente ocorreu um erro. Jogo encerrado!", Toast.LENGTH_LONG).show();
						BluetoothHelper.closeSocket();
						finish();
					}

					if (codeReceived == 0 || codeReceived == 1 || codeReceived == 2) // Recebimento de cartas
					{
						clientCardIndex = codeReceived;
						setOpponentPlayingCard();
							
						if (startedRound)
						{
							verifyWinner();
						}
						else
						{
							manager.playerTurn = 1;
							startYourTurnAnim();
						}
					}
					else // Recebimento de pedidos de truco e afins
					{
						if (codeReceived == 7) // Pedido de truco do cliente
						{
							vibrator.vibrate(750);
							
							if (manager.gameValue == 1)
								trucoAlert.setTitle("Truco!");
							
							if (manager.gameValue == 3)
								trucoAlert.setTitle("Seis");
							
							if (manager.gameValue == 6)
								trucoAlert.setTitle("Nove");
							
							if (manager.gameValue == 9)
								trucoAlert.setTitle("Doze");
							
							trucoAlert.show();
						}
						else if (codeReceived == 8) // Cliente correu do pedido de truco ou de aumento da aposta feita pelo host
						{
							
							manager.player1MatchScore += manager.gameValue;
							p1MatchScore.setText(String.valueOf(manager.player1MatchScore));
							
							trucoResponseAlert.setTitle("Truco!");
							trucoResponseAlert.setMessage("O Adversário Recusou o Pedido de Truco.\nVocê Ganhou a Rodada Valendo: " + manager.gameValue + " Pontos!");
							trucoResponseAlert.show();
							newGame();
						}
						else if (codeReceived == 9) // Cliente aceitou o pedido de truco
						{
							manager.increaseGameValue();

							if (manager.gameValue == 3)
								trucoResponseAlert.setTitle("Três");
							
							if (manager.gameValue == 6)
								trucoResponseAlert.setTitle("Seis");
							
							if (manager.gameValue == 9)
								trucoResponseAlert.setTitle("Nove");
							
							if (manager.gameValue == 12)
								trucoResponseAlert.setTitle("Doze");
							
							trucoResponseAlert.setMessage("O Adversário Aceitou o Pedido de Truco.\nRodada Valendo: " + manager.gameValue + " Pontos!");
							trucoResponseAlert.show();
						}
					}
				}
				Log.i("doReceiveCardInfo", "Saiu");
			}
		};
		
		if (BluetoothHelper.getBtAdapter().isEnabled())
		{
			if (BluetoothHelper.getBtSocket().isConnected())
			{
				receiveInfo.execute();
			}
			else
			{
				Toast.makeText(HostGameActivity.this, "Erro de Conexão", Toast.LENGTH_LONG).show();
				BluetoothHelper.closeSocket();
				finish();
			}
		}
		else
		{
			Toast.makeText(HostGameActivity.this, "O Bluetooth Foi Desligado", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	private void doWaitAndRefreshPlayingCards()
	{
		AsyncTask<Void, Void, Void> waitAndRefreshPlayingCards = new AsyncTask<Void, Void, Void>() 
		{
			@Override
			protected Void doInBackground(Void... params) 
			{
				try 
				{
					Thread.sleep(2000);
				} 
				catch (InterruptedException e) 
				{
					Log.i(getClass().getName(), e.getMessage().toString());
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) 
			{
				super.onPostExecute(result);
				
				int resourceId = getResources().getIdentifier("decks_back", "drawable", getPackageName());
				playingCard.setImageDrawable(getResources().getDrawable(resourceId));
				opponentPlayingCard.setImageDrawable(getResources().getDrawable(resourceId));
				turnUsed = false;
			}
		};
		
		waitAndRefreshPlayingCards.execute();
	}
	
	private void doWaitAndNewGame()
	{
		AsyncTask<Void, Void, Void> waitAndRefreshPlayingCards = new AsyncTask<Void, Void, Void>() 
		{
			@Override
			protected Void doInBackground(Void... params) 
			{
				try 
				{
					Thread.sleep(3000);
				} 
				catch (InterruptedException e) 
				{
					Log.i(getClass().getName(), e.getMessage().toString());
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) 
			{
				super.onPostExecute(result);
				
				newGame();
			}
		};
		
		waitAndRefreshPlayingCards.execute();
	}
}