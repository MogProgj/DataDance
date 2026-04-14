package structlab.core.graph;

import java.util.Collections;
import java.util.List;

/**
 * Playback controller for stepping through algorithm frames.
 * Maintains the current frame index and supports forward/backward navigation.
 */
public class PlaybackController {

    private List<AlgorithmFrame> frames = List.of();
    private int currentIndex = -1;

    /** Loads a new set of frames and resets to the first frame. */
    public void load(List<AlgorithmFrame> frames) {
        this.frames = frames != null ? List.copyOf(frames) : List.of();
        this.currentIndex = this.frames.isEmpty() ? -1 : 0;
    }

    /** Returns true if frames are loaded. */
    public boolean isLoaded() {
        return !frames.isEmpty();
    }

    /** Returns the current frame, or null if none loaded. */
    public AlgorithmFrame current() {
        if (currentIndex < 0 || currentIndex >= frames.size()) return null;
        return frames.get(currentIndex);
    }

    /** Steps to the next frame. Returns false if already at end. */
    public boolean next() {
        if (currentIndex < frames.size() - 1) {
            currentIndex++;
            return true;
        }
        return false;
    }

    /** Steps to the previous frame. Returns false if already at start. */
    public boolean previous() {
        if (currentIndex > 0) {
            currentIndex--;
            return true;
        }
        return false;
    }

    /** Returns true if there's a next frame. */
    public boolean hasNext() {
        return currentIndex < frames.size() - 1;
    }

    /** Returns true if there's a previous frame. */
    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    /** Jumps to the first frame. */
    public void reset() {
        if (!frames.isEmpty()) {
            currentIndex = 0;
        }
    }

    /** Jumps to the last frame. */
    public void jumpToEnd() {
        if (!frames.isEmpty()) {
            currentIndex = frames.size() - 1;
        }
    }

    /**
     * Jumps directly to the given frame index (clamped to valid range).
     * Returns true if the index was within range, false if clamped.
     */
    public boolean jumpTo(int index) {
        if (frames.isEmpty()) return false;
        int clamped = Math.max(0, Math.min(index, frames.size() - 1));
        currentIndex = clamped;
        return clamped == index;
    }

    /** Returns the current frame index (0-based), or -1 if empty. */
    public int currentIndex() {
        return currentIndex;
    }

    /** Returns the total number of frames. */
    public int frameCount() {
        return frames.size();
    }

    /** Returns all frames (unmodifiable). */
    public List<AlgorithmFrame> frames() {
        return frames;
    }

    /** Clears all frames and resets. */
    public void clear() {
        frames = List.of();
        currentIndex = -1;
    }
}
