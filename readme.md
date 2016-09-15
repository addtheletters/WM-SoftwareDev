# Software Development Course Projects
These are projects completed for the Software Development course. These assignments were handed out as existing codebases that we were tasked with completing or extending. 
As a result there is some messy code and bizzare inconsistencies that I'd have loved to avoid if I were writing everything from scratch. Ah well. I feel that my own code is pretty readable in comparison to what we were given to work with. ʕノ•ᴥ•ʔノ ︵ ┻━┻

Each project features the actual source as well as fairly high-coverage unit testing for additions to the existing codebase. 
Javadocs have been generated for new and modified code.
Also included in this repo is SVN data. During the course's progress, SVN was used for version control and project submission for grading.

## SlidingPuzzle
Java app. Name is fairly self-explanatory. Interesting algorithms implemented there include BFS for dragging tiles around and A* / A-Star pathfinding to try and find good solutions.

## Maze
Java app. Generates a maze; allows the user to try and solve manually, or solve automatically with a choice of algorithms. Shows a minimap with walls and an ideal solution path.

Maze-generating algorithms are:
- [DFS](https://en.wikipedia.org/wiki/Depth-first_search): Default, provided in project files. Uses a depth first search to fill in walls.
- [Prim](https://en.wikipedia.org/wiki/Prim%27s_algorithm): Uses a heavily simplified version of Prim's algorithm to connect all maze cells together.
- [Kruskal](https://en.wikipedia.org/wiki/Kruskal%27s_algorithm): Uses a heavily simplified version of Kruskal's algorithm to do the same.

## AMazeByBenZhang
Android app. Name was not my choice. Basically a port of all features from the standard Java Maze. Requires messing with Android Graphics and a mess of threading.
