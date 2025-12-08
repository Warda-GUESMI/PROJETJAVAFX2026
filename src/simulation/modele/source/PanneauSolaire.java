package simulation.modele.source;

import simulation.modele.simulation.EnergieException;

/**
 * Implémentation finale de PanneauSolaire (héritage restreint).
 */
public final class PanneauSolaire implements SourceEnergie {
    private final double surface; 
    private final double rendement; 
    private final double puissanceNominale; 

    
    public PanneauSolaire(double surface, double rendement, double puissanceNominale) throws EnergieException {
        if (surface <= 0 || rendement < 0 || rendement > 1 || puissanceNominale <= 0) {
            throw EnergieException.simulationInvalide("Paramètres Panneau invalides");
        }
        this.surface = surface;
        this.rendement = rendement;
        this.puissanceNominale = puissanceNominale;
    }

    @Override
    public double produireEnergie() {
        return surface * rendement * puissanceNominale; // Logique simple ; ajoute facteur soleil futur
    }

    @Override
    public double getCapacite() {
        return puissanceNominale;
    }

    @Override
    public double getProduction() {
        return produireEnergie();
    }
}