package simulation.modele.simulation;
/**
 * Exception personnalisée pour les erreurs liées à l'énergie.
 
 */
public class EnergieException extends Exception {
    /**
    
     */
    public EnergieException(String message) {
        super(message);
    }
    
    public EnergieException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Factory pour énergie négative.
     
     */
    public static EnergieException energieNegative(double valeur) {
        return new EnergieException("Quantité d'énergie négative non autorisée : " + valeur);
    }
    /**
     * Factory pour simulation invalide.
    
     */
    public static EnergieException simulationInvalide(String detail) {
        return new EnergieException("Simulation invalide : " + detail);
    }
    /**
     * Factory pour décharge excédant (manquante avant).
     
     */
    public static EnergieException dechargeExcedant(double qte, double niveau) {
        return new EnergieException("Décharge excédant le niveau disponible : " + qte + " > " + niveau);
    }
}