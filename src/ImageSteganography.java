import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ImageSteganography {

    public static void main(String[] args)
    {
        String path = "";

        System.out.print("Enter the path of cover image : ");
        Scanner scan = new Scanner(System.in);
        path = scan.nextLine();

        try {
            File img = new File(path);
            String name = img.getName();
            BufferedImage image = ImageIO.read(img);
            RGBChannelSteganography.imageSteganographyEncode(image,name);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
