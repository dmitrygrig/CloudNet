/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cloudnet.messaging;

import cloudnet.util.Ensure;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The application components of a Distributed Application are hosted on
 * multiple cloud resources and have to exchange information with each other.
 * Often, the integration with other cloud applications and non-cloud
 * applications is also required. Communication partners exchange information
 * asynchronously using messages. The message-oriented middleware handles the
 * complexity of addressing, availability of communication partners and message
 * format transformation.
 *
 * (http://www.cloudcomputingpatterns.org/Message-oriented_Middleware)
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class MessageBus {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBus.class);

    private final List<Message> messages = new ArrayList<>();

    public MessageBus() {
    }

    public void push(Message message) {
        Ensure.NotNull(message, "message");
        messages.add(message);
        LOGGER.trace("%s was pushed into the message bus.", message);
    }

    public <T extends Message> List<T> getMessagesByType(Class<T> clazz) {
        Ensure.NotNull(clazz, "clazz");
        return messages
                .stream()
                .filter(m -> clazz.isInstance(m))
                .map(m -> (T) m)
                .collect(Collectors.toList());
    }

    public <T extends Message> void removeRange(List<T> messagesToRemove) {
        Ensure.NotNull(messagesToRemove, "messagesToRemove");
        messagesToRemove.stream().forEach(m -> remove(m));
    }

    public void remove(Message message) {
        Ensure.NotNull(message, "message");
        messages.remove(message);
        LOGGER.trace("%s was removed from the message bus.", message);
    }

    public void clear() {
        messages.clear();
        LOGGER.trace("Message bus was cleared.");
    }
}
