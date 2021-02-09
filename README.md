# BuildingConstruction
Construct multiple buildings and keep track using min heaps and red-black trees

## Building.java
It is a user defined data type containing a triplet of three integer variables, namely building_num, executed_time and total_time. Building_num is a unique value, its importance will be explained later. It also has a parameterized constructor so that when a new building is created, the values are stored in the required fields. It is used only by MinHeap class since we have to implement
MinHeap using arrays.

## risingCity.java
### Imports
 - java.io.File - Used to open a file for reading
 - java.util.Scanner - Used to read from a file
 - java.util.HashSet - Used to store all buildings in the city so that a duplicate entry is not permitted

### Global Variables
 - globalcounter - Integer counter on which the program decides whether to read a line from the file
 - secondarycounter - Integer used to denote working time for the current building under construction

### Main()
Local Variables
 - inputline - String that contains lines of input file commands (1 at a time)
 - inputfilename - String that contains input file name
 - brokenInput - String array that stores broken parts of the input line
