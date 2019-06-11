package it.polito.tdp.seriea;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {
	
	Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ChoiceBox<Team> boxSquadra;

    @FXML
    private Button btnSelezionaSquadra;

    @FXML
    private Button btnTrovaAnnataOro;

    @FXML
    private Button btnTrovaCamminoVirtuoso;

    @FXML
    private TextArea txtResult;

    @FXML
    void doSelezionaSquadra(ActionEvent event) {
    	Team t = boxSquadra.getValue() ;
    	
    	if(t==null) {
    		txtResult.appendText("Devi selezionare una squadra\n");
    		return ;
    	}
    	
    	Map<Season, Integer> punteggi = model.calcolaPunteggi(t) ;
    	
    	txtResult.clear();
    	
    	for(Season s: punteggi.keySet()) {
    		txtResult.appendText(String.format("%s: %d\n", s.getDescription(), punteggi.get(s)) );
    	}
    }

    @FXML
    void doTrovaAnnataOro(ActionEvent event) {
    	
    	Season annata = model.calcolaAnnataDOro() ;
    	txtResult.appendText(String.format("Annata d'oro: %s\n", annata.getDescription()));

    }

    @FXML
    void doTrovaCamminoVirtuoso(ActionEvent event) {
    	List<Season> percorso = model.camminoVirtuoso() ;
    	txtResult.appendText(percorso.toString());

    }

    public void setModel(Model m) {
    	this.model = m ;
    	
    	boxSquadra.getItems().clear();
    	boxSquadra.getItems().addAll(model.getSquadre()) ;
    }
    
    @FXML
    void initialize() {
        assert boxSquadra != null : "fx:id=\"boxSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnSelezionaSquadra != null : "fx:id=\"btnSelezionaSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaAnnataOro != null : "fx:id=\"btnTrovaAnnataOro\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaCamminoVirtuoso != null : "fx:id=\"btnTrovaCamminoVirtuoso\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";

    }
}
