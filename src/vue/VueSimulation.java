package vue;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Controleur.ControleurSimulation;
import simulation.modele.simulation.RecordSimulation;
import java.util.List;

/**
 * Interface principale de simulation avec graphiques en temps réel.
 */
public class VueSimulation extends Stage {
    
    private final ControleurSimulation controleur;
    
    // Composants graphiques
    private Label lblProduction, lblConsommation, lblBilan, lblTemps;
    private LineChart<Number, Number> graphiqueEvolution;
    private PieChart graphiqueRepartition;
    private TextArea txtHistorique;
    private Button btnDemarrer, btnPause, btnReset;
    
    // Données du graphique
    private XYChart.Series<Number, Number> serieProduction;
    private XYChart.Series<Number, Number> serieConsommation;
    
    // Animation
    private AnimationTimer timer;
    private boolean simulationEnCours = false;
    private int compteurTemps = 0;
    
    /**
     * Constructeur de la vue.
     * @param controleur Le contrôleur de simulation
     */
    public VueSimulation(ControleurSimulation controleur) {
        this.controleur = controleur;
        initialiserInterface();
        configurerAnimation();
        configurerGestionnaireAlerte();
        this.setTitle("Simulation Énergétique en Temps Réel");
    }
    
    /**
     * Initialise l'interface graphique.
     */
    private void initialiserInterface() {
        BorderPane racine = new BorderPane();
        racine.setPadding(new Insets(10));
        
        // Partie supérieure : Indicateurs
        racine.setTop(creerPanneauIndicateurs());
        
        // Partie centrale : Graphiques
        racine.setCenter(creerPanneauGraphiques());
        
        // Partie droite : Historique
        racine.setRight(creerPanneauHistorique());
        
        // Partie inférieure : Contrôles
        racine.setBottom(creerPanneauControles());
        
        Scene scene = new Scene(racine, 1200, 700);
        // CSS désactivé - pas nécessaire pour le fonctionnement
        this.setScene(scene);
    }
    
    /**
     * Crée le panneau des indicateurs en temps réel.
     * @return Le nœud contenant les indicateurs
     */
    private VBox creerPanneauIndicateurs() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 5;");
        
        Label titre = new Label("📊 Tableau de Bord Énergétique");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        GridPane grille = new GridPane();
        grille.setHgap(30);
        grille.setVgap(10);
        
        // Indicateur Production
        Label lblTitreProduction = new Label("Production:");
        lblTitreProduction.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px;");
        lblProduction = new Label("0.00 kWh");
        lblProduction.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Indicateur Consommation
        Label lblTitreConsommation = new Label("Consommation:");
        lblTitreConsommation.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
        lblConsommation = new Label("0.00 kWh");
        lblConsommation.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Indicateur Bilan
        Label lblTitreBilan = new Label("Bilan:");
        lblTitreBilan.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 14px;");
        lblBilan = new Label("0.00 kWh");
        lblBilan.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Temps écoulé
        Label lblTitreTemps = new Label("Temps:");
        lblTitreTemps.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px;");
        lblTemps = new Label("0 unités");
        lblTemps.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        grille.add(lblTitreProduction, 0, 0);
        grille.add(lblProduction, 0, 1);
        grille.add(lblTitreConsommation, 1, 0);
        grille.add(lblConsommation, 1, 1);
        grille.add(lblTitreBilan, 2, 0);
        grille.add(lblBilan, 2, 1);
        grille.add(lblTitreTemps, 3, 0);
        grille.add(lblTemps, 3, 1);
        
        conteneur.getChildren().addAll(titre, grille);
        return conteneur;
    }
    
    /**
     * Crée le panneau des graphiques.
     * @return Le nœud contenant les graphiques
     */
    private VBox creerPanneauGraphiques() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(10));
        
        // Graphique en ligne : Évolution temporelle
        graphiqueEvolution = creerGraphiqueEvolution();
        
        // Graphique circulaire : Répartition actuelle
        graphiqueRepartition = creerGraphiqueRepartition();
        
        HBox ligneGraphiques = new HBox(10);
        ligneGraphiques.getChildren().addAll(graphiqueEvolution, graphiqueRepartition);
        
        conteneur.getChildren().add(ligneGraphiques);
        return conteneur;
    }
    
    /**
     * Crée le graphique d'évolution temporelle.
     * @return Le graphique configuré
     */
    private LineChart<Number, Number> creerGraphiqueEvolution() {
        NumberAxis axeX = new NumberAxis();
        axeX.setLabel("Temps (unités)");
        axeX.setAutoRanging(true);
        
        NumberAxis axeY = new NumberAxis();
        axeY.setLabel("Énergie (kWh)");
        axeY.setAutoRanging(true);
        
        LineChart<Number, Number> graphique = new LineChart<>(axeX, axeY);
        graphique.setTitle("Évolution Production vs Consommation");
        graphique.setPrefSize(600, 300);
        graphique.setCreateSymbols(false);
        
        // Séries de données
        serieProduction = new XYChart.Series<>();
        serieProduction.setName("Production");
        
        serieConsommation = new XYChart.Series<>();
        serieConsommation.setName("Consommation");
        
        graphique.getData().addAll(serieProduction, serieConsommation);
        
        return graphique;
    }
    
    /**
     * Crée le graphique de répartition.
     * @return Le graphique circulaire configuré
     */
    private PieChart creerGraphiqueRepartition() {
        PieChart graphique = new PieChart();
        graphique.setTitle("Répartition Énergétique");
        graphique.setPrefSize(400, 300);
        return graphique;
    }
    
    /**
     * Crée le panneau d'historique.
     * @return Le nœud contenant l'historique
     */
    private VBox creerPanneauHistorique() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setPrefWidth(250);
        
        Label titre = new Label("📜 Historique");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        txtHistorique = new TextArea();
        txtHistorique.setEditable(false);
        txtHistorique.setPrefRowCount(25);
        txtHistorique.setWrapText(true);
        
        Button btnVoirStats = new Button("Voir Statistiques");
        btnVoirStats.setOnAction(e -> afficherStatistiques());
        
        conteneur.getChildren().addAll(titre, txtHistorique, btnVoirStats);
        return conteneur;
    }
    
    /**
     * Crée le panneau de contrôles.
     * Utilise des expressions Lambda pour les gestionnaires d'événements.
     * @return Le nœud contenant les boutons
     */
    private HBox creerPanneauControles() {
        HBox conteneur = new HBox(15);
        conteneur.setPadding(new Insets(10));
        
        btnDemarrer = new Button("▶ Démarrer");
        btnDemarrer.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px;");
        btnDemarrer.setOnAction(e -> demarrerSimulation());
        
        btnPause = new Button("⏸ Pause");
        btnPause.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px;");
        btnPause.setDisable(true);
        btnPause.setOnAction(e -> pauserSimulation());
        
        btnReset = new Button("⏹ Réinitialiser");
        btnReset.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px;");
        btnReset.setOnAction(e -> reinitialiserSimulation());
        
        Button btnConfigurerAlerte = new Button("⚙ Configurer Alerte");
        btnConfigurerAlerte.setOnAction(e -> configurerAlerte());
        
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> this.close());
        
        conteneur.getChildren().addAll(btnDemarrer, btnPause, btnReset, btnConfigurerAlerte, btnFermer);
        return conteneur;
    }
    
    /**
     * Configure l'animation pour la simulation en temps réel.
     * Utilise AnimationTimer avec expression Lambda.
     */
    private void configurerAnimation() {
        timer = new AnimationTimer() {
            private long dernierTemps = 0;
            
            @Override
            public void handle(long now) {
                // Exécuter toutes les 1 seconde
                if (now - dernierTemps >= 1_000_000_000) {
                    try {
                        controleur.lancerSimulation();
                        mettreAJourInterface();
                        compteurTemps++;
                        dernierTemps = now;
                    } catch (Exception ex) {
                        afficherAlerte("Erreur", "Erreur durant la simulation: " + ex.getMessage());
                        pauserSimulation();
                    }
                }
            }
        };
    }
    
    /**
     * Configure le gestionnaire d'alerte via interface fonctionnelle Consumer.
     */
    private void configurerGestionnaireAlerte() {
        controleur.definirGestionnaireAlerte(message -> {
            // Consumer<String> avec expression Lambda
            afficherAlerte("⚠️ Alerte Énergie", message);
        });
    }
    
    /**
     * Démarre la simulation.
     */
    private void demarrerSimulation() {
        simulationEnCours = true;
        timer.start();
        btnDemarrer.setDisable(true);
        btnPause.setDisable(false);
    }
    
    /**
     * Met en pause la simulation.
     */
    private void pauserSimulation() {
        simulationEnCours = false;
        timer.stop();
        btnDemarrer.setDisable(false);
        btnPause.setDisable(true);
    }
    
    /**
     * Réinitialise la simulation.
     */
    private void reinitialiserSimulation() {
        pauserSimulation();
        compteurTemps = 0;
        serieProduction.getData().clear();
        serieConsommation.getData().clear();
        txtHistorique.clear();
        controleur.viderHistorique();
        mettreAJourInterface();
    }
    
    /**
     * Met à jour tous les composants de l'interface.
     * Utilise des streams pour traiter l'historique.
     */
    private void mettreAJourInterface() {
        double production = controleur.obtenirProductionTotale();
        double consommation = controleur.obtenirConsommationTotale();
        double bilan = controleur.calculerBilan();
        
        // Mise à jour des labels
        lblProduction.setText(String.format("%.2f kWh", production));
        lblConsommation.setText(String.format("%.2f kWh", consommation));
        lblBilan.setText(String.format("%.2f kWh", bilan));
        lblBilan.setStyle(bilan >= 0 ? 
            "-fx-text-fill: #2ecc71; -fx-font-size: 18px; -fx-font-weight: bold;" : 
            "-fx-text-fill: #e74c3c; -fx-font-size: 18px; -fx-font-weight: bold;");
        lblTemps.setText(compteurTemps + " unités");
        
        // Mise à jour du graphique d'évolution
        serieProduction.getData().add(new XYChart.Data<>(compteurTemps, production));
        serieConsommation.getData().add(new XYChart.Data<>(compteurTemps, consommation));
        
        // Limiter à 50 points pour performances
        if (serieProduction.getData().size() > 50) {
            serieProduction.getData().remove(0);
            serieConsommation.getData().remove(0);
        }
        
        // Mise à jour du graphique de répartition
        mettreAJourGraphiqueRepartition(production, consommation);
        
        // Mise à jour de l'historique
        mettreAJourHistorique();
    }
    
    /**
     * Met à jour le graphique de répartition.
     * @param production Production actuelle
     * @param consommation Consommation actuelle
     */
    private void mettreAJourGraphiqueRepartition(double production, double consommation) {
        graphiqueRepartition.getData().clear();
        
        PieChart.Data dataProd = new PieChart.Data("Production", production);
        PieChart.Data dataConso = new PieChart.Data("Consommation", consommation);
        
        graphiqueRepartition.getData().addAll(dataProd, dataConso);
    }
    
    /**
     * Met à jour l'historique textuel.
     * Utilise un stream pour formater les 10 dernières simulations.
     */
    private void mettreAJourHistorique() {
        List<RecordSimulation> dernieres = controleur.obtenirDernieresSimulations(10);
        
        String texte = dernieres.stream()
                .map(RecordSimulation::toString)
                .reduce("", (acc, ligne) -> ligne + "\n" + acc); // Ordre inversé
        
        txtHistorique.setText(texte);
    }
    
    /**
     * Affiche les statistiques globales.
     */
    private void afficherStatistiques() {
        String stats = controleur.obtenirStatistiques();
        afficherAlerte("Statistiques de Simulation", stats);
    }
    
    /**
     * Configure une alerte personnalisée.
     */
    private void configurerAlerte() {
        Dialog<ButtonType> dialogue = new Dialog<>();
        dialogue.setTitle("Configuration de l'Alerte");
        dialogue.setHeaderText("Définir les seuils d'alerte");
        
        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(10);
        
        TextField txtSeuilConso = new TextField("100.0");
        TextField txtSeuilProd = new TextField("50.0");
        
        grille.add(new Label("Seuil consommation max (kWh):"), 0, 0);
        grille.add(txtSeuilConso, 1, 0);
        grille.add(new Label("Seuil production min (kWh):"), 0, 1);
        grille.add(txtSeuilProd, 1, 1);
        
        dialogue.getDialogPane().setContent(grille);
        dialogue.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialogue.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                try {
                    double seuilConso = Double.parseDouble(txtSeuilConso.getText());
                    double seuilProd = Double.parseDouble(txtSeuilProd.getText());
                    controleur.configurerAlerte(seuilConso, seuilProd);
                    afficherAlerte("Succès", "Alerte configurée avec succès !");
                } catch (NumberFormatException ex) {
                    afficherAlerte("Erreur", "Valeurs invalides.");
                }
            }
        });
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte.
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