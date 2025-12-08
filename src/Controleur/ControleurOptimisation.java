package Controleur;

import simulation.modele.simulation.*;
import simulation.modele.source.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur pour l'optimisation énergétique.
 * Génère des recommandations et calcule des métriques d'optimisation.
 */
public class ControleurOptimisation {
    
    private final GestionEnergie gestionEnergie;
    private double objectifEconomie = 20.0; // Objectif en pourcentage
    private Map<String, Double> parametresOptimises;
    
    /**
     * Constructeur du contrôleur.
     * @param gestionEnergie Le modèle de gestion d'énergie
     */
    public ControleurOptimisation(GestionEnergie gestionEnergie) {
        if (gestionEnergie == null) {
            throw new IllegalArgumentException("GestionEnergie ne peut pas être null");
        }
        this.gestionEnergie = gestionEnergie;
        this.parametresOptimises = new HashMap<>();
    }
    
    /**
     * Calcule le rendement actuel du système.
     * Utilise un stream pour calculer.
     * @return Rendement en pourcentage
     */
    public double calculerRendementActuel() {
        double production = gestionEnergie.productionTotale();
        double consommation = gestionEnergie.consommationTotale();
        
        if (production == 0) return 0.0;
        
        double rendement = (Math.min(production, consommation) / production) * 100.0;
        return Math.min(rendement, 100.0);
    }
    
    /**
     * Calcule le rendement optimal théorique.
     * @return Rendement optimal en pourcentage
     */
    public double calculerRendementOptimal() {
        // Le rendement optimal serait de réduire les pertes et optimiser la production
        double rendementActuel = calculerRendementActuel();
        double marge = 100.0 - rendementActuel;
        
        // On peut espérer gagner 60% de la marge actuelle avec optimisation
        return rendementActuel + (marge * 0.6);
    }
    
    /**
     * Calcule les économies potentielles en kWh.
     * @return Économies en kWh
     */
    public double calculerEconomiesPotentielles() {
        double consommation = gestionEnergie.consommationTotale();
        double rendementActuel = calculerRendementActuel();
        double rendementOptimal = calculerRendementOptimal();
        
        double amelioration = (rendementOptimal - rendementActuel) / 100.0;
        return consommation * amelioration;
    }
    
    /**
     * Calcule le score d'efficacité global (0-100).
     * Utilise plusieurs métriques avec streams.
     * @return Score d'efficacité
     */
    public double calculerScoreEfficacite() {
        double rendement = calculerRendementActuel();
        double bilan = gestionEnergie.productionTotale() - gestionEnergie.consommationTotale();
        double ratioSources = calculerRatioSourcesRenouvelables();
        
        // Score pondéré
        double score = (rendement * 0.4) + 
                      ((bilan >= 0 ? 50 : 30) * 0.3) + 
                      (ratioSources * 0.3);
        
        return Math.min(score, 100.0);
    }
    
    /**
     * Calcule le ratio de sources d'énergie renouvelables.
     * @return Ratio en pourcentage
     */
    private double calculerRatioSourcesRenouvelables() {
        long totalSources = gestionEnergie.getSources().size();
        if (totalSources == 0) return 0.0;
        
        long sourcesRenouvelables = gestionEnergie.getSources().stream()
            .filter(s -> s instanceof PanneauSolaire || s instanceof Eolienne)
            .count();
        
        return (sourcesRenouvelables * 100.0) / totalSources;
    }
    
    /**
     * Calcule les émissions de CO2 estimées.
     * @return Émissions en kg de CO2
     */
    public double calculerEmissionsCO2() {
        // Estimation : 0.5 kg CO2 par kWh consommé
        double consommation = gestionEnergie.consommationTotale();
        double ratioRenouvelable = calculerRatioSourcesRenouvelables() / 100.0;
        
        // Les sources renouvelables réduisent les émissions
        return consommation * 0.5 * (1.0 - ratioRenouvelable);
    }
    
    /**
     * Calcule la production optimale théorique.
     * @return Production optimale en kWh
     */
    public double calculerProductionOptimale() {
        double production = gestionEnergie.productionTotale();
        // On peut augmenter de 15% avec optimisation
        return production * 1.15;
    }
    
    /**
     * Calcule la consommation optimale théorique.
     * @return Consommation optimale en kWh
     */
    public double calculerConsommationOptimale() {
        double consommation = gestionEnergie.consommationTotale();
        // On peut réduire de 20% avec optimisation
        return consommation * 0.8;
    }
    
    /**
     * Calcule la répartition de la consommation.
     * Utilise un stream avec Collectors.
     * @return Map nom -> consommation
     */
    public Map<String, Double> calculerRepartitionConsommation() {
        return gestionEnergie.getConsommateurs().stream()
            .collect(Collectors.toMap(
                Consommateur::getNom,
                Consommateur::getConsommation
            ));
    }
    
    /**
     * Génère les recommandations d'optimisation.
     * Utilise des streams et expressions Lambda.
     * @return Liste de recommandations
     */
    public List<RecommandationOptimisation> genererRecommandations() {
        List<RecommandationOptimisation> recommandations = new ArrayList<>();
        
        // Analyser la production
        double production = gestionEnergie.productionTotale();
        double consommation = gestionEnergie.consommationTotale();
        double bilan = production - consommation;
        
        // Recommandation sur le bilan énergétique
        if (bilan < 0) {
            recommandations.add(new RecommandationOptimisation(
                "Augmenter la capacité de production",
                "Le système est en déficit énergétique. Ajouter des panneaux solaires ou éoliennes pour équilibrer le bilan.",
                "HAUTE",
                "ÉLEVÉ",
                Math.abs(bilan),
                "PRODUCTION"
            ));
        }
        
        // Recommandation sur les sources renouvelables
        double ratioRenouvelable = calculerRatioSourcesRenouvelables();
        if (ratioRenouvelable < 70) {
            recommandations.add(new RecommandationOptimisation(
                "Augmenter les sources renouvelables",
                String.format("Votre système utilise %.1f%% de sources renouvelables. Visez au moins 70%% pour réduire les émissions.", ratioRenouvelable),
                "HAUTE",
                "ÉLEVÉ",
                0.0,
                "ENVIRONNEMENT"
            ));
        }
        
        // Recommandation sur le stockage
        long nombreBatteries = gestionEnergie.getSources().stream()
            .filter(Batterie.class::isInstance)
            .count();
        
        if (nombreBatteries == 0) {
            recommandations.add(new RecommandationOptimisation(
                "Ajouter un système de stockage",
                "Installer des batteries pour stocker l'énergie excédentaire et lisser les pics de consommation.",
                "MOYENNE",
                "MOYEN",
                consommation * 0.15,
                "STOCKAGE"
            ));
        }
        
        // Recommandation sur la consommation
        if (consommation > 80) {
            recommandations.add(new RecommandationOptimisation(
                "Optimiser la consommation",
                "La consommation est élevée. Envisager de réduire la consommation des appareils énergivores.",
                "MOYENNE",
                "MOYEN",
                consommation * 0.2,
                "CONSOMMATION"
            ));
        }
        
        // Recommandation sur les panneaux solaires
        long nombrePanneaux = gestionEnergie.getSources().stream()
            .filter(PanneauSolaire.class::isInstance)
            .count();
        
        if (nombrePanneaux == 0 && bilan < 0) {
            recommandations.add(new RecommandationOptimisation(
                "Installer des panneaux solaires",
                "L'énergie solaire est une source propre et économique. Installation recommandée pour améliorer le bilan.",
                "HAUTE",
                "ÉLEVÉ",
                Math.abs(bilan) * 0.5,
                "PRODUCTION"
            ));
        }
        
        // Recommandation sur l'efficacité
        double rendement = calculerRendementActuel();
        if (rendement < 70) {
            recommandations.add(new RecommandationOptimisation(
                "Améliorer l'efficacité du système",
                String.format("Le rendement actuel (%.1f%%) est faible. Vérifier les pertes et optimiser la distribution.", rendement),
                "HAUTE",
                "MOYEN",
                consommation * 0.1,
                "EFFICACITÉ"
            ));
        }
        
        // Trier par priorité et impact
        return recommandations.stream()
            .sorted((r1, r2) -> Double.compare(r2.scoreGlobal(), r1.scoreGlobal()))
            .toList();
    }
    
    /**
     * Génère une analyse complète du système.
     * @return Texte d'analyse formaté
     */
    public String genererAnalyseComplete() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════\n");
        sb.append("     ANALYSE ÉNERGÉTIQUE COMPLÈTE\n");
        sb.append("═══════════════════════════════════════\n\n");
        
        // Métriques actuelles
        sb.append("📊 MÉTRIQUES ACTUELLES\n");
        sb.append("─────────────────────────────────────\n");
        sb.append(String.format("Production totale: %.2f kWh\n", gestionEnergie.productionTotale()));
        sb.append(String.format("Consommation totale: %.2f kWh\n", gestionEnergie.consommationTotale()));
        sb.append(String.format("Bilan: %.2f kWh\n", gestionEnergie.productionTotale() - gestionEnergie.consommationTotale()));
        sb.append(String.format("Rendement: %.1f%%\n\n", calculerRendementActuel()));
        
        // Sources d'énergie
        sb.append("⚡ SOURCES D'ÉNERGIE\n");
        sb.append("─────────────────────────────────────\n");
        sb.append(String.format("Nombre de sources: %d\n", gestionEnergie.getSources().size()));
        sb.append(String.format("Sources renouvelables: %.1f%%\n", calculerRatioSourcesRenouvelables()));
        sb.append(String.format("Émissions CO2: %.2f kg\n\n", calculerEmissionsCO2()));
        
        // Potentiel d'optimisation
        sb.append("🎯 POTENTIEL D'OPTIMISATION\n");
        sb.append("─────────────────────────────────────\n");
        sb.append(String.format("Rendement optimal: %.1f%%\n", calculerRendementOptimal()));
        sb.append(String.format("Économies potentielles: %.2f kWh\n", calculerEconomiesPotentielles()));
        sb.append(String.format("Score d'efficacité: %.0f/100\n\n", calculerScoreEfficacite()));
        
        // Recommandations
        List<RecommandationOptimisation> recommandations = genererRecommandations();
        sb.append(String.format("💡 %d RECOMMANDATIONS\n", recommandations.size()));
        sb.append("─────────────────────────────────────\n");
        recommandations.stream()
            .limit(3)
            .forEach(r -> sb.append(String.format("• [%s] %s\n", r.priorite(), r.titre())));
        
        sb.append("\n═══════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Applique une recommandation.
     * @param recommandation La recommandation à appliquer
     */
    public void appliquerRecommandation(RecommandationOptimisation recommandation) {
        // Simulation d'application
        System.out.println("Application de : " + recommandation.titre());
        
        // Enregistrer dans les paramètres optimisés
        parametresOptimises.put(recommandation.categorie(), recommandation.economieEstimee());
    }
    
    /**
     * Applique toutes les recommandations.
     */
    public void appliquerToutesRecommandations() {
        genererRecommandations().forEach(this::appliquerRecommandation);
    }
    
    /**
     * Définit l'objectif d'économie.
     * @param objectif Objectif en pourcentage
     */
    public void definirObjectifEconomie(double objectif) {
        this.objectifEconomie = Math.max(0, Math.min(objectif, 100));
    }
    
    /**
     * Simule un scénario d'optimisation.
     * @param augmentationProd Augmentation de production en %
     * @param reductionConso Réduction de consommation en %
     * @return Description du résultat
     */
    public String simulerScenario(double augmentationProd, double reductionConso) {
        double prodActuelle = gestionEnergie.productionTotale();
        double consoActuelle = gestionEnergie.consommationTotale();
        
        double nouvelleProd = prodActuelle * (1 + augmentationProd / 100.0);
        double nouvelleConso = consoActuelle * (1 - reductionConso / 100.0);
        double nouveauBilan = nouvelleProd - nouvelleConso;
        
        return String.format(
            "RÉSULTAT DE SIMULATION\n\n" +
            "Avant:\n" +
            "  Production: %.2f kWh\n" +
            "  Consommation: %.2f kWh\n" +
            "  Bilan: %.2f kWh\n\n" +
            "Après:\n" +
            "  Production: %.2f kWh (+%.1f%%)\n" +
            "  Consommation: %.2f kWh (-%.1f%%)\n" +
            "  Bilan: %.2f kWh\n\n" +
            "Amélioration du bilan: %.2f kWh",
            prodActuelle, consoActuelle, prodActuelle - consoActuelle,
            nouvelleProd, augmentationProd,
            nouvelleConso, reductionConso,
            nouveauBilan,
            nouveauBilan - (prodActuelle - consoActuelle)
        );
    }
    
    /**
     * Génère un rapport d'optimisation complet.
     * @return Rapport formaté
     */
    public String genererRapportOptimisation() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════════\n");
        sb.append("           RAPPORT D'OPTIMISATION COMPLET\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        sb.append(genererAnalyseComplete());
        
        sb.append("\n\n📋 DÉTAIL DES RECOMMANDATIONS\n");
        sb.append("═══════════════════════════════════════════════════════\n\n");
        
        genererRecommandations().forEach(r -> {
            sb.append(String.format("┌─ [%s] %s\n", r.priorite(), r.titre()));
            sb.append(String.format("│  Description: %s\n", r.description()));
            sb.append(String.format("│  Impact: %s\n", r.impact()));
            sb.append(String.format("│  Économies estimées: %.2f kWh\n", r.economieEstimee()));
            sb.append(String.format("└─ Catégorie: %s\n\n", r.categorie()));
        });
        
        sb.append("═══════════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
    
    /**
     * Exporte le rapport complet.
     * @return Rapport exportable
     */
    public String exporterRapportComplet() {
        return genererRapportOptimisation();
    }
    
    /**
     * Réinitialise les paramètres d'optimisation.
     */
    public void reinitialiser() {
        parametresOptimises.clear();
        objectifEconomie = 20.0;
    }
    
    /**
     * Obtient le modèle de gestion d'énergie.
     * @return GestionEnergie
     */
    public GestionEnergie getGestionEnergie() {
        return gestionEnergie;
    }
}