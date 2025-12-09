package Controleur;

import simulation.modele.simulation.EnergieException;
import simulation.modele.simulation.GestionEnergie;
import simulation.modele.source.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur moderne pour gérer les sources d'énergie.
 * Compatible avec VueGestionSource.
 */
public class ControleurSource {

    private final GestionEnergie gestionEnergie;

    public ControleurSource(GestionEnergie gestionEnergie) {
        if (gestionEnergie == null) {
            throw new IllegalArgumentException("GestionEnergie ne peut pas être null");
        }
        this.gestionEnergie = gestionEnergie;
    }

    // ============================= CREATE =============================
    public void ajouterPanneauSolaire(double surface, double rendement, double puissanceNominale) 
            throws EnergieException {
        gestionEnergie.ajouterSource(new PanneauSolaire(surface, rendement, puissanceNominale));
    }

    public void ajouterEolienne(double vitesseVent, double puissanceNominale) 
            throws EnergieException {
        gestionEnergie.ajouterSource(new Eolienne(vitesseVent, puissanceNominale));
    }

    public void ajouterBatterie(double capaciteMax, double niveauInitial, double efficacite) 
            throws EnergieException {
        gestionEnergie.ajouterSource(new Batterie(capaciteMax, niveauInitial, efficacite));
    }

    // ============================= READ =============================
    public List<SourceEnergie> obtenirSources() {
        return gestionEnergie.getSources();
    }

    public Optional<SourceEnergie> obtenirSourceParIndex(int index) {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        if (index >= 0 && index < sources.size()) {
            return Optional.of(sources.get(index));
        }
        return Optional.empty();
    }

    // ============================= DELETE =============================
    /**
     * Supprime une source d'énergie spécifique.
     * @param source Source à supprimer
     * @return true si supprimée, false sinon
     */
    public boolean supprimerSource(SourceEnergie source) {
        if (source == null) return false;
        return gestionEnergie.getSources().remove(source);
    }

    /**
     * Supprime une source par son index.
     * @param index Index de la source
     * @return true si supprimée, false sinon
     */
    public boolean supprimerSourceParIndex(int index) {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        if (index >= 0 && index < sources.size()) {
            sources.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Supprime toutes les sources.
     * @return Nombre de sources supprimées
     */
    public int supprimerTout() {
        int nb = gestionEnergie.getSources().size();
        gestionEnergie.getSources().clear();
        return nb;
    }

    // ============================= UPDATE =============================
    /**
     * Remplace une source à l'index donné.
     * @param index Index à remplacer
     * @param nouvelleSource Nouvelle source
     * @return true si remplacée, false sinon
     */
    public boolean remplacerSource(int index, SourceEnergie nouvelleSource) throws EnergieException {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        if (index >= 0 && index < sources.size()) {
            sources.set(index, nouvelleSource);
            return true;
        }
        return false;
    }

    // ============================= UTILITAIRES =============================
    public double calculerProductionTotale() {
        return gestionEnergie.getSources().stream()
                .mapToDouble(SourceEnergie::getProduction)
                .sum();
    }

    public String obtenirStatistiques() {
        long nbPanneaux = gestionEnergie.getSources().stream()
                .filter(PanneauSolaire.class::isInstance).count();
        long nbEoliennes = gestionEnergie.getSources().stream()
                .filter(Eolienne.class::isInstance).count();
        long nbBatteries = gestionEnergie.getSources().stream()
                .filter(Batterie.class::isInstance).count();

        double productionTotale = calculerProductionTotale();

        return String.format(
                "📊 STATISTIQUES SOURCES\n" +
                "Total sources : %d\n" +
                "  - Panneaux solaires : %d\n" +
                "  - Éoliennes : %d\n" +
                "  - Batteries : %d\n" +
                "Production totale : %.2f kWh",
                gestionEnergie.getSources().size(), nbPanneaux, nbEoliennes, nbBatteries, productionTotale
        );
    }

    public GestionEnergie getGestionEnergie() {
        return gestionEnergie;
    }
}
