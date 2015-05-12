package com.example.paulina.myapplication;

import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    @Test
    public void shouldHaveHappySmiles() throws Exception {
        assertThat("Hello World!", equalTo("Hello World!"));
    }

}