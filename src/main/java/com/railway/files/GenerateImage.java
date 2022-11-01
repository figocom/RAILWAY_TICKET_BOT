package com.railway.files;

import com.railway.container.UserContainer;
import com.railway.controller.UserController;
import com.railway.db.Database;
import com.railway.entity.Place;
import com.railway.entity.Train;
import com.railway.entity.Wagon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateImage {
//    output image
    public static String imagePath = "src/main/resources/Images/generated/";

//    template background images
    public static String imageTemplate38 = "src/main/resources/Templates/template38.png";

//    chair template
    public static String pathGreenLeft = "src/main/resources/Images/green_left_38x38.png";
    public static String pathGreenRight = "src/main/resources/Images/green_right_38x38.png";
    public static String pathGrayLeft = "src/main/resources/Images/gray_left_38x38.png";
    public static String pathGrayRight = "src/main/resources/Images/gray_right_38x38.png";

    public static void generateImage(String chatId, String reysId) {
        try {
            Train train = Database.getTrainById(Database.getReysById(reysId).getTrain_id());
            Wagon wagon = UserContainer.wagonList.stream().filter(wagoncha->wagoncha.getNumber().equals(UserContainer.currentWagonNumber)).findFirst().get();
            UserContainer.currentWagonId = wagon.getId();
            List<Place> placeList = Database.getPlacesByWagonId(wagon.getId());

            UserContainer.currentPlaceList = placeList.stream().filter(Place::is_active).toList();
            BufferedImage bufferedImage = ImageIO.read(new File(imageTemplate38));
            BufferedImage seatGreenLeft = ImageIO.read(new File(pathGreenLeft));
            BufferedImage seatGreenRight = ImageIO.read(new File(pathGreenRight));
            BufferedImage seatGrayLeft = ImageIO.read(new File(pathGrayLeft));
            BufferedImage seatGrayRight = ImageIO.read(new File(pathGrayRight));

            Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();

            Font font = new Font("Serif", Font.PLAIN, 30);
            g2d.setFont(font);
            g2d.setColor(Color.BLACK);
//          vagon nomer
            g2d.drawString(String.valueOf(UserContainer.currentWagonNumber),101,118);
//            vagon type
            g2d.drawString("("+wagon.getType()+")",250,118);
//          hamma bosh joylar
            g2d.drawString(String.valueOf(placeList.stream().filter(placecha->placecha.is_active()).count()),217,512);
//          tepadagi bosh joy
            g2d.drawString(String.valueOf(placeList.stream().filter(placecha->placecha.is_in_on_top()&&placecha.is_active()).count()),262,578);
//          pasdagi bosh joy
            g2d.drawString(String.valueOf(placeList.stream().filter(placecha->!placecha.is_in_on_top()&&placecha.is_active()).count()),278,653);
//            draw seats
            int x = 493;
            int count = 1;
            g2d.setColor(Color.WHITE);
            font = new Font("Serif", Font.PLAIN, 13);
            g2d.setFont(font);
            for(int y = 1199; y>=231; y-=121){
                g2d.drawImage((placeList.get(count-1).is_active())?seatGreenRight:seatGrayRight,x,y,null);
                g2d.drawString(String.valueOf(count++),x+15,y+25);

                g2d.drawImage((placeList.get(count-1).is_active())?seatGreenLeft:seatGrayLeft, x+100,y,null);
                g2d.drawString(String.valueOf(count++),x+115,y+25);

                g2d.drawImage((placeList.get(count-1).is_active())?seatGreenRight:seatGrayRight, x, y-68, null);
                g2d.drawString(String.valueOf(count++),x+15,y-43);

                g2d.drawImage((placeList.get(count-1).is_active())?seatGreenLeft:seatGrayLeft, x+100, y-68, null);
                g2d.drawString(String.valueOf(count++),x+115,y-43);
            }
            g2d.drawImage((placeList.get(count-1).is_active())?seatGreenRight:seatGrayRight, x, 1252, null);
            g2d.drawString(String.valueOf(count++),x+15,1277);

            g2d.drawImage((placeList.get(count-1).is_active())?seatGreenLeft:seatGrayLeft, x+100, 1252, null);
            g2d.drawString(String.valueOf(count),x+115,1277);

            File file = new File(imagePath+chatId+".png");
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
