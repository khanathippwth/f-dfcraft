/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

final class BadMap<K, V> {
    private final int size;
    private final Entry[] entries;
    private static final Entry[] emptyEntries = new Entry[0];
    private static final int[] primes = new int[]{2, 5, 11, 17, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997, 1009, 2053, 3079, 4057, 7103, 10949, 16069, 32609, 65867, 104729};

    BadMap() {
        this(0, emptyEntries);
    }

    private BadMap(int n, Entry[] entryArray) {
        this.size = n;
        this.entries = entryArray;
    }

    BadMap<K, V> copyingPut(K k, V v) {
        int n = this.size + 1;
        Entry[] entryArray = n > this.entries.length ? new Entry[BadMap.nextPrime(n * 2 - 1)] : new Entry[this.entries.length];
        if (entryArray.length == this.entries.length) {
            System.arraycopy(this.entries, 0, entryArray, 0, this.entries.length);
        } else {
            BadMap.rehash(this.entries, entryArray);
        }
        int n2 = Math.abs(k.hashCode());
        BadMap.store(entryArray, n2, k, v);
        return new BadMap<K, V>(n, entryArray);
    }

    private static <K, V> void store(Entry[] entryArray, int n, K k, V v) {
        int n2 = n % entryArray.length;
        Entry entry = entryArray[n2];
        entryArray[n2] = new Entry(n, k, v, entry);
    }

    private static void store(Entry[] entryArray, Entry entry) {
        int n = entry.hash % entryArray.length;
        Entry entry2 = entryArray[n];
        entryArray[n] = entry2 == null && entry.next == null ? entry : new Entry(entry.hash, entry.key, entry.value, entry2);
    }

    private static void rehash(Entry[] entryArray, Entry[] entryArray2) {
        for (Entry entry : entryArray) {
            while (entry != null) {
                BadMap.store(entryArray2, entry);
                entry = entry.next;
            }
        }
    }

    V get(K k) {
        if (this.entries.length == 0) {
            return null;
        }
        int n = Math.abs(k.hashCode());
        int n2 = n % this.entries.length;
        Entry entry = this.entries[n2];
        if (entry == null) {
            return null;
        }
        return (V)entry.find(k);
    }

    private static int nextPrime(int n) {
        for (int n2 : primes) {
            if (n2 <= n) continue;
            return n2;
        }
        return primes[primes.length - 1];
    }

    static final class Entry {
        final int hash;
        final Object key;
        final Object value;
        final Entry next;

        Entry(int n, Object object, Object object2, Entry entry) {
            this.hash = n;
            this.key = object;
            this.value = object2;
            this.next = entry;
        }

        Object find(Object object) {
            if (this.key.equals(object)) {
                return this.value;
            }
            if (this.next != null) {
                return this.next.find(object);
            }
            return null;
        }
    }
}

