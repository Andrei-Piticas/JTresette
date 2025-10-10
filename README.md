# 🃏 Tresette (Java Swing) - Implementazione Avanzata con Design Pattern



---

## 🎯 Panoramica del Progetto

Questo repository contiene un'implementazione completa e robusta del classico gioco di carte italiano **Tresette**. Sviluppato interamente in **Java (JDK 21)**, il progetto ha rappresentato l'elaborato finale per il corso di *Metodologie di Programmazione*, con un focus primario sull'**Ingegneria del Software** e l'applicazione di **Design Pattern** per una manutenzione e scalabilità ottimali.

Il gioco permette a un utente di allearsi con un bot in una sfida 2 vs 2 contro due avversari artificiali.

---

## ✨ Funzionalità del Gioco

L'applicazione offre un'esperienza di gioco coinvolgente grazie a un'interfaccia utente riccamente personalizzata:

* **Partite 2 vs 2 Complete:** Gioco fedele alle regole del Tresette in modalità a squadre.
* **IA Essenziale:** I bot seguono le regole base di gioco, incluse le priorità e l'obbligo di "rispondere al palo".
* **Interfaccia Utente Coinvolgente:** Tutti i componenti grafici (pannelli, pulsanti, ecc.) sono stati personalizzati con asset grafici dedicati.
* **Feedback Immersivo:** Include **musica di sottofondo** ed **effetti sonori** per le prese e le azioni, con controlli dedicati per l'attivazione/disattivazione.
* **Gestione del Profilo e Progressione:**
    * Statistiche persistenti (Vittorie/Sconfitte/Giocate) salvate in `stats.json`.
    * Sistema di **livelli** basato sulle partite vinte.
    * Personalizzazione del **nickname** e dell'**avatar**.

---

## 🏛️ Architettura e Ingegneria del Software

Il codice è stato strutturato rigorosamente per garantire la **separazione delle responsabilità** e l'estensibilità:

### 🧱 Pattern Architetturale: Model-View-Controller (MVC)

Abbiamo garantito un disaccoppiamento netto dei tre strati:

| Layer | Componenti Chiave | Responsabilità |
| :--- | :--- | :--- |
| **Model** | `Partita2v2`, `Carta`, `Strategia`, `Statistiche` | Logica pura del gioco, regole, dati e stato, totalmente agnostico dalla UI. |
| **View** | `MainMenu`, `GamePanel`, `ProfilePanel` (tutti in Swing) | Visualizzazione dei dati e gestione degli eventi utente (click), senza alcuna logica di gioco. |
| **Controller** | `JTresette` (avvio), `GiocatoreUmano` | Fa da ponte, traducendo gli input della View in modifiche al Model. |

### 🛠️ Design Pattern Implementati

L'applicazione implementa diversi Design Pattern per risolvere problemi di progettazione specifici:

* **Observer:** Utilizzato per il *refresh automatico* dell'interfaccia (`GamePanel` come Observer) in risposta agli eventi di gioco (`Partita2v2` come Observable), mantenendo il disaccoppiamento.
* **Singleton:** Garantisce un'unica istanza centrale (`AudioManager`) per la gestione e il controllo di tutte le risorse audio del gioco.
* **Strategy:** Utilizzato per separare l'algoritmo decisionale del bot (`Strategia`) dalla sua esecuzione (`BotPlayer`), rendendo facile l'implementazione di nuove logiche di IA.
* **Facade:** La classe `Partita` semplifica l'interazione del Controller con la complessa logica interna di `Partita2v2`.

---

## 🚀 Come Eseguire il Progetto

### Prerequisiti

* **JDK 21** o versione successiva installata.

### Setup e Avvio

1.  **Librerie:** Assicurati che la libreria di serializzazione JSON (`org.json`) sia inclusa nel tuo classpath, preferibilmente posizionata in una cartella `lib/` alla radice del progetto.
2.  **Esecuzione:** Compila ed esegui la classe principale.

```bash
# Esegui (Esempio tipico, usa il tuo IDE preferito per il setup del classpath)
$ javac -cp lib/json.jar *.java 
$ java -cp .:lib/json.jar JTresette
