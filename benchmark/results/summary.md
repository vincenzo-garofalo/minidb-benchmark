# Riepilogo dei Benchmark

A partire dai file CSV di esempio stabili:

- `benchmark/results/examples/throughput_results_example.csv`
- `benchmark/results/examples/throughput_summary_example.csv`
- `benchmark/results/examples/memory_results_example.csv`
- `benchmark/results/examples/memory_summary_example.csv`

Comandi utilizzati:

```powershell
java -cp benchmark\out BenchmarkRunner --mode throughput --runs 100 --no-generate-workloads
java -cp benchmark\out BenchmarkRunner --mode memory --runs 3 --no-generate-workloads
```

Throughput e memoria sono misurati in esecuzioni separate. La modalità throughput disabilita il campionamento RSS per evitare overhead sulla misurazione delle prestazioni; la modalità memory misura i valori RSS iniziali e di picco su 3 run e li aggrega con media e deviazione standard.

## Risultati Throughput

Ogni riga riporta la media di 100 run per lo stesso linguaggio e workload. Per ogni workload, i linguaggi sono ordinati dal throughput medio più alto al più basso. L'intervallo di confidenza al 95% è calcolato sul throughput medio.

### Workload large

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| large_mixed | rust | 100 | 4.85 | 20647 | 624 | 3.02 | [20523, 20771] | 0.0444 | 0.0699 | 0.0985 | 25.5451 |
| large_mixed | python | 100 | 5.70 | 17574 | 510 | 2.90 | [17473, 17675] | 0.0517 | 0.0800 | 0.1082 | 82.8588 |
| large_mixed | java | 100 | 8.41 | 14791 | 1699 | 11.49 | [14454, 15128] | 0.0595 | 0.1000 | 0.1424 | 1758.9592 |
| large_read_heavy | rust | 100 | 4.77 | 21005 | 697 | 3.32 | [20866, 21143] | 0.0440 | 0.0674 | 0.0944 | 26.8596 |
| large_read_heavy | python | 100 | 5.67 | 17679 | 825 | 4.67 | [17516, 17843] | 0.0524 | 0.0747 | 0.0981 | 82.1146 |
| large_read_heavy | java | 100 | 6.91 | 14491 | 480 | 3.31 | [14396, 14587] | 0.0598 | 0.1063 | 0.1524 | 129.0720 |
| large_write_heavy | rust | 100 | 4.85 | 20629 | 500 | 2.43 | [20529, 20728] | 0.0444 | 0.0706 | 0.0995 | 26.0795 |
| large_write_heavy | python | 100 | 5.72 | 17482 | 481 | 2.75 | [17387, 17577] | 0.0523 | 0.0787 | 0.1053 | 85.0510 |
| large_write_heavy | java | 100 | 6.77 | 14776 | 456 | 3.09 | [14685, 14867] | 0.0592 | 0.1022 | 0.1450 | 123.3103 |

### Workload medium

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| medium_mixed | rust | 100 | 0.54 | 18482 | 1213 | 6.56 | [18241, 18722] | 0.0468 | 0.0765 | 0.1117 | 26.2674 |
| medium_mixed | python | 100 | 0.68 | 14817 | 542 | 3.66 | [14710, 14925] | 0.0531 | 0.0835 | 0.1155 | 87.4047 |
| medium_mixed | java | 100 | 1.33 | 7589 | 741 | 9.76 | [7442, 7736] | 0.0989 | 0.1884 | 0.2801 | 157.0782 |
| medium_read_heavy | rust | 100 | 0.53 | 18856 | 975 | 5.17 | [18663, 19050] | 0.0461 | 0.0743 | 0.1062 | 26.8994 |
| medium_read_heavy | python | 100 | 1.20 | 8556 | 1197 | 13.99 | [8319, 8794] | 0.0667 | 0.1250 | 0.2768 | 191.6357 |
| medium_read_heavy | java | 100 | 1.22 | 8302 | 672 | 8.09 | [8168, 8435] | 0.0881 | 0.1711 | 0.2668 | 145.9857 |
| medium_write_heavy | rust | 100 | 0.54 | 18486 | 1401 | 7.58 | [18208, 18764] | 0.0473 | 0.0765 | 0.1088 | 27.2664 |
| medium_write_heavy | python | 100 | 0.67 | 14980 | 569 | 3.80 | [14867, 15093] | 0.0524 | 0.0824 | 0.1133 | 87.0199 |
| medium_write_heavy | java | 100 | 1.20 | 8318 | 364 | 4.37 | [8245, 8390] | 0.0891 | 0.1706 | 0.2519 | 142.0981 |

### Workload small

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| small_mixed | rust | 100 | 0.08 | 13178 | 643 | 4.88 | [13050, 13305] | 0.0465 | 0.0672 | 0.0914 | 24.3138 |
| small_mixed | python | 100 | 0.16 | 6378 | 610 | 9.56 | [6257, 6499] | 0.0550 | 0.0854 | 0.1140 | 88.8499 |
| small_mixed | java | 100 | 0.30 | 3430 | 307 | 8.95 | [3369, 3491] | 0.1318 | 0.2446 | 0.3306 | 129.7720 |
| small_read_heavy | rust | 100 | 0.07 | 13470 | 433 | 3.22 | [13384, 13556] | 0.0456 | 0.0642 | 0.0892 | 23.7695 |
| small_read_heavy | python | 100 | 0.15 | 6633 | 439 | 6.63 | [6546, 6721] | 0.0538 | 0.0805 | 0.1057 | 83.3628 |
| small_read_heavy | java | 100 | 0.29 | 3502 | 223 | 6.36 | [3458, 3546] | 0.1271 | 0.2321 | 0.3106 | 128.7222 |
| small_write_heavy | rust | 100 | 0.08 | 12742 | 373 | 2.93 | [12668, 12816] | 0.0464 | 0.0726 | 0.1031 | 25.7823 |
| small_write_heavy | python | 100 | 0.15 | 6512 | 286 | 4.39 | [6456, 6569] | 0.0539 | 0.0854 | 0.1157 | 84.4000 |
| small_write_heavy | java | 100 | 0.31 | 3190 | 188 | 5.90 | [3153, 3228] | 0.1384 | 0.2588 | 0.3654 | 140.8977 |

## Risultati Memoria

Ogni riga riporta la media di 3 run per lo stesso linguaggio e workload. Per ogni workload, i linguaggi sono ordinati dal picco RSS medio più basso al più alto. La deviazione standard indica la stabilità della misura RSS tra run.

### Workload large

| Workload | Linguaggio | Run | Comandi | RSS iniziale medio (MB) | Dev. std iniziale | RSS picco medio (MB) | Dev. std picco |
|---|---|---:|---:|---:|---:|---:|---:|
| large_mixed | rust | 3 | 100000 | 3.76 | 0.01 | 6.89 | 0.01 |
| large_mixed | python | 3 | 100000 | 13.26 | 0.10 | 16.86 | 0.38 |
| large_mixed | java | 3 | 100000 | 35.04 | 0.65 | 115.61 | 1.33 |
| large_read_heavy | rust | 3 | 100000 | 3.77 | 0.00 | 5.34 | 0.00 |
| large_read_heavy | python | 3 | 100000 | 13.12 | 0.03 | 14.52 | 0.04 |
| large_read_heavy | java | 3 | 100000 | 35.15 | 0.73 | 114.79 | 0.63 |
| large_write_heavy | rust | 3 | 100000 | 3.77 | 0.01 | 5.36 | 0.01 |
| large_write_heavy | python | 3 | 100000 | 13.16 | 0.14 | 14.39 | 0.14 |
| large_write_heavy | java | 3 | 100000 | 35.52 | 0.22 | 113.48 | 0.14 |

### Workload medium

| Workload | Linguaggio | Run | Comandi | RSS iniziale medio (MB) | Dev. std iniziale | RSS picco medio (MB) | Dev. std picco |
|---|---|---:|---:|---:|---:|---:|---:|
| medium_mixed | rust | 3 | 10000 | 3.77 | 0.01 | 4.14 | 0.07 |
| medium_mixed | python | 3 | 10000 | 13.17 | 0.11 | 13.55 | 0.11 |
| medium_mixed | java | 3 | 10000 | 35.18 | 0.60 | 74.82 | 1.18 |
| medium_read_heavy | rust | 3 | 10000 | 3.77 | 0.00 | 4.00 | 0.00 |
| medium_read_heavy | python | 3 | 10000 | 13.26 | 0.09 | 13.44 | 0.10 |
| medium_read_heavy | java | 3 | 10000 | 35.48 | 0.07 | 76.06 | 7.17 |
| medium_write_heavy | rust | 3 | 10000 | 3.77 | 0.00 | 3.93 | 0.00 |
| medium_write_heavy | python | 3 | 10000 | 13.26 | 0.08 | 13.42 | 0.08 |
| medium_write_heavy | java | 3 | 10000 | 35.45 | 0.04 | 78.50 | 3.98 |

### Workload small

| Workload | Linguaggio | Run | Comandi | RSS iniziale medio (MB) | Dev. std iniziale | RSS picco medio (MB) | Dev. std picco |
|---|---|---:|---:|---:|---:|---:|---:|
| small_mixed | rust | 3 | 1000 | 3.77 | 0.00 | 3.81 | 0.00 |
| small_mixed | python | 3 | 1000 | 13.21 | 0.15 | 13.25 | 0.15 |
| small_mixed | java | 3 | 1000 | 35.10 | 0.65 | 41.29 | 0.02 |
| small_read_heavy | rust | 3 | 1000 | 3.77 | 0.01 | 3.80 | 0.01 |
| small_read_heavy | python | 3 | 1000 | 13.15 | 0.16 | 13.17 | 0.16 |
| small_read_heavy | java | 3 | 1000 | 35.28 | 0.65 | 41.32 | 0.12 |
| small_write_heavy | rust | 3 | 1000 | 3.76 | 0.00 | 3.79 | 0.00 |
| small_write_heavy | python | 3 | 1000 | 13.19 | 0.14 | 13.20 | 0.14 |
| small_write_heavy | java | 3 | 1000 | 35.56 | 0.24 | 41.10 | 0.24 |

## Note

- Rust ottiene il throughput medio più alto in tutti i workload. Il vantaggio è particolarmente evidente sui workload `large`, dove supera stabilmente Python e Java sia nei workload misti sia in quelli read-heavy e write-heavy.
- Python si colloca generalmente al secondo posto per throughput nei workload `large` e nella maggior parte dei workload `medium` e `small`. L'eccezione più evidente è `medium_read_heavy`, dove Java e Python hanno throughput medi molto vicini, ma Java risulta leggermente inferiore nel valore medio aggregato.
- Java mostra il throughput medio più basso nella maggior parte dei workload, soprattutto nei workload piccoli e medi. Questo risultato è coerente con il fatto che il benchmark misura il programma completo da CLI, includendo avvio del processo, parsing testuale e I/O su `stdin`/`stdout`, non solo l'esecuzione interna delle operazioni.
- Gli intervalli di confidenza al 95% sono relativamente stretti nella maggior parte dei workload grazie alle 100 run. Questo rende più solido il confronto tra linguaggi: ad esempio, nei workload `large` gli intervalli di Rust, Python e Java rimangono separati, quindi l'ordinamento osservato non dipende da una singola run rumorosa.
- Il coefficiente di variazione aiuta a individuare i casi meno stabili. Alcuni workload, come `medium_read_heavy` per Python e `large_mixed` per Java, mostrano una variabilità più alta rispetto agli altri casi; in questi punti il sistema è più sensibile al rumore di esecuzione o al costo dell'I/O.
- La latenza massima media (`Max medio`) è più alta per Python e Java rispetto a Rust in diversi workload. Questo indica che, oltre alla prestazione media, Rust tende ad avere code di latenza più contenute in questa configurazione sperimentale.
- Per la memoria, Rust mantiene il picco RSS medio più basso in tutti i workload. Python si colloca in una fascia intermedia, mentre Java mostra il picco RSS medio più alto, in particolare sui workload `large` e `medium`.
- Le deviazioni standard della memoria sono generalmente basse, soprattutto per Rust e Python. Questo indica che le misure RSS sono stabili tra le 3 run. Java presenta qualche oscillazione più visibile nei workload medi, ma l'ordine complessivo tra i linguaggi rimane invariato.
- Gli intervalli di confidenza sono stati calcolati solo per il throughput, perché è la metrica maggiormente soggetta a variabilità temporale. Per la memoria RSS sono riportate media e deviazione standard, sufficienti a mostrare stabilità e riproducibilità della misura.
- I file CSV dentro `benchmark/results/examples/` sono pensati come snapshot di documentazione e non dovrebbero essere sovrascritti durante le normali esecuzioni del benchmark.
