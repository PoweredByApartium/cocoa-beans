/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

/**
 * To be implemented by command classes to provide general functionality
 */
public interface CommandNode extends GenericNode {

    /**
     * This method should be overriden by implementations in order to provide fallback implementation for the cmd system
     * @param sender sender
     * @param label label
     * @param args args
     * @return you know this shit
     */
    @ApiStatus.OverrideOnly
    default boolean fallbackHandle(Sender sender, String label, String[] args) {
        return false;
    }
    
    @ApiStatus.AvailableSince("0.0.23")
    default boolean handleException(Sender sender, String label, String[] args, Throwable throwable) {
        return false;
    }

}
