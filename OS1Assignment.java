import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;


public class OS1Assignment {



    public static final int PAGE_SIZE = 128;
    public static final long [] PAGE_TABLE = {2, 4, 1, 7, 3, 5, 6};
    public static final byte MASK = (1 << 7) - 1;

    public static void main(String[] args) {

        String fileName = args[0];
        long[] virtualAddresses = readFile(fileName);

        long [] physicalAddress = new long [virtualAddresses.length];

        for (int i = 0; i < virtualAddresses.length; i++){
            long offset = virtualAddresses[i] & MASK;
            long frameNumber = PAGE_TABLE[(int) virtualAddresses[i] >> 7] ;
            physicalAddress[i] = frameNumber * PAGE_SIZE + offset;
        }

        writeToFile(physicalAddress);

    }

    private static void writeToFile(long [] addresses) {

        try(FileOutputStream outputStream = new FileOutputStream("output-OS1")) {
          
            for (long address: addresses) {
                outputStream.write( longToBytes(address));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static long [] readFile(String fileName) {
        List<Long> virtualAddresses = new ArrayList<>();
        try(InputStream inputStream = new FileInputStream(fileName)) {
            while (inputStream.available() > 0) {
                virtualAddresses.add(toUnsignedLong(inputStream.readNBytes(8)));
            }
        } catch (IOException e) {
            System.out.printf("File not found: %s%n", fileName);
        }

        return virtualAddresses.stream()
                .mapToLong(Long::longValue).toArray();
    }

    public static long toUnsignedLong(byte[] bytes) {
        return IntStream.range(0, bytes.length)
                .mapToLong(i -> (long) (bytes[i] & 0xff) << (i * 8))
                .reduce(0L, (a, b) -> a | b);
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

}