package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JSONException extends IOException {

    private final List<JSONException.Entry> entries = Lists.newArrayList();
    private final String message;

    public JSONException(String message) {
        this.entries.add(new JSONException.Entry());
        this.message = message;
    }

    public JSONException(String message, Throwable cause) {
        super(cause);
        this.entries.add(new JSONException.Entry());
        this.message = message;
    }

    public void prependJsonKey(String key) {
        this.entries.get(0).addJsonKey(key);
    }

    public void setFilenameAndFlush(String filename) {
        this.entries.get(0).filename = filename;
        this.entries.add(0, new JSONException.Entry());
    }

    public String getMessage() {
        return "Invalid " + this.entries.get(this.entries.size() - 1).toString() + ": " + this.message;
    }

    public static JSONException forException(Exception exception) {
        if (exception instanceof JSONException) {
            return (JSONException)exception;
        } else {
            String s = exception.getMessage();

            if (exception instanceof FileNotFoundException) {
                s = "File not found";
            }

            return new JSONException(s, exception);
        }
    }

    public static class Entry {
        private String filename;
        private final List<String> jsonKeys;

        private Entry() {
            this.filename = null;
            this.jsonKeys = Lists.newArrayList();
        }

        private void addJsonKey(String key) {
            this.jsonKeys.add(0, key);
        }

        public String getJsonKeys() {
            return StringUtils.join(this.jsonKeys, "->");
        }

        public String toString() {
            return this.filename != null ? (!this.jsonKeys.isEmpty() ? this.filename + " " + this.getJsonKeys() : this.filename) : (!this.jsonKeys.isEmpty() ? "(Unknown file) " + this.getJsonKeys() : "(Unknown file)");
        }
    }
}
