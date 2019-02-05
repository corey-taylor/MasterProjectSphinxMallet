# Master Project - Sphinx+Mallet

The purpose of this code is to use words and topics both from a corpus and from a speech sample to progressively adapt the language model of the speech recogniser, Sphinx. 

/src/TopicModeller2.java does all the work here.

A topic model is first trained on a corpus from the documents a user reads, writes, etc. Then, speech is read in by the recogniser. The transcript is added to the corpus and the topic model re-trained. The most probable topics are elucidated and most probable words extracted from these topics. The probabilities of these words are then boosted in the language model.
