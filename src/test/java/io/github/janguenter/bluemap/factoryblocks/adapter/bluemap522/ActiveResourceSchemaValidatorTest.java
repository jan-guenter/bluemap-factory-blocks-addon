/* SPDX-License-Identifier: MIT */
package io.github.janguenter.bluemap.factoryblocks.adapter.bluemap522;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

class ActiveResourceSchemaValidatorTest {

    @Test
    void giantDimensionsRequireAnExactIntegralJsonNumber() {
        assertEquals(3, ActiveResourceSchemaValidator.integerValue(JsonParser.parseString("3")));
        assertEquals(3, ActiveResourceSchemaValidator.integerValue(JsonParser.parseString("3.0")));
        assertEquals(Integer.MIN_VALUE,
                ActiveResourceSchemaValidator.integerValue(JsonParser.parseString("\"3\"")));
        assertEquals(Integer.MIN_VALUE,
                ActiveResourceSchemaValidator.integerValue(JsonParser.parseString("3.1")));
        assertEquals(Integer.MIN_VALUE,
                ActiveResourceSchemaValidator.integerValue(JsonParser.parseString("2147483648")));
    }
}
