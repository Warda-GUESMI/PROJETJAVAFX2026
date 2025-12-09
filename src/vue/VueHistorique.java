package vue;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TextField txtFiltreTemps;
    private Label lblNombreSimulations, lblProductionMoyenne, lblConsommationMoyenne;
    private TextArea txtDetailsSelection;
    
    /**
     * Constructeur de la vue.
     * @param controleur Le contrôleur associé
     */
    public VueHistorique(ControleurHistorique controleur) {
        this.controleur = controleur;
        initialiserInterface();
    }
    
    /**
     * Initialise tous les composants de l'interface.
     */
    private void initialiserInterface() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        
        // Partie supérieure : Statistiques globales
        VBox panneauStats = creerPanneauStatistiques();
        
        // Partie centrale : Tableau de l'historique
        VBox panneauTableau = creerTableauHistorique();
        VBox.setVgrow(panneauTableau, Priority.ALWAYS);
        
        // Partie inférieure : Contrôles
        HBox panneauControles = creerPanneauControles();
        
        this.getChildren().addAll(panneauStats, panneauTableau, panneauControles);
        
        // Charger les données initiales
        recharger();
    }
    
    /**
     * Crée le panneau des statistiques globales.
     * @return Le nœud contenant les statistiques
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
        
        // Nombre de simulations
        Label lblTitreNombre = new Label("Nombre de simulations :");
        lblTitreNombre.setStyle("-fx-font-weight: bold;");
        lblNombreSimulations = new Label("0");
        lblNombreSimulations.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        // Production moyenne
        Label lblTitreProdMoy = new Label("Production moyenne :");
        lblTitreProdMoy.setStyle("-fx-font-weight: bold;");
        lblProductionMoyenne = new Label("0.00 kWh");
        lblProductionMoyenne.setStyle("-fx-font-size: 16px; -fx-text-fill: #27ae60;");
        
        // Consommation moyenne
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
     * CORRIGÉ pour fonctionner avec les Records Java 17.
     * @return Le nœud contenant le tableau
     */
    private VBox creerTableauHistorique() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        
        Label titre = new Label("📜 Historique Complet des Simulations");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        tableauHistorique = new TableView<>();
        tableauHistorique.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // ✅ COLONNES CORRIGÉES - Compatible avec Records
        TableColumn<RecordSimulation, Integer> colTemps = new TableColumn<>("Temps");
        colTemps.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().temps()).asObject());
        colTemps.setPrefWidth(100);
        
        TableColumn<RecordSimulation, Double> colProduction = new TableColumn<>("Production (kWh)");
        colProduction.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().production()).asObject());
        colProduction.setPrefWidth(150);
        colProduction.setCellFactory(col -> new TableCell<RecordSimulation, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", item));
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });
        
        TableColumn<RecordSimulation, Double> colConsommation = new TableColumn<>("Consommation (kWh)");
        colConsommation.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().consommation()).asObject());
        colConsommation.setPrefWidth(150);
        colConsommation.setCellFactory(col -> new TableCell<RecordSimulation, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", item));
                    setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
        
        // Colonne calculée : Bilan
        TableColumn<RecordSimulation, String> colBilan = new TableColumn<>("Bilan (kWh)");
        colBilan.setCellValueFactory(cellData -> {
            double bilan = cellData.getValue().production() - cellData.getValue().consommation();
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", bilan));
        });
        colBilan.setPrefWidth(150);
        colBilan.setCellFactory(col -> new TableCell<RecordSimulation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    double valeur = Double.parseDouble(item);
                    if (valeur >= 0) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Colonne calculée : État
        TableColumn<RecordSimulation, String> colEtat = new TableColumn<>("État");
        colEtat.setCellValueFactory(cellData -> {
            double bilan = cellData.getValue().production() - cellData.getValue().consommation();
            String etat = bilan >= 0 ? "✅ Excédent" : "⚠️ Déficit";
            return new javafx.beans.property.SimpleStringProperty(etat);
        });
        colEtat.setPrefWidth(120);
        
        tableauHistorique.getColumns().addAll(colTemps, colProduction, colConsommation, colBilan, colEtat);
        
        // Événement de sélection avec Lambda
        tableauHistorique.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherDetails(newVal);
            }
        });
        
        // Zone de détails
        Label lblDetails = new Label("🔍 Détails de la simulation sélectionnée:");
        lblDetails.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        
        txtDetailsSelection = new TextArea();
        txtDetailsSelection.setEditable(false);
        txtDetailsSelection.setWrapText(true);
        txtDetailsSelection.setPrefRowCount(5);
        txtDetailsSelection.setPromptText("Sélectionnez une simulation pour voir les détails");
        
        conteneur.getChildren().addAll(titre, tableauHistorique, lblDetails, txtDetailsSelection);
        return conteneur;
    }
    
    /**
     * Crée le panneau des contrôles.
     * @return Le nœud contenant les boutons
     */
    private HBox creerPanneauControles() {
        HBox conteneur = new HBox(15);
        conteneur.setPadding(new Insets(10));
        conteneur.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Filtrage
        Label lblFiltre = new Label("Filtrer par temps ≥ :");
        txtFiltreTemps = new TextField();
        txtFiltreTemps.setPromptText("Ex: 10");
        txtFiltreTemps.setPrefWidth(100);
        
        Button btnFiltrer = new Button("Filtrer");
        btnFiltrer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnFiltrer.setOnAction(e -> appliquerFiltre());
        
        Button btnReinitialiser = new Button("Tout Afficher");
        btnReinitialiser.setOnAction(e -> recharger());
        
        Button btnExporter = new Button("📄 Exporter");
        btnExporter.setStyle("-fx-background-color: #16a085; -fx-text-fill: white;");
        btnExporter.setOnAction(e -> exporterHistorique());
        
        Button btnSauvegarder = new Button("💾 Sauvegarder");
        btnSauvegarder.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnSauvegarder.setOnAction(e -> {
            controleur.sauvegarder();
            afficherInfo("Sauvegarde", "Historique sauvegardé avec succès !");
        });
        
        Button btnVider = new Button("🗑️ Vider");
        btnVider.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnVider.setOnAction(e -> viderHistorique());
        
        Button btnActualiser = new Button("🔄 Actualiser");
        btnActualiser.setOnAction(e -> recharger());
        
        conteneur.getChildren().addAll(
            lblFiltre, txtFiltreTemps, btnFiltrer, btnReinitialiser,
            btnExporter, btnSauvegarder, btnVider, btnActualiser
        );
        
        return conteneur;
    }
    
    /**
     * Recharge l'historique complet depuis le contrôleur.
     */
    public void recharger() {
        List<RecordSimulation> historique = controleur.obtenirHistorique();
        tableauHistorique.getItems().clear();
        tableauHistorique.getItems().addAll(historique);
        
        // Mettre à jour les statistiques
        lblNombreSimulations.setText(String.valueOf(controleur.compterSimulations()));
        lblProductionMoyenne.setText(String.format("%.2f kWh", controleur.calculerProductionMoyenne()));
        lblConsommationMoyenne.setText(String.format("%.2f kWh", controleur.calculerConsommationMoyenne()));
        
        System.out.println("✅ Historique rechargé : " + historique.size() + " enregistrement(s)");
    }
    
    /**
     * Applique le filtre par temps.
     */
    private void appliquerFiltre() {
        String texte = txtFiltreTemps.getText().trim();
        if (texte.isEmpty()) {
            recharger();
            return;
        }
        
        try {
            int tempsMin = Integer.parseInt(texte);
            List<RecordSimulation> filtres = controleur.filtrerParTemps(tempsMin);
            tableauHistorique.getItems().clear();
            tableauHistorique.getItems().addAll(filtres);
            
            System.out.println("✅ Filtre appliqué : " + filtres.size() + " résultat(s)");
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Veuillez entrer un nombre valide.");
        }
    }
    
    /**
     * Exporte l'historique en format texte.
     */
    private void exporterHistorique() {
        String rapport = controleur.exporterEnTexte();
        
        // Afficher dans une fenêtre
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Historique");
        alert.setHeaderText("Historique exporté");
        
        TextArea textArea = new TextArea(rapport);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(60);
        
        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(700, 600);
        alert.showAndWait();
    }
    
    /**
     * Vide l'historique après confirmation.
     */
    private void viderHistorique() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Vider l'historique");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer toutes les données ?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                controleur.viderHistorique();
                recharger();
                txtDetailsSelection.clear();
                afficherInfo("Succès", "Historique vidé avec succès !");
            }
        });
    }
    
    /**
     * Affiche les détails d'une simulation sélectionnée.
     * @param record L'enregistrement sélectionné
     */
    private void afficherDetails(RecordSimulation record) {
        double bilan = record.production() - record.consommation();
        String etat = bilan >= 0 ? "✅ EXCÉDENT" : "⚠️ DÉFICIT";
        
        String details = String.format(
            "════════════════════════════════════\n" +
            "   DÉTAILS DE LA SIMULATION\n" +
            "════════════════════════════════════\n\n" +
            "⏱️  Temps : %d unités\n" +
            "⚡ Production : %.2f kWh\n" +
            "🔌 Consommation : %.2f kWh\n" +
            "📊 Bilan : %.2f kWh\n" +
            "🎯 État : %s\n" +
            "\n════════════════════════════════════",
            record.temps(),
            record.production(),
            record.consommation(),
            bilan,
            etat
        );
        
        txtDetailsSelection.setText(details);
    }
    
    /**
     * Affiche une alerte d'erreur.
     * @param titre Le titre de l'alerte
     * @param message Le message à afficher
     */
    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche une information.
     * @param titre Le titre de l'information
     * @param message Le message à afficher
     */
    private void afficherInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}