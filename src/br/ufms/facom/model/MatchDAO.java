package br.ufms.facom.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class MatchDAO
{
	
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
	{ MySQLiteHelper.COLUMN_ID, 
	  MySQLiteHelper.COLUMN_BLUETOOTH_HOST_NAME, MySQLiteHelper.COLUMN_BLUETOOTH_HOST_ADRESS,
	  MySQLiteHelper.COLUMN_BLUETOOTH_CLIENT_NAME, MySQLiteHelper.COLUMN_BLUETOOTH_CLIENT_ADRESS,
	  MySQLiteHelper.COLUMN_HOST_SCORE, MySQLiteHelper.COLUMN_CLIENT_SCORE };
	
	public MatchDAO(Context context)
	{
		dbHelper = new MySQLiteHelper(context);
	}
	
	// abre conexao com o banco de dados. Abre para a escrita
	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}
	
	// fecha conexao com o banco de dados
	public void close()
	{
		dbHelper.close();
	}
	
	// criar uma tarefa
	public Match createMatch(String hostName, String hostAdress, String clientName, String clientAdress, int hostScore, int clientScore)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_BLUETOOTH_HOST_NAME, hostName);
		values.put(MySQLiteHelper.COLUMN_BLUETOOTH_HOST_ADRESS, hostAdress);
		values.put(MySQLiteHelper.COLUMN_BLUETOOTH_CLIENT_NAME, clientName);
		values.put(MySQLiteHelper.COLUMN_BLUETOOTH_CLIENT_ADRESS, clientAdress);
		values.put(MySQLiteHelper.COLUMN_HOST_SCORE, hostScore);
		values.put(MySQLiteHelper.COLUMN_CLIENT_SCORE, clientScore);
		long insertId = database.insert(MySQLiteHelper.TABLE_MATCHES, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Match newMatch = cursorToMatch(cursor);
		cursor.close();
		return newMatch;
	}
	
	// deletar uma tarefa
	public void deleteMatch(Match match)
	{
		long id = match.getId();
		System.out.println("Task deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_MATCHES, MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	// listar todas os jogos
	public List<Match> getAllMatches()
	{
		List<Match> matches = new ArrayList<Match>();
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MATCHES, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			Match match = cursorToMatch(cursor);
			matches.add(match);
			cursor.moveToNext();
		}
		
		cursor.close();
		return matches;
	}
	
	// transformar um cursor em um objeto da classe Task
	private Match cursorToMatch(Cursor cursor)
	{
		Match match = new Match();
		match.setId(cursor.getLong(0));
		match.setHostName(cursor.getString(1));
		match.setHostAdress(cursor.getString(2));
		match.setClientName(cursor.getString(3));
		match.setClientAdress(cursor.getString(4));
		match.setHostScore(cursor.getInt(5));
		match.setClientScore(cursor.getInt(6));
		return match;
	}
}
