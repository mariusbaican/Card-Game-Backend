# GwentStone Lite

### Introduction

My goal for this project was to create an **intuitive** and **easy to use** ecosystem.  
To achieve this, I have **modularised** every aspect of the game, so that adding new  
cards, commands etc. is a method definition away. I will now walk you through  
every class within this architecture to better illustrate my design choices:

#### 1. Game

The **Game** class utilises the **Singleton** design pattern, as it stores most of the  
data of the game itself, so it needs to be **globally available**. This class possesses  
all the methods required to reset, start, run and end a game, but *doesn't handle  
commands itself*. Those get passed onto the class I am going to talk about next.  

#### 2. ActionHandler

The **ActionHandler's** sole purpose is taking input commands, deciding what **behavior**  
they require to be completed and utilises the elements of the game (Player, Board,  
Cards) to fulfill said commands, in addition to handling the turns and rounds. It  
also utilises the **Singleton** design pattern, as we only need one object to handle  
commands to avoid possible conflicts. The structure of the action handling method  
allows for continued expansion of the available commands in the game. You just have to  
create another case in the switch and call the method that fulfills your new command.

#### 3. Player

The **players** are arguably the **biggest part of a game**. They make all the choices that  
change the outcome of a match, going through the incredible amount of possible  
scenarios. This is exactly why my project was *centered around the Player class*. At the  
beginning, I mentioned my architecture was **intuitive**, thus most of the actions within  
the game are present in the Player class. Who places cards? **The player**. Who chooses  
which cards to attack? **The player**. Who chooses which abilities to use and where to use  
them? **The player**. So this is exactly why most of the actions are defined within the player.

#### 4. Board

The **Board** is also utilising the **Singleton** design pattern, as it stores the current state  
of the game (a unique game, thus a unique Board instance) which needs to be **globally  
available**. The class itself doesn't possess much authority compared to other parts of the  
project, but it is very useful for storing and accessing cards at specific coordinates.

#### 5. Deck

The **Deck** class could be considered unnecessary, as it could've worked the same way the  
player's hand works, an array of cards. The main reason for utilising a separate class  
was **readability** and the implementation of the shuffle method internally.

#### 6. Card

The Card class exists just because of the abundance of similarities between the MinionCards  
and the HeroCards. It is used to store the common traits of the two subclasses. It is probably  
heavily unnecessary, as there is no sight of **upcasting** or **downcasting** in the project itself,  
but I approached it as a way of **learning good practices**, thus the Card class was created.  

#### 7. MinionCard

This is a subclass of Card, **inheriting** most of its features, but adding the attackDamage  
and minionType fields. The minionType enum is used to determine which row cards should  
be placed on: Regular and Druid minions have to be placed on the **back row**, while Tanks  
and Legendary minions have to be placed on the **front row**.

#### 8. HeroCard

This is also a subclass of Card, mostly implemented for **readability**, to differentiate  
the Hero from the Minions and vice versa.

#### 9. Ability

Now this is the part I was proud of when I started making this project, however it came  
back to bite at some point. I have learned about **functional interfaces** a while back  
for another project of mine and I thought: hmm, how cool would it be to internally store  
the ability within each card. The main **issue** I encountered was, when trying to copy a  
card, copying its ability would keep it attached to the original card instance. This  
resulted in the original card being changed when the ability was called, not the copy.  
The one thing I regret about this approach is not creating a **global HashMap** that stored  
the card names as keys and the abilities as values, to make ability distribution much  
simpler. The main **problem** with doing this is the Miraj card, that requires the attacker's  
health, making the globalisation a little more difficult. I only came up with this idea  
after I had finished the project and reworking it wasn't really an option because of other  
college assignments.

