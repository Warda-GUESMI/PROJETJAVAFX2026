package simulation.modele.simulation;

/**
 * Record immuable pour représenter une recommandation d'optimisation.
 * Conforme aux exigences Java 17 (Record).
 */
public record RecommandationOptimisation(
    String titre,
    String description,
    String priorite,
    String impact,
    double economieEstimee,
    String categorie
) {
    
    /**
     * Constructeur compact avec validation.
     */
    public RecommandationOptimisation {
        if (titre == null || titre.isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("La description est obligatoire");
        }
        if (priorite == null || !priorite.matches("HAUTE|MOYENNE|BASSE")) {
            priorite = "MOYENNE";
        }
        if (impact == null || !impact.matches("ÉLEVÉ|MOYEN|FAIBLE")) {
            impact = "MOYEN";
        }
        if (economieEstimee < 0) {
            economieEstimee = 0;
        }
        if (categorie == null || categorie.isEmpty()) {
            categorie = "GÉNÉRAL";
        }
    }
    
    /**
     * Constructeur simplifié sans économie estimée.
     */
    public RecommandationOptimisation(String titre, String description, String priorite, 
                                       String impact, String categorie) {
        this(titre, description, priorite, impact, 0.0, categorie);
    }
    
    /**
     * Calcule le score de priorité (0-1).
     * @return Score de priorité
     */
    public double scorePriorite() {
        return switch (priorite) {
            case "HAUTE" -> 1.0;
            case "MOYENNE" -> 0.5;
            case "BASSE" -> 0.25;
            default -> 0.0;
        };
    }
    
    /**
     * Calcule le score d'impact (0-1).
     * @return Score d'impact
     */
    public double scoreImpact() {
        return switch (impact) {
            case "ÉLEVÉ" -> 1.0;
            case "MOYEN" -> 0.5;
            case "FAIBLE" -> 0.25;
            default -> 0.0;
        };
    }
    
    /**
     * Calcule le score global de la recommandation.
     * @return Score global
     */
    public double scoreGlobal() {
        return (scorePriorite() + scoreImpact()) / 2.0;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - Impact: %s | Économie: %.2f kWh",
            priorite, titre, impact, economieEstimee);
    }
}