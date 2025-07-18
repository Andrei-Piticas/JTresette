Questo progetto è un'implementazione completa del classico gioco di carte italiano Tresette, sviluppata in Java con un'interfaccia grafica realizzata interamente in Swing. L'applicazione è stata creata come progetto per il corso di Metodologie di Programmazione, con un'enfasi particolare sulla corretta applicazione dei principi di ingegneria del software e dei design pattern.

Il gioco permette a un singolo utente di affrontare una partita 2 contro 2, alleandosi con un bot contro una squadra di due avversari artificiali.

✨ Funzionalità Principali
Partita Completa 2 vs 2: Gioca una partita completa di Tresette con un compagno bot contro due avversari.

Intelligenza Artificiale Semplice: I bot seguono le regole fondamentali del gioco, come l'obbligo di rispondere al "palo".

Gestione del Profilo:

Modifica del nickname.

Personalizzazione dell'avatar tramite caricamento di un'immagine dal computer.

Statistiche persistenti (partite giocate, vinte e perse) salvate su un file stats.json.

Sistema di Livelli: Il livello del giocatore aumenta in base al numero di partite vinte, fornendo un senso di progressione.

Interfaccia Grafica Personalizzata: Tutti i componenti, dai pannelli ai checkbox, sono stati personalizzati con immagini per un'esperienza di gioco più coinvolgente.

Feedback Audio e Visivo:

Musica di sottofondo ed effetti sonori per le azioni di gioco.

Controlli per attivare/disattivare musica ed effetti.

Un pannello a comparsa a fine mano mostra chi ha vinto la presa.

Un pannello finale annuncia la squadra vincitrice.

🏛️ Architettura e Design Pattern
Il progetto è stato sviluppato seguendo rigorosamente il pattern architetturale Model-View-Controller (MVC) per garantire una netta separazione delle responsabilità.

Model: Contiene il cuore del gioco. Le classi Partita2v2, Carta, Mazzo, BotPlayer, Strategia e Statistiche gestiscono le regole, i dati e la logica di business, senza alcun legame con l'interfaccia grafica.

View: Contiene tutte le classi dell'interfaccia grafica realizzate in Swing, come MainMenu, GamePanel, ProfilePanel e SettingPanel. Il suo unico scopo è mostrare i dati e catturare l'input dell'utente.

Controller: Funge da ponte tra la View e il Model. Classi come JTresette (che avvia l'app) e GiocatoreUmano (che traduce i click in mosse) rientrano in questo layer.

Sono stati inoltre implementati i seguenti Design Pattern:

Observer: Per disaccoppiare il GamePanel (Observer) dalla logica di gioco Partita2v2 (Observable), permettendo alla UI di aggiornarsi in tempo reale.

Singleton: Utilizzato nella classe AudioManager per garantire un unico punto di controllo per tutte le risorse audio.

Strategy: Implementato per separare l'algoritmo decisionale (Strategia) dal BotPlayer, rendendo l'IA facilmente estensibile.

Facade: La classe Partita agisce come una facciata semplice per la logica più complessa contenuta in Partita2v2.

🛠️ Tecnologie Utilizzate
Linguaggio: Java (JDK 21)

Interfaccia Grafica: Java Swing

Gestione Dati: Libreria org.json per la serializzazione in JSON.

Audio: API standard javax.sound.sampled.

🚀 Come Eseguire il Progetto
Prerequisiti:

Avere installato un JDK (Java Development Kit) versione 21 o superiore.

Librerie:

Assicurarsi che la libreria json.jar sia inclusa nel classpath del progetto. La configurazione standard prevede di inserirla in una cartella lib/ alla radice del progetto.

Avvio:

Compilare ed eseguire la classe JTresette.java che contiene il metodo main.
