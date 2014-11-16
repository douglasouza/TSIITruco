package br.ufms.facom.manager;

import java.util.Random;

public class TrucoManager {
	
	public Deck deck;
	public Card vira;
	public int manilha;
	
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
