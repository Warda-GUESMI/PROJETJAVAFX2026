package vue;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Controleur.ControleurAlertes;
import simulation.modele.simulation.AlerteRecord;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Interface graphique pour gérer et visualiser les alertes énergétiques.
 * Centre de notifications avec historique complet.
 */
public class VueAlertes extends Stage {
    
    private final ControleurAlertes controleur;
    
    // Composants graphiques
    private TableView<AlerteRecord> tableauAlertes;
    private ListView<String> listeNotifications;
    private Label lblNombreAlertes, lblDerniereAlerte, lblAlertesActives;
    private TextField txtSeuilConsommation, txtSeuilProduction, txtSeuilBatterie;
    private CheckBox chkNotificationsSonores, chkNotificationsVisuelles, chkAutoResolve;
    private ProgressBar progressSeverite;
    private TextArea txtDetailsAlerte;
    
    /**
     * Constructeur de la vue.
     * @param controleur Le contrôleur associé
     */
    public VueAlertes(ControleurAlertes controleur) {
        this.controleur = controleur;
        initialiserInterface();
        configurerEcouteurs();
        this.setTitle("🔔 Centre de Gestion des Alertes");
    }
    
    /**
     * Initialise tous les composants de l'interface.
     */
    private void initialiserInterface() {
        BorderPane racine = new BorderPane();
        racine.setPadding(new Insets(10));
        
        // Partie supérieure : Statistiques des alertes
        racine.setTop(creerPanneauStatistiques());
        
        // Partie centrale : Tableau des alertes
        racine.setCenter(creerPanneauCentral());
        
        // Partie droite : Configuration et notifications
        racine.setRight(creerPanneauConfiguration());
        
        // Partie inférieure : Contrôles
        racine.setBottom(creerPanneauControles());
        
        Scene scene = new Scene(racine, 1300, 750);
        this.setScene(scene);
        
        // Charger les données initiales
        actualiserTout();
    }
    
    /**
     * Crée le panneau des statistiques des alertes.
     * @return Le nœud contenant les statistiques
     */
    private VBox creerPanneauStatistiques() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); -fx-background-radius: 5;");
        
        Label titre = new Label("🔔 Tableau de Bord des Alertes");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        GridPane grille = new GridPane();
        grille.setHgap(40);
        grille.setVgap(10);
        grille.setPadding(new Insets(10, 0, 0, 0));
        
        // Nombre total d'alertes
        Label lblTitreNombre = new Label("Total d'alertes:");
        lblTitreNombre.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        lblNombreAlertes = new Label("0");
        lblNombreAlertes.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Alertes actives
        Label lblTitreActives = new Label("Alertes actives:");
        lblTitreActives.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        lblAlertesActives = new Label("0");
        lblAlertesActives.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Dernière alerte
        Label lblTitreDerniere = new Label("Dernière alerte:");
        lblTitreDerniere.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        lblDerniereAlerte = new Label("Aucune");
        lblDerniereAlerte.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Barre de sévérité
        Label lblSeverite = new Label("Niveau de sévérité:");
        lblSeverite.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        progressSeverite = new ProgressBar(0);
        progressSeverite.setPrefWidth(200);
        progressSeverite.setStyle("-fx-accent: #f39c12;");
        
        grille.add(lblTitreNombre, 0, 0);
        grille.add(lblNombreAlertes, 0, 1);
        grille.add(lblTitreActives, 1, 0);
        grille.add(lblAlertesActives, 1, 1);
        grille.add(lblTitreDerniere, 2, 0);
        grille.add(lblDerniereAlerte, 2, 1);
        grille.add(lblSeverite, 3, 0);
        grille.add(progressSeverite, 3, 1);
        
        conteneur.getChildren().addAll(titre, grille);
        return conteneur;
    }
    
    /**
     * Crée le panneau central avec le tableau et les notifications.
     * @return Le nœud contenant le panneau central
     */
    private HBox creerPanneauCentral() {
        HBox conteneur = new HBox(10);
        conteneur.setPadding(new Insets(10));
        
        // Tableau des alertes (70%)
        VBox panneauTableau = creerTableauAlertes();
        HBox.setHgrow(panneauTableau, Priority.ALWAYS);
        
        // Liste des notifications en temps réel (30%)
        VBox panneauNotifications = creerPanneauNotifications();
        panneauNotifications.setPrefWidth(350);
        
        conteneur.getChildren().addAll(panneauTableau, panneauNotifications);
        return conteneur;
    }
    
    /**
     * Crée le tableau affichant les alertes.
     * @return Le nœud contenant le tableau
     */
    private VBox creerTableauAlertes() {
        VBox conteneur = new VBox(10);
        
        Label titre = new Label("📋 Historique Complet des Alertes");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        tableauAlertes = new TableView<>();
        tableauAlertes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Colonnes du tableau
        TableColumn<AlerteRecord, String> colDate = new TableColumn<>("Date/Heure");
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        colDate.setPrefWidth(150);
        colDate.setCellFactory(col -> new TableCell<AlerteRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-family: monospace;");
                }
            }
        });
        
        TableColumn<AlerteRecord, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(120);
        
        TableColumn<AlerteRecord, String> colSeverite = new TableColumn<>("Sévérité");
        colSeverite.setCellValueFactory(new PropertyValueFactory<>("severite"));
        colSeverite.setPrefWidth(100);
        colSeverite.setCellFactory(col -> new TableCell<AlerteRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "CRITIQUE":
                            setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "HAUTE":
                            setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
                            break;
                        case "MOYENNE":
                            setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                            break;
                        case "BASSE":
                            setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black;");
                            break;
                    }
                }
            }
        });
        
        TableColumn<AlerteRecord, String> colMessage = new TableColumn<>("Message");
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
        colMessage.setPrefWidth(300);
        
        TableColumn<AlerteRecord, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setPrefWidth(100);
        colStatut.setCellFactory(col -> new TableCell<AlerteRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("ACTIVE")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #27ae60;");
                    }
                }
            }
        });
        
        tableauAlertes.getColumns().addAll(colDate, colType, colSeverite, colMessage, colStatut);
        
        // Événement de sélection avec Lambda
        tableauAlertes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                afficherDetailsAlerte(newVal);
            }
        });
        
        conteneur.getChildren().addAll(titre, tableauAlertes);
        VBox.setVgrow(tableauAlertes, Priority.ALWAYS);
        return conteneur;
    }
    
    /**
     * Crée le panneau des notifications en temps réel.
     * @return Le nœud contenant les notifications
     */
    private VBox creerPanneauNotifications() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        
        Label titre = new Label("🔴 Notifications en Direct");
        titre.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        listeNotifications = new ListView<>();
        listeNotifications.setPrefHeight(400);
        listeNotifications.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");
        
        Button btnEffacer = new Button("Effacer les notifications");
        btnEffacer.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnEffacer.setOnAction(e -> listeNotifications.getItems().clear());
        
        // Détails de l'alerte sélectionnée
        Label lblDetails = new Label("Détails de l'alerte:");
        lblDetails.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        
        txtDetailsAlerte = new TextArea();
        txtDetailsAlerte.setEditable(false);
        txtDetailsAlerte.setWrapText(true);
        txtDetailsAlerte.setPrefRowCount(8);
        txtDetailsAlerte.setPromptText("Sélectionnez une alerte pour voir les détails");
        
        conteneur.getChildren().addAll(titre, listeNotifications, btnEffacer, lblDetails, txtDetailsAlerte);
        VBox.setVgrow(listeNotifications, Priority.ALWAYS);
        return conteneur;
    }
    
    /**
     * Crée le panneau de configuration des alertes.
     * @return Le nœud contenant la configuration
     */
    private VBox creerPanneauConfiguration() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(10));
        conteneur.setPrefWidth(300);
        conteneur.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        
        Label titre = new Label("⚙️ Configuration des Alertes");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Separator sep1 = new Separator();
        
        // Configuration des seuils
        Label lblSeuils = new Label("Seuils d'Alerte:");
        lblSeuils.setStyle("-fx-font-weight: bold;");
        
        GridPane grilleSeuils = new GridPane();
        grilleSeuils.setHgap(10);
        grilleSeuils.setVgap(10);
        
        Label lblSeuilConso = new Label("Consommation max (kWh):");
        txtSeuilConsommation = new TextField("100");
        txtSeuilConsommation.setPromptText("Ex: 100");
        
        Label lblSeuilProd = new Label("Production min (kWh):");
        txtSeuilProduction = new TextField("50");
        txtSeuilProduction.setPromptText("Ex: 50");
        
        Label lblSeuilBat = new Label("Batterie min (%):");
        txtSeuilBatterie = new TextField("20");
        txtSeuilBatterie.setPromptText("Ex: 20");
        
        grilleSeuils.add(lblSeuilConso, 0, 0);
        grilleSeuils.add(txtSeuilConsommation, 0, 1);
        grilleSeuils.add(lblSeuilProd, 0, 2);
        grilleSeuils.add(txtSeuilProduction, 0, 3);
        grilleSeuils.add(lblSeuilBat, 0, 4);
        grilleSeuils.add(txtSeuilBatterie, 0, 5);
        
        Button btnAppliquerSeuils = new Button("✓ Appliquer les Seuils");
        btnAppliquerSeuils.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnAppliquerSeuils.setMaxWidth(Double.MAX_VALUE);
        btnAppliquerSeuils.setOnAction(e -> appliquerSeuils());
        
        Separator sep2 = new Separator();
        
        // Options de notification
        Label lblOptions = new Label("Options de Notification:");
        lblOptions.setStyle("-fx-font-weight: bold;");
        
        chkNotificationsSonores = new CheckBox("Notifications sonores");
        chkNotificationsSonores.setSelected(true);
        
        chkNotificationsVisuelles = new CheckBox("Notifications visuelles");
        chkNotificationsVisuelles.setSelected(true);
        
        chkAutoResolve = new CheckBox("Résolution automatique");
        chkAutoResolve.setSelected(false);
        
        Separator sep3 = new Separator();
        
        // Actions rapides
        Label lblActions = new Label("Actions Rapides:");
        lblActions.setStyle("-fx-font-weight: bold;");
        
        Button btnAcquitter = new Button("✓ Acquitter Toutes");
        btnAcquitter.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnAcquitter.setMaxWidth(Double.MAX_VALUE);
        btnAcquitter.setOnAction(e -> acquitterToutesAlertes());
        
        Button btnSupprimerResolues = new Button("🗑️ Supprimer Résolues");
        btnSupprimerResolues.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        btnSupprimerResolues.setMaxWidth(Double.MAX_VALUE);
        btnSupprimerResolues.setOnAction(e -> supprimerAlertesResolues());
        
        conteneur.getChildren().addAll(
            titre, sep1,
            lblSeuils, grilleSeuils, btnAppliquerSeuils, sep2,
            lblOptions, chkNotificationsSonores, chkNotificationsVisuelles, chkAutoResolve, sep3,
            lblActions, btnAcquitter, btnSupprimerResolues
        );
        
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
        
        Button btnActualiser = new Button("🔄 Actualiser");
        btnActualiser.setOnAction(e -> actualiserTout());
        
        Button btnExporter = new Button("📄 Exporter les Alertes");
        btnExporter.setStyle("-fx-background-color: #16a085; -fx-text-fill: white;");
        btnExporter.setOnAction(e -> exporterAlertes());
        
        Button btnViderHistorique = new Button("🗑️ Vider l'Historique");
        btnViderHistorique.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnViderHistorique.setOnAction(e -> viderHistorique());
        
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> this.close());
        
        conteneur.getChildren().addAll(btnActualiser, btnExporter, btnViderHistorique, btnFermer);
        return conteneur;
    }
    
    /**
     * Configure les écouteurs d'événements.
     * Utilise des expressions Lambda.
     */
    private void configurerEcouteurs() {
        // Écouter les nouvelles alertes du contrôleur
        controleur.definirGestionnaireNouvelleAlerte(alerte -> {
            ajouterNotification(alerte);
            if (chkNotificationsSonores.isSelected()) {
                jouerSonAlerte();
            }
        });
    }
    
    /**
     * Affiche les détails d'une alerte sélectionnée.
     * @param alerte L'alerte à afficher
     */
    private void afficherDetailsAlerte(AlerteRecord alerte) {
        if (alerte == null) return;
        
        String details = String.format(
            "═══════════════════════════════════\n" +
            "       DÉTAILS DE L'ALERTE\n" +
            "═══════════════════════════════════\n\n" +
            "📅 Date/Heure:\n    %s\n\n" +
            "🏷️  Type:\n    %s\n\n" +
            "⚠️  Sévérité:\n    %s\n\n" +
            "💬 Message:\n    %s\n\n" +
            "📊 Statut:\n    %s\n\n" +
            "═══════════════════════════════════\n",
            alerte.dateHeure(),
            alerte.type(),
            alerte.severite(),
            alerte.message(),
            alerte.statut()
        );
        
        txtDetailsAlerte.setText(details);
    }
    
    /**
     * Ajoute une notification dans la liste en temps réel.
     * @param alerte L'alerte à notifier
     */
    private void ajouterNotification(AlerteRecord alerte) {
        String icone = switch (alerte.severite()) {
            case "CRITIQUE" -> "🔴";
            case "HAUTE" -> "🟠";
            case "MOYENNE" -> "🟡";
            default -> "🟢";
        };
        
        String notification = String.format("[%s] %s %s",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            icone,
            alerte.message()
        );
        
        listeNotifications.getItems().add(0, notification);
        
        // Limiter à 50 notifications
        if (listeNotifications.getItems().size() > 50) {
            listeNotifications.getItems().remove(50);
        }
    }
    
    /**
     * Joue un son d'alerte (simulation).
     */
    private void jouerSonAlerte() {
        // Simulation - dans une vraie application, utiliser javafx.scene.media.Media
        System.out.println("🔊 BEEEP! Alerte sonore!");
    }
    
    /**
     * Applique les nouveaux seuils d'alerte.
     */
    private void appliquerSeuils() {
        try {
            double seuilConso = Double.parseDouble(txtSeuilConsommation.getText());
            double seuilProd = Double.parseDouble(txtSeuilProduction.getText());
            double seuilBat = Double.parseDouble(txtSeuilBatterie.getText());
            
            controleur.configurerSeuils(seuilConso, seuilProd, seuilBat);
            
            afficherAlerte("Succès", "Seuils d'alerte appliqués avec succès !");
        } catch (NumberFormatException ex) {
            afficherAlerte("Erreur", "Veuillez entrer des valeurs numériques valides.");
        }
    }
    
    /**
     * Acquitte toutes les alertes actives.
     */
    private void acquitterToutesAlertes() {
        controleur.acquitterToutesAlertes();
        actualiserTout();
        afficherAlerte("Succès", "Toutes les alertes ont été acquittées.");
    }
    
    /**
     * Supprime les alertes résolues.
     */
    private void supprimerAlertesResolues() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer les alertes résolues");
        confirmation.setContentText("Êtes-vous sûr ?");
        
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                controleur.supprimerAlertesResolues();
                actualiserTout();
                afficherAlerte("Succès", "Alertes résolues supprimées.");
            }
        });
    }
    
    /**
     * Exporte les alertes en format texte.
     */
    private void exporterAlertes() {
        String contenu = controleur.exporterAlertes();
        
        Stage fenetreExport = new Stage();
        fenetreExport.setTitle("Exportation des Alertes");
        
        TextArea txtExport = new TextArea(contenu);
        txtExport.setEditable(false);
        
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        conteneur.getChildren().add(txtExport);
        
        Scene scene = new Scene(conteneur, 700, 500);
        fenetreExport.setScene(scene);
        fenetreExport.show();
    }
    
    /**
     * Vide l'historique des alertes.
     */
    private void viderHistorique() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Vider l'historique des alertes");
        confirmation.setContentText("Cette action est irréversible. Continuer ?");
        
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.OK) {
                controleur.viderHistorique();
                actualiserTout();
                afficherAlerte("Succès", "Historique vidé.");
            }
        });
    }
    
    /**
     * Actualise tout : tableau + statistiques.
     */
    public void actualiserTout() {
        actualiserTableau();
        actualiserStatistiques();
    }
    
    /**
     * Actualise le tableau des alertes.
     */
    private void actualiserTableau() {
        tableauAlertes.getItems().clear();
        tableauAlertes.getItems().addAll(controleur.obtenirAlertes());
    }
    
    /**
     * Actualise les statistiques.
     * Utilise des streams pour les calculs.
     */
    private void actualiserStatistiques() {
        int total = controleur.compterAlertes();
        int actives = controleur.compterAlertesActives();
        String derniere = controleur.obtenirDerniereAlerte();
        double severite = controleur.calculerNiveauSeverite();
        
        lblNombreAlertes.setText(String.valueOf(total));
        lblAlertesActives.setText(String.valueOf(actives));
        lblDerniereAlerte.setText(derniere);
        progressSeverite.setProgress(severite);
        
        // Changer la couleur selon la sévérité
        if (severite >= 0.8) {
            progressSeverite.setStyle("-fx-accent: #c0392b;");
        } else if (severite >= 0.5) {
            progressSeverite.setStyle("-fx-accent: #e74c3c;");
        } else if (severite >= 0.3) {
            progressSeverite.setStyle("-fx-accent: #f39c12;");
        } else {
            progressSeverite.setStyle("-fx-accent: #27ae60;");
        }
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