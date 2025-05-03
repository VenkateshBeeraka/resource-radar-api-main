package com.resourceradar.security;

import org.springframework.security.access.AccessDeniedException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenExpirationException extends AccessDeniedException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public TokenExpirationException(String message) {
		super(message);
	}
}
