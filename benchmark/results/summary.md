# Benchmark Results Summary

Questa sintesi riporta due esecuzioni finali del runner:

- throughput senza campionamento memoria:
  `java -cp benchmark\out BenchmarkRunner --runs 5 --rss-sample-every 0`
- benchmark end-to-end con campionamento RSS:
  `java -cp benchmark\out BenchmarkRunner --runs 3`

I CSV completi sono:

- `benchmark/results/results_no_rss.csv`
- `benchmark/results/results_with_rss.csv`

## Throughput Senza RSS

Questa run disattiva il campionamento memoria. E' la misura più utile per
confrontare il throughput delle tre CLI senza il costo aggiuntivo di PowerShell
su Windows.

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms |
|---|---:|---:|---:|---:|---:|---:|---:|
| large_mixed | rust | 5 | 18965.61 | 5.289 | 0.047 | 0.083 | 0.119 |
| large_mixed | python | 5 | 16044.97 | 6.275 | 0.054 | 0.102 | 0.140 |
| large_mixed | java | 5 | 12742.49 | 7.960 | 0.068 | 0.129 | 0.193 |
| medium_mixed | rust | 5 | 17263.07 | 0.583 | 0.049 | 0.086 | 0.129 |
| medium_mixed | python | 5 | 13972.28 | 0.716 | 0.055 | 0.093 | 0.134 |
| medium_mixed | java | 5 | 8395.76 | 1.192 | 0.088 | 0.167 | 0.241 |
| small_mixed | rust | 5 | 10885.22 | 0.092 | 0.052 | 0.093 | 0.138 |
| small_mixed | python | 5 | 5946.90 | 0.169 | 0.056 | 0.095 | 0.132 |
| small_mixed | java | 5 | 2893.99 | 0.346 | 0.153 | 0.284 | 0.413 |

## Run Con RSS

Questa run mantiene il campionamento memoria attivo. I valori di throughput sono
molto più bassi perché su Windows il runner usa PowerShell per leggere il
`WorkingSet64` dei processi.

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms | avg RSS peak MB |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| large_mixed | rust | 3 | 1689.84 | 59.285 | 0.047 | 0.086 | 0.126 | 6.51 |
| large_mixed | python | 3 | 1608.84 | 62.173 | 0.057 | 0.105 | 0.156 | 16.55 |
| large_mixed | java | 3 | 1533.46 | 65.437 | 0.068 | 0.129 | 0.198 | 113.32 |
| medium_mixed | java | 3 | 1597.56 | 6.260 | 0.080 | 0.177 | 0.269 | 72.04 |
| medium_mixed | rust | 3 | 1581.49 | 6.328 | 0.049 | 0.089 | 0.132 | 3.85 |
| medium_mixed | python | 3 | 1533.59 | 6.558 | 0.063 | 0.110 | 0.160 | 13.25 |
| small_mixed | rust | 3 | 1617.14 | 0.623 | 0.045 | 0.081 | 0.132 | 3.50 |
| small_mixed | python | 3 | 1401.60 | 0.716 | 0.060 | 0.103 | 0.145 | 12.83 |
| small_mixed | java | 3 | 1192.23 | 0.859 | 0.178 | 0.348 | 0.543 | 40.68 |

## Interpretazione

Senza RSS, Rust risulta il più veloce su tutti i workload, Python secondo e Java
terzo. Con RSS attiva, le differenze di throughput si riducono molto perché il
costo del campionamento memoria entra nel tempo totale.

La memoria osservata è coerente con le aspettative: Rust usa meno memoria,
Python rimane su valori intermedi, Java ha il picco RSS più alto a causa del
runtime JVM.

Questi risultati misurano il comportamento end-to-end delle CLI, quindi includono
stdin/stdout, parsing testuale, stampa delle risposte e gestione del processo.
Non sono una misura isolata del solo core in memoria del database.
