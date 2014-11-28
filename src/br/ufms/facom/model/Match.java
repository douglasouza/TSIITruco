package br.ufms.facom.model;

public class Match {
	
	private long id;
	private String hostName;
	private String hostAdress;
	private String clientName;
	private String clientAdress;
	private int hostScore;
	private int clientScore;


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getHostAdress() {
		return hostAdress;
	}
	public void setHostAdress(String hostAdress) {
		this.hostAdress = hostAdress;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientAdress() {
		return clientAdress;
	}
	public void setClientAdress(String clientAdress) {
		this.clientAdress = clientAdress;
	}
	public int getHostScore() {
		return hostScore;
	}
	public void setHostScore(int hostScore) {
		this.hostScore = hostScore;
	}
	public int getClientScore() {
		return clientScore;
	}
	public void setClientScore(int clientScore) {
		this.clientScore = clientScore;
	}

	@Override
	public String toString() {
		return "Match " + id + ":\n" 
	         + hostName + " - " + hostAdress + "\n" 
			 + clientName + " - " + clientAdress + "\n"
			 + "Score: " + hostScore + "-" + clientScore;
	}
}