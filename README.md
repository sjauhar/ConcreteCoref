ConcreteCoref
=============

ConcreteCoref provides in-document co-reference annotation for Concrete Communication objects. 
Assumes presence of a SituationMentionSet , and adds a new SituationSet that represents the coreference-resolved events.


Requirements:

(1) Fanseparser  ( http://www.isi.edu/publications/licensed-sw/fanseparser/ ), and start a parseserver on the local machine ().
(2) Java memory requirements: -Xms256M -Xmx6G -Xss32m


Sample test file `CMUCoref.java` reads a file containing Communications from the EECB corpus (hardcoded for this test), and sequentially annotates event-coreferences for each communication.

 