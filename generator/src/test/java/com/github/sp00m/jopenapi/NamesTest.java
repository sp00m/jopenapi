package com.github.sp00m.jopenapi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamesTest {

    @Test
    void toFieldName() {
        assertEquals("fooBar", Names.toFieldName("foo bar"));
        assertEquals("minusOnePointTwoEPlusThree", Names.toFieldName("-1.2E+3"));
        assertEquals("fourTwoNaN", Names.toFieldName("-42NaN"));
        assertEquals("fourZeroFourNotFound", Names.toFieldName("404_NOT_FOUND"));
        assertEquals("a", Names.toFieldName("a"));
        assertEquals("lessThanSign", Names.toFieldName("<"));
        assertEquals("defaultValue", Names.toFieldName("default"));
        assertEquals("someUrl", Names.toFieldName("Some-URL"));
        assertEquals("unknown", Names.toFieldName("---"));
    }

    @Test
    void toClassName() {
        assertEquals("FooBar", Names.toClassName("foo bar"));
    }

    @Test
    void toEnumValue() {
        assertEquals("FOO_BAR", Names.toEnumValue("foo bar"));
    }

    @Test
    void toPackageName() {
        assertEquals("foo_bar", Names.toPackageName("foo bar"));
    }

}
