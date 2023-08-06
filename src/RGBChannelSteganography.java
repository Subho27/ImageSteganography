import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

public class RGBChannelSteganography {

    public static void imageSteganographyEncode(BufferedImage image, String filename)
    {
        String indicator = "", secret = "";
        int capacity = 0;
        int k = 0;
        indicator = getIndicator(image);
        capacity = calculateCapacity(image, indicator);
        System.out.println(capacity + " bits(total)");
        System.out.println((capacity/5) + " bits(should be embedded)");
        System.out.println((capacity/5)/8 + " letters");

        System.out.print("Enter the secret text to hide : ");
        Scanner scanner = new Scanner(System.in);
        secret = scanner.nextLine();
        secret = StringtoBinary.strToBinary(secret);
        System.out.println("Size of secret text : " + secret.length() + " bits");
        if(secret.length() <= capacity/5) {
            String[] cypher = new String[secret.length() / 4];
            for (int i = 0; i < cypher.length; i++) {
                cypher[i] = secret.substring(k, k + 4);
                k = k + 4;
            }
            image = stegoMethod(image, indicator, cypher);
            try {
                File outfile = new File("stego_im.png");
                /*String extension = "";
                int e = filename.lastIndexOf('.');
                if (e > 0) {
                    extension = filename.substring(e + 1);
                }*/
                ImageIO.write(image, "png", outfile);
                int pixel = image.getRGB(256,3);
                int cha = (pixel >> 16) & 0xff;
                int in = (pixel >> 8) & 0xff;
                int ch = (pixel) & 0xff;
                System.out.print(cha+" "+in+" "+ch);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("For this text you need a image of greater in size.");
        }
    }

    public static void imageSteganographyDecode(BufferedImage image)
    {
        String indicator = "", result = "";
        //indicator = findIndicator(image);
        //System.out.print(indicator);
        result = stegoDecode(image, "green");
        System.out.print(result);
    }

    private static String stegoDecode(BufferedImage image, String indicator)
    {
        String result = "";
        int w = image.getWidth();
        int h = image.getHeight();
        int indicator_channel = 0, channel1 = 0, channel2 = 0, alpha = 0;
        String ch1 = "", ch2 = "", ch3 = "", al ="";
        int p = image.getRGB(256,3);
        int c = (p >> 16) & 0xff;
        int i2 = (p >> 8) & 0xff;
        int c2 = (p) & 0xff;
        System.out.print(c+" "+i2+" "+c2);
        if(indicator.equals("green")) {
            int flag = 0,f = 0, length = 40;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if(result.length() == length) {
                        flag = 1;
                        break;
                    }
                    int pixel = image.getRGB(j, i);
                    channel1 = (pixel >> 16) & 0xff;
                    indicator_channel = (pixel >> 8) & 0xff;
                    channel2 = (pixel) & 0xff;
                    String helper = "00000000";
                    ch1 = helper.substring(Integer.toBinaryString(indicator_channel).length()) + Integer.toBinaryString(indicator_channel);
                    ch2 = helper.substring(Integer.toBinaryString(channel1).length()) + Integer.toBinaryString(channel1);
                    ch3 = helper.substring(Integer.toBinaryString(channel2).length()) + Integer.toBinaryString(channel2);
                    if(channel1 > 63 && channel2 > 63) {
                        continue;
                    }
                    else {
                        if(ch1.charAt(7) == '0'){
                            result = result + ch2.substring(4);
                        }
                        else{
                            result = result + ch3.substring(4);
                        }
                    }

                }
                if (flag == 1) {
                    break;
                }
            }
        }
        return result;
    }

    private static String findIndicator(BufferedImage image) {
        int alpha = 0;
        String alpha_bin = "";
        int pixel = image.getRGB(0, 0);
        alpha = (pixel >> 24) & 0xff;
        System.out.print(alpha);
        String helper = "00000000";
        alpha_bin = helper.substring(Integer.toBinaryString(alpha).length()) + Integer.toBinaryString(alpha);
        alpha_bin = alpha_bin.substring(6);
        if(alpha_bin == "00"){ return "green"; }
        else if(alpha_bin == "01"){ return "red"; }
        else if(alpha_bin == "10"){ return "blue"; }
        else{ return "error decoding"; }
    }

    private static BufferedImage stegoMethod(BufferedImage image, String indicator, String[] cypher)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage bufferedImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        int indicator_channel = 0, channel1 = 0, channel2 = 0, alpha = 0;
        String ch1 = "", ch2 = "", ch3 = "", al ="";
        if(indicator.equals("green")) {
            int c = 0, flag = 0;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {

                    int pixel = image.getRGB(j, i);
                    channel1 = (pixel >> 16) & 0xff;
                    indicator_channel = (pixel >> 8) & 0xff;
                    channel2 = (pixel) & 0xff;
                    String helper = "00000000";
                    ch1 = helper.substring(Integer.toBinaryString(indicator_channel).length()) + Integer.toBinaryString(indicator_channel);
                    ch2 = helper.substring(Integer.toBinaryString(channel1).length()) + Integer.toBinaryString(channel1);
                    ch3 = helper.substring(Integer.toBinaryString(channel2).length()) + Integer.toBinaryString(channel2);
                    System.out.print(ch1+" "+ch2+" "+ch3+" "+j+" "+i+"\n");
                    if (channel1 <= 63 && channel2 > 63) {
                        System.out.print(cypher[c]+"\n");
                        ch2 = ch2.substring(0,4) +  cypher[c];
                        ch1 = ch1.substring(0,7) + "0";
                        c = c + 1;
                    } else if (channel1 > 63 && channel2 <= 63) {
                        System.out.print(cypher[c]+"\n");
                        ch3 = ch3.substring(0,4) + cypher[c];
                        ch1 = ch1.substring(0,7) + "1";
                        c = c + 1;
                    } else if (channel1 <= 63 && channel2 <= 63) {
                        if (channel1 <= channel2) {
                            System.out.print(cypher[c]+"\n");
                            ch2 = ch2.substring(0,4) + cypher[c];
                            ch1 = ch1.substring(0,7) + "0";
                            c = c + 1;
                        } else {
                            System.out.print(cypher[c]+"\n");
                            ch3 = ch3.substring(0,4) + cypher[c];
                            ch1 = ch1.substring(0,7) + "1";
                            c = c + 1;
                        }
                    }
                    System.out.print(ch1+" "+ch2+" "+ch3+" "+j+" "+i+"\n");
                    Color color = new Color(Integer.parseInt(ch1,2), Integer.parseInt(ch2,2), Integer.parseInt(ch3,2));
                    int rgb = color.getRGB();
                    bufferedImage.setRGB(j, i, rgb);
                }
                /*if(flag == 1)
                    break;*/
            }
            System.out.print("Image Steganography successful(indi g).");
        }
        if(indicator.equals("red")) {
            int c = 0, flag = 0;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if(c == cypher.length){
                        flag = 1;
                        break;
                    }
                    int pixel = image.getRGB(j, i);
                    indicator_channel = (pixel >> 16) & 0xff;
                    channel1 = (pixel >> 8) & 0xff;
                    channel2 = (pixel) & 0xff;
                    String helper = "00000000";
                    ch1 = helper.substring(Integer.toBinaryString(indicator_channel).length()) + Integer.toBinaryString(indicator_channel);
                    ch2 = helper.substring(Integer.toBinaryString(channel1).length()) + Integer.toBinaryString(channel1);
                    ch3 = helper.substring(Integer.toBinaryString(channel2).length()) + Integer.toBinaryString(channel2);
                    if (channel1 <= 63 && channel2 > 63) {
                        ch2 = ch2.substring(0,4) +  cypher[c];
                        ch1 = ch1.substring(0,7) + "0";
                        c = c + 1;
                    } else if (channel1 > 63 && channel2 <= 63) {
                        ch3 = ch3.substring(0,4) + cypher[c];
                        ch1 = ch1.substring(0,7) + "1";
                        c = c + 1;
                    } else if (channel1 <= 63 && channel2 <= 63) {
                        if (channel1 <= channel2) {
                            ch2 = ch2.substring(0,4) + cypher[c];
                            ch1 = ch1.substring(0,7) + "0";
                            c = c + 1;
                        } else {
                            ch3 = ch3.substring(0,4) + cypher[c];
                            ch1 = ch1.substring(0,7) + "1";
                            c = c + 1;
                        }
                    } else {
                        continue;
                    }
                    Color color = new Color(Integer.parseInt(ch1,2), Integer.parseInt(ch2,2), Integer.parseInt(ch3,2));
                    int rgb = color.getRGB();
                    bufferedImage.setRGB(j, i, rgb);
                }
                if(flag == 1)
                    break;
            }
            System.out.print("Image Steganography successful(indi r).");
        }
        if(indicator.equals("blue")) {
            int c = 0, flag = 0;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    if(c == cypher.length){
                        flag = 1;
                        break;
                    }
                    int pixel = image.getRGB(j, i);
                    channel1 = (pixel >> 16) & 0xff;
                    channel2 = (pixel >> 8) & 0xff;
                    indicator_channel = (pixel) & 0xff;
                    String helper = "00000000";
                    ch1 = helper.substring(Integer.toBinaryString(indicator_channel).length()) + Integer.toBinaryString(indicator_channel);
                    ch2 = helper.substring(Integer.toBinaryString(channel1).length()) + Integer.toBinaryString(channel1);
                    ch3 = helper.substring(Integer.toBinaryString(channel2).length()) + Integer.toBinaryString(channel2);
                    if (channel1 <= 63 && channel2 > 63) {
                        ch2 = ch2.substring(0,4) +  cypher[c];
                        ch1 = ch1.substring(0,7) + "0";
                        c = c + 1;
                    } else if (channel1 > 63 && channel2 <= 63) {
                        ch3 = ch3.substring(0,4) + cypher[c];
                        ch1 = ch1.substring(0,7) + "1";
                        c = c + 1;
                    } else if (channel1 <= 63 && channel2 <= 63) {
                        if (channel1 <= channel2) {
                            ch2 = ch2.substring(0,4)+cypher[c];
                            ch1 = ch1.substring(0,7)+ "0";
                            c = c + 1;
                        } else {
                            ch3 = ch3.substring(0,4) + cypher[c];
                            ch1 = ch1.substring(0,7) + "1";
                            c = c + 1;
                        }
                    } else {
                        continue;
                    }
                    Color color = new Color(Integer.parseInt(ch1,2), Integer.parseInt(ch2,2), Integer.parseInt(ch3,2));
                    int rgb = color.getRGB();
                    bufferedImage.setRGB(j, i, rgb);
                }
                if(flag == 1)
                    break;
            }
            System.out.print("Image Steganography successful(indi b).");
        }
        return bufferedImage;
    }

    private static String getIndicator(BufferedImage image) {

        int red = 0, green = 0, blue = 0;
        int w = image.getWidth();
        int h = image.getHeight();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = image.getRGB(j, i);
                red = ((pixel >> 16) & 0xff) + red;
                green = ((pixel >> 8) & 0xff) + green;
                blue = ((pixel) & 0xff) + blue;
            }
        }
        if(red > green)
            if(red > blue)
                return "red";
            else
                return "blue";
        else
            if(green > blue)
                return "green";
            else
                return "blue";
    }

    private static int calculateCapacity(BufferedImage image, String indicator)
    {
        int red = 0, green = 0, blue = 0;
        int w = image.getWidth();
        int h = image.getHeight();
        int capacity = 0;

        if(indicator.equals("red")){
            for (int i = 0; i < h; i++)
                for (int j = 0; j < w; j++) {
                    int pixel = image.getRGB(j, i);
                    green = ((pixel >> 8) & 0xff);
                    blue = ((pixel) & 0xff);
                    if (!(green > 63 && blue > 63)) {
                        capacity = capacity + 4;
                    }
                }
        }
        else if(indicator.equals("green")){
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int pixel = image.getRGB(j, i);
                    red = ((pixel >> 16) & 0xff);
                    blue = ((pixel) & 0xff);
                    if(!(red > 63 && blue > 63))
                        capacity = capacity + 4;
                }
            }
        }
        else if(indicator.equals("blue")){
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    int pixel = image.getRGB(j, i);
                    red = ((pixel >> 16) & 0xff);
                    green = ((pixel >> 8) & 0xff);
                    if(!(red > 63 && green > 63))
                        capacity = capacity + 4;
                }
            }
        }
        return capacity;
    }

}
