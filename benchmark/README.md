# MiniDB Benchmark

Questa cartella contiene gli strumenti per confrontare le prestazioni delle tre
implementazioni di MiniDB:

- Java: `java-minidb`
- Python: `python-minidb`
- Rust: `rust-minidb`

L'obiettivo del benchmark è misurare quanto tempo impiega ogni implementazione a
eseguire la stessa sequenza di comandi MiniDB.

## Cosa Misuriamo

In questa prima fase misuriamo il comportamento end-to-end dei programmi da
linea di comando.

Questo significa che ogni implementazione viene avviata come processo separato,
riceve comandi tramite `stdin` e produce risposte su `stdout`.

Le metriche principali sono:

- tempo totale di esecuzione;
- numero di comandi eseguiti;
- throughput (comandi al secondo);
- latenza (tempo per una singola operazione);
- memoria RSS del processo (quanta RAM fisica si occupa).

### Throughput

Il throughput indica quante operazioni al secondo riesce a gestire ogni
implementazione.

La formula principale è:

```text
commands_per_second = numero_comandi / tempo_totale
```

Questa è la metrica principale del runner, perché permette di confrontare
direttamente Python, Java e Rust sullo stesso workload.

### Latenza

La latenza indica quanto tempo impiega una singola operazione.

Non basta guardare solo la media: serve osservare la distribuzione dei tempi.
Le statistiche principali sono:

- `p50`: mediana, cioè il valore sotto cui cade il 50% delle operazioni;
- `p95`: valore sotto cui cade il 95% delle operazioni;
- `p99`: coda lenta, cioè il valore sotto cui cade il 99% delle operazioni.

Il `p99` è importante perché rappresenta le operazioni lente che colpiscono una
piccola parte delle richieste. In un sistema reale sono spesso quelle più
visibili agli utenti.

Nel benchmark end-to-end la latenza può essere misurata in due modi:

- misurando ogni singolo comando, con maggiore dettaglio ma più overhead;
- misurando blocchi di comandi, con meno overhead ma minore precisione.

Il runner misura già la latenza comando per comando. Questa misura include anche
il costo di `stdin`, `stdout` e del prompt della CLI, quindi va interpretata come
latenza end-to-end.

### Memoria RSS

La memoria RSS, Resident Set Size, indica quanta RAM fisica occupa il processo.

La misureremo in due momenti:

- a riposo, subito dopo l'avvio del processo;
- sotto carico, durante o dopo l'esecuzione del workload.

Su Linux puo essere misurata con strumenti come `ps` o leggendo
`/proc/<pid>/status`.

Su Windows, dove questo progetto viene eseguito attualmente, il runner usa
PowerShell per leggere il `WorkingSet64` del processo.

La RSS è utile soprattutto nei workload con molti `SET`, perché il database
mantiene tutte le chiavi in memoria.

## Struttura Dei File

```text
benchmark/
|-- README.md
|-- src/
    `-- BenchmarkRunner.java
|-- workloads/
|   |-- small_mixed.txt
|   |-- medium_mixed.txt
|   `-- large_mixed.txt
`-- results/
    `-- results.csv
```

### `BenchmarkRunner.java`

Programma principale del benchmark.

Il runner:

- compila le implementazioni;
- legge i file in `workloads/`;
- esegue lo stesso workload su Python, Java e Rust;
- misura il tempo di ogni esecuzione;
- calcola il throughput;
- raccoglie la latenza, se abilitata;
- raccoglie la memoria RSS, se abilitata;
- ripete ogni test piu volte;
- salva i risultati in formato CSV dentro `results/`.

Per compilare il runner:

```powershell
javac -d benchmark\out benchmark\src\BenchmarkRunner.java
```

Per eseguirlo:

```powershell
java -cp benchmark\out BenchmarkRunner
```

Opzioni disponibili da terminale:

| Opzione | Valore | Default | Descrizione |
| --- | --- | --- | --- |
| `--runs` | numero intero | `5` | Numero di ripetizioni per ogni coppia linguaggio/workload. |
| `--workloads` | nomi file separati da virgola | tutti i `.txt` | Esegue solo i workload indicati, nell'ordine specificato. |
| `--rss-sample-every` | numero intero | `1000` | Campiona la memoria RSS ogni N comandi. Con `0` disattiva la misura RSS. |
| `--generate-workloads` | `true`/`false` | `true` | Abilita o disabilita la generazione automatica dei workload mancanti o vuoti. |
| `--no-generate-workloads` | nessuno | non attivo | Disabilita la generazione automatica dei workload. Equivale a `--generate-workloads false`. |
| `--generate-only` | nessuno | non attivo | Genera o rigenera i workload standard e termina senza compilare o eseguire benchmark. |

Esempi utili:

```powershell
# Esegue tutti i workload, 5 run ciascuno, con campionamento RSS ogni 1000 comandi
java -cp benchmark\out BenchmarkRunner

# Esegue un solo workload con 3 ripetizioni
java -cp benchmark\out BenchmarkRunner --workload medium_write_heavy.txt --runs 3

# Esegue due o piu workload specifici
java -cp benchmark\out BenchmarkRunner --workloads medium_read_heavy.txt,medium_write_heavy.txt --runs 3

# Esegue i benchmark senza misurare la memoria RSS
java -cp benchmark\out BenchmarkRunner --runs 3 --rss-sample-every 0

# Usa solo i workload gia presenti, senza generarne di nuovi
java -cp benchmark\out BenchmarkRunner --no-generate-workloads

# Genera o rigenera i workload standard senza lanciare benchmark
java -cp benchmark\out BenchmarkRunner --generate-only
```

### `workloads/`

Contiene i file di input del benchmark.

Ogni workload è un file di testo con un comando MiniDB per riga.

Esempio:

```text
SET key1 10
GET key1
EXISTS key1
INCR counter
DEL key1
```

I workload attualmente previsti sono:

- `small_mixed.txt`: workload piccolo, utile per testare che il runner funzioni;
- `medium_mixed.txt`: workload intermedio;
- `large_mixed.txt`: workload più grande, utile per misure più stabili.
- `small_read_heavy.txt`, `medium_read_heavy.txt`, `large_read_heavy.txt`:
  workload con circa 80% `GET`, 10% `SET`, 10% `EXISTS`;
- `small_write_heavy.txt`, `medium_write_heavy.txt`, `large_write_heavy.txt`:
  workload con circa 80% `SET`, 10% `GET`, 10% `EXISTS`.

### `results/`

Contiene i risultati generati dal benchmark.

Il file principale previsto è:

```text
benchmark/results/results.csv
```

Esempio di righe attese:

```csv
language,workload,commands,run,time_seconds,commands_per_second,p50_ms,p95_ms,p99_ms,rss_start_mb,rss_peak_mb
python,small_mixed,1000,1,0.120,8333.33,0.08,0.15,0.40,18.5,24.1
java,small_mixed,1000,1,0.080,12500.00,0.05,0.12,0.30,42.0,58.7
rust,small_mixed,1000,1,0.030,33333.33,0.02,0.04,0.09,3.8,6.2
```

## Metodo Di Benchmark

Il benchmark confronta le tre implementazioni eseguendo lo stesso file di
workload su ciascun linguaggio. Ogni implementazione viene avviata come processo
separato, riceve i comandi tramite `stdin` e restituisce una risposta su
`stdout`.

I workload sono deterministici: a parità di file, Python, Java e Rust ricevono
la stessa sequenza di comandi nello stesso ordine. Questo rende confrontabili i
risultati tra linguaggi.

Ogni combinazione linguaggio/workload può essere ripetuta più volte tramite
`--runs`, in modo da ridurre il rumore causato da altri processi del sistema
operativo, cache, avvio della JVM e variazioni temporanee della macchina.

## Note Di Interpretazione

I risultati di questa prima fase includono anche il costo di:

- avvio del processo;
- parsing testuale dei comandi;
- input/output da terminale;
- stampa delle risposte.

Quindi questo benchmark misura il programma completo, non soltanto la funzione
interna `execute`.

Su Windows la misura della memoria RSS usa PowerShell per leggere il
`WorkingSet64` del processo. Questo campionamento può aggiungere overhead,
soprattutto nei workload grandi, perché il runner avvia un comando PowerShell
ogni `--rss-sample-every` comandi. Di conseguenza il throughput va interpretato
come misura end-to-end della CLI completa, non come misura pura del core in
memoria.

Per confrontare meglio il throughput senza il costo del campionamento memoria si
può eseguire il runner disattivando la RSS:

```powershell
java -cp benchmark\out BenchmarkRunner --runs 3 --rss-sample-every 0
```

In una fase successiva si potrà aggiungere un benchmark più specifico sul core
del database, misurando direttamente l'esecuzione dei comandi in memoria.
