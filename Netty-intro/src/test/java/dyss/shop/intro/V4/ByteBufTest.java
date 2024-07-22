package dyss.shop.intro.V4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @author DYss东阳书生
 * @date 2024/7/22 11:20
 * @Description 描述
 */
@Slf4j
public class ByteBufTest {
    @Test
    public void create_BUF(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("buffer.getClass() = " + buffer.getClass());
        log(buffer);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append("a");
        }
        buffer.writeBytes(sb.toString().getBytes());
        log(buffer);
    }

    /**
     * 方便buffer的调试
     * @param buffer buffer
     */
    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
