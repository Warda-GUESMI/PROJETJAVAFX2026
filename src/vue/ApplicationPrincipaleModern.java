package vue;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import Controleur.*;
import simulation.modele.simulation.GestionEnergie;

/**
 * ApplicationPrincipaleModern.java
 * Variante "ULTRA MODERNE" de l'écran principal.
 * Style : barre latérale compacte, toolbar épurée, centre dynamique.
 * Compatible Java 17 + JavaFX.
 */
public class ApplicationPrincipaleModern extends Application {

    // --- Modèle & contrôleurs ---
    private GestionEnergie gestionEnergie;
    private ControleurSource controleurSource;
    private ControleurConsommateur controleurConsommateur;
    private ControleurSimulation controleurSimulation;
    private ControleurHistorique controleurHistorique;
    private ControleurAlertes controleurAlertes;
    private ControleurOptimisation controleurOptimisation;

    // --- Vues (peuvent être Node ou Stage) ---
    private VueGestionSource vueSource;
    private VueGestionConsommateur vueConsommateur;
    private VueSimulation vueSimulation;
    private VueHistorique vueHistorique;
    private VueAlertes vueAlertes;
    private VueOptimisation vueOptimisation;

    // Container principal
    private BorderPane root;
    private StackPane centre; // zone qui affiche les vues
    private ToggleGroup navGroup;

    // Barres et statut
    private Label lblStatut;

    @Override
    public void start(Stage primaryStage) {
        initialiserModeleEtControleurs();
        root = new BorderPane();

        root.setLeft(creerBarreLaterale());
        root.setTop(creerToolbar(primaryStage));
        centre = new StackPane();
        centre.setPadding(new Insets(20));
        centre.setStyle("-fx-background-color: linear-gradient(#0f1724, #071021);");

        // Démarrer sur le dashboard moderne
        Node accueil = creerDashboard();
        centre.getChildren().add(accueil);
        root.setCenter(centre);

        root.setBottom(creerBarreStatut());

        Scene scene = new Scene(root, 1400, 900);
        // Police par défaut plus nette
        scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Roboto', 'Helvetica';");

        primaryStage.setTitle("⚡ Système de Gestion Énergétique — Modern UI");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Raccourci : Echap pour quitter (confirmation)
        scene.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.ESCAPE) {
                quitterApplication();
            }
        });
    }

    /**
     * Initialise le modèle et les contrôleurs (MVC).
     */
    private void initialiserModeleEtControleurs() {
        gestionEnergie = new GestionEnergie();
        controleurSource = new ControleurSource(gestionEnergie);
        controleurConsommateur = new ControleurConsommateur(gestionEnergie);
        controleurSimulation = new ControleurSimulation(gestionEnergie);
        controleurHistorique = new ControleurHistorique(controleurSimulation.getHistorique());
        controleurAlertes = new ControleurAlertes(gestionEnergie);
        controleurOptimisation = new ControleurOptimisation(gestionEnergie);

        // Instancier vues (elles peuvent créer leurs propres Stages internes)
        vueSource = new VueGestionSource(controleurSource);
        vueConsommateur = new VueGestionConsommateur(controleurConsommateur);
        vueSimulation = new VueSimulation(controleurSimulation);
        vueHistorique = new VueHistorique(controleurHistorique);
        vueAlertes = new VueAlertes(controleurAlertes);
        vueOptimisation = new VueOptimisation(controleurOptimisation);
    }

    /**
     * Barre latérale moderne (compacte) pour la navigation.
     */
    private VBox creerBarreLaterale() {
        VBox side = new VBox(12);
        side.setPadding(new Insets(18));
        side.setPrefWidth(220);
        side.setStyle(
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #081526 0%, #0b2540 100%);" +
            "-fx-border-width: 0 1 0 0; -fx-border-color: rgba(255,255,255,0.04);"
        );

        Label logo = new Label("⚡ ENICARTHAGE");
        logo.setTextFill(Color.WHITE);
        logo.setFont(Font.font(18));

        // Recherche rapide
        TextField search = new TextField();
        search.setPromptText("Rechercher une source, conso, simulation...");
        search.setStyle("-fx-background-radius: 8; -fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");

        // Nav buttons
        navGroup = new ToggleGroup();
        ToggleButton tbAccueil = createNavButton("Accueil", "🏠", true);
        ToggleButton tbSources = createNavButton("Sources", "⚡", false);
        ToggleButton tbConsos = createNavButton("Consommateurs", "🏠", false);
        ToggleButton tbSim = createNavButton("Simulation", "▶️", false);
        ToggleButton tbHist = createNavButton("Historique", "📜", false);
        ToggleButton tbAlert = createNavButton("Alertes", "🔔", false);
        ToggleButton tbOpt = createNavButton("Optimisation", "🎯", false);

        // Events: basculer le contenu centre
        tbAccueil.setOnAction(e -> setCentre(creerDashboard()));
        tbSources.setOnAction(e -> setCentre(obtenirContenu(vueSource)));
        tbConsos.setOnAction(e -> setCentre(obtenirContenu(vueConsommateur)));
        tbSim.setOnAction(e -> setCentre(obtenirContenu(vueSimulation)));
        tbHist.setOnAction(e -> { setCentre(obtenirContenu(vueHistorique)); vueHistorique.recharger(); });
        tbAlert.setOnAction(e -> { setCentre(obtenirContenu(vueAlertes)); if (vueAlertes != null) vueAlertes.actualiserTout(); });
        tbOpt.setOnAction(e -> setCentre(obtenirContenu(vueOptimisation)));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Minibar bottom : version + quick actions
        HBox bottom = new HBox(8);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(6, 0, 0, 0));
        Label ver = new Label("v1.0");
        ver.setTextFill(Color.web("#bfc7d6"));
        Button btnQuit = new Button("Quitter");
        btnQuit.setOnAction(e -> quitterApplication());
        btnQuit.setStyle("-fx-background-radius: 6; -fx-font-size: 12px;");

        bottom.getChildren().addAll(ver, new Region(), btnQuit);
        HBox.setHgrow(bottom.getChildren().get(1), Priority.ALWAYS);

        side.getChildren().addAll(logo, search, tbAccueil, tbSources, tbConsos, tbSim, tbHist, tbAlert, tbOpt, spacer, bottom);
        return side;
    }

    /**
     * Crée un ToggleButton stylé pour la barre latérale.
     */
    private ToggleButton createNavButton(String text, String icon, boolean selected) {
        ToggleButton btn = new ToggleButton(icon + "  " + text);
        btn.setToggleGroup(navGroup);
        btn.setSelected(selected);
        btn.setPrefWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; " +
            "-fx-padding: 10 12 10 12; -fx-background-radius: 8;"
        );
        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 12 10 12; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 12 10 12; -fx-background-radius: 8;"));
        return btn;
    }

    /**
     * Toolbar moderne en haut avec titre, recherche, profil et actions rapides.
     */
    private HBox creerToolbar(Stage stage) {
        HBox toolbar = new HBox(12);
        toolbar.setPadding(new Insets(12, 18, 12, 18));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-background-color: linear-gradient(#071124, #082033);");

        Label titre = new Label("Gestion Énergétique — Dashboard");
        titre.setTextFill(Color.WHITE);
        titre.setFont(Font.font(16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField quick = new TextField();
        quick.setPromptText("Recherche rapide...");
        quick.setPrefWidth(380);

        Button btnRefresh = new Button("⟳");
        btnRefresh.setOnAction(e -> actualiserVueActive());
        btnRefresh.setStyle("-fx-background-radius: 6; -fx-font-size: 14px;");

        Button btnAide = new Button("? Aide");
        btnAide.setOnAction(e -> afficherGuide());
        btnAide.setStyle("-fx-background-radius: 6; -fx-font-size: 12px;");

        // Profil menu
        MenuButton profile = new MenuButton("Warda");
        profile.getItems().addAll(new MenuItem("Profil"), new MenuItem("Préférences"), new MenuItem("À propos"));
        profile.getItems().get(2).setOnAction(e -> afficherAPropos());

        toolbar.getChildren().addAll(titre, spacer, quick, btnRefresh, btnAide, profile);
        return toolbar;
    }

    /**
     * Définit la zone centrale avec animation.
     */
    private void setCentre(Node node) {
        if (node == null) {
            node = creerPlaceholder("Contenu indisponible");
        }

        // Animation fade entre contenus
        if (!centre.getChildren().isEmpty()) {
            Node old = centre.getChildren().get(0);
            FadeTransition ftOut = new FadeTransition(Duration.millis(180), old);
            ftOut.setFromValue(1.0);
            ftOut.setToValue(0.0);
            ftOut.setOnFinished(evt -> centre.getChildren().remove(old));
            ftOut.play();
        }

        node.setOpacity(0);
        centre.getChildren().add(node);
        FadeTransition ftIn = new FadeTransition(Duration.millis(220), node);
        ftIn.setFromValue(0.0);
        ftIn.setToValue(1.0);
        ftIn.play();

        // léger effet de scale pour pop-in
        ScaleTransition st = new ScaleTransition(Duration.millis(220), node);
        st.setFromX(0.995);
        st.setFromY(0.995);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    /**
     * Crée un Dashboard moderne (cartes cliquables) — version compacte.
     */
    private Node creerDashboard() {
        VBox rootDash = new VBox(24);
        rootDash.setPadding(new Insets(24));

        Label titre = new Label("Tableau de bord");
        titre.setTextFill(Color.WHITE);
        titre.setFont(Font.font(26));

        FlowPane cartes = new FlowPane();
        cartes.setHgap(18);
        cartes.setVgap(18);

        cartes.getChildren().addAll(
            creerCarteSmall("⚡ Sources", "Gérer les sources", () -> setCentre(obtenirContenu(vueSource))),
            creerCarteSmall("🏠 Consommateurs", "Gérer les consommateurs", () -> setCentre(obtenirContenu(vueConsommateur))),
            creerCarteSmall("▶️ Simulation", "Lancer une simulation", () -> setCentre(obtenirContenu(vueSimulation))),
            creerCarteSmall("📜 Historique", "Historique des simulations", () -> { setCentre(obtenirContenu(vueHistorique)); vueHistorique.recharger(); }),
            creerCarteSmall("🔔 Alertes", "Voir les alertes", () -> { setCentre(obtenirContenu(vueAlertes)); if (vueAlertes != null) vueAlertes.actualiserTout(); }),
            creerCarteSmall("🎯 Optimisation", "Recommandations", () -> setCentre(obtenirContenu(vueOptimisation)))
        );

        rootDash.getChildren().addAll(titre, cartes);
        return rootDash;
    }

    private VBox creerCarteSmall(String titre, String sous, Runnable action) {
        VBox carte = new VBox(8);
        carte.setPadding(new Insets(18));
        carte.setPrefSize(260, 120);
        carte.setStyle(
            "-fx-background-color: linear-gradient(#133044, #0d2536); -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 6);"
        );
        Label t = new Label(titre);
        t.setTextFill(Color.WHITE);
        t.setFont(Font.font(16));
        Label s = new Label(sous);
        s.setTextFill(Color.web("#bcd3e6"));
        Button btn = new Button("Ouvrir");
        btn.setOnAction(e -> action.run());
        btn.setStyle("-fx-background-radius: 8; -fx-font-size: 12px;");
        carte.getChildren().addAll(t, s, new Region(), btn);
        VBox.setVgrow(carte.getChildren().get(2), Priority.ALWAYS);
        carte.setOnMouseEntered(e -> carte.setScaleX(1.02));
        carte.setOnMouseExited(e -> carte.setScaleX(1.0));
        return carte;
    }

    /**
     * Obtient le contenu d'une vue (Node ou Stage) — réutilisé.
     */
    private Node obtenirContenu(Object vue) {
        if (vue == null) return creerPlaceholder("Vue non disponible");
        if (vue instanceof Node) return (Node) vue;
        if (vue instanceof Stage) {
            Stage s = (Stage) vue;
            if (s.getScene() != null && s.getScene().getRoot() != null) return s.getScene().getRoot();
        }
        // Certains composants (ex: VueHistorique) peuvent être des Controls personnalisés
        try {
            return (Node) vue;
        } catch (Exception ex) {
            return creerPlaceholder("Type de vue non supporté: " + vue.getClass().getSimpleName());
        }
    }

    /**
     * Placeholder simple
     */
    private VBox creerPlaceholder(String message) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        Label lbl = new Label(message);
        lbl.setTextFill(Color.web("#c6cbd6"));
        lbl.setFont(Font.font(14));
        box.getChildren().add(lbl);
        return box;
    }

    /**
     * Barre de statut minimaliste
     */
    private HBox creerBarreStatut() {
        HBox barre = new HBox(12);
        barre.setPadding(new Insets(10));
        barre.setStyle("-fx-background-color: linear-gradient(#091220, #07101a); -fx-border-color: rgba(255,255,255,0.03); -fx-border-width: 1 0 0 0;");
        lblStatut = new Label("✓ Système prêt");
        lblStatut.setTextFill(Color.web("#84e39d"));
        Label info = new Label("Sources: 0  •  Consommateurs: 0  •  Simulations: 0");
        info.setTextFill(Color.web("#a7b6c6"));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label ver = new Label("ENICARTHAGE • v1.0");
        ver.setTextFill(Color.web("#8f9bb0"));
        barre.getChildren().addAll(lblStatut, info, spacer, ver);
        return barre;
    }

    /**
     * Actualise la vue active (selon bouton actif).
     */
    private void actualiserVueActive() {
        Toggle selected = navGroup.getSelectedToggle();
        if (selected == null) {
            afficherNotification("Aucune vue sélectionnée");
            return;
        }
        String text = ((ToggleButton) selected).getText();
        if (text.contains("Historique")) {
            vueHistorique.recharger();
        } else if (text.contains("Alertes")) {
            if (vueAlertes != null) vueAlertes.actualiserTout();
        }
        afficherNotification("✓ Vue actualisée");
    }

    /**
     * Boîte À propos
     */
    private void afficherAPropos() {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle("À propos");
        alerte.setHeaderText("Système de Gestion Énergétique — Modern UI");
        alerte.setContentText("Mini-Projet ENICARTHAGE 2025-2026\nTech: Java 17 + JavaFX — Theme moderne");
        alerte.showAndWait();
    }

    /**
     * Guide d'utilisation simple
     */
    private void afficherGuide() {
        Alert guide = new Alert(Alert.AlertType.INFORMATION);
        guide.setTitle("Guide d'utilisation");
        guide.setHeaderText("Raccourcis & étapes");
        guide.setContentText("Utilisez la barre latérale pour naviguer. Appuyez sur ESC pour quitter. Cliquez sur 'Refresh' pour actualiser la vue active.");
        guide.showAndWait();
    }

    private void afficherNotification(String message) {
        // Log console + mise à jour statut (simple)
        System.out.println("📢 " + message);
        if (lblStatut != null) lblStatut.setText(message);
    }

    private void quitterApplication() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Quitter");
        confirmation.setHeaderText("Êtes-vous sûr?");
        confirmation.setContentText("Voulez-vous quitter l'application?");
        confirmation.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
