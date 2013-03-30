package org.ne.utrino.plankton;

@SuppressWarnings("serial")
/**
 * Signals that an error occurred while decoding plankton.
 */
public class DecodingError extends RuntimeException {

  public DecodingError(String message) {
    super(message);
  }

}
