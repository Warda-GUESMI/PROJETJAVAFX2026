package simulation.modele.simulation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Record immuable pour représenter une alerte énergétique.
 * Conforme aux exigences Java 17 (Record).
 */
public record AlerteRecord(
    LocalDateTime timestamp,
    String type,
    String severite,
    String message,
    String statut,
    double valeurMesuree,
    double valeurSeuil
) {
    
    /**
     * Constructeur compact avec validation.
     */
    public AlerteRecord {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Le type d'alerte est obligatoire");
        }
        if (severite == null || !severite.matches("CRITIQUE|HAUTE|MOYENNE|BASSE")) {
            throw new IllegalArgumentException("Sévérité invalide");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Le message est obligatoire");
        }
        if (statut == null || !statut.matches("ACTIVE|RESOLUE|ACQUITTEE")) {
            statut = "ACTIVE";
        }
    }
    
    /**
     * Constructeur simplifié sans timestamp (utilise l'instant actuel).
     */
    public AlerteRecord(String type, String severite, String message, String statut, 
                        double valeurMesuree, double valeurSeuil) {
        this(LocalDateTime.now(), type, severite, message, statut, valeurMesuree, valeurSeuil);
    }
    
    /**
     * Retourne la date/heure formatée.
     * @return String formaté
     */
    public String dateHeure() {
        return timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
    
    /**
     * Crée une copie avec un nouveau statut.
     * @param nouveauStatut Le nouveau statut
     * @return Nouvelle instance avec statut modifié
     */
    public AlerteRecord avecStatut(String nouveauStatut) {
        return new AlerteRecord(timestamp, type, severite, message, nouveauStatut, 
                                valeurMesuree, valeurSeuil);
    }
    
    /**
     * Vérifie si l'alerte est active.
     * @return true si active
     */
    public boolean estActive() {
        return "ACTIVE".equals(statut);
    }
    
    /**
     * Vérifie si l'alerte est critique.
     * @return true si critique
     */
    public boolean estCritique() {
        return "CRITIQUE".equals(severite);
    }
    
    /**
     * Calcule le score de sévérité (0-1).
     * @return Score de sévérité
     */
    public double scoreSeverite() {
        return switch (severite) {
            case "CRITIQUE" -> 1.0;
            case "HAUTE" -> 0.75;
            case "MOYENNE" -> 0.5;
            case "BASSE" -> 0.25;
            default -> 0.0;
        };
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s (Statut: %s)",
            dateHeure(), severite, type, message, statut);
    }
}