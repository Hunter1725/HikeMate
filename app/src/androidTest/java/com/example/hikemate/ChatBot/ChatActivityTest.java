package com.example.hikemate.ChatBot;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.hikemate.ChatBot.ChatActivity;
import com.example.hikemate.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChatActivityTest {

    @Rule
    public ActivityScenarioRule<ChatActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ChatActivity.class);

    @Test
    public void testSendMessage() {
        // Type a message in the EditText and click the send button
        Espresso.onView(ViewMatchers.withId(R.id.edtMessage))
                .perform(ViewActions.typeText("Hello, this is a test message"))
                .perform(ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.btnSend)).perform(ViewActions.click());

        // Wait for a response to appear in the RecyclerView (you may need to adjust the delay)
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                .check(ViewAssertions.matches(ViewMatchers.hasMinimumChildCount(2))); // Assuming at least 2 messages, one sent and one received

        // Add more assertions here based on the expected behavior of your app
        // For example, you can check if the response message appears in the RecyclerView
        Espresso.onView(ViewMatchers.withText(R.string.typing))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    // Add more tests as needed for other functionalities in your ChatActivity
}