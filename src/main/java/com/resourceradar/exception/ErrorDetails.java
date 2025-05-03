package com.resourceradar.exception;

import java.util.Date;

import org.springframework.http.HttpStatus.Series;

public class ErrorDetails {

	private Date timestamp;
	 private String message;
	 private String details;
	 private Series status;

	 public ErrorDetails(Date timestamp, String message, String details, Series status) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
		this.status = status;
	}

	public Date getTimestamp() {
	  return timestamp;
	 }

	 public String getMessage() {
	  return message;
	 }

	 public String getDetails() {
	  return details;
	 }

	public Series getStatus() {
		return status;
	}
}
