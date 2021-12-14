package com.tersesystems.nojndi.agent;

import javax.naming.*;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.NamingManager;
import java.lang.instrument.Instrumentation;
import java.util.Hashtable;

public class NoJndiAgent {
    private NoJndiAgent() {
    }

    public static void premain(String arg, Instrumentation inst) throws Exception {
        NamingManager.setInitialContextFactoryBuilder(env -> new NoPermissionsInitialContextFactory());
    }

    public static class NoPermissionsInitialContextFactory implements InitialContextFactory {
        @Override
        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
            throw new NoPermissionException("JNDI is disabled!");
        }
    }
}