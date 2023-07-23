package org.bookscrabble;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LettersDisplayer extends Canvas {
    private String[] letters;
    private String[] score;
    public final int cellSize = 30;

    public LettersDisplayer() {
        this.letters = new String[7];
    }

    public void setLetters(String letters, String score) {
        this.score = new String[score.length()];

        int j = 0;

        for (int i = 0; i < letters.length(); i++) {
            this.letters[i] = String.valueOf(letters.charAt(i));
        }
        for (int i = 0; j < score.length(); i++) {
            if (String.valueOf(letters.charAt(i)).equals("Z") || String.valueOf(letters.charAt(i)).equals("Q")) {
                String temp = String.valueOf(score.charAt(j));
                temp += String.valueOf(score.charAt(j + 1));
                this.score[i] = temp;
                j++;
            } else
                this.score[i] = String.valueOf(score.charAt(j));
            j++;
        }

        redraw();
    }

    private void redraw() {
        if (letters == null) {
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

            gc.setFill(Color.DARKBLUE);
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            if (letters[i].equals("Z") || letters[i].equals("Q")) {
                gc.fillText(score[i], rectX + cellSize, rectY + cellSize * 1.4);
            } else {
                gc.fillText(score[i], rectX + cellSize * 1.2, rectY + cellSize * 1.4);
            }

        }

    }
}
