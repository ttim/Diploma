package ru.abishev.utils;

// todo: add tests!
public class StringPool {
    private final int modCount;

    private final byte[][] data;
    private final int[] next;
    private final int[] first;

    private int currentId = 0;

    public StringPool(int maxSize) {
        data = new byte[maxSize][];
        next = new int[maxSize];

        modCount = maxSize * 2;
        first = new int[modCount];
    }

    // pool of strings
    // you can add string to pool and get id for string
    // equality based on signature() method

    /**
     * @param s
     * @return -1 in case of non-existing string in pool
     */
    public int getId(String s) {
        String signature = getSignature(s);
        int hash = (signature.hashCode() % modCount + modCount) % modCount;
        int f = first[hash];
        while (f != 0) {
            // check f
            if (signature.equals(getSignature(new String(data[f])))) {
                return f;
            }
            f = next[f];
        }
        return -1;
    }

    public String getById(int id) {
        return new String(data[id]);
    }

    public int add(String s) {
        int existingId = getId(s);
        if (existingId != -1) {
            return existingId;
        }

        currentId++;
        data[currentId] = s.getBytes();
        int hash = (getSignature(s).hashCode() % modCount + modCount) % modCount;
        next[currentId] = first[hash];
        first[hash] = currentId;

        return currentId;
    }

    protected String getSignature(String str) {
        return str;
    }

    public static void main(String[] args) {
        StringPool pool = new StringPool(1000);
        pool.add("1");
        pool.add("2");
        pool.add("1");
        pool.add("3");
        pool.add("5");
        pool.add("5");
        pool.add("4");

        System.out.println(pool.getId("1") + " " + pool.getId("2") + " " + pool.getId("3") + " " + pool.getId("4") + " " + pool.getId("5"));
    }
}
