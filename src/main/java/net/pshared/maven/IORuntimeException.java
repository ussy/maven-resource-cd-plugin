package net.pshared.maven;

import java.io.IOException;

public class IORuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1632530451579143124L;

    public IORuntimeException(IOException e) {
        super(e);
    }
}
