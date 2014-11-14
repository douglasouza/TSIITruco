package br.ufms.facom.activity;

import br.ufms.facom.truco.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button btnCreateGame;
	private Button btnFindGame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}

	private void init() 
	{
		btnCreateGame = (Button) findViewById(R.id.btnCreateGame);
		btnFindGame = (Button) findViewById(R.id.btnFindGame);
		
		btnCreateGame.setOnClickListener(this);
		btnFindGame.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btnCreateGame:
				Log.i(getClass().getName(), "Create Game!");
				Intent lobbyIntent = new Intent(MainActivity.this, LobbyActivity.class);
				startActivity(lobbyIntent);
				break;
			case R.id.btnFindGame:
				Log.i(getClass().getName(), "Find Game!");
				Intent awaitIntent = new Intent(MainActivity.this, JoinGameActivity.class);
				startActivity(awaitIntent);
				break;
		}
	}
}
