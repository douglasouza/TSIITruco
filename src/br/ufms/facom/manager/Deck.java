package br.ufms.facom.manager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck
{
	public List<Card> deck;
	
	public Deck()
	{
		deck = new ArrayList<Card>();
		Card card;
		
		for(CardValue cardValue: CardValue.values() )
		{
			for(Suit suit : Suit.values())
			{
				card = new Card(suit, cardValue);
				
				deck.add(card);
				
			}
		}
		
	}
	
	public void shuffleDeck()
	{
		Collections.shuffle(deck);
	}
	
	public void addCard(Card card)
	{
		deck.add(card);
	}
	
	public Card removeCard()
	{
		Card card = deck.remove(0);
		
		return card; 
	}
		
}
