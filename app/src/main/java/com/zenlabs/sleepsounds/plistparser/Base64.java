package com.zenlabs.sleepsounds.plistparser;

import java.util.Arrays;

/**
 * Created by fedoro on 5/12/16.
 */
public class Base64 {
    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();
    private static final int[] IA = new int[256];
    static {
        Arrays.fill(IA, -1);
        for (int i = 0, iS = CA.length; i < iS; i++)
            IA[CA[i]] = i;
        IA['='] = 0;
    }

    public final static char[] encodeToChar(byte[] sArr, boolean lineSep) {

        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0)
            return new char[0];

        int eLen = (sLen / 3) * 3;
        int cCnt = ((sLen - 1) / 3 + 1) << 2;
        int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0);
        char[] dArr = new char[dLen];

        for (int s = 0, d = 0, cc = 0; s < eLen;) {

            int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8
                    | (sArr[s++] & 0xff);

            dArr[d++] = CA[(i >>> 18) & 0x3f];
            dArr[d++] = CA[(i >>> 12) & 0x3f];
            dArr[d++] = CA[(i >>> 6) & 0x3f];
            dArr[d++] = CA[i & 0x3f];

            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        }

        int left = sLen - eLen; // 0 - 2.
        if (left > 0) {

            int i = ((sArr[eLen] & 0xff) << 10)
                    | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

            dArr[dLen - 4] = CA[i >> 12];
            dArr[dLen - 3] = CA[(i >>> 6) & 0x3f];
            dArr[dLen - 2] = left == 2 ? CA[i & 0x3f] : '=';
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }

    public final static byte[] decode(char[] sArr) {

        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0)
            return new byte[0];

        int sepCnt = 0;
        for (int i = 0; i < sLen; i++)

            if (IA[sArr[i]] < 0)
                sepCnt++;

        if ((sLen - sepCnt) % 4 != 0)
            return null;

        int pad = 0;
        for (int i = sLen; i > 1 && IA[sArr[--i]] <= 0;)
            if (sArr[i] == '=')
                pad++;

        int len = ((sLen - sepCnt) * 6 >> 3) - pad;

        byte[] dArr = new byte[len];

        for (int s = 0, d = 0; d < len;) {

            int i = 0;
            for (int j = 0; j < 4; j++) {

                int c = IA[sArr[s++]];
                if (c >= 0)
                    i |= c << (18 - j * 6);
                else
                    j--;
            }

            dArr[d++] = (byte) (i >> 16);
            if (d < len) {
                dArr[d++] = (byte) (i >> 8);
                if (d < len)
                    dArr[d++] = (byte) i;
            }
        }
        return dArr;
    }

    public final static byte[] decodeFast(char[] sArr) {

        int sLen = sArr.length;
        if (sLen == 0)
            return new byte[0];

        int sIx = 0, eIx = sLen - 1;

        while (sIx < eIx && IA[sArr[sIx]] < 0)
            sIx++;

        while (eIx > 0 && IA[sArr[eIx]] < 0)
            eIx--;

        int pad = sArr[eIx] == '=' ? (sArr[eIx - 1] == '=' ? 2 : 1) : 0;
        int cCnt = eIx - sIx + 1;
        int sepCnt = sLen > 76 ? (sArr[76] == '\r' ? cCnt / 78 : 0) << 1 : 0;

        int len = ((cCnt - sepCnt) * 6 >> 3) - pad;
        byte[] dArr = new byte[len];

        int d = 0;
        for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {

            int i = IA[sArr[sIx++]] << 18 | IA[sArr[sIx++]] << 12
                    | IA[sArr[sIx++]] << 6 | IA[sArr[sIx++]];

            dArr[d++] = (byte) (i >> 16);
            dArr[d++] = (byte) (i >> 8);
            dArr[d++] = (byte) i;

            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }

        if (d < len) {

            int i = 0;
            for (int j = 0; sIx <= eIx - pad; j++)
                i |= IA[sArr[sIx++]] << (18 - j * 6);

            for (int r = 16; d < len; r -= 8)
                dArr[d++] = (byte) (i >> r);
        }

        return dArr;
    }

    public final static byte[] encodeToByte(byte[] sArr, boolean lineSep) {

        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0)
            return new byte[0];

        int eLen = (sLen / 3) * 3;
        int cCnt = ((sLen - 1) / 3 + 1) << 2;
        int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0);
        byte[] dArr = new byte[dLen];

        for (int s = 0, d = 0, cc = 0; s < eLen;) {

            int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8
                    | (sArr[s++] & 0xff);

            dArr[d++] = (byte) CA[(i >>> 18) & 0x3f];
            dArr[d++] = (byte) CA[(i >>> 12) & 0x3f];
            dArr[d++] = (byte) CA[(i >>> 6) & 0x3f];
            dArr[d++] = (byte) CA[i & 0x3f];

            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        }

        int left = sLen - eLen; // 0 - 2.
        if (left > 0) {
            // Prepare the int
            int i = ((sArr[eLen] & 0xff) << 10)
                    | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

            // Set last four chars
            dArr[dLen - 4] = (byte) CA[i >> 12];
            dArr[dLen - 3] = (byte) CA[(i >>> 6) & 0x3f];
            dArr[dLen - 2] = left == 2 ? (byte) CA[i & 0x3f] : (byte) '=';
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }

    public final static byte[] decode(byte[] sArr) {

        int sLen = sArr.length;

        int sepCnt = 0;
        for (int i = 0; i < sLen; i++)

            if (IA[sArr[i] & 0xff] < 0)
                sepCnt++;

        if ((sLen - sepCnt) % 4 != 0)
            return null;

        int pad = 0;
        for (int i = sLen; i > 1 && IA[sArr[--i] & 0xff] <= 0;)
            if (sArr[i] == '=')
                pad++;

        int len = ((sLen - sepCnt) * 6 >> 3) - pad;

        byte[] dArr = new byte[len];

        for (int s = 0, d = 0; d < len;) {

            int i = 0;
            for (int j = 0; j < 4; j++) {
                int c = IA[sArr[s++] & 0xff];
                if (c >= 0)
                    i |= c << (18 - j * 6);
                else
                    j--;
            }

            dArr[d++] = (byte) (i >> 16);
            if (d < len) {
                dArr[d++] = (byte) (i >> 8);
                if (d < len)
                    dArr[d++] = (byte) i;
            }
        }

        return dArr;
    }

    public final static byte[] decodeFast(byte[] sArr) {

        int sLen = sArr.length;
        if (sLen == 0)
            return new byte[0];

        int sIx = 0, eIx = sLen - 1;

        while (sIx < eIx && IA[sArr[sIx] & 0xff] < 0)
            sIx++;

        while (eIx > 0 && IA[sArr[eIx] & 0xff] < 0)
            eIx--;

        int pad = sArr[eIx] == '=' ? (sArr[eIx - 1] == '=' ? 2 : 1) : 0;
        int cCnt = eIx - sIx + 1;
        int sepCnt = sLen > 76 ? (sArr[76] == '\r' ? cCnt / 78 : 0) << 1 : 0;

        int len = ((cCnt - sepCnt) * 6 >> 3) - pad;
        byte[] dArr = new byte[len];

        int d = 0;
        for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {

            int i = IA[sArr[sIx++]] << 18 | IA[sArr[sIx++]] << 12
                    | IA[sArr[sIx++]] << 6 | IA[sArr[sIx++]];

            dArr[d++] = (byte) (i >> 16);
            dArr[d++] = (byte) (i >> 8);
            dArr[d++] = (byte) i;

            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }

        if (d < len) {

            int i = 0;
            for (int j = 0; sIx <= eIx - pad; j++)
                i |= IA[sArr[sIx++]] << (18 - j * 6);

            for (int r = 16; d < len; r -= 8)
                dArr[d++] = (byte) (i >> r);
        }

        return dArr;
    }

    public final static String encodeToString(byte[] sArr, boolean lineSep) {
        return new String(encodeToChar(sArr, lineSep));
    }

    public final static byte[] decode(String str) {

        int sLen = str != null ? str.length() : 0;
        if (sLen == 0)
            return new byte[0];

        int sepCnt = 0;
        for (int i = 0; i < sLen; i++)

            if (IA[str.charAt(i)] < 0)
                sepCnt++;

        if ((sLen - sepCnt) % 4 != 0)
            return null;

        int pad = 0;
        for (int i = sLen; i > 1 && IA[str.charAt(--i)] <= 0;)
            if (str.charAt(i) == '=')
                pad++;

        int len = ((sLen - sepCnt) * 6 >> 3) - pad;

        byte[] dArr = new byte[len];

        for (int s = 0, d = 0; d < len;) {

            int i = 0;
            for (int j = 0; j < 4; j++) {
                int c = IA[str.charAt(s++)];
                if (c >= 0)
                    i |= c << (18 - j * 6);
                else
                    j--;
            }

            dArr[d++] = (byte) (i >> 16);
            if (d < len) {
                dArr[d++] = (byte) (i >> 8);
                if (d < len)
                    dArr[d++] = (byte) i;
            }
        }
        return dArr;
    }

    public final static byte[] decodeFast(String s) {

        int sLen = s.length();
        if (sLen == 0)
            return new byte[0];

        int sIx = 0, eIx = sLen - 1;

        while (sIx < eIx && IA[s.charAt(sIx) & 0xff] < 0)
            sIx++;

        while (eIx > 0 && IA[s.charAt(eIx) & 0xff] < 0)
            eIx--;

        int pad = s.charAt(eIx) == '=' ? (s.charAt(eIx - 1) == '=' ? 2 : 1) : 0;
        int cCnt = eIx - sIx + 1;
        int sepCnt = sLen > 76 ? (s.charAt(76) == '\r' ? cCnt / 78 : 0) << 1
                : 0;

        int len = ((cCnt - sepCnt) * 6 >> 3) - pad;
        byte[] dArr = new byte[len];

        int d = 0;
        for (int cc = 0, eLen = (len / 3) * 3; d < eLen;) {

            int i = IA[s.charAt(sIx++)] << 18 | IA[s.charAt(sIx++)] << 12
                    | IA[s.charAt(sIx++)] << 6 | IA[s.charAt(sIx++)];

            dArr[d++] = (byte) (i >> 16);
            dArr[d++] = (byte) (i >> 8);
            dArr[d++] = (byte) i;

            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }

        if (d < len) {

            int i = 0;
            for (int j = 0; sIx <= eIx - pad; j++)
                i |= IA[s.charAt(sIx++)] << (18 - j * 6);

            for (int r = 16; d < len; r -= 8)
                dArr[d++] = (byte) (i >> r);
        }

        return dArr;
    }
}
