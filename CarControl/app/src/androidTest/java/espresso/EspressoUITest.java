package espresso;

//Import statements for tests
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyRightOf;

import static org.junit.Assert.assertEquals;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.code.carcontrol.R;

//import statement to get access to target class
import com.code.carcontrol.MainActivity;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoUITest{
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void TestUIelements(){
        /**
         *The purpose of this test is to ensure the display, positioning and alignment of elements
         * on screen by making use of espresso instrumented testing
         */
        onView(withText("ROTATE LEFT")).check(matches(isDisplayed()));
        onView(withText("ROTATE RIGHT")).check(matches(isDisplayed()));
        onView(withText("FIND LEFT PATH")).check(matches(isDisplayed()));
        onView(withText("FIND RIGHT PATH")).check(matches(isDisplayed()));
        onView(withText("CRUISE CONTROL")).check(matches(isDisplayed()));

        onView(withText("ROTATE LEFT")).check(isCompletelyLeftOf(withId(R.id.ROTATE_RIGHT)));
        onView(withText("FIND LEFT PATH")).check(isCompletelyLeftOf(withId(R.id.FindRightPath)));
        onView(withText("ROTATE RIGHT")).check(isCompletelyRightOf(withId(R.id.ROTATE_LEFT)));
        onView(withText("FIND RIGHT PATH")).check(isCompletelyRightOf(withId(R.id.FindLeftPath)));

    }


}
