# AntiPlague Coronavirus Game

![Project Screenshot](src/image/image.png)

## Overview
AntiPlague Coronavirus Game is a Java-based simulation game inspired by "Plague Inc." The game challenges players to prevent the infection of a virus spreading across a world map. With a graphical interface built using Java Swing, this project adheres to MVC design principles and avoids WYSIWYG tools for generating windows.

## Features

1. **World Map and Countries**:
   - A map divided into 11 countries.
   - Visualization of various transport methods (airlines, boats, trains) with at least 3 implemented methods.

2. **Virus Mechanics**:
   - Infection starts in a randomly chosen.
   - The spread and speed depend on selected difficulty levels (3 levels implemented).

3. **Upgrades**:
   - A system to purchase upgrades using points.
   - 10 upgrades, each modifying criteria like transport routes or infection resistance.

4. **Game Interface**:
   - Main menu with options for:
     - New Game
     - High Scores
     - Exit
   - Separate window for difficulty selection.
   - In-game time counter and point tracker.
   - High Score window with a scrollable JList for saved results.

5. **Multithreading**:
   - Separate threads for the time counter, virus spread, and other dynamic elements.

6. **Animations**:
   - Simple animations for transport connections (e.g., airplane icons moving along paths).

7. **Game Termination**:
   - Shortcut `Ctrl+Shift+Q` to exit to the main menu.

8. **Persistent High Scores**:
   - Rankings saved to ensure data retention across sessions.

## Technical Details

- **Framework**: Java Swing (No JavaFX)
- **Design Pattern**: MVC (Model-View-Controller)
- **Serialization**: Interface `Serializable` for saving high scores.
- **Scalability**: All windows are scalable, ensuring usability on various screen sizes.
- **Exception Handling**: User-friendly error messages displayed for any runtime issues.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/byzhs/AntiPlague-Game.git

2. Navigate to the project directory:
    ```bash
    cd AntiPlague-Game

3. Compile the project:
    ```bash
    javac -d bin src/**/*.java

4. Run the project:
    ```bash
    java -cp bin Main

## Usage

1. Launch the application to access the main menu.
2. Start a new game and select the difficulty level.
3. Use strategies and upgrades to stop the virus from infecting the entire world.
5. Save your high scores and compare with others.
