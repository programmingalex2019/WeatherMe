package com.example.weatherme;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.weatherme.Screens.Landing;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<Landing> activityScenarioRule =
            new ActivityScenarioRule<Landing>(Landing.class);

    @Test
    public void checkMatchingID(){

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Espresso.onView(ViewMatchers.withId(R.id.signIn))
                .check(ViewAssertions.matches(ViewMatchers.withText("SignIn")));

    }
}
