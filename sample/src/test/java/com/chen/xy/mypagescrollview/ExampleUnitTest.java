package com.chen.xy.mypagescrollview;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

import com.chen.xy.mypagescrollview.TestLogic;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest extends AndroidTestCase{
    @Test
    public void testAddition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testAddingLogicTast() {
        TestLogic tl = new TestLogic();
        assertEquals("2 plus 2 must 4", 3, tl.add(2, 2));
    }
}