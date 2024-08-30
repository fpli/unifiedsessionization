package com.ebay.epic.soj.business.testUtil;

import com.ebay.epic.soj.business.utils.BotruleUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BotruleUtilsTest {

    @Test
    void setBotFlagShouldSetCorrectBit() {
        long botSignature = 0b0000;
        long bitPos = 2;
        long expected = 0b0010;

        assertEquals(expected, BotruleUtils.setBotFlag(botSignature, bitPos));
    }

    @Test
    void setBotFlagShouldNotChangeOtherBits() {
        long botSignature = 0b1010;
        long bitPos = 2;
        long expected = 0b1010;

        assertEquals(expected, BotruleUtils.setBotFlag(botSignature, bitPos));
    }

    @Test
    void validateBotShouldReturnTrueWhenBitIsSet() {
        long botSignature = 0b0010;
        long bitPos = 2;

        assertTrue(BotruleUtils.validateBot(botSignature, bitPos));
    }

    @Test
    void validateBotShouldReturnFalseWhenBitIsNotSet() {
        long botSignature = 0b1000;
        long bitPos = 2;

        assertFalse(BotruleUtils.validateBot(botSignature, bitPos));
    }

    @Test
    void validateBotShouldHandleMaxLongValue() {
        long botSignature = Long.MAX_VALUE;
        long bitPos = 63;
        assertTrue(BotruleUtils.validateBot(botSignature, bitPos));
    }
}
