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
- latenze `p50`, `p95`, `p99` e latenza massima (`p100`);
- deviazione standard campionaria e coefficiente di variazione del throughput tra run;
- memoria RSS (Resident Set Size) del processo, cioè la memoria (RAM) fisica utilizzata;
- media e deviazione standard della memoria RSS tra run.

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
- `max_ms`: latenza massima osservata nella singola run.

Queste latenze sono end-to-end: includono anche il costo di `stdin`, `stdout` e
del prompt della CLI.

### Variabilità Tra Run

Quando una stessa coppia linguaggio/workload viene ripetuta più volte con
`--runs`, il runner calcola nel riepilogo console:

- `avg_cmd/s`: throughput medio;
- `std_cmd/s`: deviazione standard campionaria del throughput;
- `cv_pct`: coefficiente di variazione, cioè `std_cmd/s / avg_cmd/s * 100`;
- `ci95_low` e `ci95_high`: estremi dell'intervallo di confidenza al 95% del throughput medio.

La deviazione standard campionaria misura quanto i throughput delle singole run
si discostano dal throughput medio. Se una stessa configurazione produce
throughput molto simili tra loro, la deviazione standard è bassa; se invece i
risultati oscillano molto, la deviazione standard cresce.

Dato un insieme di `n` run con throughput `x1, x2, ..., xn` e media `x_bar`, il
runner usa la formula campionaria:

```text
std_cmd/s = sqrt(((x1 - x_bar)^2 + (x2 - x_bar)^2 + ... + (xn - x_bar)^2) / (n - 1))
```

La divisione per `n - 1` indica che le run eseguite sono trattate come un
campione di possibili misurazioni, non come l'intera popolazione teorica di
tutte le esecuzioni possibili.

Il coefficiente di variazione normalizza questa variabilità rispetto alla media:

```text
cv_pct = (std_cmd/s / avg_cmd/s) * 100
```

Questo valore permette di confrontare la stabilità di benchmark con throughput
molto diversi. Ad esempio, una deviazione standard di 500 cmd/s pesa molto di
più su una media di 2.000 cmd/s che su una media di 20.000 cmd/s.

### Intervalli Di Confidenza

L'intervallo di confidenza al 95% stima l'intervallo entro cui si trova il vero
throughput medio, tenendo conto della variabilità osservata tra le run.

Il runner calcola il margine dell'intervallo così:

```text
margin = t * std_cmd/s / sqrt(n)
```

dove:

- `n` è il numero di run;
- `std_cmd/s` è la deviazione standard campionaria;
- `t` è il valore critico della distribuzione t di Student per `n - 1` gradi di libertà.

Gli estremi salvati sono:

```text
ci95_lower = avg_cmd/s - margin
ci95_upper = avg_cmd/s + margin
```

Per campioni piccoli il runner usa valori tabellati della t di Student; per
campioni grandi il valore converge verso `1.96`. Con una sola run il margine è
posto a `0.0`, perché non è possibile stimare la variabilità tra run.

Queste metriche ampliano lo spettro di misurazione perché indicano quanto sono
stabili i risultati, non solo quanto sono veloci in media.

### Memoria RSS

La memoria RSS, Resident Set Size, indica quanta RAM fisica occupa il processo.

In modalità `memory`, il runner misura per ogni run:

- `rss_start_mb`: memoria osservata poco dopo l'avvio del processo;
- `rss_peak_mb`: massimo valore RSS osservato durante l'esecuzione del workload.

La modalità `memory` può essere ripetuta con `--runs`, come la modalità
`throughput`. Per la memoria sono sufficienti poche ripetizioni, tipicamente
3-5 run: l'obiettivo non è stimare un throughput medio con alta precisione, ma
mostrare che la misura RSS è stabile e riproducibile.

Il runner produce anche una sintesi aggregata con:

- `avg_rss_start_mb`: media della memoria RSS iniziale;
- `std_rss_start_mb`: deviazione standard campionaria della memoria RSS iniziale;
- `avg_rss_peak_mb`: media del picco RSS;
- `std_rss_peak_mb`: deviazione standard campionaria del picco RSS.

Gli intervalli di confidenza non vengono calcolati per la memoria, perché la
deviazione standard è sufficiente a documentare la stabilità della misura RSS.

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
    |-- throughput_summary.csv
    |-- memory_results.csv
    |-- memory_summary.csv
    |-- examples/
    |   |-- throughput_results_example.csv
    |   |-- throughput_summary_example.csv
    |   |-- memory_results_example.csv
    |   `-- memory_summary_example.csv
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
- salva risultati CSV raw e aggregati per throughput e memoria;
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
| `--runs` | numero intero | `5` in `throughput`, `1` in `memory` | Numero di ripetizioni per ogni coppia linguaggio/workload. Per `throughput` sono consigliate 30-100 run; per `memory` sono sufficienti 3-5 run quando si vuole documentare la stabilità RSS. |
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

# Esegue la misurazione della memoria con 3 run per workload
java -cp benchmark\out BenchmarkRunner --mode memory --runs 3 --no-generate-workloads

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
  `throughput`, con una riga per singola run;
- `throughput_summary.csv`: riepilogo aggregato dell'ultima esecuzione in
  modalità `throughput`, con una riga per coppia linguaggio/workload;
- `memory_results.csv`: risultati dell'ultima esecuzione in modalità `memory`;
- `memory_summary.csv`: riepilogo aggregato dell'ultima esecuzione in modalità
  `memory`, con una riga per coppia linguaggio/workload;
- `examples/throughput_results_example.csv`: snapshot raw stabile del throughput;
- `examples/throughput_summary_example.csv`: snapshot aggregato stabile del throughput;
- `examples/memory_results_example.csv`: snapshot raw stabile della memoria;
- `examples/memory_summary_example.csv`: snapshot aggregato stabile della memoria;
- `summary.md`: riepilogo leggibile dei risultati di esempio.

I file `throughput_results.csv`, `throughput_summary.csv`,
`memory_results.csv` e `memory_summary.csv` vengono sovrascritti a ogni nuova
esecuzione della rispettiva modalità. I file dentro `examples/` sono pensati
come snapshot documentali e non dovrebbero essere sovrascritti durante le
normali esecuzioni.

### Formato Di `throughput_results.csv`

```csv
language,workload,commands,run,time_seconds,commands_per_second,p50_ms,p95_ms,p99_ms,max_ms
python,small_mixed,1000,1,0.1621,6168.49,0.06350,0.12300,0.15970,1.28420
```

Questo file è raw: mantiene tutte le singole run e serve per riproducibilità e
controlli puntuali.

### Formato Di `throughput_summary.csv`

```csv
language,workload,runs,commands,avg_time_seconds,avg_commands_per_second,std_commands_per_second,cv_percent,ci95_margin_commands_per_second,ci95_lower_commands_per_second,ci95_upper_commands_per_second,avg_p50_ms,avg_p95_ms,avg_p99_ms,avg_max_ms
python,small_mixed,5,1000,0.2543,4128.77,614.22,14.8762,762.63,3366.14,4891.40,0.08120,0.14240,0.20530,1.28420
```

Questo file è aggregato: ogni riga riassume tutte le run per una coppia
linguaggio/workload; contiene medie, misure di variabilità e intervalli di
confidenza già pronti.

### Formato Di `memory_results.csv`

```csv
language,workload,commands,run,rss_start_mb,rss_peak_mb
python,small_mixed,1000,1,13.28,13.32
```

### Formato Di `memory_summary.csv`

```csv
language,workload,runs,commands,avg_rss_start_mb,std_rss_start_mb,avg_rss_peak_mb,std_rss_peak_mb
python,small_mixed,5,1000,13.28,0.04,13.32,0.05
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
`--runs`. In modalità `throughput`, questo riduce il rumore causato da altri
processi del sistema operativo, cache, avvio della JVM e variazioni temporanee
della macchina. In modalità `memory`, poche run sono sufficienti per mostrare la
stabilità della memoria RSS osservata.

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
