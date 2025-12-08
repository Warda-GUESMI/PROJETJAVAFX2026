package simulation.modele.simulation;

/**
 * Classe AlerteEnergie pour surveiller les seuils de consommation et production.
 */
public final class AlerteEnergie {
    private final double seuilConsommation;
    private final double seuilProduction;
        public AlerteEnergie(double seuilConsommation, double seuilProduction) {
        if (seuilConsommation < 0 || seuilProduction < 0) {
            throw new IllegalArgumentException("Les seuils doivent être positifs.");
        }
        this.seuilConsommation = seuilConsommation;
        this.seuilProduction = seuilProduction;
    }
    /**
     * Vérifie si les seuils sont dépassés via une Predicate (interface fonctionnelle).
     
     */
    public boolean verifierSeuils(Simulation sim) {
        java.util.function.Predicate<Simulation> predicateAlerte = s ->
            s.getConsommationTotale() > seuilConsommation || s.getProduc() < seuilProduction;
        return predicateAlerte.test(sim);
    }
}