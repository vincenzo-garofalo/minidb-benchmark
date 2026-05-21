# Riepilogo dei Benchmark

A partire dai file CSV di esempio stabili:

- `benchmark/results/throughput_results_example.csv`
- `benchmark/results/memory_results_example.csv`

Comandi utilizzati:

```powershell
java -cp benchmark\out BenchmarkRunner --mode throughput --runs 5 --no-generate-workloads
java -cp benchmark\out BenchmarkRunner --mode memory --no-generate-workloads
```

Throughput e memoria sono misurati in esecuzioni separate. La modalità throughput disabilita il campionamento RSS per evitare overhead sulla misurazione delle prestazioni; la modalità memory misura solo i valori RSS iniziali e di picco.

## Risultati Throughput

Ogni riga riporta la media di 5 run per lo stesso linguaggio e workload. Per ogni workload, i linguaggi sono ordinati dal throughput medio più alto al più basso.

### Workload large

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|
| large_mixed | rust | 5 | 5.39 | 18654 | 0.0474 | 0.0851 | 0.1262 |
| large_mixed | python | 5 | 5.79 | 17322 | 0.0511 | 0.0862 | 0.1203 |
| large_mixed | java | 5 | 7.24 | 13850 | 0.0609 | 0.1163 | 0.1735 |
| large_read_heavy | rust | 5 | 5.09 | 19765 | 0.0451 | 0.0777 | 0.1147 |
| large_read_heavy | python | 5 | 5.81 | 17211 | 0.0519 | 0.0854 | 0.1192 |
| large_read_heavy | java | 5 | 7.16 | 14015 | 0.0609 | 0.1142 | 0.1700 |
| large_write_heavy | rust | 5 | 5.00 | 20015 | 0.0448 | 0.0770 | 0.1121 |
| large_write_heavy | python | 5 | 5.81 | 17209 | 0.0520 | 0.0849 | 0.1181 |
| large_write_heavy | java | 5 | 7.00 | 14292 | 0.0597 | 0.1107 | 0.1629 |

### Workload medium

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|
| medium_mixed | rust | 5 | 0.55 | 18215 | 0.0467 | 0.0811 | 0.1183 |
| medium_mixed | python | 5 | 0.69 | 14421 | 0.0535 | 0.0894 | 0.1249 |
| medium_mixed | java | 5 | 1.24 | 8069 | 0.0901 | 0.1769 | 0.2627 |
| medium_read_heavy | rust | 5 | 0.57 | 17697 | 0.0476 | 0.0829 | 0.1223 |
| medium_read_heavy | python | 5 | 0.70 | 14379 | 0.0532 | 0.0890 | 0.1264 |
| medium_read_heavy | java | 5 | 1.21 | 8316 | 0.0895 | 0.1710 | 0.2569 |
| medium_write_heavy | rust | 5 | 0.58 | 17606 | 0.0492 | 0.0836 | 0.1263 |
| medium_write_heavy | python | 5 | 0.70 | 14338 | 0.0539 | 0.0893 | 0.1247 |
| medium_write_heavy | java | 5 | 1.23 | 8153 | 0.0895 | 0.1755 | 0.2672 |

### Workload small

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|
| small_mixed | rust | 5 | 0.11 | 9312 | 0.0615 | 0.1044 | 0.1590 |
| small_mixed | python | 5 | 0.27 | 3934 | 0.0879 | 0.1551 | 0.2418 |
| small_mixed | java | 5 | 0.49 | 2090 | 0.1983 | 0.4151 | 0.7958 |
| small_read_heavy | rust | 5 | 0.11 | 9415 | 0.0631 | 0.1124 | 0.1647 |
| small_read_heavy | python | 5 | 0.20 | 5096 | 0.0654 | 0.1086 | 0.1472 |
| small_read_heavy | java | 5 | 0.36 | 2795 | 0.1585 | 0.2880 | 0.4559 |
| small_write_heavy | rust | 5 | 0.09 | 10914 | 0.0510 | 0.0916 | 0.1500 |
| small_write_heavy | python | 5 | 0.22 | 4523 | 0.0761 | 0.1287 | 0.1901 |
| small_write_heavy | java | 5 | 0.38 | 2665 | 0.1674 | 0.3081 | 0.4934 |

## Risultati Memoria

Ogni riga riporta una singola esecuzione orientata alla memoria per lo stesso linguaggio e workload. Per ogni workload, i linguaggi sono ordinati dal picco RSS più basso al più alto.

### Workload large

| Workload | Linguaggio | Comandi | RSS iniziale (MB) | RSS picco (MB) |
|---|---|---:|---:|---:|
| large_mixed | rust | 100000 | 3.68 | 6.80 |
| large_mixed | python | 100000 | 13.16 | 15.93 |
| large_mixed | java | 100000 | 35.30 | 114.56 |
| large_read_heavy | rust | 100000 | 3.68 | 5.25 |
| large_read_heavy | python | 100000 | 13.16 | 14.59 |
| large_read_heavy | java | 100000 | 35.23 | 113.91 |
| large_write_heavy | rust | 100000 | 3.68 | 5.28 |
| large_write_heavy | python | 100000 | 13.25 | 14.47 |
| large_write_heavy | java | 100000 | 35.32 | 112.89 |

### Workload medium

| Workload | Linguaggio | Comandi | RSS iniziale (MB) | RSS picco (MB) |
|---|---|---:|---:|---:|
| medium_mixed | rust | 10000 | 3.66 | 4.08 |
| medium_mixed | python | 10000 | 13.17 | 13.56 |
| medium_mixed | java | 10000 | 35.42 | 72.61 |
| medium_read_heavy | rust | 10000 | 3.68 | 3.91 |
| medium_read_heavy | python | 10000 | 13.30 | 13.48 |
| medium_read_heavy | java | 10000 | 35.34 | 77.58 |
| medium_write_heavy | rust | 10000 | 3.68 | 3.84 |
| medium_write_heavy | python | 10000 | 13.28 | 13.45 |
| medium_write_heavy | java | 10000 | 35.26 | 68.83 |

### Workload small

| Workload | Linguaggio | Comandi | RSS iniziale (MB) | RSS picco (MB) |
|---|---|---:|---:|---:|
| small_mixed | rust | 1000 | 3.68 | 3.73 |
| small_mixed | python | 1000 | 13.16 | 13.20 |
| small_mixed | java | 1000 | 34.46 | 40.88 |
| small_read_heavy | rust | 1000 | 3.69 | 3.71 |
| small_read_heavy | python | 1000 | 13.19 | 13.20 |
| small_read_heavy | java | 1000 | 35.48 | 41.43 |
| small_write_heavy | rust | 1000 | 3.68 | 3.71 |
| small_write_heavy | python | 1000 | 13.01 | 13.02 |
| small_write_heavy | java | 1000 | 35.34 | 41.38 |

## Note

- Rust ottiene il throughput medio più alto in ogni gruppo di workload in questa esecuzione.
- Java mostra il picco RSS più alto in ogni gruppo di workload in questa esecuzione.
- Python si colloca tra Rust e Java per uso di memoria RSS, mentre il throughput è generalmente inferiore a Rust e superiore a Java sui workload più grandi.
- I file CSV con `_example` nel nome sono pensati come snapshot di documentazione e non dovrebbero essere sovrascritti durante le normali esecuzioni del benchmark.
