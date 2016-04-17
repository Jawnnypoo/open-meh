package com.jawnnypoo.openmeh.util;

/**
 * Indicates that messages can be sent
 */
public interface MessageSender {

    boolean sendMessage(String path, byte[] data);
}
