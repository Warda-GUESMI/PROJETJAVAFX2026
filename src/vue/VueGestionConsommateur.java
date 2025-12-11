package vue;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Controleur.ControleurConsommateur;
import simulation.modele.simulation.Consommateur;

/**
 * Interface graphique pour gérer les consommateurs d'énergie.
 */
public class VueGestionConsommateur extends Stage {
    
    private final ControleurConsommateur controleur;
    
    // Composants graphiques
    private TableView<Consommateur> tableauConsommateurs;
    private TextField txtNom, txtConsommation;
    private TextArea txtAppareils;
    
    /**
     * Constructeur de la vue.
     * @param controleur Le contrôleur associé
     */
    public VueGestionConsommateur(ControleurConsommateur controleur) {
        this.controleur = controleur;
        initialiserInterface();
        this.setTitle("Gestion des Consommateurs");
    }
    
    /**
     * Initialise tous les composants de l'interface.
     */
    private void initialiserInterface() {
        BorderPane racine = new BorderPane();
        racine.setPadding(new Insets(10));
        
        // Partie supérieure : Tableau des consommateurs
        racine.setTop(creerTableauConsommateurs());
        
        // Partie centrale : Formulaire d'ajout
        racine.setCenter(creerFormulaireAjout());
        
        // Partie inférieure : Boutons d'action
        racine.setBottom(creerPanneauBoutons());
        
        Scene scene = new Scene(racine, 800, 600);
        this.setScene(scene);
    }
    
    /**
     * Crée le tableau affichant les consommateurs.
     * @return Le nœud contenant le tableau
     */
    private VBox creerTableauConsommateurs() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(10));
        
        Label titre = new Label("Liste des Consommateurs");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        tableauConsommateurs = new TableView<>();
        
        // Colonnes du tableau
        TableColumn<Consommateur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colNom.setPrefWidth(250);
        
        TableColumn<Consommateur, Double> colConsommation = new TableColumn<>("Consommation (kWh)");
        colConsommation.setCellValueFactory(new PropertyValueFactory<>("consommation"));
        colConsommation.setPrefWidth(200);
        
        // Colonne avec style personnalisé pour afficher le nombre d'appareils
        TableColumn<Consommateur, Integer> colAppareils = new TableColumn<>("Nb Appareils");
        colAppareils.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getConsommations().size()
            ).asObject());
        colAppareils.setPrefWidth(150);
        
        tableauConsommateurs.getColumns().addAll(colNom, colConsommation, colAppareils);
        
        conteneur.getChildren().addAll(titre, tableauConsommateurs);
        return conteneur;
    }
    
    /**
     * Crée le formulaire pour ajouter un nouveau consommateur.
     * @return Le nœud contenant le formulaire
     */
    private VBox creerFormulaireAjout() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(10));
        conteneur.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1;");
        
        Label titre = new Label("Ajouter un Consommateur");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Champ Nom
        GridPane grille = new GridPane();
        grille.setHgap(10);
        grille.setVgap(10);
        
        Label lblNom = new Label("Nom du consommateur :");
        txtNom = new TextField();
        txtNom.setPromptText("Ex: Maison, Bureau, Usine...");
        
        Label lblConsommation = new Label("Consommation (kWh/h) :");
        txtConsommation = new TextField();
        txtConsommation.setPromptText("Ex: 5.5");
        
        Label lblAppareils = new Label("Appareils (optionnel) :");
        txtAppareils = new TextArea();
        txtAppareils.setPromptText("Format: Appareil1=Conso1, Appareil2=Conso2\nEx: Climatisation=3.5, Ordinateur=0.5");
        txtAppareils.setPrefRowCount(3);
        
        grille.add(lblNom, 0, 0);
        grille.add(txtNom, 1, 0);
        grille.add(lblConsommation, 0, 1);
        grille.add(txtConsommation, 1, 1);
        grille.add(lblAppareils, 0, 2);
        grille.add(txtAppareils, 1, 2);
        
        conteneur.getChildren().addAll(titre, grille);
        return conteneur;
    }
    
    /**
     * Crée le panneau contenant les boutons d'action.
     * Utilise des expressions Lambda pour les gestionnaires d'événements.
     * @return Le nœud contenant les boutons
     */
    private HBox creerPanneauBoutons() {
        HBox conteneur = new HBox(15);
        conteneur.setPadding(new Insets(10));
        
        Button btnAjouter = new Button("Ajouter le Consommateur");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAjouter.setOnAction(e -> ajouterConsommateur());
        
        Button btnModifier = new Button("Modifier la Consommation");
        btnModifier.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnModifier.setOnAction(e -> modifierConsommation());
        
        /*Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnSupprimer.setOnAction(e -> supprimerConsommateur());
        
        Button btnActualiser = new Button("Actualiser");
        btnActualiser.setOnAction(e -> actualiserTableau());
        
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> this.close());*/
        
        conteneur.getChildren().addAll(btnAjouter, btnModifier);
        return conteneur;
    }
    
    /**
     * Ajoute un consommateur via le contrôleur.
     * Utilise try-catch pour gérer les exceptions.
     */
    private void ajouterConsommateur() {
        try {
            String nom = txtNom.getText().trim();
            String consoStr = txtConsommation.getText().trim();
            
            if (nom.isEmpty() || consoStr.isEmpty()) {
                afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires.");
                return;
            }
            
            double consommation = Double.parseDouble(consoStr);
            
            // Ajouter le consommateur
            controleur.ajouterConsommateur(nom, consommation);
            
            // Traiter les appareils si renseignés
            String appareilsStr = txtAppareils.getText().trim();
            if (!appareilsStr.isEmpty()) {
                String[] lignes = appareilsStr.split(",");
                for (String ligne : lignes) {
                    String[] parts = ligne.split("=");
                    if (parts.length == 2) {
                        String nomAppareil = parts[0].trim();
                        double consoAppareil = Double.parseDouble(parts[1].trim());
                        controleur.ajouterAppareil(nom, nomAppareil, consoAppareil);
                    }
                }
            }
            
            actualiserTableau();
            viderChamps();
            afficherAlerte("Succès", "Consommateur ajouté avec succès !");
            
        } catch (NumberFormatException ex) {
            afficherAlerte("Erreur", "Veuillez entrer des valeurs numériques valides.");
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Erreur lors de l'ajout : " + ex.getMessage());
        }
    }
    
    /**
     * Modifie la consommation du consommateur sélectionné.
     */
    private void modifierConsommation() {
        Consommateur consommateurSelectionne = tableauConsommateurs.getSelectionModel().getSelectedItem();
        
        if (consommateurSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un consommateur à modifier.");
            return;
        }
        
        // Boîte de dialogue pour demander le nouveau facteur
        TextInputDialog dialogue = new TextInputDialog("1.0");
        dialogue.setTitle("Modifier la consommation");
        dialogue.setHeaderText("Modification de : " + consommateurSelectionne.getNom());
        dialogue.setContentText("Facteur multiplicateur (ex: 1.5 = +50%, 0.8 = -20%) :");
        
        dialogue.showAndWait().ifPresent(facteurStr -> {
            try {
                double facteur = Double.parseDouble(facteurStr);
                controleur.ajusterConsommation(consommateurSelectionne.getNom(), facteur);
                actualiserTableau();
                afficherAlerte("Succès", "Consommation modifiée !");
            } catch (NumberFormatException ex) {
                afficherAlerte("Erreur", "Valeur invalide.");
            }
        });
    }
    
    /**
     * Supprime le consommateur sélectionné.
     */
    /*private void supprimerConsommateur() {
        Consommateur consommateurSelectionne = tableauConsommateurs.getSelectionModel().getSelectedItem();
        
        if (consommateurSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un consommateur à supprimer.");
            return;
        }
        
        controleur.supprimerConsommateur(consommateurSelectionne);
        actualiserTableau();
        afficherAlerte("Succès", "Consommateur supprimé !");
    }
    
    /**
     * Actualise le tableau avec les données du contrôleur.
     */
    public void actualiserTableau() {
        tableauConsommateurs.getItems().clear();
        tableauConsommateurs.getItems().addAll(controleur.obtenirConsommateurs());
    }
    
    /**
     * Vide tous les champs de saisie.
     */
    private void viderChamps() {
        txtNom.clear();
        txtConsommation.clear();
        txtAppareils.clear();
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
}