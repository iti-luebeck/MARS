/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.communication.tcpimpl;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class JaxbValidationEventHandler implements ValidationEventHandler {

    @Override
    public boolean handleEvent(ValidationEvent event) {

        Level loggerLevel = Level.INFO;

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "\nEVENT");

        switch (event.getSeverity()) {
            case 1:
                loggerLevel = Level.WARNING;
                break;
            case 2:
                loggerLevel = Level.SEVERE;
                break;
        }

        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "MESSAGE:  " + event.getMessage());
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "LINKED EXCEPTION:  " + event.getLinkedException());
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "LOCATOR");
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "    LINE NUMBER:  " + event.getLocator().getLineNumber());
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "    OFFSET:  " + event.getLocator().getOffset());
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "    OBJECT:  " + event.getLocator().getObject());
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "    NODE:  " + event.getLocator().getNode());
        Logger.getLogger(this.getClass().getName()).log(loggerLevel, "    URL:  " + event.getLocator().getURL());

        return true;
    }
}
