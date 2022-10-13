package com.doudou.bugly;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class PushParams {

    public static Map<String, String> sign() {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("messageTitle", "深蹲页" + (new Random().nextInt()));
        map.put("messageDescription", "跳转到深蹲页");
        map.put("messagePayload", "{\"id\":\"1234\",\"title\":\"深蹲页\",\"desc\":\"跳转到深蹲页\",\"img\":\"http://system.songwo888.com/catMotion/764bc54ab5489f9f721497999027476d.jpg\",\"open_type\":\"11\",\"url\":\"http://motion.mop.com/appfe__test/waterhistory.html\",\"is_login\":\"0\",\"type\":\"1\"}");
        map.put("passThrough", "0");// : 是否透传消息 0/不是 1/是 针对apns 透传为静默推送
        map.put("clickActionType", "0");
        map.put("customerAppKey", "ARBK2RHRA76FK6");
        map.put("receivers", "gn42X8ka6PEmw1oK");
        map.put("batchType", "IDS");
        map.put("ts", System.currentTimeMillis() + "");
        map.put("sandBox", "1");

        String serverSecret = "7c5af42291ac4a1ab83d0932a70c";

        String content = map.entrySet().stream().map(
                e -> e.getKey() + e.getValue()
        ).reduce((s1, s2) ->
                s1 + s2
        ).get();
        content += serverSecret;

        byte[] digest = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digest = md5.digest(content.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //16是表示转换为16进制数
        String md5Str = new BigInteger(1, digest).toString(16).toUpperCase(Locale.ROOT);

        map.put("sign", md5Str);

        Log.INSTANCE.i("messageTitle = " + map.get("messageTitle"));
        return map;
    }

}
