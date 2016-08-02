package tellh.com.autogo_core;

import org.junit.Test;

import autogo.utils.ClassUtils;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void checkNullTest() throws Exception{
        int age = 0;
        double score=0;
        String name="1";
        assertEquals(false, ClassUtils.checkNull(name));
    }
}