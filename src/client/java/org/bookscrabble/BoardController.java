package org.bookscrabble;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import server.Board;
import server.Tile;
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
    private Text playerName;
    @FXML
    private Text currentPlayerName;
    @FXML
    private Text playerScore;
    private String[][] boardData;
    private Board board;
    private Tile[][] boardTiles;
    private String[] letterArray;
    private Text playerTilesArray;
    private Text playerTilesLetters;
    private String letterclicked;


    private String word;
    private int r, c;

    private boolean vertical;


    public BoardController(ViewModel vm) {
        this.currentPlayerName = new Text();
        this.playerName = new Text();
        this.playerTilesArray = new Text();
        this.playerTilesLetters = new Text();
        this.playerScore = new Text();

        setViewModel(vm);

        boardData = new String[15][15];


        this.boardTiles = new Tile[15][15];

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


    public void setBoardAndDisplay(){

        currentPlayerName.textProperty().bind(this.vm.currPlayerName);
        playerName.textProperty().bind(this.vm.playerName);
        playerTilesArray.textProperty().bind(this.vm.playerTiles);
        playerScore.textProperty().bind(this.vm.playerScore);
        playerTilesLetters.textProperty().bind(this.vm.playerTilesLetters);
        this.boardTiles = vm.boardTiles;


            for (int i = 0; i < boardTiles.length; i++) {
                for (int j = 0; j < boardTiles[i].length; j++) {
                    if (boardTiles[i][j] != null){
                        boardData[i][j] = String.valueOf(boardTiles[i][j].letter);
                    }
                }
            }

        boolean showEndButton = false;

        boardDisplayer.setBoardData(boardData);
        lettersDisplayer.setLetters(playerTilesLetters.getText());

        if(currentPlayerName.getText().equals(playerName.getText())) {
            showEndButton = true;
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
                    if (letterclicked != "") {
                        boardData[row][column] = letterclicked;
                        boardTiles[row][column] = toTiles(letterclicked);

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
                }
            });
            doneButton.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> doneButton.requestFocus());

            doneButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                double x, y;

                @Override
                public void handle(MouseEvent mouseEvent) {
                    checkBoard();
                    //If word added to board we need to send new board to game (still ongoing)
                    vm.setBoardTiles(boardTiles);
                }

            });
        }
        doneButton.setVisible(showEndButton);
    }

    private Tile toTiles(String letterclicked) {
            int index = playerTilesArray.getText().indexOf(letterclicked);
            if (index + 1 < playerTilesArray.getText().length()) {
                char nextChar = playerTilesArray.getText().charAt(index + 1);
                if (Character.isDigit(nextChar)) {
                    Tile tile = new Tile(letterclicked.charAt(0), Character.getNumericValue(nextChar));
                    return tile;
                }
            }
            return null;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void update(Observable o, Object arg) {
    }


}
