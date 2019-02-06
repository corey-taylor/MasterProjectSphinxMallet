=======
# Master Project - Sphinx+Mallet

The purpose of this code is to use words and topics both from a corpus and from a speech sample to progressively adapt the language model of a speech recogniser, Sphinx. 

Don't think MALLET or Sphinx4 were on Github at the time so this is code is also well out of date. This is, again, purely for posterity/potential employers.

/src/TopicModeller2.java does all the work here. Other code snippets do other things but this is the class that has most of the stuff in it.

A topic model is first trained on a corpus from the documents a user reads, writes, etc. Then, speech is read in by the recogniser. The transcript is added to the corpus and the topic model re-trained. The most probable topics are elucidated and most probable words extracted from these topics. The normalised probabilities of these words are then boosted in the language model prior to the next speech snippet being analysed.

The point of the exercise was to see if progressive adaptation of the language model with topic information would result in a better guess at the phrases most likely spoken/read about by the user. Not a usable product but a reasonable proof-of-concept.

Some files (language models, mainly) are not there because they're too big:

remote: error: File resources/LanguageModels/US English Language Model/en-70k-0.1.lm.gz is 111.33 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File resources/LanguageModels/WSJ.lm is 115.48 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File resources/LanguageModels/language_model.arpaformat.DMP is 124.82 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File resources/model.dmp is 124.77 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File resources/LanguageModels/HUB4.lm is 612.46 MB; this exceeds GitHub's file size limit of 100.00 MB
remote: error: File resources/input.lm is 599.62 MB; this exceeds GitHub's file size limit of 100.00 MB
>>>>>>> 5d9ee0bffadb4910d02f5c8e3a2833e390b8bbcc
