package com.example.fahad.publicservices;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static org.junit.Assert.*;


public class MainPage {

    @Rule
    public ActivityTestRule<MainActivity> firstTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mfirst = null;

   Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(CustomerMainActivity.class.getName(),null,false);
    @Before
    public void setUp() throws Exception {
        mfirst = firstTestRule.getActivity();
    }
    @Test
    public void  testgo(){
        assertNotNull(mfirst.findViewById(R.id.btnUser));

        onView(ViewMatchers.withId(R.id.btnUser)).perform(click());

         Activity userside =getInstrumentation().waitForMonitorWithTimeout(monitor , 5000);
         assertNotNull(userside);
         userside.finish();
    }
    @After
    public void tearDown() throws Exception {
    }

}