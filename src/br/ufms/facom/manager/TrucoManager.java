package br.ufms.facom.manager;

import java.util.Random;

public class TrucoManager {
	
	public static final String P1_WINNER = "P1WIN";
	public static final String P2_WINNER = "P2WIN";
	public static final String DRAW = "DRAW";
	
	public static final int GAME_P1_WINNER = 1;
	public static final int GAME_P2_WINNER = 2;
	public static final int GAME_DRAW = 0;
	public static final int NO_WINNER_YET = -1;
	
	public static final int ROUND_P1_WINNER = 1;
	public static final int ROUND_P2_WINNER = 2;
	public static final int ROUND_DRAW = 0;
	
	/*
	 * Match = Partida
	 * Game = Rodada
	 * Round = 1/3 da rodada
	 */
	
	public Deck deck;
	public Card vira;
	public int manilha;
	
	// Resultados de cada round
	public int firstRoundResult; 
	public int secondRoundResult;
	public int thirdRoundResult;
	
	// Pontuacao de cada jogador dentro da rodada
	public int player1GameScore;
	public int player2GameScore;
	
	// Pontuacao de cada jogador dentro da partida
	public int player1MatchScore;
	public int player2MatchScore;
	
	// Valor dessa rodada com relacao a partida. Default = 1. Incrementado com chamadas de truco
	public int gameValue;
	
	// Indica de quem é a vez de jogar
	public int playerTurn;
	
	// Cartas de cada jogador
	public Card [] handPlayer1;
	public Card [] handPlayer2;
	
	// Nova Partida
	public TrucoManager()
	{
		deck = new Deck();
		deck.shuffleDeck();
		
		player1MatchScore = 0;
		player2MatchScore = 0;
	}
	
	// Nova Rodada
	public void newGame()
	{
		gameValue = 1;
		
		player1GameScore = 0;
		player1GameScore = 0;
		
		handPlayer1 = new Card[3];
		handPlayer2 = new Card[3];
		
		for(int i = 0; i < 3; i++)
		{
			handPlayer1[i] = deck.removeCard();
			handPlayer2[i] = deck.removeCard();	
		}

		vira = deck.removeCard();
		
		// Caso o vira seja a carta mais valiosa (3)
		if(vira.cardValue.ordinal() == 9)
		{
			manilha = 0;
		}
		else
		{
			manilha = (vira.cardValue.ordinal())+ 1;
		}
		
		// Define quem começa a rodada aleatoriamente
		Random rand = new Random();
		playerTurn = rand.nextInt(2);
	}
	
	// Aumenta o valor da rodada. (Truco)
	public void increaseGameValue()
	{
		if (gameValue == 1)
			gameValue += 2;
		else
			gameValue += 3;
	}
	
	// Verifica se já tem um vencedor da rodada (game) no segundo round
	public int secondRoundWinner()
	{
		if (firstRoundResult == ROUND_DRAW) // Empate no primeiro round, quem ganhar o segundo, ganha a rodada
		{
			if (secondRoundResult == ROUND_P1_WINNER)
			{
				player1MatchScore += 1;
				return GAME_P1_WINNER;
			}
			else if (secondRoundResult == ROUND_P2_WINNER)
			{
				player2MatchScore += 1;
				return GAME_P2_WINNER;
			}
		}
		
		if (secondRoundResult == ROUND_DRAW) // Empate no segundo round, quem ganhou o primeiro, ganha a rodada
		{
			if (firstRoundResult == ROUND_P1_WINNER)
			{
				player1MatchScore += 1;
				return GAME_P1_WINNER;
			}
			else if (firstRoundResult == ROUND_P2_WINNER)
			{
				player2MatchScore += 1;
				return GAME_P2_WINNER;
			}
		}
		
		// Se algum jogador ganhou dois rounds, ganhou a rodada
		if (firstRoundResult == ROUND_P1_WINNER && secondRoundResult == ROUND_P1_WINNER)
		{
			player1MatchScore += 1;
			return GAME_P1_WINNER;
		}
		else if (firstRoundResult == ROUND_P2_WINNER && secondRoundResult == ROUND_P2_WINNER)
		{
			player2MatchScore += 1;
			return GAME_P2_WINNER;
		}
		
		return NO_WINNER_YET; // Ainda não houve vencedor
	}
	
	// Se não houve vencedor no segundo round, verifica-se novamente no fim da rodada, chamando esse método
	public int gameResult()
	{
		/*
		 * Se esse método precisou ser chamado, então houve dois empates, 
		 * ou cada jogador venceu uma. Em suma o jogo está empatado.
		 * Quem vencer o terceiro round, ganha a rodada.
		 */
		if (thirdRoundResult == ROUND_P1_WINNER)
		{
			player1MatchScore += 1;
			return GAME_P1_WINNER;
		}
		else if (thirdRoundResult == ROUND_P2_WINNER)
		{
			player2MatchScore += 1;
			return GAME_P2_WINNER;
		}
		
		return GAME_DRAW; // Três empates, ninguém ganha
	}
	
	// Verifica quem venceu o round baseado nas cartas de cada jogador
	public int compareCards(int cardIndexPlayer1, int cardIndexPlayer2, int roundCount)
	{
		if (handPlayer1[cardIndexPlayer1].cardValue.ordinal() == manilha && handPlayer2[cardIndexPlayer2].cardValue.ordinal() == manilha)
		{
			if (handPlayer1[cardIndexPlayer1].suit.ordinal() > handPlayer2[cardIndexPlayer2].suit.ordinal())
			{
				if (roundCount == 1)
					firstRoundResult = ROUND_P1_WINNER;
				else if (roundCount == 2)
					secondRoundResult = ROUND_P1_WINNER;
				else if (roundCount == 3)
					thirdRoundResult = ROUND_P1_WINNER;
				
				player1GameScore += 1;
				return ROUND_P1_WINNER;
			}
			else
			{
				if (roundCount == 1)
					firstRoundResult = ROUND_P2_WINNER;
				else if (roundCount == 2)
					secondRoundResult = ROUND_P2_WINNER;
				else if (roundCount == 3)
					thirdRoundResult = ROUND_P2_WINNER;
				
				player2GameScore += 1;
				return ROUND_P2_WINNER;
			}
		}
		else if (handPlayer1[cardIndexPlayer1].cardValue.ordinal() == manilha)
		{
			if (roundCount == 1)
				firstRoundResult = ROUND_P1_WINNER;
			else if (roundCount == 2)
				secondRoundResult = ROUND_P1_WINNER;
			else if (roundCount == 3)
				thirdRoundResult = ROUND_P1_WINNER;
			
			player1GameScore += 1;
			return ROUND_P1_WINNER;
		}
		else if (handPlayer2[cardIndexPlayer2].cardValue.ordinal() == manilha)
		{
			if (roundCount == 1)
				firstRoundResult = ROUND_P2_WINNER;
			else if (roundCount == 2)
				secondRoundResult = ROUND_P2_WINNER;
			else if (roundCount == 3)
				thirdRoundResult = ROUND_P2_WINNER;
			
			player2GameScore += 1;
			return ROUND_P2_WINNER;
		}
		else
		{
			if (handPlayer1[cardIndexPlayer1].cardValue.ordinal() > handPlayer2[cardIndexPlayer2].cardValue.ordinal())
			{
				if (roundCount == 1)
					firstRoundResult = ROUND_P1_WINNER;
				else if (roundCount == 2)
					secondRoundResult = ROUND_P1_WINNER;
				else if (roundCount == 3)
					thirdRoundResult = ROUND_P1_WINNER;
				
				player1GameScore += 1;
				return ROUND_P1_WINNER;
			}
			else if (handPlayer1[cardIndexPlayer1].cardValue.ordinal() < handPlayer2[cardIndexPlayer2].cardValue.ordinal())
			{
				if (roundCount == 1)
					firstRoundResult = ROUND_P2_WINNER;
				else if (roundCount == 2)
					secondRoundResult = ROUND_P2_WINNER;
				else if (roundCount == 3)
					thirdRoundResult = ROUND_P2_WINNER;
				
				player2GameScore += 1;
				return ROUND_P2_WINNER;
			}
			//empate
			else
			{
				if (roundCount == 1)
					firstRoundResult = ROUND_DRAW;
				else if (roundCount == 2)
					secondRoundResult = ROUND_DRAW;
				else if (roundCount == 3)
					thirdRoundResult = ROUND_DRAW;
				
				return ROUND_DRAW;
			}
		}
	}
}
