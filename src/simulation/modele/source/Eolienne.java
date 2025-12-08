package simulation.modele.source;

import simulation.modele.simulation.EnergieException;

/**
 * Implémentation finale d'Eolienne (héritage restreint via sealed).
 */
public final class Eolienne implements SourceEnergie {
    private double vitesseVent;
    private final double puissanceNominale; // Immuable

    /**
     * Constructeur.
     * @param vitesseVent Vitesse du vent (≥0).
     * @param puissanceNominale Puissance nominale (>0).
     */
    public Eolienne(double vitesseVent, double puissanceNominale) throws EnergieException {
        if (vitesseVent < 0 || puissanceNominale <= 0) {
            throw EnergieException.energieNegative(vitesseVent);
        }
        this.vitesseVent = vitesseVent;
        this.puissanceNominale = puissanceNominale;
    }

    @Override
    public double produireEnergie() {
        return puissanceNominale * Math.pow(Math.max(0, vitesseVent / 10), 3);
    }

    @Override
    public double getCapacite() {
        return puissanceNominale;
    }

    @Override
    public double getProduction() {
        return produireEnergie();
    }

    // Setter contrôlé pour vent (ex. pour simulation)
    public void setVitesseVent(double vitesse) throws EnergieException {
        if (vitesse < 0) throw EnergieException.energieNegative(vitesse);
        this.vitesseVent = vitesse;
    }
}