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
| large_mixed | rust | 100 | 5.14 | 19502 | 733 | 3.76 | [19356, 19647] | 0.0471 | 0.0740 | 0.1089 | 25.2044 |
| large_mixed | python | 100 | 6.51 | 15407 | 855 | 5.55 | [15237, 15576] | 0.0579 | 0.0965 | 0.1395 | 92.6484 |
| large_mixed | java | 100 | 7.69 | 13019 | 467 | 3.59 | [12926, 13112] | 0.0652 | 0.1240 | 0.1810 | 139.1746 |
| large_read_heavy | rust | 100 | 5.28 | 18953 | 639 | 3.37 | [18826, 19079] | 0.0476 | 0.0806 | 0.1159 | 27.9562 |
| large_read_heavy | python | 100 | 6.30 | 15937 | 1038 | 6.51 | [15731, 16143] | 0.0561 | 0.0941 | 0.1331 | 92.7344 |
| large_read_heavy | java | 100 | 7.62 | 13147 | 585 | 4.45 | [13031, 13264] | 0.0650 | 0.1219 | 0.1771 | 140.1888 |
| large_write_heavy | rust | 100 | 5.18 | 19361 | 841 | 4.34 | [19194, 19528] | 0.0469 | 0.0777 | 0.1148 | 26.2576 |
| large_write_heavy | python | 100 | 6.42 | 15628 | 812 | 5.19 | [15467, 15790] | 0.0571 | 0.0970 | 0.1384 | 95.3920 |
| large_write_heavy | java | 100 | 7.59 | 13217 | 725 | 5.49 | [13073, 13360] | 0.0649 | 0.1218 | 0.1775 | 136.1325 |

### Workload medium

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| medium_mixed | rust | 100 | 0.60 | 17020 | 1784 | 10.48 | [16666, 17374] | 0.0507 | 0.0875 | 0.1324 | 28.9631 |
| medium_mixed | python | 100 | 0.71 | 14218 | 1429 | 10.05 | [13934, 14501] | 0.0547 | 0.0927 | 0.1320 | 91.3834 |
| medium_mixed | java | 100 | 1.34 | 7610 | 824 | 10.83 | [7446, 7773] | 0.0984 | 0.1926 | 0.2947 | 153.8064 |
| medium_read_heavy | rust | 100 | 0.59 | 17026 | 959 | 5.64 | [16836, 17217] | 0.0501 | 0.0879 | 0.1285 | 28.0962 |
| medium_read_heavy | python | 100 | 0.84 | 12118 | 1352 | 11.16 | [11850, 12387] | 0.0656 | 0.1080 | 0.1525 | 103.5173 |
| medium_read_heavy | java | 100 | 1.30 | 7735 | 397 | 5.13 | [7657, 7814] | 0.0962 | 0.1874 | 0.2724 | 151.8380 |
| medium_write_heavy | rust | 100 | 0.62 | 16413 | 1739 | 10.60 | [16068, 16758] | 0.0524 | 0.0926 | 0.1371 | 30.5398 |
| medium_write_heavy | python | 100 | 0.81 | 12644 | 1755 | 13.88 | [12295, 12992] | 0.0620 | 0.1095 | 0.1627 | 106.0941 |
| medium_write_heavy | java | 100 | 1.30 | 7724 | 596 | 7.72 | [7605, 7842] | 0.0962 | 0.1910 | 0.2818 | 153.3455 |

### Workload small

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| small_mixed | rust | 100 | 0.10 | 10185 | 1082 | 10.63 | [9971, 10400] | 0.0585 | 0.0961 | 0.1392 | 32.1116 |
| small_mixed | python | 100 | 0.20 | 4998 | 620 | 12.41 | [4875, 5121] | 0.0704 | 0.1163 | 0.1865 | 110.9085 |
| small_mixed | java | 100 | 0.35 | 2839 | 222 | 7.82 | [2795, 2883] | 0.1525 | 0.2769 | 0.3915 | 147.8119 |
| small_read_heavy | rust | 100 | 0.10 | 10292 | 420 | 4.08 | [10209, 10375] | 0.0567 | 0.0955 | 0.1397 | 31.3295 |
| small_read_heavy | python | 100 | 0.17 | 5908 | 602 | 10.19 | [5789, 6028] | 0.0567 | 0.0982 | 0.1431 | 94.2813 |
| small_read_heavy | java | 100 | 0.36 | 2814 | 296 | 10.51 | [2755, 2873] | 0.1626 | 0.2961 | 0.4342 | 160.0529 |
| small_write_heavy | rust | 100 | 0.10 | 10560 | 568 | 5.38 | [10447, 10673] | 0.0551 | 0.0938 | 0.1374 | 30.8934 |
| small_write_heavy | python | 100 | 0.16 | 6160 | 607 | 9.85 | [6040, 6281] | 0.0524 | 0.0924 | 0.1336 | 92.5317 |
| small_write_heavy | java | 100 | 0.35 | 2880 | 256 | 8.91 | [2829, 2931] | 0.1584 | 0.2896 | 0.4290 | 155.0012 |

## Risultati Memoria

Ogni riga riporta la media di 3 run per lo stesso linguaggio e workload. Per ogni workload, i linguaggi sono ordinati dal picco RSS medio più basso al più alto.

### Workload large

| Workload | Linguaggio | Run | RSS iniziale medio (MB) | Dev. std iniziale (MB) | RSS picco medio (MB) | Dev. std picco (MB) |
|---|---|---:|---:|---:|---:|---:|
| large_mixed | rust | 3 | 3.51 | 0.00 | 7.08 | 0.00 |
| large_mixed | python | 3 | 13.02 | 0.08 | 17.57 | 0.15 |
| large_mixed | java | 3 | 35.13 | 0.00 | 118.14 | 0.97 |
| large_read_heavy | rust | 3 | 3.50 | 0.00 | 5.45 | 0.00 |
| large_read_heavy | python | 3 | 13.06 | 0.05 | 15.39 | 0.14 |
| large_read_heavy | java | 3 | 35.28 | 0.09 | 116.22 | 0.11 |
| large_write_heavy | rust | 3 | 3.50 | 0.01 | 4.77 | 0.01 |
| large_write_heavy | python | 3 | 12.96 | 0.13 | 14.43 | 0.12 |
| large_write_heavy | java | 3 | 34.86 | 0.58 | 115.81 | 0.13 |

### Workload medium

| Workload | Linguaggio | Run | RSS iniziale medio (MB) | Dev. std iniziale (MB) | RSS picco medio (MB) | Dev. std picco (MB) |
|---|---|---:|---:|---:|---:|---:|
| medium_mixed | rust | 3 | 3.51 | 0.00 | 3.96 | 0.06 |
| medium_mixed | python | 3 | 13.01 | 0.13 | 13.42 | 0.13 |
| medium_mixed | java | 3 | 35.29 | 0.15 | 77.64 | 2.54 |
| medium_read_heavy | rust | 3 | 3.51 | 0.01 | 3.82 | 0.01 |
| medium_read_heavy | python | 3 | 13.08 | 0.03 | 13.38 | 0.02 |
| medium_read_heavy | java | 3 | 35.28 | 0.25 | 75.82 | 3.87 |
| medium_write_heavy | rust | 3 | 3.51 | 0.00 | 3.73 | 0.00 |
| medium_write_heavy | python | 3 | 12.88 | 0.04 | 13.10 | 0.05 |
| medium_write_heavy | java | 3 | 35.19 | 0.09 | 76.01 | 1.78 |

### Workload small

| Workload | Linguaggio | Run | RSS iniziale medio (MB) | Dev. std iniziale (MB) | RSS picco medio (MB) | Dev. std picco (MB) |
|---|---|---:|---:|---:|---:|---:|
| small_mixed | rust | 3 | 3.51 | 0.00 | 3.60 | 0.00 |
| small_mixed | python | 3 | 12.95 | 0.10 | 12.99 | 0.10 |
| small_mixed | java | 3 | 35.30 | 0.19 | 41.17 | 0.00 |
| small_read_heavy | rust | 3 | 3.51 | 0.00 | 3.56 | 0.00 |
| small_read_heavy | python | 3 | 13.00 | 0.10 | 13.02 | 0.10 |
| small_read_heavy | java | 3 | 34.90 | 0.78 | 40.93 | 0.08 |
| small_write_heavy | rust | 3 | 3.51 | 0.00 | 3.54 | 0.00 |
| small_write_heavy | python | 3 | 13.05 | 0.08 | 13.07 | 0.08 |
| small_write_heavy | java | 3 | 35.23 | 0.03 | 41.26 | 0.46 |

## Note Interpretative

- Rust ottiene il throughput medio più alto in tutti i 9 workload. Gli intervalli di confidenza dei workload large sono stretti e separati, quindi il confronto è statisticamente solido soprattutto sulle dimensioni maggiori.
- Nei workload large Python è generalmente secondo e Java terzo. Anche in `large_read_heavy` Python resta sopra Java con un CV del 6.51%, quindi l'ordinamento è stabile.
- Tutti i coefficienti di variabilità del throughput sono sotto la soglia del 15%. Questo rende il dataset presentabile senza casi critici di instabilità statistica.
- I workload small restano più sensibili al rumore di sistema perché il tempo assoluto di esecuzione è molto breve. Questo rende naturale trovare CV più alti rispetto ai workload large, pur restando entro una soglia accettabile.
- I casi tra 10% e 15% vanno interpretati con cautela metodologica: `medium_write_heavy` python 13.88%, `small_mixed` python 12.41%, `medium_read_heavy` python 11.16%, `medium_mixed` java 10.83%, `small_mixed` rust 10.63%, `medium_write_heavy` rust 10.60%, `small_read_heavy` java 10.51%, `medium_mixed` rust 10.48%, `small_read_heavy` python 10.19%, `medium_mixed` python 10.05%.
- Le misure di memoria sono molto stabili: le deviazioni standard sono basse e Rust mantiene il picco RSS medio più basso in tutti i workload.
- Java mostra un consumo di memoria sensibilmente più alto, coerente con l'overhead della JVM, mentre Python si colloca tra Rust e Java in tutti i workload.
