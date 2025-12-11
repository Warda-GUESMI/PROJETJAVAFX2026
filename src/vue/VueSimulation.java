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

    // Séries pour graphique
    private XYChart.Series<Number, Number> serieProduction;
    private XYChart.Series<Number, Number> serieConsommation;

    // Animation
    private AnimationTimer timer;
    private boolean simulationEnCours = false;
    private long tempsDebut;
    private double tempsSimulation; // secondes écoulées

    // Simulation naturelle
    private double productionMax = 120.0;
    private double consommationMax = 100.0;
    private double production;
    private double consommation;

    public VueSimulation(ControleurSimulation controleur) {
        this.controleur = controleur;
        this.production = productionMax;
        this.consommation = 0.0;
        initialiserInterface();
        configurerAnimation();
        configurerGestionnaireAlerte();
        this.setTitle("Simulation Énergétique en Temps Réel");
    }

    // ------------------- Interface -------------------
    private void initialiserInterface() {
        BorderPane racine = new BorderPane();
        racine.setPadding(new Insets(10));

        racine.setTop(creerPanneauIndicateurs());
        racine.setCenter(creerPanneauGraphiques());
        racine.setRight(creerPanneauHistorique());
        racine.setBottom(creerPanneauControles());

        Scene scene = new Scene(racine, 1200, 700);
        this.setScene(scene);
    }

    private VBox creerPanneauIndicateurs() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #2c3e50; -fx-border-radius: 5;");

        Label titre = new Label("📊 Tableau de Bord Énergétique");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        GridPane grille = new GridPane();
        grille.setHgap(30);
        grille.setVgap(10);

        Label lblTitreProduction = new Label("Production:");
        lblTitreProduction.setStyle("-fx-text-fill: #3498db; -fx-font-size: 14px;");
        lblProduction = new Label("0.00 kWh");
        lblProduction.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblTitreConsommation = new Label("Consommation:");
        lblTitreConsommation.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
        lblConsommation = new Label("0.00 kWh");
        lblConsommation.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblTitreBilan = new Label("Bilan:");
        lblTitreBilan.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 14px;");
        lblBilan = new Label("0.00 kWh");
        lblBilan.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblTitreTemps = new Label("Temps:");
        lblTitreTemps.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14px;");
        lblTemps = new Label("0 s");
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

    private VBox creerPanneauGraphiques() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(10));

        graphiqueEvolution = creerGraphiqueEvolution();
        graphiqueRepartition = creerGraphiqueRepartition();

        HBox ligneGraphiques = new HBox(10);
        ligneGraphiques.getChildren().addAll(graphiqueEvolution, graphiqueRepartition);

        conteneur.getChildren().add(ligneGraphiques);
        return conteneur;
    }

    private LineChart<Number, Number> creerGraphiqueEvolution() {
        NumberAxis axeX = new NumberAxis();
        axeX.setLabel("Temps (s)");
        axeX.setAutoRanging(true);
        axeX.setForceZeroInRange(false);

        NumberAxis axeY = new NumberAxis();
        axeY.setLabel("Énergie (kWh)");
        axeY.setAutoRanging(true);

        LineChart<Number, Number> graphique = new LineChart<>(axeX, axeY);
        graphique.setTitle("Évolution Production vs Consommation");
        graphique.setPrefSize(600, 300);
        graphique.setCreateSymbols(false);

        serieProduction = new XYChart.Series<>();
        serieProduction.setName("Production");

        serieConsommation = new XYChart.Series<>();
        serieConsommation.setName("Consommation");

        graphique.getData().addAll(serieProduction, serieConsommation);
        return graphique;
    }

    private PieChart creerGraphiqueRepartition() {
        PieChart graphique = new PieChart();
        graphique.setTitle("Répartition Énergétique");
        graphique.setPrefSize(400, 300);
        return graphique;
    }

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

    // ------------------- Animation -------------------
    private void configurerAnimation() {
        timer = new AnimationTimer() {
            private long dernierTemps = 0;

            @Override
            public void handle(long now) {
                if (now - dernierTemps >= 1_000_000_000) { // 1 seconde
                    if (simulationEnCours) {
                        tempsSimulation = (System.currentTimeMillis() - tempsDebut) / 1000.0;

                        // Production exponentielle décroissante
                        production = productionMax * Math.exp(-0.02 * tempsSimulation);

                        // Consommation croissante puis stabilisée
                        consommation = consommationMax * (1 - Math.exp(-0.01 * tempsSimulation));

                        // Ajouter à l'historique
                        RecordSimulation record = new RecordSimulation((int) production, consommation, tempsSimulation);
                        controleur.ajouterRecordSimulation(record);

                        // Mettre à jour interface
                        mettreAJourInterface();
                    }
                    dernierTemps = now;
                }
            }
        };
    }

    private void configurerGestionnaireAlerte() {
        controleur.definirGestionnaireAlerte(message -> {
            Alert alerte = new Alert(Alert.AlertType.INFORMATION);
            alerte.setTitle("⚠️ Alerte Énergie");
            alerte.setHeaderText(null);
            alerte.setContentText(message);
            alerte.showAndWait();
        });
    }

    // ------------------- Contrôles -------------------
    private void demarrerSimulation() {
        simulationEnCours = true;
        tempsDebut = System.currentTimeMillis();
        timer.start();
        btnDemarrer.setDisable(true);
        btnPause.setDisable(false);
    }

    private void pauserSimulation() {
        simulationEnCours = false;
        timer.stop();
        btnDemarrer.setDisable(false);
        btnPause.setDisable(true);
    }

    private void reinitialiserSimulation() {
        pauserSimulation();
        production = productionMax;
        consommation = 0.0;
        serieProduction.getData().clear();
        serieConsommation.getData().clear();
        txtHistorique.clear();
        controleur.viderHistorique();
        mettreAJourInterface();
    }

    // ------------------- Mise à jour interface -------------------
    private void mettreAJourInterface() {
        double bilan = production - consommation;

        lblProduction.setText(String.format("%.2f kWh", production));
        lblConsommation.setText(String.format("%.2f kWh", consommation));
        lblBilan.setText(String.format("%.2f kWh", bilan));
        lblBilan.setStyle(bilan >= 0 ?
                "-fx-text-fill: #2ecc71; -fx-font-size: 18px; -fx-font-weight: bold;" :
                "-fx-text-fill: #e74c3c; -fx-font-size: 18px; -fx-font-weight: bold;"
        );
        lblTemps.setText(String.format("%.1f s", tempsSimulation));

        serieProduction.getData().add(new XYChart.Data<>(tempsSimulation, production));
        serieConsommation.getData().add(new XYChart.Data<>(tempsSimulation, consommation));

        if (serieProduction.getData().size() > 50) {
            serieProduction.getData().remove(0);
            serieConsommation.getData().remove(0);
        }

        mettreAJourGraphiqueRepartition();
        mettreAJourHistorique();
    }

    private void mettreAJourGraphiqueRepartition() {
        graphiqueRepartition.getData().clear();
        PieChart.Data dataProd = new PieChart.Data("Production", production);
        PieChart.Data dataConso = new PieChart.Data("Consommation", consommation);
        graphiqueRepartition.getData().addAll(dataProd, dataConso);
    }

    private void mettreAJourHistorique() {
        List<RecordSimulation> dernieres = controleur.obtenirDernieresSimulations(10);
        StringBuilder sb = new StringBuilder();
        for (RecordSimulation rec : dernieres) {
            sb.append(rec.toString()).append("\n");
        }
        txtHistorique.setText(sb.toString());
    }

    private void afficherStatistiques() {
        String stats = controleur.obtenirStatistiques();
        afficherAlerte("Statistiques de Simulation", stats);
    }

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

    private void afficherAlerte(String titre, String message) {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }
}
