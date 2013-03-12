package org.openmrs.module.feedpublishermodule.context;

import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

@Component
public class ContextWrapper {
    public void openAuthenticatedSession(){
        Context.openSession();
        authenticate();
    }

    public <T>  T getService(Class<? extends T> clazz){
        return Context.getService(clazz);
    }

    //TODO: read from admin global properties.
    private void authenticate(){
        String username = "admin";
        String password =  "!4321Abcd";
        Context.authenticate(username, password);
    }

    public void closeSession() {
        Context.closeSession();
    }
}
