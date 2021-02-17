package com.murengezi.minecraft.event;

import com.google.common.collect.Maps;
import java.util.Map;

public class ClickEvent {

    private final ClickEvent.Action action;
    private final String value;

    public ClickEvent(ClickEvent.Action action, String theValue) {
        this.action = action;
        this.value = theValue;
    }

    public ClickEvent.Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            ClickEvent clickevent = (ClickEvent)obj;

            if (this.action != clickevent.action) {
                return false;
            } else {
                if (this.value != null) {
                    return this.value.equals(clickevent.value);
                } else {
                    return clickevent.value == null;
                }
            }
        } else {
            return false;
        }
    }

    public String toString() {
        return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        return 31 * this.action.hashCode() + (this.value != null ? this.value.hashCode() : 0);
    }

    public enum Action {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true);

        private static final Map<String, ClickEvent.Action> nameMapping = Maps.newHashMap();
        private final boolean allowedInChat;
        private final String canonicalName;

        Action(String canonicalName, boolean allowedInChat) {
            this.canonicalName = canonicalName;
            this.allowedInChat = allowedInChat;
        }

        public boolean shouldAllowInChat() {
            return this.allowedInChat;
        }

        public String getCanonicalName() {
            return this.canonicalName;
        }

        public static ClickEvent.Action getValueByCanonicalName(String canonicalNameIn) {
            return nameMapping.get(canonicalNameIn);
        }

        static {
            for (ClickEvent.Action action : values()) {
                nameMapping.put(action.getCanonicalName(), action);
            }
        }
    }
}
