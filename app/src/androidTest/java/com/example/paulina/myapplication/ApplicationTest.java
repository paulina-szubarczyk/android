package com.example.paulina.myapplication;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ApplicationTest {

    @Test
    public void shouldHaveHappySmiles() throws Exception {
        assertThat("Hello World!", equalTo("Hello World!"));
    }

}