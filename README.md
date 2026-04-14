

````md
# StructLab – Data Structure and Algorithm Visualization in Java

StructLab is a Java desktop app for exploring data structures, comparing implementations, and stepping through graph algorithms visually. It combines a JavaFX GUI with a terminal mode for interactive learning and testing.

---

## 🔧 Features

- 🧱 **Interactive Data Structure Exploration**
  - Run operations and inspect live state changes.
  - Explore structures through a visual GUI.

- ⚖️ **Implementation Comparison**
  - Compare multiple implementations side by side.
  - See how the same operations behave across variants.

- 🧠 **Graph Algorithm Lab**
  - Step through graph algorithms visually.
  - Playback controls, presets, and custom graph building.

- 📚 **Learn Tab**
  - Quick reference for structures and algorithms.
  - Built-in guide on how to use the app.

- ⚙️ **Desktop App + Terminal Mode**
  - JavaFX GUI for the main experience.
  - Terminal mode available for command-based interaction.

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your_username/StructLab.git
cd StructLab
````

### 2. Run the App

#### Option A: Run the Release JAR

Download the latest `structlab.jar` from the **Releases** page, then run:

```bash
java -jar structlab.jar
```

This launches the **GUI**.

Optional terminal mode:

```bash
java -jar structlab.jar --terminal
```

#### Option B: Run from Source

Make sure Java 17+ is installed.

Run the GUI:

```bash
./mvnw clean javafx:run
```

Run terminal mode:

```bash
./mvnw compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"
```

On Windows, use `.\mvnw.cmd` instead of `./mvnw`.

---

## 📈 Sample Usage

Use the app through these main sections:

* **Explore** → run operations on a structure and inspect its state
* **Compare** → compare implementations side by side
* **Learn** → read short explanations for structures, algorithms, and app usage
* **Algorithm Lab** → build or load a graph and step through an algorithm visually

---

## 🧠 Educational Use

This project was built for educational use, especially for understanding:

* how data structures behave internally
* how different implementations compare
* how graph algorithms work step by step
* how visual state changes connect to core logic

````

### Tiny recommendation
Replace this line:

```md
git clone https://github.com/your_username/StructLab.git
````

with your actual repo URL before pushing.

