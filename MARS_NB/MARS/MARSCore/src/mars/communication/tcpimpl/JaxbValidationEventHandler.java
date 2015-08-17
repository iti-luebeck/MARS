/*
 * Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
