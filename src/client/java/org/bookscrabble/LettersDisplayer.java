package org.bookscrabble;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import server.Tile;

public class LettersDisplayer extends Canvas {
    private String[] letters;
    private Tile[] playerTilesArray;
    public final int cellSize = 30;

    public LettersDisplayer() {
        this.letters = new String[7];
    }

    public void setLetters(String letters) {
        for (int i = 0; i < letters.length(); i++) {
            this.letters[i] = String.valueOf(letters.charAt(i));
        }
        redraw();
    }

    private void redraw() {
        if (letters == null) {
            System.out.println("Letters array is null.");
            return;
        }

        double width = getWidth();
        double height = getHeight();
        int rows = (int) Math.ceil((double) letters.length / (width / cellSize));
        int columns = (int) Math.ceil((double) letters.length / rows);

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        for (int i = 0; i < letters.length; i++) {
            int row = i / columns;
            int col = i % columns;

            double rectX = col * cellSize;
            double rectY = row * cellSize;

            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(rectX, rectY, cellSize, cellSize);

            gc.setFill(Color.BLACK);
            gc.setFont(new Font(14));
            gc.fillText(letters[i], rectX + 5, rectY + cellSize - 5);
        }
    }
}
