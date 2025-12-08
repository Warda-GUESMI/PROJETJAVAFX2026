/**
 * Module de simulation énergétique.
 * Ce module gère la simulation de production et consommation d'énergie.
 * 
 * Conforme aux exigences Java 17 du projet :
 * - Utilisation de modules
 * - Packages structurés
 * - Héritage restreint (sealed interfaces)
 * - Records
 */
module GestionSimulation {
    // Dépendances JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    // Dépendances pour SQLite (si utilisé)
    requires java.sql;
    
    // Dépendances pour logging
    requires java.logging;
    
    // Export des packages pour permettre l'accès
    exports simulation.modele.source;
    exports simulation.modele.simulation;
    exports vue;
    exports Controleur;
    
    // Ouverture pour JavaFX (réflexion pour PropertyValueFactory)
    opens simulation.modele.source to javafx.base;
    opens simulation.modele.simulation to javafx.base;
    opens vue to javafx.fxml, javafx.graphics;
}