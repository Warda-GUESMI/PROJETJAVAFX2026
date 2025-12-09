package vue;



import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Controleur.ControleurSource;
import simulation.modele.source.*;

/**
 * Interface graphique pour gérer les sources d'énergie.
 */
public class VueGestionSource extends Stage {
    
    private final ControleurSource controleur;
    
    // Composants graphiques
    private TableView<SourceEnergie> tableauSources;
    private ComboBox<String> comboTypeSource;
    private TextField txtSurface, txtRendement, txtPuissanceNominale, txtVitesseVent;
    private TextField txtCapaciteMax, txtNiveauInitial, txtEfficacite;
    private VBox panneauParametres;
    
    /**
     * Constructeur de la vue.
     * @param controleur Le contrôleur associé
     */
    public VueGestionSource(ControleurSource controleur) {
        this.controleur = controleur;
        initialiserInterface();
        this.setTitle("Gestion des Sources d'Énergie");
    }
    
    /**
     * Initialise tous les composants de l'interface.
     */
    private void initialiserInterface() {
        BorderPane racine = new BorderPane();
        racine.setPadding(new Insets(10));
        
        // Partie supérieure : Tableau des sources
        racine.setTop(creerTableauSources());
        
        // Partie centrale : Formulaire d'ajout
        racine.setCenter(creerFormulaireAjout());
        
        // Partie inférieure : Boutons d'action
        racine.setBottom(creerPanneauBoutons());
        
        Scene scene = new Scene(racine, 800, 600);
        this.setScene(scene);
    }
    
    /**
     * Crée le tableau affichant les sources d'énergie.
     * @return Le nœud contenant le tableau
     */
    private VBox creerTableauSources() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        
        Label titre = new Label("Liste des Sources d'Énergie");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        tableauSources = new TableView<>();
        
        // Colonnes du tableau
        TableColumn<SourceEnergie, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));
        
        TableColumn<SourceEnergie, Double> colCapacite = new TableColumn<>("Capacité");
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        
        TableColumn<SourceEnergie, Double> colProduction = new TableColumn<>("Production Actuelle");
        colProduction.setCellValueFactory(new PropertyValueFactory<>("production"));
        
        tableauSources.getColumns().addAll(colType, colCapacite, colProduction);
        
        conteneur.getChildren().addAll(titre, tableauSources);
        return conteneur;
    }
    
    /**
     * Crée le formulaire pour ajouter une nouvelle source.
     * @return Le nœud contenant le formulaire
     */
    private VBox creerFormulaireAjout() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1;");
        
        Label titre = new Label("Ajouter une Source d'Énergie");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Sélection du type de source
        HBox ligneType = new HBox(10);
        Label lblType = new Label("Type de source :");
        comboTypeSource = new ComboBox<>();
        comboTypeSource.getItems().addAll("Panneau Solaire", "Éolienne", "Batterie");
        comboTypeSource.setOnAction(e -> afficherParametresSelonType());
        ligneType.getChildren().addAll(lblType, comboTypeSource);
        
        // Panneau dynamique pour les paramètres spécifiques
        panneauParametres = new VBox(10);
        panneauParametres.setPadding(new Insets(10));
        
        conteneur.getChildren().addAll(titre, ligneType, panneauParametres);
        return conteneur;
    }
    
    /**
     * Affiche les champs de saisie selon le type de source sélectionné.
     * Utilise une expression Lambda pour gérer l'événement.
     */
    private void afficherParametresSelonType() {
        panneauParametres.getChildren().clear();
        
        String typeSelectionne = comboTypeSource.getValue();
        
        if (typeSelectionne == null) return;
        
        switch (typeSelectionne) {
            case "Panneau Solaire":
                txtSurface = new TextField();
                txtRendement = new TextField();
                txtPuissanceNominale = new TextField();
                
                panneauParametres.getChildren().addAll(
                    new Label("Surface (m²) :"), txtSurface,
                    new Label("Rendement (0-1) :"), txtRendement,
                    new Label("Puissance nominale (kW) :"), txtPuissanceNominale
                );
                break;
                
            case "Éolienne":
                txtVitesseVent = new TextField();
                txtPuissanceNominale = new TextField();
                
                panneauParametres.getChildren().addAll(
                    new Label("Vitesse du vent (m/s) :"), txtVitesseVent,
                    new Label("Puissance nominale (kW) :"), txtPuissanceNominale
                );
                break;
                
            case "Batterie":
                txtCapaciteMax = new TextField();
                txtNiveauInitial = new TextField();
                txtEfficacite = new TextField();
                
                panneauParametres.getChildren().addAll(
                    new Label("Capacité maximale (kWh) :"), txtCapaciteMax,
                    new Label("Niveau initial (kWh) :"), txtNiveauInitial,
                    new Label("Efficacité (0-1) :"), txtEfficacite
                );
                break;
        }
    }
    
    /**
     * Crée le panneau contenant les boutons d'action.
     * Utilise des expressions Lambda pour les gestionnaires d'événements.
     * @return Le nœud contenant les boutons
     */
    private HBox creerPanneauBoutons() {
        HBox conteneur = new HBox(15);
        conteneur.setPadding(new Insets(10));
        
        Button btnAjouter = new Button("Ajouter la Source");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAjouter.setOnAction(e -> ajouterSource());
        
        Button btnSupprimer = new Button("Supprimer la Source");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
       btnSupprimer.setOnAction(e -> supprimerSourceAvecConfirmation());

        
        Button btnActualiser = new Button("Actualiser");
        btnActualiser.setOnAction(e -> actualiserTableau());
        
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> this.close());
        
        conteneur.getChildren().addAll(btnAjouter, btnSupprimer, btnActualiser, btnFermer);
        return conteneur;
    }
    
    /**
     * Ajoute une source d'énergie via le contrôleur.
     * Utilise un try-with-resources virtuel pour la validation.
     */
    private void ajouterSource() {
        try {
            String type = comboTypeSource.getValue();
            if (type == null) {
                afficherAlerte("Erreur", "Veuillez sélectionner un type de source.");
                return;
            }
            
            switch (type) {
                case "Panneau Solaire":
                    controleur.ajouterPanneauSolaire(
                        Double.parseDouble(txtSurface.getText()),
                        Double.parseDouble(txtRendement.getText()),
                        Double.parseDouble(txtPuissanceNominale.getText())
                    );
                    break;
                    
                case "Éolienne":
                    controleur.ajouterEolienne(
                        Double.parseDouble(txtVitesseVent.getText()),
                        Double.parseDouble(txtPuissanceNominale.getText())
                    );
                    break;
                    
                case "Batterie":
                    controleur.ajouterBatterie(
                        Double.parseDouble(txtCapaciteMax.getText()),
                        Double.parseDouble(txtNiveauInitial.getText()),
                        Double.parseDouble(txtEfficacite.getText())
                    );
                    break;
            }
            
            actualiserTableau();
            viderChamps();
            afficherAlerte("Succès", "Source ajoutée avec succès !");
            
        } catch (NumberFormatException ex) {
            afficherAlerte("Erreur", "Veuillez entrer des valeurs numériques valides.");
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Erreur lors de l'ajout : " + ex.getMessage());
        }
    }
    
    /**
     * Supprime la source sélectionnée dans le tableau.
     */
    private void supprimerSource() {
        SourceEnergie sourceSelectionnee = tableauSources.getSelectionModel().getSelectedItem();
        if (sourceSelectionnee == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner une source à supprimer.");
            return;
        }
        
        controleur.supprimerSource(sourceSelectionnee);
        actualiserTableau();
    }
    
    /**
     * Actualise le tableau avec les données du contrôleur.
     * Utilise un stream pour la conversion.
     */
    public void actualiserTableau() {
        tableauSources.getItems().clear();
        tableauSources.getItems().addAll(controleur.obtenirSources());
    }
    
    /**
     * Vide tous les champs de saisie.
     */
    private void viderChamps() {
        panneauParametres.getChildren().clear();
        comboTypeSource.setValue(null);
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte.
     * @param titre Le titre de l'alerte
     * @param message Le message à afficher
     */
    private void afficherAlerte(String titre, String message) {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
    /**
 * Supprime la source sélectionnée avec une confirmation utilisateur.
 */
private void supprimerSourceAvecConfirmation() {

    SourceEnergie sourceSelectionnee = tableauSources.getSelectionModel().getSelectedItem();

    if (sourceSelectionnee == null) {
        afficherAlerte("Erreur", "Veuillez sélectionner une source à supprimer.");
        return;
    }

    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Confirmation de suppression");
    confirmation.setHeaderText("Voulez-vous vraiment supprimer cette source ?");
    confirmation.setContentText("Type : " + sourceSelectionnee.getClass().getSimpleName());

    confirmation.showAndWait().ifPresent(reponse -> {
        if (reponse == ButtonType.OK) {

            boolean supprime = controleur.supprimerSource(sourceSelectionnee);

            if (supprime) {
                actualiserTableau();
                afficherAlerte("Succès", "Source supprimée avec succès !");
            } else {
                afficherAlerte("Erreur", "Impossible de supprimer la source.");
            }
        }
    });
}

}