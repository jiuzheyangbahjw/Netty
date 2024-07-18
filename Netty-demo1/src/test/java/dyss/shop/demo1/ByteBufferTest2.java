package dyss.shop.demo1;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    public void test_recurseFind() throws IOException {
        Path path = Paths.get("src/main/resources");
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger JarCount = new AtomicInteger();

      /*  Files.walkFileTree(path,new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")){
                    System.out.println("jar包:"+(JarCount));
                }
                return super.visitFile(file, attrs);
            }
        });*/

        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                System.out.println(dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
    }

    @Test
    public void test_recurseDelete() throws IOException {
        Path path = Paths.get("src/main/resources");
        /**
         * 危险的操作，这是不走回收站的。
         */
//        Files.walkFileTree(path,new SimpleFileVisitor<Path>(){
//            @Override
//            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                Files.delete(file);
//                return super.visitFile(file, attrs);
//            }
//
//            @Override
//            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                Files.delete(dir);
//                return super.postVisitDirectory(dir, exc);
//            }
//        });
    }

    @Test
    public void test_recurseCopy() throws IOException {
        String source = "src/main/resources";
        String target ="src/main/resources/bo";
        //流
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName=path.toString().replace(source,target);
                Path targetPath = Paths.get(targetName);
                //判断目录
                if (Files.isDirectory(path)){
                    Files.createDirectory(targetPath);
                } else if (Files.isRegularFile(path)) {
                    Files.copy(path, targetPath);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
