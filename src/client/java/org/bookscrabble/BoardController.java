package org.bookscrabble;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import viewmodel.ViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    Button scoreBoardButton;
    @FXML
    Button closeButton;
    @FXML
    private Text playerName;
    @FXML
    private Text currentPlayerName;
    @FXML
    private Text playerScore;
    @FXML
    private Text roundsText;

    @FXML
    public VBox playerNameContainer;
    @FXML
    public VBox playerScoreContainer;
    @FXML
    public VBox playerClassementContainer;
    private String[][] boardData;
    private String[] letterArray;
    private Text playerTilesLetters;
    private String letterclicked;

    private String word;
    private int r, c;

    private boolean vertical;

    private int[][] boardDataRound;


    public BoardController(ViewModel vm) {
        this.currentPlayerName = new Text();
        this.playerName = new Text();
//        this.playerTilesArray = new Text();
        this.playerTilesLetters = new Text();
        this.playerScore = new Text();
        this.playerNameContainer = new VBox();
        this.playerScoreContainer = new VBox();
        this.playerClassementContainer = new VBox();
        this.roundsText = new Text();

        setViewModel(vm);

        boardDataRound = new int[15][15];
        boardData = new String[15][15];

        letterclicked = "";
        c = -1;
        r = -1;
        word = "";
        vertical = true;

    }

    public BoardController() {

    }

    public void setViewModel(ViewModel vm) {
        this.vm = vm;
        this.vm.addObserver(this);
    }

    public void checkBoard() {
        String wordToReplace = word;
        if (word.length() != 0)
            checkWord();
        System.out.println("checkBoard");
        System.out.println(word);
        System.out.println(r);
        System.out.println(c);
        System.out.println(vertical);
        vm.playTurn(word, r, c, vertical, wordToReplace);
        c = -1;
        r = -1;
        word = "";
        vertical = true;

        // check that all the words on the board are legal
    }


    public void setBoardAndDisplay() {
        currentPlayerName.textProperty().bind(this.vm.currPlayerName);
        playerName.textProperty().bind(this.vm.playerName);
        playerScore.textProperty().bind(this.vm.playerScore);
        playerTilesLetters.textProperty().bind(this.vm.playerTilesLetters);
        roundsText.setText(vm.round + " / " + vm.maxRounds);

        letterArray = new String[playerTilesLetters.getText().length()];

        for (int i = 0; i < playerTilesLetters.getText().length(); i++) {
            char letter = playerTilesLetters.getText().charAt(i);
            String temp = String.valueOf(letter);

            letterArray[i] = temp;
        }

        this.boardData = vm.boardData.clone();
        for (int i = 0; i < vm.boardData.length; i++) {
            this.boardData[i] = vm.boardData[i].clone();
        }


        boolean showEndButton = false;

        boardDisplayer.setBoardData(boardData);
        lettersDisplayer.setLetters(playerTilesLetters.getText());
        if (currentPlayerName.getText().equals(playerName.getText())) {
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
                    //delete letter from board by clicking on it only if it was put in same round
                    if (letterclicked == "" && boardDataRound[row][column] == vm.round) {
                        //add tile back to letter deck
                        int index = playerTilesLetters.getText().indexOf(boardData[row][column]);
                        if (letterArray[index].equals(""))
                            letterArray[index] = boardData[row][column];
                        letterArray[index] = boardData[row][column];
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
        doneButton.setVisible(showEndButton);
        scoreBoardButton.setOnAction(this::scoreBoard);
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

    //The word that actually get play is usually different from the tiles the player put on board because he used a letter that is already on board
    //This function will give us the actual word the player played, the tiles he got out of his deck and also update the word r and c
    public void checkWord() {
        int[] indices = null;
        int rowIndex = 0;
        int colIndex = 0;

        L1:
        for (int i = 0; i < boardDataRound.length; i++) {
            for (int j = 0; j < boardDataRound[i].length; j++) {
                if (boardDataRound[i][j] == vm.round) {
                    indices = new int[]{i, j};
                    break L1;
                }
            }
        }
        if (indices != null) {
            rowIndex = indices[0];
            colIndex = indices[1];
        }
        if (word.length() == 1) {
            vertical = boardData[rowIndex][colIndex - 1].equals("_") && boardData[rowIndex][colIndex + 1].equals("_");
        }
        if (vertical) {
            if (!boardData[rowIndex - 1][colIndex].equals("_")) {
                while (!boardData[rowIndex - 1][colIndex].equals("_")) {
                    rowIndex--;
                }
            }
            StringBuilder verticalString = new StringBuilder();
            for (int row = rowIndex; row < boardData.length && !boardData[rowIndex][colIndex].equals("_"); row++) {
                if (boardData[row][colIndex].equals("_")) {
                    break;
                }
                verticalString.append(boardData[row][colIndex]);
            }
            word = verticalString.toString();

        }
        if (!vertical) {
            if (!boardData[rowIndex][colIndex - 1].equals("_")) {
                while (!boardData[rowIndex][colIndex - 1].equals("_")) {
                    colIndex--;
                }
            }
            StringBuilder horizontalString = new StringBuilder();
            for (int col = colIndex; col < boardData.length && !boardData[rowIndex][colIndex].equals("_"); col++) {
                if (boardData[rowIndex][col].equals("_")) {
                    break;
                }
                horizontalString.append(boardData[rowIndex][col]);
            }
            word = horizontalString.toString();
        }
        r = rowIndex;
        c = colIndex;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StringProperty[] playerArray = vm.getPlayersArray();
        StringProperty[] scoreArray = vm.getPlayersScores();
        int playersDisplayed = Math.min(10, playerArray.length);

        String[] stringArray = new String[playersDisplayed];
        String[] stringArrayScore = new String[playersDisplayed];
        String[] names = new String[playersDisplayed];
        String[] scores = new String[playersDisplayed];


        for (int i = 0; i < playersDisplayed; i++) {
            String playerNameWithScore = playerArray[i].get();
            String[] parts = playerNameWithScore.split(":");
            String playerName = parts[0];
            stringArray[i] = playerName;
        }

        for (int i = 0; i < playersDisplayed; i++) {
            String playerNameWithScore = scoreArray[i].get();
            String[] parts = playerNameWithScore.split(":");
            String playerName = parts[0];
            stringArrayScore[i] = playerName;
        }

        // Create an array of indices
        Integer[] indices = new Integer[playersDisplayed];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        // Sort the indices array based on the values in string2
        Arrays.sort(indices, Comparator.comparingInt(index -> Integer.parseInt(stringArrayScore[(int) index])).reversed());
        int k = 0;
        // Print the sorted names and scores
        for (Integer index : indices) {
            names[k] = stringArray[index];
            scores[k] = stringArrayScore[index];
            k++;
        }


        playerNameContainer.getChildren().clear();
        for (String str : names) {
            Label label = new Label(str);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 25)); // Set font and size
            playerNameContainer.getChildren().add(label);
        }


        playerScoreContainer.getChildren().clear();
        for (String str : scores) {
            Label label = new Label(str);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 25)); // Set font and size
            playerScoreContainer.getChildren().add(label);
        }

        playerClassementContainer.getChildren().clear();
        for (int i = 0; i < stringArray.length; i++) {
            Label label = new Label((String.valueOf(i + 1)) + ".");
            label.setFont(Font.font("Arial", FontWeight.BOLD, 25)); // Set font and size
            playerClassementContainer.getChildren().add(label);
        }

    }

    @Override
    public void update(Observable o, Object arg) {
    }

    public void scoreBoard(ActionEvent event) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("score-board-screen.fxml"));

            BoardController controller = new BoardController();
            controller.setVm(vm); // Set the variable value
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 400, 400);
            Stage stage = new Stage();
            stage.setTitle("Score-Board");
            stage.setScene(scene);
            controller.closeButton.setOnAction(e -> stage.close());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setVm(ViewModel vm) {
        this.vm = vm;
    }
}
