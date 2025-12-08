
package Controleur;

import simulation.modele.simulation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class GestionController {

    private final GestionEnergie gestion = new GestionEnergie();

    @FXML private Label lblEtat;

    @FXML
    public void simulerUniteTemps() {
        try {
            gestion.simulerUniteTemps();
            lblEtat.setText(gestion.getEtat());
        } catch (EnergieException e) {
        }
    }
}
