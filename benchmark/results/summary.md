# Benchmark Results Summary

Questa sintesi riporta due esecuzioni finali del runner sui workload mixed, read-heavy e write-heavy:

- throughput senza campionamento memoria:
  `java -cp benchmark\out BenchmarkRunner --runs 5 --rss-sample-every 0 --no-generate-workloads`
- benchmark end-to-end con campionamento RSS:
  `java -cp benchmark\out BenchmarkRunner --runs 3 --no-generate-workloads`

I CSV completi sono:

- `benchmark/results/results_no_rss.csv`
- `benchmark/results/results_with_rss.csv`

## Throughput Senza RSS

Questa run disattiva il campionamento memoria. E' la misura più utile per confrontare il throughput delle tre CLI senza il costo aggiuntivo di PowerShell su Windows.

### Large Workloads

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms |
|---|---:|---:|---:|---:|---:|---:|---:|
| large_mixed | rust | 5 | 19069.91 | 5.270 | 0.047 | 0.081 | 0.118 |
| large_mixed | python | 5 | 17221.13 | 5.892 | 0.051 | 0.089 | 0.123 |
| large_mixed | java | 5 | 13734.54 | 7.326 | 0.063 | 0.115 | 0.168 |
| large_read_heavy | rust | 5 | 18971.28 | 5.291 | 0.047 | 0.080 | 0.115 |
| large_read_heavy | python | 5 | 17119.64 | 5.846 | 0.053 | 0.085 | 0.116 |
| large_read_heavy | java | 5 | 14165.47 | 7.065 | 0.060 | 0.112 | 0.162 |
| large_write_heavy | rust | 5 | 19998.28 | 5.011 | 0.045 | 0.075 | 0.106 |
| large_write_heavy | python | 5 | 16717.05 | 5.989 | 0.054 | 0.087 | 0.121 |
| large_write_heavy | java | 5 | 14233.53 | 7.030 | 0.060 | 0.110 | 0.161 |

### Medium Workloads

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms |
|---|---:|---:|---:|---:|---:|---:|---:|
| medium_mixed | rust | 5 | 18533.15 | 0.540 | 0.046 | 0.077 | 0.112 |
| medium_mixed | python | 5 | 14458.59 | 0.692 | 0.054 | 0.087 | 0.123 |
| medium_mixed | java | 5 | 8230.77 | 1.217 | 0.090 | 0.172 | 0.253 |
| medium_read_heavy | rust | 5 | 18335.60 | 0.546 | 0.047 | 0.078 | 0.115 |
| medium_read_heavy | python | 5 | 14142.34 | 0.710 | 0.055 | 0.091 | 0.128 |
| medium_read_heavy | java | 5 | 8490.11 | 1.180 | 0.087 | 0.167 | 0.249 |
| medium_write_heavy | rust | 5 | 18634.59 | 0.537 | 0.046 | 0.076 | 0.112 |
| medium_write_heavy | python | 5 | 13881.13 | 0.721 | 0.056 | 0.091 | 0.127 |
| medium_write_heavy | java | 5 | 8368.12 | 1.198 | 0.090 | 0.170 | 0.250 |

### Small Workloads

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms |
|---|---:|---:|---:|---:|---:|---:|---:|
| small_mixed | rust | 5 | 10272.53 | 0.098 | 0.056 | 0.093 | 0.129 |
| small_mixed | python | 5 | 6033.28 | 0.166 | 0.057 | 0.098 | 0.135 |
| small_mixed | java | 5 | 2764.54 | 0.363 | 0.170 | 0.311 | 0.423 |
| small_read_heavy | rust | 5 | 10393.52 | 0.096 | 0.055 | 0.094 | 0.150 |
| small_read_heavy | python | 5 | 5163.91 | 0.194 | 0.065 | 0.108 | 0.153 |
| small_read_heavy | java | 5 | 2718.89 | 0.368 | 0.162 | 0.304 | 0.458 |
| small_write_heavy | rust | 5 | 9878.04 | 0.102 | 0.058 | 0.099 | 0.150 |
| small_write_heavy | python | 5 | 4548.97 | 0.221 | 0.077 | 0.126 | 0.183 |
| small_write_heavy | java | 5 | 2686.94 | 0.373 | 0.168 | 0.308 | 0.436 |

## Run Con RSS

Questa run mantiene il campionamento memoria attivo. I valori di throughput sono molto più bassi perché su Windows il runner usa PowerShell per leggere il `WorkingSet64` dei processi.

### Large Workloads

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms | avg RSS peak MB |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| large_mixed | rust | 3 | 1771.63 | 56.465 | 0.046 | 0.081 | 0.118 | 6.55 |
| large_mixed | python | 3 | 1745.34 | 57.314 | 0.056 | 0.095 | 0.134 | 16.49 |
| large_mixed | java | 3 | 1668.07 | 60.009 | 0.062 | 0.116 | 0.178 | 115.50 |
| large_read_heavy | python | 3 | 1785.70 | 56.011 | 0.053 | 0.089 | 0.124 | 14.27 |
| large_read_heavy | java | 3 | 1731.92 | 57.745 | 0.060 | 0.110 | 0.163 | 113.81 |
| large_read_heavy | rust | 3 | 1689.20 | 59.259 | 0.065 | 0.108 | 0.146 | 5.00 |
| large_write_heavy | rust | 3 | 1780.16 | 56.197 | 0.046 | 0.080 | 0.117 | 5.08 |
| large_write_heavy | python | 3 | 1777.61 | 56.257 | 0.053 | 0.091 | 0.128 | 14.12 |
| large_write_heavy | java | 3 | 1739.84 | 57.486 | 0.061 | 0.111 | 0.167 | 112.93 |

### Medium Workloads

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms | avg RSS peak MB |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| medium_mixed | python | 3 | 1698.85 | 5.889 | 0.057 | 0.095 | 0.133 | 13.22 |
| medium_mixed | rust | 3 | 1654.22 | 6.096 | 0.052 | 0.091 | 0.133 | 3.85 |
| medium_mixed | java | 3 | 1519.66 | 6.600 | 0.089 | 0.177 | 0.269 | 72.40 |
| medium_read_heavy | python | 3 | 1736.60 | 5.761 | 0.055 | 0.093 | 0.129 | 13.06 |
| medium_read_heavy | rust | 3 | 1718.52 | 5.839 | 0.047 | 0.081 | 0.120 | 3.71 |
| medium_read_heavy | java | 3 | 1566.30 | 6.424 | 0.083 | 0.175 | 0.274 | 70.30 |
| medium_write_heavy | rust | 3 | 1767.50 | 5.658 | 0.046 | 0.080 | 0.118 | 3.64 |
| medium_write_heavy | python | 3 | 1746.32 | 5.728 | 0.055 | 0.092 | 0.131 | 13.07 |
| medium_write_heavy | java | 3 | 1640.92 | 6.096 | 0.078 | 0.158 | 0.233 | 76.40 |

### Small Workloads

| workload | language | runs | avg cmd/s | avg time s | avg p50 ms | avg p95 ms | avg p99 ms | avg RSS peak MB |
|---|---:|---:|---:|---:|---:|---:|---:|---:|
| small_mixed | rust | 3 | 1791.85 | 0.559 | 0.045 | 0.076 | 0.107 | 3.53 |
| small_mixed | python | 3 | 1731.00 | 0.579 | 0.057 | 0.099 | 0.142 | 12.91 |
| small_mixed | java | 3 | 1348.31 | 0.749 | 0.151 | 0.280 | 0.369 | 40.85 |
| small_read_heavy | rust | 3 | 1664.43 | 0.601 | 0.052 | 0.093 | 0.134 | 3.51 |
| small_read_heavy | python | 3 | 1662.75 | 0.602 | 0.059 | 0.099 | 0.138 | 12.90 |
| small_read_heavy | java | 3 | 1460.87 | 0.685 | 0.139 | 0.268 | 0.357 | 40.69 |
| small_write_heavy | rust | 3 | 1769.33 | 0.566 | 0.046 | 0.079 | 0.110 | 3.51 |
| small_write_heavy | python | 3 | 1712.05 | 0.585 | 0.056 | 0.095 | 0.136 | 12.88 |
| small_write_heavy | java | 3 | 1455.26 | 0.687 | 0.142 | 0.264 | 0.353 | 40.98 |

## Interpretazione

Senza RSS, Rust risulta il più veloce su tutti i workload; Python resta competitivo soprattutto sui workload grandi, mentre Java è penalizzato dal costo della JVM nel benchmark end-to-end da CLI.

Con RSS attiva, le differenze di throughput si riducono molto perché il costo del campionamento memoria entra nel tempo totale. Questi numeri vanno quindi usati soprattutto per osservare i picchi di memoria, non per confrontare il throughput puro.

La memoria osservata è coerente con le aspettative: Rust usa meno memoria, Python rimane su valori intermedi e Java ha il picco RSS più alto a causa del runtime JVM.

Questi risultati misurano il comportamento end-to-end delle CLI, quindi includono stdin/stdout, parsing testuale, stampa delle risposte e gestione del processo. Non sono una misura isolata del solo core in memoria del database.
