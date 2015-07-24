package cz.upol.mobyg;

import cz.upol.mobyg.color.ColorHSV;
import cz.upol.mobyg.image.CenterOfGravity;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import java.awt.*;
import java.awt.image.*;

import static cz.upol.mobyg.utils.ColorUtils.*;
import static cz.upol.mobyg.utils.ImageUtils.saveImage;

public class Main extends Application {

    private Image image;
    private ColorHSV redHSV, greenHSV, blueHSV;
    final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);

    private Rotate rotateX = new Rotate();
    private Rotate rotateY = new Rotate();


    @Override
    public void start(Stage primaryStage) throws Exception {
        //Parent root1 = FXMLLoader.load(getClass().getResource("sample.fxml"));
        BorderPane root = new BorderPane();

        grabber.start();


        // preparation
        ImageView imageView = new ImageView();
        ImageView preview = new ImageView();
        Thread mainThread = createMainThread(imageView);

        // Radio buttons
        final ToggleGroup group = new ToggleGroup();

        RadioButton rb1 = new RadioButton("Red");
        rb1.setToggleGroup(group);
        rb1.setSelected(true);
        RadioButton rb2 = new RadioButton("Green");
        rb2.setToggleGroup(group);
        RadioButton rb3 = new RadioButton("Blue");
        rb3.setToggleGroup(group);

        HBox radioButtons = new HBox(rb1, rb2, rb3);

        // Box
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.LIGHTGRAY);
        material.setSpecularColor(Color.rgb(30, 30, 30));

        Shape3D box = new Box(2, 2, 2);
        box.setMaterial(material);
        box.setDrawMode(DrawMode.FILL);
        box.setCullFace(CullFace.BACK);


        rotateX = new Rotate();
        rotateX.setAxis(Rotate.X_AXIS);

        rotateY = new Rotate();
        rotateY.setAxis(Rotate.Y_AXIS);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll (
                rotateX,
                rotateY,
                new Translate(0, 0, -15));

        Group boxGroup = new Group();
        boxGroup.getChildren().add(camera);
        boxGroup.getChildren().add(box);

        SubScene groupScene = new SubScene(boxGroup, 300, 300);
        groupScene.setCamera(camera);



        // Normal buttons
        Button startButton = new Button("Start");
        startButton.setOnAction((event) -> mainThread.start());
        Button printButton = new Button("Print");
        printButton.setOnAction(event -> grabFrame(preview));

        HBox normalButtons = new HBox(startButton, printButton);
        VBox buttons = new VBox(radioButtons, normalButtons);

        /*normalButtons.addEventFilter(KeyEvent.KEY_PRESSED, event -> event.consume());
        buttons.addEventFilter(KeyEvent.KEY_PRESSED, event -> event.consume());*/

        root.setTop(buttons);

        preview.setOnMouseClicked(event -> calibrateColor(event, group, preview));
        root.setLeft(imageView);
        root.setRight(preview);
        root.setCenter(new Group(groupScene));

        primaryStage.setTitle("Mobyg - moving objects by gestures");
        primaryStage.setScene(new Scene(root, 1600, 570));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private Thread createMainThread(ImageView imageView) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                CenterOfGravity centerOfGravity = new CenterOfGravity();

                Frame grabbedFrame;
                BufferedImage bufferedImage;
                try {
                    while (true) {
                        grabbedFrame = grabber.grab();
                        Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
                        bufferedImage = java2DFrameConverter.convert(grabbedFrame);
                        image = SwingFXUtils.toFXImage(bufferedImage, null);

                        ColorHSV[][] colorHSVs = getPixelsHSV(image);

                        Point redPoint = centerOfGravity.getRedPosition();                   // before
                        int redXcoord = (int)redPoint.getX();
                        int redYcoord = (int)redPoint.getY();

                        Color[][] filteredRGBarray = centerOfGravity.filterPoints(colorHSVs, redHSV, greenHSV, blueHSV);
                        Color[][] erodedRGBarray = centerOfGravity.erosion(filteredRGBarray);
                        Color[][] centeredRGBArray = centerOfGravity.getCenterOfGravity(erodedRGBarray);

                        int angleToRotateY = (int) (redXcoord - redPoint.getX());                    //after
                        int angleToRotateX = (int) (redYcoord - redPoint.getY());

                        Platform.runLater(() -> rotateY.setAngle(rotateY.getAngle() + angleToRotateY));
                        Platform.runLater(() -> rotateX.setAngle(rotateX.getAngle() + angleToRotateX));

                        Image erodedTest = getPixelsRGB(centeredRGBArray);
                        Platform.runLater(() -> imageView.setImage(erodedTest));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);

        return thread;
    }

    private void calibrateColor(MouseEvent event, ToggleGroup group, ImageView imageView) {
        RadioButton selectedBtn = (RadioButton) group.getSelectedToggle();
        Color myColor = imageView.getImage().getPixelReader().getColor((int) event.getX(), (int) event.getY());
        ColorHSV pickedHSV = convertRGBtoHSV(myColor);

        switch (selectedBtn.getText()) {
            case "Red":
                redHSV = pickedHSV;
                System.out.println(pickedHSV.getHue() * 360 + ", " + pickedHSV.getSaturation() * 360 + ", " + pickedHSV.getValue() * 360);
                break;
            case "Green":
                greenHSV = pickedHSV;
                System.out.println(pickedHSV.getHue() * 360 + ", " + pickedHSV.getSaturation() * 360 + ", " + pickedHSV.getValue() * 360);
                break;
            case "Blue":
                blueHSV = pickedHSV;
                System.out.println(pickedHSV.getHue() * 360 + ", " + pickedHSV.getSaturation() * 360 + ", " + pickedHSV.getValue() * 360);
                break;
        }

    }

    //TODO predelat -> nema parametr a vraci image
    private void grabFrame(ImageView imageView) {
        //final OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        Image testImage;
        Frame grabbedFrame;
        BufferedImage bufferedImage;
        try {

            grabbedFrame = grabber.grab();
            Java2DFrameConverter java2DFrameConverter = new Java2DFrameConverter();
            bufferedImage = java2DFrameConverter.convert(grabbedFrame);
            image = SwingFXUtils.toFXImage(bufferedImage, null);
            testImage = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(testImage);
            //saveImage(image.getPixelReader(), "testGrab.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

