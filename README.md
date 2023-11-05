# 2D Sandbox Game Engine from scratch
 
## Define the Problem and Proposed Solution
I love playing with AI, and that made me want to have a versatile and resourceful playground.
To achieve that, I needed a game engine with full access to everything in the game.
Therefore, I made a 2D sandbox game engine from scratch with Java.

I could've used existing libraries or game engines like Unity, Unreal, Godot, etc.
However, I did not - hoping to get more in-depth insight into game development by starting from the very beginning.

## Object-Oriented-Programming Design

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/design.png'>

Above is the data structure image. I utilized OOP techniques as much as possible 
for better organization.  

### Abstraction and Inheritance:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/inheritance.png' align='right' height=250>

Abstraction refers to the act of representing the essential features 
without specifying their detailed properties. Abstraction is often 
used to create the inheritance of classes for better organization of classes. 
Inheritance is a process of acquiring one’s structure and data by deriving 
a new (daughter) class from the original (mother) class. Additional features 
can be added to the daughter class without modifying the mother class. 

### Encapsulation:

Encapsulation is the process of hiding one’s data and giving only a limited level of access to protect the data from unwanted access. So, the class provides two methods called mutator and accessor to provide the other classes the indirect and limited access to its data.

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/encapsulation.png' height=100>

For example, “inventory” is encapsulated with a “private” access modifier. So, an accessor method, "getInventory", allows other classes to see the inventory data. However, I didn’t make a mutator method to prevent other classes from modifying the inventory data. And this is what makes encapsulation such a strong programming technique.

### Polymorphism:

Polymorphism is the practice of allowing classes to behave differently to provide fluent modification of data. Polymorphism is extensively used alongside inheritance since data morphing only works towards the more abstract side. The below image is an example of polymorphism. Even though the “addEntity” method only receives the Entity class as its parameter, it works for all the classes that extend the Entity class. Polymorphism is another feature that makes the inheritance feature of the OOP style of Java so convenient.

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/polymorphism.png' height="200">

### Association

Association is the method to establish the connections between classes through another class. When one class has an object referencing the other class, it’s called a “has-a” relationship. For example, the Inventory class is associated with the Item class and UIObject class. By establishing the connection between Items and the UIObjects to visualize the Items, the Inventory class is created. Therefore, the strength of association is the ability to combine many classes to create a very useful and complex class with many properties. 

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/association.png' height=100>

## Other Implementations Highlight

### Artificial Intelligence

AI in this game works by running the task with the highest priority from the list. In a nutshell, it's a collection of if-else.

Also, there are two types of tasks in my game: active and passive tasks. They can run simultaneously, however, active tasks have much higher priority than passive tasks. 

For example, there is an entity called a wolf. A wolf wanders around until it finds a target. Then the wolf chases the target and attacks it. This behavior can be portrayed in this manner:

Active Tasks:
1. Attack (if the target is close)
2. Find a target (if the target is not set)
3. Find a path to the target (if the target exists)
4. Move towards the target (if there is a valid path to the target)

Passive Tasks:
1. Wander (if there is no target)

#### A* Pathfinding

A* algorithm is one of the best and most popular path-finding and graph traversal techniques for its simplicity and power. It finds the best path by minimizing the distance from the current node to the starting point and the distance from the current node to the goal. The heuristic is like the following:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/a-star.png' width=350>

And this is how it worked in-game:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/pathfinding.gif' width=400>

### Infinite Map and Chunking

I added an infinite map feature to add more flexibility to the game and a chunking feature to improve the map loading performance. 

An infinite map can be accomplished by dividing the map into smaller chunks – and I call this “Chunking.” So, the idea behind chunking is to only deal with the chunks near the player. 

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/chunk.png' height="250">

In the above image, each cell is a chunk, which contains data about that part of the map, like tiles, entities, etc. The red box represents the screen visible to the user, the green circles represent the loaded chunks, and the red crossed-out circles are the chunks too far away that we don’t bother updating. If the player moves toward the east, the unloaded chunks on the far east will load, and the chunks on the far west will save their data and unload. 

### Random Map Generation 

I implemented a random map generation feature using noise (Simplex Noise). Noise is a random group of numbers determined by its frequency and amplitude. However, it is a cloud-like shape generally, and this behavior will make the climate transition more natural. A noise typically looks like the image below on the left.

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/noise.png' height="150"> <img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/climate_map.png' height="150">

The magic happens when we apply the idea of climate components to this seemingly meaningless cloud of noises. For example, I can make noises for elevation and moisture and use them to determine the climate (see the moisture vs elevation graph above on the right). For example, if the elevation is very low, then it will be a lake since water gathers in the low elevated area geographically. And if elevation is normal but moisture is high, then the climate will be rainforest. Check the example map below:

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/map.png' height="200">

### Graphical User Interface

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/ui.gif' width=400>

GUI is one of the easiest ways for users to interact with computers. I implemented various GUI components such as buttons, text edit box, scrollbar, label, etc. 

OOP techniques like inheritance made the implementation easier. For example, let’s say I made a button. Then, a text edit box is a button with mutable text, and a label is a button with a text or image that can’t be pressed.

## Discussion

I successfully created a 2D sandbox game engine with desired features like maps, entities, artificial intelligence, graphical user interface, etc. 

Using various OOP techniques, I made this project modular, scalable, and easy to maintain. For instance, if I want to add a new entity, a zombie for instance, I can simply extend the HostileEntity class and add some pre-built AIs like chase and attack.


