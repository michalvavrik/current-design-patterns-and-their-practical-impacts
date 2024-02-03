package edu.michalvavrik.graphql.shim;

import io.smallrye.graphql.api.ErrorCode;

@ErrorCode("42")
public class PhilosophyException extends RuntimeException {
}
