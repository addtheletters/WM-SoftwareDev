# Project 4 CRC cards
##### Software Development Summer 2016
##### Ben Zhang

<hr>

### AMazeActivity
#### Responsibilities
- Register UI controls for starting activity
- Start GeneratingActivity with parameters specified by UI

#### Collaborators
- GeneratingActivity is started, given parameters
- DebugSpinnerListener is given to UI spinners for debug purposes
- anonymous click listeners for button function


### AMazeActivity.DebugSpinnerListener
#### Responsibilities
- provide action to be performed for debug when spinners are manipulated

#### Collaborators
- AMazeActivity, used by

<hr>

### GeneratingActivity
#### Responsibilities
- Generate or load a maze given parameters from intent
- Update UI to reflect loading progress
- Start PlayActivity when ready

#### Collaborators
- AMazeActivity, started by
- PlayActivity is started, passed along driver info
- SimulatedBuildProgress is used to pretend generation takes time
- PlaceholderMaze temporarily holds maze info and stands-in for actual maze data


### GeneratingActivity.SimulatedBuildProgress
#### Responsibilities
- Simulate a build process happening asynchronously which takes time

#### Collaborators
- GeneratingActivity, called by


### GeneratingActivity.PlaceholderMaze
#### Responsibilities
- Stand-in for real maze
- Hold maze generation parameters

#### Collaborators
- GeneratingActivity holds instance
- PlayActivity holds instance

<hr>

### PlayActivity
#### Responsibilities
- Display maze and overlays
- Function of UI controls to move around
- Make drivers run and move to finish activity if maze is finished

#### Collaborators
- GeneratingActivity, started by
- PlaceholderMaze is used to represent maze data temporarily
- PlaceholderDriver is used to represent driver temporarily
- DebugButtonListener is given to UI buttons for debug purposes
- FinishActivity is started, given results of maze navigation


### PlayActivity.PlaceholderDriver
#### Responsibilities
- Stand-in for real driver
- Provide dummy data for testing play and finish activities

#### Collaborators
- PlayActivity holds instance

<hr>

### FinishActivity
#### Responsibilities
- Show results of maze completion
- Show any details about how maze was completed

#### Collaborators
- PlayActivity, started by
