package vue;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Controleur.ControleurOptimisation;
import simulation.modele.simulation.RecommandationOptimisation;
import java.util.List;

/**
 * Interface graphique pour l'optimisation énergétique.
 * Propose des recommandations et calcule le rendement optimal.
 */
public class VueOptimisation extends Stage {
    
    private final ControleurOptimisation controleur;
    
    // Composants graphiques
    private Label lblRendementActuel, lblRendementOptimal, lblEconomiesPotentielles;
    private Label lblScoreGlobal, lblEmissionsCO2;
    private ProgressBar progressRendement, progressOptimisation;
    private ListView<RecommandationOptimisation> listeRecommandations;
    private BarChart<String, Number> graphiqueComparaison;
    private PieChart graphiqueRepartition;
    private TextArea txtAnalyse, txtRapportOptimisation;
    private Slider sliderObjectifEconomie;
    
    /**
     * Constructeur de la vue.
     * @param controleur Le contrôleur associé
     */
    public VueOptimisation(ControleurOptimisation controleur) {
        this.controleur = controleur;
        initialiserInterface();
        this.setTitle("🎯 Centre d'Optimisation Énergétique");
    }
    
    /**
     * Initialise tous les composants de l'interface.
     */
    private void initialiserInterface() {
        BorderPane racine = new BorderPane();
        racine.setPadding(new Insets(10));
        
        // Partie supérieure : Indicateurs de performance
        racine.setTop(creerPanneauIndicateurs());
        
        // Partie centrale : Graphiques et analyse
        racine.setCenter(creerPanneauCentral());
        
        // Partie droite : Recommandations
        racine.setRight(creerPanneauRecommandations());
        
        // Partie inférieure : Contrôles et actions
        racine.setBottom(creerPanneauControles());
        
        Scene scene = new Scene(racine, 1400, 800);
        this.setScene(scene);
        
        // Charger l'analyse initiale
        lancerAnalyse();
    }
    
    /**
     * Crée le panneau des indicateurs de performance.
     * @return Le nœud contenant les indicateurs
     */
    private VBox creerPanneauIndicateurs() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #229954); -fx-background-radius: 5;");
        
        Label titre = new Label("🎯 Tableau de Bord d'Optimisation");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        GridPane grille = new GridPane();
        grille.setHgap(40);
        grille.setVgap(10);
        grille.setPadding(new Insets(10, 0, 0, 0));
        
        // Rendement actuel
        VBox boxRendement = creerIndicateur("Rendement Actuel", "0%", "#3498db");
        lblRendementActuel = (Label) boxRendement.getChildren().get(1);
        progressRendement = new ProgressBar(0);
        progressRendement.setPrefWidth(150);
        progressRendement.setStyle("-fx-accent: #3498db;");
        boxRendement.getChildren().add(progressRendement);
        
        // Rendement optimal
        VBox boxOptimal = creerIndicateur("Rendement Optimal", "0%", "#2ecc71");
        lblRendementOptimal = (Label) boxOptimal.getChildren().get(1);
        
        // Économies potentielles
        VBox boxEconomies = creerIndicateur("Économies Potentielles", "0 kWh", "#f39c12");
        lblEconomiesPotentielles = (Label) boxEconomies.getChildren().get(1);
        
        // Score global
        VBox boxScore = creerIndicateur("Score d'Efficacité", "0/100", "#9b59b6");
        lblScoreGlobal = (Label) boxScore.getChildren().get(1);
        progressOptimisation = new ProgressBar(0);
        progressOptimisation.setPrefWidth(150);
        progressOptimisation.setStyle("-fx-accent: #9b59b6;");
        boxScore.getChildren().add(progressOptimisation);
        
        // Émissions CO2
        VBox boxCO2 = creerIndicateur("Émissions CO2", "0 kg", "#e74c3c");
        lblEmissionsCO2 = (Label) boxCO2.getChildren().get(1);
        
        grille.add(boxRendement, 0, 0);
        grille.add(boxOptimal, 1, 0);
        grille.add(boxEconomies, 2, 0);
        grille.add(boxScore, 3, 0);
        grille.add(boxCO2, 4, 0);
        
        conteneur.getChildren().addAll(titre, grille);
        return conteneur;
    }
    
    /**
     * Crée un indicateur stylisé.
     * @param titre Le titre
     * @param valeur La valeur initiale
     * @param couleur La couleur
     * @return VBox contenant l'indicateur
     */
    private VBox creerIndicateur(String titre, String valeur, String couleur) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        
        Label lblTitre = new Label(titre);
        lblTitre.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        
        Label lblValeur = new Label(valeur);
        lblValeur.setStyle(String.format("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;"));
        
        box.getChildren().addAll(lblTitre, lblValeur);
        return box;
    }
    
    /**
     * Crée le panneau central avec graphiques et analyse.
     * @return Le nœud contenant le panneau central
     */
    private HBox creerPanneauCentral() {
        HBox conteneur = new HBox(10);
        conteneur.setPadding(new Insets(10));
        
        // Partie gauche : Graphiques
        VBox panneauGraphiques = creerPanneauGraphiques();
        HBox.setHgrow(panneauGraphiques, Priority.ALWAYS);
        
        // Partie droite : Analyse textuelle
        VBox panneauAnalyse = creerPanneauAnalyse();
        panneauAnalyse.setPrefWidth(350);
        
        conteneur.getChildren().addAll(panneauGraphiques, panneauAnalyse);
        return conteneur;
    }
    
    /**
     * Crée le panneau des graphiques.
     * @return Le nœud contenant les graphiques
     */
    private VBox creerPanneauGraphiques() {
        VBox conteneur = new VBox(15);
        
        // Graphique en barres : Comparaison actuel vs optimal
        graphiqueComparaison = creerGraphiqueComparaison();
        
        // Graphique circulaire : Répartition de la consommation
        graphiqueRepartition = creerGraphiqueRepartition();
        
        HBox ligneGraphiques = new HBox(10);
        ligneGraphiques.getChildren().addAll(graphiqueComparaison, graphiqueRepartition);
        
        conteneur.getChildren().add(ligneGraphiques);
        return conteneur;
    }
    
    /**
     * Crée le graphique de comparaison.
     * @return Le graphique configuré
     */
    private BarChart<String, Number> creerGraphiqueComparaison() {
        CategoryAxis axeX = new CategoryAxis();
        axeX.setLabel("Paramètres");
        
        NumberAxis axeY = new NumberAxis();
        axeY.setLabel("Valeur (kWh)");
        
        BarChart<String, Number> graphique = new BarChart<>(axeX, axeY);
        graphique.setTitle("Comparaison : Actuel vs Optimal");
        graphique.setPrefSize(500, 350);
        graphique.setLegendVisible(true);
        
        return graphique;
    }
    
    /**
     * Crée le graphique de répartition.
     * @return Le graphique circulaire configuré
     */
    private PieChart creerGraphiqueRepartition() {
        PieChart graphique = new PieChart();
        graphique.setTitle("Répartition de la Consommation");
        graphique.setPrefSize(400, 350);
        graphique.setLegendVisible(true);
        return graphique;
    }
    
    /**
     * Crée le panneau d'analyse textuelle.
     * @return Le nœud contenant l'analyse
     */
    private VBox creerPanneauAnalyse() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        
        Label titre = new Label("📊 Analyse Détaillée");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        txtAnalyse = new TextArea();
        txtAnalyse.setEditable(false);
        txtAnalyse.setWrapText(true);
        txtAnalyse.setPrefRowCount(15);
        txtAnalyse.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        
        Label lblObjectif = new Label("Objectif d'économie (%):");
        lblObjectif.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        
        sliderObjectifEconomie = new Slider(0, 50, 20);
        sliderObjectifEconomie.setShowTickLabels(true);
        sliderObjectifEconomie.setShowTickMarks(true);
        sliderObjectifEconomie.setMajorTickUnit(10);
        sliderObjectifEconomie.setBlockIncrement(5);
        
        Label lblValeurSlider = new Label("20%");
        lblValeurSlider.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        // Mise à jour dynamique du label avec Lambda
        sliderObjectifEconomie.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblValeurSlider.setText(String.format("%.0f%%", newVal.doubleValue()));
        });
        
        Button btnRecalculer = new Button("Recalculer avec nouvel objectif");
        btnRecalculer.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnRecalculer.setMaxWidth(Double.MAX_VALUE);
        btnRecalculer.setOnAction(e -> recalculerAvecObjectif());
        
        conteneur.getChildren().addAll(titre, txtAnalyse, lblObjectif, sliderObjectifEconomie, 
                                        lblValeurSlider, btnRecalculer);
        VBox.setVgrow(txtAnalyse, Priority.ALWAYS);
        return conteneur;
    }
    
    /**
     * Crée le panneau des recommandations.
     * @return Le nœud contenant les recommandations
     */
    private VBox creerPanneauRecommandations() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setPrefWidth(400);
        conteneur.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        
        Label titre = new Label("💡 Recommandations d'Optimisation");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        listeRecommandations = new ListView<>();
        listeRecommandations.setPrefHeight(300);
        listeRecommandations.setCellFactory(param -> new ListCell<RecommandationOptimisation>() {
            @Override
            protected void updateItem(RecommandationOptimisation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    VBox box = new VBox(5);
                    box.setPadding(new Insets(5));
                    
                    Label lblTitre = new Label(item.titre());
                    lblTitre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                    
                    Label lblDesc = new Label(item.description());
                    lblDesc.setWrapText(true);
                    lblDesc.setStyle("-fx-font-size: 11px;");
                    
                    Label lblImpact = new Label(String.format("Impact: %s | Priorité: %s", 
                        item.impact(), item.priorite()));
                    lblImpact.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
                    
                    box.getChildren().addAll(lblTitre, lblDesc, lblImpact);
                    setGraphic(box);
                    
                    // Couleur selon priorité
                    switch (item.priorite()) {
                        case "HAUTE":
                            setStyle("-fx-background-color: #ffe6e6;");
                            break;
                        case "MOYENNE":
                            setStyle("-fx-background-color: #fff4e6;");
                            break;
                        case "BASSE":
                            setStyle("-fx-background-color: #e6f7ff;");
                            break;
                    }
                }
            }
        });
        
        Button btnAppliquer = new Button("✓ Appliquer la Recommandation");
        btnAppliquer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnAppliquer.setMaxWidth(Double.MAX_VALUE);
        btnAppliquer.setOnAction(e -> appliquerRecommandation());
        
        Button btnToutAppliquer = new Button("✓✓ Appliquer Toutes");
        btnToutAppliquer.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnToutAppliquer.setMaxWidth(Double.MAX_VALUE);
        btnToutAppliquer.setOnAction(e -> appliquerToutesRecommandations());
        
        Separator sep = new Separator();
        
        Label lblRapport = new Label("📄 Rapport d'Optimisation:");
        lblRapport.setStyle("-fx-font-weight: bold;");
        
        txtRapportOptimisation = new TextArea();
        txtRapportOptimisation.setEditable(false);
        txtRapportOptimisation.setWrapText(true);
        txtRapportOptimisation.setPrefRowCount(10);
        
        Button btnGenererRapport = new Button("📊 Générer Rapport Complet");
        btnGenererRapport.setMaxWidth(Double.MAX_VALUE);
        btnGenererRapport.setOnAction(e -> genererRapport());
        
        conteneur.getChildren().addAll(
            titre, listeRecommandations, btnAppliquer, btnToutAppliquer, 
            sep, lblRapport, txtRapportOptimisation, btnGenererRapport
        );
        
        VBox.setVgrow(listeRecommandations, Priority.ALWAYS);
        return conteneur;
    }
    
    /**
     * Crée le panneau des contrôles.
     * @return Le nœud contenant les boutons
     */
    private HBox creerPanneauControles() {
        HBox conteneur = new HBox(15);
        conteneur.setPadding(new Insets(10));
        conteneur.setAlignment(Pos.CENTER);
        
        Button btnAnalyser = new Button("🔍 Analyser à Nouveau");
        btnAnalyser.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        btnAnalyser.setOnAction(e -> lancerAnalyse());
        
        Button btnSimuler = new Button("🎮 Simuler Scénario");
        btnSimuler.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 14px;");
        btnSimuler.setOnAction(e -> simulerScenario());
        
        Button btnExporter = new Button("📄 Exporter Rapport");
        btnExporter.setStyle("-fx-background-color: #16a085; -fx-text-fill: white; -fx-font-size: 14px;");
        btnExporter.setOnAction(e -> exporterRapport());
        
        Button btnReinitialiser = new Button("🔄 Réinitialiser");
        btnReinitialiser.setOnAction(e -> reinitialiser());
        
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> this.close());
        
        conteneur.getChildren().addAll(btnAnalyser, btnSimuler, btnExporter, btnReinitialiser, btnFermer);
        return conteneur;
    }
    
    /**
     * Lance l'analyse d'optimisation.
     * Utilise le contrôleur pour calculer les métriques.
     */
    private void lancerAnalyse() {
        // Calculer les métriques
        double rendementActuel = controleur.calculerRendementActuel();
        double rendementOptimal = controleur.calculerRendementOptimal();
        double economies = controleur.calculerEconomiesPotentielles();
        double score = controleur.calculerScoreEfficacite();
        double co2 = controleur.calculerEmissionsCO2();
        
        // Mettre à jour les indicateurs
        lblRendementActuel.setText(String.format("%.1f%%", rendementActuel));
        lblRendementOptimal.setText(String.format("%.1f%%", rendementOptimal));
        lblEconomiesPotentielles.setText(String.format("%.2f kWh", economies));
        lblScoreGlobal.setText(String.format("%.0f/100", score));
        lblEmissionsCO2.setText(String.format("%.2f kg", co2));
        
        progressRendement.setProgress(rendementActuel / 100.0);
        progressOptimisation.setProgress(score / 100.0);
        
        // Mettre à jour les graphiques
        mettreAJourGraphiques();
        
        // Charger les recommandations
        chargerRecommandations();
        
        // Générer l'analyse textuelle
        genererAnalyse();
    }
    
    /**
     * Met à jour les graphiques avec les données du contrôleur.
     */
    private void mettreAJourGraphiques() {
        // Graphique de comparaison
        graphiqueComparaison.getData().clear();
        
        XYChart.Series<String, Number> serieActuel = new XYChart.Series<>();
        serieActuel.setName("Actuel");
        serieActuel.getData().add(new XYChart.Data<>("Production", controleur.getGestionEnergie().productionTotale()));
        serieActuel.getData().add(new XYChart.Data<>("Consommation", controleur.getGestionEnergie().consommationTotale()));
        
        XYChart.Series<String, Number> serieOptimal = new XYChart.Series<>();
        serieOptimal.setName("Optimal");
        serieOptimal.getData().add(new XYChart.Data<>("Production", controleur.calculerProductionOptimale()));
        serieOptimal.getData().add(new XYChart.Data<>("Consommation", controleur.calculerConsommationOptimale()));
        
        graphiqueComparaison.getData().addAll(serieActuel, serieOptimal);
        
        // Graphique de répartition
        mettreAJourGraphiqueRepartition();
    }
    
    /**
     * Met à jour le graphique de répartition.
     */
    private void mettreAJourGraphiqueRepartition() {
        graphiqueRepartition.getData().clear();
        
        var repartition = controleur.calculerRepartitionConsommation();
        repartition.forEach((nom, valeur) -> {
            PieChart.Data data = new PieChart.Data(nom, valeur);
            graphiqueRepartition.getData().add(data);
        });
    }
    
    /**
     * Charge les recommandations d'optimisation.
     */
    private void chargerRecommandations() {
        List<RecommandationOptimisation> recommandations = controleur.genererRecommandations();
        listeRecommandations.getItems().clear();
        listeRecommandations.getItems().addAll(recommandations);
    }
    
    /**
     * Génère l'analyse textuelle détaillée.
     */
    private void genererAnalyse() {
        String analyse = controleur.genererAnalyseComplete();
        txtAnalyse.setText(analyse);
    }
    
    /**
     * Recalcule avec un nouvel objectif d'économie.
     */
    private void recalculerAvecObjectif() {
        double objectif = sliderObjectifEconomie.getValue();
        controleur.definirObjectifEconomie(objectif);
        lancerAnalyse();
        afficherAlerte("Recalculé", String.format("Analyse recalculée avec objectif de %.0f%% d'économie", objectif));
    }
    
    /**
     * Applique la recommandation sélectionnée.
     */
    private void appliquerRecommandation() {
        RecommandationOptimisation recom = listeRecommandations.getSelectionModel().getSelectedItem();
        if (recom == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner une recommandation.");
            return;
        }
        
        controleur.appliquerRecommandation(recom);
        lancerAnalyse();
        afficherAlerte("Succès", "Recommandation appliquée !");
    }
    
    /**
     * Applique toutes les recommandations.
     */
    private void appliquerToutesRecommandations() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Appliquer toutes les recommandations");
        confirmation.setContentText("Cela modifiera les paramètres de simulation. Continuer ?");
        
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                controleur.appliquerToutesRecommandations();
                lancerAnalyse();
                afficherAlerte("Succès", "Toutes les recommandations ont été appliquées !");
            }
        });
    }
    
    /**
     * Génère le rapport complet d'optimisation.
     */
    private void genererRapport() {
        String rapport = controleur.genererRapportOptimisation();
        txtRapportOptimisation.setText(rapport);
    }
    
    /**
     * Simule un scénario d'optimisation.
     */
    private void simulerScenario() {
        // Boîte de dialogue pour paramètres de simulation
        Dialog<ButtonType> dialogue = new Dialog<>();
        dialogue.setTitle("Simulation de Scénario");
        dialogue.setHeaderText("Définir les paramètres du scénario");
        
        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(10);
        
        TextField txtAugmentProd = new TextField("10");
        TextField txtReductionConso = new TextField("15");
        
        grille.add(new Label("Augmentation production (%):"), 0, 0);
        grille.add(txtAugmentProd, 1, 0);
        grille.add(new Label("Réduction consommation (%):"), 0, 1);
        grille.add(txtReductionConso, 1, 1);
        
        dialogue.getDialogPane().setContent(grille);
        dialogue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialogue.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                try {
                    double augmentProd = Double.parseDouble(txtAugmentProd.getText());
                    double reductConso = Double.parseDouble(txtReductionConso.getText());
                    
                    String resultat = controleur.simulerScenario(augmentProd, reductConso);
                    afficherAlerte("Résultat de Simulation", resultat);
                } catch (NumberFormatException ex) {
                    afficherAlerte("Erreur", "Valeurs invalides.");
                }
            }
        });
    }
    
    /**
     * Exporte le rapport d'optimisation.
     */
    private void exporterRapport() {
        String rapport = controleur.exporterRapportComplet();
        
        Stage fenetreExport = new Stage();
        fenetreExport.setTitle("Rapport d'Optimisation");
        
        TextArea txtExport = new TextArea(rapport);
        txtExport.setEditable(false);
        
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.getChildren().add(txtExport);
        
        Scene scene = new Scene(conteneur, 800, 600);
        fenetreExport.setScene(scene);
        fenetreExport.show();
    }
    
    /**
     * Réinitialise l'analyse.
     */
    private void reinitialiser() {
        controleur.reinitialiser();
        lancerAnalyse();
    }
    
    /**
     * Affiche une alerte.
     * @param titre Le titre
     * @param message Le message
     */
    private void afficherAlerte(String titre, String message) {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}