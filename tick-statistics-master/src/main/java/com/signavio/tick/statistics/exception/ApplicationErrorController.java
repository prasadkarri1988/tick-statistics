package com.signavio.tick.statistics.exception;

import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationErrorController implements ErrorController{

	  @PostMapping("/error")
	  @ResponseBody
	  public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
		  Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		  Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		  
		  Integer statusCode = null;
		  String errorMessage = null;
		  if (status != null) 
		        statusCode = Integer.valueOf(status.toString());
		  if(exception!=null)
			  errorMessage = exception.getMessage();
		    
	      ErrorResponse errorDetails = new ErrorResponse(new Date(), statusCode, errorMessage);
		    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	  }

	  @Override
	  public String getErrorPath() {
	      return "/error";
	  }
}
