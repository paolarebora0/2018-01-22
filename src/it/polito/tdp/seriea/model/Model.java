package it.polito.tdp.seriea.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {

	private SerieADAO dao;
	private List<Team> squadre;
	private List<Season> stagioni;
	
	private Map<Integer, Season> stagioniIdMap;
	private Map<String, Team> squadreIdMap;	
	private Map<Season, Integer> punteggi;
	
	private Graph<Season, DefaultWeightedEdge> grafo;
	private List<Season> percorsoBest;
	private int deltaPesi;
	
	public Model() {
		dao = new SerieADAO();
		squadre = dao.listTeams();
		stagioni = dao.listAllSeasons();
		
		squadreIdMap = new HashMap<String, Team>();
		for(Team t: squadre)
			this.squadreIdMap.put(t.getTeam(), t);
		
		stagioniIdMap = new HashMap<Integer, Season>();
		for(Season s: stagioni) 
			this.stagioniIdMap.put(s.getSeason(), s);
		
	}
	public List<Team> listAllTeams() {
		return squadre;		
	}
	
	public Map<Season, Integer> calcolaPunteggi(Team squadra) {
		
		punteggi = new HashMap<Season, Integer>();
		
		List<Match> partite = dao.listMatchesForTeam(squadra, stagioniIdMap, squadreIdMap);
		
		for (Match m: partite) {
			
			Season stagione = m.getSeason();
			
			int punti = 0;
			
			if(m.getFtr().equals("D")) {
				punti = 1;
			} 
			else {
				if((m.getHomeTeam().equals(squadra) && m.getFtr().equals("H")) ||
						(m.getAwayTeam().equals(squadra) && m.getFtr().equals("A"))) {
					punti = 3;
				}
			}
			
			Integer attuale = punteggi.get(stagione);
			if(attuale == null)
				attuale = 0;
			punteggi.put(stagione, attuale+punti);
		}
		
		return punteggi;
	}
	
	public Season trovaAnnataDOro() {
		
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, punteggi.keySet());
		
		for(Season s1: punteggi.keySet()) {
			for(Season s2: punteggi.keySet()) {
				if(!s1.equals(s2)) {
					int punti1 = punteggi.get(s1);
					int punti2 = punteggi.get(s2);
					if(punti1>punti2) {
						Graphs.addEdge(grafo, s2, s1, (punti1-punti2));
					} else {
						Graphs.addEdge(grafo, s1, s2, (punti2-punti1));
					}
				}
			}
		}
		
		//Calcolo annata d'oro
		if(grafo.vertexSet().isEmpty()) {
			deltaPesi = 0;
			return null;
		} else if (grafo.vertexSet().size() == 1) {
			this.deltaPesi = 0;
			Season unica = grafo.vertexSet().iterator().next();
			return unica;
		} else {
			
			Season migliore = null;
			int max = 0;
			for(Season s: grafo.vertexSet()) {
				int valore = pesoStagione(s);
				if(valore > max) {
					max = valore;
					migliore = s;
				}
			}
			
			this.deltaPesi = max;
			return migliore;
		}
		
	}
	
	private int pesoStagione(Season s) {
		int somma = 0;
		
		for(DefaultWeightedEdge e:grafo.incomingEdgesOf(s)) {
			somma = somma + (int) grafo.getEdgeWeight(e);
		}
		
		for(DefaultWeightedEdge e:grafo.outgoingEdgesOf(s)) {
			somma = somma - (int) grafo.getEdgeWeight(e);
		}
		return somma;
	}
	public int getDeltaPesi() {
		
		return deltaPesi;
	}
}
