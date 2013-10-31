Trigrams process

Started at: 20131030:12:48 GMT

OK, it's a tokeniser, with a map. The map maps token tuples onto tokens.

(A, B) -> C

But, one tuple (A, B) may map onto any number of a number of tokens, so it's

(A, B) -> one_of( C, D, E...)

From the problem specification: 'What do we do with punctuation? Paragraphs?'

Punctuation is just tokens and should follow the same rules as other tokens

i.e. 'I CAME, I SAW, I CONQUERED,' should be treated as 'I CAME COMMA I SAW COMMA I CONQUERED'

Paragraphs... Since this is nonsense, a paragraph won't contain a logical unit of narrative vor argument, since there can be no logical units. It is effectively a lorem ipsum text. So paragraphs should be generated at random at the ends of sentences, with a roughly 20% probability (i.e. on average five sentences to a paragraph). A 'sentence' ends just exactly when we emit a period.

Is it really as simple as that? Seriously?

OK, this is (1) an ideal Prolog problem, and (2) something which it would be delightful to tackle in Lisp or Clojure (or indeed use that neat little Prolog-in-Clojure I saw somewhere recently) but I'm asked to do it in Java, so Java it shall (for now) be.

OK, interesting little problemette:

iterating over tokens on the input, I need to hold open N uncompleted tuples, where N is the number of tokens in a tuple. H'mmmm...

Oh, bother. A stream tokenizer doesn't return simple tokens, it tries to be clever.

Question: does it matter if, against a word, we map multiple identical tuples? Answer: no, it doesn't - it just slightly increases the probability of that sequence being selected on output.

Damn! and actually, this is why this really is a Lisp problem! the data structure we want is not

I -> [[I, CAME, COMMA],[I, CAME, TO],[I, SAW, COMMA],[I, SAW, HER]]

it would be better to have rule trees

I -> [I, [[CAME, [[COMMA], [TO]]], [SAW [[COMMA],[HER]]]]

Because then we could just walk the rule tree on output... Wait! No, dammit, it's even simpler than that. All we need to store is successors. Because we walk the succession hierarchy as deep as we need on output... Jings, that's neat.

But no, in won't work - because then I don't know where I've come from. Rule trees it must be. 

Right. Backtracking required? My hunch is yes, because, suppose the input comprises the sequences:

A B B
A B C
B C A
B C D
C D A
C A B
D B C

Then we start by emitting A B C D, we're stuck, because we have no rule with the left-hand side 'D A'. So we have to roll back the B C D step and choose B C A instead. Which means, generation must be a recursive function.

OK, this would be SO MUCH easier in a functional language like Clojure... the problem is in the backtracking. I had thought it wouldn't matter not marking which branches I'd explored because I could just explore branches at random, but that doesn't work because either I end up getting stuck in an infinite loop retrying branches I've explored before, or else I could fail when there is a valid solution.

OK, the problem only specified that the tuple length should be two. I'm trying to build the general case. But the special case of tuple length = 2 would be easier to solve. Should I admit defeat, or shall I be arrogant? It would be more elegant to solve the general case.

Argh, power cut. This is not what I need. Copied everything onto laptop but Git repository is corrupt. Never mind, don't have time to fix it. Also, don't have Java 7 on laptop so no try-with-resources... bother.

Also don't have Netbeans on laptop and while Eclipse is handling the Netbeans project mostly fine, I can't do 'ant jar' because of stuff in the netbeans project file. Argghh! Hnd-hacked project.properties and now it works...

Now overtired and making mistakes - a situation made worse by broken git. I don't think I'm going through the whole sequence as I intend; also, something is scrambling glanceBack - and it's something I've broken recently, which makes it worse. Taking a break.

And, dammit! Although I've specified that line feed and carriage return are whitespace, the parser is still treating them as special. Oh, no, I beg it's pardon, it isn't. However, although I haven't specified 'period' as a word character, it is being treated as one. Bother.

Having said all that, the parse tree is looking very good. I'm extracting the rules well. It's applying them that's proving hard.

Right, StreamTokenizer was a poor choice. It seems to be legacy. But schlurping the whole text into a string and then using StringTokenizer or String.split() looks a bad choice too, since I don't know how long the string is. H'mmmm... This is a problem I kind of don't need, since it's not key to the project, and the .endsWith(PERIOD) hack works around it. Concentate on output.

New Git repository, and this time pushed out to Goldsmith, so that local power problems shouldn't affect it...

Decluttered the TextGenerator class by moving the whole read stage into two new classes, Generator and Tokeniser. More declutter needed.

Right, fully decluttered, All bugs(!) are in new class Composer. I have a little Liszt...

Parsing word tuples for n > 2 working sweetly. That is not the problem!

Major refactoring and cleanup of the compose stage...

ye! Utuvienyes

