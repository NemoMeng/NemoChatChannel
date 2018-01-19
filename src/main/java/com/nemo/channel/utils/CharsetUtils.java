/* 
 * All rights Reserved, Designed By 微迈科技
 * 2018/1/19 10:35
 */
package com.nemo.channel.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * 字符集相关操作工具
 * Created by Nemo on 2018/1/19.
 */
public class CharsetUtils {

    private static final String UTF_8 = "UTF-8";
    private static CharsetEncoder encoder = Charset.forName(UTF_8).newEncoder();
    private static CharsetDecoder decoder = Charset.forName(UTF_8).newDecoder();

    /**
     * 重新编码
     * @param in
     * @return
     * @throws CharacterCodingException
     */
    public static ByteBuffer encode(CharBuffer in) throws CharacterCodingException {
        return encoder.encode(in);
    }

    /**
     * 重新编码
     * @param in
     * @return
     */
    public static ByteBuffer encode(String in) throws CharacterCodingException {
        return encode(CharBuffer.wrap(in));
    }

    /**
     * 解码
     * @param in
     * @return
     * @throws CharacterCodingException
     */
    public static CharBuffer decode(ByteBuffer in) throws CharacterCodingException{
        return decoder.decode(in);
    }

}
