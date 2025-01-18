package com.intellipick.intern8th.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Const {

    public static final String BEARER_PREFIX = "Bearer ";

    public static final long TOKEN_ACCESS_TIME = 60 * 60 * 1000L;
    public static final long TOKEN_REFERSH_TIME = 7 * 24 * 60 * 60 * 1000L;

    public static final String USER_USERNAME = "username";
    public static final String USER_AUTHORITY_NAME = "authorityName";
}
