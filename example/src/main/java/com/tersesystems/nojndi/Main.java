package com.tersesystems.nojndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NoPermissionException;

/**
 * Runs a program that uses JNDI.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("About to try JNDI lookup!");
        try {
            Context ctx = new InitialContext();
            String datasource = "ldap://localhost:389";
            ctx.lookup(datasource);

            // Happens on normal circumstances...
            System.err.println("ATTACK SUCCEEDED: JNDI lookup call!");
        } catch (NoPermissionException e) {
            System.out.println("ATTACK FAILED: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("PROGRAM FAILED: ");
            e.printStackTrace();
        }
    }

}
