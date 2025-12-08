package Controleur;

import simulation.modele.simulation.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Contrôleur pour gérer les alertes énergétiques.
 * Gère la création, le suivi et l'historique des alertes.
 */
public class ControleurAlertes {
    
    private final GestionEnergie gestionEnergie;
    private final List<AlerteRecord> historiqueAlertes;
    private double seuilConsommation = 100.0;
    private double seuilProduction = 50.0;
    private double seuilBatterie = 20.0;
    private Consumer<AlerteRecord> gestionnaireNouvelleAlerte;
    
    /**
     * Constructeur du contrôleur.
     * @param gestionEnergie Le modèle de gestion d'énergie
     */
    public ControleurAlertes(GestionEnergie gestionEnergie) {
        if (gestionEnergie == null) {
            throw new IllegalArgumentException("GestionEnergie ne peut pas être null");
        }
        this.gestionEnergie = gestionEnergie;
        this.historiqueAlertes = new ArrayList<>();
    }
    
    /**
     * Définit le gestionnaire pour les nouvelles alertes.
     * Utilise une interface fonctionnelle Consumer.
     * @param gestionnaire Le gestionnaire (Consumer)
     */
    public void definirGestionnaireNouvelleAlerte(Consumer<AlerteRecord> gestionnaire) {
        this.gestionnaireNouvelleAlerte = gestionnaire;
    }
    
    /**
     * Configure les seuils d'alerte.
     * @param seuilConso Seuil de consommation
     * @param seuilProd Seuil de production
     * @param seuilBat Seuil de batterie
     */
    public void configurerSeuils(double seuilConso, double seuilProd, double seuilBat) {
        this.seuilConsommation = seuilConso;
        this.seuilProduction = seuilProd;
        this.seuilBatterie = seuilBat;
        
        // Mettre à jour l'alerte dans GestionEnergie
        gestionEnergie.definirAlerte(seuilConso, seuilProd);
    }
    
    /**
     * Vérifie et génère des alertes si nécessaire.
     * Utilise des expressions Lambda pour les conditions.
     */
    public void verifierEtGenererAlertes() {
        double production = gestionEnergie.productionTotale();
        double consommation = gestionEnergie.consommationTotale();
        double bilan = production - consommation;
        
        // Alerte consommation excessive
        if (consommation > seuilConsommation) {
            String severite = consommation > seuilConsommation * 1.5 ? "CRITIQUE" : "HAUTE";
            AlerteRecord alerte = new AlerteRecord(
                "CONSOMMATION_EXCESSIVE",
                severite,
                String.format("Consommation excessive : %.2f kWh (seuil : %.2f kWh)", 
                    consommation, seuilConsommation),
                "ACTIVE",
                consommation,
                seuilConsommation
            );
            ajouterAlerte(alerte);
        }
        
        // Alerte production insuffisante
        if (production < seuilProduction) {
            String severite = production < seuilProduction * 0.5 ? "CRITIQUE" : "MOYENNE";
            AlerteRecord alerte = new AlerteRecord(
                "PRODUCTION_FAIBLE",
                severite,
                String.format("Production insuffisante : %.2f kWh (seuil : %.2f kWh)", 
                    production, seuilProduction),
                "ACTIVE",
                production,
                seuilProduction
            );
            ajouterAlerte(alerte);
        }
        
        // Alerte déficit énergétique
        if (bilan < 0) {
            String severite = Math.abs(bilan) > 50 ? "CRITIQUE" : "HAUTE";
            AlerteRecord alerte = new AlerteRecord(
                "DEFICIT_ENERGETIQUE",
                severite,
                String.format("Déficit énergétique : %.2f kWh", Math.abs(bilan)),
                "ACTIVE",
                bilan,
                0.0
            );
            ajouterAlerte(alerte);
        }
        
        // Alerte batteries faibles
        verifierEtatBatteries();
    }
    
    /**
     * Vérifie l'état des batteries et génère des alertes.
     * Utilise un stream avec filter.
     */
    private void verifierEtatBatteries() {
        gestionEnergie.getSources().stream()
            .filter(simulation.modele.source.Batterie.class::isInstance)
            .map(s -> (simulation.modele.source.Batterie) s)
            .forEach(batterie -> {
                double pourcentage = (batterie.getNiveau() / batterie.getCapacite()) * 100;
                if (pourcentage < seuilBatterie) {
                    String severite = pourcentage < seuilBatterie * 0.5 ? "CRITIQUE" : "HAUTE";
                    AlerteRecord alerte = new AlerteRecord(
                        "BATTERIE_FAIBLE",
                        severite,
                        String.format("Batterie faible : %.1f%% (seuil : %.1f%%)", 
                            pourcentage, seuilBatterie),
                        "ACTIVE",
                        pourcentage,
                        seuilBatterie
                    );
                    ajouterAlerte(alerte);
                }
            });
    }
    
    /**
     * Ajoute une alerte à l'historique.
     * @param alerte L'alerte à ajouter
     */
    private void ajouterAlerte(AlerteRecord alerte) {
        historiqueAlertes.add(alerte);
        
        // Notifier via Consumer si défini
        if (gestionnaireNouvelleAlerte != null) {
            gestionnaireNouvelleAlerte.accept(alerte);
        }
    }
    
    /**
     * Obtient toutes les alertes.
     * Utilise un stream pour créer une copie.
     * @return Liste des alertes
     */
    public List<AlerteRecord> obtenirAlertes() {
        return historiqueAlertes.stream()
            .toList();
    }
    
    /**
     * Filtre les alertes par sévérité.
     * Utilise une interface fonctionnelle Predicate.
     * @param severite La sévérité recherchée
     * @return Liste filtrée
     */
    public List<AlerteRecord> filtrerParSeverite(String severite) {
        Predicate<AlerteRecord> predicatSeverite = alerte -> alerte.severite().equals(severite);
        
        return historiqueAlertes.stream()
            .filter(predicatSeverite)
            .toList();
    }
    
    /**
     * Filtre les alertes par type.
     * @param type Le type recherché
     * @return Liste filtrée
     */
    public List<AlerteRecord> filtrerParType(String type) {
        return historiqueAlertes.stream()
            .filter(a -> a.type().equals(type))
            .toList();
    }
    
    /**
     * Filtre les alertes par statut.
     * @param statut Le statut recherché
     * @return Liste filtrée
     */
    public List<AlerteRecord> filtrerParStatut(String statut) {
        return historiqueAlertes.stream()
            .filter(a -> a.statut().equals(statut))
            .toList();
    }
    
    /**
     * Obtient les alertes actives uniquement.
     * Utilise une référence de méthode.
     * @return Liste des alertes actives
     */
    public List<AlerteRecord> obtenirAlertesActives() {
        return historiqueAlertes.stream()
            .filter(AlerteRecord::estActive)
            .toList();
    }
    
    /**
     * Obtient les alertes critiques.
     * @return Liste des alertes critiques
     */
    public List<AlerteRecord> obtenirAlertesCritiques() {
        return historiqueAlertes.stream()
            .filter(AlerteRecord::estCritique)
            .toList();
    }
    
    /**
     * Compte le nombre total d'alertes.
     * @return Nombre d'alertes
     */
    public int compterAlertes() {
        return historiqueAlertes.size();
    }
    
    /**
     * Compte le nombre d'alertes actives.
     * Utilise un stream avec filter et count.
     * @return Nombre d'alertes actives
     */
    public int compterAlertesActives() {
        return (int) historiqueAlertes.stream()
            .filter(AlerteRecord::estActive)
            .count();
    }
    
    /**
     * Compte les alertes par sévérité.
     * Utilise un stream avec Collectors.
     * @return Map sévérité -> nombre
     */
    public Map<String, Long> compterParSeverite() {
        return historiqueAlertes.stream()
            .collect(Collectors.groupingBy(
                AlerteRecord::severite,
                Collectors.counting()
            ));
    }
    
    /**
     * Obtient la dernière alerte.
     * @return Description de la dernière alerte
     */
    public String obtenirDerniereAlerte() {
        if (historiqueAlertes.isEmpty()) {
            return "Aucune alerte";
        }
        
        AlerteRecord derniere = historiqueAlertes.get(historiqueAlertes.size() - 1);
        return String.format("%s - %s", derniere.dateHeure(), derniere.message());
    }
    
    /**
     * Calcule le niveau de sévérité global (0-1).
     * Utilise un stream avec mapToDouble.
     * @return Niveau de sévérité moyen
     */
    public double calculerNiveauSeverite() {
        if (historiqueAlertes.isEmpty()) {
            return 0.0;
        }
        
        return obtenirAlertesActives().stream()
            .mapToDouble(AlerteRecord::scoreSeverite)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Acquitte toutes les alertes actives.
     * Utilise un stream avec map.
     */
    public void acquitterToutesAlertes() {
        List<AlerteRecord> nouvelleListe = historiqueAlertes.stream()
            .map(alerte -> alerte.estActive() ? alerte.avecStatut("ACQUITTEE") : alerte)
            .toList();
        
        historiqueAlertes.clear();
        historiqueAlertes.addAll(nouvelleListe);
    }
    
    /**
     * Acquitte une alerte spécifique.
     * @param index L'index de l'alerte
     */
    public void acquitterAlerte(int index) {
        if (index >= 0 && index < historiqueAlertes.size()) {
            AlerteRecord alerte = historiqueAlertes.get(index);
            if (alerte.estActive()) {
                historiqueAlertes.set(index, alerte.avecStatut("ACQUITTEE"));
            }
        }
    }
    
    /**
     * Résout une alerte.
     * @param index L'index de l'alerte
     */
    public void resoudreAlerte(int index) {
        if (index >= 0 && index < historiqueAlertes.size()) {
            AlerteRecord alerte = historiqueAlertes.get(index);
            historiqueAlertes.set(index, alerte.avecStatut("RESOLUE"));
        }
    }
    
    /**
     * Supprime les alertes résolues.
     * Utilise removeIf avec Predicate.
     */
    public void supprimerAlertesResolues() {
        historiqueAlertes.removeIf(alerte -> "RESOLUE".equals(alerte.statut()));
    }
    
    /**
     * Vide l'historique des alertes.
     */
    public void viderHistorique() {
        historiqueAlertes.clear();
    }
    
    /**
     * Exporte les alertes en format texte.
     * Utilise un stream avec map et Collectors.
     * @return String formaté
     */
    public String exporterAlertes() {
        if (historiqueAlertes.isEmpty()) {
            return "Aucune alerte enregistrée.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("           RAPPORT COMPLET DES ALERTES\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        // Statistiques
        sb.append(String.format("Nombre total d'alertes : %d\n", compterAlertes()));
        sb.append(String.format("Alertes actives : %d\n", compterAlertesActives()));
        sb.append(String.format("Niveau de sévérité : %.1f%%\n\n", calculerNiveauSeverite() * 100));
        
        // Répartition par sévérité
        sb.append("Répartition par sévérité:\n");
        compterParSeverite().forEach((sev, count) -> 
            sb.append(String.format("  - %s : %d\n", sev, count))
        );
        
        sb.append("\n═══════════════════════════════════════════════════════\n");
        sb.append("                  DÉTAILS DES ALERTES\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        // Détails de chaque alerte
        String details = historiqueAlertes.stream()
            .map(AlerteRecord::toString)
            .collect(Collectors.joining("\n"));
        
        sb.append(details);
        sb.append("\n\n═══════════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Obtient le modèle de gestion d'énergie.
     * @return GestionEnergie
     */
    public GestionEnergie getGestionEnergie() {
        return gestionEnergie;
    }
}