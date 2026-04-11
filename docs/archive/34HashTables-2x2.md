Algorithms
ROBERT SEDGEWICK  |  KEVIN WAYNE Symbol table implementations:  summary
|                              | ggguuuaaarrraaannnttteeeeee |                   | aaavvveeerrraaagggeee   cccaaassseee |        |                |                    |
| ---------------------------- | --------------------------- | ----------------- | ------------------------------------ | ------ | -------------- | ------------------ |
| iimmpplleemmeennttaattiioonn |                             |                   |                                      |        | oorrddeerreedd | kkeeyy             |
|                              |                             |                   |                                      |        | ooppss??       | iinntteerrffaaccee |
|                              | search insert               | delete search hit | insert                               | delete |                |                    |
3.4  HASH TABLES sequential search
|     | N N | N ½ N | N   | ½ N |     | equals() |
| --- | --- | ----- | --- | --- | --- | -------- |
(unordered list)
‣ hash functions binary search
|     | lg N N | N lg N | ½ N | ½ N | ✔   | compareTo() |
| --- | ------ | ------ | --- | --- | --- | ----------- |
(ordered array)
‣ separate chaining
| BST | N N | N 1.39 lg N | 1.39 lg N | √   N | ✔   | compareTo() |
| --- | --- | ----------- | --------- | ----- | --- | ----------- |
‣ linear probing
Algorithms
FOURTH EDITION ‣ context
| red-black BST | 2 lg N 2 lg N | 2 lg N 1.0 lg N | 1.0 lg N | 1.0 lg N | ✔   | compareTo() |
| ------------- | ------------- | --------------- | -------- | -------- | --- | ----------- |
ROBERT SEDGEWICK  |  KEVIN WAYNE
http://algs4.cs.princeton.edu
Q.  Can we do better?
A.  Yes, but with different access to the data.
2
Hashing:  basic plan
Save items in a key-indexed table (index is a function of the key).
0
Hash function.  Method for computing array index from key.
1
hash("it") = 3 2
3.4  HASH TABLES
3 "it"
??
4
| Issues. |     | ‣ hash functions |     |     |     |     |
| ------- | --- | ---------------- | --- | --- | --- | --- |
hash("times") = 3 5
・
Computing the hash function.
|     |     | ‣ separate chaining |     |     |     |     |
| --- | --- | ------------------- | --- | --- | --- | --- |
・
Equality test:  Method for checking whether two keys are equal.
|     |     | ‣ linear probing |     |     |     |     |
| --- | --- | ---------------- | --- | --- | --- | --- |
Algorithms
・ Collision resolution:  Algorithm and data structure
|     |     | ‣ context |     |     |     |     |
| --- | --- | --------- | --- | --- | --- | --- |
to handle two keys that hash to the same array index.
ROBERT SEDGEWICK  |  KEVIN WAYNE
Classic space-time tradeoff.
http://algs4.cs.princeton.edu
・
No space limitation:  trivial hash function with key as index.
・
No time limitation:  trivial collision resolution with sequential search.
・ Space and time limitations:  hashing (the real world).
3

| Computing the hash function | Java’s hash code conventions |     |     |
| --------------------------- | ---------------------------- | --- | --- |
Idealistic goal.  Scramble the keys uniformly to produce a table index. All Java classes inherit a method hashCode(), which returns a 32-bit int.
・
Efficiently computable.
key
・ Each table index equally likely for each key.
Requirement.  If x.equals(y), then (x.hashCode() == y.hashCode()).
thoroughly researched problem,
still problematic in practical applications
Highly desirable.   If !x.equals(y), then (x.hashCode() != y.hashCode()).
Ex 1.  Phone numbers.
x y
・
Bad:  first three digits.
・ table
Better:  last three digits.
index
Ex 2.  Social Security numbers.
x.hashCode() y.hashCode()
| ・   | 573 = California, 574 = Alaska |     |     |
| --- | ------------------------------ | --- | --- |
Bad:  first three digits.
(assigned in chronological order within geographic region)
・
Better:  last three digits.
Default implementation.  Memory address of x.
Legal (but poor) implementation.  Always return 17.
Customized implementations.  Integer, Double, String, File, URL, Date, …
Practical challenge.   Need different approach for each key type. User-defined types.  Users are on their own.
5 6
Implementing hash code:  integers, booleans, and doubles Implementing hash code:  strings
Java library implementation
Java library implementations
char Unicode
public final class String
{
| public final class Integer    | public final class Double      |                            | … …    |
| ----------------------------- | ------------------------------ | -------------------------- | ------ |
| {                             | {                              |    private final char[] s; |        |
|                               |                                |    ...                     | 'a' 97 |
|    private final int value;   |    private final double value; |                            |        |
|    ...                        |    ...                         |                            | 'b' 98 |
|                               |                                |    public int hashCode()   |        |
|                               |                                |    {                       | 'c' 99 |
|    public int hashCode()      |    public int hashCode()       |                            |        |
      int hash = 0;
|    {  return value;  } |    {   |     |     |
| ---------------------- | ------ | --- | --- |
… ...
}       long bits = doubleToLongBits(value);       for (int i = 0; i < length(); i++)
|     |       return (int) (bits ^ (bits >>> 32)); |          hash = s[i] + (31 * hash); |     |
| --- | ------------------------------------------ | ----------------------------------- | --- |
      return hash;
   }
   }
} ith character of s
| public final class Boolean |     | }   |     |
| -------------------------- | --- | --- | --- |
{
   private final boolean value;   convert to IEEE 64-bit representation;
   ...
xor most significant 32-bits ・
    Horner's method to hash string of length L:  L multiplies/adds.
with least significant 32-bits
   public int hashCode() ・ Equivalent to  h = s[0]  · 31L–1   + … +  s[L – 3]  · 312   +  s[L – 2] ·  311  +  s[L – 1] · 310.
|    { | Warning: -0.0 and +0.0 have different hash codes |     |     |
| ---- | ------------------------------------------------ | --- | --- |
      if (value) return 1231;
      else       return 1237;
Ex.  String s = "call";
   }
int code = s.hashCode(); 3045982 = 99·313 + 97·312 + 108·311 + 108·310
}
               = 108 + 31· (108 + 31 · (97 + 31 · (99)))
(Horner's method)
7 8

Implementing hash code: strings Implementing hash code: user-defined types
Performance optimization.
・ public final class Transaction implements Comparable<Transaction>
Cache the hash value in an instance variable.
{
・
Return cached value. private final String who;
private final Date when;
private final double amount;
public final class String
{
public Transaction(String who, Date when, double amount)
private int hash = 0; cache of hash code
{ /* as before */ }
private final char[] s;
...
...
public int hashCode()
public boolean equals(Object y)
{
{ /* as before */ }
int h = hash; return cached value
if (h != 0) return h;
public int hashCode()
nonzero constant
for (int i = 0; i < length(); i++)
{
h = s[i] + (31 * h); int hash = 17; for reference types,
hash = h; store cache of hash code hash = 31*hash + who.hashCode(); use hashCode()
return h;
hash = 31*hash + when.hashCode();
} for primitive types,
hash = 31*hash + ((Double) amount).hashCode();
use hashCode()
}
return hash;
of wrapper type
}
} typically a small prime
Q. What if hashCode() of string is 0?
9 10
Hash code design Modular hashing
"Standard" recipe for user-defined types. Hash code. An int between -231 and 231 - 1.
・
Combine each significant field using the 31x + y rule. Hash function. An int between 0 and M - 1 (for use as array index).
・
If field is a primitive type, use wrapper type hashCode().
typically a prime or power of 2
・
If field is null, return 0.
x
・
If field is a reference type, use hashCode(). applies rule recursively
private int hash(Key key)
・ If field is an array, apply to each entry. or use Arrays.deepHashCode() { return key.hashCode() % M; }
bug
x.hashCode()
private int hash(Key key)
In practice. Recipe works reasonably well; used in Java libraries. { return Math.abs(key.hashCode()) % M; }
In theory. Keys are bitstring; "universal" hash functions exist.
1-in-a-billion bug
hash(x)
hashCode() of "polygenelubricants" is -231
private int hash(Key key)
{ return (key.hashCode() & 0x7fffffff) % M; }
Basic rule. Need to use the whole key to compute hash code;
consult an expert for state-of-the-art hash codes. correct
11 12

Uniform hashing assumption Uniform hashing assumption
Uniform hashing assumption. Each key is equally likely to hash to an Uniform hashing assumption. Each key is equally likely to hash to an
integer between 0 and M - 1. integer between 0 and M - 1.
Bins and balls. Throw balls uniformly at random into M bins. Bins and balls. Throw balls uniformly at random into M bins.
0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
Birthday problem. Expect two balls in the same bin after ~ π M / 2 tosses.
Coupon collector. Expect every bin has ≥ 1 ball after ~ M ln M tosses.
Load balancing. After M tosses, expect most loaded bin has Hash value frequencies for words in Tale of Two Cities (M = 97)
Θ ( log M / log log M ) balls. Java's String data uniformly distribute the keys of Tale of Two Cities
13 14
Collisions
Collision. Two distinct keys hashing to same index.
・Birthday problem ⇒ can't avoid collisions unless you have
a ridiculous (quadratic) amount of memory.
・Coupon collector + load balancing ⇒ collisions are evenly distributed.
3.4 HASH TABLES
‣ hash functions
0
‣ separate chaining
1
hash("it") = 3
Algorithms ‣ linear probing 2
3 "it"
‣ context
??
4
hash("times") = 3
5
ROBERT SEDGEWICK | KEVIN WAYNE
http://algs4.cs.princeton.edu
Challenge. Deal with collisions efficiently.
16

Separate-chaining symbol table Separate-chaining symbol table:  Java implementation
Use an array of M < N linked lists.  [H. P. Luhn, IBM 1953]
public class SeparateChainingHashST<Key, Value>
・
| Hash:  map key to integer i between 0 and M - 1. |     |     |     |     | {   |     |     |     |     |
| ------------------------------------------------ | --- | --- | --- | --- | --- | --- | --- | --- | --- |
・ Insert:  put at front of ith chain (if not already there).    private int M = 97;               // number of chains array doubling and
|     |     |     |     |     |    private Node[] st = new Node[M];  // array of chains |     |     |     | halving code omitted |
| --- | --- | --- | --- | --- | ------------------------------------------------------- | --- | --- | --- | -------------------- |
・
Search:  need to search only ith chain.
   private static class Node
   {
key hash value
|     |     |     |     |     |       private Object key; | no generic array creation |     |     |     |
| --- | --- | --- | --- | --- | ------------------------- | ------------------------- | --- | --- | --- |
S   2   0
|     |           |     |          |     |       private Object val; | (declare key and value of type Object) |     |     |     |
| --- | --------- | --- | -------- | --- | ------------------------- | -------------------------------------- | --- | --- | --- |
|     | E   0   1 |     | A 8 E 12 |     |       private Node next;  |                                        |     |     |     |
      ...
A   0   2
   }
|     | R   4   3 | st[] |     |     |     |     |     |     |     |
| --- | --------- | ---- | --- | --- | --- | --- | --- | --- | --- |
null
|     | C   4   4 | 0   |     |     |    private int hash(Key key)                       |     |     |     |     |
| --- | --------- | --- | --- | --- | -------------------------------------------------- | --- | --- | --- | --- |
|     |           | 1   |     |     |    {  return (key.hashCode() & 0x7fffffff) % M;  } |     |     |     |     |
H   4   5
|     |     | 2   | X 7 S 0 |     |     |     |     |     |     |
| --- | --- | --- | ------- | --- | --- | --- | --- | --- | --- |
E   0   6
|     |           | 3   |           |     |    public Value get(Key key) {                    |     |     |     |     |
| --- | --------- | --- | --------- | --- | ------------------------------------------------- | --- | --- | --- | --- |
|     | X   2   7 | 4   |           |     |       int i = hash(key);                          |     |     |     |     |
|     |           |     | L 11 P 10 |     |                                                   |     |     |     |     |
|     | A   0   8 |     |           |     |       for (Node x = st[i]; x != null; x = x.next) |     |     |     |     |
         if (key.equals(x.key)) return (Value) x.val;
M   4   9
      return null;
|     | P   3  10 |     | M 9 H 5 | C 4 R 3 |     |     |     |     |     |
| --- | --------- | --- | ------- | ------- | --- | --- | --- | --- | --- |
   }
L   3  11
}
E   0  12
Hashing with separate chaining for standard indexing client  17 18
Separate-chaining symbol table:  Java implementation Analysis of separate chaining
Proposition.  Under uniform hashing assumption, prob. that the number of
public class SeparateChainingHashST<Key, Value>
keys in a list is within a constant factor of N / M is extremely close to 1.
{
   private int M = 97;               // number of chains
   private Node[] st = new Node[M];  // array of chains
Pf sketch.  Distribution of list size obeys a binomial distribution.
   private static class Node
   {
(10, .12511...)
|       private Object key; |     |     |     |     |     |     |     | .125 |     |
| ------------------------- | --- | --- | --- | --- | --- | --- | --- | ---- | --- |
      private Object val;
      private Node next;
0
      ...
|    } |     |     |     |     |     | 0 10 | 20  | 30  |     |
| ---- | --- | --- | --- | --- | --- | ---- | --- | --- | --- |
Binomial distribution (N = 104, M = 103, ! = 10)
   private int hash(Key key)
   {  return (key.hashCode() & 0x7fffffff) % M;  }
   public void put(Key key, Value val) { equals() and hashCode()
      int i = hash(key);
      for (Node x = st[i]; x != null; x = x.next) Consequence.  Number of probes for search/insert is proportional to N / M.
|          if (key.equals(x.key)) { x.val = val; return; } |     |     |     |     | ・   |     |     |     |     |
| -------------------------------------------------------- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
M too large  ⇒  too many empty chains.
      st[i] = new Node(key, val, st[i]);
|    } |     |     |     |     | ・   |     |     |     | M times faster than |
| ---- | --- | --- | --- | --- | --- | --- | --- | --- | ------------------- |
M too small  ⇒  chains too long.
sequential search
・
Typical choice:  M ~ N / 4  ⇒  constant-time ops.
}
19 20

Resizing in a separate-chaining hash table Deletion in a separate-chaining hash table
Goal.  Average length of list N / M  = constant. Q.  How to delete a key (and its associated value)?
・
Double size of array M when N / M  ≥  8. A.  Easy: need only consider chain containing key.
・ Halve size of array M when N / M  ≤  2.
・
| Need to rehash all keys when resizing. |     |     |     | x.hashCode() does not change |     |     |     |     |     |     |     |     |
| -------------------------------------- | --- | --- | --- | ---------------------------- | --- | --- | --- | --- | --- | --- | --- | --- |
but hash(x) can change
|     |     |     |     |     |     |     | before deleting C  |     |     | after deleting C  |     |     |
| --- | --- | --- | --- | --- | --- | --- | ------------------ | --- | --- | ----------------- | --- | --- |
before resizing
st[]
|     |     | A B | C D | E   | F G | H I J |      | K I |     |      | K I |     |
| --- | --- | --- | --- | --- | --- | ----- | ---- | --- | --- | ---- | --- | --- |
|     |     |     |     |     |     |       | st[] |     |     | st[] |     |     |
0
|     |     |     |     |     |     |     | 0   |     |     | 0   |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
|     | 1   |     |     |     |     |     |     | P N | L   |     | P N | L   |
|     |     | K L | M N | O   | P   |     |     |     |     |     |     |     |
|     |     |     |     |     |     |     | 1   |     |     | 1   |     |     |
|     |     |     |     |     |     |     | 2   | J F | C B | 2   | J F | B   |
|     |     |     |     |     |     |     | 3   |     |     | 3   |     |     |
after resizing
|     |     | K I |     |     |     |     |     | O M |     |     | O M |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
st[]
0
|     |     | P N | L E | A   |     |     |     |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
1
|     | 2   | J F | C B |     |     |     |     |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
3
|     |     | O M | H G | D   |     |     |     |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
21 22
Symbol table implementations:  summary
|     | ggguuuaaarrraaannnttteeeeee |     |     | aaavvveeerrraaagggeee   cccaaassseee |     |     |     |     |     |     |     |     |
| --- | --------------------------- | --- | --- | ------------------------------------ | --- | --- | --- | --- | --- | --- | --- | --- |
oorrddeerreedd kkeeyy
iimmpplleemmeennttaattiioonn
ooppss?? iinntteerrffaaccee
|                   | search | insert delete | search hit | insert | delete |          |     |     |                  |     |     |     |
| ----------------- | ------ | ------------- | ---------- | ------ | ------ | -------- | --- | --- | ---------------- | --- | --- | --- |
| sequential search |        |               |            |        |        |          |     |     | 3.4  HASH TABLES |     |     |     |
|                   | N      | N N           | ½ N        | N      | ½ N    | equals() |     |     |                  |     |     |     |
(unordered list)
| binary search |      |     |      |     |     |               |     |     | ‣ hash functions |     |     |     |
| ------------- | ---- | --- | ---- | --- | --- | ------------- | --- | --- | ---------------- | --- | --- | --- |
|               | lg N | N N | lg N | ½ N | ½ N | ✔ compareTo() |     |     |                  |     |     |     |
(ordered array)
‣ separate chaining
| BST | N   | N N | 1.39 lg N | 1.39 lg N | √ N | ✔ compareTo() |     |     |                  |     |     |     |
| --- | --- | --- | --------- | --------- | --- | ------------- | --- | --- | ---------------- | --- | --- | --- |
|     |     |     |           |           |     |               |     |     | ‣ linear probing |     |     |     |
Algorithms
‣ context
|     | 2 lg N 2 lg N | 2 lg N | 1.0 lg N | 1.0 lg N | 1.0 lg N | ✔ compareTo() |     |     |     |     |     |     |
| --- | ------------- | ------ | -------- | -------- | -------- | ------------- | --- | --- | --- | --- | --- | --- |
red-black BST
equals() ROBERT SEDGEWICK  |  KEVIN WAYNE
| separate chaining | N   | N N | 3-5 * | 3-5 * | 3-5 * | hashCode() |     |     |     |     |     |     |
| ----------------- | --- | --- | ----- | ----- | ----- | ---------- | --- | --- | --- | --- | --- | --- |
http://algs4.cs.princeton.edu
*  under uniform hashing assumption
23

Collision resolution:  open addressing Linear-probing hash table demo
Open addressing.  [Amdahl-Boehme-Rocherster-Samuel, IBM 1953]  Hash.  Map key to integer i between 0 and M-1.
When a new key collides, find next empty slot, and put it there. Insert.  Put at table index i if free; if not try i+1, i+2, etc.
|     |     | st[0] | jocularly |     |     |     |     |     |     |     |     |     |
| --- | --- | ----- | --------- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
linear-probing hash table
|     |     | st[1] | null     |     |     |     |       |       |       |         |          |     |
| --- | --- | ----- | -------- | --- | --- | --- | ----- | ----- | ----- | ------- | -------- | --- |
|     |     | st[2] | listen   |     |     |     |       |       |       |         |          |     |
|     |     | st[3] | suburban |     |     |     |       |       |       |         |          |     |
|     |     |       |          |     |     |     | 0 1 2 | 3 4 5 | 6 7 8 | 9 10 11 | 12 13 14 | 15  |
null
st[]
|     |     | st[30000] | browsing |     |     | M = 16 |     |     |     |     |     |     |
| --- | --- | --------- | -------- | --- | --- | ------ | --- | --- | --- | --- | --- | --- |
linear probing (M = 30001, N = 15000)
25
Linear-probing hash table demo Linear-probing hash table summary
Hash.  Map key to integer i between 0 and M-1. Hash.  Map key to integer i between 0 and M-1.
Search.  Search table index i; if occupied but no match, try i+1, i+2, etc. Insert.  Put at table index i if free; if not try i+1, i+2, etc.
Search.  Search table index i; if occupied but no match, try i+1, i+2, etc.
Note.  Array size M must be greater than number of key-value pairs N.
| search K |     |     |     |     |     |     |     |     |     |     |     |     |
| -------- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
hash(K) = 5
0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
| st[] P | M   | A C S | H L | E   | R X | st[]   | P M | A C | S H L | E   | R   | X   |
| ------ | --- | ----- | --- | --- | --- | ------ | --- | --- | ----- | --- | --- | --- |
| M = 16 |     |       | K   |     |     | M = 16 |     |     |       |     |     |     |
search miss
(return null)
28

Linear-probing symbol table: Java implementation Linear-probing symbol table: Java implementation
public class LinearProbingHashST<Key, Value> public class LinearProbingHashST<Key, Value>
{ {
private int M = 30001; private int M = 30001;
array doubling and
private Value[] vals = (Value[]) new Object[M]; private Value[] vals = (Value[]) new Object[M];
halving code omitted
private Key[] keys = (Key[]) new Object[M]; private Key[] keys = (Key[]) new Object[M];
private int hash(Key key) { /* as before */ } private int hash(Key key) { /* as before */ }
private void put(Key key, Value val) { /* next slide */ } private Value get(Key key) { /* previous slide */ }
public Value get(Key key) public void put(Key key, Value val)
{ {
for (int i = hash(key); keys[i] != null; i = (i+1) % M) int i;
if (key.equals(keys[i])) for (i = hash(key); keys[i] != null; i = (i+1) % M)
return vals[i]; if (keys[i].equals(key))
return null; break;
} keys[i] = key;
vals[i] = val;
}
} }
29 30
Clustering Knuth's parking problem
Cluster. A contiguous block of items. Model. Cars arrive at one-way street with M parking spaces.
Observation. New keys likely to hash into middle of big clusters. Each desires a random space i : if space i is taken, try i + 1, i + 2, etc.
Q. What is mean displacement of a car?
displacement = 3
Half-full. With M / 2 cars, mean displacement is ~ 3 / 2.
Full. With M cars, mean displacement is ~ π M / 8 .
31 32

Analysis of linear probing Resizing in a linear-probing hash table
Proposition.  Under uniform hashing assumption, the average # of probes  Goal.  Average length of list N / M  ≤  ½.
・
in a linear probing hash table of size M that contains N  =  α M keys is: Double size of array M when N / M  ≥  ½.
・ Halve   size of array M when N / M  ≤  ⅛.
|     |     | 1   |     | 1   | 1   |     | 1   |     |     |     | ・   |     |     |     |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
Need to rehash all keys when resizing.
|     |     |     | 1 +        |     |                      | 1 + |     |     |     |     |     |     |     |     |     |     |     |     |     |
| --- | --- | --- | ---------- | --- | -------------------- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
|     |     | ⇥ 2 |            | 1   | ⇥ 2                  | (1  |  )2 |     |     |     |     |     |     |     |     |     |     |     |     |
|     |     |     |            |   ⇥ |                      |     |     | ⇥   |     |     |     |     |     |     |     |     |     |     |     |
|     |     |     | search hit |     | search miss / insert |     |     |     |     |     |     |     |     |     |     |     |     |     |     |
before resizing
Pf.
|     |     |     |     |     |     |     |     |     |     |     |        | 0 1 2 | 3   | 4 5 6 | 7   |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ------ | ----- | --- | ----- | --- | --- | --- | --- | --- |
|     |     |     |     |     |     |     |     |     |     |     | keys[] | E S   |     | R A   |     |     |     |     |     |
|     |     |     |     |     |     |     |     |     |     |     | vals[] | 1 0   |     | 3 2   |     |     |     |     |     |
after resizing
Parameters.
|     |     |     |     |     |     |     |     |     |     |     |     | 0 1 2 | 3   | 4 5 6 | 7 8 | 9 10 11 | 12  | 13 14 | 15  |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ----- | --- | ----- | --- | ------- | --- | ----- | --- |
・ M too large  ⇒  too many empty array entries.
|     |     |     |     |     |     |     |     |     |     |     | keys[] |     | A   | S   |     | E   |     | R   |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ------ | --- | --- | --- | --- | --- | --- | --- | --- |
・
M too small  ⇒  search time blows up.
|     |     |     |     |     |     |                                      |     |     |     |     | vals[] |     |     | 2 0 |     | 1   |     |     | 3   |
| --- | --- | --- | --- | --- | --- | ------------------------------------ | --- | --- | --- | --- | ------ | --- | --- | --- | --- | --- | --- | --- | --- |
| ・   |     |     |     |     |     | # probes for search hit is about 3/2 |     |     |     |     |        |     |     |     |     |     |     |     |     |
Typical choice:  α  =  N / M  ~  ½.
# probes for search miss is about 5/2
33 34
Deletion in a linear-probing hash table ST implementations:  summary
Q.  How to delete a key (and its associated value)?
A.  Requires some care:  can't just delete array entries. ggguuuaaarrraaannnttteeeeee aaavvveeerrraaagggeee   cccaaassseee
|     |     |     |     |     |     |     |     |     |     |     |     |     |     |     |     |     |     | oorrddeerreedd | kkeeyy |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | -------------- | ------ |
iimmpplleemmeennttaattiioonn
|     |     |     |     |     |     |     |     |     |     |     |     |        |               |            |        |        |     | ooppss?? | iinntteerrffaaccee |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ------ | ------------- | ---------- | ------ | ------ | --- | -------- | ------------------ |
|     |     |     |     |     |     |     |     |     |     |     |     | search | insert delete | search hit | insert | delete |     |          |                    |
sequential search
|     |     |     |     |     |     |     |     |     |     |     |     | N   | N   | N ½ N | N   | ½ N |     |     | equals() |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ----- | --- | --- | --- | --- | -------- |
(unordered list)
before deleting S
|     | 0 1 | 2   | 3 4 | 5   | 6 7 8 | 9   | 10 11 | 12 13 | 14 15 |     |     |     |     |     |     |     |     |     |     |
| --- | --- | --- | --- | --- | ----- | --- | ----- | ----- | ----- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
binary search
|     |     |     |     |     |     |     |     |     |     |     |     | lg N | N   | N lg N | ½ N | ½ N |     | ✔   | compareTo() |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ---- | --- | ------ | --- | --- | --- | --- | ----------- |
(ordered array)
| keys[] | P M  |     | A   | C   | S H L  |     | E   |     | R X |     |     |     |     |             |           |     |     |     |             |
| ------ | ---- | --- | --- | --- | ------ | --- | --- | --- | --- | --- | --- | --- | --- | ----------- | --------- | --- | --- | --- | ----------- |
| vals[] | 10 9 |     | 8   | 4   | 0 5 11 |     | 12  |     | 3 7 |     |     |     |     |             |           |     |     |     |             |
|        |      |     |     |     |        |     |     |     |     |     | BST | N   | N   | N 1.39 lg N | 1.39 lg N | √   | N   | ✔   | compareTo() |

|     |     |     |     |     |     |     |     |     |     |     |     | 2 lg N 2 lg N | 2 lg N | 1.0 lg N | 1.0 lg N | 1.0 lg N |     | ✔   | compareTo() |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ------------- | ------ | -------- | -------- | -------- | --- | --- | ----------- |
red-black BST
doesn't work, e.g., if hash(H) = 4
after deleting S ?
equals()
0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 separate chaining N N N 3-5 * 3-5 * 3-5 * hashCode()
| keys[] | P M |     | A   | C   | H L |     | E   |     | R X |     |     |     |     |     |     |     |     |     |     |
| ------ | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
equals()
|        |      |     |     |     |      |     |     |     |     | linear probing |     | N   | N   | N 3-5 * | 3-5 * | 3-5 * |     |     |            |
| ------ | ---- | --- | --- | --- | ---- | --- | --- | --- | --- | -------------- | --- | --- | --- | ------- | ----- | ----- | --- | --- | ---------- |
| vals[] | 10 9 |     | 8   | 4   | 5 11 |     | 12  |     | 3 7 |                |     |     |     |         |       |       |     |     | hashCode() |
*  under uniform hashing assumption
35 36

War story:  algorithmic complexity attacks
Q.  Is the uniform hashing assumption important in practice?
A. Obvious situations:  aircraft control, nuclear reactor, pacemaker.
A. Surprising situations:  denial-of-service attacks.
st[]
3.4  HASH TABLES
0
1
2
‣ hash functions
|     |     |     |     |     | 3   | malicious adversary learns your hash function |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --------------------------------------------- | --- | --- | --- | --- |
‣ separate chaining 4 (e.g., by reading Java API) and causes a big pile-up
5
Bug 750533 - (CVE-2012-2739) CVE-2012-2739 java: hash table collisions Format For Printing  - XML  - Clone This Bug  - Last Comment in single slot that grinds performance to a halt
| CPU uAsagle gDoSo (orCiERtTh-20m11-0s03) |     | ‣ linear probing |     |     | 6   |     |     |     |     |     |
| ---------------------------------------- | --- | ---------------- | --- | --- | --- | --- | --- | --- | --- | --- |
7
| Status:ASSIGNED |     | ‣ conRetpeorxtetd: | 2011-11-01 10:13 EDT by Jan Lieskovsky |     |     |     |     |     |     |     |
| --------------- | --- | ------------------ | -------------------------------------- | --- | --- | --- | --- | --- | --- | --- |
|                 |     | Modified:          | 2012-11-27 10:50 EST (History)         |     |     |     |     |     |     |     |
Aliases:CVE-2012-2739 (edit)
|     |     | CC List: | 8 users (show) |     |     |     |     |     |     |     |
| --- | --- | -------- | -------------- | --- | --- | --- | --- | --- | --- | --- |
Real-world exploits.  [Crosby-Wallach 2003]
| R P r o d | u S c t : S e c u r i t y   | R    K e s p o n  sW e |           |     |     |     |     |     |     |     |     |
| --------- | ---------------------------------------------------- | --------- | --- | --- | --- | --- | --- | --- | --- | --- |
| O B E R T | E D G E W I C K E V I N A Y N E                      | See Also: |     |     | ・   |     |     |     |     |     |
Co m p o n e n t : v u l n e r a b i l i t y   ( S h o w   o th e r bugs) Bro server:  send carefully chosen packets to DOS the server,
htVteprs:/io/na(slg):su4ns.cpesc.ipfierdinceton.edu
Fixed In Version:
using less bandwidth than a dial-up modem.
| Platform:All Linux |     | Doc Type: | Bug Fix |     |     |     |     |     |     |     |
| ------------------ | --- | --------- | ------- | --- | --- | --- | --- | --- | --- | --- |
|                    |     | Doc Text: |         |     | ・   |     |     |     |     |     |
Priority:medium Severity: medium Perl 5.8.0:  insert carefully chosen strings into associative array.
Clone Of:
Target Milestone:---
|     |     |     |     |     | ・   |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
Target Release:--- Environment: Linux 2.4.20 kernel:  save files with carefully chosen names.
| Assigned To:Red Hat Security Response Team |     | Last Closed: | 2011-12-29 07:40:08 |     |     |     |     |     |     |     |
| ------------------------------------------ | --- | ------------ | ------------------- | --- | --- | --- | --- | --- | --- | --- |
QA Contact: 38
URL:
Whiteboard:impact=moderate,public=20111228,repor...
Keywords:Reopened, Security
War story:  algorithmic complexity attacks Algorithmic complexity attack on Java
Depends On:
Blocks:hashdos/oCERT-2011-003 750536
|     | Show dependency tree / graph |     |     |     |     |     |     |     |     |     |
| --- | ---------------------------- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
A Java bug report. Goal.  Find family of strings with the same hash code.
Groups:  None (edit) Solution.  The base-31 hash code is part of Java's string API.
Attachments (Terms of Use)
Add an attachment (proposed patch, testcase, etc.)
| Jan Lieskovsky  | 2011-11-01 10:13:47 EDT |     |     | Description |     |     |     |     |     |     |
| --------------- | ----------------------- | --- | --- | ----------- | --- | --- | --- | --- | --- | --- |
Julian Wälde and Alexander Klink reported that the String.hashCode() hash function is not sufficiently collision  key hashCode() key hashCode() key hashCode()
resistant.  hashCode() value is used in the implementations of HashMap and Hashtable classes:
http://docs.oracle.com/javase/6/docs/api/java/util/HashMap.html "Aa" 2112 "AaAaAaAa" -540425984 "BBAaAaAa" -540425984
http://docs.oracle.com/javase/6/docs/api/java/util/Hashtable.html
|     |     |     |     |     | "BB" | 2112 | "AaAaAaBB" | -540425984 | "BBAaAaBB" | -540425984 |
| --- | --- | --- | --- | --- | ---- | ---- | ---------- | ---------- | ---------- | ---------- |
A specially-crafted set of keys could trigger hash function collisions, which can degrade performance of HashMap
or Hashtable by changing hash table operations complexity from an expected/average O(1) to the worst case O(n).
Reporters were able to find colliding strings efficiently using equivalent substrings and meet in the middle  "AaAaBBAa" -540425984 "BBAaBBAa" -540425984
techniques.
|     |     |     |     |     |     |     | "AaAaBBBB" | -540425984 | "BBAaBBBB" | -540425984 |
| --- | --- | --- | --- | --- | --- | --- | ---------- | ---------- | ---------- | ---------- |
This problem can be used to start a denial of service attack against Java applications that use untrusted inputs
as HashMap or Hashtable keys.  An example of such application is web application server (such as tomcat, see bug
#750521) that may fill hash tables with data from HTTP request (such as GET or POST parameters).  A remote  "AaBBAaAa" -540425984 "BBBBAaAa" -540425984
attack could use that to make JVM use excessive amount of CPU time by sending a POST request with large amount
of parameters which hash to the same value.
|     |     |     |     |     |     |     | "AaBBAaBB" | -540425984 | "BBBBAaBB" | -540425984 |
| --- | --- | --- | --- | --- | --- | --- | ---------- | ---------- | ---------- | ---------- |
This problem is similar to the issue that was previously reported for and fixed
| in e.g. perl: |     |     |     |     |     |     | "AaBBBBAa" | -540425984 | "BBBBBBAa" | -540425984 |
| ------------- | --- | --- | --- | --- | --- | --- | ---------- | ---------- | ---------- | ---------- |
  http://www.cs.rice.edu/~scrosby/hash/CrosbyWallach_UsenixSec2003.pdf
|                 |                         |     |     |           |     |     | "AaBBBBBB" | -540425984 | "BBBBBBBB" | -540425984 |
| --------------- | ----------------------- | --- | --- | --------- | --- | --- | ---------- | ---------- | ---------- | ---------- |
| Jan Lieskovsky  | 2011-11-01 10:18:44 EDT |     |     | Comment 2 |     |     |            |            |            |            |
Acknowledgements: 2N strings of length 2N that hash to same value!
Red Hat would like to thank oCERT for reporting this issue. oCERT acknowledges Julian Wälde and Alexander Klink
as the original reporters.
39 40
| Tomas Hoger  | 2011-12-29 07:23:27 EST |     |     | Comment 11 |     |     |     |     |     |     |
| ------------ | ----------------------- | --- | --- | ---------- | --- | --- | --- | --- | --- | --- |
This issue was presented on 28C3:
http://events.ccc.de/congress/2011/Fahrplan/events/4680.en.html
Details were posted to full-disclosure:
http://seclists.org/fulldisclosure/2011/Dec/477

Diversion:  one-way hash functions Separate chaining vs. linear probing
One-way hash function.  "Hard" to find a key that will hash to a desired  Separate chaining.
・
value (or two keys that hash to same value). Performance degrades gracefully.
・ Clustering less sensitive to poorly-designed hash function.
Ex.  MD4, MD5, SHA-0, SHA-1, SHA-2, WHIRLPOOL, RIPEMD-160, ….
key hash value
|     | Linear probing. |     |     | S   2   0 |     |     |
| --- | --------------- | --- | --- | --------- | --- | --- |
known to be insecure
|     | ・   | Less wasted space. |     | E   0   1 |     | A 8 E 12 |
| --- | --- | ------------------ | --- | --------- | --- | -------- |
|     | ・   |                    |     | A   0   2 |     |          |
Better cache performance.
|     |     |     |     | R   4   3 | st[] |     |
| --- | --- | --- | --- | --------- | ---- | --- |
null
| String password = args[0];  |     |     |     |     | 0   |     |
| --------------------------- | --- | --- | --- | --- | --- | --- |
C   4   4
MessageDigest sha1 = MessageDigest.getInstance("SHA1"); H   4   5 1
| byte[] bytes = sha1.digest(password); |     |     |     |     | 2   | X 7 S 0 |
| ------------------------------------- | --- | --- | --- | --- | --- | ------- |
E   0   6
3
|                                  |     |     |     | X   2   7 | 4   |           |
| -------------------------------- | --- | --- | --- | --------- | --- | --------- |
| /* prints bytes as hex string */ |     |     |     |           |     | L 11 P 10 |
A   0   8
M   4   9
P   3  10
M 9 H 5 C 4 R 3
L   3  11
|     |     | 0 1 2 | 3 4 5 6 | 7 8E  9 0 1 01211 | 12 13 14 15 |     |
| --- | --- | ----- | ------- | ----------------- | ----------- | --- |
Applications.  Digital fingerprint, message digest, storing passwords.
|     | keys[] | P M | A C S | H L E | Hashing with R  sepa X rate chaining for standard indexing client  |     |
| --- | ------ | --- | ----- | ----- | ------------------------------------------------------------------ | --- |
Caveat.  Too expensive for use in ST implementations. vals[] 10 9 8 4 0 5 11 12 3 7
41 42
Hashing: variations on the theme Hash tables vs. balanced search trees
| Many improved versions have been studied. | Hash tables. |     |     |     |     |     |
| ----------------------------------------- | ------------ | --- | --- | --- | --- | --- |
・ Simpler to code.
・
Two-probe hashing.  [ separate-chaining variant ] No effective alternative for unordered keys.
| ・   | ・   |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- |
Hash to two positions, insert key in shorter of the two chains. Faster for simple keys (a few arithmetic ops versus log N compares).
| ・   | ・   |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- |
Reduces expected length of the longest chain to log log N. Better system support in Java for strings (e.g., cached hash code).
Double hashing.   [ linear-probing variant ] Balanced search trees.
| ・   | ・   |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- |
Use linear probing, but skip a variable amount, not just 1 each time. Stronger performance guarantee.
| ・   | ・   |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- |
Effectively eliminates clustering. Support for ordered ST operations.
・ Can allow table to become nearly full. ・ Easier to implement compareTo() correctly than equals() and hashCode().
・
More difficult to implement delete.
Java system includes both.
Cuckoo hashing.  [ linear-probing variant ] ・ Red-black BSTs:  java.util.TreeMap, java.util.TreeSet.
| ・   | ・   |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- |
Hash key to two positions; insert key into either position; if occupied,  Hash tables:  java.util.HashMap, java.util.IdentityHashMap.
reinsert displaced key into its alternative position (and recur).
・ Constant worst-case time for search.
43 44