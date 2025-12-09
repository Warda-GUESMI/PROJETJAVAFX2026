package simulation.modele.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import simulation.modele.source.SourceEnergie;
import simulation.modele.source.StockageEnergie;

public class GestionEnergie {

    private final List<RecordSimulation> historique = new ArrayList<>();
    private final List<SourceEnergie> sources = new ArrayList<>();
    private final List<Consommateur> consommateurs = new ArrayList<>();

    private AlerteEnergie alerte;
    private int tempsSimule = 0;   // ✔ temps réel de la simulation

    public GestionEnergie() {}

    // -------------------------------
    // AJOUT DES ÉLÉMENTS DE SIMULATION
    // -------------------------------

    public void ajouterSource(SourceEnergie s) throws EnergieException {
        if (s == null) throw EnergieException.simulationInvalide("Source nulle");
        sources.add(s);
    }

    public void ajouterConsommateur(Consommateur c) throws EnergieException {
        if (c == null) throw EnergieException.simulationInvalide("Consommateur nul");
        consommateurs.add(c);
    }

    // -------------------------------
    // ALERTES
    // -------------------------------

    public void definirAlerte(AlerteEnergie a) {
        this.alerte = a;
    }

    public void definirAlerte(double seuilConsommation, double seuilProduction) {
        this.alerte = new AlerteEnergie(seuilConsommation, seuilProduction);
    }

    // -------------------------------
    // MESURES DE CONSOMMATION/PRODUCTION
    // -------------------------------

    public double productionTotale() {
        Simulation sim = new Simulation(sources, consommateurs);
        return sim.getProduc();
    }

    public double consommationTotale() {
        Simulation sim = new Simulation(sources, consommateurs);
        return sim.getConsommationTotale();
    }

    public boolean verifierAlerte() {
        if (alerte == null) return false;
        Simulation sim = new Simulation(sources, consommateurs);
        return alerte.verifierSeuils(sim);
    }

    public Map<String, Double> consommationParConsommateur() {
        return consommateurs.stream()
                .collect(Collectors.toMap(
                        Consommateur::getNom,
                        Consommateur::getConsommation
                ));
    }

    // -------------------------------
    // SIMULATION
    // -------------------------------

    public void simulerUniteTemps() throws EnergieException {

        tempsSimule++;   // ✔ on avance réellement dans le temps

        double prod = productionTotale();
        double conso = consommationTotale();

        RecordSimulation r = new RecordSimulation(tempsSimule, conso, prod);
        historique.add(r);

        System.out.printf("⏱ Temps %d | Production %.2f kWh | Consommation %.2f kWh%n",
                tempsSimule, prod, conso);
    }

    // -------------------------------
    // STOCKAGE D’ÉNERGIE
    // -------------------------------

    public void gererStockage() {
        sources.stream()
                .filter(StockageEnergie.class::isInstance)
                .map(s -> (StockageEnergie) s)
                .forEach(b -> {
                    try {
                        b.charger(10.0);  // exemple simple
                    } catch (EnergieException ex) {
                        Logger.getLogger(GestionEnergie.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }

    // -------------------------------
    // HISTORIQUE
    // -------------------------------

    public void ajouterSimulation(Simulation sim) {
        historique.add(new RecordSimulation(sim));
    }

    public void afficherHistorique() {
        historique.forEach(System.out::println);
    }

    public List<RecordSimulation> getHistorique() {
        return new ArrayList<>(historique);
    }

    public RecordSimulation getDerniereSimulation() {
        return historique.isEmpty() ? null : historique.get(historique.size() - 1);
    }

    // -------------------------------
    // GETTERS
    // -------------------------------

    public List<SourceEnergie> getSources() {
        return new ArrayList<>(sources);
    }

    public List<Consommateur> getConsommateurs() {
        return new ArrayList<>(consommateurs);
    }

    public String getEtat() {
        Simulation sim = new Simulation(sources, consommateurs);
        return sim.getEtatString();
    }
}


