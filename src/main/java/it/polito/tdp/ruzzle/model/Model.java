package it.polito.tdp.ruzzle.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.ruzzle.db.DizionarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Model {
	private final int SIZE = 4;
	private Board board ;
	private List<String> dizionario ;
	private StringProperty statusText ;

	public Model() {
		this.statusText = new SimpleStringProperty() ;
		
		this.board = new Board(SIZE);
		DizionarioDAO dao = new DizionarioDAO() ;
		this.dizionario = dao.listParola() ;  //parole estratte dal dao
		statusText.set(String.format("%d parole lette", this.dizionario.size())) ;
	
	}
	
	public void reset() {
		this.board.reset() ;
		this.statusText.set("Board Reset");
	}

	public Board getBoard() {
		return this.board;
	}

	public final StringProperty statusTextProperty() {
		return this.statusText;
	}
	

	public final String getStatusText() {
		return this.statusTextProperty().get();
	}
	

	public final void setStatusText(final String statusText) {
		this.statusTextProperty().set(statusText);
	}

	
	//procedura ricorsiva
	
	public List<Pos> trovaParola(String parola) {
		
		//data una parola, guardo se è presente nella griglia
		
		//posizione della lettera di partenza confrontata con quelle nella griglia
		
		for(Pos p: board.getPositions()) {
			
			if(parola.charAt(0)== board.getCellValueProperty(p).get().charAt(0)) {
				
				List<Pos> percorso= new ArrayList<Pos>();
				percorso.add(p);
				
				if(cerca(parola, 1, percorso)) {
					
					//parola è stata trovata, ritorno il percorso
					
					return percorso;
				}
				
				
			}
		}
		
		
		return null;
	}

	private boolean cerca(String parola, int livello, List<Pos> percorso) {
		
		//caso terminale
		if(livello ==parola.length()) {
			
			return true;
		}
		
		//recupero le parole adiacenti
		
		Pos ultima= percorso.get(percorso.size()-1);
		List<Pos> adiacenti= board.getAdjacencies(ultima);
		
		//per ognuna di queste, le controllo per vedere se posso creare altre parole es. PI-PPO --> P terza + se non è già stata usata
		
		for(Pos p: adiacenti) {
			if(!percorso.contains(p) && parola.charAt(livello) == board.getCellValueProperty(p).get().charAt(0)) {
				
				percorso.add(p);
				if(cerca(parola, livello+1, percorso)) return true; //faccio ricorsione e esco se la trovo 
				
				percorso.remove(percorso.size()-1); //backtracking
				
				
			}
			
		}
		
		
		
		return false;
	}

	public List<String> trovaTutte() {
		
		//prende parole dizionario e per ognuna di essere fa il trova
		
		List<String> tutte= new ArrayList<String>();
		
		for(String parola: this.dizionario) {
			
			parola=parola.toUpperCase();
			
			if(parola.length()>1) {
			
				if(this.trovaParola(parola) != null) //percorso diverso da nullo --> parola c'è
					tutte.add(parola);
			}
		}
		
		return tutte;
	}
	
	public boolean parolaValida(String parola) {
		
		if(parola.length()>1 && this.dizionario.contains(parola.toLowerCase()))
			return true;
		
		return false;
	}
	

}
