# 2D-Sandbox-Game
 
## Define the Problem and Proposed Solution
I love playing with AI, and that made me want to have a versatile and resourceful playground.
To achieve that, I needed a game engine with full access to everything in the game.
Therefore, I made a 2D sandbox game engine from scratch with Java.

I could've used existing libraries or game engines like Unity, Unreal, Godot, etc.
However, I did not - hoping to get more in-depth insight into game development.

## Design

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

<img src='https://raw.githubusercontent.com/Juhyung8371/2D-Sandbox-Game/main/readme_images/association.png' height="150">

