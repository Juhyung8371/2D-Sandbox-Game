# 2D-Sandbox-Game
 
## Define the Problem and Proposed Solution
I love playing with AI, and that made me want to have a versatile and resourceful playground.
To achieve that, I needed a game engine with full access to everything in the game.
Therefore, I made a 2D sandbox game engine from scratch with Java.

I could've used existing libraries or game engines like Unity, Unreal, Godot, etc.
However, I did not - hoping to get more in-depth insight into game development.

## Object-Oriented-Programming Design

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/design.png'>

Above is the data structure image. I utilized OOP techniques as much as possible 
for better organization.  

### Abstraction and Inheritance:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/inheritance.png' align='right' height=250>

Abstraction refers to the act of representing the essential features 
without specifying the detail properties of it. Abstraction is often 
used to create the inheritance of classes for better organization of classes. 
Inheritance is a process of acquiring one’s structure and data by deriving 
a new (daughter) class from the original (mother) class. Additional features 
can be added to daughter class without modifying the mother class. 


### Encapsulation:

Encapsulation is the process of hiding one’s data and giving only limited level of access to protect the data from unwanted access. So, the class provides two methods called mutator and accessor to provide the other classes the indirect and limited access to its data.

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/encapsulation.png' height=100>

For example, “inventory” is encapsulated with a “private” access modifier. So, an accessor method “getInventory“ allows other classes to see the inventory data. However, I didn’t make a mutator method to prevent other classes to modify the inventory data. And this is what makes encapsulation such a strong programming technique.

### Polymorphism:

Polymorphism is the practice to allow classes to behave differently to provide fluent modification of data. Polymorphism is extensively used alongside with inheritance since data morphing only works towards the more abstract side. Below image is an example of polymorphism. Even though “addEntity” method only receives Entity class as its parameter, it works for all the classes that extend Entity class. Polymorphism is another feature that makes inheritance feature of OOP style of Java so convenient.

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/polymorphism.png' height="200">

### Association

Association is the method to establish the connections between classes through another class. When one class has an object referencing to the other class it’s called “has-a” relationship. For example, Inventory class is associated with Item class and UIObject class. By establishing the connection between Items and the UIObjects to visualize the Items, the Inventory class is created. Therefore, the strength of association is the ability to combine many classes to create a very useful and complex class with many properties. 

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/association.png' height=100>

## Other Implementations Highlight

### Artificial Intelligence

AI in this game works by running the task with the highest priority from the list. In a nutshell, it's a collection of if-else.

Also, there are two types of tasks in my game: active and passive tasks. They can run simulataneously, however, active tasks have much higher priority than passive tasks. 

For example, there is an entity called wolf. A wolf wanders around until it finds a target. Then the wolf chases the target and attacks it. This behavior can be portrayed in this manner:

Active Tasks:
1. Attack (if the target is close)
2. Find target (if the target is not set)
3. Find a path to the target (if the target exists)
4. Move towards the target (if there is a valid path to the target)

Passive Tasks:
1. Wander (if there is no target)

#### A* Pathfinding

A* algorithm is one of the best and popular path-finding and graph traversals techniques for its simplicity and power.

I find the best path by minimizing the distance from the current node to the starting point and distance from the current node to the goal. The heuristic is like the following:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/a-star.png' width=350>

And this is how it worked in-game:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/pathfinding.gif' width=400>

### Infinite Map and Chunking

I added an infinite map feature to add more flexibility to the game, and added chunking feature to improve the map loading performance. 

An infinite map can be accomplished by dividing the map into smaller chunks – and I call this “Chunking”. So, the idea behind chunking is to only deal with the chunks near the player. 

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/chunk.png' height="250">

In the above image, each cell is a chunk, which contains many data about that part of the map like tiles, entities, etc. The red box represents the screen visible to the user, the green circles represent the loaded chunks and red crossed out circles are the chunks too far away that we don’t bother updating. If the player moves toward east, then the unloaded chunks on the far east will load, and the chunks on the far west will save its data and unload. 

### Random Map Generation 

I implemented a random map generation feature using noise (Simplex Noise). Noise is a random group of numbers determined by its frequency and amplitude. However, it is cloude-like shape generally, and this behavior will make the climate transition more natural. A noise typically looks like the image below on the left.

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/noise.png' height="150"> <img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/climate_map.png' height="150">

The magic happens when we apply the idea of climate components to this seemingly meaningless cloud of noises. For example, I can make noises for elevation and moisture, and use them to determine the climate (see the mositure vs elevation graph above on the right). For example, if elevation is very low, then it will be lake, since water gathers in the low elevated area geographically. And if elevation is normal but moisture is high, then the climate will be rainforest. Check the example map below:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/map.png' height="200">

