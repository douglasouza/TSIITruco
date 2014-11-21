package br.ufms.facom.manager;

import java.util.Random;

public class TrucoManager {
	
	public static final String P1_WINNER = "P1WIN";
	public static final String P2_WINNER = "P2WIN";
	public static final String DRAW = "DRAW";
	
	public Deck deck;
	public Card vira;
	public int manilha;
	
	public String firstRoundResult;
	public String secondRoundResult;
	public String thirdRoundResult;
	
	public int scorePlayer1;
	public int scorePlayer2;
	
	public int bonus;
	
	public int playerTurn;
	
	public Card [] handPlayer1;
	public Card [] handPlayer2;
	
	public int usedCardPlayer1;
	public int usedCardPlayer2;
	
	
	public TrucoManager()
	{
		
		usedCardPlayer1 = 0;
		usedCardPlayer2 = 0;
		
		bonus = 0;
		deck = new Deck();
		deck.shuffleDeck();
		scorePlayer1 = scorePlayer2 = 0;
		handPlayer1 = new Card[3];
		for(int i = 0; i < 3; i++)
		{
			handPlayer1[i] = deck.removeCard();	
		}
		
		handPlayer2 = new Card[3];
		
		for(int i = 0; i < 3; i++)
		{
			handPlayer2[i] = deck.removeCard();	
		}
		
		vira = deck.removeCard();
		//caso o vira seja a carta mais valiosa
		if(vira.cardValue.ordinal() == 9)
		{
			manilha = 0;
		}
		else
		{
			manilha = (vira.cardValue.ordinal())+ 1;
		}
		
		Random rand = new Random();
		
		playerTurn = rand.nextInt(2);
	}
	
	public void returnCards()
	{
		bonus = 0;
		
		deck.addCard(vira);
		for(int i = 0; i < 3; i++)
		{
			deck.addCard(handPlayer1[i]);
			deck.addCard(handPlayer2[i]);
		}
		
		usedCardPlayer1 = 0;
		usedCardPlayer2 = 0;
		
		deck.shuffleDeck();
	}
	
	public void newTurn()
	{
		handPlayer1 = new Card[3];
		
		for(int i = 0; i < 3; i++)
		{
			handPlayer1[i] = deck.removeCard();	
		}
		
		handPlayer2 = new Card[3];
		
		for(int i = 0; i < 3; i++)
		{
			handPlayer2[i] = deck.removeCard();	
		}
		
		vira = deck.removeCard();
	}
	
	public void giveBonus()
	{
		bonus +=3;
	}
	
	// Verifica se já tem um vencedor da rodada (game) no segundo round
	public int secondRoundWinner()
	{
		if (firstRoundResult.equals(DRAW)) // Empate no primeiro round, quem ganhar o segundo, ganha a rodada
		{
			if (secondRoundResult.equals(P1_WINNER))
				return 1;
			else if (secondRoundResult.equals(P2_WINNER))
				return 2;				
		}
		
		if (secondRoundResult.equals(DRAW)) // Empate no segundo round, quem ganhou o primeiro, ganha a rodada
		{
			if (firstRoundResult.equals(P1_WINNER))
				return 1;
			else if (firstRoundResult.equals(P2_WINNER))
				return 2;				
		}
		
		// Se algum jogador ganhou dois rounds, ganhou a rodada
		if (firstRoundResult.equals(P1_WINNER) && secondRoundResult.equals(P1_WINNER))
			return 1;
		else if (firstRoundResult.equals(P2_WINNER) && secondRoundResult.equals(P2_WINNER))
			return 2;
		
		return 0; // Ainda não houve vencedor
	}
	
	// Se não houve vencedor no segundo round, verifica-se novamente no fim da rodada, chamando esse método
	public int gameResult()
	{
		/*
		 * Se esse método precisou ser chamado, então houve dois empates, 
		 * ou cada jogador venceu uma. Em suma o jogo está empatado.
		 * Quem vencer o terceiro round, ganha a rodada.
		 */
		if (thirdRoundResult.equals(P1_WINNER))
			return 1;
		else if (thirdRoundResult.equals(P2_WINNER))
			return 2;
		
		return 0; // Três empates, ninguém ganha
	}
	
	public int compareCards(int cardIndexPlayer1, int cardIndexPlayer2)
	{
		if
		(
				handPlayer1[cardIndexPlayer1].cardValue.ordinal() == manilha
				&&
				handPlayer2[cardIndexPlayer2].cardValue.ordinal() == manilha
		)
		{
			if
			(
					handPlayer1[cardIndexPlayer1].suit.ordinal()
					>
					handPlayer2[cardIndexPlayer2].suit.ordinal()
					
			)
			{
				scorePlayer1 = 3 + bonus;
				return 1;
			}
			else
			{
				scorePlayer2 = 3 + bonus;
				return 2;
			}
		}
		else if(handPlayer1[cardIndexPlayer1].cardValue.ordinal() == manilha)
		{
			scorePlayer1 = 3 + bonus;
			return 1;
		}
		else if(handPlayer2[cardIndexPlayer2].cardValue.ordinal() == manilha)
		{
			scorePlayer2 = 3 + bonus;
			return 2;
		}
		else
		{
			if
			(
					handPlayer1[cardIndexPlayer1].cardValue.ordinal()
					>
					handPlayer2[cardIndexPlayer2].cardValue.ordinal()
					
			)
			{
				scorePlayer1 = 3 + bonus;
				return 1;
			}
			else if
			(
					handPlayer1[cardIndexPlayer1].cardValue.ordinal()
					<
					handPlayer2[cardIndexPlayer2].cardValue.ordinal()
			)
			{
				scorePlayer2 = 3 + bonus;
				return 2;
			}
			//empate
			else
			{
				return 0;
			}
		}
	}
	
	
}
