package com.murengezi.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {

    private final List<SoundList.SoundEntry> soundList = Lists.newArrayList();

    private boolean replaceExisting;
    private SoundCategory category;

    public List<SoundList.SoundEntry> getSoundList() {
        return this.soundList;
    }

    public boolean canReplaceExisting() {
        return this.replaceExisting;
    }

    public void setReplaceExisting(boolean replaceExisting) {
        this.replaceExisting = replaceExisting;
    }

    public SoundCategory getSoundCategory() {
        return this.category;
    }

    public void setSoundCategory(SoundCategory category) {
        this.category = category;
    }

    public static class SoundEntry {

        private String name;
        private float volume = 1.0F, pitch = 1.0F;
        private int weight = 1;
        private SoundList.SoundEntry.Type type = SoundList.SoundEntry.Type.FILE;
        private boolean streaming = false;

        public String getSoundEntryName() {
            return this.name;
        }

        public void setSoundEntryName(String name) {
            this.name = name;
        }

        public float getSoundEntryVolume() {
            return this.volume;
        }

        public void setSoundEntryVolume(float volume) {
            this.volume = volume;
        }

        public float getSoundEntryPitch() {
            return this.pitch;
        }

        public void setSoundEntryPitch(float pitch) {
            this.pitch = pitch;
        }

        public int getSoundEntryWeight() {
            return this.weight;
        }

        public void setSoundEntryWeight(int weight) {
            this.weight = weight;
        }

        public SoundList.SoundEntry.Type getSoundEntryType() {
            return this.type;
        }

        public void setSoundEntryType(SoundList.SoundEntry.Type type) {
            this.type = type;
        }

        public boolean isStreaming() {
            return this.streaming;
        }

        public void setStreaming(boolean isStreaming) {
            this.streaming = isStreaming;
        }

        public enum Type {
            FILE("file"), SOUND_EVENT("event");

            private final String type;

            Type(String type) {
                this.type = type;
            }

            public static SoundList.SoundEntry.Type getType(String type) {
                for (SoundList.SoundEntry.Type soundType : values()) {
                    if (soundType.type.equals(type)) {
                        return soundType;
                    }
                }

                return null;
            }
        }
    }
}
