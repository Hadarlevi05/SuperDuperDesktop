package Controllers;

import Models.SDMResultObject;
import Models.SuperDuperMarket;
import UIUtils.CommonUsed;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;

public class MainController {
    @FXML
    private Text userName;
    @FXML
    private Button changeUserButton;
    @FXML
    private Text currentBranch;
    @FXML
    private Button newBranchButton;
    @FXML
    private Button resetBranchButton;
    @FXML
    public Label sdmHeader;
    @FXML
    private Button changeRepoButton;
    @FXML
    private Button loadRepoButton;
    @FXML
    private Button createNewRepoButton;
    @FXML
    private Text currentRepo;
    @FXML
    private Text pathRepo;
    @FXML
    private Button changeLayoutButton;
    @FXML
    private HBox layoutHbox;
    @FXML
    private Button pinkButton;
    @FXML
    private Button blueButton;
    @FXML
    private Button greenButton;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private Label magitLargeHeader;
    @FXML
    private ComboBox<String> branchesOptionsComboBox;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button deleteBranchButton;
    @FXML
    private Pane textPane;
    @FXML
    private Pane showStatusPane;
    @FXML
    private AnchorPane treeAnchorPane;
    @FXML
    private Text commitTreeHeader;
    @FXML
    private Button showStatusButton;
    @FXML
    private Button commitButton;
    @FXML
    private Button pushButton;
    @FXML
    private Button pullButton;
    @FXML
    private Button mergeButton;
    @FXML
    private Button cloneButton;
    @FXML
    private Button fetchButton;
    @FXML
    private Button showCommitData;
    @FXML
    private Text wcStatusHeader;
    @FXML
    private GridPane gridHeader;
    @FXML
    private ColumnConstraints columnHeader;
    @FXML
    private ColumnConstraints columnButton;
    @FXML
    private ColumnConstraints footerColumn;
    //  =========================== Controllers ==================================
    private ShowMarketsController marketController = new ShowMarketsController();
    private ShowItemsController itemController = new ShowItemsController();
    private ShowCustomersController customersController = new ShowCustomersController();

    private PlaceOrderController placeOrderController = new PlaceOrderController();
    public MapController mapController = new MapController();
    private OrderHistoryController orderHistoryController = new OrderHistoryController();
    //  =========================== Scene Builder ================================
    private ScrollPane scrollPane = new ScrollPane();
    public boolean mapIsShow = false;

    //private PannableCanvas canvas;

    //  ================================ Utils ===================================
    //private Graph commitTreeGraph = new Graph();
    private SuperDuperMarket superDuperMarket = new SuperDuperMarket();
    private String remoteRepoPath;
    private Scene scene;
    private Stage primaryStage;
    private String style;
    private String secondBranchCommitSha1 = null;
    private boolean isShowStatusOpen = false;
    private boolean isGreenButtonPressed = false;
    private boolean isBlueButtonPressed = true;
    private boolean isPinkButtonPressed = false;


//  ============================= Util Functions ===============================

    @FXML
    void showMarkets() {
        marketController.showMarkets(superDuperMarket, textPane);
    }

    @FXML
    void showItems() {
        itemController.showItems(superDuperMarket, textPane);
    }

    @FXML
    void showLayoutButtons(ActionEvent event) {
        layoutHbox.setVisible(true);
    }

    @FXML
    void changeStyleToBlue(ActionEvent event) {
        //primaryStage.getScene().getStylesheets().clear();
        //primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style1.css").toExternalForm());
        //if(canvas != null) {
        //    canvas.setBackground(Background.EMPTY);
        //    style = "-fx-background-color: #3B5998";
        //    canvas.setStyle(style);
        //    scrollPane.setContent(canvas);
        //    treeAnchorPane.getChildren().clear();
        //    treeAnchorPane.getChildren().add(scrollPane);
        //    setTreeBoundaries();
        //    primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode.css").toExternalForm());
        //}
//
        //isBlueButtonPressed = true;
        //isGreenButtonPressed = false;
        //isPinkButtonPressed = false;
    }

    @FXML
    void changeStyleToGreen(ActionEvent event) {
        //primaryStage.getScene().getStylesheets().clear();
        //primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style 2.css").toExternalForm());
        //if(canvas != null) {
        //    canvas.setBackground(Background.EMPTY);
        //    style = "-fx-background-color: #405d27";
        //    canvas.setStyle(style);
        //    scrollPane.setContent(canvas);
        //    treeAnchorPane.getChildren().clear();
        //    treeAnchorPane.getChildren().add(scrollPane);
        //    setTreeBoundaries();
        //    primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode2.css").toExternalForm());
        //}
        //isGreenButtonPressed = true;
        //isBlueButtonPressed = false;
        //isPinkButtonPressed = false;
    }

    @FXML
    void changeStyleToPink(ActionEvent event) {
        //primaryStage.getScene().getStylesheets().clear();
        //primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/Style 3.css").toExternalForm());
        //if(canvas != null) {
        //    canvas.setBackground(Background.EMPTY);
        //    style = "-fx-background-color: #622569";
        //    canvas.setStyle(style);
        //    scrollPane.setContent(canvas);
        //    treeAnchorPane.getChildren().clear();
        //    treeAnchorPane.getChildren().add(scrollPane);
        //    setTreeBoundaries();
        //    primaryStage.getScene().getStylesheets().add(getClass().getResource("/Css/CommitNode3.css").toExternalForm());
        //}
//
        //isPinkButtonPressed = true;
        //isGreenButtonPressed = false;
        //isBlueButtonPressed = false;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void initialize(Stage primaryStage) {

        /* your code */
        //GridPane grid = new GridPane();
        /* your code */
        //ColumnConstraints column1 = new ColumnConstraints();
        columnButton.setPercentWidth(20);
        columnHeader.setPercentWidth(80);
        footerColumn.setPercentWidth(100);
        setPrimaryStage(primaryStage);
        if (mapIsShow) {
            mapController.refresh(textPane);
        }
        //gridHeader.getColumnConstraints().addAll()

        /*columnButton.setPercentWidth(10);
        columnHeader.setPercentWidth(20);

        gridHeader.getColumnConstraints().addAll(columnButton,columnHeader);*/
        //layoutHbox.setVisible(false);

        //currentRepo.wrappingWidthProperty().set(120);
        //userName.wrappingWidthProperty().set(70);
        //currentBranch.wrappingWidthProperty().set(50);

        //userName.textProperty().bind(myMagit.getUserName());
        //currentRepo.textProperty().bind(myMagit.getRepoName());
        //currentBranch.textProperty().bind(myMagit.getCurrentBranch());

        //treeAnchorPane.prefWidthProperty().bind(mainSplitPane.widthProperty());

        //newBranchButton.setDisable(true);
        //resetBranchButton.setDisable(true);
        //commitButton.setDisable(true);
        //showStatusButton.setDisable(true);
        //pushButton.setDisable(true);
        //pullButton.setDisable(true);
        //mergeButton.setDisable(true);
        //cloneButton.setDisable(true);
        //fetchButton.setDisable(true);
        //branchesOptionsComboBox.setDisable(true);
        //checkoutButton.setDisable(true);
        //deleteBranchButton.setDisable(true);
        //showCommitData.setDisable(true);
        //branchesOptionsComboBox.setOnAction(e -> {
        //    deleteBranchButton.setDisable(false);
        //    checkoutButton.setDisable(false);
        //});
    }

    private void setRepoActionsAvailable() {
        showStatusButton.setDisable(false);
        branchesOptionsComboBox.setDisable(false);
        newBranchButton.setDisable(false);
        resetBranchButton.setDisable(false);
        commitButton.setDisable(false);
        //branchesOptionsComboBox.setItems(myMagit.getCurrentBranchesNames());
        mergeButton.setDisable(false);
        cloneButton.setDisable(false);
        showCommitData.setDisable(false);
        fetchButton.setDisable(false);
        pullButton.setDisable(false);
        pushButton.setDisable(false);
    }

    @FXML
    void showMaps() {
        mapIsShow = true;
        textPane.getChildren().clear();
        mapController.ShowStoresAndOrdersOnMap(superDuperMarket, textPane);
    }

    @FXML
    void placeOrder() {
        mapIsShow = false;
        textPane.getChildren().clear();
        placeOrderController.placeOrder(superDuperMarket, textPane);
    }

    @FXML
    void loadSDMData() {
        mapIsShow = false;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML file!");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML file", "*.xml"));

        File selectFile = fileChooser.showOpenDialog(primaryStage);
        if (selectFile == null) {
            return;
        }

        try {
            if (isShowStatusOpen) {
                showStatusPane.getChildren().clear();
                isShowStatusOpen = false;
            }

            SDMResultObject res =
                    superDuperMarket.loadSDMFromXML(selectFile.getAbsolutePath());
            if (res.getIsHasError()) {
                CommonUsed.showError(res.getErrorMSG());
            } else {
                CommonUsed.showSuccess("File loaded to system succesfully!");
            }
        } catch (JAXBException e) {
            CommonUsed.showError(e.getMessage());
        }
    }

    @FXML
    public void showOrderHistory(ActionEvent actionEvent) {
        mapIsShow = false;
        textPane.getChildren().clear();
        orderHistoryController.showOrderHistory(superDuperMarket, textPane);
    }

    @FXML
    public void showCustomers(ActionEvent actionEvent) {
        mapIsShow = false;
        textPane.getChildren().clear();
        customersController.showCustomers(superDuperMarket, textPane);
    }

}

