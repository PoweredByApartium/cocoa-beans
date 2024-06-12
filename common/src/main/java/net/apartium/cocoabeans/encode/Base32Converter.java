package net.apartium.cocoabeans.encode;

/* package-private */ class Base32Converter implements BaseConverter {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private static final int[] BASE32_LOOKUP = {
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
            0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
            0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
            0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
    };

    /* package-private */ static final Base32Converter INSTANCE = new Base32Converter();

    private Base32Converter() {}

    @Override
    public String encode(byte[] data) {
        StringBuilder result = new StringBuilder((data.length + 7) / 8 * 5);

        int i = 0;
        int index = 0;
        int digit;

        int currByte;
        int nextByte;

        while(i < data.length) {
            currByte = (data[i] >= 0) ? data[i] : (data[i] + 256);

            if (index > 3) {
                nextByte = (i + 1) < data.length
                        ? (data[i + 1] >= 0) ? data[i + 1] : (data[i + 1] + 256)
                        : 0;

                digit = currByte & (255 >> index);
                index = (index + 5) % 8;
                digit <<= index;
                digit |= nextByte >> (8 - index);
                i++;
            } else {
                digit = (currByte >> (8 - (index + 5))) & 31;
                index = (index + 5) % 8;

                if (index == 0)
                    i++;

            }

            result.append(BASE32_CHARS.charAt(digit));
        }

        result.append("=".repeat((8 - (result.length() % 8)) % 8));

        return result.toString();
    }

    @Override
    public byte[] decode(String data) {
        int length = data.length();
        if (length == 0)
            return new byte[0];

        for (int i = length - 1; i >= 0; i--) {
            if (data.charAt(i) != '=')
                break;
            length -= 1;
        }


        int numBytes = (length * 5) / 8;
        byte[] result = new byte[numBytes];

        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;

        int lookup;

        for (char c : data.toCharArray()) {
            if (c == '=')
                break;

            lookup = BASE32_LOOKUP[c];
            if (lookup == 0xFF)
                throw new IllegalArgumentException("Illegal character: " + c);

            buffer <<= 5;
            buffer |= lookup & 31;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 255);
                bitsLeft -= 8;
            }
        }

        return result;
    }

}
