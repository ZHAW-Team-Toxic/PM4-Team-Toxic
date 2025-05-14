# Developer Set-up

> **Projekt:** Frontier  
> **Modul:** PM4 – ZHAW  
> **Team:** Toxic  
> **Version:** Mai 2025  
> Dieses Dokument richtet sich an neue Entwickler:innen im Projekt *Frontier* und beschreibt alle nötigen Schritte, um das Projekt lokal aufzusetzen, zu verstehen und weiterzuentwickeln.

---

## 1. Projektüberblick

*Frontier* ist ein 2D-Tower-Defense-Spiel mit strategischem Ressourcenmanagement. Ziel ist es, das eigene Hauptquartier gegen Gegnerwellen zu verteidigen, Ressourcen zu verwalten und durch Wiederholung eines Tages taktische Vorteile zu gewinnen.

Das Spiel ist als spielbarer Prototyp mit vollständiger Gameplay-Loop konzipiert und wird mit Java und **libGDX** umgesetzt. Die Architektur basiert auf einem **Entity-Component-System (Ashley)**. Für weitere Details siehe [Wiki](https://github.com/ZHAW-Team-Toxic/PM4-Team-Toxic/wiki/Libgdx-Guide).

---

## 2. Voraussetzungen

| Tool                  | Version/Empfehlung                                 |
|-----------------------|-----------------------------------------------------|
| Java Development Kit  | **JDK 21** (z. B. OpenJDK von [Adoptium](https://adoptium.net)) |
| Git                   | Aktuelle Version, installiert über [git-scm.com](https://git-scm.com) |
| Gradle                | Wird automatisch über den Wrapper verwendet         |
| IDE                   | **IntelliJ IDEA** empfohlen (mit Gradle & Java Support) |
| Optional              | VS Code mit Java Extension Pack                     |

---

## 3. Projekt klonen und starten

```bash
git clone https://github.com/your-team/frontier.git
cd frontier
./gradlew lwjgl3:run
````

Unter Windows ggf.:

```bat
gradlew.bat lwjgl3:run
```

Das Spiel startet im Desktop-Modus mit einer manuell erstellten Karte. Das Assets-Verzeichnis muss sich im richtigen Pfad befinden (`/assets`).

---

## 4. Projektstruktur

```plaintext
frontier/
├── core/         # Spiel-Logik und ECS-Systeme (Ashley)
├── desktop/      # Desktop-Launcher-Konfiguration
├── lwjgl3/       # Desktop-Runtime (libGDX)
├── assets/       # Texturen, Audio, Karten, Fonts
├── README.md     # Kurzbeschreibung des Projekts
├── build.gradle  # Projekt- und Build-Definition
frontier-launcher/
├── app/         # Launcher Logik nur in einer einzige Datei beinhaltet.
```
---

## 5. Codeformatierung

Verwende das folgende Kommando, um den Code automatisch zu formatieren (Clean Code-Konventionen):

```bash
./gradlew 
```
spotlessApply
Die Formatierung ist CI-relevant und Bestandteil des Review-Prozesses.

---

## 6. Asset verwaltung

In diesem Projekt verwalten wir Spiel-Assets mit den Tools **TexturePacker**, **Tiled Editor** und **SkinComposer**. Die Assets sind in gepackte und ungepackte Bereiche unterteilt.
- [Texture Packer](https://libgdx.com/wiki/tools/texture-packer)
- [Tiled Editor](https://www.mapeditor.org/)
- [SkinComposer](https://libgdx.com/wiki/tools/skin-composer)

---
## 📁 Projektstruktur

Die Assets liegen im `assets/`-Ordner mit folgender Struktur:

```
assets/
├── audio/           # Audio-Dateien (Musik, Effekte)
├── packed/          # Vom TexturePacker erzeugte Textur-Atlanten
├── skins/           # Skins für UI aus SkinComposer
├── Sprites/         # Einzelne Sprite-Dateien (nicht dokumentiert)
├── testAssets/      # Testmaterialien
├── TMX/             # Karten-Dateien aus Tiled (tmx)
├── TSX/             # Tileset-Dateien aus Tiled (tsx)
├── unpacked/        # Ungenutzte Einzelbilder zur Verarbeitung
├── config.json      # Projektkonfiguration
├── pack.json        # Konfiguration für TexturePacker
```
---

## 🧰 TexturePacker

Der **TexturePacker** wird verwendet, um Bilder aus dem Ordner `unpacked/` in ein komprimiertes Atlas-Format zu verpacken. Das Ergebnis wird im Ordner `packed/` gespeichert. Das verbessert Performance und Ladezeiten.

### ✅ Wichtig: Befehl zum Ausführen

```bash
./gradlew texturePacker
```

Führe diesen Befehl **immer aus**, wenn:

* neue Bilder hinzukommen,
* bestehende Bilder geändert werden,
* oder du etwas aus dem Atlas im Spiel verwenden willst.
* nach einem rebase oder merge sollte dies auch immer ausgeführt werden, wenn es konflikte gab im `packed/` folder.

---

## 🗺️ Tiled Editor

Der **Tiled Editor** dient zur Erstellung und Bearbeitung von Spielkarten. Diese bestehen aus:

* **TMX-Dateien** (Maps) im Ordner `TMX/`
* **TSX-Dateien** (Tilesets) im Ordner `TSX/`

Stelle sicher, dass alle Tilesets in Tiled auf die **gepackten Texturen** im Ordner `packed/` verweisen, **nicht auf die Originaldateien** in `unpacked/`.

---

## 🎨 SkinComposer

**SkinComposer** wird für das UI-Design verwendet. Alle Skins befinden sich im Ordner `skins/`.

Wenn du neue UI-Elemente hinzufügen oder ändern willst:

* Öffne das Projekt in SkinComposer
* Exportiere die Skins nach Bearbeitung
* Stelle sicher, dass sie im Spiel korrekt geladen werden

---
## Config.json

Unter assets gibt es auch noch ein config.json, diese Datei beinhaltet einige Konfigurationen für das Spiel.
---


## 6. Testing & Qualität

Wir verwenden eine abgestufte Teststrategie:

| Testtyp           | Beschreibung                            | Tools          |
| ----------------- | --------------------------------------- | -------------- |
| Unit-Tests        | Logik einzelner Systeme (z. B. Schaden) | JUnit, Mockito |
| Integration-Tests | Zusammenspiel mehrerer Module           | JUnit          |
| Screenshot-Tests  | Visuelle UI-Kontrolle (lokal)           | manuell/lokal  |

Entscheidungsmatrix: [Ashley ECS Testing](https://github.com/ZHAW-Team-Toxic/PM4-Team-Toxic/blob/main/README.md)

| **Komponente**                   | **Testart**           | **CI-fähig** | **Empfehlung**                                     |
|----------------------------------|-----------------------|--------------|----------------------------------------------------|
| Komponentenlogik (z. B. HP)      | Unit Test             | Ja           | Direkte Zustandsprüfungen                          |
| System-Logik (`processEntity`)   | Unit / Integration    | Ja           | Mock-Entities, ECS-Ausführung testen               |
| Engine + Systems gemeinsam       | Integration Test      | Ja           | Kompletten Update-Cycle simulieren                 |
| Entity-Lifecycle (create/destroy)| Integration Test      | Ja           | Komponenten hinzufügen/entfernen & prüfen          |
| System ↔ Stage-Interaktion       | Mixed Integration Test| Teilweise    | UI-Reaktion auf Systemaktionen simulieren          |
| ECS + Rendering                  | Screenshot Test only  | Nein         | Nur testen, wenn visuelle Korrektheit kritisch ist |

Tests befinden sich in:

```plaintext
core/src/test/java
```

---

## 7. Entwicklungsworkflow

### Branching-Strategie

* `main`: stabile Versionen
* `develop`: aktiver Entwicklungsstand
* `feature/<name>`: neue Features oder Fixes

### GitHub-Workflow

1. Erstelle einen neuen Feature-Branch
2. Implementiere & commite lokal
3. Push auf GitHub & öffne Pull Request (PR)
4. Code Review durch Teammitglied
5. Merge nach Freigabe

### Kommunikation & Koordination

* Kommunikation: **Microsoft Teams**
* Aufgabenverwaltung: **GitHub Projects** ([Kanban](https://github.com/orgs/ZHAW-Team-Toxic/projects/2))
* Architektur-Entscheidungen: siehe [Guidebook](https://github.com/ZHAW-Team-Toxic/PM4-Team-Toxic/wiki/00:-Software-Guidebook) Kapitel 12

---

## 8. Spielstände & Daten

* Speicherstände werden lokal im JSON-Format abgelegt
* Es gibt derzeit **einen Save-Slot**
* Kein Server, keine Cloud-Synchronisation
* Spielstände bleiben bei Updates erhalten

---

## 9. Optional: Update-Launcher

Ein optionaler Launcher prüft bei jedem Start auf neue GitHub-Versionen:

```bash
java -jar frontier-launcher.jar
```

Er lädt neue Builds automatisch herunter und ersetzt die lokale `.jar`.

---

## 10. Troubleshooting

| Problem                          | Lösung                                                  |
| -------------------------------- | ------------------------------------------------------- |
| Spiel zeigt schwarzen Bildschirm | Prüfe `assets/`-Verzeichnis und Dateipfade              |
| Java-Fehler (`JAVA_HOME`)        | Setze Umgebungsvariable `JAVA_HOME` auf JDK 21          |
| Kein Sound unter Linux           | Installiere `libopenal` oder `alsa`                     |
| IntelliJ erkennt Gradle nicht    | Im Gradle-Toolfenster auf „Reload All Projects“ klicken |
| UI leer / Karte fehlt            | Prüfe Tilemap-Pfad und lade Karte korrekt               |

---

## 11. Erste Schritte als Entwickler\:in

1. Lies das Game-Concept und Guidebook
2. Klone das Projekt & starte es lokal
3. Wähle ein offenes GitHub-Issue oder melde dich im Team
4. Erstelle einen Feature-Branch
5. Nutze `./gradlew spotlessApply` vor jedem Commit
6. Teste deine Änderung mit `./gradlew test`
7. Eröffne einen PR & hol Feedback

---

## 12. Kontakt & Support

| Ansprechpartner  | Rolle                                    |
| ---------------- | ---------------------------------------- |
| Kevin Sonnek     | Technischer Kontakt                      |
| Manuel Strenge   | Architektur/Gameplay                     |
| Thomas Hilberink | Core-Engine, ECS, Technischer Kontakt    |

Für Fragen:

* Teams-Channel `#frontier-dev`
* GitHub Issues bei technischen Problemen

---

> Dokument erstellt am **12. Mai 2025**
> Letzte Prüfung: `TODO` durch Teammitglied beim Einfügen ins Repository


## 13. Teststrategie & Empfehlungen

Das Projekt *Frontier* verwendet ein gestaffeltes Testmodell, das zwischen Unit-Tests, Integrationstests und optionalen Screenshot-Tests unterscheidet.

| Testtyp           | Beschreibung                               | CI-tauglich | Tools              |
|-------------------|--------------------------------------------|-------------|---------------------|
| Unit-Tests        | Einzelne System-Logik, z. B. Schadenberechnung | Ja       | JUnit, Mockito      |
| Integration-Tests | Zusammenspiel mehrerer Komponenten (UI + Logik) | Ja       | JUnit               |
| Screenshot-Tests  | Visuelle Validierung der UI (z. B. Layout) | Nein     | Lokal, manuell      |

### 💡 Empfehlung:

- Teste ECS-Systeme mit Unit- oder Integrationstests  
- Screenshot-Tests nur, wenn die visuelle Korrektheit *kritisch* ist
- Verwende `./gradlew test` zur Ausführung aller automatisierten Tests

## 14 CI/CD-Integration

Der CI/CD-Workflow (Continuous Integration / Continuous Deployment) sorgt dafür, dass Änderungen am Code automatisch getestet und bereitgestellt werden. Dies hilft uns, die Qualität des Projekts sicherzustellen und eine schnelle Entwicklung zu ermöglichen. Hier ist eine kurze Übersicht, wie der Workflow funktioniert:

### 1. Code-Änderungen und Branching-Strategie

- **Feature Branches:** Beginne jedes Feature oder Bugfix in einem neuen Branch, der von `main` abgezweigt wird. Benenne den Branch so, dass er das Feature oder Problem beschreibt, an dem du arbeitest.
  - Beispiel: `feature/"Issue-number"-add-new-tower` oder `bugfix/"Issue-number"-fix-tower-damage`

### 2. Pull Request (PR)

- **Erstelle einen Pull Request:** Sobald deine Änderungen abgeschlossen sind und getestet wurden, erstelle einen Pull Request (PR) von deinem Feature-Branch zum `main` Branch.
- **Code Review:** Dein PR wird von einem Teammitglied überprüft. Es wird geprüft, ob der Code die **Clean Code**-Standards erfüllt und keine Fehler oder Konflikte mit dem Haupt-Branch verursacht.
- **Testausführung:** Beim Erstellen des PRs wird der CI-Prozess automatisch ausgelöst. Der CI-Server führt eine Reihe von Tests aus (wie Unit-Tests, Integrationstests) und baut das Projekt, um sicherzustellen, dass der Code stabil bleibt.

### 3. CI-Checks und ihre Bedeutung

- **Build Frontier Projects / build-frontier:** Baut das Hauptprojekt. Muss erfolgreich sein.
- **Build Frontier Projects / build-launcher:** Baut den Launcher des Spiels. Muss erfolgreich sein.
- **Build Frontier Projects / detect-changes:** Überprüft, ob es Änderungen gibt, die einen neuen Build auslösen. Wird verwendet, um unnötige Builds zu vermeiden. **Nicht erforderlich für den Erfolg.**
- **Build Frontier Projects / Test Results:** Führt Tests aus und zeigt die Ergebnisse an. **Alle Tests müssen erfolgreich sein.**
- **SonarCloud Code Analysis:** Überprüft den Code auf Qualität, Fehler und Sicherheitslücken. Dieser Schritt ist **optional**, aber man sollte **die Kriterien des Quality Gates** erfüllen.

### 4. Merge

- **Merge:** Sobald der PR genehmigt und die Tests bestanden sind, wird ein `Squash Merge` ausgeführt.

### 5. Release & Deployment

Die Tags sollten das folgende Format haben:

- **Tag-Format:** `<projektname>-v<version>`
  - Beispiel: `frontier-v1.0.0` oder `frontier-launcher-v1.0.0`

**Varianten:**

- **Alpha/Beta-Versionen:** Tags mit `-alpha` oder `-beta` am Ende, z.B. `frontier-v1.0.0-alpha` oder `frontier-v1.0.0-beta` für Vorabversionen.
- **Finale Versionen:** Normale Tags ohne `-alpha` oder `-beta`, z.B. `frontier-v1.0.0`.

Sobald der Tag gepushed wird, wird automatishc einen Build für das jeweilige projekt ausgeführt und auf GitHub unter die Releases als Zip zurverfügung gestellt. Die Funktion `packageAll` verwendet **construo**, um die Version in den Builds zu integrieren.