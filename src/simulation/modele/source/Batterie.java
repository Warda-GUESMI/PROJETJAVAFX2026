package simulation.modele.source;

import simulation.modele.simulation.EnergieException;

/**
 * Classe Batterie pour stockage d'énergie.
 
 */
public final class Batterie implements SourceEnergie, StockageEnergie {

    private double capaciteMax;
    private double niveauActuel;
    private final double efficacite;

   
    public Batterie(double capaciteMax, double niveauInitial, double efficacite) throws EnergieException {
        if (capaciteMax <= 0 || niveauInitial < 0 || niveauInitial > capaciteMax || efficacite < 0 || efficacite > 1) {
            throw EnergieException.simulationInvalide("Paramètres Batterie invalides");
        }
        this.capaciteMax = capaciteMax;
        this.niveauActuel = niveauInitial;
        this.efficacite = efficacite;
    }

    /**
     * Charge énergie (ajoute au niveau actuel).
    
     */
    @Override
    public void charger(double qte) throws EnergieException {
        if (qte < 0) {
            throw EnergieException.energieNegative(qte);
        }
        niveauActuel = Math.min(capaciteMax, niveauActuel + qte * efficacite);
    }

    /**
     * Décharge énergie (soustraire du niveau).
     
     */
    @Override
    public void decharger(double qte) throws EnergieException {
        if (qte < 0) {
            throw EnergieException.energieNegative(qte);
        }
        if (qte > niveauActuel) {
         throw EnergieException.dechargeExcedant(qte, this.getNiveauActuel());        }
        niveauActuel = Math.max(0, niveauActuel - qte);
    }

    
     
    @Override
    public double getNiveau() {
        return niveauActuel;
    }

    /**
     * Production (0 pour batterie, ou niveau si décharge).
    
     */
    @Override
    public double getProduction() {
        return 0.0;
    }

    /**
     * Capacité maximale.
    
     */
    @Override
    public double getCapacite() {
        return capaciteMax;
    }

    /**
     * Produire énergie (délégué).
.
     */
    @Override
    public double produireEnergie() {
        return getProduction();
    }

    public double getNiveauActuel() { return niveauActuel;};
    }
