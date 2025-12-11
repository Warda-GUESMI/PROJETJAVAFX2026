package vue;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import Controleur.ControleurHistorique;
import simulation.modele.simulation.RecordSimulation;
import java.util.List;

/**
 * Interface graphique pour visualiser et gérer l'historique des simulations.
 * VERSION INTÉGRÉE DANS UN ONGLET (pas un Stage).
 */
public class VueHistorique extends VBox {
    
    private final ControleurHistorique controleur;
    
    // Composants graphiques
    private TableView<RecordSimulation> tableauHistorique;
    private Label lblNombreSimulations, lblProductionMoyenne, lblConsommationMoyenne;
    private TextArea txtDetailsSelection;
    
    /**
     * Constructeur de la vue.
     * @param controleur Le contrôleur associé
     */
    public VueHistorique(ControleurHistorique controleur) {
        this.controleur = controleur;
        initialiserInterface();
        recharger(); // Charger les données initiales
    }
    
    /**
     * Initialise tous les composants de l'interface.
     */
    private void initialiserInterface() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        
        VBox panneauStats = creerPanneauStatistiques();
        VBox panneauTableau = creerTableauHistorique();
        VBox.setVgrow(panneauTableau, Priority.ALWAYS);
        
        HBox panneauControles = creerPanneauControles();
        
        this.getChildren().addAll(panneauStats, panneauTableau, panneauControles);
    }
    
    /**
     * Crée le panneau des statistiques globales.
     */
    private VBox creerPanneauStatistiques() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        
        Label titre = new Label("📊 Statistiques Globales");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grille = new GridPane();
        grille.setHgap(30);
        grille.setVgap(10);
        grille.setPadding(new Insets(10, 0, 0, 0));
        
        Label lblTitreNombre = new Label("Nombre de simulations :");
        lblTitreNombre.setStyle("-fx-font-weight: bold;");
        lblNombreSimulations = new Label("0");
        lblNombreSimulations.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        Label lblTitreProdMoy = new Label("Production moyenne :");
        lblTitreProdMoy.setStyle("-fx-font-weight: bold;");
        lblProductionMoyenne = new Label("0.00 kWh");
        lblProductionMoyenne.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");
        
        Label lblTitreConsoMoy = new Label("Consommation moyenne :");
        lblTitreConsoMoy.setStyle("-fx-font-weight: bold;");
        lblConsommationMoyenne = new Label("0.00 kWh");
        lblConsommationMoyenne.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c;");
        
        grille.add(lblTitreNombre, 0, 0);
        grille.add(lblNombreSimulations, 1, 0);
        grille.add(lblTitreProdMoy, 2, 0);
        grille.add(lblProductionMoyenne, 3, 0);
        grille.add(lblTitreConsoMoy, 4, 0);
        grille.add(lblConsommationMoyenne, 5, 0);
        
        conteneur.getChildren().addAll(titre, grille);
        return conteneur;
    }
    
    /**
     * Crée le tableau affichant l'historique.
     */
    private VBox creerTableauHistorique() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        
        Label titre = new Label("📜 Historique Complet des Simulations");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        tableauHistorique = new TableView<>();
        tableauHistorique.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<RecordSimulation, Integer> colTemps = new TableColumn<>("Temps");
        colTemps.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().temps()).asObject());
        
        TableColumn<RecordSimulation, Double> colProduction = new TableColumn<>("Production (kWh)");
        colProduction.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().production()).asObject());
        
        TableColumn<RecordSimulation, Double> colConsommation = new TableColumn<>("Consommation (kWh)");
        colConsommation.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().consommation()).asObject());
        
        TableColumn<RecordSimulation, String> colBilan = new TableColumn<>("Bilan (kWh)");
        colBilan.setCellValueFactory(cellData -> {
            double bilan = cellData.getValue().production() - cellData.getValue().consommation();
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", bilan));
        });
        
        TableColumn<RecordSimulation, String> colEtat = new TableColumn<>("État");
        colEtat.setCellValueFactory(cellData -> {
            double bilan = cellData.getValue().production() - cellData.getValue().consommation();
            String etat = bilan >= 0 ? "✅ Excédent" : "⚠️ Déficit";
            return new javafx.beans.property.SimpleStringProperty(etat);
        });
        
        tableauHistorique.getColumns().addAll(colTemps, colProduction, colConsommation, colBilan, colEtat);
        
        tableauHistorique.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) afficherDetails(newVal);
        });
        
        Label lblDetails = new Label("🔍 Détails de la simulation sélectionnée:");
        lblDetails.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        
        txtDetailsSelection = new TextArea();
        txtDetailsSelection.setEditable(false);
        txtDetailsSelection.setPrefHeight(100);
        
        conteneur.getChildren().addAll(titre, tableauHistorique, lblDetails, txtDetailsSelection);
        VBox.setVgrow(tableauHistorique, Priority.ALWAYS);
        
        return conteneur;
    }
    
    /**
     * Crée le panneau des contrôles (uniquement Exporter + Vider).
     */
    private HBox creerPanneauControles() {
        HBox conteneur = new HBox(20);
        conteneur.setPadding(new Insets(10));
        conteneur.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button btnRecharger = new Button("🔄 Actualiser");
        btnRecharger.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnRecharger.setOnAction(e -> recharger());
        
        Button btnExporter = new Button("📄 Exporter");
        btnExporter.setStyle("-fx-background-color: #16a085; -fx-text-fill: white;");
        btnExporter.setOnAction(e -> exporterHistorique());
        
        Button btnVider = new Button("🗑️ Vider");
        btnVider.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnVider.setOnAction(e -> viderHistorique());
        
        conteneur.getChildren().addAll(btnRecharger, btnExporter, btnVider);
        return conteneur;
    }
    
    /**
     * Affiche les détails d'une simulation.
     */
    private void afficherDetails(RecordSimulation record) {
        if (record == null) return;
        
        double bilan = record.production() - record.consommation();
        String etat = bilan >= 0 ? "Excédent" : "Déficit";
        
        String details = String.format(
            "═══════════════════════════════════════\n" +
            "        DÉTAILS DE LA SIMULATION\n" +
            "═══════════════════════════════════════\n\n" +
            "⏱️  Temps:          %d\n" +
            "⚡ Production:     %.2f kWh\n" +
            "🔌 Consommation:   %.2f kWh\n" +
            "📊 Bilan:          %.2f kWh\n" +
            "🎯 État:           %s\n" +
            "\n═══════════════════════════════════════",
            record.temps(),
            record.production(),
            record.consommation(),
            bilan,
            etat
        );
        
        txtDetailsSelection.setText(details);
    }
    
    /**
     * Exporte l'historique en texte.
     */
    private void exporterHistorique() {
        String contenu = controleur.exporterEnTexte();
        
        if (contenu.isEmpty() || contenu.contains("vide")) {
            afficherAlerte("Information", "L'historique est vide.");
            return;
        }
        
        Alert fenetre = new Alert(Alert.AlertType.INFORMATION);
        fenetre.setTitle("Exportation de l'Historique");
        fenetre.setHeaderText("Historique exporté");
        
        TextArea txtExport = new TextArea(contenu);
        txtExport.setEditable(false);
        txtExport.setPrefSize(700, 500);
        
        fenetre.getDialogPane().setContent(txtExport);
        fenetre.showAndWait();
    }
    
    /**
     * Vide l'historique.
     */
    private void viderHistorique() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Vider l'historique");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer tout l'historique ?");
        
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                controleur.viderHistorique();
                recharger(); // Recharger après vidage
                afficherAlerte("Succès", "Historique vidé avec succès !");
            }
        });
    }
    
    /**
     * ✅ MÉTHODE PUBLIQUE pour recharger toutes les données
     */
    public void recharger() {
        actualiserTableau();
        actualiserStatistiques();
    }
    
    /**
     * Actualise le tableau avec les données de l'historique.
     */
    private void actualiserTableau() {
        List<RecordSimulation> historique = controleur.obtenirHistorique();
        tableauHistorique.getItems().clear();
        tableauHistorique.getItems().addAll(historique);
    }
    
    /**
     * Actualise les statistiques affichées.
     */
    private void actualiserStatistiques() {
        List<RecordSimulation> historique = controleur.obtenirHistorique();
        
        lblNombreSimulations.setText(String.valueOf(historique.size()));
        
        if (historique.isEmpty()) {
            lblProductionMoyenne.setText("0.00 kWh");
            lblConsommationMoyenne.setText("0.00 kWh");
            txtDetailsSelection.setText("Aucune simulation sélectionnée");
            return;
        }
        
        double prodMoy = historique.stream().mapToDouble(RecordSimulation::production).average().orElse(0);
        double consoMoy = historique.stream().mapToDouble(RecordSimulation::consommation).average().orElse(0);
        
        lblProductionMoyenne.setText(String.format("%.2f kWh", prodMoy));
        lblConsommationMoyenne.setText(String.format("%.2f kWh", consoMoy));
    }
    
    /**
     * Affiche une alerte d'information.
     */
    private void afficherAlerte(String titre, String message) {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}