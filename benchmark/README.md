# MiniDB Benchmark

Questa cartella contiene gli strumenti per confrontare le prestazioni delle tre
implementazioni di MiniDB:

- Java: `java-minidb`
- Python: `python-minidb`
- Rust: `rust-minidb`

Il benchmark esegue la stessa sequenza di comandi sulle tre implementazioni e
produce risultati confrontabili su throughput, latenze e memoria.

## Cosa Misuriamo

Il runner misura il comportamento end-to-end dei programmi da linea di comando.
Ogni implementazione viene avviata come processo separato, riceve comandi tramite
`stdin` e produce risposte su `stdout`.

Le metriche principali sono:

- tempo totale di esecuzione;
- numero di comandi eseguiti;
- throughput, cioè comandi eseguiti al secondo;
- latenze `p50`, `p95`, `p99`;
- memoria RSS (Resident Set Size) del processo, cioè la memoria (RAM) fisica utilizzata.

Il runner separa esplicitamente le misurazioni in due modalità:

- `throughput`: misura tempo, throughput e latenze;
- `memory`: misura la memoria RSS iniziale e di picco.

Questa separazione evita che il campionamento della memoria alteri i risultati
di throughput, soprattutto su Windows.

### Throughput

Il throughput indica quante operazioni al secondo riesce a gestire ogni
implementazione.

La formula principale è:

```text
commands_per_second = numero_comandi / tempo_totale
```

Questa è la metrica principale per confrontare Python, Java e Rust sullo stesso
workload.

### Latenza

La latenza indica quanto tempo impiega una singola operazione.

Il runner misura la latenza comando per comando e calcola:

- `p50`: mediana, cioè il valore sotto cui cade il 50% delle operazioni;
- `p95`: valore sotto cui cade il 95% delle operazioni;
- `p99`: coda lenta, cioè il valore sotto cui cade il 99% delle operazioni.

Queste latenze sono end-to-end: includono anche il costo di `stdin`, `stdout` e
del prompt della CLI.

### Memoria RSS

La memoria RSS, Resident Set Size, indica quanta RAM fisica occupa il processo.

In modalità `memory`, il runner misura:

- `rss_start_mb`: memoria osservata poco dopo l'avvio del processo;
- `rss_peak_mb`: massimo valore RSS osservato durante l'esecuzione del workload.

Su Linux/Unix il runner prova prima a leggere `/proc/<pid>/status`, poi usa `ps`
come alternativa. Su Windows usa PowerShell per leggere `WorkingSet64`.

La modalità `memory` è separata dalla modalità `throughput` perché su Windows il
campionamento RSS tramite PowerShell è relativamente costoso. In questo modo i
risultati di throughput non vengono distorti dalla misurazione della memoria.

## Struttura Dei File

```text
benchmark/
|-- README.md
|-- src/
|   `-- BenchmarkRunner.java
|-- workloads/
|   |-- small_mixed.txt
|   |-- medium_mixed.txt
|   |-- large_mixed.txt
|   |-- ...
`-- results/
    |-- throughput_results.csv
    |-- memory_results.csv
    |-- ...
    `-- summary.md
```

## BenchmarkRunner

Il file principale è:

```text
benchmark/src/BenchmarkRunner.java
```

Il runner:

- compila le implementazioni Java e Rust;
- legge i workload da `benchmark/workloads/`;
- esegue lo stesso workload su Python, Java e Rust;
- ripete i test secondo il valore di `--runs` in modalità `throughput`;
- salva risultati CSV separati per throughput e memoria;
- genera o rigenera i workload standard quando richiesto.

Per compilare il runner:

```powershell
javac -d benchmark\out benchmark\src\BenchmarkRunner.java
```

Per eseguirlo con la modalità predefinita:

```powershell
java -cp benchmark\out BenchmarkRunner
```

La modalità predefinita è `throughput`.

## Opzioni Da Terminale

| Opzione | Valore | Default | Descrizione |
| --- | --- | --- | --- |
| `--mode` | `throughput` oppure `memory` | `throughput` | Sceglie il tipo di misurazione da eseguire. |
| `--runs` | numero intero | `5` | Numero di ripetizioni per ogni coppia linguaggio/workload in modalità `throughput`. In modalità `memory` viene eseguita una sola misurazione per workload. |
| `--workload` | nome file | tutti i `.txt` | Esegue un solo workload specifico. |
| `--workloads` | nomi file separati da virgola | tutti i `.txt` | Esegue solo i workload indicati. |
| `--generate-workloads` | `true`/`false` | `true` | Abilita o disabilita la generazione automatica dei workload mancanti o vuoti. |
| `--no-generate-workloads` | nessuno | non attivo | Disabilita la generazione automatica dei workload. Equivale a `--generate-workloads false`. |
| `--generate-only` | nessuno | non attivo | Genera o rigenera i workload standard e termina senza compilare o eseguire benchmark. |

### Esempi Utili

```powershell
# Esegue tutti i workload in modalità throughput, con 5 run ciascuno
java -cp benchmark\out BenchmarkRunner

# Usa solo i workload già presenti, senza generarne di nuovi
java -cp benchmark\out BenchmarkRunner --mode throughput --no-generate-workloads

# Esegue un solo workload con 3 ripetizioni
java -cp benchmark\out BenchmarkRunner --workload medium_write_heavy.txt --runs 3

# Esegue due o più workload specifici
java -cp benchmark\out BenchmarkRunner --workloads medium_read_heavy.txt,medium_write_heavy.txt --runs 3

# Esegue la misurazione della memoria
java -cp benchmark\out BenchmarkRunner --mode memory --no-generate-workloads

# Esegue la misurazione della memoria su un solo workload
java -cp benchmark\out BenchmarkRunner --mode memory --workload large_mixed.txt --no-generate-workloads

# Genera o rigenera i workload standard senza lanciare benchmark
java -cp benchmark\out BenchmarkRunner --generate-only
```

## Workload

La cartella `benchmark/workloads/` contiene i file di input del benchmark.

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

- `small_mixed.txt`: workload misto da 1.000 comandi;
- `medium_mixed.txt`: workload misto da 10.000 comandi;
- `large_mixed.txt`: workload misto da 100.000 comandi;
- `small_read_heavy.txt`, `medium_read_heavy.txt`, `large_read_heavy.txt`:
  workload con circa 80% `GET`, 10% `SET`, 10% `EXISTS`;
- `small_write_heavy.txt`, `medium_write_heavy.txt`, `large_write_heavy.txt`:
  workload con circa 80% `SET`, 10% `GET`, 10% `EXISTS`.

I workload sono deterministici: a parità di file, Python, Java e Rust ricevono
la stessa sequenza di comandi nello stesso ordine.

## Risultati

La cartella `benchmark/results/` contiene i risultati generati dal runner.

I file principali sono:

- `throughput_results.csv`: risultati dell'ultima esecuzione in modalità
  `throughput`;
- `memory_results.csv`: risultati dell'ultima esecuzione in modalità `memory`;
- `throughput_results_example.csv`: snapshot stabile di esempio/documentazione;
- `memory_results_example.csv`: snapshot stabile di esempio/documentazione;
- `summary.md`: riepilogo leggibile dei risultati di esempio.

I file `throughput_results.csv` e `memory_results.csv` vengono sovrascritti a
ogni nuova esecuzione della rispettiva modalità. I file con `_example` nel nome
sono pensati come snapshot documentali e non dovrebbero essere sovrascritti
durante le normali esecuzioni.

### Formato Di `throughput_results.csv`

```csv
language,workload,commands,run,time_seconds,commands_per_second,p50_ms,p95_ms,p99_ms
python,small_mixed,1000,1,0.1621,6168.49,0.06350,0.12300,0.15970
```

### Formato Di `memory_results.csv`

```csv
language,workload,commands,rss_start_mb,rss_peak_mb
python,small_mixed,1000,13.28,13.32
```

### Summary

Il file `summary.md` presenta i risultati in forma più leggibile.

Nel riepilogo:

- il throughput è ordinato per efficienza decrescente, quindi prima il linguaggio
  con più comandi al secondo;
- la memoria è ordinata per efficienza crescente, quindi prima il linguaggio con
  minore picco RSS.

## Metodo Di Benchmark

Il benchmark confronta le tre implementazioni eseguendo lo stesso file di
workload su ciascun linguaggio. Ogni implementazione viene avviata come processo
separato, riceve i comandi tramite `stdin` e restituisce una risposta su
`stdout`.

Ogni combinazione linguaggio/workload può essere ripetuta più volte tramite
`--runs` in modalità `throughput`, in modo da ridurre il rumore causato da altri
processi del sistema operativo, cache, avvio della JVM e variazioni temporanee
della macchina.

La modalità `memory`, invece, esegue una sola misurazione per workload e
linguaggio. In questa modalità il runner non misura latenze e throughput, ma si
concentra sulla memoria RSS.

## Note Di Interpretazione

I risultati includono anche il costo di:

- avvio del processo;
- parsing testuale dei comandi;
- input/output da terminale;
- stampa delle risposte.

Quindi questo benchmark misura il programma completo, non soltanto la funzione
interna `execute`.

Su Windows la misura della memoria RSS usa PowerShell per leggere il
`WorkingSet64` del processo. Questa misura è adatta alla modalità `memory`, ma
non viene usata nella modalità `throughput` per evitare overhead sui tempi.
