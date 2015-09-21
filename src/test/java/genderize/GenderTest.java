package genderize;

import abhi.genderize.Gender;
import abhi.genderize.NameGender;
import org.junit.Assert;
import org.junit.Test;


public class GenderTest {

    @Test
    public void testNullGender() {
        NameGender name = new NameGender();
        name.setGender("null");
        Assert.assertEquals(Gender.NULL, name.getGenderType());
    }

    @Test
    public void testFemaleGender() {
        NameGender name = new NameGender();
        name.setGender("female");
        Assert.assertEquals(Gender.FEMALE, name.getGenderType());
    }

    @Test
    public void testMaleGender() {
        NameGender name = new NameGender();
        name.setGender("male");
        Assert.assertEquals(Gender.MALE, name.getGenderType());
    }
}
