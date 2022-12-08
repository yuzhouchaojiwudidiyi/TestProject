/*
 * Copyright 2019 F1ReKing.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.f1reking.serialportlib.util;

import android.util.Log;

import java.util.Arrays;

/**
 * @author F1ReKing
 * @date 2019/11/1 14:08
 * @Description
 */
public class ByteUtils {

    public static int isOdd(int num) {
        return num & 0x1;
    }

    public static int HexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 十六进制字符串 转字节数组
     */
    public static byte[] hexToByteArr(String hex) {
        int hexLen = hex.length();
        byte[] result;
        if (isOdd(hexLen) == 1) {
            hexLen++;
            result = new byte[hexLen / 2];
            hex = "0" + hex;
        } else {
            result = new byte[hexLen / 2];
        }
        int j = 0;
        for (int i = 0; i < hexLen; i += 2) {
            result[j] = HexToByte(hex.substring(i, i + 2));
            j++;
        }
        return result;
    }

/*
    public static void main(String[] args) {
//        38 36 31 30 38 39 30 35 30 38 33 36 32 30 35 16 05 00 10 00 00 11 0e 00
        String str = strTo16("861089050836205");
        System.out.println(str);

//        String s = str + "16" + "05" + "00100000" + "110e00";
        String s = str + "22" + "05" + "01020304" + "070809";
        byte[] bytes = hexToByteArr(s);
        System.out.println(Arrays.toString(bytes));


        String days = "1";
        String bin = "00000000000000000000000000000000";
        char[] chars = bin.toCharArray();
        String[] split = days.split(",");
        for (int i = 0; i < split.length; i++) {
            String day = split[i];
            chars[Integer.parseInt(day)-1] = '1';
        }
        String binDays = String.valueOf(chars);
        System.out.println(binDays);
    }
*/

    /**
     *  * 字符串转化成为16进制字符串
     *  * @param s
     *  * @return
     *  
     */
    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

}
