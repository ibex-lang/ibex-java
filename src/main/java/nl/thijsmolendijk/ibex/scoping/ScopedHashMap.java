package nl.thijsmolendijk.ibex.scoping;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Simple scoped HashMap.
 *
 * Created by molenzwiebel on 20-12-15.
 */
public class ScopedHashMap<K, V> extends AbstractMap<K, V> {
    private LinkedList<HashMap<K, V>> scope = new LinkedList<>();

    public ScopedHashMap() {
        scope.add(new HashMap<>());
    }

    /**
     * Adds a new level to the scope.
     */
    public void extend() {
        scope.add(new HashMap<>());
    }

    /**
     * Removes a level from the scope.
     */
    public void end() {
        scope.remove(scope.size() - 1);
    }

    /**
     * Puts the provided values in the provided scope. Topmost scope has index of 0
     */
    public V putInScope(int scopeIdx, K key, V value) {
        return scope.get(scopeIdx).put(key, value);
    }

    @Override
    public V put(K key, V value) {
        return scope.get(scope.size() - 1).put(key, value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> ret = new LinkedHashSet<>();
        for (int i = scope.size() - 1; i >= 0; i--) {
            ret.addAll(scope.get(i).entrySet());
        }
        return ret;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V ret = defaultValue;
        for (int i = scope.size() - 1; i >= 0; i--) {
            if (scope.get(i).containsKey(key)) {
                ret = scope.get(i).get(key);
                break;
            }
        }
        return ret;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        entrySet().forEach(x -> action.accept(x.getKey(), x.getValue()));
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!scope.get(scope.size() - 1).containsKey(key)) {
            return scope.get(scope.size() - 1).put(key, value);
        }
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        for (int i = scope.size() - 1; i >= 0; i--) {
            if (scope.get(i).containsKey(key) && scope.get(i).get(key).equals(value)) {
                return scope.get(i).remove(key, value);
            }
        }
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        for (int i = scope.size() - 1; i >= 0; i--) {
            if (scope.get(i).containsKey(key) && scope.get(i).get(key).equals(oldValue)) {
                return scope.get(i).replace(key, oldValue, newValue);
            }
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        for (int i = scope.size() - 1; i >= 0; i--) {
            if (scope.get(i).containsKey(key)) {
                return scope.get(i).replace(key, value);
            }
        }
        return null;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
