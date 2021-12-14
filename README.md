# NoJNDI

This is a simple proof of concept agent that disables JNDI lookups globally across the JVM.

This is useful for mitigating the Log4Shell attack, but is also applicable to any situation where you're 

## How It Works

The `javax.naming.spi.NamingManager` class is responsible for setting an initial context, using the `setInitialContextFactoryBuilder` method.   Once installed, the builder cannot be replaced.

The agent immediately sets this in the `premain` method to return an `InitialContextFactory` that always throws a `NoPermissionException`.

When doing a lookup, `InitialContext` does a lookup that will find the default initial context, which calls `NamingManager.getInitialContext`:

```java

public class InitialContext implements Context {
    // ...
    public Object lookup(String name) throws NamingException {
        return getURLOrDefaultInitCtx(name).lookup(name);
    }

    protected Context getURLOrDefaultInitCtx(Name name)
        throws NamingException {
        if (NamingManager.hasInitialContextFactoryBuilder()) {
            return getDefaultInitCtx();
        }
        if (name.size() > 0) {
            String first = name.get(0);
            String scheme = getURLScheme(first);
            if (scheme != null) {
                Context ctx = NamingManager.getURLContext(scheme, myProps);
                if (ctx != null) {
                    return ctx;
                }
            }
        }
        return getDefaultInitCtx();
    }

    protected Context getDefaultInitCtx() throws NamingException{
        if (!gotDefault) {
            defaultInitCtx = NamingManager.getInitialContext(myProps);
            gotDefault = true;
        }
        if (defaultInitCtx == null)
            throw new NoInitialContextException();

        return defaultInitCtx;
    }
}
```

And then the `NamingManager.getInitialContext` calls `getInitialContextFactoryBuilder` as you'd expect:

```java
public class NamingManager {
    public static Context getInitialContext(Hashtable<?,?> env)
            throws NamingException {
        ClassLoader loader;
        InitialContextFactory factory = null;

        InitialContextFactoryBuilder builder = getInitialContextFactoryBuilder();
        if (builder == null) {
            // ...object factory stuff not relevant
        } else {
            factory = builder.createInitialContextFactory(env);
        }

        return factory.getInitialContext(env);
    }
}
```

## Why It Works

JNDI is complicated in its internals, but practically speaking, JNDI is used for LDAP access.  If you're not using LDAP, you don't need JNDI.  Nuke it.

## But I Do Need JNDI!

There is a subset of Java applications that do need JNDI, mostly for LDAP authentication through JAAS.

In this situation, I think probably the best thing you can do is specify your own LDAP context factory to replace `com.sun.jndi.ldap.LdapCtxFactory` and enforce some whitelisting, and disable [every other kind of lookup](https://docs.oracle.com/javase/tutorial/jndi/overview/index.html).


