package com.neoobjectpascal;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DAPServerTest {

    @Test
    void readsOneDapMessageWhenTheTransportDeliversItInFragments() throws Exception {
        String body = "{\"seq\":1,\"command\":\"initialize\"}";
        String wire = "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" + body;
        DAPServer server = new DAPServer(new FragmentedInputStream(wire.getBytes(StandardCharsets.UTF_8)),
                new ByteArrayOutputStream(), "program.npas");
        Method readMessage = DAPServer.class.getDeclaredMethod("readMessage");
        readMessage.setAccessible(true);

        assertEquals(body, readMessage.invoke(server));
    }

    private static final class FragmentedInputStream extends ByteArrayInputStream {
        FragmentedInputStream(byte[] data) {
            super(data);
        }

        @Override
        public synchronized int read(byte[] buffer, int offset, int length) {
            return super.read(buffer, offset, Math.min(1, length));
        }
    }
}
