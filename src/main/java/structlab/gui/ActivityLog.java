package structlab.gui;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ActivityLog {

    public record Entry(String action, String detail, String category, LocalDateTime timestamp) {}

    private final List<Entry> entries = new ArrayList<>();

    public void log(String action, String detail, String category) {
        entries.add(new Entry(action, detail, category, LocalDateTime.now()));
    }

    public List<Entry> getRecent(int limit) {
        int size = entries.size();
        int from = Math.max(0, size - limit);
        List<Entry> recent = new ArrayList<>(entries.subList(from, size));
        Collections.reverse(recent);
        return Collections.unmodifiableList(recent);
    }

    public List<Entry> getAll() {
        List<Entry> all = new ArrayList<>(entries);
        Collections.reverse(all);
        return Collections.unmodifiableList(all);
    }

    /** Returns all entries matching a category filter (case-insensitive). */
    public List<Entry> getByCategory(String category) {
        List<Entry> filtered = entries.stream()
                .filter(e -> e.category().equalsIgnoreCase(category))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(filtered);
        return Collections.unmodifiableList(filtered);
    }

    /** Returns the distinct categories used so far. */
    public Set<String> getCategories() {
        return entries.stream()
                .map(Entry::category)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public void clear() { entries.clear(); }

    public int size() { return entries.size(); }
}
