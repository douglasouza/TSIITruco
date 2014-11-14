package br.ufms.facom.activity;

import android.app.Activity;
import android.os.Bundle;
import br.ufms.facom.truco.R;

public class ClientGameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_game);
	}
}
