# Riepilogo dei Benchmark

A partire dai file CSV di esempio stabili:

- `benchmark/results/examples/throughput_results_example.csv`
- `benchmark/results/examples/throughput_summary_example.csv`
- `benchmark/results/examples/memory_results_example.csv`
- `benchmark/results/examples/memory_summary_example.csv`

Comandi utilizzati:

```powershell
javac -d benchmark\out benchmark\src\BenchmarkRunner.java
java -cp benchmark\out BenchmarkRunner --mode throughput --runs 100 --workload <workload>.txt --no-generate-workloads
java -cp benchmark\out BenchmarkRunner --mode memory --runs 3 --workload <workload>.txt --no-generate-workloads
```

La simulazione è stata spezzata per workload per evitare timeout lunghi. I CSV intermedi sono stati ricombinati mantenendo una sola intestazione per file. Throughput e memoria sono misurati in esecuzioni separate: la modalità throughput disabilita il campionamento RSS, mentre la modalità memory misura RSS iniziale e picco su 3 run.

## Risultati Throughput

Ogni riga riporta la media di 100 run per lo stesso linguaggio e workload. Per ogni workload, i linguaggi sono ordinati dal throughput medio più alto al più basso. L'intervallo di confidenza al 95% è calcolato sul throughput medio.

### Workload large

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| large_high_cardinality | rust | 100 | 4.45 | 22532 | 833 | 3.70 | [22367, 22697] | 0.0415 | 0.0588 | 0.0864 | 24.1609 |
| large_high_cardinality | python | 100 | 5.43 | 18534 | 1397 | 7.53 | [18257, 18811] | 0.0489 | 0.0746 | 0.1041 | 82.1817 |
| large_high_cardinality | java | 100 | 6.27 | 15963 | 602 | 3.77 | [15844, 16082] | 0.0552 | 0.0935 | 0.1331 | 111.9525 |
| large_hot_keys | rust | 100 | 4.38 | 22899 | 1059 | 4.62 | [22688, 23109] | 0.0411 | 0.0577 | 0.0850 | 24.3231 |
| large_hot_keys | python | 100 | 5.45 | 18499 | 1567 | 8.47 | [18188, 18810] | 0.0501 | 0.0733 | 0.1020 | 81.8333 |
| large_hot_keys | java | 100 | 6.26 | 15999 | 575 | 3.59 | [15885, 16113] | 0.0552 | 0.0941 | 0.1339 | 112.6708 |
| large_mixed | rust | 100 | 4.42 | 22742 | 1599 | 7.03 | [22425, 23060] | 0.0417 | 0.0575 | 0.0797 | 23.1276 |
| large_mixed | python | 100 | 5.34 | 18823 | 1044 | 5.55 | [18616, 19030] | 0.0493 | 0.0703 | 0.0996 | 78.4218 |
| large_mixed | java | 100 | 6.32 | 15831 | 362 | 2.29 | [15760, 15903] | 0.0554 | 0.0955 | 0.1361 | 112.9783 |
| large_numeric_incr | rust | 100 | 4.23 | 23678 | 729 | 3.08 | [23534, 23823] | 0.0402 | 0.0533 | 0.0746 | 20.7497 |
| large_numeric_incr | python | 100 | 5.19 | 19293 | 775 | 4.02 | [19139, 19447] | 0.0484 | 0.0666 | 0.0932 | 75.5006 |
| large_numeric_incr | java | 100 | 6.13 | 16326 | 608 | 3.73 | [16205, 16446] | 0.0543 | 0.0909 | 0.1285 | 109.9811 |
| large_read_heavy | rust | 100 | 4.42 | 22727 | 1244 | 5.47 | [22480, 22974] | 0.0414 | 0.0587 | 0.0839 | 23.3300 |
| large_read_heavy | python | 100 | 5.20 | 19339 | 1126 | 5.82 | [19116, 19563] | 0.0481 | 0.0683 | 0.0962 | 75.6971 |
| large_read_heavy | java | 100 | 6.31 | 15928 | 1091 | 6.85 | [15711, 16144] | 0.0552 | 0.0966 | 0.1367 | 112.0720 |
| large_ttl_expiration | rust | 100 | 4.31 | 23244 | 619 | 2.66 | [23121, 23367] | 0.0405 | 0.0568 | 0.0822 | 22.8987 |
| large_ttl_expiration | python | 100 | 6.40 | 15649 | 460 | 2.94 | [15558, 15741] | 0.0430 | 0.1220 | 0.1625 | 97.4860 |
| large_ttl_expiration | java | 100 | 6.82 | 14782 | 1348 | 9.12 | [14514, 15049] | 0.0532 | 0.1167 | 0.1645 | 125.5229 |
| large_write_heavy | rust | 100 | 4.33 | 23109 | 806 | 3.49 | [22949, 23269] | 0.0408 | 0.0569 | 0.0816 | 22.1877 |
| large_write_heavy | python | 100 | 5.26 | 19192 | 1700 | 8.86 | [18855, 19530] | 0.0462 | 0.0741 | 0.1013 | 76.2784 |
| large_write_heavy | java | 100 | 6.07 | 16510 | 596 | 3.61 | [16391, 16628] | 0.0538 | 0.0897 | 0.1254 | 105.7182 |

### Workload medium

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| medium_high_cardinality | rust | 100 | 0.50 | 20088 | 1193 | 5.94 | [19851, 20325] | 0.0444 | 0.0646 | 0.0930 | 23.1846 |
| medium_high_cardinality | python | 100 | 0.64 | 15703 | 1329 | 8.47 | [15439, 15966] | 0.0517 | 0.0756 | 0.1037 | 79.7449 |
| medium_high_cardinality | java | 100 | 1.10 | 9108 | 530 | 5.81 | [9002, 9213] | 0.0799 | 0.1487 | 0.2234 | 130.4365 |
| medium_hot_keys | rust | 100 | 0.48 | 21018 | 999 | 4.75 | [20820, 21216] | 0.0428 | 0.0616 | 0.0883 | 19.4042 |
| medium_hot_keys | python | 100 | 0.61 | 16378 | 725 | 4.43 | [16234, 16522] | 0.0496 | 0.0716 | 0.0985 | 73.1396 |
| medium_hot_keys | java | 100 | 1.08 | 9317 | 655 | 7.03 | [9187, 9447] | 0.0780 | 0.1421 | 0.2080 | 135.8741 |
| medium_mixed | rust | 100 | 0.48 | 20892 | 1212 | 5.80 | [20652, 21133] | 0.0430 | 0.0624 | 0.0894 | 19.8895 |
| medium_mixed | python | 100 | 0.62 | 16118 | 1139 | 7.07 | [15892, 16344] | 0.0509 | 0.0731 | 0.0992 | 73.2616 |
| medium_mixed | java | 100 | 1.07 | 9418 | 558 | 5.92 | [9307, 9529] | 0.0774 | 0.1388 | 0.2030 | 133.1397 |
| medium_numeric_incr | rust | 100 | 0.46 | 21658 | 1245 | 5.75 | [21411, 21905] | 0.0423 | 0.0574 | 0.0755 | 18.4921 |
| medium_numeric_incr | python | 100 | 0.63 | 15976 | 938 | 5.87 | [15790, 16163] | 0.0510 | 0.0738 | 0.1011 | 74.8064 |
| medium_numeric_incr | java | 100 | 1.06 | 9490 | 586 | 6.18 | [9374, 9607] | 0.0766 | 0.1342 | 0.1944 | 140.3206 |
| medium_read_heavy | rust | 100 | 0.46 | 21922 | 1095 | 5.00 | [21705, 22139] | 0.0416 | 0.0568 | 0.0760 | 18.1307 |
| medium_read_heavy | python | 100 | 0.59 | 16900 | 860 | 5.09 | [16729, 17071] | 0.0491 | 0.0664 | 0.0873 | 69.3411 |
| medium_read_heavy | java | 100 | 1.04 | 9639 | 728 | 7.56 | [9495, 9784] | 0.0760 | 0.1338 | 0.1941 | 136.9082 |
| medium_ttl_expiration | rust | 100 | 0.48 | 20892 | 1014 | 4.86 | [20690, 21093] | 0.0431 | 0.0619 | 0.0878 | 19.7169 |
| medium_ttl_expiration | python | 100 | 0.63 | 15909 | 1486 | 9.34 | [15614, 16204] | 0.0517 | 0.0744 | 0.1016 | 74.9431 |
| medium_ttl_expiration | java | 100 | 1.12 | 8977 | 738 | 8.22 | [8831, 9123] | 0.0809 | 0.1542 | 0.2333 | 137.6633 |
| medium_write_heavy | rust | 100 | 0.49 | 20372 | 1706 | 8.37 | [20033, 20710] | 0.0447 | 0.0632 | 0.0880 | 19.9499 |
| medium_write_heavy | python | 100 | 0.64 | 15806 | 891 | 5.63 | [15630, 15983] | 0.0514 | 0.0752 | 0.1041 | 75.5427 |
| medium_write_heavy | java | 100 | 1.12 | 8993 | 596 | 6.63 | [8875, 9111] | 0.0810 | 0.1509 | 0.2243 | 139.2607 |

### Workload small

| Workload | Linguaggio | Run | Tempo medio (s) | Cmd/s medio | Dev. std cmd/s | CV % | IC 95% cmd/s | p50 medio (ms) | p95 medio (ms) | p99 medio (ms) | Max medio (ms) |
|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| small_high_cardinality | rust | 100 | 0.08 | 12628 | 577 | 4.57 | [12513, 12742] | 0.0494 | 0.0763 | 0.1061 | 23.3229 |
| small_high_cardinality | python | 100 | 0.16 | 6109 | 361 | 5.92 | [6037, 6180] | 0.0593 | 0.0911 | 0.1237 | 88.1708 |
| small_high_cardinality | java | 100 | 0.33 | 3068 | 204 | 6.64 | [3027, 3108] | 0.1293 | 0.2383 | 0.3162 | 136.1974 |
| small_hot_keys | rust | 100 | 0.08 | 12517 | 552 | 4.41 | [12408, 12627] | 0.0496 | 0.0748 | 0.1050 | 24.0166 |
| small_hot_keys | python | 100 | 0.17 | 5940 | 435 | 7.32 | [5854, 6027] | 0.0609 | 0.0950 | 0.1307 | 90.9002 |
| small_hot_keys | java | 100 | 0.32 | 3141 | 165 | 5.24 | [3108, 3174] | 0.1486 | 0.2632 | 0.3517 | 140.2628 |
| small_mixed | rust | 100 | 0.08 | 12449 | 612 | 4.92 | [12327, 12570] | 0.0503 | 0.0768 | 0.1068 | 23.6826 |
| small_mixed | python | 100 | 0.17 | 5953 | 429 | 7.20 | [5868, 6039] | 0.0616 | 0.0942 | 0.1265 | 90.0067 |
| small_mixed | java | 100 | 0.35 | 2870 | 260 | 9.07 | [2819, 2922] | 0.1551 | 0.2764 | 0.3743 | 145.0349 |
| small_numeric_incr | rust | 100 | 0.08 | 12708 | 670 | 5.27 | [12575, 12841] | 0.0495 | 0.0760 | 0.1064 | 22.8487 |
| small_numeric_incr | python | 100 | 0.17 | 6036 | 347 | 5.75 | [5967, 6105] | 0.0603 | 0.0925 | 0.1248 | 88.7887 |
| small_numeric_incr | java | 100 | 0.33 | 3022 | 169 | 5.59 | [2989, 3056] | 0.1426 | 0.2574 | 0.3457 | 134.7798 |
| small_read_heavy | rust | 100 | 0.08 | 12553 | 656 | 5.22 | [12423, 12684] | 0.0496 | 0.0761 | 0.1060 | 23.7254 |
| small_read_heavy | python | 100 | 0.17 | 6051 | 332 | 5.48 | [5985, 6116] | 0.0603 | 0.0927 | 0.1257 | 88.2507 |
| small_read_heavy | java | 100 | 0.32 | 3160 | 138 | 4.38 | [3133, 3188] | 0.1482 | 0.2606 | 0.3522 | 138.2416 |
| small_ttl_expiration | rust | 100 | 0.08 | 12623 | 778 | 6.16 | [12469, 12777] | 0.0495 | 0.0767 | 0.1085 | 23.2182 |
| small_ttl_expiration | python | 100 | 0.17 | 5999 | 342 | 5.69 | [5931, 6066] | 0.0613 | 0.0937 | 0.1272 | 88.9544 |
| small_ttl_expiration | java | 100 | 0.33 | 3010 | 164 | 5.45 | [2977, 3042] | 0.1419 | 0.2568 | 0.3436 | 133.1721 |
| small_write_heavy | rust | 100 | 0.08 | 12627 | 573 | 4.54 | [12513, 12741] | 0.0498 | 0.0749 | 0.1031 | 23.4853 |
| small_write_heavy | python | 100 | 0.17 | 5990 | 449 | 7.50 | [5901, 6079] | 0.0611 | 0.0936 | 0.1247 | 89.6479 |
| small_write_heavy | java | 100 | 0.31 | 3264 | 276 | 8.45 | [3209, 3318] | 0.1410 | 0.2555 | 0.3371 | 133.8387 |

## Risultati Memoria

Ogni riga riporta la media di 3 run per lo stesso linguaggio e workload. Per ogni workload, i linguaggi sono ordinati dal picco RSS medio più basso al più alto.

### Workload large

| Workload | Linguaggio | Run | RSS iniziale medio (MB) | Dev. std iniziale (MB) | RSS picco medio (MB) | Dev. std picco (MB) |
|---|---|---:|---:|---:|---:|---:|
| large_high_cardinality | rust | 3 | 3.77 | 0.00 | 13.24 | 0.00 |
| large_high_cardinality | python | 3 | 13.20 | 0.07 | 24.02 | 0.52 |
| large_high_cardinality | java | 3 | 34.71 | 0.53 | 121.41 | 0.80 |
| large_hot_keys | rust | 3 | 3.77 | 0.01 | 3.80 | 0.01 |
| large_hot_keys | python | 3 | 13.26 | 0.10 | 13.32 | 0.11 |
| large_hot_keys | java | 3 | 35.59 | 0.16 | 112.38 | 0.16 |
| large_mixed | rust | 3 | 3.77 | 0.00 | 7.34 | 0.00 |
| large_mixed | python | 3 | 13.12 | 0.01 | 17.92 | 0.12 |
| large_mixed | java | 3 | 35.61 | 0.22 | 119.19 | 0.39 |
| large_numeric_incr | rust | 3 | 3.77 | 0.00 | 5.24 | 0.00 |
| large_numeric_incr | python | 3 | 13.29 | 0.07 | 14.82 | 0.06 |
| large_numeric_incr | java | 3 | 35.50 | 0.11 | 113.56 | 0.29 |
| large_read_heavy | rust | 3 | 3.77 | 0.00 | 5.71 | 0.00 |
| large_read_heavy | python | 3 | 13.25 | 0.07 | 15.66 | 0.06 |
| large_read_heavy | java | 3 | 35.35 | 0.72 | 115.33 | 1.33 |
| large_ttl_expiration | rust | 3 | 3.77 | 0.00 | 6.59 | 0.00 |
| large_ttl_expiration | python | 3 | 13.25 | 0.06 | 16.46 | 0.19 |
| large_ttl_expiration | java | 3 | 35.13 | 0.51 | 125.33 | 4.40 |
| large_write_heavy | rust | 3 | 3.77 | 0.00 | 5.04 | 0.00 |
| large_write_heavy | python | 3 | 13.26 | 0.02 | 14.74 | 0.02 |
| large_write_heavy | java | 3 | 35.58 | 0.06 | 116.35 | 0.39 |

### Workload medium

| Workload | Linguaggio | Run | RSS iniziale medio (MB) | Dev. std iniziale (MB) | RSS picco medio (MB) | Dev. std picco (MB) |
|---|---|---:|---:|---:|---:|---:|
| medium_high_cardinality | rust | 3 | 3.77 | 0.00 | 4.58 | 0.00 |
| medium_high_cardinality | python | 3 | 13.30 | 0.03 | 14.48 | 0.03 |
| medium_high_cardinality | java | 3 | 35.45 | 0.07 | 74.36 | 0.89 |
| medium_hot_keys | rust | 3 | 3.77 | 0.00 | 3.80 | 0.00 |
| medium_hot_keys | python | 3 | 13.19 | 0.08 | 13.27 | 0.07 |
| medium_hot_keys | java | 3 | 35.52 | 0.16 | 74.21 | 4.36 |
| medium_mixed | rust | 3 | 3.77 | 0.00 | 4.25 | 0.00 |
| medium_mixed | python | 3 | 13.21 | 0.09 | 13.64 | 0.10 |
| medium_mixed | java | 3 | 35.41 | 0.17 | 71.15 | 0.91 |
| medium_numeric_incr | rust | 3 | 3.77 | 0.00 | 4.00 | 0.00 |
| medium_numeric_incr | python | 3 | 13.20 | 0.05 | 13.44 | 0.04 |
| medium_numeric_incr | java | 3 | 35.39 | 0.02 | 75.60 | 6.64 |
| medium_read_heavy | rust | 3 | 3.77 | 0.00 | 4.14 | 0.05 |
| medium_read_heavy | python | 3 | 13.17 | 0.09 | 13.48 | 0.09 |
| medium_read_heavy | java | 3 | 35.05 | 0.66 | 71.73 | 0.49 |
| medium_ttl_expiration | rust | 3 | 3.77 | 0.00 | 4.16 | 0.00 |
| medium_ttl_expiration | python | 3 | 13.31 | 0.08 | 13.72 | 0.08 |
| medium_ttl_expiration | java | 3 | 35.45 | 0.08 | 75.14 | 3.62 |
| medium_write_heavy | rust | 3 | 3.77 | 0.00 | 3.99 | 0.00 |
| medium_write_heavy | python | 3 | 13.25 | 0.14 | 13.48 | 0.14 |
| medium_write_heavy | java | 3 | 35.40 | 0.06 | 72.30 | 1.07 |

### Workload small

| Workload | Linguaggio | Run | RSS iniziale medio (MB) | Dev. std iniziale (MB) | RSS picco medio (MB) | Dev. std picco (MB) |
|---|---|---:|---:|---:|---:|---:|
| small_high_cardinality | rust | 3 | 3.77 | 0.00 | 3.90 | 0.00 |
| small_high_cardinality | python | 3 | 13.32 | 0.12 | 13.40 | 0.11 |
| small_high_cardinality | java | 3 | 35.10 | 0.56 | 40.72 | 0.13 |
| small_hot_keys | rust | 3 | 3.77 | 0.00 | 3.79 | 0.00 |
| small_hot_keys | python | 3 | 13.28 | 0.12 | 13.29 | 0.12 |
| small_hot_keys | java | 3 | 35.47 | 0.09 | 41.03 | 0.27 |
| small_mixed | rust | 3 | 3.77 | 0.00 | 3.85 | 0.00 |
| small_mixed | python | 3 | 13.31 | 0.06 | 13.35 | 0.06 |
| small_mixed | java | 3 | 35.39 | 0.08 | 41.14 | 0.12 |
| small_numeric_incr | rust | 3 | 3.77 | 0.00 | 3.82 | 0.00 |
| small_numeric_incr | python | 3 | 13.20 | 0.12 | 13.23 | 0.15 |
| small_numeric_incr | java | 3 | 35.59 | 0.20 | 41.09 | 0.21 |
| small_read_heavy | rust | 3 | 3.77 | 0.00 | 3.82 | 0.00 |
| small_read_heavy | python | 3 | 13.25 | 0.14 | 13.28 | 0.14 |
| small_read_heavy | java | 3 | 35.58 | 0.15 | 41.05 | 0.25 |
| small_ttl_expiration | rust | 3 | 3.77 | 0.00 | 3.83 | 0.00 |
| small_ttl_expiration | python | 3 | 13.23 | 0.09 | 13.26 | 0.09 |
| small_ttl_expiration | java | 3 | 35.05 | 0.64 | 40.56 | 0.31 |
| small_write_heavy | rust | 3 | 3.77 | 0.00 | 3.80 | 0.00 |
| small_write_heavy | python | 3 | 13.28 | 0.13 | 13.32 | 0.13 |
| small_write_heavy | java | 3 | 35.41 | 0.12 | 41.11 | 0.19 |

## Note Interpretative

- Rust ottiene il throughput medio più alto in tutti i 21 workload.
- Python è generalmente secondo per throughput, mentre Java resta terzo nella maggior parte dei workload; Java conserva però risultati relativamente stabili sui workload large.
- Le misure di memoria confermano lo stesso ordinamento in tutti i workload: Rust ha il picco RSS medio più basso, Python è intermedio, Java ha il picco più alto per l'overhead della JVM.
- I workload `high_cardinality` aumentano il picco RSS rispetto ai profili con poche chiavi, soprattutto su `large_high_cardinality`, dove il picco medio è 13.24 MB per Rust, 24.02 MB per Python e 121.41 MB per Java.
- Dopo rerun mirati dei workload con CV iniziale superiore al 10%, la tabella finale dei risultati throughput non contiene più combinazioni workload-linguaggio con CV >= 10%.
- I workload small restano più sensibili al rumore di sistema perché il tempo assoluto di esecuzione è breve; i workload large sono più rappresentativi per confronti stabili.
