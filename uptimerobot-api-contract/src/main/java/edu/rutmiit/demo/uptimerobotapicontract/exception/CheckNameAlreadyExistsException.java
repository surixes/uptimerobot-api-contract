package edu.rutmiit.demo.uptimerobotapicontract.exception;

public class CheckNameAlreadyExistsException extends RuntimeException {
    public CheckNameAlreadyExistsException(String checkName) {
        super("Check with name=" + checkName + " already exists");
    }
}
