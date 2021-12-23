package org.acme.exception;

import lombok.*;
import org.acme.topology.TransactionTopology;
import org.jboss.logging.Logger;

@AllArgsConstructor
@Getter
@Generated
@ToString
public class ValidationBinException extends Exception{

    private final org.jboss.logging.Logger logger = Logger.getLogger(ValidationBinException.class);

    @Override
    public String getMessage() {
        logger.info("Topic Validating bin Empty="+super.getMessage());
        return super.getMessage();
    }
}
