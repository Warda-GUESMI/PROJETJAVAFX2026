package vue;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Controleur.*;
import simulation.modele.simulation.GestionEnergie;

/**
 * Classe principale de l'application JavaFX.
 * Toutes les vues sont intégrées dans un TabPane central.
 */
public class ApplicationPrincipale extends Application {
    
    // Modèle et contrôleurs partagés
    private GestionEnergie gestionEnergie;
    private ControleurSource controleurSource;
    private ControleurConsommateur controleurConsommateur;
    private ControleurSimulation controleurSimulation;
    private ControleurHistorique controleurHistorique;
    private ControleurAlertes controleurAlertes;
    private ControleurOptimisation controleurOptimisation;
    
    // Vues intégrées (certaines sont des Stage, d'autres des Node)
    private VueGestionSource vueSource;
    private VueGestionConsommateur vueConsommateur;
    private VueSimulation vueSimulation;
    private VueHistorique vueHistorique;
    private VueAlertes vueAlertes;
    private VueOptimisation vueOptimisation;
    
    // Références aux contenus extraits pour actualisation
    private BorderPane contenuSource;
    private BorderPane contenuConsommateur;
    private BorderPane contenuSimulation;
    
    // TabPane principal
    private TabPane tabPane;
    
    /**
     * Point d'entrée JavaFX.
     * @param primaryStage La fenêtre principale
     */
    @Override
    public void start(Stage primaryStage) {
        initialiserModeleEtControleurs();
        
        primaryStage.setTitle("? Système de Gestion Énergétique ");
        primaryStage.setScene(creerScenePrincipale());
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    /**
     * Initialise le modèle et tous les contrôleurs.
     */
    private void initialiserModeleEtControleurs() {
        // Modèle unique partagé par tous les contrôleurs (MVC)
        gestionEnergie = new GestionEnergie();
        
        // Contrôleurs
        controleurSource = new ControleurSource(gestionEnergie);
        controleurConsommateur = new ControleurConsommateur(gestionEnergie);
        controleurSimulation = new ControleurSimulation(gestionEnergie);
        controleurHistorique = new ControleurHistorique(controleurSimulation.getHistorique());
        controleurAlertes = new ControleurAlertes(gestionEnergie);
        controleurOptimisation = new ControleurOptimisation(gestionEnergie);
    }
    
    /**
     * Crée la scène principale avec toutes les vues intégrées.
     * @return La scène configurée
     */
    private Scene creerScenePrincipale() {
        BorderPane racine = new BorderPane();
        
        // Barre de menu en haut
        MenuBar barreMenu = creerBarreMenu();
        racine.setTop(barreMenu);
        
        // TabPane central contenant toutes les vues
        tabPane = creerTabPane();
        racine.setCenter(tabPane);
        
        // Barre de statut en bas
        HBox barreStatut = creerBarreStatut();
        racine.setBottom(barreStatut);
        
        Scene scene = new Scene(racine, 1600, 900);
        return scene;
    }
    
    /**
     * Crée le TabPane contenant toutes les vues.
     * @return Le TabPane configuré
     */
    private TabPane creerTabPane() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Onglet 1 : Accueil / Dashboard
        Tab tabAccueil = new Tab("Accueil");
        tabAccueil.setContent(creerVueAccueil());
        
        // Onglet 2 : Gestion des Sources d'Énergie
        Tab tabSources = new Tab("Sources d'Énergie");
        vueSource = new VueGestionSource(controleurSource);
        // Extraire le contenu du Stage (BorderPane racine)
        if (vueSource.getScene() != null && vueSource.getScene().getRoot() != null) {
            contenuSource = (BorderPane) vueSource.getScene().getRoot();
            ScrollPane scrollSources = new ScrollPane(contenuSource);
            scrollSources.setFitToWidth(true);
            scrollSources.setFitToHeight(true);
            tabSources.setContent(scrollSources);
        } else {
            tabSources.setContent(creerPlaceholder("Erreur de chargement de la vue Sources"));
        }
        
        // Onglet 3 : Gestion des Consommateurs
        Tab tabConsommateurs = new Tab("?Consommateurs");
        vueConsommateur = new VueGestionConsommateur(controleurConsommateur);
        // Extraire le contenu du Stage
        if (vueConsommateur.getScene() != null && vueConsommateur.getScene().getRoot() != null) {
            contenuConsommateur = (BorderPane) vueConsommateur.getScene().getRoot();
            ScrollPane scrollConsommateurs = new ScrollPane(contenuConsommateur);
            scrollConsommateurs.setFitToWidth(true);
            scrollConsommateurs.setFitToHeight(true);
            tabConsommateurs.setContent(scrollConsommateurs);
        } else {
            tabConsommateurs.setContent(creerPlaceholder("Erreur de chargement de la vue Consommateurs"));
        }
        
        // Onglet 4 : Simulation
        Tab tabSimulation = new Tab(" Simulation");
        vueSimulation = new VueSimulation(controleurSimulation);
        // Extraire le contenu du Stage
        if (vueSimulation.getScene() != null && vueSimulation.getScene().getRoot() != null) {
            contenuSimulation = (BorderPane) vueSimulation.getScene().getRoot();
            ScrollPane scrollSimulation = new ScrollPane(contenuSimulation);
            scrollSimulation.setFitToWidth(true);
            scrollSimulation.setFitToHeight(true);
            tabSimulation.setContent(scrollSimulation);
        } else {
            tabSimulation.setContent(creerPlaceholder("Erreur de chargement de la vue Simulation"));
        }
        
        // Onglet 5 : Historique
        Tab tabHistorique = new Tab(" Historique");
        vueHistorique = new VueHistorique(controleurHistorique);
        ScrollPane scrollHistorique = new ScrollPane(vueHistorique);
        scrollHistorique.setFitToWidth(true);
        scrollHistorique.setFitToHeight(true);
        tabHistorique.setContent(scrollHistorique);
        
        // Onglet 6 : Alertes
        Tab tabAlertes = new Tab("Alertes");
        vueAlertes = new VueAlertes(controleurAlertes);
        // Extraire le contenu du Stage
        if (vueAlertes.getScene() != null && vueAlertes.getScene().getRoot() != null) {
            BorderPane contenuAlertes = (BorderPane) vueAlertes.getScene().getRoot();
            ScrollPane scrollAlertes = new ScrollPane(contenuAlertes);
            scrollAlertes.setFitToWidth(true);
            scrollAlertes.setFitToHeight(true);
            tabAlertes.setContent(scrollAlertes);
        } else {
            tabAlertes.setContent(creerPlaceholder("Erreur de chargement de la vue Alertes"));
        }
        
        // Onglet 7 : Optimisation
        Tab tabOptimisation = new Tab(" Optimisation");
        vueOptimisation = new VueOptimisation(controleurOptimisation);
        // Extraire le contenu du Stage
        if (vueOptimisation.getScene() != null && vueOptimisation.getScene().getRoot() != null) {
            BorderPane contenuOptimisation = (BorderPane) vueOptimisation.getScene().getRoot();
            ScrollPane scrollOptimisation = new ScrollPane(contenuOptimisation);
            scrollOptimisation.setFitToWidth(true);
            scrollOptimisation.setFitToHeight(true);
            tabOptimisation.setContent(scrollOptimisation);
        } else {
            tabOptimisation.setContent(creerPlaceholder("Erreur de chargement de la vue Optimisation"));
        }
        
        tabs.getTabs().addAll(
            tabAccueil, 
            tabSources, 
            tabConsommateurs, 
            tabSimulation, 
            tabHistorique, 
            tabAlertes, 
            tabOptimisation
        );
        
        // Listener pour actualiser les vues au changement d'onglet
        tabs.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == tabHistorique) {
                vueHistorique.recharger();
            } else if (newTab == tabAlertes) {
                if (vueAlertes != null) {
                    vueAlertes.actualiserTout();
                }
            }
        });
        
        return tabs;
    }
    
    /**
     * Crée la vue d'accueil / Dashboard.
     * @return Le nœud contenant le dashboard
     */
    private VBox creerVueAccueil() {
        VBox conteneur = new VBox(40);
        conteneur.setAlignment(Pos.TOP_CENTER);
        conteneur.setPadding(new Insets(60, 40, 40, 40));
        // Gradient vibrant et moderne avec syntaxe JavaFX correcte
        conteneur.setStyle(
            "-fx-background-color: " +
            "linear-gradient(from 0% 0% to 100% 100%, " +
            "#FA8BFF 0%, " +
            "#2BD2FF 52%, " +
            "#2BFF88 100%);"
        );
        
        // Titre principal avec effet glassmorphism
        VBox headerBox = new VBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(30, 50, 30, 50));
        headerBox.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.25); " +
            "-fx-background-radius: 25; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5); " +
            "-fx-border-color: rgba(255, 255, 255, 0.4); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 25;"
        );
        
        Label titre = new Label("⚡ Gestion Énergétique Intelligente");
        titre.setStyle(
            "-fx-font-size: 52px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);"
        );
        
        Label sousTitre = new Label("🚀 Plateforme de Simulation & Optimisation en Temps Réel");
        sousTitre.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: 600;"
        );
        
        headerBox.getChildren().addAll(titre, sousTitre);
        
        // Grid de cartes modernes avec couleurs vibrantes
        GridPane grille = new GridPane();
        grille.setHgap(30);
        grille.setVgap(30);
        grille.setAlignment(Pos.CENTER);
        grille.setPadding(new Insets(20, 0, 0, 0));
        
        // Section Gestion - Couleurs vibrantes
        VBox carteGestion = creerCarteModerne(
            "⚡", "Sources d'Énergie", 
            "Gérez vos sources solaires, éoliennes et batteries",
            "#FF6B6B", "#FFE66D", 1  // Rouge corail vers jaune
        );
        
        VBox carteConsommateurs = creerCarteModerne(
            "🏠", "Consommateurs", 
            "Configurez et surveillez vos consommateurs",
            "#4ECDC4", "#44E5E7", 2  // Turquoise vif
        );
        
        // Section Simulation
        VBox carteSimulation = creerCarteModerne(
            "▶️", "Simulation", 
            "Lancez des simulations en temps réel",
            "#A8E6CF", "#56EEF4", 3  // Vert menthe vers bleu ciel
        );
        
        VBox carteHistorique = creerCarteModerne(
            "📜", "Historique", 
            "Consultez l'historique des simulations",
            "#FFB6D9", "#D4A5FF", 4  // Rose vers violet clair
        );
        
        // Section Analyse
        VBox carteAlertes = creerCarteModerne(
            "🔔", "Centre des Alertes", 
            "Notifications et alertes en temps réel",
            "#FF8B94", "#FFC6C7", 5  // Rose saumon
        );
        
        VBox carteOptimisation = creerCarteModerne(
            "🎯", "Optimisation", 
            "Recommandations intelligentes",
            "#B4A7D6", "#8EC5FC", 6  // Lavande vers bleu clair
        );
        
        // Disposition en grille 3x2
        grille.add(carteGestion, 0, 0);
        grille.add(carteConsommateurs, 1, 0);
        grille.add(carteSimulation, 2, 0);
        grille.add(carteHistorique, 0, 1);
        grille.add(carteAlertes, 1, 1);
        grille.add(carteOptimisation, 2, 1);
        
        // Footer moderne
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 30, 15, 30));
        footer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.15); " +
            "-fx-background-radius: 20; " +
            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 20;"
        );
        
        Label lblInfo = new Label("🎓 Mini-Projet Java 2025-2026");
        lblInfo.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: 600;"
        );
        
        Label lblSep = new Label("•");
        lblSep.setStyle("-fx-text-fill: rgba(255,255,255,0.6); -fx-font-size: 16px;");
        
        Label lblEcole = new Label("ENICARTHAGE");
        lblEcole.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: 600;"
        );
        
        footer.getChildren().addAll(lblInfo, lblSep, lblEcole);
        
        conteneur.getChildren().addAll(headerBox, grille, footer);
        
        return conteneur;
    }
    
    /**
     * Crée une carte moderne cliquable pour le dashboard.
     */
    private VBox creerCarteModerne(String icone, String titre, String description, 
                                    String couleur1, String couleur2, int indexOnglet) {
        VBox carte = new VBox(20);
        carte.setAlignment(Pos.CENTER);
        carte.setPadding(new Insets(35, 25, 35, 25));
        carte.setPrefSize(300, 240);
        carte.setMaxSize(300, 240);
        // Syntaxe JavaFX correcte pour linear-gradient
        carte.setStyle(String.format(
            "-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 100%%, %s 0%%, %s 100%%); " +
            "-fx-background-radius: 25; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 25, 0, 0, 8); " +
            "-fx-cursor: hand; " +
            "-fx-border-color: rgba(255, 255, 255, 0.5); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 25;",
            couleur1, couleur2
        ));
        
        // Conteneur de l'icône avec effet glassmorphism
        StackPane iconeContainer = new StackPane();
        iconeContainer.setPrefSize(90, 90);
        iconeContainer.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.35); " +
            "-fx-background-radius: 45; " +
            "-fx-border-color: rgba(255, 255, 255, 0.6); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 45; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5);"
        );
        
        Label lblIcone = new Label(icone);
        lblIcone.setStyle("-fx-font-size: 48px;");
        iconeContainer.getChildren().add(lblIcone);
        
        // Titre
        Label lblTitre = new Label(titre);
        lblTitre.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
        );
        lblTitre.setWrapText(true);
        lblTitre.setAlignment(Pos.CENTER);
        lblTitre.setMaxWidth(250);
        
        // Description
        Label lblDesc = new Label(description);
        lblDesc.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: white; " +
            "-fx-text-alignment: center; " +
            "-fx-font-weight: 500;"
        );
        lblDesc.setWrapText(true);
        lblDesc.setAlignment(Pos.CENTER);
        lblDesc.setMaxWidth(260);
        
        carte.getChildren().addAll(iconeContainer, lblTitre, lblDesc);
        
        // Effets hover animés
        carte.setOnMouseEntered(e -> {
            carte.setStyle(String.format(
                "-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 100%%, %s 0%%, %s 100%%); " +
                "-fx-background-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 35, 0, 0, 12); " +
                "-fx-cursor: hand; " +
                "-fx-scale-x: 1.08; " +
                "-fx-scale-y: 1.08; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 25;",
                couleur1, couleur2
            ));
            
            iconeContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.5); " +
                "-fx-background-radius: 45; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 45; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); " +
                "-fx-scale-x: 1.1; " +
                "-fx-scale-y: 1.1;"
            );
            
            lblTitre.setStyle(
                "-fx-font-size: 23px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);"
            );
        });
        
        carte.setOnMouseExited(e -> {
            carte.setStyle(String.format(
                "-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 100%%, %s 0%%, %s 100%%); " +
                "-fx-background-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 25, 0, 0, 8); " +
                "-fx-cursor: hand; " +
                "-fx-scale-x: 1.0; " +
                "-fx-scale-y: 1.0; " +
                "-fx-border-color: rgba(255, 255, 255, 0.5); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 25;",
                couleur1, couleur2
            ));
            
            iconeContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.35); " +
                "-fx-background-radius: 45; " +
                "-fx-border-color: rgba(255, 255, 255, 0.6); " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 45; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 5); " +
                "-fx-scale-x: 1.0; " +
                "-fx-scale-y: 1.0;"
            );
            
            lblTitre.setStyle(
                "-fx-font-size: 22px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: white; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
            );
        });
        
        // Action au clic avec animation
        carte.setOnMousePressed(e -> {
            carte.setStyle(String.format(
                "-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 100%%, %s 0%%, %s 100%%); " +
                "-fx-background-radius: 25; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5); " +
                "-fx-cursor: hand; " +
                "-fx-scale-x: 1.05; " +
                "-fx-scale-y: 1.05; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 25;",
                couleur1, couleur2
            ));
        });
        
        carte.setOnMouseReleased(e -> {
            tabPane.getSelectionModel().select(indexOnglet);
        });
        
        return carte;
    }
    
    /**
     * Crée un wrapper pour VueAlertes (qui est un Stage).
     * Extrait le contenu de la scène du Stage.
     */
    private Region creerWrapperAlertes() {
        try {
            // Si VueAlertes a une méthode pour obtenir son contenu principal
            if (vueAlertes != null && vueAlertes.getScene() != null) {
                return (Region) vueAlertes.getScene().getRoot();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'intégration de VueAlertes: " + e.getMessage());
        }
        
        // Fallback : afficher un message
        VBox wrapper = new VBox(20);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(50));
        
        Label titre = new Label("🔔 Centre des Alertes");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label message = new Label("La vue Alertes doit être modifiée pour s'intégrer dans un onglet.");
        message.setStyle("-fx-font-size: 14px;");
        
        Button btnOuvrirFenetre = new Button("Ouvrir dans une fenêtre séparée");
        btnOuvrirFenetre.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px;");
        btnOuvrirFenetre.setOnAction(e -> {
            if (!vueAlertes.isShowing()) {
                vueAlertes.show();
            } else {
                vueAlertes.toFront();
            }
        });
        
        wrapper.getChildren().addAll(titre, message, btnOuvrirFenetre);
        return wrapper;
    }
    
    /**
     * Obtient le contenu d'une vue (gère les différents types de vues).
     * @param vue La vue (peut être Stage, VBox, ou autre)
     * @return Le Node à afficher
     */
    private javafx.scene.Node obtenirContenu(Object vue) {
        if (vue == null) {
            return creerPlaceholder("Vue non disponible");
        }
        
        // Si c'est déjà un Node, le retourner directement
        if (vue instanceof javafx.scene.Node) {
            return (javafx.scene.Node) vue;
        }
        
        // Si c'est un Stage, extraire le contenu de sa scène
        if (vue instanceof Stage) {
            Stage stage = (Stage) vue;
            if (stage.getScene() != null && stage.getScene().getRoot() != null) {
                return stage.getScene().getRoot();
            }
        }
        
        // Sinon, créer un placeholder
        return creerPlaceholder("Type de vue non supporté: " + vue.getClass().getSimpleName());
    }
    
    /**
     * Crée un placeholder avec un message.
     */
    private VBox creerPlaceholder(String message) {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50));
        
        Label label = new Label("⚠️ " + message);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        box.getChildren().add(label);
        return box;
    }
    
    /**
     * Crée un wrapper pour VueOptimisation (qui est un Stage).
     * Extrait le contenu de la scène du Stage.
     */
    private Region creerWrapperOptimisation() {
        try {
            // Si VueOptimisation a une méthode pour obtenir son contenu principal
            if (vueOptimisation != null && vueOptimisation.getScene() != null) {
                return (Region) vueOptimisation.getScene().getRoot();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'intégration de VueOptimisation: " + e.getMessage());
        }
        
        // Fallback : afficher un message
        VBox wrapper = new VBox(20);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(50));
        
        Label titre = new Label("🎯 Centre d'Optimisation");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label message = new Label("La vue Optimisation doit être modifiée pour s'intégrer dans un onglet.");
        message.setStyle("-fx-font-size: 14px;");
        
        Button btnOuvrirFenetre = new Button("Ouvrir dans une fenêtre séparée");
        btnOuvrirFenetre.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 14px;");
        btnOuvrirFenetre.setOnAction(e -> {
            if (!vueOptimisation.isShowing()) {
                vueOptimisation.show();
            } else {
                vueOptimisation.toFront();
            }
        });
        
        wrapper.getChildren().addAll(titre, message, btnOuvrirFenetre);
        return wrapper;
    }
    
    /**
     * Crée la barre de menu en haut.
     * @return La barre de menu configurée
     */
    private MenuBar creerBarreMenu() {
        MenuBar barreMenu = new MenuBar();
        
        // Menu Fichier
        Menu menuFichier = new Menu("Fichier");
        MenuItem itemQuitter = new MenuItem("Quitter");
        itemQuitter.setOnAction(e -> quitterApplication());
        menuFichier.getItems().add(itemQuitter);
        
        // Menu Navigation
        Menu menuNav = new Menu("Navigation");
        MenuItem itemAccueil = new MenuItem("Accueil");
        itemAccueil.setOnAction(e -> tabPane.getSelectionModel().select(0));
        MenuItem itemSources = new MenuItem("Sources d'Énergie");
        itemSources.setOnAction(e -> tabPane.getSelectionModel().select(1));
        MenuItem itemConsommateurs = new MenuItem("Consommateurs");
        itemConsommateurs.setOnAction(e -> tabPane.getSelectionModel().select(2));
        MenuItem itemSimulation = new MenuItem("Simulation");
        itemSimulation.setOnAction(e -> tabPane.getSelectionModel().select(3));
        MenuItem itemHistorique = new MenuItem("Historique");
        itemHistorique.setOnAction(e -> tabPane.getSelectionModel().select(4));
        MenuItem itemAlertes = new MenuItem("Alertes");
        itemAlertes.setOnAction(e -> tabPane.getSelectionModel().select(5));
        MenuItem itemOptimisation = new MenuItem("Optimisation");
        itemOptimisation.setOnAction(e -> tabPane.getSelectionModel().select(6));
        menuNav.getItems().addAll(
            itemAccueil, itemSources, itemConsommateurs, 
            itemSimulation, itemHistorique, itemAlertes, itemOptimisation
        );
        
        // Menu Affichage
        Menu menuAffichage = new Menu("Affichage");
        MenuItem itemPleinEcran = new MenuItem("Plein écran");
        itemPleinEcran.setOnAction(e -> {
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        });
        MenuItem itemActualiser = new MenuItem("Actualiser");
        itemActualiser.setOnAction(e -> actualiserVueActive());
        menuAffichage.getItems().addAll(itemPleinEcran, itemActualiser);
        
        // Menu Aide
        Menu menuAide = new Menu("Aide");
        MenuItem itemAPropos = new MenuItem("À propos");
        itemAPropos.setOnAction(e -> afficherAPropos());
        MenuItem itemGuide = new MenuItem("Guide d'utilisation");
        itemGuide.setOnAction(e -> afficherGuide());
        menuAide.getItems().addAll(itemAPropos, itemGuide);
        
        barreMenu.getMenus().addAll(menuFichier, menuNav, menuAffichage, menuAide);
        
        return barreMenu;
    }
    
    /**
     * Crée la barre de statut en bas.
     */
    private HBox creerBarreStatut() {
        HBox barre = new HBox(20);
        barre.setPadding(new Insets(5, 10, 5, 10));
        barre.setAlignment(Pos.CENTER_LEFT);
        barre.setStyle("-fx-background-color: #34495e; -fx-border-color: #2c3e50; -fx-border-width: 1 0 0 0;");
        
        Label lblStatut = new Label("✅ Système prêt");
        lblStatut.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        Label lblSources = new Label("Sources: 0");
        lblSources.setStyle("-fx-text-fill: white;");
        
        Separator sep2 = new Separator();
        sep2.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        Label lblConso = new Label("Consommateurs: 0");
        lblConso.setStyle("-fx-text-fill: white;");
        
        Separator sep3 = new Separator();
        sep3.setOrientation(javafx.geometry.Orientation.VERTICAL);
        
        Label lblSimulations = new Label("Simulations: 0");
        lblSimulations.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblVersion = new Label("v1.0 | ENICARTHAGE");
        lblVersion.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 10px;");
        
        barre.getChildren().addAll(
            lblStatut, sep1, lblSources, sep2, lblConso, sep3, lblSimulations, spacer, lblVersion
        );
        
        return barre;
    }
    
    /**
     * Actualise la vue active.
     */
    private void actualiserVueActive() {
        int index = tabPane.getSelectionModel().getSelectedIndex();
        switch (index) {
            case 4: // Historique
                vueHistorique.recharger();
                break;
            case 5: // Alertes
                vueAlertes.actualiserTout();
                break;
            case 6: // Optimisation
                // vueOptimisation.lancerAnalyse();
                break;
        }
        
        afficherNotification("✓ Vue actualisée");
    }
    
    /**
     * Affiche la boîte de dialogue "À propos".
     */
    private void afficherAPropos() {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle("À propos");
        alerte.setHeaderText("Système de Gestion Énergétique");
        alerte.setContentText(
            "Application de simulation énergétique\n\n" +
            "Fonctionnalités :\n" +
            "• Gestion des sources d'énergie (Solaire, Éolien, Batteries)\n" +
            "• Gestion des consommateurs\n" +
            "• Simulation en temps réel avec graphiques\n" +
            "• Centre des alertes énergétiques\n" +
            "• Optimisation et recommandations\n" +
            "• Historique des simulations\n\n" +
            "Technologies : Java 17 + JavaFX\n" +
            "Pattern : MVC (Model-View-Controller)\n\n" +
            "Mini-Projet Programmation Avancée en Java\n" +
            "ENICARTHAGE 2025-2026"
        );
        alerte.showAndWait();
    }
    
    /**
     * Affiche le guide d'utilisation.
     */
    private void afficherGuide() {
        Alert guide = new Alert(Alert.AlertType.INFORMATION);
        guide.setTitle("Guide d'utilisation");
        guide.setHeaderText("Comment utiliser l'application");
        guide.setContentText(
            "ÉTAPES :\n\n" +
            "1. Ajoutez des sources d'énergie (onglet ⚡)\n" +
            "   → Solaire, Éolien, ou Batteries\n\n" +
            "2. Ajoutez des consommateurs (onglet 🏠)\n" +
            "   → Définissez leur consommation\n\n" +
            "3. Lancez une simulation (onglet ▶️)\n" +
            "   → Observez la production vs consommation\n\n" +
            "4. Consultez l'historique (onglet 📜)\n" +
            "   → Analysez les résultats passés\n\n" +
            "5. Surveillez les alertes (onglet 🔔)\n" +
            "   → Gérez les dépassements de seuils\n\n" +
            "6. Optimisez le système (onglet 🎯)\n" +
            "   → Suivez les recommandations"
        );
        guide.showAndWait();
    }
    
    /**
     * Affiche une notification temporaire.
     */
    private void afficherNotification(String message) {
        // TODO: Implémenter un système de notification toast
        System.out.println("📢 " + message);
    }
    
    /**
     * Quitte l'application proprement.
     */
    private void quitterApplication() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Quitter l'application");
        confirmation.setContentText("Êtes-vous sûr de vouloir quitter ?");
        
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                System.exit(0);
            }
        });
    }
    
    /**
     * Point d'entrée de l'application.
     * @param args Arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}