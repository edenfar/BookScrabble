package org.bookscrabble;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BoardDisplayer extends Canvas {
    String[][] boardData;
    String[][] colorData;
    Font boldFont = Font.font("Arial", FontWeight.BOLD, 15);

    public BoardDisplayer() {
        this.colorData = new String[][]{
                {"1", "0", "0", "3", "0", "0", "0", "1", "0", "0", "0", "3", "0", "0", "1"},
                {"0", "4", "0", "0", "0", "2", "0", "0", "0", "2", "0", "0", "0", "4", "0"},
                {"0", "0", "4", "0", "0", "0", "3", "0", "3", "0", "0", "0", "4", "0", "0"},
                {"3", "0", "0", "4", "0", "0", "0", "3", "0", "0", "0", "4", "0", "0", "3"},
                {"0", "0", "0", "0", "4", "0", "0", "0", "0", "0", "4", "0", "0", "0", "0"},
                {"0", "2", "0", "0", "0", "2", "0", "0", "0", "2", "0", "0", "0", "2", "0"},
                {"0", "0", "3", "0", "0", "0", "3", "0", "3", "0", "0", "0", "3", "0", "0"},
                {"1", "0", "0", "3", "0", "0", "0", "4", "0", "0", "0", "3", "0", "0", "1"},
                {"0", "0", "3", "0", "0", "0", "3", "0", "3", "0", "0", "0", "3", "0", "0"},
                {"0", "2", "0", "0", "0", "2", "0", "0", "0", "2", "0", "0", "0", "2", "0"},
                {"0", "0", "0", "0", "4", "0", "0", "0", "0", "0", "4", "0", "0", "0", "0"},
                {"3", "0", "0", "4", "0", "0", "0", "3", "0", "0", "0", "4", "0", "0", "3"},
                {"0", "0", "4", "0", "0", "0", "3", "0", "3", "0", "0", "0", "4", "0", "0"},
                {"0", "4", "0", "0", "0", "2", "0", "0", "0", "2", "0", "0", "0", "4", "0"},
                {"1", "0", "0", "3", "0", "0", "0", "1", "0", "0", "0", "3", "0", "0", "1"},
        };
    }

    public void setBoardData(String[][] boardData) {
        this.boardData = boardData;
        redrow();
    }

    private void redrow() {
        if (boardData == null) {
        } else {
            double W = getWidth();
            double H = getHeight();
            double w = W / boardData[0].length;
            double h = H / boardData.length;

            GraphicsContext gc = getGraphicsContext2D();

            for (int i = 0; i < boardData.length; i++) {
                for (int j = 0; j < boardData[i].length; j++) {
                    if (!boardData[i][j].equals("_")) {
                        gc.setFill(Color.web("#E8CE4D"));
                        gc.setFont(boldFont);
                        gc.fillRect(j * w, i * h, w, h);
                        gc.setFill(Color.BLACK);
                        gc.fillText(boardData[i][j], j * w + 10, i * h + (h / 2) + 5);
                    } else {
                        gc.setFill(getColor(colorData[i][j])); // Set the fill color
                        gc.fillRect(j * w, i * h, w, h);
                    }
                }
            }
        }
    }

    private Paint getColor(String color) {
        if (color == "0") {
            return Color.GREEN;
        }
        if (color == "1") {
            return Color.RED;
        }
        if (color == "2") {
            return Color.BLUE;
        }
        if (color == "3") {
            return Color.LIGHTBLUE;
        }
        if (color == "4") {
            return Color.YELLOW;
        }
        return Color.BLACK;
    }
}
