package dyss.shop.intro.V4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @author DYss东阳书生
 * @date 2024/7/22 14:44
 * @Description 描述
 */

public class TestSlice {
    @Test
    public void test_slice(){
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','f','g','h'});
        log(buf);

        ByteBuf slice = buf.slice(0, 5);
        ByteBuf slice1 = buf.slice(5, 2);
        log(slice);
        log(slice1);
        //同一个
        System.out.println("``````````````````````````````````````````");
        slice.setByte(0,'t');
        log(slice);
        log(buf);
    }

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
