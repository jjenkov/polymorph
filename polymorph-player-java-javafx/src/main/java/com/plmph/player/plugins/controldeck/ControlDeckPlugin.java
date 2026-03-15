package com.plmph.player.plugins.controldeck;

import com.plmph.player.Playable;
import com.plmph.player.PolymorphPlayerPlugin;
import com.plmph.player.PolymorphPlayerProxy;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.List;

public class ControlDeckPlugin implements PolymorphPlayerPlugin {

    private PolymorphPlayerProxy proxy;
    private Canvas canvas = null;

    private int leftMargin = 32;
    private int topMargin = 32;

    private double  mouseDownX = 0D;
    private double  mouseDownY = 0D;

    @Override
    public void init(PolymorphPlayerProxy proxy) {
        this.proxy = proxy;
        // executed from the JavaFX UI thread, so it is allowed to access the proxy methods without a call to runLaterInUiThread()

        // access the root window (Stage) and configure it with a dark theme.
        Stage rootStage = proxy.getRootStage();
        rootStage.initStyle(StageStyle.UNDECORATED);
        rootStage.setWidth(1024);
        rootStage.setHeight(576);



        // mouse move listener
        addMouseListeners(rootStage, leftMargin, topMargin);
        addKeyboardListeners(rootStage, leftMargin, topMargin);

        // canvas
        this.canvas = new Canvas(1024, 576);
        GraphicsContext graphicsContext2D = this.canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(Color.web("#121212"));
        graphicsContext2D.fillRect(0, 0, 1024, 576);

        // logo icon color
        Color logoIconColor = Color.web("#CDDC39");

        drawLogoIcon(graphicsContext2D, logoIconColor, leftMargin, topMargin);

        // draw top title text/
        drawLogoText(graphicsContext2D, leftMargin, topMargin, logoIconColor);

        // draw close window cross
        drawCloseCross(graphicsContext2D, this.canvas, leftMargin, topMargin);

        Pane rootPane = new Pane(this.canvas); // Pane has no layout built-in - which is what we want for the control deck.
        Scene scene = new Scene(rootPane);
        rootStage.setScene(scene);

        rootStage.show();
    }

    private static void drawCloseCross(GraphicsContext graphicsContext2D, Canvas canvas, int leftMargin, int topMargin) {
        graphicsContext2D.setLineWidth(2);
        graphicsContext2D.beginPath();
        graphicsContext2D.moveTo(canvas.getWidth() - leftMargin +  8, topMargin - 16);
        graphicsContext2D.lineTo(canvas.getWidth() - leftMargin + 16, topMargin -  8);
        graphicsContext2D.moveTo(canvas.getWidth() - leftMargin + 16, topMargin - 16);
        graphicsContext2D.lineTo(canvas.getWidth() - leftMargin +  8, topMargin -  8);
        graphicsContext2D.stroke();
        graphicsContext2D.closePath();
    }

    private static void drawLogoText(GraphicsContext graphicsContext2D, int leftMargin, int topMargin, Color logoIconColor) {
        Font fontPolymorph = Font.font("Roboto Condensed", FontWeight.BOLD, 48);
        Font fontPlayer    = Font.font("Roboto Condensed", FontWeight.BOLD, 48);

        graphicsContext2D.setFont(fontPolymorph);
        graphicsContext2D.setFill(Color.web("#FFFFFF"));
        graphicsContext2D.fillText("Polymorph", leftMargin + 48, topMargin + 32);
        graphicsContext2D.setFill(logoIconColor);

        Text text = new Text();
        text.setFont(fontPolymorph);
        text.setText("Polymorph");
        double playerLeftAdjust = text.getLayoutBounds().getWidth();

        graphicsContext2D.setFont(fontPlayer);
        graphicsContext2D.fillText("Player", leftMargin + 48 + playerLeftAdjust + 8, topMargin + 32);
    }

    private static void drawLogoIcon(GraphicsContext graphicsContext2D, Color logoIconColor, int leftMargin, int topMargin) {
        //draw logo icon
        graphicsContext2D.setFill(logoIconColor);
        graphicsContext2D.setStroke(logoIconColor);
        graphicsContext2D.setLineWidth(4);
        graphicsContext2D.setLineCap(StrokeLineCap.ROUND);
        graphicsContext2D.setLineJoin(StrokeLineJoin.ROUND);

        graphicsContext2D.beginPath();
        graphicsContext2D.moveTo(leftMargin, topMargin);
        graphicsContext2D.lineTo(leftMargin + 16, topMargin + 16);
        graphicsContext2D.lineTo(leftMargin, topMargin + 32);
        graphicsContext2D.closePath();
        graphicsContext2D.fill();
        graphicsContext2D.stroke();
    }

    private void addMouseListeners(Stage rootStage, int leftMargin, int topMargin) {
        rootStage.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            mouseDownX = event.getScreenX();
            mouseDownY = event.getScreenY();
        });
        rootStage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getSceneX() > rootStage.getWidth() - leftMargin && event.getSceneY() < topMargin){
                rootStage.close();
            }
        });

        rootStage.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
             double mouseNowX =  event.getScreenX();
             double mouseNowY =  event.getScreenY();

             double xMoved = mouseNowX - mouseDownX;
             double yMoved = mouseNowY - mouseDownY;

             rootStage.setX(rootStage.getX() + xMoved);
             rootStage.setY(rootStage.getY() + yMoved);

             mouseDownX = mouseNowX;
             mouseDownY = mouseNowY;
        });
    }

    private void addKeyboardListeners(Stage rootStage, int leftMargin, int topMargin) {
        rootStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.R){
                System.out.println("R key pressed: " + event.toString());
                openFileChooser(rootStage, "Run a File...");
            }
        });
    }

    private void openFileChooser(Stage rootStage, String dialogTitle) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(dialogTitle);
        fileChooser.setInitialDirectory(new File("."));

        File selectedFile = fileChooser.showOpenDialog(rootStage);

        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            System.out.println("File name only: " + selectedFile.getName());

            // play file in same play group
            Playable playable = new Playable(Playable.SCRIPT, selectedFile.getAbsolutePath());
            this.proxy.getPlayGroup().play(playable);
        }
        drawPlayGroups(this.canvas);
    }

    private void drawPlayGroups(Canvas canvas) {
        Font fontText = Font.font("Roboto Condensed", FontWeight.BOLD, 24);

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFont(fontText);

        // only shows all playables in this play group, but should show all in entire application.
        int x = leftMargin;
        int y = topMargin + 100;
        for( Playable playable : this.proxy.getPlayGroup().getPlayables()){
            graphicsContext2D.setFill(Color.web("#ffffff"));

            graphicsContext2D.fillText( playable.getAddress(), x, y );
            y+=40;
        }
    }
}
