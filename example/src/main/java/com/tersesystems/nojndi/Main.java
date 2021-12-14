package com.tersesystems.nojndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Runs a program that uses JNDI.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("About to try JNDI lookup!");
        try {
            Context ctx = new InitialContext();
            String datasource = "ldap://example.com";
            ctx.lookup(datasource);

            // Happens on normal circumstances...
            System.err.println("ATTACK SUCCEEDED: JNDI lookup call!");
        } catch (NamingException e) {
            // Happens on agent redefinition of java.lang.System
            System.out.println("ATTACK FAILED: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("PROGRAM FAILED: " + e.getMessage());
        }
    }

}
