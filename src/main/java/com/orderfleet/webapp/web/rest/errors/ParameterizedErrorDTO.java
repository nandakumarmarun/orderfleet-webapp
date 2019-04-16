package com.orderfleet.webapp.web.rest.errors;

import java.io.Serializable;

/**
 * DTO for sending a parameterized error message.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public class ParameterizedErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String message;
    private final String[] params;

    public ParameterizedErrorDTO(String message, String... params) {
        this.message = message;
        this.params = params;
    }

    public String getMessage() {
        return message;
    }

    public String[] getParams() {
        return params;
    }

}
