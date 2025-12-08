package Controleur;

import simulation.modele.simulation.EnergieException;
import simulation.modele.simulation.GestionEnergie;
import simulation.modele.source.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Contrôleur CRUD complet pour les sources d'énergie.
 */
public class ControleurSource {
    
    private final GestionEnergie gestionEnergie;
    
    public ControleurSource(GestionEnergie gestionEnergie) {
        if (gestionEnergie == null) {
            throw new IllegalArgumentException("GestionEnergie ne peut pas être null");
        }
        this.gestionEnergie = gestionEnergie;
    }
    
    // ============================================================================
    // CREATE - Ajouter des sources
    // ============================================================================
    
    /**
     * CREATE : Ajoute un panneau solaire.
     */
    public void ajouterPanneauSolaire(double surface, double rendement, double puissanceNominale) 
            throws EnergieException {
        SourceEnergie source = new PanneauSolaire(surface, rendement, puissanceNominale);
        gestionEnergie.ajouterSource(source);
        System.out.println("✅ CREATE : Panneau solaire ajouté");
    }
    
    /**
     * CREATE : Ajoute une éolienne.
     */
    public void ajouterEolienne(double vitesseVent, double puissanceNominale) 
            throws EnergieException {
        SourceEnergie source = new Eolienne(vitesseVent, puissanceNominale);
        gestionEnergie.ajouterSource(source);
        System.out.println("✅ CREATE : Éolienne ajoutée");
    }
    
    /**
     * CREATE : Ajoute une batterie.
     */
    public void ajouterBatterie(double capaciteMax, double niveauInitial, double efficacite) 
            throws EnergieException {
        SourceEnergie source = new Batterie(capaciteMax, niveauInitial, efficacite);
        gestionEnergie.ajouterSource(source);
        System.out.println("✅ CREATE : Batterie ajoutée");
    }
    
    // ============================================================================
    // READ - Lire/Consulter des sources
    // ============================================================================
    
    /**
     * READ : Obtient toutes les sources.
     */
    public List<SourceEnergie> obtenirSources() {
        return gestionEnergie.getSources();
    }
    
    /**
     * READ : Obtient une source par index.
     */
    public Optional<SourceEnergie> obtenirSourceParIndex(int index) {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        if (index >= 0 && index < sources.size()) {
            return Optional.of(sources.get(index));
        }
        return Optional.empty();
    }
    
    /**
     * READ : Filtre les sources par type.
     */
    public List<SourceEnergie> filtrerParType(Class<? extends SourceEnergie> type) {
        return gestionEnergie.getSources().stream()
                .filter(type::isInstance)
                .toList();
    }
    
    /**
     * READ : Obtient toutes les batteries.
     */
    public List<Batterie> obtenirBatteries() {
        return gestionEnergie.getSources().stream()
                .filter(Batterie.class::isInstance)
                .map(s -> (Batterie) s)
                .toList();
    }
    
    /**
     * READ : Obtient tous les panneaux solaires.
     */
    public List<PanneauSolaire> obtenirPanneauxSolaires() {
        return gestionEnergie.getSources().stream()
                .filter(PanneauSolaire.class::isInstance)
                .map(s -> (PanneauSolaire) s)
                .toList();
    }
    
    /**
     * READ : Obtient toutes les éoliennes.
     */
    public List<Eolienne> obtenirEoliennes() {
        return gestionEnergie.getSources().stream()
                .filter(Eolienne.class::isInstance)
                .map(s -> (Eolienne) s)
                .toList();
    }
    
    /**
     * READ : Recherche des sources par capacité minimale.
     */
    public List<SourceEnergie> rechercherParCapaciteMin(double capaciteMin) {
        return gestionEnergie.getSources().stream()
                .filter(s -> s.getCapacite() >= capaciteMin)
                .toList();
    }
    
    /**
     * READ : Recherche des sources par production minimale.
     */
    public List<SourceEnergie> rechercherParProductionMin(double productionMin) {
        return gestionEnergie.getSources().stream()
                .filter(s -> s.getProduction() >= productionMin)
                .toList();
    }
    
    /**
     * READ : Compte le nombre de sources par type.
     */
    public long compterParType(Class<? extends SourceEnergie> type) {
        return gestionEnergie.getSources().stream()
                .filter(type::isInstance)
                .count();
    }
    
    // ============================================================================
    // UPDATE - Modifier des sources
    // ============================================================================
    
    /**
     * UPDATE : Modifie la vitesse du vent d'une éolienne.
     */
    public boolean modifierVitesseVentEolienne(int index, double nouvelleVitesse) 
            throws EnergieException {
        Optional<SourceEnergie> sourceOpt = obtenirSourceParIndex(index);
        
        if (sourceOpt.isPresent() && sourceOpt.get() instanceof Eolienne) {
            Eolienne eolienne = (Eolienne) sourceOpt.get();
            eolienne.setVitesseVent(nouvelleVitesse);
            System.out.println("✅ UPDATE : Vitesse du vent modifiée");
            return true;
        }
        
        System.out.println("❌ UPDATE : Éolienne non trouvée à l'index " + index);
        return false;
    }
    
    /**
     * UPDATE : Charge une batterie.
     */
    public boolean chargerBatterie(int index, double quantite) throws EnergieException {
        Optional<SourceEnergie> sourceOpt = obtenirSourceParIndex(index);
        
        if (sourceOpt.isPresent() && sourceOpt.get() instanceof Batterie) {
            Batterie batterie = (Batterie) sourceOpt.get();
            batterie.charger(quantite);
            System.out.println("✅ UPDATE : Batterie chargée de " + quantite + " kWh");
            return true;
        }
        
        System.out.println("❌ UPDATE : Batterie non trouvée à l'index " + index);
        return false;
    }
    
    /**
     * UPDATE : Décharge une batterie.
     */
    public boolean dechargerBatterie(int index, double quantite) throws EnergieException {
        Optional<SourceEnergie> sourceOpt = obtenirSourceParIndex(index);
        
        if (sourceOpt.isPresent() && sourceOpt.get() instanceof Batterie) {
            Batterie batterie = (Batterie) sourceOpt.get();
            batterie.decharger(quantite);
            System.out.println("✅ UPDATE : Batterie déchargée de " + quantite + " kWh");
            return true;
        }
        
        System.out.println("❌ UPDATE : Batterie non trouvée à l'index " + index);
        return false;
    }
    
    /**
     * UPDATE : Remplace une source à un index donné.
     */
    public boolean remplacerSource(int index, SourceEnergie nouvelleSource) 
            throws EnergieException {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        
        if (index >= 0 && index < sources.size()) {
            sources.set(index, nouvelleSource);
            System.out.println("✅ UPDATE : Source remplacée à l'index " + index);
            return true;
        }
        
        System.out.println("❌ UPDATE : Index invalide " + index);
        return false;
    }
    
    // ============================================================================
    // DELETE - Supprimer des sources
    // ============================================================================
    
    /**
     * DELETE : Supprime une source par référence.
     */
    public boolean supprimerSource(SourceEnergie source) {
        if (source == null) {
            System.out.println("❌ DELETE : Source null");
            return false;
        }
        
        boolean supprime = gestionEnergie.getSources().remove(source);
        if (supprime) {
            System.out.println("✅ DELETE : Source supprimée");
        } else {
            System.out.println("❌ DELETE : Source non trouvée");
        }
        return supprime;
    }
    
    /**
     * DELETE : Supprime une source par index.
     */
    public boolean supprimerSourceParIndex(int index) {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        
        if (index >= 0 && index < sources.size()) {
            sources.remove(index);
            System.out.println("✅ DELETE : Source supprimée à l'index " + index);
            return true;
        }
        
        System.out.println("❌ DELETE : Index invalide " + index);
        return false;
    }
    
    /**
     * DELETE : Supprime toutes les sources d'un type donné.
     */
    public int supprimerParType(Class<? extends SourceEnergie> type) {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        int nbSupprime = (int) sources.stream()
                .filter(type::isInstance)
                .count();
        
        sources.removeIf(type::isInstance);
        System.out.println("✅ DELETE : " + nbSupprime + " source(s) de type " + 
                           type.getSimpleName() + " supprimée(s)");
        return nbSupprime;
    }
    
    /**
     * DELETE : Supprime toutes les sources.
     */
    public int supprimerTout() {
        int nb = gestionEnergie.getSources().size();
        gestionEnergie.getSources().clear();
        System.out.println("✅ DELETE : " + nb + " source(s) supprimée(s)");
        return nb;
    }
    
    /**
     * DELETE : Supprime les sources avec production nulle.
     */
    public int supprimerSourcesInactives() {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        int nbSupprime = (int) sources.stream()
                .filter(s -> s.getProduction() == 0)
                .count();
        
        sources.removeIf(s -> s.getProduction() == 0);
        System.out.println("✅ DELETE : " + nbSupprime + " source(s) inactive(s) supprimée(s)");
        return nbSupprime;
    }
    
    // ============================================================================
    // MÉTHODES UTILITAIRES
    // ============================================================================
    
    /**
     * Calcule la production totale.
     */
    public double calculerProductionTotale() {
        return gestionEnergie.getSources().stream()
                .mapToDouble(SourceEnergie::getProduction)
                .sum();
    }
    
    /**
     * Obtient des statistiques sur les sources.
     */
    public String obtenirStatistiques() {
        List<SourceEnergie> sources = gestionEnergie.getSources();
        
        long nbPanneaux = compterParType(PanneauSolaire.class);
        long nbEoliennes = compterParType(Eolienne.class);
        long nbBatteries = compterParType(Batterie.class);
        
        double productionTotale = calculerProductionTotale();
        
        return String.format(
            "📊 STATISTIQUES SOURCES\n" +
            "Total sources : %d\n" +
            "  - Panneaux solaires : %d\n" +
            "  - Éoliennes : %d\n" +
            "  - Batteries : %d\n" +
            "Production totale : %.2f kWh",
            sources.size(), nbPanneaux, nbEoliennes, nbBatteries, productionTotale
        );
    }
    
    public GestionEnergie getGestionEnergie() {
        return gestionEnergie;
    }
}