# Frontier – Manuelles Test-Playbook (SOLL-Version)

> Dieses Dokument beschreibt die erwarteten Funktionen und zugehörigen manuellen Tests für das Spiel *Frontier* in seiner spielbaren Alpha-Version. Es dient als Qualitätssicherungstool für Entwickler und als Nachweis für funktionale Fortschritte.

---

## Allgemeine Hinweise

* Vor jedem Test: Spiel mit `./gradlew lwjgl3:run` (Linux/macOS) oder `gradlew.bat lwjgl3:run` (Windows) starten
* Standard-Map laden
* Teststatus dokumentieren:

  * `funktioniert`
  * `teilweise`
  * `fehlt/fehlerhaft`

---

## Testkategorien & Testfälle

### 1. Spielstart & Hauptmenü

**T-11**: *Spiel startet korrekt*

* Ziel: Keine Fehler beim Start
* Schritte:

  1. Starte Spiel
  2. Beobachte Ladebalken und Übergang zum Hauptmenü
  3. Hauptmenü mit Hintergrundbild und Animationen ist vorhanden
* Erwartet: Keine Exceptions, Hauptmenü wird geladen, Optionen werden angezeigt
* Status: \[  ]

**T-12**: *Menübuttons reagieren korrekt*

* Ziel: UI ist funktionsfähig
* Schritte:

  1. Klicke auf "Start"
  2. Klicke auf "Load"
  3. Klicke auf "Exit"
* Erwartet: Start/Load öffnet Map, Exit beendet Spiel
* TODO: Load öffnet gespeicherte Spiele oder das zuletzt gespeicherte Spiel
* Status: \[  ]

---

### 2. Karte & Baugrenzen

**T-20**: *Map ist geladen und Kacheln korrekt dargestellt*

* Ziel: Alle Terrain-Typen sind sichtbar
* Erwartet: Gras, Wald, Stein, Eisen, Wasser sind sichtbar
* Status: \[  ]

**T-21**: *Baugrenzen werden visuell angezeigt*

* Ziel: Kein Bau auf Wald, Stein, Eisen, Wasser
* Schritte:

  1. Öffne Baumenü
  2. Bewege Maus über verbotene Tiles
* Erwartet: Gebäude erscheint rot / nicht platzierbar
* Status: \[  ]

---

### 3. Ressourcenmanagement

**T-30**: *Startressourcen vorhanden (Gold, Holz, Stein, Eisen)*

* Erwartet: Werte sichtbar rechts oben, alle > 0
* Status: \[  ]

**T-31**: *Gebäude produzieren Ressourcen nach Überleben des Tages*

* Schritte:

  1. Baue 1x Holzgebäude auf Waldrand
  2. Starte Tag, überlebe
* Erwartet: Holz-Zuwachs gemäss Anzeige
* Wiederhole für Stein und Eisen
* Status: \[  ]

---

### 4. Bau & Abbau

**T-40**: *Gebäude lassen sich platzieren (gültige Felder)*

* Ziel: Platzierung um Ressourcenfeld möglich
* Erwartet: Kein Fehler, Gebäude erscheint sichtbar
* TODO: Spieler erhält Feedback
* Gebäude nimmt Aufgabe wahr

  * Resourcengebäude -> Spieler erhält die richtige Resource
  * Turm -> Schiesst auf Gegner (reichweite und Schussart des Turms beachten)
  * Mauer -> Blockiert Gegnerbewegung&#x20;
* Status: \[  ]

**T-41**: *Gebäude lassen sich abreissen (mit UI-Klick)*

* Erwartet: Gebäude verschwindet, Feld wird wieder frei
* Status: \[  ]

**T-42 (negativ)**: *Platzierung auf verbotenen Feldern blockiert*

* Erwartet: Kein Gebäude entsteht, kein Ressourcenabzug
* TODO: Spieler erhält Feedback
* Status: \[  ]

---

### 5. Gegnerverhalten & Kampf

**T-50**: *Start einer Gegnerwelle per Knopf*

* Erwartet: Gegner erscheinen auf gelben Feldern
* Status: \[  ]

**T-51**: *Gegner laufen auf HQ zu und greifen an*

* Ziel: Pfadfindung aktiv, HQ verliert HP
* Status: \[  ]

**T-52**: *Türme greifen automatisch Gegner an*

* Erwartet: Gegner verlieren HP, evtl. sterben
* Beachte verhalten aus T-40
* Status: \[  ]

**T-53 (negativ)**: *Gegner spawn in zu hoher Anzahl führt zu FPS-Einbruch*

* Ziel: Bug dokumentieren
* Alternatives Ziel: Mathematische Formel anpassen
* Status: \[  ]

---

### 6. Tagesverlauf & Fortschritt

**T-60**: *Nach Überleben eines Tages startet neuer Tag*

* Erwartet: Neue Ressourcen, neue Welle
* Status: \[  ]

**T-61**: *Gegnerzahl steigt mit Formel (siehe Entschluss vom 04.05., siehe Ergebnis T-53)*

* Erwartet: Spürbar mehr Gegner pro Runde
* Status: \[  ]

**T-62**: *Spiel endet bei HQ-Zerstörung (Game Over)*

* Erwartet: Spiel kehrt zum Titelbildschirm zurück
* TODO: "Defeat"-Bildschirm bei Niederlage vor dem zurückkehren zum Titelbildschirm
* Status: \[  ]

---

### 7. Feedback & UI

**T-70**: *HP-Balken auf Gebäuden sichtbar*

* Erwartet: Balken erscheint bei Schaden
* Status: \[  ]

**T-71**: *Visualisierung von Bauverbot sichtbar (Rot-Färbung)*

* Status: \[  ]

**T-72**: *Ressourcenanzeige zeigt auch zu erwartenden Zuwachs*

* Erwartet: z. B. Holz: 10 (+2)
* Status: \[  ]

**T-73 (negativ)**: *Kein Feedback bei fehlenden Ressourcen für Bau*

* Ziel: Später visuelles Signal einbauen, siehe T-42
* Status: \[  ]

---

### 8. Offene Punkte / Bugs

**B-01**: Gegner manuell spawnbar – führt zu FPS-Einbruch
**B-02**: HQ fehlt beim Start – soll fixer Startpunkt sein
**B-03**: Kein Sound trotz Implementierungsmöglichkeit
**B-04**: Kein Feedback bei Platzierungsfehlern (nur rot)
**B-05**: Speicherfunktion nicht fertiggestellt
**B-06**: Zeitreise verworfen, aber UI-Reste vorhanden?

---

## Abschluss

* Jeder Test soll regelmässig vor Sprintende durchgeführt werden
* Testergebnis mit Datum, Name und Änderungsstatus protokollieren
* Bei neuen Features: Neue Test-ID erstellen und an diese Struktur anlehnen
