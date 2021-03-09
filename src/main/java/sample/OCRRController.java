package sample;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import com.gtranslate.Language;
import com.gtranslate.Translator;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.RasterFormatException;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class OCRRController implements Initializable {

    double x1,y1,x,y,x2,y2,width,height;
    static Rectangle clip;
    Image image;
    BufferedImage igg ;
    String textFileDir= "D:";
    String FromText,FromLang,ToLang,ToText;
    Scene cur_scene;
    Tesseract tess = new net.sourceforge.tess4j.Tesseract();
    boolean is_copy,is_save,is_trans;
    @FXML
    private ImageView ImageArea;

    @FXML
    private javafx.scene.shape.Rectangle rectt;

    public void handleOCR(BufferedImage img, boolean iscopy,boolean issave,boolean isftrans,String filedir,String fromlang, String toLang) throws AWTException {
        is_copy = iscopy;
        is_save = issave;
        is_trans = isftrans;
        textFileDir = filedir;
        FromLang = fromlang;
        ToLang = toLang;
        tess.setDatapath(Paths.get("").toAbsolutePath().toString()+"\\tessdata");
        if (FromLang.equals("English")) tess.setLanguage("eng");
        else tess.setLanguage(FromLang);
        tess.setTessVariable("user_defined_dpi", "100");
        igg = img;
        image = SwingFXUtils.toFXImage(img,null);
        ImageArea.setImage(image);
        rectt.setVisible(false);
        rectt.setFill(Color.rgb(255,255,255,0.1));
    }

    @FXML
    public void Presser(MouseEvent event) throws IOException {
        x1 = event.getScreenX();
        y1 = event.getScreenY();
    }

    @FXML
    public  void Exitt(KeyEvent event) throws IOException {
        if(event.getCode().equals(KeyCode.ESCAPE)){
            Stage cur_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
            Parent root = loader.load();
            Scene cn = new Scene(root);
            cn.setFill(Color.TRANSPARENT);
            cur_stage.hide();
            cur_stage.setScene(cn);
            cur_stage.setMaximized(false);
            cur_stage.show();
        }
    }

    @FXML
    public void dragger(MouseEvent event){
        x2 = event.getX();
        y2 = event.getY();
        rectt.setX(Math.min(x2,x1));
        rectt.setY(Math.min(y2,y1));
        rectt.setWidth(Math.abs(x2-x1));
        rectt.setHeight(Math.abs(y2-y1));
        if(!rectt.isVisible()) rectt.setVisible(true);

    }

    @FXML
    public void Releaser(MouseEvent event) throws Exception {
        if(Math.pow(event.getScreenY()-y1,2)+ Math.pow(event.getScreenX()-x1,2) > 5) {
            BufferedImage processedImage = cropMyImage(igg, (int) rectt.getWidth(), (int) rectt.getHeight(), (int) rectt.getX(), (int) rectt.getY());
            rectt.setVisible(false);
            try {
                FromText = tess.doOCR(processedImage);
                ToText = FromText;
            } catch (TesseractException e) {
                System.err.println(e.getMessage());
            }
            if(is_copy){
                StringSelection stringSelection;
                if(is_trans)  stringSelection = new StringSelection(ToText);
            else stringSelection = new StringSelection(FromText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);}
            if(is_trans){
                String from="en",to="vi";
                switch (ToLang){
                    case "English":
                        to = "en";
                        break;
                    case "Vietnamese":
                        to = "vi";
                        break;
                    case "Japanese":
                        to = "ja";
                        break;
                    case "Chinese":
                        to = "zh-CN";
                        break;
                    case "Korean":
                        to = "ko";
                        break;
                    case "Russian":
                        to = "ru";
                        break;
                }
                switch (FromLang){
                    case "English":
                        from = "en";
                        break;
                    case "Vietnamese":
                        from = "vi";
                        break;
                    case "Japanese":
                        from = "ja";
                        break;
                    case "Chinese":
                        from = "zh-CN";
                        break;
                    case "Korean":
                        from = "ko";
                        break;
                    case "Russian":
                        from = "ru";
                        break;
                }
                ToText = FromText;
               // Translator translate = Translator.getInstance(); ToText = translate.translate(FromText, from, to);
            }
            if(is_save){
                SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
                Date date = new Date();
                BufferedWriter out = new BufferedWriter(new FileWriter(new File(textFileDir +"\\"+ (String)formatter.format(date) + ".txt" ), StandardCharsets.UTF_8, true));
                try {
                    if(is_trans) out.write(ToText);
                    else out.write(FromText);
                } finally {
                    out.close();
                }
            }

        }


    }

    public void Close(Stage stage, Scene backScene) throws IOException {
        Stage cur_stage = stage;
        backScene.setFill(Color.TRANSPARENT);
        cur_stage.hide();
        cur_stage.setScene(backScene);
        cur_stage.setMaximized(false);
        cur_stage.show();
    }

    public static BufferedImage cropMyImage(BufferedImage img, int cropWidth,
                                            int cropHeight, int cropStartX, int cropStartY) throws Exception {
        BufferedImage clipped = null;
        Dimension size = new Dimension(cropWidth, cropHeight);
        createClip(img, size, cropStartX, cropStartY);

        try {
            clipped = img.getSubimage(clip.x, clip.y, clip.width, clip.height);

        } catch (RasterFormatException rfe) {
            System.out.println("Raster format error: " + rfe.getMessage());
            return null;
        }
        return clipped;
    }

    /**
     * This method crops an original image to the crop parameters provided.
     *
     * If the crop rectangle lies outside the rectangle (even if partially),
     * adjusts the rectangle to be included within the image area.
     *
     * @param img = Original Image To Be Cropped
     * @param size = Crop area rectangle
     * @param clipX = Starting X-position of crop area rectangle
     * @param clipY = Strating Y-position of crop area rectangle
     * @throws Exception
     */
    private static void createClip(BufferedImage img, Dimension size,
                                   int clipX, int clipY) throws Exception {
/**
 * Some times clip area might lie outside the original image,
 * fully or partially. In such cases, this program will adjust
 * the crop area to fit within the original image.
 *
 * isClipAreaAdjusted flas is usded to denote if there was any
 * adjustment made.
 */
        boolean isClipAreaAdjusted = false;

/**Checking for negative X Co-ordinate**/
        if (clipX < 0) {
            clipX = 0;
            isClipAreaAdjusted = true;
        }
/**Checking for negative Y Co-ordinate**/
        if (clipY < 0) {
            clipY = 0;
            isClipAreaAdjusted = true;
        }

/**Checking if the clip area lies outside the rectangle**/
        if ((size.width + clipX) <= img.getWidth()
                && (size.height + clipY) <= img.getHeight()) {

/**
 * Setting up a clip rectangle when clip area
 * lies within the image.
 */

            clip = new Rectangle(size);
            clip.x = clipX;
            clip.y = clipY;
        } else {

/**
 * Checking if the width of the clip area lies outside the image.
 * If so, making the image width boundary as the clip width.
 */
            if ((size.width + clipX) > img.getWidth())
                size.width = img.getWidth() - clipX;

/**
 * Checking if the height of the clip area lies outside the image.
 * If so, making the image height boundary as the clip height.
 */
            if ((size.height + clipY) > img.getHeight())
                size.height = img.getHeight() - clipY;

/**Setting up the clip are based on our clip area size adjustment**/
            clip = new Rectangle(size);
            clip.x = clipX;
            clip.y = clipY;

            isClipAreaAdjusted = true;

        }
        if (isClipAreaAdjusted)
            System.out.println("Crop Area Lied Outside The Image."
                    + " Adjusted The Clip Rectanglen");
    }

    public static void writeImage(BufferedImage img, String fileLocation,
                                  String extension) {
        try {
            BufferedImage bi = img;
            File outputfile = new File(fileLocation);
            ImageIO.write(bi, extension, outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize (URL url, ResourceBundle rb){
    }
}