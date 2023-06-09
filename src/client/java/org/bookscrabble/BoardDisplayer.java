package org.bookscrabble;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoardDisplayer extends Canvas {
    String[][] boardData;

    public BoardDisplayer() {
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
            System.out.println("not null");

//            double W = getWidth();
//            double H = getHeight();
            System.out.println("W: " + W);
            System.out.println("H: " + H);
            double w = W / boardData[0].length;
            double h = H / boardData.length;

            GraphicsContext gc = getGraphicsContext2D();

            /* going over the rows */
            System.out.println("boardData.length: " + boardData.length);
            for (int i = 0; i < boardData.length; i++)

                /* going over the columns */
                for (int j = 0; j < boardData[i].length; j++) {
                    if (boardData[i][j] == "0") {
                        gc.setFill(Color.GREEN); // Set the fill color to green
                        gc.fillRect(j * w, i * h, w, h);
                    }
                    if (boardData[i][j] == "1") {
                        gc.setFill(Color.RED); // Set the fill color to red
                        gc.fillRect(j * w, i * h, w, h);
                    }
                    if (boardData[i][j] == "2") {
                        gc.setFill(Color.BLUE); // Set the fill color to blue
                        gc.fillRect(j * w, i * h, w, h);
                    }
                    if (boardData[i][j] == "3") {
                        gc.setFill(Color.LIGHTBLUE); // Set the fill color to lightblue
                        gc.fillRect(j * w, i * h, w, h);
                    }
                    if (boardData[i][j] == "4") {
                        gc.setFill(Color.YELLOW); // Set the fill color to yellow
                        gc.fillRect(j * w, i * h, w, h);
                    }
                    if ((boardData[i][j] != "0") && (boardData[i][j] != "1") && (boardData[i][j] != "2") && (boardData[i][j] != "3") && (boardData[i][j] != "4")) {
                        gc.setFill(Color.YELLOW);
                        gc.fillRect(j * w, i * h, w, h);
                        gc.setFill(Color.BLACK);
                        gc.fillText(boardData[i][j], j * w + (w / 2), i * h + (h / 2));
                    }


                }
        }
    }


}
