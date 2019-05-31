package me.rainny.reaper.listener;

public class Tuple<K, V>
{
    private final K key;
    private final V value;
    
    public Tuple(final K k, final V value) {
        this.key = k;
        this.value = value;
    }
    
    public K getKey() {
        return this.key;
    }
    
    public V getValue() {
        return this.value;
    }
}
