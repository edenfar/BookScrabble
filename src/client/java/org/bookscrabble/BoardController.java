package org.bookscrabble;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import viewmodel.ViewModel;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class BoardController extends Observable implements Observer, Initializable {

    private ViewModel vm;
    @FXML
    BoardDisplayer boardDisplayer;
    @FXML
    LettersDisplayer lettersDisplayer;
    @FXML
    Button doneButton;
    @FXML
    Button saveGameButton;
    @FXML
    private Text playerName;
    @FXML
    private Text currentPlayerName;
    @FXML
    private Text playerScore;
    private String[][] boardData;
    private String[] letterArray;
    private Text playerTilesArray;
    private Text playerTilesLetters;
    private String letterclicked;

    private String word;
    private int r, c;

    private boolean vertical;

    private int[][] boardDataRound;


    public BoardController(ViewModel vm) {
        this.currentPlayerName = new Text();
        this.playerName = new Text();
        this.playerTilesArray = new Text();
        this.playerTilesLetters = new Text();
        this.playerScore = new Text();

        setViewModel(vm);

        boardDataRound = new int[15][15];
        boardData = new String[15][15];

        letterclicked = "";
        c = -1;
        r = -1;
        word = "";
        vertical = true;

    }

    public void setViewModel(ViewModel vm) {
        this.vm = vm;
        this.vm.addObserver(this);
    }

    public void checkBoard() {
        System.out.println("checkBoard");
        System.out.println(word);
        System.out.println(r);
        System.out.println(c);
        System.out.println(vertical);
        vm.playTurn(word, r, c, vertical);
        c = -1;
        r = -1;
        word = "";
        vertical = true;

        // check that all the words on the board are legal
    }


    public void setBoardAndDisplay() {

        //TODO: Update Bag


        currentPlayerName.textProperty().bind(this.vm.currPlayerName);
        playerName.textProperty().bind(this.vm.playerName);
        playerTilesArray.textProperty().bind(this.vm.playerTiles);
        playerScore.textProperty().bind(this.vm.playerScore);
        playerTilesLetters.textProperty().bind(this.vm.playerTilesLetters);

        this.boardData = vm.boardData.clone();
        for (int i = 0; i < vm.boardData.length; i++) {
            this.boardData[i] = vm.boardData[i].clone();
        }

        boardDisplayer.setBoardData(boardData);
        lettersDisplayer.setLetters(playerTilesLetters.getText());

        boolean showEndButton = currentPlayerName.getText().equals(playerName.getText());
        doneButton.setVisible(showEndButton);
        if (showEndButton) {
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
                    //delete letter from board by clicking on it only if it was put in same round
                    if (letterclicked == "" && boardDataRound[row][column] == vm.round) {
                        //add tile back to letter deck
                        letterArray[findEmptyStringIndex(letterArray)] = boardData[row][column];
                        boardData[row][column] = "_";
                        boardDataRound[row][column] = 0;
                        word = word.substring(0, word.length() - 1);
                    }
                    if (!letterclicked.equals("") && ((boardData[row][column].equals("_")) || (boardDataRound[row][column] == vm.round))) {

                        //If we replace a letter we put on board on same turn
                        if (!word.equals("") && (boardDataRound[row][column] == vm.round) && !boardData[row][column].equals("_")) {
                            //add tile back to letter deck
                            letterArray[findEmptyStringIndex(letterArray)] = boardData[row][column];
                            word = word.substring(0, word.length() - 1);
                        }

                        boardData[row][column] = letterclicked;
                        boardDataRound[row][column] = vm.round;
                        word = word.concat(letterclicked);
                        letterclicked = "";
                        if (r == -1) {
                            r = row;
                            c = column;
                        } else {
                            //if the letter in teh same row, the word is vertical
                            if (r == row) {
                                vertical = false;
                                //check if the new letter is from the left
                                if (column < c) {
                                    c = (column);
                                }
                            } else {
                                if (c == column) {
                                    vertical = true;
                                    if (row < r) {
                                        r = row;
                                    }
                                }
                            }
                        }
                    }
                    boardDisplayer.setBoardData(boardData);
                    vm.setFirstRound();

                }
            });


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
                    letterArray = new String[playerTilesLetters.getText().length()];

                    for (int i = 0; i < playerTilesLetters.getText().length(); i++) {
                        char letter = playerTilesLetters.getText().charAt(i);
                        String temp = String.valueOf(letter);

                        letterArray[i] = temp;
                    }
                    letterclicked = letterArray[index];
                    letterArray[index] = "";

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
        saveGameButton.setVisible(vm.isHost);
        saveGameButton.setOnMouseClicked(mouseEvent -> vm.saveGame());
    }


    public static int findEmptyStringIndex(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals("")) {
                return i;
            }
        }
        return -1; // If empty string not found
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }


}
