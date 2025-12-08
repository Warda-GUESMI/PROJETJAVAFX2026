package Controleur;

import simulation.modele.simulation.Consommateur;
import simulation.modele.simulation.EnergieException;
import simulation.modele.simulation.GestionEnergie;
import java.util.List;
import java.util.function.Predicate;

/**
 * Contrôleur pour gérer les consommateurs d'énergie.
 * Fait le lien entre la vue et le modèle.
 */
public class ControleurConsommateur {
    
    private final GestionEnergie gestionEnergie;
    
    /**
     * Constructeur du contrôleur.
     * @param gestionEnergie Le modèle de gestion d'énergie
     */
    public ControleurConsommateur(GestionEnergie gestionEnergie) {
        if (gestionEnergie == null) {
            throw new IllegalArgumentException("GestionEnergie ne peut pas être null");
        }
        this.gestionEnergie = gestionEnergie;
    }
    
    /**
     * Ajoute un consommateur.
     * @param nom Nom du consommateur
     * @param consommation Consommation par unité de temps
     * @throws EnergieException Si paramètres invalides
     */
    public void ajouterConsommateur(String nom, double consommation) throws EnergieException {
        Consommateur consommateur = new Consommateur(nom, consommation);
        gestionEnergie.ajouterConsommateur(consommateur);
    }
    
    /**
     * Ajoute un appareil à un consommateur existant.
     * Utilise un stream avec filter pour trouver le consommateur.
     * @param nomConsommateur Nom du consommateur
     * @param nomAppareil Nom de l'appareil
     * @param consommationAppareil Consommation de l'appareil
     */
    public void ajouterAppareil(String nomConsommateur, String nomAppareil, double consommationAppareil) {
        gestionEnergie.getConsommateurs().stream()
                .filter(c -> c.getNom().equals(nomConsommateur))
                .findFirst()
                .ifPresent(c -> c.ajouterConsommation(nomAppareil, consommationAppareil));
    }
    
    /**
     * Ajuste la consommation d'un consommateur par un facteur multiplicateur.
     * Utilise une expression Lambda avec Predicate.
     * @param nomConsommateur Nom du consommateur
     * @param facteur Facteur multiplicateur
     */
    public void ajusterConsommation(String nomConsommateur, double facteur) {
        // Utilisation d'une interface fonctionnelle Predicate
        Predicate<Consommateur> predicatNom = c -> c.getNom().equals(nomConsommateur);
        
        gestionEnergie.getConsommateurs().stream()
                .filter(predicatNom) // Interface fonctionnelle
                .findFirst()
                .ifPresent(c -> c.ajusterConso(facteur)); // Référence de méthode
    }
    
    /**
     * Supprime un consommateur.
     * @param consommateur Le consommateur à supprimer
     */
    public void supprimerConsommateur(Consommateur consommateur) {
        if (consommateur == null) {
            throw new IllegalArgumentException("Consommateur ne peut pas être null");
        }
        
        List<Consommateur> consommateurs = gestionEnergie.getConsommateurs();
        consommateurs.removeIf(c -> c.equals(consommateur)); // Expression Lambda
    }
    
    /**
     * Obtient tous les consommateurs.
     * @return Liste des consommateurs
     */
    public List<Consommateur> obtenirConsommateurs() {
        return gestionEnergie.getConsommateurs().stream()
                .toList(); // Stream
    }
    
    /**
     * Calcule la consommation totale.
     * Utilise un stream avec mapToDouble et référence de méthode.
     * @return Consommation totale
     */
    public double calculerConsommationTotale() {
        return gestionEnergie.getConsommateurs().stream()
                .mapToDouble(Consommateur::getConsommation) // Référence de méthode
                .sum();
    }
    
    /**
     * Trouve les consommateurs dont la consommation dépasse un seuil.
     * Utilise un stream avec filter et Lambda.
     * @param seuil Le seuil de consommation
     * @return Liste des consommateurs au-dessus du seuil
     */
    public List<Consommateur> trouverConsommateursAuDessusSeuil(double seuil) {
        return gestionEnergie.getConsommateurs().stream()
                .filter(c -> c.getConsommation() > seuil) // Expression Lambda
                .toList();
    }
    
    /**
     * Obtient le modèle de gestion.
     * @return GestionEnergie
     */
    public GestionEnergie getGestionEnergie() {
        return gestionEnergie;
    }
}
