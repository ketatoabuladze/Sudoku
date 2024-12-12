#How to play:
Solo play:
Download the files in a folder. 
Example: TerminalSudoku
Open terminal and navigate to directory these files are saved 
	Example : cd desktop/TerminalSudoku
Once here run: 
javac Server.java Client.java Sudoku.java
In the same directory that you are compiled the files in run: 
 java Server 8000 ←-or any port
Open a new terminal window and navigate to the same directory and run: 
java Client localhost 8000 ←- must be same port as Server
Once you connect you should see a sudoku board.
To play use command:
update <row> <col> <value> 
If your value is not a valid move for sudoku you will receive an error, try a new value.
If your value is possible but may not be the correct move, you will not get an error but will be unable to solve the puzzle due to the rules of sudoku.
To see the most recent version of the board use the command:
	show

Distributed Play:
If you want to play on two or more machines, host the server on one machine then when you run the client use ‘java <server_computer’s ip address> <port>. 
For example if the person who is hosting the server’s ip address is 192.00.00.1 and its on port 8000 you would do:
java Client 192.00.00.1 8000 
