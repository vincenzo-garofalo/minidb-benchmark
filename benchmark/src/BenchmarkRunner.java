import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/// Runner per il benchmarking (misurazione e confronto delle performance) dei database implementati in Rust, Python, Java.
public final class BenchmarkRunner {
    /* COSTANTI E CONFIGURAZIONE */
    // ricava la directory corrente da cui viene eseguito il programma (la root del progetto);
        // costruisce i path alle cartelle usate: workload in input, risultati in output.
    private static final Path ROOT_DIR = Path.of("").toAbsolutePath().normalize();
    private static final Path BENCHMARK_DIR = ROOT_DIR.resolve("benchmark");
    private static final Path WORKLOAD_DIR = BENCHMARK_DIR.resolve("workloads");
    private static final Path RESULTS_DIR = BENCHMARK_DIR.resolve("results");
    private static final Path THROUGHPUT_RESULTS_FILE = RESULTS_DIR.resolve("throughput_results.csv");
    private static final Path MEMORY_RESULTS_FILE = RESULTS_DIR.resolve("memory_results.csv");

    private static final Path PYTHON_DIR = ROOT_DIR.resolve("python-minidb").resolve("src");
    private static final Path JAVA_DIR = ROOT_DIR.resolve("java-minidb");
    private static final Path JAVA_CLASSES_DIR = JAVA_DIR.resolve("target").resolve("benchmark-classes");
    private static final Path RUST_DIR = ROOT_DIR.resolve("rust-minidb");

    // valori di default: 5 run per workload, campionamento memoria ogni 500 ms nella modalità memory.
    private static final int DEFAULT_RUNS = 5;
    private static final int DEFAULT_MEMORY_SAMPLE_MS = 500;
    private static final int MEMORY_STARTUP_DELAY_MS = 100; // millisecondi di attesa prima di iniziare il campionamento della memoria
    private static final int MEMORY_SYNC_SAMPLE_EVERY_COMMANDS = 10_000;    // campionamento sincrono della memoria ogni 10.000 comandi

    // mappa nome-file -> numero di comandi da generare per quel workload.
    private static final Map<String, Integer> WORKLOAD_SIZES = Map.of(
            "small_mixed.txt", 1_000,
            "medium_mixed.txt", 10_000,
            "large_mixed.txt", 100_000,
            "small_read_heavy.txt", 1_000,
            "medium_read_heavy.txt", 10_000,
            "large_read_heavy.txt", 100_000,
            "small_write_heavy.txt", 1_000,
            "medium_write_heavy.txt", 10_000,
            "large_write_heavy.txt", 100_000
    );

    // costruttore di default privato per evitare istanze non necessarie.
    private BenchmarkRunner() {
    }



    /* MAIN */
    /// Punto di ingresso del programma: prepara cartelle, compila le implementazioni e lancia tutti i benchmark richiesti.
    public static void main(String[] args) throws Exception {
        // legge le opzioni passate da terminale, ad esempio --runs o --workload.
        Options options = Options.parse(args);

        // crea le cartelle necessarie se non esistono ancora.
        Files.createDirectories(WORKLOAD_DIR);
        Files.createDirectories(RESULTS_DIR);

        // genera workload di esempio solo se richiesto e se i file mancano o sono vuoti.
        if (options.generateWorkloads()) {
            generateMissingOrEmptyWorkloads(options.generateOnly());
        }
        if (options.generateOnly()) {
            System.out.println("Workload generation completed.");
            return;
        }

        // compila le versioni Java e Rust prima di misurarle, così il benchmark usa eseguibili aggiornati.
        buildJava();
        buildRust();

        // descrive come avviare ciascuna implementazione: linguaggio, comando e directory di lavoro.
        List<Implementation> implementations = List.of(
                new Implementation("python", List.of(pythonCommand(), "main.py"), PYTHON_DIR),
                new Implementation("java", List.of("java", "-cp", JAVA_CLASSES_DIR.toString(), "Main"), JAVA_DIR),
                new Implementation("rust", List.of(rustExecutable().toString()), RUST_DIR)
        );

        // carica i workload specifici se indicati, altrimenti carica tutti i file .txt disponibili.
        List<Workload> workloads = loadWorkloads(options.workloads());
        if (workloads.isEmpty()) {
            System.err.println("No workloads found. Create files in benchmark/workloads or use --generate-workloads.");
            System.exit(1);
        }

        // esegue il benchmark in modalità memoria, 1 run per ogni workload e implementazione.
        if (options.mode() == Mode.MEMORY) {
            List<MemoryResult> memoryResults = new ArrayList<>();
            for (Workload workload : workloads) {
                for (Implementation implementation : implementations) {
                    System.out.printf(
                            "Measuring memory for %s on %s (%d commands)%n",
                            implementation.language(),
                            workload.path().getFileName(),
                            workload.commands().size()
                    );
                    memoryResults.add(runSingleMemoryBenchmark(
                            implementation,
                            workload,
                            DEFAULT_MEMORY_SAMPLE_MS
                    ));
                }
            }
            printMemoryResultsSummary(memoryResults);
            writeMemoryResults(memoryResults);
            System.out.println("Memory results written to " + MEMORY_RESULTS_FILE);
            return;
        }

        // esegue il benchmark in modalità throughput (modalità di default), n run per ogni workload e implementazione.
        List<ThroughputResult> results = new ArrayList<>();
        for (Workload workload : workloads) {
            for (Implementation implementation : implementations) {
                for (int run = 1; run <= options.runs(); run++) {
                    System.out.printf(
                            "Running %s on %s (%d commands), run %d/%d%n",
                            implementation.language(),
                            workload.path().getFileName(),
                            workload.commands().size(),
                            run,
                            options.runs()
                    );
                    results.add(runSingleThroughputBenchmark(
                            implementation,
                            workload,
                            run
                    ));
                }
            }
        }

        // mostra i risultati in forma leggibile e li salva anche nel CSV.
        printThroughputResultsSummary(results);
        writeThroughputResults(results);
        System.out.println("Throughput results written to " + THROUGHPUT_RESULTS_FILE);
    }



    /* METODI */
    /// Crea i workload standard se non esistono ancora o se sono file vuoti.
    private static void generateMissingOrEmptyWorkloads(boolean overwriteExisting) throws IOException {
        for (Map.Entry<String, Integer> entry : WORKLOAD_SIZES.entrySet()) {
            Path path = WORKLOAD_DIR.resolve(entry.getKey());
            if (!overwriteExisting && Files.exists(path) && Files.size(path) > 0) {
                continue;
            }
            List<String> commands = generateCommands(entry.getKey(), entry.getValue());
            Files.write(path, commands, StandardCharsets.UTF_8);
            System.out.printf("Generated %s with %d commands%n", path, entry.getValue());
        }
    }

    /// Metodo helper che sceglie il profilo di workload a partire dal nome del file.
    private static List<String> generateCommands(String fileName, int commandCount) {
        if (fileName.contains("read_heavy")) {
            return generateReadHeavyCommands(commandCount);
        }
        if (fileName.contains("write_heavy")) {
            return generateWriteHeavyCommands(commandCount);
        }
        return generateMixedCommands(commandCount);
    }

    /// Metodo helper che costruisce una lista di comandi misti per simulare operazioni tipiche sul database.
    private static List<String> generateMixedCommands(int commandCount) {
        List<String> commands = new ArrayList<>(commandCount);
        int keyCount = Math.max(100, Math.min(commandCount / 10, 10_000)); // setta il numero di chiavi utilizzate per i comandi, da 100 a 10_000

        for (int index = 0; index < commandCount; index++) {
            String key = "key" + (index % keyCount);
            int operation = index % 5;
            switch (operation) { // 5 possibili comandi: SET, GET, EXISTS, INCR, SET temp
                case 0 -> commands.add("SET " + key + " " + index);
                case 1 -> commands.add("GET " + key);
                case 2 -> commands.add("EXISTS " + key);
                case 3 -> commands.add("INCR counter");
                default -> commands.add("SET temp" + index + " " + index);
            }
        }

        return commands;
    }

    /// Metodo helper che costruisce un workload orientato alle letture: circa 80% GET, 10% SET, 10% EXISTS.
    private static List<String> generateReadHeavyCommands(int commandCount) {
        List<String> commands = new ArrayList<>(commandCount);
        int keyCount = Math.max(100, Math.min(commandCount / 10, 10_000));

        for (int index = 0; index < commandCount; index++) {
            String key = "key" + ((index / 10) % keyCount); // la chiave cambia ogni 10 comandi (prima SET, poi 8 GET sulla stessa chiave, poi EXISTS, poi la chiave cambia)
            int operation = index % 10; // ogni 10 comandi: 8 GET, 1 SET, 1 EXISTS
            if (operation == 0) {
                commands.add("SET " + key + " " + index);
            } else if (operation == 9) {
                commands.add("EXISTS " + key);
            } else {
                commands.add("GET " + key);
            }
        }

        return commands;
    }

    /// Metodo helper che costruisce un workload orientato alle scritture: circa 80% SET, 10% GET, 10% EXISTS.
    private static List<String> generateWriteHeavyCommands(int commandCount) {
        List<String> commands = new ArrayList<>(commandCount);
        int keyCount = Math.max(100, Math.min(commandCount / 10, 10_000));

        for (int index = 0; index < commandCount; index++) {
            String key = "key" + (index % keyCount);
            int operation = index % 10; // ogni 10 comandi: 8 SET, 1 GET, 1 EXISTS
            if (operation < 8) {
                commands.add("SET " + key + " " + index);
            } else if (operation == 8) {
                commands.add("GET " + key);
            } else {
                commands.add("EXISTS " + key);
            }
        }

        return commands;
    }

    /// Compila tutti i file sorgente Java dentro java-minidb/src nella cartella target usata dal benchmark.
    private static void buildJava() throws IOException, InterruptedException {
        Files.createDirectories(JAVA_CLASSES_DIR);

        List<String> sourceFiles;
        try (Stream<Path> stream = Files.list(JAVA_DIR.resolve("src"))) {
            sourceFiles = stream
                    .filter(path -> path.toString().endsWith(".java"))
                    .sorted()
                    .map(Path::toString)
                    .toList();
        }

        if (sourceFiles.isEmpty()) {
            throw new IllegalStateException("No Java source files found.");
        }

        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-d");
        command.add(JAVA_CLASSES_DIR.toString());
        command.addAll(sourceFiles);
        runCommand(command, JAVA_DIR);
    }

    /// Compila il progetto Rust in modalità release, più adatta a misurare le performance reali.
    private static void buildRust() throws IOException, InterruptedException {
        runCommand(List.of("cargo", "build", "--release"), RUST_DIR);
    }

    /// Metodo helper che esegue un comando esterno e, se fallisce, include stdout e stderr nel messaggio di errore.
    private static void runCommand(List<String> command, Path cwd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command); // serve per eseguire comandi esterni
        builder.directory(cwd.toFile()); // imposta la directory di lavoro per il processo
        Process process = builder.start(); // avvia il processo

        String stdout = readAll(process.inputReader(StandardCharsets.UTF_8)); // legge tutto lo standard output del processo
        String stderr = readAll(process.errorReader(StandardCharsets.UTF_8)); // legge tutto lo standard error del processo
        int exitCode = process.waitFor(); // attende il processo fino a quando non viene terminato

        if (exitCode != 0) { // quando il processo termina restituisce un codice pari a 0 per indicare successo, diverso da 0 per errore
            throw new IllegalStateException(
                    "Command failed: " + String.join(" ", command)
                            + "\nstdout:\n" + stdout
                            + "\nstderr:\n" + stderr
            );
        }
    }

    /// Sceglie il comando Python corretto in base al sistema operativo.
    private static String pythonCommand() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
                ? "python"      // se il sistema operativo è Windows
                : "python3";    // altrimenti usa Python 3 (Linux/macOS)
    }

    /// Restituisce il path dell'eseguibile Rust, aggiungendo .exe quando il programma gira su Windows.
    private static Path rustExecutable() {
        String extension = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
                ? ".exe"        // se il sistema operativo è Windows
                : "";           // altrimenti usa il file binario normale (Linux/macOS)
        return RUST_DIR.resolve("target").resolve("release").resolve("rust-minidb" + extension);
    }

    /// Legge i workload da disco e scarta file mancanti, vuoti o righe vuote.
    private static List<Workload> loadWorkloads(List<String> selectedWorkloads) throws IOException {
        List<Path> workloadPaths;
        if (!selectedWorkloads.isEmpty()) {
            workloadPaths = selectedWorkloads.stream()
                    .map(WORKLOAD_DIR::resolve)
                    .toList();
        } else {
            try (Stream<Path> stream = Files.list(WORKLOAD_DIR)) {
                workloadPaths = stream
                        .filter(path -> path.toString().endsWith(".txt"))
                        .sorted()
                        .toList();
            }
        }

        List<Workload> workloads = new ArrayList<>();
        for (Path path : workloadPaths) {
            if (!Files.exists(path) || Files.size(path) == 0) {
                continue;
            }
            List<String> commands = Files.readAllLines(path, StandardCharsets.UTF_8)
                    .stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();
            if (!commands.isEmpty()) {
                workloads.add(new Workload(path, commands));
            }
        }

        return workloads;
    }

    /// Avvia una singola implementazione, invia tutti i comandi del workload e misura tempi, throughput e latenze.
    private static ThroughputResult runSingleThroughputBenchmark(
            Implementation implementation,
            Workload workload,
            int run
    ) throws IOException, InterruptedException {
        // avvia il processo esterno per eseguire il benchmark di una specifica implementazione.
        ProcessBuilder builder = new ProcessBuilder(implementation.command());
        builder.directory(implementation.cwd().toFile());
        Process process = builder.start();

        List<Double> latenciesMs = new ArrayList<>(workload.commands().size()); // misura le latenze dei comandi

        // stdin invia comandi al database, stdout riceve una risposta per ogni comando eseguito.
        try (
                BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
        ) {
            long startNanos = System.nanoTime(); // salva il tempo iniziale del benchmark in nanosecondi

            for (int index = 0; index < workload.commands().size(); index++) {
                String command = workload.commands().get(index);

                // misura la latenza del singolo comando: da quando viene scritto a quando arriva la risposta.
                long operationStartNanos = System.nanoTime();   // tempo appena prima di inviare il comando
                stdin.write(command);   // scrive il comando nello standard input del processo del database
                stdin.newLine();        // aggiunge un carattere di terminazione di riga
                stdin.flush();          // forza l'invio immediato del comando

                String response = stdout.readLine();    // legge la risposta dallo standard output del processo del database
                long operationEndNanos = System.nanoTime();     // tempo appena dopo aver ricevuto la risposta
                if (response == null) { // il processo del database ha terminato prima di ricevere la risposta
                    String stderr = readAll(process.errorReader(StandardCharsets.UTF_8));
                    throw new IllegalStateException(
                            implementation.language() + " stopped before completing " + workload.path().getFileName()
                                    + "\nstderr:\n" + stderr
                    );
                }

                latenciesMs.add((operationEndNanos - operationStartNanos) / 1_000_000.0);   // calcola la latenza del comando in millisecondi (converte da nanosecondi)
            }

            // chiude in modo ordinato il processo del database.
            stdin.write("EXIT");
            stdin.newLine();
            stdin.flush();

            int exitCode = process.waitFor();
            long endNanos = System.nanoTime();  // salva il tempo finale del benchmark in nanosecondi

            if (exitCode != 0) {
                String stderr = readAll(process.errorReader(StandardCharsets.UTF_8));
                throw new IllegalStateException(
                        implementation.language() + " exited with code " + exitCode
                                + "\nstderr:\n" + stderr
                );
            }

            double timeSeconds = Duration.ofNanos(endNanos - startNanos).toNanos() / 1_000_000_000.0;   // calcola la durata totale del benchmark in secondi
            int commandCount = workload.commands().size();
            double throughput = commandCount / timeSeconds;

            return new ThroughputResult(
                    implementation.language(),
                    stripExtension(workload.path().getFileName().toString()),
                    commandCount,
                    run,
                    timeSeconds,
                    throughput,
                    percentile(latenciesMs, 50),
                    percentile(latenciesMs, 95),
                    percentile(latenciesMs, 99)
            );
        }
    }

    /// Avvia una singola implementazione e misura solo il picco RSS in una run separata dal throughput.
    private static MemoryResult runSingleMemoryBenchmark(
            Implementation implementation,
            Workload workload,
            int memorySampleMs
    ) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(implementation.command());
        builder.directory(implementation.cwd().toFile());
        Process process = builder.start();

        Thread.sleep(MEMORY_STARTUP_DELAY_MS);  // delay per permettere al processo di avviarsi completamente prima di iniziare la misurazione della memoria
        OptionalDouble rssStartMb = readRssMb(process.pid());
        AtomicReference<OptionalDouble> rssPeakMb = new AtomicReference<>(rssStartMb);  // variabile condivisa tra thread, per questo Atomic, per memorizzare il picco RSS
        AtomicBoolean sampling = new AtomicBoolean(true);   // flag condiviso tra thread, per questo Atomic, che dice al thread di campionamento se deve continuare a misurare

        Thread sampler = new Thread(() -> { // thread separato che si occupa di campionare la memoria in background, periodicamente
            while (sampling.get() && process.isAlive()) {
                rssPeakMb.updateAndGet(current -> maxOptional(current, readRssMb(process.pid())));  // legge il picco RSS attuale e lo aggiorna se necessario
                try {
                    Thread.sleep(memorySampleMs);
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        sampler.setDaemon(true);    // imposta il thread come daemon, per farlo terminare automaticamente quando il processo principale termina
        sampler.start();

        try (
                BufferedWriter stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
        ) {
            for (int index = 0; index < workload.commands().size(); index++) {
                String command = workload.commands().get(index);
                stdin.write(command);   // scrive il comando nello standard input del processo del database
                stdin.newLine();        // aggiunge un carattere di terminazione di riga
                stdin.flush();          // forza l'invio immediato del comando

                String response = stdout.readLine();    // legge la risposta dallo standard output del processo del database
                if (response == null) {
                    String stderr = readAll(process.errorReader(StandardCharsets.UTF_8));
                    throw new IllegalStateException(
                            implementation.language() + " stopped before completing " + workload.path().getFileName()
                            + "\nstderr:\n" + stderr
                    );
                }

                if ((index + 1) % MEMORY_SYNC_SAMPLE_EVERY_COMMANDS == 0) { // campionamento sincrono ogni MEMORY_SYNC_SAMPLE_EVERY_COMMANDS comandi
                    rssPeakMb.updateAndGet(current -> maxOptional(current, readRssMb(process.pid())));
                }
            }

            rssPeakMb.updateAndGet(current -> maxOptional(current, readRssMb(process.pid())));  // misurazione prima di chiudere il database

            // chiude in modo ordinato il processo del database.
            stdin.write("EXIT");
            stdin.newLine();
            stdin.flush();

            int exitCode = process.waitFor();
            sampling.set(false);    //dice al thread di campionamento di fermarsi
            sampler.interrupt();    // interrompe il thread di campionamento, se bloccato
            sampler.join();         // attende la terminazione del thread di campionamento
            rssPeakMb.updateAndGet(current -> maxOptional(current, readRssMb(process.pid())));  // ultima misurazione dopo aver chiuso il database

            if (exitCode != 0) {
                String stderr = readAll(process.errorReader(StandardCharsets.UTF_8));
                throw new IllegalStateException(
                        implementation.language() + " exited with code " + exitCode
                                + "\nstderr:\n" + stderr
                );
            }

            return new MemoryResult(
                    implementation.language(),
                    stripExtension(workload.path().getFileName().toString()),
                    workload.commands().size(),
                    rssStartMb,
                    rssPeakMb.get()
            );
        } finally {
            sampling.set(false);
            sampler.interrupt();
            if (process.isAlive()) {
                process.destroyForcibly();  // termina il processo del database con un force kill
            }
        }
    }

    /// Metodo helper che legge la memoria RSS del processo scegliendo il metodo adatto al sistema operativo.
    private static OptionalDouble readRssMb(long pid) {
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")) {
            return readWindowsRssMb(pid);   // se il sistema operativo è Windows
        }
        return readUnixRssMb(pid);          // se il sistema operativo è Unix-like
    }

    /// Metodo helper che su Windows usa PowerShell/Get-Process per leggere il WorkingSet64 e convertirlo in MB.
    private static OptionalDouble readWindowsRssMb(long pid) {
        try {
            Process process = new ProcessBuilder(
                    "powershell",
                    "-NoProfile",
                    "-Command",
                    "(Get-Process -Id " + pid + ").WorkingSet64"    // comando per ottenere la memoria residente del processo
            ).start();

            String stdout = readAll(process.inputReader(StandardCharsets.UTF_8)).trim();
            process.waitFor();
            if (stdout.isEmpty()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(Long.parseLong(stdout) / (1024.0 * 1024.0));   // converte la memoria residente in MB (WorkingSet64 è in byte)
        } catch (IOException | InterruptedException | NumberFormatException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // ripristina lo stato di interruzione
            }
            return OptionalDouble.empty();
        }
    }

    /// Metodo helper che su Unix/Linux prova prima /proc/<pid>/status, poi il comando ps come alternativa.
    private static OptionalDouble readUnixRssMb(long pid) {
        Path statusPath = Path.of("/proc", Long.toString(pid), "status");
        if (Files.exists(statusPath)) {
            try {
                for (String line : Files.readAllLines(statusPath, StandardCharsets.UTF_8)) {
                    if (line.startsWith("VmRSS:")) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length >= 2) {
                            return OptionalDouble.of(Long.parseLong(parts[1]) / 1024.0); // converte la memoria residente in MB (VmRSS è in kB)
                        }
                    }
                }
            } catch (IOException | NumberFormatException ignored) {
                return OptionalDouble.empty();
            }
        }

        // alternativa
        try {
            Process process = new ProcessBuilder("ps", "-o", "rss=", "-p", Long.toString(pid)).start();
            String stdout = readAll(process.inputReader(StandardCharsets.UTF_8)).trim();
            process.waitFor();
            if (stdout.isEmpty()) {
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(Long.parseLong(stdout) / 1024.0);
        } catch (IOException | InterruptedException | NumberFormatException error) {
            if (error instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return OptionalDouble.empty();
        }
    }

    /// Metodo helper che legge tutto il contenuto di un BufferedReader e lo restituisce come stringa.
    private static String readAll(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append(System.lineSeparator());
        }
        return builder.toString();
    }

    /// Metodo helper che restituisce il massimo tra due valori opzionali, gestendo il caso in cui uno dei due non sia disponibile.
    private static OptionalDouble maxOptional(OptionalDouble left, OptionalDouble right) {
        if (left.isEmpty()) {
            return right;
        }
        if (right.isEmpty()) {
            return left;
        }
        return OptionalDouble.of(Math.max(left.getAsDouble(), right.getAsDouble()));
    }

    /// Metodo helper che rimuove l'estensione dal nome del file, ad esempio small_mixed.txt -> small_mixed.
    private static String stripExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    /// Metodo helper che calcola il percentile richiesto su una lista di latenze, dopo averle ordinate.
    private static double percentile(List<Double> values, int percentile) {
        if (values.isEmpty()) {
            return 0.0;
        }
        List<Double> ordered = values.stream().sorted(Comparator.naturalOrder()).toList();
        int index = (int) Math.ceil((percentile / 100.0) * ordered.size()) - 1; // calcola l'indice dell'elemento che rappresenta il percentile richiesto
        index = Math.max(0, Math.min(index, ordered.size() - 1));
        return ordered.get(index);
    }

    /// Stampa una sintesi leggibile dei risultati di throughput.
    private static void printThroughputResultsSummary(List<ThroughputResult> results) {
        if (results.isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Benchmark results");
        System.out.printf(
                Locale.ROOT,
                "%-8s %-18s %-4s %-9s %-10s %-12s %-9s %-9s %-9s%n",
                "language",
                "workload",
                "run",
                "commands",
                "time_s",
                "cmd/s",
                "p50_ms",
                "p95_ms",
                "p99_ms"
        );
        System.out.println("-".repeat(95));

        String previousWorkload = null;
        String previousLanguage = null;
        for (ThroughputResult result : results) {
            if (previousWorkload != null && !previousWorkload.equals(result.workload())) {
                System.out.println("-".repeat(95));
            } else if (previousLanguage != null && !previousLanguage.equals(result.language())) {
                System.out.println();
            }
            System.out.printf(
                    Locale.ROOT,
                    "%-8s %-18s %-4d %-9d %-10.2f %-12.0f %-9.4f %-9.4f %-9.4f%n",
                    result.language(),
                    result.workload(),
                    result.run(),
                    result.commands(),
                    result.timeSeconds(),
                    result.commandsPerSecond(),
                    result.p50Ms(),
                    result.p95Ms(),
                    result.p99Ms()
            );
            previousWorkload = result.workload();
            previousLanguage = result.language();
        }

        System.out.println();
        System.out.println();
        System.out.println();
        printAverageThroughputSummary(results);
        System.out.println();
    }

    /// Metodo helper che stampa il throughput medio per coppia workload/linguaggio.
    private static void printAverageThroughputSummary(List<ThroughputResult> results) {
        Map<String, List<ThroughputResult>> groupedResults = new HashMap<>(); // gruppo -> risultati del gruppo
        for (ThroughputResult result : results) {
            String key = result.workload() + "\0" + result.language();
            groupedResults.computeIfAbsent(key, ignored -> new ArrayList<>()).add(result);
        }

        List<AverageThroughputSummary> averages = groupedResults.values()
                .stream()
                .map(group -> {
                    ThroughputResult first = group.get(0); // solo per recuperare workload e language, comuni a tutti i risultati del gruppo
                    double averageCommandsPerSecond = group.stream()
                            .mapToDouble(ThroughputResult::commandsPerSecond)
                            .average()
                            .orElse(0.0);
                    return new AverageThroughputSummary(
                            first.workload(),
                            first.language(),
                            group.size(),
                            averageCommandsPerSecond
                    );
                })
                .sorted(Comparator  // ordina prima per nome workload e poi per throughput (comando/s) in ordine decrescente
                        .comparing(AverageThroughputSummary::workload)
                        .thenComparing(Comparator.comparingDouble(AverageThroughputSummary::commandsPerSecond).reversed()))
                .toList();

        System.out.println("Average throughput");
        System.out.printf(Locale.ROOT, "%-14s %-8s %-4s %-12s%n", "workload", "language", "runs", "avg_cmd/s");
        System.out.println("-".repeat(45));
        String previousWorkload = null;
        for (AverageThroughputSummary average : averages) {
            if (previousWorkload != null && !previousWorkload.equals(average.workload())) {
                System.out.println("-".repeat(45));
            }
            System.out.printf(
                    Locale.ROOT,
                    "%-14s %-8s %-4d %-12.0f%n",
                    average.workload(),
                    average.language(),
                    average.runs(),
                    average.commandsPerSecond()
            );
            previousWorkload = average.workload();
        }
    }

    /// Scrive i risultati di throughput in un file CSV.
    private static void writeThroughputResults(List<ThroughputResult> results) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(THROUGHPUT_RESULTS_FILE, StandardCharsets.UTF_8)) {
            writer.write("language,workload,commands,run,time_seconds,commands_per_second,p50_ms,p95_ms,p99_ms");
            writer.newLine();
            for (ThroughputResult result : results) {
                writer.write(String.join(",",
                        result.language(),
                        result.workload(),
                        Integer.toString(result.commands()),
                        Integer.toString(result.run()),
                        String.format(Locale.ROOT, "%.4f", result.timeSeconds()),
                        String.format(Locale.ROOT, "%.2f", result.commandsPerSecond()),
                        String.format(Locale.ROOT, "%.5f", result.p50Ms()),
                        String.format(Locale.ROOT, "%.5f", result.p95Ms()),
                        String.format(Locale.ROOT, "%.5f", result.p99Ms())
                ));
                writer.newLine();
            }
        }
    }

    /// Stampa una sintesi leggibile dei risultati di memoria.
    private static void printMemoryResultsSummary(List<MemoryResult> results) {
        if (results.isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Memory results");
        System.out.printf(
                Locale.ROOT,
                "%-8s %-18s %-9s %-12s %-12s%n",
                "language",
                "workload",
                "commands",
                "rss_start",
                "rss_peak"
        );
        System.out.println("-".repeat(65));

        String previousWorkload = null;
        String previousLanguage = null;
        for (MemoryResult result : results) {
            if (previousWorkload != null && !previousWorkload.equals(result.workload())) {
                System.out.println("-".repeat(65));
            } else if (previousLanguage != null && !previousLanguage.equals(result.language())) {
                System.out.println();
            }
            System.out.printf(
                    Locale.ROOT,
                    "%-8s %-18s %-9d %-12s %-12s%n",
                    result.language(),
                    result.workload(),
                    result.commands(),
                    formatOptionalTableDouble(result.rssStartMb()),
                    formatOptionalTableDouble(result.rssPeakMb())
            );
            previousWorkload = result.workload();
            previousLanguage = result.language();
        }
        System.out.println();
        System.out.println();
        System.out.println();
        printAverageMemorySummary(results);
        System.out.println();
    }

    /// Metodo helper che stampa la memoria RSS media per coppia workload/linguaggio.
    private static void printAverageMemorySummary(List<MemoryResult> results) {
        Map<String, List<MemoryResult>> groupedResults = new HashMap<>();   // gruppo -> risultati del gruppo
        for (MemoryResult result : results) {
            String key = result.workload() + "\0" + result.language();
            groupedResults.computeIfAbsent(key, ignored -> new ArrayList<>()).add(result);
        }

        List<AverageMemorySummary> averages = groupedResults.values()
                .stream()
                .map(group -> {
                    MemoryResult first = group.get(0);  // solo per recuperare workload e language, comuni a tutti i risultati del gruppo
                    double averageRssStartMb = averageOptional(group.stream()
                            .map(MemoryResult::rssStartMb)
                            .toList());
                    double averageRssPeakMb = averageOptional(group.stream()
                            .map(MemoryResult::rssPeakMb)
                            .toList());
                    return new AverageMemorySummary(
                            first.workload(),
                            first.language(),
                            group.size(),
                            averageRssStartMb,
                            averageRssPeakMb
                    );
                })
                .sorted(Comparator  // ordina prima per nome workload e poi per rssPeakMb in ordine crescente
                        .comparing(AverageMemorySummary::workload)
                        .thenComparing(Comparator.comparingDouble(AverageMemorySummary::rssPeakMb)))
                .toList();

        System.out.println("Average memory");
        System.out.printf(Locale.ROOT, "%-18s %-8s %-4s %-14s %-14s%n", "workload", "language", "runs", "avg_start_mb", "avg_peak_mb");
        System.out.println("-".repeat(65));
        String previousWorkload = null;
        for (AverageMemorySummary average : averages) {
            if (previousWorkload != null && !previousWorkload.equals(average.workload())) {
                System.out.println("-".repeat(65));
            }
            System.out.printf(
                    Locale.ROOT,
                    "%-18s %-8s %-4d %-14.2f %-14.2f%n",
                    average.workload(),
                    average.language(),
                    average.runs(),
                    average.rssStartMb(),
                    average.rssPeakMb()
            );
            previousWorkload = average.workload();
        }
    }

    /// Scrive i risultati di memoria in un CSV.
    private static void writeMemoryResults(List<MemoryResult> results) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(MEMORY_RESULTS_FILE, StandardCharsets.UTF_8)) {
            writer.write("language,workload,commands,rss_start_mb,rss_peak_mb");
            writer.newLine();
            for (MemoryResult result : results) {
                writer.write(String.join(",",
                        result.language(),
                        result.workload(),
                        Integer.toString(result.commands()),
                        result.rssStartMb().isPresent() ? String.format(Locale.ROOT, "%.2f", result.rssStartMb().getAsDouble()) : "",
                        result.rssPeakMb().isPresent() ? String.format(Locale.ROOT, "%.2f", result.rssPeakMb().getAsDouble()) : ""
                ));
                writer.newLine();
            }
        }
    }

    /// Metodo helper che formatta un valore opzionale per le tabelle sul terminale.
    private static String formatOptionalTableDouble(OptionalDouble value) {
        return value.isPresent() ? String.format(Locale.ROOT, "%.2f", value.getAsDouble()) : "-";
    }

    /// Metodo helper che calcola la media di valori opzionali, ignorando quelli non disponibili.
    private static double averageOptional(List<OptionalDouble> values) {
        return values.stream()
                .filter(OptionalDouble::isPresent)
                .mapToDouble(OptionalDouble::getAsDouble)
                .average()
                .orElse(0.0);
    }



    /* RECORD INTERNI */
    /// Enum che rappresenta la modalità di esecuzione del benchmark.
    private enum Mode {
        THROUGHPUT, // modalità di default
        MEMORY
    }

    /// Record interno che contiene le opzioni configurabili da riga di comando.
    private record Options(
            int runs,                   // quante volte ripetere ogni benchmark
            List<String> workloads,     // quali file workload usare, vuota se si usano tutti
            boolean generateWorkloads,  // se deve generare automaticamente i file workload mancanti (true), o se deve usare i workload già presenti (false)
            boolean generateOnly,       // se deve solo generare i workload senza lanciare i benchmark (rigenera tutti i workload, anche se esistono già, se abilitato)
            Mode mode                   // modalità throughput o memory
    ) {
        /// Interpreta gli argomenti CLI e applica i valori di default quando un'opzione non viene passata.
        private static Options parse(String[] args) {
            int runs = DEFAULT_RUNS;
            List<String> workloads = new ArrayList<>();
            boolean generateWorkloads = true;
            boolean generateOnly = false;
            Mode mode = Mode.THROUGHPUT;

            Map<String, String> values = new HashMap<>();  // mappa per memorizzare i valori degli argomenti

            for (int index = 0; index < args.length; index++) {
                String arg = args[index];
                if ("--no-generate-workloads".equals(arg)) {
                    generateWorkloads = false;
                    continue;
                }
                if ("--generate-only".equals(arg)) {
                    generateOnly = true;
                    generateWorkloads = true;
                    continue;
                }
                if (!arg.startsWith("--")) {
                    throw new IllegalArgumentException("Unknown argument: " + arg);
                }
                if (index + 1 >= args.length) {
                    throw new IllegalArgumentException("Missing value for argument: " + arg);
                }
                values.put(arg, args[++index]);
            }

            if (values.containsKey("--runs")) {
                runs = Integer.parseInt(values.get("--runs"));
            }
            if (values.containsKey("--workload")) {
                workloads.add(values.get("--workload"));
            }
            if (values.containsKey("--workloads")) {
                for (String workload : values.get("--workloads").split(",")) {
                    String trimmed = workload.trim();
                    if (!trimmed.isEmpty()) {
                        workloads.add(trimmed);
                    }
                }
            }
            if (values.containsKey("--mode")) {
                mode = parseMode(values.get("--mode"));
            }
            if (values.containsKey("--generate-workloads")) {
                generateWorkloads = Boolean.parseBoolean(values.get("--generate-workloads"));
            }

            return new Options(runs, workloads, generateWorkloads, generateOnly, mode);
        }

        /// Metodo helper che applica la modalità inserita negli argomenti CLI.
        private static Mode parseMode(String value) {
            String normalized = value.toLowerCase(Locale.ROOT);
            return switch (normalized) {
                case "throughput" -> Mode.THROUGHPUT;
                case "memory" -> Mode.MEMORY;
                default -> throw new IllegalArgumentException("Invalid --mode value: " + value);
            };
        }
    }

    /// Record interno che rappresenta una versione del database da testare: nome, comando di avvio e directory da cui eseguirlo.
    private record Implementation(String language, List<String> command, Path cwd) {
    }

    /// Record interno che rappresenta un file workload già caricato in memoria insieme alla lista dei comandi da inviare.
    private record Workload(Path path, List<String> commands) {
    }

    /// Record interno che rappresenta una riga del CSV finale con tutte le metriche misurate per una singola run.
    private record ThroughputResult(
            String language,            // implementazione testata (rust, python, java, ...)
            String workload,            // nome del workload testato (small, medium, large)
            int commands,               // numero di comandi eseguiti
            int run,                    // numero di run/ripetizione del benchmark
            double timeSeconds,         // tempo totale in secondi per l'esecuzione del benchmark
            double commandsPerSecond,   // throughput misurato, comandi eseguiti al secondo
            double p50Ms,               // latenza media per il 50% dei comandi eseguiti, in millisecondi
            double p95Ms,               // latenza media per il 95% dei comandi eseguiti, in millisecondi
            double p99Ms                // latenza media per il 99% dei comandi eseguiti, in millisecondi
    ) {
    }

    /// Record interno che rappresenta una riga del CSV dedicato alla misurazione memoria.
    private record MemoryResult(
            String language,
            String workload,
            int commands,
            OptionalDouble rssStartMb,  // memoria RSS all'inizio del benchmark, in megabyte
            OptionalDouble rssPeakMb    // memoria RSS al momento del peak, in megabyte
    ) {
    }

    /// Record interno usato solo per stampare la memoria media.
    private record AverageMemorySummary(
            String workload,
            String language,
            int runs,
            double rssStartMb,
            double rssPeakMb
    ) {
    }

    /// Record interno usato solo per stampare il throughput medio.
    private record AverageThroughputSummary(
            String workload,
            String language,
            int runs,
            double commandsPerSecond
    ) {
    }
}
