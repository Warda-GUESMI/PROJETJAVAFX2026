package simulation.modele.simulation;

import simulation.modele.source.SourceEnergie;
import java.util.List;

/**
 * Classe Simulation pour gérer le temps et calculs globaux.
 
 */
public class Simulation {
    private final List<SourceEnergie> listeSources;
    private final List<Consommateur> listeConsommateurs;
    private int tempsSimule;

    
    public Simulation(List<SourceEnergie> sources, List<Consommateur> consommateurs) {
        this.listeSources = List.copyOf(sources != null ? sources : List.of());
        this.listeConsommateurs = List.copyOf(consommateurs != null ? consommateurs : List.of());
        this.tempsSimule = 0;
    }

    /**
     * Simule une unité de temps.
     */
    public void simulerUniteTemps() {
        tempsSimule++;
       
    }

    /**
     * Consommation totale via stream.
     * @return Somme.
     */
    public double getConsommationTotale() {
        return listeConsommateurs.stream()
                .mapToDouble(Consommateur::getConsommation)
                .sum();
    }

    /**
     * Production totale via stream.
     * 
     */
    public double getProduc() {
        return listeSources.stream()
                .mapToDouble(SourceEnergie::getProduction)
                .sum();
    }

    /**
     * État string.
     * @return Chaîne d'état.
     */
    public String getEtatString() {
        return String.format("Temps : %d | Conso : %.2f | Prod : %.2f", tempsSimule, getConsommationTotale(), getProduc());
    }

    public int getTempsSimule() { return tempsSimule; }
}