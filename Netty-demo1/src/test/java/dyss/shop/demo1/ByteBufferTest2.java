package dyss.shop.demo1;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author DYss东阳书生
 * @date 2024/7/17 20:48
 * @Description 描述
 */

public class ByteBufferTest2 {
    @Test
    public void test_Path(){
        Path source = Paths.get("../../../java/resources/data.txt");
        System.out.println("source = " + source);
        System.out.println(source.normalize());
    }

    @Test
    public void test_Path2() throws IOException {
        Path source = Paths.get("src/main/resources/data.txt");
        System.out.println(Files.exists(source));
//        Files.createDirectory(source);
//        Files.createDirectories(source);
    }

    @Test
    public void test_Copy() throws IOException {
        Path from = Paths.get("src/main/resources/data.txt");
        Path to = Paths.get("src/main/resources/to.txt");
        Files.copy(from,to, StandardCopyOption.REPLACE_EXISTING);
//        Files.delete(to)
//        Files.move(from,to,StandardCopyOption.ATOMIC_MOVE)
    }
}
