# Proiect GlobalWaves  - Etapa 1

<div align="center"><img src="https://tenor.com/view/listening-to-music-spongebob-gif-8009182.gif" width="300px"></div>

#### Assignment Link: [https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa1](https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa1)


## Skel Structure

* src/
  * checker/ - checker files
  * fileio/ - contains classes used to read data from the json files
  * main/
      * Main - the Main class runs the checker on your implementation. Add the entry point to your implementation in it. Run Main to test your implementation from the IDE or from command line.
      * Test - run the main method from Test class with the name of the input file from the command line and the result will be written
        to the out.txt file. Thus, you can compare this result with ref.
* input/ - contains the tests and library in JSON format
* ref/ - contains all reference output for the tests in JSON format

## Tests:
1. test01_searchBar_songs_podcasts - 4p
2. test02_playPause_song - 4p
3. test03_like_create_addRemove - 4p
4. test04_like_create_addRemove_error - 4p
5. test05_playPause_playlist_podcast - 4p
6. test06_playPause_error -4p
7. test07_repeat - 4p
8. test08_repeat_error - 4p
9. test09_shuffle - 4p
10. test10_shuffle_error - 4p
11. test11_next_prev_forward_backward - 4p
12. test12_next_prev_forward_backward_error - 4p
13. test13_searchPlaylist_follow ---  (+4)
14. test14_searchPlaylist_follow_error - 4p
15. test15_statistics - 4p
16. test16_complex - 10p
17. test17_complex - 10p

<div align="center"><img src="https://tenor.com/view/homework-time-gif-24854817.gif" width="500px"></div>

# Implementation

## Structure

* input/
  * CommandInput - class that is used to read the command files
  * Filters - saves the filters used for search
* output/
  * I have created classes that represent the output messages
  * and every class contains the 'toObjectNode' method that transforms the object into ObjectNode
* extended/
  * contains extensions for classes such as SongInput and Podcast Input
* music/
  * this contains the Player and Playlist structures
* service/
  * contains the Service class that contains methods for every command given in the task
* user/
  * contains the UserDetails class that has all the details regarding the user performing commands

## Flow explanation

### Beginning
* All action takes place in the 'action' method from Main class.
* First of all, using the readValue from a objectMapper I read the input file and save it to a list of 'CommandInput' object,
  and also I create a hashmap that stores the users created with the name as key and the UserInput object as value.
* I created a for enhanced statement that iterates through the list of commands read from the input file
* and contains a switch that has as parameter the command from the input and executes methods specific for each command.
* Every method specific per each command is performing the specified checks and validation as is it specified in the requirements.

### Each command - flow and logic

##### <b>Search</b>
- Based on the type searched (song, playlist or podcast) it's called the specific method from the Service class.
  Those methods check what filters are not null and filter the song/playlists/podcasts based on these not null filters.
- When this command is given, the player resets its value.
##### <b>Select</b>
- This method checks if the user used the search command before and in case he did, then verifies if the index is in the range
  of the searched results and then sets to the current user details instance the selected index song/playlist/podcast
  and also creates a Player instance otherwise it returns a specific message.
##### <b>Load</b>
- If the user previous has selected an option from the result from search command, then it plays the current track
  from the user player instance that gave the command and sets the 'timestampStarted' variable from the Player to the current
  timestamp.
##### <b>PlayPause</b>
- The method triggered by this command changes the current user Player paused variable value to true or false.
  This also changes the 'playedTime' variable with the total time in which the player was playing.
##### <b>Repeat</b>
- This command changes the repeat variable from the player from 0 to 1, 1 to 2 and 2 to 0. When the playing type is playlist
  and the repeat variable is set to 2, then I set a new variable 'timestampStartedRepeat' that holds the timestamp the current song was played.
##### <b>Shuffle</b>
- This method uses the sort method from Collections and uses the current playing playlist to shuffle it based on a Random(seed) object.
- During the shuffling, it keeps the current playing song  and sets the 'playedTime' and 'timestampStarted' variables based on  the current song.
##### <b>Forward/Backward</b>
- This skips or rewinds the current playing track with 90 seconds considering if it is having less than 90 seconds from the beginning,
  respectively it has less than 90 seconds until it finishes.
##### <b>Like</b>
- For this command, I added to the SongInput class an attribute that holds the total number of likes a song has received.
  And also, every user contains a list of the songs he likes.
- From the Player, I get the current playing song (otherwise I retrieve a specific message)
  and check if the user already contains this song in his list, and then I add it and increment the song variable or
  remove it and decrement.
##### <b>Next/Prev</b>
- The methods used for these commands calculate the current playing time from the current track
  and set the timestamp started based on the result.
#### <b>addRemoveInPlaylist</b>
- For this command, I take the user's player and to check if is loaded, and then, I check if the source is a song.
- Also, this method takes the playlists from the user to check if the one with the index given has the current playing
  song or not.
#### <b>Status</b>
- This method goes for the playing type and, then also, for the repeat variable. Based on the repeat variable, the currentPlayedTime
  variable is modified.
##### <b>createPlaylist</b>
- For this command, I search through the whole user's playlists to check if it's already created and, if not, add the
  playlist for that specific user.
- Also, this method returns a Stats class
#### <b>switchVisibility</b>
- For this, I get the list of playlists from the user and check for the index. When the index is good, the privatePlaylist field from the Playlist class
  reverts.
- This method, also prints a message based on the updated visibility of the playlist.
#### <b>Follow</b>
- This method increases or decreases the number of followers a playlists has. It checks for error cases, and, then it goes
  into the user's follow method to check if the playlist is present or not.
- Also, this command adds or removes the followed playlist from the user's followed playlists list.
#### <b>showPlaylists</b>
- For this command, I take the playlists from the same user and list them.
#### <b>showPreferredSongs</b>
- For this command, I take the liked songs from a user and return then with a list.
##### <b>getTop5Songs</b>
- For this, I copy the list with all songs from the library and using the sort method from list and
  the compareTo method I sort that list first based on the number of likes and then based on the place in the list.
#### <b>getTop5Playlists</b>
- For this, I sort the list with playlists using the compareTo method to sort based on the followers that the playlists have.
  And, then, i put the first 5 results in another list with only their name.