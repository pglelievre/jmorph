# jmorph
JMorph: Software for performing rapid morphometric measurements on digital images of fossil assemblages

CONDITIONS OF USE:  
- Properly cite usage of JMorph in any publications resulting from its use.  
- Promptly inform the developers of any such publications.  
- Promptly inform the developers of any bugs or issues encountered while using JMorph.  
- Users agree that their name and institution or company position may appear on the developers' academic websites in a list of users of JMorph.
- Free for academic use. Please contact the developers if you'd like to make money with JMorph, or if you'd like to make me money with JMorph, but I can't quite imagine how.
 
Folder "cageo" is the version of JMorph at the time of publication of the following article:

Computers & Geosciences 105 (2017) 120-128  
JMorph: Software for performing rapid morphometric measurements on digital images of fossil assemblages  
Peter G. Lelièvre, Melissa Grey

File "cageo/jmorphdoc.pdf" contains a quick-start tutorial with installation instructions.

Folder "JMorph" (previously named "currentdev") contains the current version of the software.

If you are just hoping to run JMorph, grab one of the "dist" directories and launch file "JMorph.jar" inside it:
- The "JMorph/dist8" folder contains the JMorph distribution as a compiled .jar file using JDK 8.
- The "JMorph/dist19" folder contains the JMorph distribution as a compiled .jar file using JDK 19.

If you want to do some development, read on.

Directory "JMorph/src" contains Java source code. That code is dependent on the source code in my "mylibrary" repository, subdirectory "MyLibrary". There is also a rather hokey dependency diagram that I started drawing and probably gave up long ago when things started getting too complicated. That's what you can expect with free software.
