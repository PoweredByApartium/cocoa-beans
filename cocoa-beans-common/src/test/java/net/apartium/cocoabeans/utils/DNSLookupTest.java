/*
 * Copyright (c) 2025, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.utils;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DNSUtilsTest {

    @Test
    void reverseLookup() throws Exception {
        assertEquals("one.one.one.one.", DNSUtils.reverseLookup(InetAddress.getByName("1.1.1.1")));

    }

    @Test
    void lookup() throws Exception {
        List<String> result = DNSUtils.lookup("one.one.one.one.", "A");
        assertEquals(2, result.size());
        assertTrue(result.contains("1.1.1.1"));
        assertTrue(result.contains("1.0.0.1"));

    }


}