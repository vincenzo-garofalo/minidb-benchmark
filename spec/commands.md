# MiniDB Command Specification

Questa specifica definisce il comportamento comune che dovrà essere rispettato
dalle implementazioni in Rust, Python e Java.

MiniDB e un database key-value in memoria.

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
```

Le chiavi sono univoche. Impostare una chiave già esistente sovrascrive il
valore precedente.

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
ERR message
```

Significato:

- `OK`: comando eseguito correttamente senza valore da restituire.
- `NOT_FOUND`: chiave non trovata.
- `1`: risultato positivo o vero.
- `0`: risultato negativo o falso.
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

## Errori Generali

### Comando vuoto

```text

ERR empty command
```

### Comando sconosciuto

```text
UNKNOWN key
ERR unknown command UNKNOWN
```

### Troppi o pochi argomenti

Ogni comando deve ricevere esattamente il numero di argomenti indicato nella
specifica.

```text
SET a b c
ERR wrong number of arguments for SET
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
UNKNOWN x             -> ERR unknown command UNKNOWN
```

## Estensioni Future

Queste funzionalita non fanno parte della prima versione, ma potranno essere
aggiunte dopo che il core sara stabile:

- server TCP;
- protocollo RESP;
- scadenza delle chiavi con `EXPIRE` e `TTL`;
- liste con `LPUSH`, `RPUSH`, `LPOP`, `RPOP`;
- persistenza su file;
- benchmark con client concorrenti.
