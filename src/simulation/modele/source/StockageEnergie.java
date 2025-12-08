package simulation.modele.source;

import simulation.modele.simulation.EnergieException;

/**
 * Interface sealed pour stockage (héritage restreint).
 */
public sealed interface StockageEnergie permits Batterie {
    /**
     * Charge énergie.
    
     */
    void charger(double qte) throws EnergieException;

    /**
     * Décharge énergie.
     
     */
    void decharger(double qte) throws EnergieException;

    /**
     * Niveau actuel.
     
     */
    double getCapacite();  // Pour colCapacite dans VueGestionSource
    double getNiveau();
}