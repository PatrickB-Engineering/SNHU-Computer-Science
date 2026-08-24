# CS 300: Data Structures and Algorithms

Two artifacts from CS 300. Project One is the runtime and memory analysis for the three structures ABCU could have used. Project Two is the working program, built on the one that analysis picked.

**Byrne-ProjectOne.docx** Pseudocode and runtime analysis for a vector, a hash table, and a binary search tree. Big O tables for the load, search, and print paths, plus the recommendation.

**Byrne-Project_Two.cpp** The advising program. Loads the course catalog from a comma separated file, prints it in alphanumeric order, and looks up a single course with its prerequisites.

## Reflection

### What was the problem you were solving in the projects for this course?

ABCU's advising department had a course catalog sitting in a text file and no way to read it. Advisors needed two things out of it: the full catalog printed in alphanumeric order, and a single course pulled up with its prerequisites when a student asks about it. Project One was picking the data structure. Project Two was building it.

The catch is that "which structure" isn't answerable in the abstract. Eight courses. Everything is fast at eight courses. So the actual problem was figuring out what the requirement was really asking for, which turned out to be sorted output, not fast lookup.

### How did you approach the problem? Consider why data structures are important to understand.

Wrote the same program three times in pseudocode first. Vector, hash table, BST. Then costed out every line of the load function and put the totals next to each other.

That's where it got interesting. The hash table wins search, O(1) against O(log n), and it isn't close. It also has no ordering at all, by design, because scattering keys is the entire point of a hash. So printing the catalog means dumping everything back out and sorting it, every single time. The tree keeps the order as a side effect of storing the data. The sort line simply does not exist in the tree version, and code that isn't there can't break.

Which is the lesson. You don't pick a structure off a benchmark, you pick the one shaped like the way the data gets read back out. Most of my day job is data collection and population management for the government and that shows up constantly. Fast is easy. Shaped right is the hard part.

### How did you overcome any roadblocks you encountered?

Two worth writing down.

The first was that a plain BST doesn't balance itself, and a nearly sorted course file inserted in file order builds a linked list wearing pointers. Eight levels deep, exactly one node with a left child, search degraded to O(n). The provided CSV happened to arrive shuffled, so it would have worked fine and I never would have known why. Fixed it by reading every line first, sorting, then inserting the middle course of each range as that subtree's root. Minimum height for any input order, including the ugly one.

The second was dumber and cost more time. Visual Studio does not run out of the folder you think it does. Dragging the CSV into Solution Explorer doesn't copy the file anywhere useful. It has to physically live next to the .vcxproj. Spent a while convinced the loader was broken when the file was just somewhere else. Related, the path prompt now strips quotes, because Windows "copy as path" hands you a quoted string and the file stream does not care about your intent.

### How has your work on this project expanded your approach to designing software and developing programs?

The interface came first and the container came second, and I didn't plan it that way. All three pseudocode versions have the same three functions: loadCourses, searchCourse, printAllCourses. Swapping the vector for the hash table for the tree barely touched any of them. Every bit of container knowledge stays behind Insert, Search, and GetAll.

Once that was visible, the tree implementation wasn't really a rewrite. It was filling in a shape that already existed. I prototype and tinker with robotics at home and the same rule holds there. Get the interface right and you can swap the guts twice before dinner.

### How has your work on this project evolved the way you write programs that are maintainable, readable, and adaptable?

Mostly it changed how I handle bad input. An early version bailed on the first malformed line. The loader now sets a flag and keeps going, so one run tells you everything wrong with the file instead of making you fix and rerun eight times. It also runs two passes, because a prerequisite can point at a course further down the file, so nothing can be validated until every line has been read.

A few smaller things stuck too. A failed load doesn't wipe a good one, so a typo at the prompt doesn't cost the user the catalog they already had loaded. Menu options 2 and 3 check that data is actually in memory instead of printing nothing and letting you assume the file was bad. Comments explain why and not what. The insertion order note at the top of the source file is there specifically because future me is going to open it, see the loader sort a list before inserting it into a sorted structure, and think that looks insane. It isn't, and the comment says so.
