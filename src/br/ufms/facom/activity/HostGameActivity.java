package br.ufms.facom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import br.ufms.facom.truco.R;

public class HostGameActivity extends Activity implements OnClickListener{
	
	//private AnimationSet animSet;
	private ImageView card1;
	private ImageView card2;
	private ImageView card3;
	private ImageView opponentCard1;
	private ImageView opponentCard2;
	private ImageView opponentCard3;
	private ImageView playingCard;
	private ImageView opponentPlayingCard;
	private ImageView vira;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_game);
		
		init();
	}

	private void init() 
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
		
		card1.setOnClickListener(this);
		card2.setOnClickListener(this);
		card3.setOnClickListener(this);
		opponentCard1.setOnClickListener(this);
		opponentCard2.setOnClickListener(this);
		opponentCard3.setOnClickListener(this);
		playingCard.setOnClickListener(this);
		opponentPlayingCard.setOnClickListener(this);
		vira.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{
			case R.id.imageViewCard1:
				break;
			case R.id.imageViewCard2:
				break;
			case R.id.imageViewCard3:
				break;
		}
	}
	
//	private void setAnimation() 
//	{
//		Animation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
//		alphaAnim.setDuration(600);
//		
//		Animation translateAnim = new TranslateAnimation(0.0f, 0.0f, 200.0f, 0.0f);
//		translateAnim.setDuration(600);
//		
//		animSet = new AnimationSet(true);
//		animSet.addAnimation(alphaAnim);
//		animSet.addAnimation(translateAnim);
//	}
}
