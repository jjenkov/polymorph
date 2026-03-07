package com.plmph.player.plugins.controldeck;

import com.plmph.player.PolymorphPlayerPlugin;
import com.plmph.player.PolymorphPlayerProxy;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class ControlDeckPlugin implements PolymorphPlayerPlugin {

    private double  mouseDownX = 0D;
    private double  mouseDownY = 0D;

    @Override
    public void init(PolymorphPlayerProxy proxy) {
        // executed from the JavaFX UI thread, so it is allowed to access the proxy methods without a call to runLaterInUiThread()

        // access the root window (Stage) and configure it with a dark theme.
        Stage rootStage = proxy.getRootStage();
        rootStage.initStyle(StageStyle.UNDECORATED);
        rootStage.setWidth(1024);
        rootStage.setHeight(576);

        int leftMargin = 32;
        int topMargin = 32;

        // mouse move listener
        addMouseListeners(rootStage, leftMargin, topMargin);

        // canvas
        Canvas canvas = new Canvas(1024, 576);
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsContext2D.setFill(Color.web("#121212"));
        graphicsContext2D.fillRect(0, 0, 1024, 576);

        // logo icon color
        Color logoIconColor = Color.web("#CDDC39");

        drawLogoIcon(graphicsContext2D, logoIconColor, leftMargin, topMargin);

        // draw top title text/
        drawLogoText(graphicsContext2D, leftMargin, topMargin, logoIconColor);

        // draw close window cross
        drawCloseCross(graphicsContext2D, canvas, leftMargin, topMargin);

        Pane rootPane = new Pane(canvas); // Pane has no layout built-in - which is what we want for the control deck.
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
}
