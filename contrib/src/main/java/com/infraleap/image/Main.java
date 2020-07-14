package com.infraleap.image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        //String imagePath = "/Users/ehaase/Desktop/bmp2dict/CircleW.bmp";
        //String imagePath = "/Users/ehaase/Desktop/bmp2dict/CircleW.i64";
        String imagePath = "/Users/ehaase/Desktop/bmp2dict/GMARBLES.PCX";
        BufferedImage myPicture = ImageIO.read(new File(imagePath));

        drawCrap(myPicture);

        show(myPicture);
    }

    private static void show(BufferedImage myPicture) {
        JLabel picLabel = new JLabel(new ImageIcon(myPicture));
        JPanel jPanel = new JPanel();
        jPanel.add(picLabel);
        JFrame f = new JFrame();
        f.setSize(new Dimension(myPicture.getWidth(), myPicture.getHeight()));
        f.add(jPanel);
        f.setVisible(true);
        f.pack();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void drawCrap(BufferedImage myPicture) {
        Graphics2D g = (Graphics2D) myPicture.getGraphics();
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.BLUE);
        g.drawRect(10, 10, myPicture.getWidth() - 20, myPicture.getHeight() - 20);
    }

}

