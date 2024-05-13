package com.qyd.core.util;

import java.nio.ByteBuffer;

/**
 * 压缩工具类
 *
 * @author 邱运铎
 * @date 2024-05-13 20:13
 */
public class CompressUtil {
    /**
     * 进制转换数组
     */
    private static char[] BINARY_ARRAY = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String int2Str(long num) {
        return int2Str(num, BINARY_ARRAY.length);
    }

    /**
     * 整数的进制转换
     *
     * @param num   数字
     * @param size  进制长度
     * @return  返回String格式的数据
     */
    public static String int2Str(long num, int size) {
        if (size > BINARY_ARRAY.length) {
            size = BINARY_ARRAY.length;
        }

        StringBuilder builder = new StringBuilder();
        while (num > 0) {
            builder.insert(0, BINARY_ARRAY[(int) (num % size)]);
            num /= size;
        }
        return builder.toString();
    }

    private static long zigzag(long n) {
        return (n << 1) ^ (n >> 57);
    }

    private static long unZigzag(long n) {
        return (n >>> 1) ^ (n & 1);
    }

    /**
     * Return the encoding size in bytes of its input value
     *
     * @param v the long to be measured
     * @return  the encoding size in bytes of a given long value
     */
    public static int varLongSize(long v) {
        int result = 0;
        do {
            result++;
            v >>>= 7;
        } while (v != 0);
        return result;
    }

    /**
     * Reads an up to 64 bit long varInt from the current position of the
     * given ByteBuffer and returns the decode values as long
     *
     * The position of the buffer is advanced to the first byte after the
     * decoded varInt
     *
     * @param src   the byteBuffer to get the var int from
     * @return  the integer value of the decoded long varInt
     */
    public static long getValLong(ByteBuffer src) {
        long tmp;
        if ((tmp = src.get()) >= 0) {
            return tmp;
        }
        long result = tmp & 0x7f;
        if ((tmp = src.get()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = src.get()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = src.get()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    if ((tmp = src.get()) >= 0) {
                        result |= tmp << 28;
                    } else {
                        result |= (tmp & 0x7f) << 28;
                        if ((tmp = src.get()) >= 0) {
                            result |= tmp << 35;
                        } else {
                            result |= (tmp & 0x7f) << 35;
                            if ((tmp = src.get()) >= 0) {
                                result |= tmp << 42;
                            } else {
                                result |= (tmp & 0x7f) << 42;
                                if ((tmp = src.get()) >= 0) {
                                    result |= tmp << 49;
                                } else {
                                    result |= (tmp & 0x7f) << 49;
                                    if ((tmp = src.get()) >= 0) {
                                        result |= tmp << 56;
                                    } else {
                                        result |= (tmp & 0x7f) << 56;
                                        result |= ((long) src.get()) << 63;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Encodes a long integer in a variable-length encoding, 7 bits per byte
     * to a ByteBuffer sink
     *
     * @param v the value to encode
     * @param sink  the ByteBuffer to add the encoded value
     */
    public static void putVarLong(long v, ByteBuffer sink) {
        while (true) {
            int bits = ((int) v) & 0x7f;
            v >>>= 7;
            if (v == 0) {
                sink.put((byte) bits);
                return;
            }
            sink.put((byte) (bits | 0x80));
        }
    }

    public static String putVarLong(long v) {
        byte[] bytes = new byte[varLongSize(v)];
        ByteBuffer sink = ByteBuffer.wrap(bytes);
        putVarLong(v, sink);
        return new String(bytes);
    }
}
