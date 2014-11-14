package br.ufms.facom.manager;

public class TrucoManager {
	
	protected Deck deck;
	protected Card vira;
	protected int manilha;
	
	protected int scorePlayer1;
	protected int scorePlayer2;
	
	protected int bonus;
	
	protected Card [] handPlayer1;
	protected Card [] handPlayer2;
	
	protected Boolean [] usedCardPlayer1;
	
	protected Boolean [] usedCardPlayer2;
	
	
	public TrucoManager()
	{
		
		usedCardPlayer1 = new Boolean[3];
		usedCardPlayer2 = new Boolean[3];
		
		for(int i = 0; i < 3; i++)
		{
			usedCardPlayer1[i] = false;
			usedCardPlayer2[i] = false;
		}
		
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
	}
	
	public void returnCards()
	{
		bonus = 0;
		
		deck.addCard(vira);
		for(int i = 0; i < 3; i++)
		{
			deck.addCard(handPlayer1[i]);
			deck.addCard(handPlayer2[i]);
			usedCardPlayer1[i] = false;
			usedCardPlayer2[i] = false;
		}
		
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
		
		if(usedCardPlayer1[cardIndexPlayer1].equals(true) || usedCardPlayer2[cardIndexPlayer2].equals(true))
		{
			return -1;
		}
		
		usedCardPlayer1[cardIndexPlayer1] = true;
		usedCardPlayer2[cardIndexPlayer2] = true;
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
