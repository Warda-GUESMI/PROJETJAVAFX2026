package simulation.modele.source;



/**
 * Interface sealed pour sources d'énergie (héritage restreint).
 * Permits inclut Batterie pour polymorphisme.
 */

public sealed interface SourceEnergie permits PanneauSolaire, Eolienne, Batterie { 
    /**
     * Production actuelle.
     * @return Production.
     */
    double getProduction();

    /**
     * Capacité max.
     * @return Capacité.
     */
    double getCapacite();

    /**
     * Méthode par défaut.
     * @return Production.
     */
    default double produireEnergie() {
        return getProduction();
    }
}