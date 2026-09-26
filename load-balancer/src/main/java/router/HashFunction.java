package router;

import java.nio.charset.StandardCharsets;

public class HashFunction {

    public long hash(String key) {
        if (key == null) return 0L;
        byte[] data = key.getBytes(StandardCharsets.UTF_8);

        final int c1 = 0xcc9e2d51;
        final int c2 = 0x1b873593;
        int h1 = 0x9747b28c;
        int length = data.length;
        int nblocks = length / 4;

        for (int i = 0; i < nblocks; i++) {
            int i4 = i * 4;
            int k1 = (data[i4] & 0xff)
                    | ((data[i4 + 1] & 0xff) << 8)
                    | ((data[i4 + 2] & 0xff) << 16)
                    | ((data[i4 + 3] & 0xff) << 24);

            k1 *= c1;
            k1 = Integer.rotateLeft(k1, 15);
            k1 *= c2;

            h1 ^= k1;
            h1 = Integer.rotateLeft(h1, 13);
            h1 = h1 * 5 + 0xe6546b64;
        }

        int k1 = 0;
        int tail = nblocks * 4;
        switch (length & 3) {
            case 3:
                k1 ^= (data[tail + 2] & 0xff) << 16;
            case 2:
                k1 ^= (data[tail + 1] & 0xff) << 8;
            case 1:
                k1 ^= (data[tail] & 0xff);
                k1 *= c1;
                k1 = Integer.rotateLeft(k1, 15);
                k1 *= c2;
                h1 ^= k1;
        }

        h1 ^= length;
        h1 ^= (h1 >>> 16);
        h1 *= 0x85ebca6b;
        h1 ^= (h1 >>> 13);
        h1 *= 0xc2b2ae35;
        h1 ^= (h1 >>> 16);

        return h1 & 0xFFFFFFFFL;
    }
}
