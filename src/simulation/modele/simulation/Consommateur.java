package simulation.modele.simulation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * Classe Consommateur représentant un utilisateur d'énergie.
 
 */
public final class Consommateur {
    private String nom;
    private double consoParUniteTemps;
    private final Map<String, Double> consommations; // Mutable interne, mais getter immuable
    
    public Consommateur(String nom, double consoParUniteTemps) throws EnergieException { // AJOUT throws
    if (nom == null || nom.isEmpty()) {
        throw new IllegalArgumentException("Nom obligatoire et non vide.");
    }
    if (consoParUniteTemps < 0) {
        throw EnergieException.energieNegative(consoParUniteTemps);
    }
    this.nom = nom;
    this.consoParUniteTemps = consoParUniteTemps;
    this.consommations = new HashMap<>();
}
    /**
     * Retourne la consommation actuelle par unité de temps.

     */
    public double consommerEnergie() {
        return consoParUniteTemps;
    }
    /**
     * Ajuste par multiplication (facteur >0).
    
     */
    public void ajusterConso(double facteur) {
        if (facteur <= 0) {
            throw new IllegalArgumentException("Facteur d'ajustement doit être positif.");
        }
        consoParUniteTemps *= facteur;
    }
    /**
     * Ajuste par delta (évite négatif).
    
     */
    public void ajusterConsoRelative(double delta) {
        consoParUniteTemps += delta;
        if (consoParUniteTemps < 0) {
            consoParUniteTemps = 0;
        }
    }
    /**
     * Ajoute consommation pour appareil.
    
     */
    public void ajouterConsommation(String appareil, double conso) {
        if (appareil == null || appareil.isEmpty() || conso < 0) {
            throw new IllegalArgumentException("Appareil valide et conso positive obligatoires.");
        }
        consommations.put(appareil, conso);
    }
    // Getters
    public String getNom() { return nom; }
    public double getConsommation() { return consoParUniteTemps; }
    public double getConsommationParUniteTemps() { return consoParUniteTemps; }
    /**
     * Retourne Map immuable des consommations via stream.
    
     */
    public Map<String, Double> getConsommations() {
        return consommations.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}