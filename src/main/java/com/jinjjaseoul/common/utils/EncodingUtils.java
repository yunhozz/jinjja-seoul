package com.jinjjaseoul.common.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class EncodingUtils {

    public static String encodeURIComponent(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20")
                .replaceAll("\\%21", "!")
                .replaceAll("\\%27", "'")
                .replaceAll("\\%28", "(")
                .replaceAll("\\%29", ")")
                .replaceAll("\\%7E", "~");
    }

    public static String decodeURIComponent(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private EncodingUtils() {
        super();
    }
}