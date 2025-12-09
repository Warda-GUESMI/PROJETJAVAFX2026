package simulation.modele.simulation;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe finale pour gérer l'historique des simulations.
 * Utilise try-with-resources pour la gestion automatique des ressources.
 */
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
     * ✅ TRY-WITH-RESOURCES #1
     * Sauvegarde par batch - UTILISE TRY-WITH-RESOURCES
     */
    public void sauvegarderBatch() {
        List<RecordSimulation> aEcrire;
        synchronized (verrou) {
            if (records.isEmpty()) return;
            aEcrire = new ArrayList<>(records);
        }

        try {
            // Lire le contenu existant
            List<String> lignesExistantes = new ArrayList<>();
            if (Files.exists(fichierCsv)) {
                // ✅ TRY-WITH-RESOURCES pour lecture
                try (BufferedReader reader = Files.newBufferedReader(fichierCsv)) {
                    String ligne;
                    while ((ligne = reader.readLine()) != null) {
                        lignesExistantes.add(ligne);
                    }
                } // ⚡ Auto-close du reader
            }

            // ✅ TRY-WITH-RESOURCES pour écriture temporaire
            try (BufferedWriter writer = Files.newBufferedWriter(
                    fichierTemp, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                // En-tête
                if (lignesExistantes.isEmpty()) {
                    writer.write("Date/Heure,Temps (unités),Production (kWh),Consommation (kWh),Bilan (kWh)");
                    writer.newLine();
                } else {
                    // Réécrire lignes existantes
                    for (String ligne : lignesExistantes) {
                        writer.write(ligne);
                        writer.newLine();
                    }
                }

                // Nouvelles lignes
                for (RecordSimulation record : aEcrire) {
                    String ligne = String.format("%s,%d,%.2f,%.2f,%.2f",
                            LocalDateTime.now().format(DATE_FORMAT),
                            record.temps(),
                            record.production(),
                            record.consommation(),
                            record.production() - record.consommation()
                    );
                    writer.write(ligne);
                    writer.newLine();
                }

                writer.flush();
            } // ⚡ Auto-close du writer

            // Remplacement atomique
            Files.move(fichierTemp, fichierCsv, 
                      StandardCopyOption.REPLACE_EXISTING, 
                      StandardCopyOption.ATOMIC_MOVE);

            // Vider la liste APRÈS succès
            synchronized (verrou) {
                records.clear();
            }

            System.out.println("✅ Sauvegarde réussie : " + aEcrire.size() + " ligne(s)");

        } catch (IOException e) {
            System.err.println("❌ Erreur sauvegarde : " + e.getMessage());
        }
    }

    /**
     * ✅ TRY-WITH-RESOURCES #2
     * Charge l'historique depuis le fichier
     */
    public void chargerDepuisFichier() {
        synchronized (verrou) {
            if (!Files.exists(fichierCsv)) {
                System.out.println("Aucun fichier à charger.");
                return;
            }

            // ✅ TRY-WITH-RESOURCES pour lecture
            try (BufferedReader reader = Files.newBufferedReader(fichierCsv)) {
                records.clear();
                
                String ligne;
                boolean premiereLigne = true;
                
                while ((ligne = reader.readLine()) != null) {
                    if (premiereLigne) {
                        premiereLigne = false;
                        continue; // Sauter l'en-tête
                    }
                    
                    ligne = ligne.trim();
                    if (ligne.isEmpty()) continue;

                    String[] parts = ligne.split(",");
                    if (parts.length >= 5) {
                        try {
                            int temps = Integer.parseInt(parts[1].trim());
                            double prod = Double.parseDouble(parts[2].trim());
                            double conso = Double.parseDouble(parts[3].trim());
                            records.add(new RecordSimulation(temps, prod, conso));
                        } catch (NumberFormatException e) {
                            System.err.println("⚠️ Ligne ignorée : " + ligne);
                        }
                    }
                }

                System.out.println("✅ " + records.size() + " enregistrement(s) chargé(s)");

            } catch (IOException e) {
                System.err.println("❌ Erreur chargement : " + e.getMessage());
            } // ⚡ Auto-close du reader
        }
    }

    /**
     * ✅ TRY-WITH-RESOURCES #3
     * Vide l'historique et le fichier
     */
    public void vider() {
        synchronized (verrou) {
            records.clear();
        }
        
        // ✅ TRY-WITH-RESOURCES pour écriture
        try (BufferedWriter writer = Files.newBufferedWriter(
                fichierCsv,
                StandardOpenOption.CREATE, 
                StandardOpenOption.TRUNCATE_EXISTING)) {
            
            writer.write("Date/Heure,Temps (unités),Production (kWh),Consommation (kWh),Bilan (kWh)");
            writer.newLine();
            writer.flush();
            
            System.out.println("✅ Historique vidé.");
            
        } catch (IOException e) {
            System.err.println("❌ Erreur vidage : " + e.getMessage());
        } // ⚡ Auto-close du writer
    }

    /**
     * ✅ TRY-WITH-RESOURCES #4
     * Exporte l'historique dans un fichier texte
     */
    public void exporterVersTexte(Path fichierDestination) throws IOException {
        synchronized (verrou) {
            // ✅ TRY-WITH-RESOURCES pour export
            try (BufferedWriter writer = Files.newBufferedWriter(
                    fichierDestination,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                
                writer.write("═══════════════════════════════════════════════════════");
                writer.newLine();
                writer.write("           HISTORIQUE DES SIMULATIONS");
                writer.newLine();
                writer.write("═══════════════════════════════════════════════════════");
                writer.newLine();
                writer.newLine();
                
                writer.write(String.format("Nombre total de simulations : %d", records.size()));
                writer.newLine();
                writer.newLine();
                
                for (RecordSimulation record : records) {
                    writer.write(String.format(
                        "Temps: %d | Prod: %.2f kWh | Conso: %.2f kWh | Bilan: %.2f kWh",
                        record.temps(),
                        record.production(),
                        record.consommation(),
                        record.production() - record.consommation()
                    ));
                    writer.newLine();
                }
                
                writer.write("═══════════════════════════════════════════════════════");
                writer.newLine();
                writer.flush();
                
                System.out.println("✅ Export réussi vers : " + fichierDestination);
                
            } // ⚡ Auto-close du writer
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

    /**
     * ✅ TRY-WITH-RESOURCES #5
     * Initialise le fichier avec try-with-resources
     */
    private void initialiserFichier() {
        try {
            if (Files.notExists(fichierCsv)) {
                // ✅ TRY-WITH-RESOURCES pour création
                try (BufferedWriter writer = Files.newBufferedWriter(
                        fichierCsv,
                        StandardOpenOption.CREATE)) {
                    
                    writer.write("Date/Heure,Temps (unités),Production (kWh),Consommation (kWh),Bilan (kWh)");
                    writer.newLine();
                    writer.flush();
                    
                    System.out.println("✅ Fichier créé : " + fichierCsv.getFileName());
                } // ⚡ Auto-close du writer
            } else {
                System.out.println("✅ Fichier existant : " + fichierCsv.getFileName());
                chargerDepuisFichier();
            }
        } catch (IOException e) {
            System.err.println("❌ Impossible de créer le fichier : " + e.getMessage());
        }
    }
}