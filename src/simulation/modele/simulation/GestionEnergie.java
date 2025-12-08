package simulation.modele.simulation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulation.modele.source.SourceEnergie;

import simulation.modele.source.StockageEnergie;
// getsion de l enrgie 
public class GestionEnergie {
    private final List<RecordSimulation> historique;
    private final List<SourceEnergie> sources = new ArrayList<>();
    private final List<Consommateur> consommateurs = new ArrayList<>();
    private AlerteEnergie alerte;
  
    public GestionEnergie() {
        this.historique = new ArrayList<>();
    }
    /**
     * Ajoute une source.
     */
    public void ajouterSource(SourceEnergie s) throws EnergieException {
        if (s == null) {
            throw EnergieException.simulationInvalide("Source nulle");
        }
        sources.add(s);
    }
    /**
     * Ajoute un consommateur.
     .
     */
    
    public void ajouterConsommateur(Consommateur c) throws EnergieException {
        if (c == null) {
            throw EnergieException.simulationInvalide("Consommateur nul");
        }
        consommateurs.add(c);
    }
    /**
     * Définit l'alerte (version simple avec seuil conso, production par défaut 50.0).
    
     */
 
    /**
     * Définit l'alerte complète.
    
     */
    public void definirAlerte(AlerteEnergie a) {
        this.alerte = a;
    }
        // Méthode pour définir une alerte avec un objet AlerteEnergie
   
    
    // Méthode utilitaire pour créer une alerte avec deux doubles
    public void definirAlerte(double seuilConsommation, double seuilProduction) {
        this.alerte = new AlerteEnergie(seuilConsommation, seuilProduction);
    }

    /**
     * Production totale.
     
     */
    public double productionTotale() {
        Simulation sim = new Simulation(sources, consommateurs);
        return sim.getProduc();
    }
    /**
     * Consommation totale.
     * @return Somme consommation.
     */
    public double consommationTotale() {
        Simulation sim = new Simulation(sources, consommateurs);
        return sim.getConsommationTotale();
    }
    /**
     * Vérifie alerte.
     * @return True si alerte.
     */
    public boolean verifierAlerte() {
        if (alerte == null) {
            return false;
        }
        Simulation sim = new Simulation(sources, consommateurs);
        return alerte.verifierSeuils(sim);
    }
    /**
     * Consommation par consommateur.
     * @return Map nom -> conso.
     */
    public Map<String, Double> consommationParConsommateur() {
        return consommateurs.stream()
                .collect(Collectors.toMap(Consommateur::getNom, Consommateur::getConsommation));
    }
    /**
     * Simule une unité de temps.
     * @throws EnergieException Si erreur.
     */
    public void simulerUniteTemps() throws EnergieException {
        Simulation sim = new Simulation(sources, consommateurs);
        sim.simulerUniteTemps();
        ajouterSimulation(sim);
    }
    /**
     * État actuel.
     * @return String état.
     */
    public String getEtat() {
        Simulation sim = new Simulation(sources, consommateurs);
        return sim.getEtatString();
    }
    /**
     * Gère le stockage (exemple basique).
     * @throws EnergieException Si erreur.
     */
    public void gererStockage() throws EnergieException {
        sources.stream()
                .filter(StockageEnergie.class::isInstance)
                .map(s -> (StockageEnergie) s)
                .forEach(b -> {
            try {
                b.charger(10.0); // Correction minimale, assume implémentée
            } catch (EnergieException ex) {
                Logger.getLogger(GestionEnergie.class.getName()).log(Level.SEVERE, null, ex);
            }
                });
    }
    /**
     * Sources.
     * @return Liste sources (copie).
     */
    public List<SourceEnergie> getSources() {
        return new ArrayList<>(sources);
    }
    /**
     * Calcul production totale (alias).
     * @return Production.
     */
    public double calculerProductionTotale() {
        return productionTotale();
    }
    /**
     * Calcul consommation totale (alias).
     * @return Consommation.
     */
    public double calculerConsommationTotale() {
        return consommationTotale();
    }
    /**
     * Ajoute une étape de simulation dans l’historique.
     * @param sim
     */
    public void ajouterSimulation(Simulation sim) {
        RecordSimulation record = new RecordSimulation(sim); // ✔️ utilise le constructeur délégué
        historique.add(record);
    }
    /**
     * Affiche tout l’historique sous forme propre.
     */
    public void afficherHistorique() {
        historique.forEach(System.out::println);
    }
    /**
     * Retourne l’historique complet.
     */
    public List<RecordSimulation> getHistorique() {
        return historique;
    }
    /**
     * Retourne la dernière simulation de la liste.
     * @return
     */
    public RecordSimulation getDerniereSimulation() {
        if (historique.isEmpty()) {
            return null;
        }
        return historique.get(historique.size() - 1);
    }
   public List<Consommateur> getConsommateurs() {
        return new ArrayList<>(consommateurs);  // 'consommateurs' est ta List<Consommateur> privée
    }
}