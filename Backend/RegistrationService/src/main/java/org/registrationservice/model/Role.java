package org.registrationservice.model;

/**
 * All the roles available in the application.
 */
public enum Role {
    //Role given to the users that will interact with the application only for booking sports fields.
    ROLE_USER,
    //Role given to the users that will manage sports fields.
    ROLE_ADMINISTRATOR,
    //Role given to the users that will have Admin privileges.
    ROLE_ADMIN
}
