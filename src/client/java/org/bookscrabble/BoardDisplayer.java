package org.bookscrabble;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoardDisplayer extends Canvas {
    String[][] boardData;
    String[][] colorData;

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
            System.out.println("null");
        } else {
            double W = getWidth();
            double H = getHeight();
            double w = W / boardData[0].length;
            double h = H / boardData.length;

            GraphicsContext gc = getGraphicsContext2D();

            System.out.println("boardData.length: " + boardData.length);
            for (int i = 0; i < boardData.length; i++) {
                for (int j = 0; j < boardData[i].length; j++) {
                    if (boardData[i][j] != null) {
                        gc.setFill(Color.YELLOW);
                        gc.fillRect(j * w, i * h, w, h);
                        gc.setFill(Color.BLACK);
                        gc.fillText(boardData[i][j], j * w + (w / 2), i * h + (h / 2));
                    }
                    else {
                        if (colorData[i][j] == "0") {
                            gc.setFill(Color.GREEN); // Set the fill color to green
                            gc.fillRect(j * w, i * h, w, h);
                        }
                        if (colorData[i][j] == "1") {
                            gc.setFill(Color.RED); // Set the fill color to red
                            gc.fillRect(j * w, i * h, w, h);
                        }
                        if (colorData[i][j] == "2") {
                            gc.setFill(Color.BLUE); // Set the fill color to blue
                            gc.fillRect(j * w, i * h, w, h);
                        }
                        if (colorData[i][j] == "3") {
                            gc.setFill(Color.LIGHTBLUE); // Set the fill color to lightblue
                            gc.fillRect(j * w, i * h, w, h);
                        }
                        if (colorData[i][j] == "4") {
                            gc.setFill(Color.YELLOW); // Set the fill color to yellow
                            gc.fillRect(j * w, i * h, w, h);
                        }
                    }
                }
            }
        }
    }
}
