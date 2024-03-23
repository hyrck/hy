import org.junit.jupiter.api.Test;

import java.util.Random;

public class UploadFileTest {
    @Test
    public void test1(){
        String fileName = "erer.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
    @Test
    public void test(){
        int i=(int)(Math.random()*900)+100;
        int j= new Random().nextInt(900)+100;
        System.out.println(j);
    }
}
