# Master Project - Sphinx+Mallet

The purpose of this code is to use words and topics both from a corpus and from a speech sample to progressively adapt the language model of the speech recogniser, Sphinx. 

Don't think MALLET or Sphinx4 were on Github at the time so this is code is also well out of date. This is, again, purely for posterity/potential employers.

/src/TopicModeller2.java does all the work here. Other code snippets do other things but this is the class that has most of the stuff in it.

A topic model is first trained on a corpus from the documents a user reads, writes, etc. Then, speech is read in by the recogniser. The transcript is added to the corpus and the topic model re-trained. The most probable topics are elucidated and most probable words extracted from these topics. The normalised probabilities of these words are then boosted in the language model prior to the next speech snippet being analysed.

The point of the exercise was to see if progressive adaptation of the language model with topic information would result in a better guess at the phrases most likely spoken/read about by the user. Not a usable product but a reasonable proof-of-concept.
