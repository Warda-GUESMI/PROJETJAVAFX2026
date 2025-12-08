package simulation.modele.simulation;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Historique {

    private final List<RecordSimulation> records = new ArrayList<>();
    private final Object verrou = new Object();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Path fichierCsv;
    private final Path fichierTemp;

    public Historique() {
        String nom = "historique_" + LocalDateTime.now().format(FILE_DATE_FORMAT) + ".csv";
        this.fichierCsv = Paths.get(nom);
        this.fichierTemp = Paths.get(nom + ".tmp");
        initialiserFichier();
    }

    public void ajouter(RecordSimulation r) {
        if (r == null) return;
        synchronized (verrou) {
            records.add(r);
            System.out.println("✅ Record ajouté en mémoire : Temps=" + r.temps() + 
                             " | Prod=" + String.format("%.2f", r.production()) + 
                             " | Conso=" + String.format("%.2f", r.consommation()));
        }
    }

    /**
     * Sauvegarde par batch - MÉTHODE PRINCIPALE D'ÉCRITURE
     * À appeler périodiquement (ex: toutes les 10 simulations ou via timer)
     */
    public void sauvegarderBatch() {
        List<RecordSimulation> aEcrire;
        synchronized (verrou) {
            if (records.isEmpty()) return;
            aEcrire = new ArrayList<>(records);
        }

        try {
            // Lire le contenu existant du fichier principal (si existe)
            List<String> lignesExistantes = new ArrayList<>();
            if (Files.exists(fichierCsv)) {
                lignesExistantes = Files.readAllLines(fichierCsv);
            }

            // Écrire dans le fichier temporaire : en-tête + anciennes lignes + nouvelles
            try (BufferedWriter writer = Files.newBufferedWriter(
                    fichierTemp, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // En-tête
                if (lignesExistantes.isEmpty()) {
                    writer.write("Date/Heure,Temps (unités),Production (kWh),Consommation (kWh),Bilan (kWh)");
                    writer.newLine();
                } else {
                    // Réécrire les lignes existantes (sauf si on veut append uniquement)
                    for (String ligne : lignesExistantes) {
                        writer.write(ligne);
                        writer.newLine();
                    }
                }

                // Nouvelles lignes
                for (RecordSimulation r : aEcrire) {
                    String ligne = String.format("%s,%d,%.2f,%.2f,%.2f",
                            LocalDateTime.now().format(DATE_FORMAT),
                            r.temps(),
                            r.production(),
                            r.consommation(),
                            r.production() - r.consommation()
                    );
                    writer.write(ligne);
                    writer.newLine();
                }

                writer.flush();
            }

            // Remplacement atomique
            Files.move(fichierTemp, fichierCsv, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

            // Vider la liste en mémoire APRÈS succès
            synchronized (verrou) {
                records.clear();
            }

            System.out.println("✅ Sauvegarde réussie : " + aEcrire.size() + " ligne(s) écrites dans " + fichierCsv.getFileName());

        } catch (IOException e) {
            System.err.println("❌ Erreur sauvegarde batch : " + e.getMessage());
            // On garde les données en mémoire pour réessayer plus tard
        }
    }

    /**
     * Charge l'historique depuis le fichier CSV (au démarrage)
     */
    public void chargerDepuisFichier() {
        synchronized (verrou) {
            if (!Files.exists(fichierCsv)) {
                System.out.println("Aucun fichier à charger.");
                return;
            }

            try {
                List<String> lignes = Files.readAllLines(fichierCsv);
                records.clear();

                for (int i = 1; i < lignes.size(); i++) { // Sauter l'en-tête
                    String ligne = lignes.get(i).trim();
                    if (ligne.isEmpty()) continue;

                    String[] parts = ligne.split(",");
                    if (parts.length >= 5) {
                        try {
                            int temps = Integer.parseInt(parts[1].trim());
                            double prod = Double.parseDouble(parts[2].trim());
                            double conso = Double.parseDouble(parts[3].trim());
                            records.add(new RecordSimulation(temps, prod, conso));
                        } catch (NumberFormatException e) {
                            System.err.println("⚠️ Ligne ignorée (format invalide) : " + ligne);
                        }
                    }
                }

                System.out.println("✅ " + records.size() + " enregistrement(s) chargé(s) depuis " + fichierCsv.getFileName());

            } catch (IOException e) {
                System.err.println("❌ Erreur chargement fichier : " + e.getMessage());
            }
        }
    }

    public void vider() {
        synchronized (verrou) {
            records.clear();
        }
        try {
            Files.write(fichierCsv,
                "Date/Heure,Temps (unités),Production (kWh),Consommation (kWh),Bilan (kWh)\n".getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("✅ Historique vidé (fichier et mémoire).");
        } catch (IOException e) {
            System.err.println("❌ Erreur vidage fichier : " + e.getMessage());
        }
    }

    public List<RecordSimulation> getListe() {
        synchronized (verrou) {
            return Collections.unmodifiableList(new ArrayList<>(records));
        }
    }

    public int taille() {
        synchronized (verrou) {
            return records.size();
        }
    }

    public boolean estVide() {
        synchronized (verrou) {
            return records.isEmpty();
        }
    }

    public String getNomFichier() {
        return fichierCsv.toString();
    }

    private void initialiserFichier() {
        try {
            if (Files.notExists(fichierCsv)) {
                Files.write(fichierCsv,
                    "Date/Heure,Temps (unités),Production (kWh),Consommation (kWh),Bilan (kWh)\n".getBytes());
                System.out.println("✅ Fichier créé : " + fichierCsv.getFileName());
            } else {
                System.out.println("✅ Fichier existant : " + fichierCsv.getFileName());
                chargerDepuisFichier(); // Charger automatiquement au démarrage
            }
        } catch (IOException e) {
            System.err.println("❌ Impossible de créer le fichier historique : " + e.getMessage());
        }
    }
}