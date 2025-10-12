/*
 * Copyright (c) 2025, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.utils;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class DNSUtils {

    private final InitialDirContext context;

    public DNSUtils(InitialDirContext context) {
        this.context = context;
    }

    public DNSUtils()

    private InitialDirContext create() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial","com.sun.jndi.dns.DnsContextFactory");

        try {
            return new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public String reverseLookup(InetAddress address) throws NamingException {
        if (address instanceof Inet4Address inet4Address) {
            byte[] addressBytes = inet4Address.getAddress();
            String fqdn = String.format("%d.%d.%d.%d.in-addr.arpa", addressBytes[3] & 0xff, addressBytes[2] & 0xff, addressBytes[1] & 0xff, addressBytes[0] & 0xff);
            return lookup(fqdn, "PTR").get(0);
        } else {
            // IPv6 is not yet supported
            throw new IllegalArgumentException("Unsupported address type: " + address.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> lookup(String hostName, String recordType) throws NamingException {
        Attribute attr = context.getAttributes("dns:" + hostName, new String[] {recordType}).get(recordType);
        return (List<String>) Collections.list(attr.getAll());
    }

}
