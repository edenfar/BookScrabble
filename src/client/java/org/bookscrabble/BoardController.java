package org.bookscrabble;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import viewmodel.ViewModel;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class BoardController implements Observer, Initializable {

    private ViewModel vm;
    @FXML
    BoardDisplayer boardDisplayer;
    @FXML
    LettersDisplayer lettersDisplayer;
    @FXML
    Button doneButton;

    private String[][] boardData;
    private String[] letterArray;
    private String letterclicked;

    private String word;
    private int r,c;

    private boolean vertical;

    public BoardController() {
        boardData = new String[][]{
                {"1", "0", "0", "3", "0", "0", "0", "1", "0", "0", "0", "3", "0", "0", "1"},
                {"0", "4", "0", "0", "0", "2", "0", "0", "0", "2", "0", "0", "0", "4", "0"},
                {"0", "0", "4", "0", "0", "0", "3", "0", "3", "0", "0", "0", "4", "0", "0"},
                {"3", "0", "0", "4", "0", "0", "0", "3", "0", "0", "0", "4", "0", "0", "3"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
                {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"},
        };

        letterArray = new String[]{"a","b","c","d"};
        letterclicked = "";
        c = -1;
        r = -1;
        word = "";
        vertical = true;

    }
    public void setViewModel(ViewModel vm) {
        this.vm = vm;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        boardDisplayer.setBoardData(boardData);
        boardDisplayer.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> boardDisplayer.requestFocus());

        boardDisplayer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            double x, y;

            @Override
            public void handle(MouseEvent mouseEvent) {
                double cellWidth = boardDisplayer.getWidth() / boardData[0].length;
                double cellHeight = boardDisplayer.getHeight() / boardData.length;

                int row = (int) (mouseEvent.getY() / cellHeight);
                int column = (int) (mouseEvent.getX() / cellWidth);

                System.out.println("Clicked on row: " + row + ", column: " + column);
                if( letterclicked != ""){
                    boardData[row][column] = letterclicked;
                    word =word.concat(letterclicked);
                    letterclicked = "";
                    if(r == -1){
                        r = row;
                        c = column;
                    }
                    else{
                        //if the letter in teh same row, the word is vertical
                        if(r == row){
                            vertical = false;
                            //check if the new letter is from the left
                            if(column<c){
                                c = (column);
                            }
                        }
                        else{
                            if(c == column){
                                vertical = true;
                                if(row<r){
                                    r = row;
                                }

                            }
                        }

                    }


                }
                boardDisplayer.setBoardData(boardData);

            }

        });

        lettersDisplayer.setLetters(letterArray);
        lettersDisplayer.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> lettersDisplayer.requestFocus());

        lettersDisplayer.setOnMouseClicked(new EventHandler<MouseEvent>() {
            double x, y;

            @Override
            public void handle(MouseEvent mouseEvent) {

                // Calculate the actual cell width based on the available width and number of letters
                double cellWidth = lettersDisplayer.cellSize;

                double mouseX = mouseEvent.getX();

                // Calculate the index based on the mouse position and cell width
                int index = (int) (mouseX / cellWidth);

                System.out.println("Clicked on letter at index: " + index);
                letterclicked = letterArray[index];
            }
        });
        doneButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> doneButton.requestFocus());

        doneButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            double x, y;

            @Override
            public void handle(MouseEvent mouseEvent) {
                checkBoard();
            }

        });
    }

    @Override
    public void update(Observable o, Object arg) {
        // Update logic here
    }

    public void checkBoard() {
        System.out.println("checkBoard");
        System.out.println(word);
        System.out.println(r);
        System.out.println(c);
        System.out.println(vertical);
        vm.playTurn(word,r,c,vertical);
        c = -1;
        r = -1;
        word = "";
        vertical = true;

        // check that all the words on the board are legal
    }
}
