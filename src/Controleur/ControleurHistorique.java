package Controleur;

import simulation.modele.simulation.Historique;
import simulation.modele.simulation.RecordSimulation;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Contrôleur pour gérer l'historique des simulations.
 * Fait le lien entre la vue historique et le modèle.
 */
public class ControleurHistorique {
    
    private final Historique historique;
    
    /**
     * Constructeur du contrôleur.
     * @param historique Le modèle d'historique
     */
    public ControleurHistorique(Historique historique) {
        if (historique == null) {
            throw new IllegalArgumentException("Historique ne peut pas être null");
        }
        this.historique = historique;
    }
    
    /**
     * Obtient tout l'historique.
     * Utilise un stream pour créer une copie immuable.
     * @return Liste des enregistrements
     */
    public List<RecordSimulation> obtenirHistorique() {
        return historique.getListe().stream()
                .toList(); // Stream
    }
    
    /**
     * Filtre l'historique par temps minimum.
     * Utilise un stream avec Predicate (interface fonctionnelle).
     * @param tempsMin Le temps minimum
     * @return Liste filtrée
     */
    public List<RecordSimulation> filtrerParTemps(int tempsMin) {
        // Utilisation d'une interface fonctionnelle Predicate
        Predicate<RecordSimulation> predicatTemps = record -> record.temps() >= tempsMin;
        
        return historique.getListe().stream()
                .filter(predicatTemps) // Interface fonctionnelle
                .toList();
    }
    
    /**
     * Filtre l'historique par production minimale.
     * Utilise une expression Lambda directement.
     * @param productionMin La production minimale
     * @return Liste filtrée
     */
    public List<RecordSimulation> filtrerParProduction(double productionMin) {
        return historique.getListe().stream()
                .filter(r -> r.production() >= productionMin) // Expression Lambda
                .toList();
    }
    
    /**
     * Filtre l'historique par consommation maximale.
     * @param consommationMax La consommation maximale
     * @return Liste filtrée
     */
    public List<RecordSimulation> filtrerParConsommation(double consommationMax) {
        return historique.getListe().stream()
                .filter(r -> r.consommation() <= consommationMax) // Expression Lambda
                .toList();
    }
    
    /**
     * Trouve les simulations en déficit énergétique.
     * Utilise un stream avec filter et Lambda.
     * @return Liste des simulations en déficit
     */
    public List<RecordSimulation> trouverDeficits() {
        return historique.getListe().stream()
                .filter(r -> r.production() < r.consommation()) // Expression Lambda
                .toList();
    }
    
    /**
     * Trouve les simulations en excédent énergétique.
     * @return Liste des simulations en excédent
     */
    public List<RecordSimulation> trouverExcedents() {
        return historique.getListe().stream()
                .filter(r -> r.production() >= r.consommation()) // Expression Lambda
                .toList();
    }
    
    /**
     * Calcule la production moyenne sur tout l'historique.
     * Utilise un stream avec mapToDouble et average.
     * @return Production moyenne
     */
    public double calculerProductionMoyenne() {
        return historique.getListe().stream()
                .mapToDouble(RecordSimulation::production) // Référence de méthode
                .average()
                .orElse(0.0);
    }
    
    /**
     * Calcule la consommation moyenne sur tout l'historique.
     * @return Consommation moyenne
     */
    public double calculerConsommationMoyenne() {
        return historique.getListe().stream()
                .mapToDouble(RecordSimulation::consommation) // Référence de méthode
                .average()
                .orElse(0.0);
    }
    
    /**
     * Trouve la production maximale enregistrée.
     * Utilise un stream avec max.
     * @return Production maximale
     */
    public double trouverProductionMax() {
        return historique.getListe().stream()
                .mapToDouble(RecordSimulation::production)
                .max()
                .orElse(0.0);
    }
    
    /**
     * Trouve la consommation maximale enregistrée.
     * @return Consommation maximale
     */
    public double trouverConsommationMax() {
        return historique.getListe().stream()
                .mapToDouble(RecordSimulation::consommation)
                .max()
                .orElse(0.0);
    }
    
    /**
     * Calcule le bilan moyen (production - consommation).
     * Utilise un stream avec map et moyenne.
     * @return Bilan moyen
     */
    public double calculerBilanMoyen() {
        return historique.getListe().stream()
                .mapToDouble(r -> r.production() - r.consommation()) // Expression Lambda
                .average()
                .orElse(0.0);
    }
    
    /**
     * Compte le nombre de simulations.
     * @return Nombre de simulations
     */
    public int compterSimulations() {
        return historique.taille();
    }
    
    /**
     * Compte le nombre de déficits.
     * Utilise un stream avec filter et count.
     * @return Nombre de déficits
     */
    public long compterDeficits() {
        return historique.getListe().stream()
                .filter(r -> r.production() < r.consommation())
                .count();
    }
    
    /**
     * Compte le nombre d'excédents.
     * @return Nombre d'excédents
     */
    public long compterExcedents() {
        return historique.getListe().stream()
                .filter(r -> r.production() >= r.consommation())
                .count();
    }
    
    /**
     * Exporte l'historique en format texte.
     * Utilise un stream avec map et Collectors.
     * @return String formaté de l'historique
     */
    public String exporterEnTexte() {
        if (historique.estVide()) {
            return "Historique vide.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("           HISTORIQUE DES SIMULATIONS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        // Statistiques globales
        sb.append(String.format("Nombre total de simulations : %d\n", compterSimulations()));
        sb.append(String.format("Production moyenne : %.2f kWh\n", calculerProductionMoyenne()));
        sb.append(String.format("Consommation moyenne : %.2f kWh\n", calculerConsommationMoyenne()));
        sb.append(String.format("Bilan moyen : %.2f kWh\n", calculerBilanMoyen()));
        sb.append(String.format("Nombre de déficits : %d\n", compterDeficits()));
        sb.append(String.format("Nombre d'excédents : %d\n\n", compterExcedents()));
        
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("                  DÉTAILS DES SIMULATIONS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        // Détails de chaque simulation avec stream
        String details = historique.getListe().stream()
                .map(r -> String.format(
                    "Temps: %d | Prod: %.2f kWh | Conso: %.2f kWh | Bilan: %.2f kWh | État: %s",
                    r.temps(),
                    r.production(),
                    r.consommation(),
                    r.production() - r.consommation(),
                    r.production() >= r.consommation() ? "✅ OK" : "⚠️ DÉFICIT"
                ))
                .collect(Collectors.joining("\n")); // Collectors pour joindre
        
        sb.append(details);
        sb.append("\n\n═══════════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Génère un rapport de statistiques détaillé.
     * Utilise plusieurs streams pour les calculs.
     * @return String formaté du rapport
     */
    public String genererRapportStatistiques() {
        if (historique.estVide()) {
            return "Aucune simulation effectuée.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📊 RAPPORT STATISTIQUES DÉTAILLÉ\n");
        sb.append("================================\n\n");
        
        sb.append(String.format("📈 Production maximale : %.2f kWh\n", trouverProductionMax()));
        sb.append(String.format("📉 Consommation maximale : %.2f kWh\n", trouverConsommationMax()));
        sb.append(String.format("⚖️ Bilan moyen : %.2f kWh\n", calculerBilanMoyen()));
        sb.append(String.format("✅ Taux d'excédent : %.1f%%\n", 
            (compterExcedents() * 100.0 / compterSimulations())));
        sb.append(String.format("⚠️ Taux de déficit : %.1f%%\n", 
            (compterDeficits() * 100.0 / compterSimulations())));
        
        return sb.toString();
    }

    /**
     * Vide l'historique (mémoire + fichier)
     */
    public void viderHistorique() {
        historique.vider();
    }

    /**
     * Sauvegarde l'historique en mémoire vers le fichier
     */
    public void sauvegarder() {
        historique.sauvegarderBatch();
    }

    /**
     * Charge l'historique depuis le fichier
     */
    public void charger() {
        historique.chargerDepuisFichier();
    }
}