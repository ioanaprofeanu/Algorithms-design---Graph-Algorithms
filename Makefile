build:
	javac Curatare.java
	javac Fortificatii.java
	javac Beamdrone.java
	javac Curse.java

run-p1:
	java Curatare

run-p2:
	java Fortificatii

run-p3:
	java Beamdrone

run-p4:
	java Curse

clean:
	rm *.class
