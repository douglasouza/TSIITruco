package br.ufms.facom.manager;

public class Card
{
	public Suit suit;
	
	public CardValue cardValue;
	
	public String fileName;
	
	public Card(Suit suit, CardValue cardValue)
	{
		this.suit = suit;
		this.cardValue = cardValue;
		
		fileName = cardValue.toString() + "_of_" + suit.toString();
	}
	
}
