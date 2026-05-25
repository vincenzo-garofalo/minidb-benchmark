# MiniDB Command Specification

Questa specifica definisce il comportamento comune che dovrà essere rispettato
dalle implementazioni in Rust, Python e Java.

MiniDB è un database key-value in memoria.

## Obiettivi

- Mantenere lo stesso comportamento nei tre linguaggi.
- Rendere i benchmark confrontabili.
- Separare la logica del database dal protocollo di rete.
- Partire da un sottoinsieme piccolo ma solido di operazioni.

## Modello Dati

Il database memorizza coppie chiave-valore in memoria.

```text
key: string
value: string
expires_at: optional timestamp
```

Le chiavi sono univoche. Impostare una chiave già esistente sovrascrive il
valore precedente e rimuove l'eventuale scadenza associata.

## Formato Dei Comandi

Ogni comando è una riga di testo.

```text
COMMAND arg1 arg2 ...
```

Regole:

- I comandi non sono case-sensitive: `set`, `SET` e `Set` sono equivalenti.
- Gli argomenti sono separati da uno o più spazi.
- Gli spazi iniziali e finali sono ignorati.
- In questa prima versione i valori non possono contenere spazi.
- Una riga vuota produce errore.

Esempio:

```text
SET username mario
GET username
```

## Formato Delle Risposte

Le risposte sono testuali.

```text
OK
value
NOT_FOUND
1
0
-1
-2
ERR message
```

Significato:

- `OK`: comando eseguito correttamente senza valore da restituire.
- `NOT_FOUND`: chiave non trovata.
- `1`: risultato positivo o vero.
- `0`: risultato negativo o falso.
- `-1`: chiave esistente senza scadenza (`TTL`).
- `-2`: chiave non esistente (`TTL`).
- `ERR message`: comando non valido o errore di tipo.

## Comandi Supportati

### PING

Verifica che il database sia raggiungibile.

Sintassi:

```text
PING
```

Risposta:

```text
PONG
```

Esempio:

```text
> PING
PONG
```

Errori:

```text
PING extra
ERR wrong number of arguments for PING
```

### SET

Imposta il valore di una chiave.

Sintassi:

```text
SET key value
```

Risposta:

```text
OK
```

Esempio:

```text
> SET user mario
OK
```

Regole:

- Se la chiave non esiste, viene creata.
- Se la chiave esiste, il valore precedente viene sovrascritto.

Errori:

```text
SET key
ERR wrong number of arguments for SET
```

### GET

Restituisce il valore associato a una chiave.

Sintassi:

```text
GET key
```

Risposta se la chiave esiste:

```text
value
```

Risposta se la chiave non esiste:

```text
NOT_FOUND
```

Esempio:

```text
> SET user mario
OK
> GET user
mario
> GET missing
NOT_FOUND
```

Errori:

```text
GET
ERR wrong number of arguments for GET
```

### DEL

Elimina una chiave.

Sintassi:

```text
DEL key
```

Risposta se la chiave esisteva ed è stata rimossa:

```text
1
```

Risposta se la chiave non esisteva:

```text
0
```

Esempio:

```text
> SET user mario
OK
> DEL user
1
> DEL user
0
```

Errori:

```text
DEL
ERR wrong number of arguments for DEL
```

### EXISTS

Verifica se una chiave esiste.

Sintassi:

```text
EXISTS key
```

Risposta se la chiave esiste:

```text
1
```

Risposta se la chiave non esiste:

```text
0
```

Esempio:

```text
> SET user mario
OK
> EXISTS user
1
> EXISTS missing
0
```

Errori:

```text
EXISTS
ERR wrong number of arguments for EXISTS
```

### INCR

Incrementa di 1 il valore numerico associato a una chiave.

Sintassi:

```text
INCR key
```

Risposta:

```text
new_value
```

Regole:

- Se la chiave esiste e contiene un intero valido, il valore viene incrementato.
- Se la chiave non esiste, viene creata con valore `1`.
- Se la chiave contiene un valore non numerico, il comando restituisce errore.

Esempio:

```text
> INCR counter
1
> INCR counter
2
> GET counter
2
```

Errore su valore non numerico:

```text
> SET name mario
OK
> INCR name
ERR value is not an integer
```

Errore su numero di argomenti:

```text
INCR
ERR wrong number of arguments for INCR
```

### EXPIRE

Imposta una scadenza, in secondi, su una chiave esistente.

Sintassi:

```text
EXPIRE key seconds
```

Risposta se la chiave esiste e la scadenza viene impostata:

```text
1
```

Risposta se la chiave non esiste:

```text
0
```

Regole:

- `seconds` deve essere un intero valido.
- Se `seconds` è minore o uguale a `0`, la chiave viene rimossa immediatamente.
- Le chiavi scadute vengono eliminate quando vengono toccate da un comando.
- `SET` sovrascrive il valore e rimuove l'eventuale scadenza precedente.
- `INCR` mantiene la scadenza se la chiave esiste già.

Esempio:

```text
> SET session open
OK
> EXPIRE session 30
1
> EXPIRE missing 30
0
```

Errori:

```text
EXPIRE key
ERR wrong number of arguments for EXPIRE
EXPIRE key soon
ERR invalid expire seconds
```

### TTL

Restituisce i secondi rimanenti prima della scadenza di una chiave.

Sintassi:

```text
TTL key
```

Risposte:

```text
seconds
-1
-2
```

Significato:

- `seconds`: numero di secondi rimanenti prima della scadenza.
- `-1`: la chiave esiste ma non ha scadenza.
- `-2`: la chiave non esiste o è già scaduta.

Esempio:

```text
> SET session open
OK
> TTL session
-1
> EXPIRE session 30
1
> TTL session
29
> TTL missing
-2
```

Errori:

```text
TTL
ERR wrong number of arguments for TTL
```

## Errori Generali

### Troppi o pochi argomenti

Ogni comando deve ricevere esattamente il numero di argomenti indicato nella
specifica.

```text
SET a b c
ERR wrong number of arguments for SET
```

### Comando vuoto

```text

ERR empty command
```

### Argomento seconds non valido

Il terzo argomento di `EXPIRE` deve essere un intero valido.

```text
EXPIRE key soon
ERR invalid expire seconds
```

### Comando sconosciuto

```text
UNKNOWN key
ERR unknown command UNKNOWN
```

## Casi Di Test Minimi

Questi casi dovranno passare in tutte le implementazioni.

```text
PING                  -> PONG
SET a 10              -> OK
GET a                 -> 10
GET missing           -> NOT_FOUND
EXISTS a              -> 1
EXISTS missing        -> 0
DEL a                 -> 1
DEL a                 -> 0
INCR counter          -> 1
INCR counter          -> 2
SET name mario        -> OK
INCR name             -> ERR value is not an integer
TTL missing           -> -2
SET session open      -> OK
TTL session           -> -1
EXPIRE session 0      -> 1
GET session           -> NOT_FOUND
UNKNOWN x             -> ERR unknown command UNKNOWN
```

## Estensioni Future

Queste funzionalità non fanno parte della prima versione, ma potranno essere
aggiunte dopo che il core sarà stabile:

- server TCP;
- protocollo RESP;
- liste con `LPUSH`, `RPUSH`, `LPOP`, `RPOP`;
- persistenza su file;
- benchmark con client concorrenti.
