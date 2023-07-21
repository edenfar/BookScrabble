package org.bookscrabble;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LettersDisplayer extends Canvas {
    private String[] letters;
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
            int col = i % columns;
            double rectX = (col * cellSize * (1.5));
            double rectY = 0;
            Font boldFont = Font.font("Arial", FontWeight.BOLD, 30);

            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(rectX, rectY, cellSize * (1.5), cellSize * (1.5));
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeRect(rectX, rectY, cellSize * (1.5), cellSize * (1.5));
            gc.setFill(Color.BLACK);
            gc.setFont(boldFont);
            gc.fillText(letters[i], rectX + cellSize * 0.36, rectY + cellSize);

        }

    }
}
