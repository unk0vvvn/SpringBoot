package com.portfolio.sb.security.auth.jwt;

public interface JwtProperties {
	final String SECRET = "1234";
	final String TOKEN_PREFIX = "Bearer";
	final String HEADER_STRING = "Authorization";
	final String ENCODE = "UTF-8";
	final int EXPIRATION_TIME = 1000*60*10;
	
}
