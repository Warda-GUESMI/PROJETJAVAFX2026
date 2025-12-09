package simulation.modele.simulation;

/**
 * Record immuable pour état de simulation.
 * Conforme aux exigences Java 17 (Record).
 */
public record RecordSimulation(int temps, double consommation, double production) {
    
    /**
     * Constructeur compact avec validation.
     * Lève IllegalArgumentException si valeurs négatives.
     */
    public RecordSimulation {
        if (temps < 0) {
            throw new IllegalArgumentException("Le temps ne peut pas être négatif : " + temps);
        }
        if (consommation < 0) {
            throw new IllegalArgumentException("La consommation ne peut pas être négative : " + consommation);
        }
        if (production < 0) {
            throw new IllegalArgumentException("La production ne peut pas être négative : " + production);
        }
    }
    
    /**
     * Constructeur délégué depuis Simulation.
     * @param sim L'objet Simulation source
     */
    public RecordSimulation(Simulation sim) {
        this(sim.getTempsSimule(), sim.getConsommationTotale(), sim.getProduc());
    }
    
    /**
     * Calcule le bilan énergétique.
     * @return production - consommation
     */
    public double bilan() {
        return production - consommation;
    }
    
    /**
     * Vérifie si la simulation est en excédent.
     * @return true si production >= consommation
     */
    public boolean estEnExcedent() {
        return production >= consommation;
    }
    
    /**
     * Vérifie si la simulation est en déficit.
     * @return true si production < consommation
     */
    public boolean estEnDeficit() {
        return production < consommation;
    }
    
    @Override
    public String toString() {
        return String.format("Temps : %d | Consommation : %.2f kWh | Production : %.2f kWh | Bilan : %.2f kWh", 
            temps, consommation, production, bilan());
    }
}