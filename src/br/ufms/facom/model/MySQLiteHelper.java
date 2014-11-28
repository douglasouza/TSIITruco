package br.ufms.facom.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper
{
	
	// colunas
	public static final String TABLE_MATCHES = "matches";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_BLUETOOTH_HOST_NAME = "host_name";
	public static final String COLUMN_BLUETOOTH_HOST_ADRESS = "host_adress";
	public static final String COLUMN_BLUETOOTH_CLIENT_NAME = "client_name";
	public static final String COLUMN_BLUETOOTH_CLIENT_ADRESS = "client_adress";
	public static final String COLUMN_HOST_SCORE = "host_score";
	public static final String COLUMN_CLIENT_SCORE = "client_score";
	
	// arquivo que armazenará o banco de dados SQLite
	private static final String DATABASE_NAME = "truco.db";
	
	// versão do Banco de Dados
	private static final int DATABASE_VERSION = 1;
	
	// SQL de criação da Tabela
	private static final String DATABASE_CREATE = "create table " + TABLE_MATCHES 
						                        + "( " + COLUMN_ID + " integer primary key autoincrement, " 
			                                    + COLUMN_BLUETOOTH_HOST_NAME + " text not null, " 
			                                    + COLUMN_BLUETOOTH_HOST_ADRESS + " text not null, "
			                                    + COLUMN_BLUETOOTH_CLIENT_NAME + " text not null, " 
			                                    + COLUMN_BLUETOOTH_CLIENT_ADRESS + " text not null, "
			                                    + COLUMN_HOST_SCORE + " integer not null, "
			                                    + COLUMN_CLIENT_SCORE + " integer not null);";
	
	public MySQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	// invocado quando o aplicação é instalada e executa pela primeira vez
	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE);
	}
	
	// invocado quando a aplicação muda de versão
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
		onCreate(db);
	}
	
}
