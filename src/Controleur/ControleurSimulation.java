package Controleur;

import simulation.modele.simulation.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

/**
 * Contrôleur principal pour gérer la simulation.
 * Coordonne les opérations entre la vue et le modèle.
 */
public class ControleurSimulation {
    
    private final GestionEnergie gestionEnergie;
    private final Historique historique;
    private AlerteEnergie alerteActive;
    private Consumer<String> gestionnairealerte; // Interface fonctionnelle pour callback
    
    /**
     * Constructeur du contrôleur.
     * @param gestionEnergie Le modèle de gestion d'énergie
     */
    public ControleurSimulation(GestionEnergie gestionEnergie) {
        if (gestionEnergie == null) {
            throw new IllegalArgumentException("GestionEnergie ne peut pas être null");
        }
        this.gestionEnergie = gestionEnergie;
        this.historique = new Historique();
    }
    
    /**
     * Lance une étape de simulation.
     * Utilise try-with-resources conceptuel pour la gestion des ressources.
     * @throws EnergieException Si erreur durant la simulation
     */
    public void lancerSimulation() throws EnergieException {
        // Simuler une unité de temps
        gestionEnergie.simulerUniteTemps();
        
        // Enregistrer dans l'historique
        RecordSimulation record = gestionEnergie.getDerniereSimulation();
        if (record != null) {
            historique.ajouter(record);
        }
        
        // Vérifier les alertes
        verifierEtNotifierAlertes();
    }
    
    /**
     * Vérifie les alertes et notifie si nécessaire.
     * Utilise une interface fonctionnelle Consumer pour le callback.
     */
    private void verifierEtNotifierAlertes() {
        if (alerteActive != null && gestionEnergie.verifierAlerte()) {
            String message = genererMessageAlerte();
            
            // Notification via Consumer (interface fonctionnelle)
            if (gestionnairealerte != null) {
                gestionnairealerte.accept(message);
            }
        }
    }
    
    /**
     * Génère un message d'alerte personnalisé.
     * @return Le message d'alerte
     */
    private String genererMessageAlerte() {
        double production = gestionEnergie.productionTotale();
        double consommation = gestionEnergie.consommationTotale();
        
        return String.format(
            "⚠️ ALERTE ÉNERGIE\nProduction: %.2f kWh\nConsommation: %.2f kWh\nDéficit: %.2f kWh",
            production, consommation, consommation - production
        );
    }
    
    /**
     * Définit un gestionnaire d'alerte.
     * Accepte une interface fonctionnelle Consumer.
     * @param gestionnaire Le gestionnaire (Consumer)
     */
    public void definirGestionnaireAlerte(Consumer<String> gestionnaire) {
        this.gestionnairealerte = gestionnaire;
    }
    
    /**
     * Configure une alerte avec des seuils.
     * @param seuilConsommation Seuil de consommation maximale
     * @param seuilProduction Seuil de production minimale
     */
    public void configurerAlerte(double seuilConsommation, double seuilProduction) {
        this.alerteActive = new AlerteEnergie(seuilConsommation, seuilProduction);
        gestionEnergie.definirAlerte(alerteActive);
    }
    
    /**
     * Obtient l'état actuel de la simulation.
     * @return String représentant l'état
     */
    public String obtenirEtatActuel() {
        return gestionEnergie.getEtat();
    }
    
    /**
     * Obtient la production totale actuelle.
     * @return Production en kWh
     */
    public double obtenirProductionTotale() {
        return gestionEnergie.productionTotale();
    }
    
    /**
     * Obtient la consommation totale actuelle.
     * @return Consommation en kWh
     */
    public double obtenirConsommationTotale() {
        return gestionEnergie.consommationTotale();
    }
    
    /**
     * Calcule le bilan énergétique (production - consommation).
     * Utilise une expression Lambda via BiConsumer.
     * @return Le bilan
     */
    public double calculerBilan() {
        // Utilisation d'une interface fonctionnelle BiConsumer pour calculer
        BiConsumer<Double, Double> afficheurBilan = (prod, conso) -> {
            System.out.printf("Production: %.2f kWh | Consommation: %.2f kWh%n", prod, conso);
        };
        
        double production = obtenirProductionTotale();
        double consommation = obtenirConsommationTotale();
        
        afficheurBilan.accept(production, consommation);
        
        return production - consommation;
    }
    
    /**
     * Obtient l'historique complet des simulations.
     * Utilise un stream pour filtrer si nécessaire.
     * @return Liste des enregistrements
     */
    public List<RecordSimulation> obtenirHistorique() {
        return historique.getListe().stream()
                .toList(); // Stream pour créer une copie immuable
    }
    
    /**
     * Obtient les N dernières simulations.
     * Utilise un stream avec limit.
     * @param n Nombre de simulations à récupérer
     * @return Liste des N dernières simulations
     */
    public List<RecordSimulation> obtenirDernieresSimulations(int n) {
        List<RecordSimulation> liste = historique.getListe();
        int taille = liste.size();
        int debut = Math.max(0, taille - n);
        
        return liste.stream()
                .skip(debut)
                .limit(n)
                .toList();
    }
    
    /**
     * Vide l'historique des simulations.
     */
    public void viderHistorique() {
        historique.vider();
    }
    
    /**
     * Vérifie si une alerte est active.
     * @return true si alerte active
     */
    public boolean alerteEstActive() {
        return gestionEnergie.verifierAlerte();
    }
    
    /**
     * Obtient les statistiques globales.
     * Utilise des streams pour les calculs.
     * @return String formaté avec les statistiques
     */
    public String obtenirStatistiques() {
        List<RecordSimulation> liste = historique.getListe();
        
        if (liste.isEmpty()) {
            return "Aucune simulation effectuée.";
        }
        
        // Calculs avec streams et références de méthode
        double prodMoyenne = liste.stream()
                .mapToDouble(RecordSimulation::production)
                .average()
                .orElse(0.0);
        
        double consoMoyenne = liste.stream()
                .mapToDouble(RecordSimulation::consommation)
                .average()
                .orElse(0.0);
        
        double prodMax = liste.stream()
                .mapToDouble(RecordSimulation::production)
                .max()
                .orElse(0.0);
        
        double consoMax = liste.stream()
                .mapToDouble(RecordSimulation::consommation)
                .max()
                .orElse(0.0);
        
        return String.format(
            "📊 STATISTIQUES\n" +
            "Nombre de simulations: %d\n" +
            "Production moyenne: %.2f kWh\n" +
            "Consommation moyenne: %.2f kWh\n" +
            "Production max: %.2f kWh\n" +
            "Consommation max: %.2f kWh",
            liste.size(), prodMoyenne, consoMoyenne, prodMax, consoMax
        );
    }
    
    /**
     * Obtient le modèle de gestion.
     * @return GestionEnergie
     */
    public GestionEnergie getGestionEnergie() {
        return gestionEnergie;
    }
    // Dans ControleurSimulation
public void ajouterRecordSimulation(RecordSimulation record) {
    if (record != null) {
        historique.ajouter(record);
    }
}

    /**
     * Obtient l'historique.
     * @return Historique
     */
    public Historique getHistorique() {
        return historique;
    }
}