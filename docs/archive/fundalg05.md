J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 1
Fundamental Algorithms
Chapter 5: Hash Tables
JanKˇret´ınsky´
Winter2021/22

Definition(GeneralisedSearchProblem)
• Storeasetofobjectsconsistingofakeyandadditionaldata:
Object := (
key: Integer , .
record: Data );
• search/insert/deleteobjectsinthisset
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 2
| Generalised | Search | Problem |     |
| ----------- | ------ | ------- | --- |
Definition(SearchProblem)
| Input: asequenceorsetAofnelements∈A,andanx |                 |                 | ∈A.         |
| ------------------------------------------ | --------------- | --------------- | ----------- |
| Output: Indexi                             | ∈{1,...,n}withx | =A[i],orNIL,ifx | (cid:54)∈A. |
• complexitydependsondatastructure
• complexityofoperationstosetupdatastructure? (insert/delete)

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 2
| Generalised | Search | Problem |     |
| ----------- | ------ | ------- | --- |
Definition(SearchProblem)
| Input: asequenceorsetAofnelements∈A,andanx |                 |                 | ∈A.         |
| ------------------------------------------ | --------------- | --------------- | ----------- |
| Output: Indexi                             | ∈{1,...,n}withx | =A[i],orNIL,ifx | (cid:54)∈A. |
• complexitydependsondatastructure
• complexityofoperationstosetupdatastructure? (insert/delete)
Definition(GeneralisedSearchProblem)
• Storeasetofobjectsconsistingofakeyandadditionaldata:
Object := (
| key:    | Integer | , . |     |
| ------- | ------- | --- | --- |
| record: | Data    | );  |     |
• search/insert/deleteobjectsinthisset

Direct-AddressTable:
• assume: limitednumberofvaluesforthekeys:
U ={0,1,...,m−1}
• allocatetableofsizem
• usekeysdirectlyasindex
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 3
Direct-Address Tables
Definition(tableasdatastructure)
• similartoarray: accesselementviaindex
• usuallycontainselementsonlyforsomeoftheindices

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 3
Direct-Address Tables
Definition(tableasdatastructure)
• similartoarray: accesselementviaindex
• usuallycontainselementsonlyforsomeoftheindices
Direct-AddressTable:
• assume: limitednumberofvaluesforthekeys:
U ={0,1,...,m−1}
• allocatetableofsizem
• usekeysdirectlyasindex

DirAddrDelete(T:Table , x:Object){
T[x.key] := NIL;
}
DirAddrSearch(T:Table , key:Integer){
return T[key];
}
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 4
| Direct-Address Tables | (2)         |     |
| --------------------- | ----------- | --- |
| DirAddrInsert(T:Table | , x:Object) | {   |
| T[x.key] := x;        |             |     |
}

DirAddrSearch(T:Table , key:Integer){
return T[key];
}
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 4
| Direct-Address Tables | (2)         |     |
| --------------------- | ----------- | --- |
| DirAddrInsert(T:Table | , x:Object) | {   |
| T[x.key] := x;        |             |     |
}
| DirAddrDelete(T:Table | , x:Object){ |     |
| --------------------- | ------------ | --- |
| T[x.key] := NIL;      |              |     |
}

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 4
| Direct-Address Tables | (2)         |     |
| --------------------- | ----------- | --- |
| DirAddrInsert(T:Table | , x:Object) | {   |
| T[x.key] := x;        |             |     |
}
| DirAddrDelete(T:Table | , x:Object){ |     |
| --------------------- | ------------ | --- |
| T[x.key] := NIL;      |              |     |
}
| DirAddrSearch(T:Table | , key:Integer){ |     |
| --------------------- | --------------- | --- |
return T[key];
}

Disadvantages:
• mhastobesmall,
orotherwise,thetablehastobeverylarge!
• ifonlyfewelementsarestored,lotsoftableelementsareunused
(wasteofmemory)
• allkeysneedtobedistinct
(theyshouldbe,anyway)
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 5
Direct-Address Tables (3)
Advantage:
• veryfast: search/delete/insertisΘ(1)

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 5
Direct-Address Tables (3)
Advantage:
• veryfast: search/delete/insertisΘ(1)
Disadvantages:
• mhastobesmall,
orotherwise,thetablehastobeverylarge!
• ifonlyfewelementsarestored,lotsoftableelementsareunused
(wasteofmemory)
• allkeysneedtobedistinct
(theyshouldbe,anyway)

Definition(hashfunction,hashtable)
Suchafunctionhiscalledahashfunction.
Therespectivetableiscalledahashtable.
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 6
Hash Tables
Idea: computeindexfromkey
Wanted: functionhthat
• mapsagivenkeytoanindex,
• hasarelativelysmallrangeofvalues,and
• canbecomputedefficiently,

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 6
Hash Tables
Idea: computeindexfromkey
Wanted: functionhthat
• mapsagivenkeytoanindex,
• hasarelativelysmallrangeofvalues,and
• canbecomputedefficiently,
Definition(hashfunction,hashtable)
Suchafunctionhiscalledahashfunction.
Therespectivetableiscalledahashtable.

|     |     |     |     | HashDelete(T:Table | , x:Object) | {   |
| --- | --- | --- | --- | ------------------ | ----------- | --- |
|     |     |     |     | T[h(x.key)]:=      | NIL;        |     |
}
|     |     |     |     | HashSearch(T:Table | , x:Object) | {   |
| --- | --- | --- | --- | ------------------ | ----------- | --- |
return T[h(x.key)];
}
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 7
| Hash Tables        | – Insert, | Delete,     | Search |     |     |     |
| ------------------ | --------- | ----------- | ------ | --- | --- | --- |
| HashInsert(T:Table |           | , x:Object) | {      |     |     |     |
| T[h(x.key)]        | :=        | x;          |        |     |     |     |
}

|     |     |     |     | HashSearch(T:Table | , x:Object) | {   |
| --- | --- | --- | --- | ------------------ | ----------- | --- |
return T[h(x.key)];
}
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 7
| Hash Tables        | – Insert, | Delete,     | Search |     |     |     |
| ------------------ | --------- | ----------- | ------ | --- | --- | --- |
| HashInsert(T:Table |           | , x:Object) | {      |     |     |     |
| T[h(x.key)]        | :=        | x;          |        |     |     |     |
}
| HashDelete(T:Table |      | , x:Object) | {   |     |     |     |
| ------------------ | ---- | ----------- | --- | --- | --- | --- |
| T[h(x.key)]:=      | NIL; |             |     |     |     |     |
}

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 7
| Hash Tables        | – Insert, | Delete,     | Search |
| ------------------ | --------- | ----------- | ------ |
| HashInsert(T:Table |           | , x:Object) | {      |
| T[h(x.key)]        | :=        | x;          |        |
}
| HashDelete(T:Table |      | , x:Object) | {   |
| ------------------ | ---- | ----------- | --- |
| T[h(x.key)]:=      | NIL; |             |     |
}
| HashSearch(T:Table |              | , x:Object) | {   |
| ------------------ | ------------ | ----------- | --- |
| return             | T[h(x.key)]; |             |     |
}

Disadvantages:
• valuesofhhavetobedistinctforallkeys
• however: impossibletofindahashfunctionthatproduces
distinctvaluesforanysetofstoreddata
ToDo: dealwithcollisions:
objectswithdifferentkeysthatshareacommonhashvaluehaveto
bestoredinthesametableelement
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 8
So Far: Naive Hashing
Advantages:
• stillveryfast: search/delete/insertisΘ(1),ifhisΘ(1)
• sizeofthetablecanbechosenfreely,providedthereisan
appropriatehashfunctionh

ToDo: dealwithcollisions:
objectswithdifferentkeysthatshareacommonhashvaluehaveto
bestoredinthesametableelement
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 8
So Far: Naive Hashing
Advantages:
• stillveryfast: search/delete/insertisΘ(1),ifhisΘ(1)
• sizeofthetablecanbechosenfreely,providedthereisan
appropriatehashfunctionh
Disadvantages:
• valuesofhhavetobedistinctforallkeys
• however: impossibletofindahashfunctionthatproduces
distinctvaluesforanysetofstoreddata

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 8
So Far: Naive Hashing
Advantages:
• stillveryfast: search/delete/insertisΘ(1),ifhisΘ(1)
• sizeofthetablecanbechosenfreely,providedthereisan
appropriatehashfunctionh
Disadvantages:
• valuesofhhavetobedistinctforallkeys
• however: impossibletofindahashfunctionthatproduces
distinctvaluesforanysetofstoreddata
ToDo: dealwithcollisions:
objectswithdifferentkeysthatshareacommonhashvaluehaveto
bestoredinthesametableelement

|     |     |     | ChainHashInsert(T:Table    | , x:Object) | {   |
| --- | --- | --- | -------------------------- | ----------- | --- |
|     |     |     | insert x into T[h(x.key)]; |             |     |
}
|     |     |     | ChainHashDelete(T:Table    | , x:Object) | {   |
| --- | --- | --- | -------------------------- | ----------- | --- |
|     |     |     | delete x from T[h(x.key)]; |             |     |
}
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 9
| Resolve | Collisions | by Chaining |     |     |     |
| ------- | ---------- | ----------- | --- | --- | --- |
Idea:
• useatableofcontainers
• containerscanholdanarbitrarilylargeamountofdata
• using(linked)listsascontainers: chaining

|     |     |     |     | ChainHashDelete(T:Table    | , x:Object) | {   |
| --- | --- | --- | --- | -------------------------- | ----------- | --- |
|     |     |     |     | delete x from T[h(x.key)]; |             |     |
}
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 9
| Resolve | Collisions | by Chaining |     |     |     |     |
| ------- | ---------- | ----------- | --- | --- | --- | --- |
Idea:
• useatableofcontainers
• containerscanholdanarbitrarilylargeamountofdata
• using(linked)listsascontainers: chaining
| ChainHashInsert(T:Table |               | , x:Object)  | {   |     |     |     |
| ----------------------- | ------------- | ------------ | --- | --- | --- | --- |
|                         | insert x into | T[h(x.key)]; |     |     |     |     |
}

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 9
| Resolve | Collisions | by Chaining |     |
| ------- | ---------- | ----------- | --- |
Idea:
• useatableofcontainers
• containerscanholdanarbitrarilylargeamountofdata
• using(linked)listsascontainers: chaining
| ChainHashInsert(T:Table |               | , x:Object)  | {   |
| ----------------------- | ------------- | ------------ | --- |
|                         | insert x into | T[h(x.key)]; |     |
}
| ChainHashDelete(T:Table |               | , x:Object)  | {   |
| ----------------------- | ------------- | ------------ | --- |
|                         | delete x from | T[h(x.key)]; |     |
}

Advantages:
• hashfunctionnolongerhastoreturndistinctvalues
• stillveryfast,ifthelistsareshort
Disadvantages:
•
delete/searchisΘ(k),ifk elementsareintheaccessedlist
•
worstcase: allelementsstoredinonesinglelist(veryunlikely).
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 10
| Resolve                 | Collisions           | by Chaining  |          |        |
| ----------------------- | -------------------- | ------------ | -------- | ------ |
| ChainHashSearch(T:Table |                      | , x:Object)  | {        |        |
| return                  | ListSearch(x,        | T[h(x.key)]  | );       |        |
|                         | ! result : reference | to x or NIL, | if x not | found; |
}

Disadvantages:
•
delete/searchisΘ(k),ifk elementsareintheaccessedlist
•
worstcase: allelementsstoredinonesinglelist(veryunlikely).
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 10
| Resolve                 | Collisions           | by Chaining  |          |        |
| ----------------------- | -------------------- | ------------ | -------- | ------ |
| ChainHashSearch(T:Table |                      | , x:Object)  | {        |        |
| return                  | ListSearch(x,        | T[h(x.key)]  | );       |        |
|                         | ! result : reference | to x or NIL, | if x not | found; |
}
Advantages:
• hashfunctionnolongerhastoreturndistinctvalues
• stillveryfast,ifthelistsareshort

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 10
| Resolve                 | Collisions           | by Chaining  |          |        |
| ----------------------- | -------------------- | ------------ | -------- | ------ |
| ChainHashSearch(T:Table |                      | , x:Object)  | {        |        |
| return                  | ListSearch(x,        | T[h(x.key)]  | );       |        |
|                         | ! result : reference | to x or NIL, | if x not | found; |
}
Advantages:
• hashfunctionnolongerhastoreturndistinctvalues
• stillveryfast,ifthelistsareshort
Disadvantages:
•
|     | delete/searchisΘ(k),ifk | elementsareintheaccessedlist |     |     |
| --- | ----------------------- | ---------------------------- | --- | --- |
•
|     | worstcase: allelementsstoredinonesinglelist(veryunlikely). |     |     |     |
| --- | ---------------------------------------------------------- | --- | --- | --- |

Searchcomplexity:
• onaverage,thelistcorrespondingtotherequestedkeywillhave
αelements
• unsuccessfulsearch: comparetherequestedkeywithallobjects
inthelist,i.e. O(α)operations
• successfulsearch: requestedkeylastinthelist;
⇒alsoO(α)operations
Expected: Averagecomplexity: O(α)operations
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 11
Chaining – Average Search Complexity
Assumptions:
• hashtablehasmslots(tableofmlists)
• containsnelements⇒loadfactor: α= n
m
• h(k)canbecomputedinO(1)forallk
• allvaluesofhareequallylikelytooccur

Expected: Averagecomplexity: O(α)operations
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 11
| Chaining | – Average | Search | Complexity |
| -------- | --------- | ------ | ---------- |
Assumptions:
• hashtablehasmslots(tableofmlists)
| • containsnelements⇒loadfactor: |     |     | α= n |
| ------------------------------- | --- | --- | ---- |
m
• h(k)canbecomputedinO(1)forallk
• allvaluesofhareequallylikelytooccur
Searchcomplexity:
• onaverage,thelistcorrespondingtotherequestedkeywillhave
αelements
•
| unsuccessfulsearch: |                | comparetherequestedkeywithallobjects |     |
| ------------------- | -------------- | ------------------------------------ | --- |
| inthelist,i.e.      | O(α)operations |                                      |     |
| • successfulsearch: |                | requestedkeylastinthelist;           |     |
⇒alsoO(α)operations

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 11
| Chaining | – Average | Search | Complexity |
| -------- | --------- | ------ | ---------- |
Assumptions:
• hashtablehasmslots(tableofmlists)
| • containsnelements⇒loadfactor: |     |     | α= n |
| ------------------------------- | --- | --- | ---- |
m
• h(k)canbecomputedinO(1)forallk
• allvaluesofhareequallylikelytooccur
Searchcomplexity:
• onaverage,thelistcorrespondingtotherequestedkeywillhave
αelements
•
| unsuccessfulsearch: |                | comparetherequestedkeywithallobjects |     |
| ------------------- | -------------- | ------------------------------------ | --- |
| inthelist,i.e.      | O(α)operations |                                      |     |
| • successfulsearch: |                | requestedkeylastinthelist;           |     |
⇒alsoO(α)operations
| Expected: | Averagecomplexity: | O(α)operations |     |
| --------- | ------------------ | -------------- | --- |

Simplestchoice: h=k mod m (maprimenumber)
• easytocompute;evendistributionifkeysevenlydistributed
• however: not“non-smooth”
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 12
Hash Functions
Agoodhashfunctionshould:
• satisfytheassumptionofevendistribution:
eachkeyisequallylikelytobehashedtoanyoftheslots:
(cid:88) 1
(P(key=k))= forall j =0,...,m−1
m
k:h(k)=j
• beeasytocompute
• be“non-smooth”: keysthatareclosetogethershouldnot
producehashvaluesthatareclosetogether(toavoidclustering)

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 12
Hash Functions
Agoodhashfunctionshould:
• satisfytheassumptionofevendistribution:
eachkeyisequallylikelytobehashedtoanyoftheslots:
| (cid:88) |     | 1   |
| -------- | --- | --- |
(P(key=k))= forall j =0,...,m−1
m
k:h(k)=j
•
beeasytocompute
•
| be“non-smooth”: | keysthatareclosetogethershouldnot |     |
| --------------- | --------------------------------- | --- |
producehashvaluesthatareclosetogether(toavoidclustering)
| Simplestchoice: | h=k mod | m (maprimenumber) |
| --------------- | ------- | ----------------- |
• easytocompute;evendistributionifkeysevenlydistributed
| • however: | not“non-smooth” |     |
| ---------- | --------------- | --- |

Remarks:
• valueofmuncritical;e.g. m=2p
• valueofγ needstobechosenwell
• inpractice: usefix-pointarithmetics
• non-integerkeys: useencodingtointegers
(ASCII,byteencoding,...)
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 13
| The Multiplication |     | Method | for Integer | Keys |
| ------------------ | --- | ------ | ----------- | ---- |
Two-stepmethod
| 1. multiplyk | byconstant0<γ |     | <1,andextractfractionalpartofkγ |     |
| ------------ | ------------- | --- | ------------------------------- | --- |
2. multiplybym,anduseintegerpartashashvalue:
|     | h(k):=(cid:98)m(γk |     | mod 1)(cid:99)=(cid:98)m(γk | −(cid:98)γk(cid:99))(cid:99) |
| --- | ------------------ | --- | --------------------------- | ---------------------------- |

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 13
| The Multiplication |     | Method | for Integer | Keys |
| ------------------ | --- | ------ | ----------- | ---- |
Two-stepmethod
| 1. multiplyk | byconstant0<γ |     | <1,andextractfractionalpartofkγ |     |
| ------------ | ------------- | --- | ------------------------------- | --- |
2. multiplybym,anduseintegerpartashashvalue:
|     | h(k):=(cid:98)m(γk |     | mod 1)(cid:99)=(cid:98)m(γk | −(cid:98)γk(cid:99))(cid:99) |
| --- | ------------------ | --- | --------------------------- | ---------------------------- |
Remarks:
| • valueofmuncritical;e.g. |                         |                       | m=2p |     |
| ------------------------- | ----------------------- | --------------------- | ---- | --- |
| • valueofγ                | needstobechosenwell     |                       |      |     |
| • inpractice:             | usefix-pointarithmetics |                       |      |     |
| • non-integerkeys:        |                         | useencodingtointegers |      |     |
(ASCII,byteencoding,...)

Hashfunction: generatessequenceofhashtableindices:
h: U×{0,...,m−1}→{0,...,m−1}
Generalapproach:
• storeobjectinthefirstemptyslotspecifiedbytheprobe
sequence
• emptyslotinthehashtableguaranteed,iftheprobesequence
h(k,0),h(k,1),...,h(k,m−1)isapermutationof0,1,...,m−1
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 14
Open Addressing
Definition
• nocontainers: tablecontainsobjects
• eachslotofthehashtableeithercontainsanobjectorNIL
• toresolvecollisions,morethanonepositionisallowedfora
specifickey

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 14
Open Addressing
Definition
• nocontainers: tablecontainsobjects
• eachslotofthehashtableeithercontainsanobjectorNIL
• toresolvecollisions,morethanonepositionisallowedfora
specifickey
Hashfunction: generatessequenceofhashtableindices:
h: U×{0,...,m−1}→{0,...,m−1}
Generalapproach:
• storeobjectinthefirstemptyslotspecifiedbytheprobe
sequence
• emptyslotinthehashtableguaranteed,iftheprobesequence
h(k,0),h(k,1),...,h(k,m−1)isapermutationof0,1,...,m−1

|     |     |     |     |     |     | OpenHashSearch(T:Table |              |         | , k:Integer)  | : Object      | {     |
| --- | --- | --- | --- | --- | --- | ---------------------- | ------------ | ------- | ------------- | ------------- | ----- |
|     |     |     |     |     |     |                        | i := 0;      |         |               |               |       |
|     |     |     |     |     |     |                        | while T[h(k, | i )] <> | NIL and       | i < m {       |       |
|     |     |     |     |     |     |                        | if k =       | T[h(k,  | i )].key then | return T[h(k, | i )]; |
|     |     |     |     |     |     |                        | i := i+1;    |         |               |               |       |
}
|     |     |     |     |     |     |     | return NIL; |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | ----------- | --- | --- | --- | --- |
}
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 15
| Open                   | Addressing | –           | Algorithms  |           |       |     |     |     |     |     |     |
| ---------------------- | ---------- | ----------- | ----------- | --------- | ----- | --- | --- | --- | --- | --- | --- |
| OpenHashInsert(T:Table |            |             | , x:Object) | : Integer | {     |     |     |     |     |     |     |
|                        | for i from | 0 to m−1    | do {        |           |       |     |     |     |     |     |     |
|                        | j :=       | h(x.key,    | i );        |           |       |     |     |     |     |     |     |
|                        | if T[      | j]=NIL then | { T[ j ] := | x; return | j ; } |     |     |     |     |     |     |
}
|     | cast error | ”hash | table overflow” |     |     |     |     |     |     |     |     |
| --- | ---------- | ----- | --------------- | --- | --- | --- | --- | --- | --- | --- | --- |
}

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 15
| Open                   | Addressing |          | – Algorithms |           |              |       |
| ---------------------- | ---------- | -------- | ------------ | --------- | ------------ | ----- |
| OpenHashInsert(T:Table |            |          | ,            | x:Object) | : Integer    | {     |
|                        | for i from | 0        | to m−1       | do {      |              |       |
|                        | j :=       | h(x.key, | i );         |           |              |       |
|                        | if T[      | j]=NIL   | then         | { T[ j ]  | := x; return | j ; } |
}
|     | cast error | ”hash | table | overflow” |     |     |
| --- | ---------- | ----- | ----- | --------- | --- | --- |
}
| OpenHashSearch(T:Table |              |          | ,         | k:Integer) | : Object      | {     |
| ---------------------- | ------------ | -------- | --------- | ---------- | ------------- | ----- |
|                        | i := 0;      |          |           |            |               |       |
|                        | while T[h(k, | i        | )] <> NIL | and        | i < m {       |       |
|                        | if k         | = T[h(k, | i )].key  | then       | return T[h(k, | i )]; |
|                        | i :=         | i+1;     |           |            |               |       |
}
|     | return NIL; |     |     |     |     |     |
| --- | ----------- | --- | --- | --- | --- | --- |
}

Mainproblem: clustering
• continuoussequencesofoccupiedslots(“clusters”)causelotsof
checksduringsearchingandinserting
• clusterstendtogrow,becauseallobjectsthatarehashedtoa
slotinsidetheclusterwillincreaseit
|     |     |     |     | • slight(butminor)improvement: | h(k,i):=(h | (k)+ci) mod | m   |
| --- | --- | --- | --- | ------------------------------ | ---------- | ----------- | --- |
0
Mainadvantage: simpleandfast
• easytoimplement
• cacheefficient!
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 16
| Open Addressing | –          | Linear Probing |     |     |     |     |     |
| --------------- | ---------- | -------------- | --- | --- | --- | --- | --- |
| Hashfunction:   | h(k,i):=(h | (k)+i) mod     | m   |     |     |     |     |
0
| • firstslottobecheckedisT[h |     | (k)] |     |     |     |     |     |
| --------------------------- | --- | ---- | --- | --- | --- | --- | --- |
0
| • secondprobeslotisT[h |     | (k)+1],thenT[h | (k)+2],etc. |     |     |     |     |
| ---------------------- | --- | -------------- | ----------- | --- | --- | --- | --- |
|                        |     | 0              | 0           |     |     |     |     |
• wraparoundtoT[0]afterT[m−1]hasbeenchecked

Mainadvantage: simpleandfast
• easytoimplement
• cacheefficient!
J.Kretinsky:FundamentalAlgorithms
| Chapter5:HashTables,Winter2021/22 |            |        |         |     | 16  |
| --------------------------------- | ---------- | ------ | ------- | --- | --- |
| Open Addressing                   | –          | Linear | Probing |     |     |
| Hashfunction:                     | h(k,i):=(h | (k)+i) | mod     | m   |     |
0
| • firstslottobecheckedisT[h |     |     | (k)] |     |     |
| --------------------------- | --- | --- | ---- | --- | --- |
0
| • secondprobeslotisT[h |     | (k)+1],thenT[h |     | (k)+2],etc. |     |
| ---------------------- | --- | -------------- | --- | ----------- | --- |
|                        |     | 0              |     | 0           |     |
• wraparoundtoT[0]afterT[m−1]hasbeenchecked
| Mainproblem: | clustering |     |     |     |     |
| ------------ | ---------- | --- | --- | --- | --- |
• continuoussequencesofoccupiedslots(“clusters”)causelotsof
checksduringsearchingandinserting
• clusterstendtogrow,becauseallobjectsthatarehashedtoa
slotinsidetheclusterwillincreaseit
| • slight(butminor)improvement: |     |     | h(k,i):=(h | (k)+ci) mod | m   |
| ------------------------------ | --- | --- | ---------- | ----------- | --- |
0

J.Kretinsky:FundamentalAlgorithms
| Chapter5:HashTables,Winter2021/22 |            |        |         |     | 16  |
| --------------------------------- | ---------- | ------ | ------- | --- | --- |
| Open Addressing                   | –          | Linear | Probing |     |     |
| Hashfunction:                     | h(k,i):=(h | (k)+i) | mod     | m   |     |
0
| • firstslottobecheckedisT[h |     |     | (k)] |     |     |
| --------------------------- | --- | --- | ---- | --- | --- |
0
| • secondprobeslotisT[h |     | (k)+1],thenT[h |     | (k)+2],etc. |     |
| ---------------------- | --- | -------------- | --- | ----------- | --- |
|                        |     | 0              |     | 0           |     |
• wraparoundtoT[0]afterT[m−1]hasbeenchecked
| Mainproblem: | clustering |     |     |     |     |
| ------------ | ---------- | --- | --- | --- | --- |
• continuoussequencesofoccupiedslots(“clusters”)causelotsof
checksduringsearchingandinserting
• clusterstendtogrow,becauseallobjectsthatarehashedtoa
slotinsidetheclusterwillincreaseit
| • slight(butminor)improvement: |     |     | h(k,i):=(h | (k)+ci) mod | m   |
| ------------------------------ | --- | --- | ---------- | ----------- | --- |
0
| Mainadvantage: | simpleandfast |     |     |     |     |
| -------------- | ------------- | --- | --- | --- | --- |
• easytoimplement
• cacheefficient!

|     |     |     |     | Idea: doublehashingh(k,i):=(h | (k)+i ·h (k))              | mod m |
| --- | --- | --- | --- | ----------------------------- | -------------------------- | ----- |
|     |     |     |     |                               | 0 1                        |       |
|     |     |     |     | • ifh isidenticalfortwokeys,h | willgeneratedifferentprobe |       |
|     |     |     |     | 0                             | 1                          |       |
sequences
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 17
| Open Addressing        | –          | Quadratic | Probing      |     |     |     |
| ---------------------- | ---------- | --------- | ------------ | --- | --- | --- |
| Hashfunction:          | h(k,i):=(h | (k)+c i   | +c i2) mod m |     |     |     |
|                        |            | 0 1       | 2            |     |     |     |
| • howtochoseconstantsc |            | andc      | ?            |     |     |     |
1 2
| • objectswithidenticalh |     | (k)stillhavethesamesequenceofhash |     |     |     |     |
| ----------------------- | --- | --------------------------------- | --- | --- | --- | --- |
0
values
(“secondaryclustering”)

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 17
| Open Addressing        | –          | Quadratic | Probing    |     |
| ---------------------- | ---------- | --------- | ---------- | --- |
| Hashfunction:          | h(k,i):=(h | (k)+c i   | +c i2) mod | m   |
|                        |            | 0 1       | 2          |     |
| • howtochoseconstantsc |            | andc      | ?          |     |
1 2
| • objectswithidenticalh |     | (k)stillhavethesamesequenceofhash |     |     |
| ----------------------- | --- | --------------------------------- | --- | --- |
0
values
(“secondaryclustering”)
| Idea: doublehashingh(k,i):=(h |     | (k)+i                      | ·h (k)) | mod m |
| ----------------------------- | --- | -------------------------- | ------- | ----- |
|                               |     | 0                          | 1       |       |
| • ifh isidenticalfortwokeys,h |     | willgeneratedifferentprobe |         |       |
| 0                             |     | 1                          |         |       |
sequences

•
rangeofh 0 : U →{0,...,m−1}(coverentiretable)
•
h 1 (k)mustneverbe0(noprobesequencegenerated)
•
h 1 (k)shouldbeprimetomforallk
→probesequencewilltryallslots
|     |     |     |     | • ifd isthegreatestcommondivisorofh |     | (k)andm,only | 1 ofthe |
| --- | --- | --- | --- | ----------------------------------- | --- | ------------ | ------- |
|     |     |     |     |                                     |     | 1            | d       |
hashslotswillbeprobed
Possiblechoices:
|     |     |     |     | • m=2M andleth generateoddnumbers,only |     |     |     |
| --- | --- | --- | --- | -------------------------------------- | --- | --- | --- |
1
|     |     |     |     | • maprimenumber,andh | : U →{1,...,m | }withm | <m  |
| --- | --- | --- | --- | -------------------- | ------------- | ------ | --- |
|     |     |     |     |                      | 1             | 1 1    |     |
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 18
| Open Addressing | –          | Double Hashing |       |     |     |     |     |
| --------------- | ---------- | -------------- | ----- | --- | --- | --- | --- |
|                 | h(k,i):=(h | (k)+i ·h (k))  | mod m |     |     |     |     |
|                 |            | 0 1            |       |     |     |     |     |
| Howtochooseh    | 0 andh     | 1 :            |       |     |     |     |     |

Possiblechoices:
|     |     |     |     |     | • m=2M andleth generateoddnumbers,only |     |     |
| --- | --- | --- | --- | --- | -------------------------------------- | --- | --- |
1
|     |     |     |     |     | • maprimenumber,andh | : U →{1,...,m | }withm <m |
| --- | --- | --- | --- | --- | -------------------- | ------------- | --------- |
|     |     |     |     |     |                      | 1             | 1 1       |
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 18
| Open Addressing | –          | Double Hashing |       |     |     |     |     |
| --------------- | ---------- | -------------- | ----- | --- | --- | --- | --- |
|                 | h(k,i):=(h | (k)+i ·h (k))  | mod m |     |     |     |     |
|                 |            | 0 1            |       |     |     |     |     |
| Howtochooseh    | 0 andh     | 1 :            |       |     |     |     |     |
•
| rangeofh | 0 : U →{0,...,m−1}(coverentiretable) |     |     |     |     |     |     |
| -------- | ------------------------------------ | --- | --- | --- | --- | --- | --- |
•
h 1 (k)mustneverbe0(noprobesequencegenerated)
•
h 1 (k)shouldbeprimetomforallk
→probesequencewilltryallslots
| • ifd isthegreatestcommondivisorofh |     |     | (k)andm,only | 1 ofthe |     |     |     |
| ----------------------------------- | --- | --- | ------------ | ------- | --- | --- | --- |
|                                     |     |     | 1            | d       |     |     |     |
hashslotswillbeprobed

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 18
| Open Addressing | –          | Double | Hashing |       |     |
| --------------- | ---------- | ------ | ------- | ----- | --- |
|                 | h(k,i):=(h | (k)+i  | ·h (k)) | mod m |     |
|                 |            | 0      | 1       |       |     |
| Howtochooseh    | 0 andh     | 1 :    |         |       |     |
•
| rangeofh | 0 : U →{0,...,m−1}(coverentiretable) |     |     |     |     |
| -------- | ------------------------------------ | --- | --- | --- | --- |
•
h 1 (k)mustneverbe0(noprobesequencegenerated)
•
h 1 (k)shouldbeprimetomforallk
→probesequencewilltryallslots
| • ifd isthegreatestcommondivisorofh |     |     |     | (k)andm,only | 1 ofthe |
| ----------------------------------- | --- | --- | --- | ------------ | ------- |
|                                     |     |     |     | 1            | d       |
hashslotswillbeprobed
Possiblechoices:
| • m=2M | andleth generateoddnumbers,only |     |     |     |     |
| ------ | ------------------------------- | --- | --- | --- | --- |
1
| • maprimenumber,andh |     | : U | →{1,...,m | }withm | <m  |
| -------------------- | --- | --- | --------- | ------ | --- |
|                      |     | 1   |           | 1 1    |     |

• searchentry,removeit
• doesnotwork:
• insert3,7,8havingsamehash-value,thendelete7
• howtofind8?
⇒ donotdelete,justmarkasdeleted
Nextproblem:
• searchingstopsiffirstemptyentryfound
• aftermanydeletions: lotsofunnecessarycomparisons!
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 19
Open Addressing – Deletion
Problemremaining: howtodelete?

⇒ donotdelete,justmarkasdeleted
Nextproblem:
• searchingstopsiffirstemptyentryfound
• aftermanydeletions: lotsofunnecessarycomparisons!
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 19
Open Addressing – Deletion
Problemremaining: howtodelete?
• searchentry,removeit
• doesnotwork:
• insert3,7,8havingsamehash-value,thendelete7
• howtofind8?

Nextproblem:
• searchingstopsiffirstemptyentryfound
• aftermanydeletions: lotsofunnecessarycomparisons!
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 19
Open Addressing – Deletion
Problemremaining: howtodelete?
• searchentry,removeit
• doesnotwork:
• insert3,7,8havingsamehash-value,thendelete7
• howtofind8?
⇒ donotdelete,justmarkasdeleted

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 19
Open Addressing – Deletion
Problemremaining: howtodelete?
• searchentry,removeit
• doesnotwork:
• insert3,7,8havingsamehash-value,thendelete7
• howtofind8?
⇒ donotdelete,justmarkasdeleted
Nextproblem:
• searchingstopsiffirstemptyentryfound
• aftermanydeletions: lotsofunnecessarycomparisons!

Inserting
• insertingefficient,buttoomanyinserts⇒notenoughspace
⇒ ifratioαtoobig,newconstructionoftablewithlargersize
Still...
• searchingfasterthanO(logn)possible
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 20
Open Addressing – Deletion (2)
Deletiongeneralproblemforopenhashing
• only“solution”: newconstructionoftableaftersomedeletions
• hashtablesthereforecommonlydon’tsupportdeletion

Still...
• searchingfasterthanO(logn)possible
J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 20
Open Addressing – Deletion (2)
Deletiongeneralproblemforopenhashing
• only“solution”: newconstructionoftableaftersomedeletions
• hashtablesthereforecommonlydon’tsupportdeletion
Inserting
• insertingefficient,buttoomanyinserts⇒notenoughspace
⇒ ifratioαtoobig,newconstructionoftablewithlargersize

J.Kretinsky:FundamentalAlgorithms
Chapter5:HashTables,Winter2021/22 20
Open Addressing – Deletion (2)
Deletiongeneralproblemforopenhashing
• only“solution”: newconstructionoftableaftersomedeletions
• hashtablesthereforecommonlydon’tsupportdeletion
Inserting
• insertingefficient,buttoomanyinserts⇒notenoughspace
⇒ ifratioαtoobig,newconstructionoftablewithlargersize
Still...
• searchingfasterthanO(logn)possible