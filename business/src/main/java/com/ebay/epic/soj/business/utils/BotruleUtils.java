package com.ebay.epic.soj.business.utils;

public class BotruleUtils {
    private static final long MAX = Long.MAX_VALUE;

    public static Long setBotFlag(long botSignature, long bitPos) {
        return botSignature | ((1L << (bitPos - 1)) & MAX);
    }

    public static Boolean validateBot(long botSignature, long bitPos) {
        return ((botSignature >> (bitPos - 1)) & 1) == 1;
    }

}


