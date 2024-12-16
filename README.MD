# LibGDX Tutorial

### Project Goals
1. Learn about development with the libGDX library
2. Learn how to deplpoy projects to github pages
3. Create the 

### Example Project

[Vist a completed version of the project here](https://mrbrownbcc.github.io/libgdx-tutorial-SOLUTION/)

### Setup Steps:
1. To test, you will need to forward port 6080, similar to other projects. 
2. Click the link below and keep open in another tab. It contains the initial instructions

### Additional Resources:
[https://libgdx.com/wiki/](libgdx wiki)
[https://www.w3schools.com/java/java_arraylist.asp](Java Arraylists)

### Core requirements
1. Make a functional version of the snake game.


### Programming Instructions:

#### Checkpoint 1 - Due 12/17
*all editing should take place within core/src/..../Main*
1. Fix movement - edit the keyDown() method currently the snake can move in any direction at any time. Prevent the snake from moving back on itself. 
2. checkApple()


#### Checkpoint 2 - Due 12/19

1. Implement the onSnake() method. Should return true if the location is a part of the snake. 
2. Make apple positioning random - modify the placeApple() method following the instructions in code. Apple position should be random. Use your onSnake() method to help you. 
3. Implement the checkDeath() method. This method should set gameOver to true iif the snake dies. 

#### Bonus 
*Not required, but could be cool to add!*
1. Add images for the apple and the head of the snake
2. Create a new way to plat the game! You could add fixed obstacles, multiple apples, etc.!


### Testing:
To test your code, interact with the GUI you create! 

Navigate to lwjgl3/src/main/java/bcc/snakegame/lwjgl3/Lwjgl3Launcher.java to find the run button. 

To test 'desktop version', use the following terminal command: 
```
./gradlew lwjgl3:run
```
NOTE - to do this you must have turned on port forwarding of port 6080


### Deploying
Now you can share your project with the world!
You will also need to complete this step before submission. 
Run the following two commands in the terminal:

NOTE - build may take 1-3 minutes. 
```
npm run build
npi i gh-pages
npm run deploy 
```
*NOTE - the first time you deploy, it may take up to 10 minutes for the site to exist.* 
You now have a github pages available!
To find the link for your project- 
1. Go to github.com to double check your username and repo name. 
2. The URL for your project is:

`https://<your-username>.github.io/<repository-name>/`

### Submission

For each checkpoint, deploy your projeect, then submit the link to your project on canvas. 
To save your work, you can still use:
```
git add . 
git commit -m "some message"
git push
```