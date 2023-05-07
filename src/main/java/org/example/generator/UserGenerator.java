package org.example.generator;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;

public class UserGenerator {
    public static User getRandom() {
        String email = RandomStringUtils.randomAlphabetic(6) + "@" + RandomStringUtils.randomAlphabetic(4) + ".ru";
        String password = getAlphabeticRandom();
        String name = getAlphabeticRandom();
        return new User(email, password, name);
    }

    private static String getAlphabeticRandom() {
        return RandomStringUtils.randomAlphabetic(10);
    }
}
