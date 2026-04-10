/*
 * Copyright (c) 2026, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Contains annotation used to generate various means of docs and help menus for commands.
 * By definition, the included annotations have no impact on the processing of the commands at runtime.
 */
@ApiStatus.AvailableSince("0.0.49")
public class CommandDocs {

    /**
     * Indicates the section name of a command. Often overrides label
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Section {

        /**
         * {LABEL} would be replaced with the command name / alias
         * @return section name
         */
        String value();

    }

    /**
     * Hides the member from generated documentation, can be useful for fallback variants.
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Hidden {

    }

    /**
     * Indicates which version introduced the command
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Since {

        /**
         * @return product version where the command was added
         */
        String value();

    }

    private CommandDocs() {}

}
