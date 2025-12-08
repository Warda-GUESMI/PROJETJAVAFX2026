package simulation.modele.simulation;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Record immuable pour état de simulation.
 
 */
public record RecordSimulation(int temps, double consommation, double production) {
  
    public RecordSimulation {
        if (temps < 0 || consommation < 0 || production < 0) {
            try {
                throw EnergieException.energieNegative(Math.min(consommation, production));
            } catch (EnergieException ex) {
                Logger.getLogger(RecordSimulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Constructeur délégué depuis Simulation.
     */
    public RecordSimulation(Simulation sim) {
        this(sim.getTempsSimule(), sim.getConsommationTotale(), sim.getProduc()); // Délègue, propage si invalide
    }
    @Override
    public String toString() {
        return String.format("Temps : %d | Consommation : %.2f | Production : %.2f", temps, consommation, production);
    }
}